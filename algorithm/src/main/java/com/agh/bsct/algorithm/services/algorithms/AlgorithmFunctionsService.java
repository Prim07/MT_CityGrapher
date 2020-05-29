package com.agh.bsct.algorithm.services.algorithms;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.graph.ShortestPathsDistances;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AlgorithmFunctionsService {

    public double calculateFunctionValue(ShortestPathsDistances shortestPathsDistances, List<GraphNode> globalState) {
        var distancesToClosestHospitalsSum = 0.0;
        var longestDistance = shortestPathsDistances.getLongestDistance();

        for (Map<Long, Double> currentNodeShortestPathsDistances : shortestPathsDistances.getDistances().values()) {
            distancesToClosestHospitalsSum +=
                    getDistanceToClosestHospital(currentNodeShortestPathsDistances, globalState, longestDistance);
        }

        return distancesToClosestHospitalsSum;
    }

    private double getDistanceToClosestHospital(Map<Long, Double> currentNodeShortestPathsDistances,
                                                List<GraphNode> globalState,
                                                double longestDistance) {
        var distanceToClosestHospital = Double.MAX_VALUE;

        for (var currentGlobalState : globalState) {
            var distanceToHospital = currentNodeShortestPathsDistances.get(currentGlobalState.getId());
            var weightedDistanceToHospital = getWeightedDistanceToHospital(distanceToHospital,
                    currentGlobalState.getWeight(), longestDistance);

            if (weightedDistanceToHospital < distanceToClosestHospital) {
                distanceToClosestHospital = weightedDistanceToHospital;
            }
        }

        return distanceToClosestHospital;
    }

    private double getWeightedDistanceToHospital(double distanceToHospital, int weight, double longestDistance) {
        return distanceToHospital / (weight * longestDistance);
    }
}
