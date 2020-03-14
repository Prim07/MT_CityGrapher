package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.algorithms.CrossingsService;
import com.agh.bsct.algorithm.services.graph.GraphEdge;
import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Qualifier(BFAlgorithm.BRUTE_FORCE_QUALIFIER)
public class BFAlgorithm implements IAlgorithm {

    static final String BRUTE_FORCE_QUALIFIER = "bruteForceAlgorithm";

    private AlgorithmFunctionsService algorithmFunctionsService;
    private CrossingsService crossingsService;
    private GraphService graphService;


    @Autowired
    public BFAlgorithm(AlgorithmFunctionsService algorithmFunctionsService,
                       CrossingsService crossingsService,
                       GraphService graphService) {
        this.algorithmFunctionsService = algorithmFunctionsService;
        this.crossingsService = crossingsService;
        this.graphService = graphService;
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);

        final var shortestPathsDistances = graphService.getShortestPathsDistances(algorithmTask);

        var bestState = getBestState(algorithmTask, shortestPathsDistances);

        var hospitals = crossingsService.getGeographicalNodesForBestState(bestState, algorithmTask.getGraphDataDTO());
        algorithmTask.setHospitals(hospitals);

        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
    }

    private List<GraphNode> getBestState(AlgorithmTask algorithmTask,
                                         Map<Long, Map<Long, Double>> shortestPathsDistances) {
        Integer numberOfResults = algorithmTask.getNumberOfResults();
        Map<GraphNode, List<GraphEdge>> incidenceMap = algorithmTask.getGraph().getIncidenceMap();
        if (numberOfResults.equals(1)) {
            return getBestStateForOneHospital(incidenceMap, shortestPathsDistances);
        }
        if (numberOfResults.equals(2)) {
            return getBestStateForTwoHospitals(incidenceMap, shortestPathsDistances);
        }
        if (numberOfResults.equals(3)) {
            return getBestStateForThreeHospitals(incidenceMap, shortestPathsDistances);
        }
        throw new IllegalStateException("Brute Force Algorithm cannot be applied to " + numberOfResults
                + " requested results. Please choose 1, 2 or 3.");
    }

    private List<GraphNode> getBestStateForOneHospital(Map<GraphNode, List<GraphEdge>> incidenceMap,
                                                       Map<Long, Map<Long, Double>> shortestPathsDistances) {
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;

        for (var node : incidenceMap.keySet()) {
            List<GraphNode> acceptedState = Collections.singletonList(node);
            var acceptedFunctionValue = algorithmFunctionsService.calculateFunctionValue(
                    shortestPathsDistances, acceptedState);
            if (acceptedFunctionValue < bestFunctionValue) {
                bestFunctionValue = acceptedFunctionValue;
                bestState = acceptedState;
            }
        }

        return bestState;
    }

    private List<GraphNode> getBestStateForTwoHospitals(Map<GraphNode, List<GraphEdge>> incidenceMap,
                                                        Map<Long, Map<Long, Double>> shortestPathsDistances) {
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;

        for (var node1 : incidenceMap.keySet()) {
            for (var node2 : incidenceMap.keySet()) {
                List<GraphNode> acceptedState = Arrays.asList(node1, node2);
                var acceptedFunctionValue =
                        algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
                if (acceptedFunctionValue < bestFunctionValue) {
                    bestFunctionValue = acceptedFunctionValue;
                    bestState = acceptedState;
                }
            }

        }

        return bestState;
    }

    private List<GraphNode> getBestStateForThreeHospitals(Map<GraphNode, List<GraphEdge>> incidenceMap,
                                                          Map<Long, Map<Long, Double>> shortestPathsDistances) {
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;


        for (var node1 : incidenceMap.keySet()) {
            for (var node2 : incidenceMap.keySet()) {
                for (var node3 : incidenceMap.keySet()) {
                    List<GraphNode> acceptedState = Arrays.asList(node1, node2, node3);
                    var acceptedFunctionValue =
                            algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
                    if (acceptedFunctionValue < bestFunctionValue) {
                        bestFunctionValue = acceptedFunctionValue;
                        bestState = acceptedState;
                    }
                }

            }
        }

        return bestState;
    }

}
