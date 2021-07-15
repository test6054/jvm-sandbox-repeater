package com.alibaba.repeater.console.dal.repository;

import com.alibaba.repeater.console.common.exception.BizException;
import com.alibaba.repeater.console.dal.model.ReplayBatch;
import com.alibaba.repeater.console.dal.model.ReplayBatchRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: zhliang
 * @Date: 2021/7/14 10:51
 * @Description:
 * @Version: V1.0
 */
@Repository
@Transactional(rollbackFor = {RuntimeException.class, Error.class, BizException.class})
public interface ReplayBatchRelRepository extends JpaRepository<ReplayBatchRel, Long>, JpaSpecificationExecutor<ReplayBatchRel> {

}
