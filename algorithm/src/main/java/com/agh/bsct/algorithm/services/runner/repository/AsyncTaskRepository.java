package com.agh.bsct.algorithm.services.runner.repository;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AsyncTaskRepository {

    private final ConcurrentMap<String, Future> idToAsyncTask = new ConcurrentHashMap<>();

    public void put(String id, Future asyncTask) {
        idToAsyncTask.putIfAbsent(id, asyncTask);
    }

    public Future getAsyncTaskById(String id) {
        return idToAsyncTask.get(id);
    }

    public void remove(String id) {
        idToAsyncTask.remove(id);
    }

}
