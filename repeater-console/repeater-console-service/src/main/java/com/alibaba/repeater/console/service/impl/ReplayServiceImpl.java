package com.alibaba.repeater.console.service.impl;

import com.alibaba.jvm.sandbox.repeater.aide.compare.Comparable;
import com.alibaba.jvm.sandbox.repeater.aide.compare.ComparableFactory;
import com.alibaba.jvm.sandbox.repeater.aide.compare.CompareResult;
import com.alibaba.jvm.sandbox.repeater.plugin.Constants;
import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.alibaba.jvm.sandbox.repeater.plugin.core.trace.TraceGenerator;
import com.alibaba.jvm.sandbox.repeater.plugin.core.util.HttpUtil;
import com.alibaba.jvm.sandbox.repeater.plugin.core.wrapper.SerializerWrapper;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatMeta;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.MockStrategy;
import com.alibaba.repeater.console.common.domain.ModuleConfigBO;
import com.alibaba.repeater.console.common.domain.ModuleInfoBO;
import com.alibaba.repeater.console.common.domain.ReplayBO;
import com.alibaba.repeater.console.common.domain.ReplayStatus;
import com.alibaba.repeater.console.common.params.ModuleConfigParams;
import com.alibaba.repeater.console.common.params.ReplayBatchParams;
import com.alibaba.repeater.console.common.params.ReplayParams;
import com.alibaba.repeater.console.dal.dao.RecordDao;
import com.alibaba.repeater.console.dal.dao.ReplayBatchDao;
import com.alibaba.repeater.console.dal.dao.ReplayBatchRelDao;
import com.alibaba.repeater.console.dal.dao.ReplayDao;
import com.alibaba.repeater.console.dal.model.Record;
import com.alibaba.repeater.console.dal.model.Replay;
import com.alibaba.repeater.console.dal.model.ReplayBatch;
import com.alibaba.repeater.console.dal.model.ReplayBatchRel;
import com.alibaba.repeater.console.service.ModuleConfigService;
import com.alibaba.repeater.console.service.ModuleInfoService;
import com.alibaba.repeater.console.service.ReplayService;
import com.alibaba.repeater.console.service.convert.DifferenceConvert;
import com.alibaba.repeater.console.service.convert.ReplayConverter;
import com.alibaba.repeater.console.service.util.ConvertUtil;
import com.alibaba.repeater.console.service.util.ExecutorUtils;
import com.alibaba.repeater.console.service.util.JacksonUtil;
import com.alibaba.repeater.console.service.util.ResultHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link ReplayServiceImpl}
 * <p>
 *
 * @author zhaoyb1990
 */
@Service("replayService")
@Slf4j
public class ReplayServiceImpl implements ReplayService {

    @Value("${repeat.repeat.url}")
    private String repeatURL;

    @Resource
    private ModuleInfoService moduleInfoService;
    @Resource
    private RecordDao recordDao;
    @Resource
    private ReplayDao replayDao;
    @Resource
    private ReplayConverter replayConverter;
    @Resource
    private DifferenceConvert differenceConvert;
    @Resource
    private ReplayBatchDao replayBatchDao;
    @Resource
    private ReplayBatchRelDao replayBatchRelDao;
    @Resource
    private ModuleConfigService moduleConfigService;

    @Override
    public RepeaterResult<String> replay(ReplayParams params) {
        Optional.ofNullable(params.getIp()).orElseThrow(() -> new RuntimeException("ip can not be null"));
        Optional.ofNullable(params.getAppName()).orElseThrow(() -> new RuntimeException("appName can not be null"));
        Optional.ofNullable(params.getTraceId()).orElseThrow(() -> new RuntimeException("traceId can not be null"));
        RepeaterResult<ModuleInfoBO> result = moduleInfoService.query(params.getAppName(), params.getIp());
        if (!result.isSuccess() || result.getData() == null) {
            return ResultHelper.copy(result);
        }
        params.setPort(result.getData().getPort());
        params.setEnvironment(result.getData().getEnvironment());
        final Record record = recordDao.selectByAppNameAndTraceId(params.getAppName(), params.getTraceId());
        if (record == null) {
            return RepeaterResult.builder().success(false).message("data does not exist").build();
        }
        if (StringUtils.isEmpty(params.getRepeatId())) {
            params.setRepeatId(TraceGenerator.generate());
        }
        // save replay record
        Replay replay = saveReplay(record, params);
        if (replay == null) {
            return RepeaterResult.builder().success(false).message("save replay record failed").build();
        }
        return doRepeat(record, params);
    }

