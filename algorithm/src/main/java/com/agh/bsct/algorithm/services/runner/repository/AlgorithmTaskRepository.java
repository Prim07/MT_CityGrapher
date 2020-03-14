package com.agh.bsct.algorithm.services.runner.repository;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AlgorithmTaskRepository {

    private final ConcurrentMap<String, AlgorithmTask> idToTask = new ConcurrentHashMap<>();

    public AlgorithmTask put(String id, AlgorithmTask algorithmTask) {
        return idToTask.putIfAbsent(id, algorithmTask);
    }

    public AlgorithmTask getAlgorithmTaskById(String id) {
        return idToTask.get(id);
    }

    public void remove(String id) {
        idToTask.remove(id);
    }

}
