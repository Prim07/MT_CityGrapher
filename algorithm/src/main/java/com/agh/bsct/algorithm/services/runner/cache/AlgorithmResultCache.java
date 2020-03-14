package com.agh.bsct.algorithm.services.runner.cache;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;

import java.util.concurrent.ExecutionException;

public interface AlgorithmResultCache {

    AlgorithmTask createNewTask(AlgorithmOrderDTO algorithmOrderDTO) throws ExecutionException;

    AlgorithmTask getTask(String id) throws ExecutionException;

}
