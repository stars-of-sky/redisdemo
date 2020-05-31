package com.rp.redisdemo.config;

/**
 * Description: 设置线程池
 * Create By yrp.
 * Date:2020/5/31
 * Version: 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数,方法: 返回可用处理器的Java虚拟机的数量
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        // 最大线程数
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 5);
        // 队列大小
        executor.setQueueCapacity(Runtime.getRuntime().availableProcessors() * 10);
        //线程名称的前缀
        executor.setThreadNamePrefix("my-ThreadPoolTaskExecutor-");
        /*  饱和策略
         * 当最大池已满时，此策略保证不会丢失任务请求，但是可能会影响应用程序整体性能。
         * 如果队列已满并且当前同时运行的线程数达到最大线程数的时候，如果再有新任务过来
         * ThreadPoolExecutor.AbortPolicy：抛出 RejectedExecutionException来拒绝新任务的处理。
         * ThreadPoolExecutor.CallerRunsPolicy：调用执行自己的线程运行任务。您不会任务请求。但是这种策略会降低对于新任务提交速度，影响程序的整体性能。另外，这个策略喜欢增加队列容量。如果您的应用程序可以承受此延迟并且你不能任务丢弃任何一个任务请求的话，你可以选择这个策略。
         * ThreadPoolExecutor.DiscardPolicy： 不处理新任务，直接丢弃掉。
         * ThreadPoolExecutor.DiscardOldestPolicy： 此策略将丢弃最早的未处理的任务请求。
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
