package com.agh.bsct.algorithm.services.algorithms;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.graph.ShortestPathsDistances;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AlgorithmFunctionsService {

    public double calculateFunctionValue(ShortestPathsDistances shortestPathsDistances,
                                         List<GraphNode> globalState) {
        var distancesToClosestHospitalsSum = 0.0;
        var longestDistance = shortestPathsDistances.getLongestDistance();

        for (Map<Long, Double> currentNodeShortestPathsDistance : shortestPathsDistances.getDistances().values()) {
            var distanceToClosestHospitals = Double.MAX_VALUE;
            for (var currentGlobalStateNodeId : globalState) {
                var distanceToHospital = currentNodeShortestPathsDistance.get(currentGlobalStateNodeId.getId());
                var weightedDistanceToHospital = getWeightedDistanceToHospital(distanceToHospital,
                        currentGlobalStateNodeId.getWeight(), longestDistance);

                if (weightedDistanceToHospital < distanceToClosestHospitals) {
                    distanceToClosestHospitals = weightedDistanceToHospital;
                }
            }
            distancesToClosestHospitalsSum += distanceToClosestHospitals;
        }

        return distancesToClosestHospitalsSum;
    }

    private double getWeightedDistanceToHospital(double distanceToHospital, int weight, double longestDistance) {
        return distanceToHospital / (weight * longestDistance);
    }
}
