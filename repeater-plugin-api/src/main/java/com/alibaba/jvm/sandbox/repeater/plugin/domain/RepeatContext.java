package com.alibaba.jvm.sandbox.repeater.plugin.domain;

/**
 * <p>
 *  回访上下文
 *  meta   回放元数据
 *  recordModel 录制的调用记录
 *  traceId  录制traceId
 * @author zhaoyb1990
 */
public class RepeatContext {
    private RepeatMeta meta;
    private RecordModel recordModel;
    private String traceId;

    public RepeatContext(RepeatMeta meta, RecordModel recordModel, String traceId) {
        this.meta = meta;
        this.recordModel = recordModel;
        this.traceId = traceId;
    }

    public RepeatMeta getMeta() {
        return meta;
    }

    public void setMeta(RepeatMeta meta) {
        this.meta = meta;
    }

    public RecordModel getRecordModel() {
        return recordModel;
    }

    public void setRecordModel(RecordModel recordModel) {
        this.recordModel = recordModel;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
