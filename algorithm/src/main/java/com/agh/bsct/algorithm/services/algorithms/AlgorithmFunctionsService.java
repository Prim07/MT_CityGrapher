package com.agh.bsct.algorithm.services.algorithms;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AlgorithmFunctionsService {

    public double calculateFunctionValue(Map<Long, Map<Long, Double>> shortestPathsDistances,
                                         List<GraphNode> globalState) {
        var distancesToClosestHospitalsSum = 0.0;

        for (Map<Long, Double> currentNodeShortestPathsDistance : shortestPathsDistances.values()) {
            var distanceToClosestHospitals = Double.MAX_VALUE;
            for (var currentGlobalStateNodeId : globalState) {
                var distanceToHospital = currentNodeShortestPathsDistance.get(currentGlobalStateNodeId.getId());
                if (distanceToClosestHospitals > distanceToHospital) {
                    distanceToClosestHospitals = distanceToHospital;
                }
            }
            distancesToClosestHospitalsSum += distanceToClosestHospitals;
        }

        return distancesToClosestHospitalsSum;
    }
}
