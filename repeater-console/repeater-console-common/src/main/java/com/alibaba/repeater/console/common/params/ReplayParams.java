package com.alibaba.repeater.console.common.params;

import lombok.*;

import java.util.Map;

/**
 * {@link ReplayParams}
 * <p>
 *
 * @author zhaoyb1990
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplayParams extends BaseParams {

    private String ip;

    private String repeatId;

    private String port;

    private boolean mock;
    /**
     * 回放替换请求头
     */
    Map<String,String> headers;

}
