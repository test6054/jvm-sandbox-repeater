package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * @类描述：接口录制采样率
 * @创建人：zhiang
 * @创建时间：2021/7/15 13:25
 * @version：V1.0
 */
public class ApiSampleRate implements java.io.Serializable{

    /**
     * 接口地址
     */
    private String reqUri;

    /**
     * 采样率；最小力度万分之一
     * 10000 代表 100%
     */
    private Integer sampleRate = 10000;

    public String getReqUri() {
        return reqUri;
    }

    public void setReqUri(String reqUri) {
        this.reqUri = reqUri;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }
}