    @Override
    public RepeaterResult<String> saveRepeat(String body) {
        RepeatModel rm;
        try {
            rm = SerializerWrapper.hessianDeserialize(body, RepeatModel.class);
        } catch (SerializeException e) {
            log.error("error occurred when deserialize repeat model", e);
            return RepeaterResult.builder().message("operate failed").build();
        }
        // this process must handle by async
        Replay replay = replayDao.findByRepeatId(rm.getRepeatId());
        replay.setStatus(rm.isFinish() ? ReplayStatus.FINISH.getStatus() : ReplayStatus.FAILED.getStatus());
        replay.setTraceId(rm.getTraceId());
        replay.setCost(rm.getCost());
        Object expect;
        Object actual;
        try {
            if (rm.getResponse() instanceof String) {
                replay.setResponse(ConvertUtil.convert2Json((String)rm.getResponse()));
                try {
                    actual = JacksonUtil.deserialize((String)rm.getResponse(), Object.class);
                } catch (SerializeException e) {
                    actual = rm.getResponse();
                }
            } else {
                replay.setResponse(JacksonUtil.serialize(rm.getResponse()));
                actual = rm.getResponse();
            }
            replay.setMockInvocation(JacksonUtil.serialize(rm.getMockInvocations()));
            try {
                expect = JacksonUtil.deserialize(replay.getRecord().getResponse(), Object.class);
            } catch (SerializeException e) {
                expect = replay.getRecord().getResponse();
            }
        } catch (SerializeException e) {
            log.error("error occurred serialize replay response", e);
            return RepeaterResult.builder().message("operate failed").build();
        }
        //获取过滤字段
        List<String> excludeFieldList = queryExcludeFields(replay);
        Comparable comparable = ComparableFactory.instance().createDefault();
        // simple compare
        CompareResult result = comparable.compare(actual, expect ,excludeFieldList);
        replay.setSuccess(!result.hasDifference());
        try {
            replay.setDiffResult(JacksonUtil.serialize(result.getDifferences()
                    .stream()
                    .map(differenceConvert::convert)
                    .collect(Collectors.toList()), false));
        } catch (SerializeException e) {
            log.error("error occurred serialize diff result", e);
            return RepeaterResult.builder().message("operate failed").build();
        }
        Replay calllback = replayDao.saveAndFlush(replay);
        return RepeaterResult.builder().success(true).message("operate success").data("-/-").build();
    }

    private List<String> queryExcludeFields(Replay replay) {
        ReplayParams params = new ReplayParams();
        params.setAppName(replay.getAppName());
        params.setEnvironment(replay.getEnvironment());
        return queryExcludeFields(params);
    }

    private List<String> queryExcludeFields(ReplayParams params) {
        RepeaterResult<ModuleConfigBO> configResult = queryModuleConfig(params);
        String excludeField = configResult.getData().getExcludeField();
        List<String> excludeFieldList =new ArrayList<>();
        if(StringUtils.isNotBlank(excludeField)){
            excludeFieldList = Arrays.asList(excludeField.split(","));
        }
        return excludeFieldList;
    }

    private RepeaterResult<ModuleConfigBO> queryModuleConfig(ReplayParams params) {
        ModuleConfigParams configParams = new ModuleConfigParams();
        configParams.setAppName(params.getAppName());
        configParams.setEnvironment(params.getEnvironment());
        return moduleConfigService.query(configParams);
    }

    @Override
    public RepeaterResult<ReplayBO> query(ReplayParams params) {
        Replay replay = replayDao.findByRepeatId(params.getRepeatId());
        if (replay == null) {
            return RepeaterResult.builder().message("data not exist").build();
        }
        return RepeaterResult.builder().success(true).data(replayConverter.convert(replay)).build();
    }

