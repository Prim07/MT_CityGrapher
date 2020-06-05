package com.agh.bsct.algorithm.services.algorithms;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.graph.ShortestPathsDistances;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AlgorithmFunctionsService {

    private static final double FITNESS_SCORE_ROUNDING_PRECISION = 0.001;

    public double calculateFunctionValue(ShortestPathsDistances shortestPathsDistances, List<GraphNode> globalState) {
        var distancesToClosestHospitalsSum = 0.0;
        var longestDistance = shortestPathsDistances.getLongestDistance();

        for (Map<Long, Double> currentNodeShortestPathsDistances : shortestPathsDistances.getDistances().values()) {
            distancesToClosestHospitalsSum +=
                    getDistanceToClosestHospital(currentNodeShortestPathsDistances, globalState, longestDistance);
        }

        return distancesToClosestHospitalsSum;
    }

    public boolean isFunctionValueBetter(double candidateFunctionValue, double currentBestFunctionValue) {
        return Math.abs(candidateFunctionValue - currentBestFunctionValue) > FITNESS_SCORE_ROUNDING_PRECISION
                && candidateFunctionValue < currentBestFunctionValue;
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

    private double getWeightedDistanceToHospital(double distanceToHospital, double weight, double longestDistance) {
        if (isNodeOnTheEdgeOfTheCityOrNotConsidered(weight)) {
            return longestDistance * 100;
        }

        return (distanceToHospital / longestDistance) / weight;
    }

    private boolean isNodeOnTheEdgeOfTheCityOrNotConsidered(double abs) {
        return abs < 0;
    }
}
