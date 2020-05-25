package com.agh.bsct.algorithm.services.graph;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ShortestPathsDistances implements Serializable {

    private final Map<Long, Map<Long, Double>> shortestPathsDistances;
    private final Double longestDistance;

    public ShortestPathsDistances(HashMap<Long, Map<Long, Double>> shortestPathsDistances) {
        this.shortestPathsDistances = shortestPathsDistances;
        this.longestDistance = findLongestDistance();
    }

    public Map<Long, Map<Long, Double>> getDistances() {
        return shortestPathsDistances;
    }

    public Double getLongestDistance() {
        return longestDistance;
    }

    private Double findLongestDistance() {
        return shortestPathsDistances.values().stream()
                .flatMap(longDoubleMap -> longDoubleMap.values().stream())
                .max(Comparator.naturalOrder())
                .orElse(10000d);
    }

}