    private RepeaterResult<String> doRepeat(Record record, ReplayParams params) {
        RepeatMeta meta = new RepeatMeta();
        meta.setAppName(record.getAppName());
        meta.setTraceId(record.getTraceId());
        meta.setMock(params.isMock());
        meta.setRepeatId(params.getRepeatId());
        meta.setStrategyType(MockStrategy.StrategyType.PARAMETER_MATCH);
        //回放替换headers
        replaceReplayHeaders(params, meta);
        Map<String, String> requestParams = new HashMap<String, String>(2);
        try {
            requestParams.put(Constants.DATA_TRANSPORT_IDENTIFY, SerializerWrapper.hessianSerialize(meta));
        } catch (SerializeException e) {
            return RepeaterResult.builder().success(false).message(e.getMessage()).build();
        }
        HttpUtil.Resp resp = HttpUtil.doPost(String.format(repeatURL,params.getIp(),params.getPort()), requestParams);
        if (resp.isSuccess()) {
            return RepeaterResult.builder().success(true).message("operate success").data(meta.getRepeatId()).build();
        }
        return RepeaterResult.builder().success(false).message("operate failed").data(resp).build();
    }

    private void replaceReplayHeaders(ReplayParams params, RepeatMeta meta) {
        if(!CollectionUtils.isEmpty(params.getHeaders())){
            meta.setExtension(params.getHeaders());
        }else {
            RepeaterResult<ModuleConfigBO> moduleConfig = queryModuleConfig(params);
            try {
                String headers = moduleConfig.getData().getHeaders();
                if(StringUtils.isNotBlank(headers)){
                    Map<String,String> replayHeaders = JacksonUtil.deserialize(headers, HashMap.class);
                    meta.setExtension(replayHeaders);
                }
            } catch (SerializeException e) {
                e.printStackTrace();
            }
        }
    }

    private Replay saveReplay(Record record, ReplayParams params) {
        Replay replay = new Replay();
        replay.setRecord(record);
        replay.setAppName(params.getAppName());
        replay.setEnvironment(params.getEnvironment());
        replay.setIp(params.getIp());
        replay.setRepeatId(params.getRepeatId());
        replay.setGmtCreate(new Date());
        replay.setGmtModified(new Date());
        replay.setStatus(ReplayStatus.PROCESSING.getStatus());
        // 冗余了一个repeatID，实际可以直接使用replay#id
        return replayDao.save(replay);
    }

    @Override
    public RepeaterResult<String> replayList(List<ReplayParams> list) {
        if(CollectionUtils.isEmpty(list)){
            return RepeaterResult.builder().success(false).message("request params is not valid").build();
        }
        list.stream().forEach(e -> checkRequestParams(e));
        //保存批量回放记录
        ReplayBatch batch = saveBatchReplay(list);

        for (ReplayParams params : list) {
            asyncSubmit(params,batch);
        }
        return RepeaterResult.builder().success(true).message("request in executing").build();
    }

    private void checkRequestParams(ReplayParams params) {
        Optional.ofNullable(params.getIp()).orElseThrow(() -> new RuntimeException("ip can not be null"));
        Optional.ofNullable(params.getAppName()).orElseThrow(() -> new RuntimeException("appName can not be null"));
        Optional.ofNullable(params.getTraceId()).orElseThrow(() -> new RuntimeException("traceId can not be null"));
    }

    private ReplayBatch saveBatchReplay(List<ReplayParams> list) {
        ReplayBatch batch = new ReplayBatch();
        ReplayParams params = list.get(0);
        BeanUtils.copyProperties(params,batch);
        batch.setBatchRepeatId(TraceGenerator.generateBatch());
        batch.setEnvironment(params.getEnvironment());
        batch.setStatus(ReplayStatus.PROCESSING.getStatus());
        batch.setTotal(list.size());
        replayBatchDao.saveOrUpdate(batch);
        return batch;
    }

    public void asyncSubmit(ReplayParams params,ReplayBatch batch){
        ExecutorUtils.executor.execute(() -> {
            //保存单次请求执行记录和批量回放关系
            ReplayBatchRel batchRel = new ReplayBatchRel();
            batchRel.setBatchId(batch.getId());
            StringBuilder sb = new StringBuilder();

            RepeaterResult<ModuleInfoBO> result = moduleInfoService.query(params.getAppName(), params.getIp());
            if (!result.isSuccess() || result.getData() == null) {
                sb.append(params.getAppName()).append(":").append(params.getIp()).append("model info data not exist ");
                saveReplayBatchRel(batchRel, sb.toString());
                return;
            }

            params.setPort(result.getData().getPort());
            params.setEnvironment(result.getData().getEnvironment());
            final Record record = recordDao.selectByAppNameAndTraceId(params.getAppName(), params.getTraceId());
            if (record == null) {
                sb.append(params.getAppName()).append(":").append(params.getTraceId()).append("record data does not exist");
                saveReplayBatchRel(batchRel, sb.toString());
                return ;
            }

            if (StringUtils.isEmpty(params.getRepeatId())) {
                params.setRepeatId(TraceGenerator.generate());
            }
            // save replay record
            Replay replay = saveReplay(record, params);
            if (replay == null) {
                saveReplayBatchRel(batchRel, "save replay record failed");
                return ;
            }
            RepeaterResult<String> response = doRepeat(record, params);
            //记录异常回放
            if (!response.isSuccess()) {
                log.error("replay error : traceId : {} 。message : {}",params.getTraceId(),response.getData());
            }

            batchRel.setRepeatId(replay.getId());
            batchRel.setMessage(response.getMessage());
            try {
                batchRel.setData(JacksonUtil.serialize(response.getData()));
            } catch (SerializeException e) {
                e.printStackTrace();
            }
            replayBatchRelDao.saveOrUpdate(batchRel);
        });

    }

