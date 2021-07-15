package com.alibaba.jvm.sandbox.repeater.plugin.redis;

import com.alibaba.jvm.sandbox.repeater.plugin.api.InvocationProcessor;
import com.alibaba.jvm.sandbox.repeater.plugin.core.impl.AbstractInvokePluginAdapter;
import com.alibaba.jvm.sandbox.repeater.plugin.core.model.EnhanceModel;
import com.alibaba.jvm.sandbox.repeater.plugin.domain.InvokeType;
import com.alibaba.jvm.sandbox.repeater.plugin.spi.InvokePlugin;

import org.kohsuke.MetaInfServices;

import java.util.*;

/**
 * {@link RabbitmqPlugin} jedis的java插件
 * <p>
 * 拦截{@code redis.clients.jedis.commands}包下面的commands实现类
 * <p>
 * 获取redis常用操作指令，不包括所有命令
 * 详见Jedis类、BinaryJedis类的实现接口
 * </p>
 *
 * @author zhaoyb1990
 */
@MetaInfServices(InvokePlugin.class)
public class RabbitmqPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        return null;
    }


    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new RabbitmqProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return new InvokeType("rabbitmq");
    }

    @Override
    public String identity() {
        return "rabbitmq";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }


}
