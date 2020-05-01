package com.agh.bsct.algorithm.services.database;

import com.agh.bsct.algorithm.services.database.entities.ShortestPathsDistancesEntity;
import com.agh.bsct.algorithm.services.database.repositories.ShortestPathsDistancesRepository;
import com.agh.bsct.algorithm.services.graph.ShortestPathsDistances;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private final ShortestPathsDistancesRepository shortestPathsDistancesRepository;

    @Autowired
    public DatabaseService(ShortestPathsDistancesRepository shortestPathsDistancesRepository) {
        this.shortestPathsDistancesRepository = shortestPathsDistancesRepository;
    }

    public void save(ShortestPathsDistances shortestPathsDistances, String cityName) {
        ShortestPathsDistancesEntity shortestPathsDistancesEntity = new ShortestPathsDistancesEntity();
        shortestPathsDistancesEntity.setCityName(cityName);
        shortestPathsDistancesEntity.setShortestPathsDistances(shortestPathsDistances);
        shortestPathsDistancesRepository.save(shortestPathsDistancesEntity);
    }

    public boolean doesDataExist(String cityName) {
        return shortestPathsDistancesRepository.existsByCityName(cityName);
    }

    public ShortestPathsDistances getShortestPathsDistances(String cityName) {
        return shortestPathsDistancesRepository.findTopByCityName(cityName)
                .orElseThrow(() -> new IllegalStateException("City " + cityName + " does not exist in database."))
                .getShortestPathsDistances();
    }

}
