package com.agh.bsct.algorithm;

import com.agh.bsct.algorithm.services.runner.asyncrunner.CustomAsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@ComponentScan({"com.agh.bsct.algorithm.controllers",
                "com.agh.bsct.algorithm.services",
                "com.agh.bsct.algorithm.algorithms"})
@EnableAsync
public class Algorithm implements AsyncConfigurer {

    public static final String SPRING_THREAD_POOL_NAME = "threadPoolTaskExecutor";

    public static void main(String[] args) {
        SpringApplication.run(Algorithm.class, args);
    }

    @Override
    @Bean(name = SPRING_THREAD_POOL_NAME)
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(16);
        threadPoolTaskExecutor.setMaxPoolSize(32);
        threadPoolTaskExecutor.setQueueCapacity(64);
        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

}
