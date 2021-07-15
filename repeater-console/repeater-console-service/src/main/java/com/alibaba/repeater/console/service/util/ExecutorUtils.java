package com.alibaba.repeater.console.service.util;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 回放反序列化开销很大，cpu密集，线程池大小设置成核心数 - 1
 * @创建人：zhiang
 * @创建时间：2021/7/14 13:27
 * @version：V1.0
 */
public class ExecutorUtils {

    public final static ExecutorService executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() - 1,
            2 * Runtime.getRuntime().availableProcessors(), 30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<Runnable>(4096),
            new BasicThreadFactory.Builder().namingPattern("batch-repeat-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());
}
