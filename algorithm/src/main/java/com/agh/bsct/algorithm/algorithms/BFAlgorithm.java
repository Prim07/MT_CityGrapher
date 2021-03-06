package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.colours.ColoursService;
import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.graph.GraphService;
import com.agh.bsct.algorithm.services.graph.ShortestPathsDistances;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.agh.bsct.algorithm.algorithms.dummylogger.DummyLogger.printMessage;

@Component
@Qualifier(BFAlgorithm.BRUTE_FORCE_QUALIFIER)
public class BFAlgorithm implements IAlgorithm {

    static final String BRUTE_FORCE_QUALIFIER = "bruteForceAlgorithm";

    private final AlgorithmFunctionsService algorithmFunctionsService;
    private final GraphService graphService;
    private final ColoursService coloursService;


    @Autowired
    public BFAlgorithm(AlgorithmFunctionsService algorithmFunctionsService,
                       GraphService graphService,
                       ColoursService coloursService) {
        this.algorithmFunctionsService = algorithmFunctionsService;
        this.graphService = graphService;
        this.coloursService = coloursService;
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING_SHORTEST_PATHS);
        printMessage("Starting calculating shortest paths distances");
        final var shortestPathsDistances = graphService.getShortestPathsDistances(algorithmTask);

        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);
        printMessage("Starting calculating");
        var bestState = getBestState(algorithmTask, shortestPathsDistances);

        updateHospitalsInAlgorithmTask(algorithmTask, bestState);
        coloursService.updateColoursInNodes(algorithmTask, shortestPathsDistances);
        printMessage("Set status to SUCCESS");
        printMessage("BF Best state value: " +
                algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, bestState));
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
    }

    private List<GraphNode> getBestState(AlgorithmTask algorithmTask,
                                         ShortestPathsDistances shortestPathsDistances) {
        Integer numberOfResults = algorithmTask.getNumberOfResults();

        if (numberOfResults.equals(1)) {
            return getBestStateForOneHospital(algorithmTask, shortestPathsDistances);
        }
        if (numberOfResults.equals(2)) {
            return getBestStateForTwoHospitals(algorithmTask, shortestPathsDistances);
        }
        if (numberOfResults.equals(3)) {
            return getBestStateForThreeHospitals(algorithmTask, shortestPathsDistances);
        }

        throw new IllegalStateException("Brute Force Algorithm cannot be applied to " + numberOfResults
                + " requested results. Please choose 1, 2 or 3.");
    }

    private List<GraphNode> getBestStateForOneHospital(AlgorithmTask algorithmTask,
                                                       ShortestPathsDistances shortestPathsDistances) {
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;
        var incidenceMap = algorithmTask.getGraph().getIncidenceMap();

        for (var node : incidenceMap.keySet()) {
            List<GraphNode> acceptedState = Collections.singletonList(node);
            var acceptedFunctionValue = algorithmFunctionsService.calculateFunctionValue(
                    shortestPathsDistances, acceptedState);
            if (algorithmFunctionsService.isFunctionValueBetter(acceptedFunctionValue, bestFunctionValue)) {
                bestFunctionValue = acceptedFunctionValue;
                bestState = acceptedState;
                updateHospitalsInAlgorithmTask(algorithmTask, bestState);
                updateFitnessScoreInAlgorithmTask(algorithmTask, bestFunctionValue);
            }
        }

        return bestState;
    }

    private List<GraphNode> getBestStateForTwoHospitals(AlgorithmTask algorithmTask,
                                                        ShortestPathsDistances shortestPathsDistances) {
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;
        var incidenceMap = algorithmTask.getGraph().getIncidenceMap();

        for (var node1 : incidenceMap.keySet()) {
            for (var node2 : incidenceMap.keySet()) {
                List<GraphNode> acceptedState = Arrays.asList(node1, node2);
                var acceptedFunctionValue =
                        algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
                if (algorithmFunctionsService.isFunctionValueBetter(acceptedFunctionValue, bestFunctionValue)) {
                    bestFunctionValue = acceptedFunctionValue;
                    bestState = acceptedState;
                    updateHospitalsInAlgorithmTask(algorithmTask, bestState);
                    updateFitnessScoreInAlgorithmTask(algorithmTask, bestFunctionValue);
                }
            }

        }

        return bestState;
    }

    private List<GraphNode> getBestStateForThreeHospitals(AlgorithmTask algorithmTask,
                                                          ShortestPathsDistances shortestPathsDistances) {
        List<GraphNode> bestState = Collections.emptyList();
        double bestFunctionValue = Double.MAX_VALUE;
        var incidenceMap = algorithmTask.getGraph().getIncidenceMap();

        for (var node1 : incidenceMap.keySet()) {
            for (var node2 : incidenceMap.keySet()) {
                for (var node3 : incidenceMap.keySet()) {
                    List<GraphNode> acceptedState = Arrays.asList(node1, node2, node3);
                    var acceptedFunctionValue =
                            algorithmFunctionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
                    if (algorithmFunctionsService.isFunctionValueBetter(acceptedFunctionValue, bestFunctionValue)) {
                        bestFunctionValue = acceptedFunctionValue;
                        bestState = acceptedState;
                        updateHospitalsInAlgorithmTask(algorithmTask, bestState);
                        updateFitnessScoreInAlgorithmTask(algorithmTask, bestFunctionValue);
                    }
                }

            }
        }

        return bestState;
    }

    private void updateHospitalsInAlgorithmTask(AlgorithmTask algorithmTask, List<GraphNode> bestState) {
        algorithmTask.setHospitals(bestState);
    }

    private void updateFitnessScoreInAlgorithmTask(AlgorithmTask algorithmTask, double fitnessScore) {
        algorithmTask.setFitnessScore(fitnessScore);
    }

}
