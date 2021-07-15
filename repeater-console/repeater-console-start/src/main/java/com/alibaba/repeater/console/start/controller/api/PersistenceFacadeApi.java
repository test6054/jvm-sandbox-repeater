package com.alibaba.repeater.console.start.controller.api;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.params.ReplayParams;
import com.alibaba.repeater.console.service.RecordService;
import com.alibaba.repeater.console.service.ReplayService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * {@link PersistenceFacadeApi} Demo工程；作为repeater录制回放的数据存储
 * <p>
 *
 * @author zhaoyb1990
 */
@RestController
@RequestMapping("/facade/api")
public class PersistenceFacadeApi {

    @Resource
    private RecordService recordService;
    @Resource
    private ReplayService replayService;

    /**
     * 根据应用名和traceId获取录制数据
     */
    @RequestMapping(value = "record/{appName}/{traceId}", method = RequestMethod.GET)
    public RepeaterResult<String> getWrapperRecord(@PathVariable("appName") String appName,
                                                   @PathVariable("traceId") String traceId) {
        return recordService.get(appName, traceId);
    }

    /**
     * 根据appName、traceId、repeatId执行回放记录
     */
    @RequestMapping(value = "repeat/{appName}/{traceId}", method = RequestMethod.GET)
    public RepeaterResult<String> repeat(@PathVariable("appName") String appName,
                                         @PathVariable("traceId") String traceId,
                                         HttpServletRequest request) {
        ReplayParams params = ReplayParams.builder().repeatId(request.getHeader("RepeatId")).build();
        params.setAppName(appName);
        params.setTraceId(traceId);
        return replayService.replay(params);
    }

    /**
     * 保存录制结果
     */
    @RequestMapping(value = "record/save", method = RequestMethod.POST)
    public RepeaterResult<String> recordSave(@RequestBody String body) {
        return recordService.saveRecord(body);
    }

    /**
     * 保存回放结果
     */
    @RequestMapping(value = "repeat/save", method = RequestMethod.POST)
    public RepeaterResult<String> repeatSave(@RequestBody String body) {
        return replayService.saveRepeat(body);
    }

    /**
     * 根据repeatId获取回放结果
     */
    @RequestMapping(value = "repeat/callback/{repeatId}", method = RequestMethod.GET)
    public RepeaterResult<RepeatModel> callback(@PathVariable("repeatId") String repeatId) {
        return recordService.callback(repeatId);
    }

    /**
     * TODO
     * 批量流量回放接口，需另行开发
     */
}
