package com.agh.bsct.algorithm.services.runner.asyncrunner;

import com.agh.bsct.algorithm.Algorithm;
import com.agh.bsct.algorithm.algorithms.AlgorithmRunner;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AsyncAlgorithmTaskRunner {

    private static AtomicInteger THREAD_COUNT = new AtomicInteger(0);

    private AlgorithmRunner algorithmRunner;

    @Autowired
    public AsyncAlgorithmTaskRunner(AlgorithmRunner algorithmRunner) {
        this.algorithmRunner = algorithmRunner;
    }

    @Async(Algorithm.SPRING_THREAD_POOL_NAME)
    public Future<Integer> run(AlgorithmTask algorithmTask) {
        algorithmRunner.run(algorithmTask);
        return new AsyncResult<>(THREAD_COUNT.getAndIncrement());
    }


}
