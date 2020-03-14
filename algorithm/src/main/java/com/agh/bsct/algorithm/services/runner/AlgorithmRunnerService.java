package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.algorithm.services.runner.asyncrunner.AsyncAlgorithmTaskRunner;
import com.agh.bsct.algorithm.services.runner.cache.AlgorithmResultCache;
import com.agh.bsct.algorithm.services.runner.cache.GuavaLoadingCache;
import com.agh.bsct.algorithm.services.runner.repository.AsyncTaskRepository;
import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class AlgorithmRunnerService {

    private AlgorithmResultCache algorithmResultCache;
    private AsyncAlgorithmTaskRunner asyncAlgorithmTaskRunner;
    private AsyncTaskRepository asyncTaskRepository;

    @Autowired
    public AlgorithmRunnerService(GuavaLoadingCache guavaLoadingCache,
                                  AsyncAlgorithmTaskRunner asyncAlgorithmTaskRunner,
                                  AsyncTaskRepository asyncTaskRepository) {
        this.algorithmResultCache = guavaLoadingCache;
        this.asyncAlgorithmTaskRunner = asyncAlgorithmTaskRunner;
        this.asyncTaskRepository = asyncTaskRepository;
    }

    public String run(AlgorithmOrderDTO algorithmOrderDTO) throws ExecutionException {
        AlgorithmTask algorithmTask = algorithmResultCache.createNewTask(algorithmOrderDTO);
        Future<Integer> asyncTask = asyncAlgorithmTaskRunner.run(algorithmTask);
        asyncTaskRepository.put(algorithmTask.getTaskId(), asyncTask);
        return algorithmTask.getTaskId();
    }

    public AlgorithmTask get(String id) throws ExecutionException {
        cancelTaskIfItsThreadIsCancelled(id);
        return algorithmResultCache.getTask(id);
    }

    private void cancelTaskIfItsThreadIsCancelled(String id) throws ExecutionException {
        Future asyncTaskById = asyncTaskRepository.getAsyncTaskById(id);
        if (asyncTaskById.isCancelled()) {
            AlgorithmTask algorithmTask = algorithmResultCache.getTask(id);
            algorithmTask.setStatus(AlgorithmCalculationStatus.CANCELLED);
            asyncTaskRepository.remove(id);
        }
    }

    public void cancel(String id) {
        Future asyncTaskById = asyncTaskRepository.getAsyncTaskById(id);
        asyncTaskById.cancel(true);
    }
}
