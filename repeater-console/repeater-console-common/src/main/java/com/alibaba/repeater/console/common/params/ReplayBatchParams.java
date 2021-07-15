package com.alibaba.repeater.console.common.params;

import lombok.*;

/**
 * @类描述：
 * @创建人：zhiang
 * @创建时间：2021/7/14 10:59
 * @version：V1.0
 */
@Builder
@Data
public class ReplayBatchParams extends BaseParams {

    private String ip;

    private String port;

    private String appName;

    private String environment;

    private String batchRepeatId;

    private boolean mock;

    private Integer total;


}
