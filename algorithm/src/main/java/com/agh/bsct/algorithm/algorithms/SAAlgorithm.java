package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.algorithms.outputwriter.GnuplotOutputWriter;
import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.algorithms.LatestChangesService;
import com.agh.bsct.algorithm.services.colours.ColoursService;
import com.agh.bsct.algorithm.services.graph.GraphEdge;
import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.agh.bsct.algorithm.algorithms.dummylogger.DummyLogger.printMessage;

@Component
@Qualifier(SAAlgorithm.SIMULATED_ANNEALING_QUALIFIER)
public class SAAlgorithm implements IAlgorithm {

    static final String SIMULATED_ANNEALING_QUALIFIER = "simulatedAnnealingAlgorithm";

    private static final double INITIAL_TEMPERATURE = 500000.0;
    private static final double ALPHA = 0.999;
    private static final int QUEUE_SIZE = 50;

    private final AlgorithmFunctionsService functionsService;
    private final GraphService graphService;
    private final ColoursService coloursService;
    private final GnuplotOutputWriter gnuplotOutputWriter;
    private final Random random;

    @Autowired
    public SAAlgorithm(AlgorithmFunctionsService algorithmFunctionsService,
                       GraphService graphService,
                       ColoursService coloursService,
                       GnuplotOutputWriter gnuplotOutputWriter) {
        this.functionsService = algorithmFunctionsService;
        this.graphService = graphService;
        this.coloursService = coloursService;
        this.gnuplotOutputWriter = gnuplotOutputWriter;
        this.random = new Random();
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING_SHORTEST_PATHS);
        printMessage("Starting calculating shortest paths distances");
        final var shortestPathsDistances = graphService.getShortestPathsDistances(algorithmTask);

        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);

        final var incidenceMap = algorithmTask.getGraph().getIncidenceMap();

        var numberOfIterations = 0;
        var temperature = INITIAL_TEMPERATURE;

        var acceptedState = initializeGlobalState(algorithmTask, incidenceMap);
        var bestState = acceptedState;
        var acceptedFunctionValue = functionsService.calculateFunctionValue(shortestPathsDistances, acceptedState);
        var bestFunctionValue = acceptedFunctionValue;
        var latestChangesService = new LatestChangesService(QUEUE_SIZE);

        gnuplotOutputWriter.initializeResources(algorithmTask.getTaskId());

        printMessage("Starting iterating");
        while (latestChangesService.shouldIterate()) {
            var localState = changeRandomlyState(incidenceMap, acceptedState);
            var localFunctionValue = functionsService.calculateFunctionValue(shortestPathsDistances, localState);
            double delta = localFunctionValue - acceptedFunctionValue;

            if (isBetterStateFound(acceptedFunctionValue, localFunctionValue)) {
                latestChangesService.add(Boolean.TRUE);
                acceptedState = localState;
                acceptedFunctionValue = localFunctionValue;
                if (isAcceptedStateBetterThanBestState(acceptedFunctionValue, bestFunctionValue)) {
                    bestFunctionValue = acceptedFunctionValue;
                    bestState = acceptedState;
                    updateHospitalsInAlgorithmTask(algorithmTask, bestState);
                    updateFitnessScoreInAlgorithmTask(algorithmTask, acceptedFunctionValue);
                }
            } else {
                var worseResultAcceptanceProbability = random.nextDouble();
                var acceptanceProbability = Math.exp(-delta / temperature);
                if (shouldWorseChangeBeApplied(worseResultAcceptanceProbability, acceptanceProbability)) {
                    latestChangesService.add(Boolean.TRUE);
                    acceptedState = localState;
                    acceptedFunctionValue = localFunctionValue;
                    updateHospitalsInAlgorithmTask(algorithmTask, bestState);
                    updateFitnessScoreInAlgorithmTask(algorithmTask, acceptedFunctionValue);
                } else {
                    latestChangesService.add(Boolean.FALSE);
                }
            }

            gnuplotOutputWriter.writeLineIfEnabled(numberOfIterations, temperature, Math.abs(delta), localFunctionValue,
                    acceptedFunctionValue, bestFunctionValue);

            temperature = ALPHA * temperature;
            numberOfIterations++;
        }
        printMessage("Ended iterating");

        gnuplotOutputWriter.closeResources();

        updateHospitalsInAlgorithmTask(algorithmTask, bestState);
        updateFitnessScoreInAlgorithmTask(algorithmTask, acceptedFunctionValue);
        coloursService.updateColoursInNodes(algorithmTask, shortestPathsDistances);

        printMessage("SA Best state value: " + bestFunctionValue);
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
    }

    private List<GraphNode> initializeGlobalState(AlgorithmTask algorithmTask,
                                                  Map<GraphNode, List<GraphEdge>> incidenceMap) {
        var graphNodesList = new ArrayList<>(incidenceMap.keySet());
        var numberOfResults = algorithmTask.getNumberOfResults();
        var globalState = new ArrayList<GraphNode>(numberOfResults);

        for (var i = 0; i < numberOfResults; ++i) {
            int chosenNodeId = random.nextInt(algorithmTask.getGraph().getIncidenceMap().size());
            while (globalState.contains(graphNodesList.get(chosenNodeId))) {
                chosenNodeId = random.nextInt(algorithmTask.getGraph().getIncidenceMap().size());
            }
            globalState.add(i, graphNodesList.get(chosenNodeId));
        }

        return globalState;
    }

    private ArrayList<GraphNode> changeRandomlyState(Map<GraphNode, List<GraphEdge>> incidenceMap,
                                                     List<GraphNode> globalState) {
        var localState = new ArrayList<>(globalState);
        var nodeToChangeIndex = random.nextInt(localState.size());
        var nodeToChangeNeighbours = incidenceMap.get(localState.get(nodeToChangeIndex));
        var graphEdge = nodeToChangeNeighbours.get(random.nextInt(nodeToChangeNeighbours.size()));
        var endGraphNode = graphEdge.getEndGraphNode();

        while (localState.contains(endGraphNode)) {
            nodeToChangeIndex = random.nextInt(localState.size());
            nodeToChangeNeighbours = incidenceMap.get(localState.get(nodeToChangeIndex));
            graphEdge = nodeToChangeNeighbours.get(random.nextInt(nodeToChangeNeighbours.size()));
            endGraphNode = graphEdge.getEndGraphNode();
        }

        localState.set(nodeToChangeIndex, endGraphNode);

        return localState;
    }

    private boolean isBetterStateFound(double acceptedFunctionValue, double localFunctionValue) {
        return functionsService.isFunctionValueBetter(localFunctionValue, acceptedFunctionValue);
    }

    private boolean isAcceptedStateBetterThanBestState(double acceptedFunctionValue, double bestFunctionValue) {
        return functionsService.isFunctionValueBetter(acceptedFunctionValue, bestFunctionValue);
    }

    private boolean shouldWorseChangeBeApplied(double worseResultAcceptanceProbability, double acceptanceProbability) {
        return worseResultAcceptanceProbability < acceptanceProbability;
    }

    private void updateHospitalsInAlgorithmTask(AlgorithmTask algorithmTask, List<GraphNode> bestState) {
        algorithmTask.setHospitals(bestState);
    }

    private void updateFitnessScoreInAlgorithmTask(AlgorithmTask algorithmTask, double fitnessScore) {
        algorithmTask.setFitnessScore(fitnessScore);
    }

}
