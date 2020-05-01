package com.agh.bsct.algorithm.services.graph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ShortestPathsDistances implements Serializable {

    private final Map<Long, Map<Long, Double>> shortestPathsDistances;

    public ShortestPathsDistances(HashMap<Long, Map<Long, Double>> shortestPathsDistances) {
        this.shortestPathsDistances = shortestPathsDistances;
    }

    public Map<Long, Map<Long, Double>> getDistances() {
        return shortestPathsDistances;
    }

}
