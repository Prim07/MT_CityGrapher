package com.agh.bsct.algorithm.services.runner;

import com.agh.bsct.algorithm.controllers.mapper.GraphDataMapper;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.algorithm.services.runner.cache.AlgorithmResultCache;
import com.agh.bsct.algorithm.services.runner.cache.GuavaLoadingCache;
import com.agh.bsct.algorithm.services.runner.repository.AlgorithmTaskRepository;
import com.agh.bsct.algorithm.services.runner.repository.AsyncTaskRepository;
import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;
import com.agh.bsct.api.entities.graphdata.GraphDataDTO;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@RunWith(JUnitPlatform.class)
class GuavaLoadingCacheTest {

    private AlgorithmResultCache algorithmResultCache;
    private AlgorithmOrderDTO algorithmOrderDTO;

    @BeforeEach
    void setUp() {
        var algorithmTaskRepository = new AlgorithmTaskRepository();
        var asyncTaskRepository = new AsyncTaskRepository();
        var graphDataMapper = new GraphDataMapper();
        algorithmResultCache = new GuavaLoadingCache(algorithmTaskRepository, asyncTaskRepository, graphDataMapper);

        GraphDataDTO graphDataDTO = new GraphDataDTO(Collections.emptyList(), Collections.emptyList());
        algorithmOrderDTO = new AlgorithmOrderDTO(2, graphDataDTO, "sa");
    }

    @Test
    void shouldCreateSingleTask() {
        AlgorithmTask task = tryCreateNewTask();

        Assert.assertNotNull(task);
        Assert.assertNotNull(task.getTaskId());
    }

    @Test
    void shouldCreateMultipleTasks() {
        String firstTaskId = tryCreateNewTask().getTaskId();
        String secondTaskId = tryCreateNewTask().getTaskId();
        String thirdTaskId = tryCreateNewTask().getTaskId();

        Assert.assertNotNull(firstTaskId);
        Assert.assertNotNull(secondTaskId);
        Assert.assertNotNull(thirdTaskId);
    }

    @Test
    void shouldReturnCreatedTask() {
        String taskId = tryCreateNewTask().getTaskId();
        AlgorithmTask task = tryGetTask(taskId);

        Assert.assertNotNull(task);
        Assert.assertEquals(taskId, task.getTaskId());
    }

    @Test
    void shouldNotCreateNewTaskWhenGetTaskMethodIsCalledMultipleTimes() {
        String taskId = tryCreateNewTask().getTaskId();
        AlgorithmTask taskFromFirstGetAttempt = tryGetTask(taskId);
        AlgorithmTask taskFromSecondGetAttempt = tryGetTask(taskId);
        AlgorithmTask taskFromThirdGetAttempt = tryGetTask(taskId);

        Assert.assertEquals(taskFromFirstGetAttempt, taskFromSecondGetAttempt);
        Assert.assertEquals(taskFromSecondGetAttempt, taskFromThirdGetAttempt);
    }

    private AlgorithmTask tryCreateNewTask() {
        AlgorithmTask task = null;
        try {
            task = algorithmResultCache.createNewTask(algorithmOrderDTO);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return task;
    }

    private AlgorithmTask tryGetTask(String taskId) {
        AlgorithmTask task = null;
        try {
            task = algorithmResultCache.getTask(taskId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return task;
    }
}