package com.qyling.self_bi.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Data
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
    ThreadFactory threadFactory = new ThreadFactory() {
        private int threadId = 0;
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("线程" + threadId++);
            return thread;
        }
    };

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2,
            4,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2),
            threadFactory,
            new ThreadPoolExecutor.AbortPolicy()
    );
    return threadPoolExecutor;
    }
}
