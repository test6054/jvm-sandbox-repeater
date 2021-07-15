package com.alibaba.repeater.console.common.domain;

import lombok.Data;

import java.util.Date;

/**
 * @项目名称：jvm-sandbox-repeater
 * @包名：com.alibaba.repeater.console.common.domain
 * @类描述：
 * @创建人：zhiang
 * @创建时间：2021/7/14 13:08
 * @version：V1.0
 */
@Data
public class ReplayBatchBO extends BaseBO {


    private String appName;

    private String ip;

    private String environment;

    private Boolean success;

    private Long cost;

    private ReplayStatus status;

    private Long id;

    private String batchRepeatId;

    private Integer total;

    private Date gmtCreate;

    private Date gmtModified;
}
