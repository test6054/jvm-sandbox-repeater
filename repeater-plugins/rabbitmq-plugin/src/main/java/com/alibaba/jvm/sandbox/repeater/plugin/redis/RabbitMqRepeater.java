package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractRepeater;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.RepeatContext;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.Repeater;
import org.kohsuke.MetaInfServices;


/**
 * {@link RabbitMqRepeater} dubbo回放器
 * <p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(Repeater.class)
public class RabbitMqRepeater extends AbstractRepeater {

    @Override
    protected Object executeRepeat(RepeatContext context) throws Exception {
        return null;
    }

    @Override
    public InvokeType getType() {
        return new InvokeType("rabbitmq");
    }

    @Override
    public String identity() {
        return "rabbitmq";
    }
}
