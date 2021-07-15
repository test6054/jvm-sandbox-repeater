package com.alibaba.repeater.console.service;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterResult;
import com.alibaba.repeater.console.common.domain.ReplayBO;
import com.alibaba.repeater.console.common.params.ReplayBatchParams;
import com.alibaba.repeater.console.common.params.ReplayParams;

import java.util.List;

/**
 * {@link ReplayService}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface ReplayService {

    RepeaterResult<String> replay(ReplayParams params);

    RepeaterResult<String> saveRepeat(String body);

    RepeaterResult<ReplayBO> query(ReplayParams params);

    RepeaterResult<String> replayList(List<ReplayParams> params);

    RepeaterResult<String> replayBatch(ReplayBatchParams params);

}