    private void saveReplayBatchRel(ReplayBatchRel batchRel, String s) {
        batchRel.setMessage(s);
        replayBatchRelDao.saveOrUpdate(batchRel);
    }

    @Override
    public RepeaterResult<String> replayBatch(ReplayBatchParams params) {
        checkReplayBatchParams(params);
        RepeaterResult<ModuleInfoBO> result = moduleInfoService.query(params.getAppName(), params.getIp());
        if (!result.isSuccess() || result.getData() == null) {
            return ResultHelper.copy(result);
        }
        params.setPort(result.getData().getPort());
        params.setEnvironment(result.getData().getEnvironment());

        final List<Record> list = recordDao.selectByTotalNumList(params.getTotal());
        if (CollectionUtils.isEmpty(list)) {
            return RepeaterResult.builder().success(false).message("data does not exist").build();
        }

        //保存批量回放记录
        ReplayBatch batch = saveBatchReplay(params,list.size());

        for (Record record : list) {
            asyncReplayBatch(batch,record,params);
        }
        return RepeaterResult.builder().success(true).message("request in executing").build();
    }

    private void asyncReplayBatch(ReplayBatch batch,Record record, ReplayBatchParams params) {
        ExecutorUtils.executor.execute(() -> {
            //保存单次请求执行记录和批量回放关系
            ReplayBatchRel batchRel = new ReplayBatchRel();
            batchRel.setBatchId(batch.getId());
            StringBuilder sb = new StringBuilder();

            ReplayParams replayParams = new ReplayParams();
            BeanUtils.copyProperties(params,replayParams);
            replayParams.setRepeatId(TraceGenerator.generate());
            Replay replay = saveReplay(record, replayParams);
            if (replay == null) {
                sb.append("save replay record failed");
                saveReplayBatchRel(batchRel, sb.toString());
                return ;
            }
            RepeaterResult<String> response = doRepeat(record, replayParams);
            //记录异常回放
            if (!response.isSuccess()) {
                log.error("replay error : traceId : {} 。message : {}",params.getTraceId(),response.getData());
            }

            batchRel.setRepeatId(replay.getId());
            batchRel.setMessage(response.getMessage());
            try {
                batchRel.setData(JacksonUtil.serialize(response.getData()));
            } catch (SerializeException e) {
                e.printStackTrace();
            }
            replayBatchRelDao.saveOrUpdate(batchRel);
        });
    }

    private ReplayBatch saveBatchReplay(ReplayBatchParams params, Integer total) {
        ReplayBatch batch = new ReplayBatch();
        BeanUtils.copyProperties(params,batch);
        batch.setBatchRepeatId(TraceGenerator.generateBatch());
        batch.setEnvironment(params.getEnvironment());
        batch.setStatus(ReplayStatus.PROCESSING.getStatus());
        batch.setTotal(total);
        replayBatchDao.saveOrUpdate(batch);
        return batch;
    }

    private void checkReplayBatchParams(ReplayBatchParams params) {
        Optional.ofNullable(params.getIp()).orElseThrow(() -> new RuntimeException("ip can not be null"));
        Optional.ofNullable(params.getAppName()).orElseThrow(() -> new RuntimeException("appName can not be null"));
        Optional.ofNullable(params.getEnvironment()).orElseThrow(() -> new RuntimeException("environment can not be null"));
        Optional.ofNullable(params.getTotal()).orElseThrow(() -> new RuntimeException("total can not be null"));
    }
}
