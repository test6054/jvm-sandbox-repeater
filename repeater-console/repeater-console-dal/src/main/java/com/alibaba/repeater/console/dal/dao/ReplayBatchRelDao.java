package com.alibaba.repeater.console.dal.dao;

import com.alibaba.repeater.console.dal.model.ReplayBatchRel;
import com.alibaba.repeater.console.dal.repository.ReplayBatchRelRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @创建人：zhiang
 * @创建时间：2021/7/14 10:54
 * @version：V1.0
 */
@Component("ReplayBatchRelDao")
public class ReplayBatchRelDao {

    @Resource
    private ReplayBatchRelRepository replayBatchRelRepository;


    public ReplayBatchRel insert(ReplayBatchRel batchRel) {
        return replayBatchRelRepository.save(batchRel);
    }

    public ReplayBatchRel saveOrUpdate(ReplayBatchRel batchRel) {
        return replayBatchRelRepository.saveAndFlush(batchRel);
    }
}
