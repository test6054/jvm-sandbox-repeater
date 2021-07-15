package com.alibaba.repeater.console.dal.dao;

import com.alibaba.repeater.console.common.params.ReplayBatchParams;
import com.alibaba.repeater.console.dal.model.ReplayBatch;
import com.alibaba.repeater.console.dal.repository.ReplayBatchRepository;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @创建人：zhiang
 * @创建时间：2021/7/14 10:54
 * @version：V1.0
 */
@Component("ReplayBatchDao")
public class ReplayBatchDao {

    @Resource
    private ReplayBatchRepository replayBatchRepository;

    public ReplayBatch insert(ReplayBatch batch) {
        return replayBatchRepository.save(batch);
    }

    public ReplayBatch saveOrUpdate(ReplayBatch batch) {
        return replayBatchRepository.saveAndFlush(batch);
    }

    public Page<ReplayBatch> selectByParams(@NotNull final ReplayBatchParams params) {
        Pageable pageable = new PageRequest(params.getPage() - 1, params.getSize(), new Sort(Sort.Direction.DESC, "id"));
        return replayBatchRepository.findAll(
                (root, query, cb) -> {
                    List<Predicate> predicates = Lists.newArrayList();
                    if (params.getAppName() != null && !params.getAppName().isEmpty()) {
                        predicates.add(cb.equal(root.<String>get("appName"), params.getAppName()));
                    }
                    if (params.getEnvironment() != null && !params.getEnvironment().isEmpty()) {
                        predicates.add(cb.equal(root.<String>get("environment"), params.getEnvironment()));
                    }
                    if (params.getIp() != null && !params.getIp().isEmpty()) {
                        predicates.add(cb.equal(root.<String>get("ip"), params.getIp()));
                    }
                    if (params.getBatchRepeatId() != null && !params.getBatchRepeatId().isEmpty()) {
                        predicates.add(cb.equal(root.<String>get("batchRepeatId"), params.getBatchRepeatId()));
                    }
                    return cb.and(predicates.toArray(new Predicate[0]));
                },
                pageable
        );
    }
}
