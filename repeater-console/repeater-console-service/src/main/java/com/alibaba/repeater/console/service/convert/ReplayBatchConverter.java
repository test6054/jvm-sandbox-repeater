package com.alibaba.repeater.console.service.convert;

import com.alibaba.repeater.console.common.domain.ReplayBatchBO;
import com.alibaba.repeater.console.dal.model.ReplayBatch;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @创建人：zhiang
 * @创建时间：2021/7/14 13:07
 * @version：V1.0
 */
@Component("replayBatchConverter")
public class ReplayBatchConverter implements ModelConverter<ReplayBatch, ReplayBatchBO> {

    @Override
    public ReplayBatchBO convert(ReplayBatch source) {
        ReplayBatchBO rb = new ReplayBatchBO();
        BeanUtils.copyProperties(source, rb);
        return rb;
    }

    @Override
    public ReplayBatch reconvert(ReplayBatchBO target) {
        return null;
    }

}
