package com.agh.bsct.algorithm.services.runner.cache;

import com.agh.bsct.algorithm.controllers.mapper.GraphDataMapper;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import com.agh.bsct.algorithm.services.runner.repository.AlgorithmTaskRepository;
import com.agh.bsct.algorithm.services.runner.repository.AsyncTaskRepository;
import com.agh.bsct.api.entities.algorithmorder.AlgorithmOrderDTO;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GuavaLoadingCache implements AlgorithmResultCache {

    private AlgorithmTaskRepository algorithmTaskRepository;
    private AsyncTaskRepository asyncTaskRepository;
    private GraphDataMapper graphDataMapper;
    private LoadingCache<String, AlgorithmTask> idToTaskCache;

    @Autowired
    public GuavaLoadingCache(AlgorithmTaskRepository algorithmTaskRepository,
                             AsyncTaskRepository asyncTaskRepository,
                             GraphDataMapper graphDataMapper) {
        this.algorithmTaskRepository = algorithmTaskRepository;
        this.asyncTaskRepository = asyncTaskRepository;
        this.graphDataMapper = graphDataMapper;
        this.idToTaskCache = getInitializedLoadingCache();
    }

    @Override
    public AlgorithmTask createNewTask(AlgorithmOrderDTO algorithmOrderDTO) {
        var id = UUID.randomUUID().toString();
        var graph = graphDataMapper.mapToGraph(algorithmOrderDTO.getGraphDataDTO());
        AlgorithmTask algorithmTask = new AlgorithmTask(id, algorithmOrderDTO, graph);
        algorithmTaskRepository.put(id, algorithmTask);
        return algorithmTask;
    }

    @Override
    public AlgorithmTask getTask(String id) throws ExecutionException {
        return idToTaskCache.get(id);
    }

    private LoadingCache<String, AlgorithmTask> getInitializedLoadingCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .removalListener(getRemovalListener())
                .build(getCacheLoader());
    }

    private RemovalListener<String, AlgorithmTask> getRemovalListener() {
        return removalNotification -> {
            String key = removalNotification.getKey();
            algorithmTaskRepository.remove(key);
            asyncTaskRepository.remove(key);
        };
    }

    private CacheLoader<String, AlgorithmTask> getCacheLoader() {
        return new CacheLoader<>() {
            @Override
            public AlgorithmTask load(@NonNull String id) {
                return algorithmTaskRepository.getAlgorithmTaskById(id);
            }
        };
    }
}
