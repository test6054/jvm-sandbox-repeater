package com.alibaba.repeater.console.common.domain;

import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeaterConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * {@link ModuleConfigBO}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
public class ModuleConfigBO extends BaseBO {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String appName;

    private String environment;

    private RepeaterConfig configModel;

    private String config;

    private String headers;

    private String excludeField;


    @Override
    public String toString() {
        return super.toString();
    }
}
