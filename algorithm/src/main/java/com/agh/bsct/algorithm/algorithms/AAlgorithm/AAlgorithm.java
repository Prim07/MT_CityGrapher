package com.agh.bsct.algorithm.algorithms.AAlgorithm;

import com.agh.bsct.algorithm.algorithms.IAlgorithm;
import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.colours.ColoursService;
import com.agh.bsct.algorithm.services.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.agh.bsct.algorithm.algorithms.dummylogger.DummyLogger.printMessage;

@Component
@Qualifier(AAlgorithm.ANT_QUALIFIER)
public class AAlgorithm implements IAlgorithm {

    public static final String ANT_QUALIFIER = "antAlgorithm";

    private static final int NUMBER_OF_EPOCHS = 10000;

    private final AlgorithmFunctionsService algorithmFunctionsService;
    private final GraphService graphService;
    private final ColoursService coloursService;
    private final Population population;

    @Autowired
    public AAlgorithm(AlgorithmFunctionsService algorithmFunctionsService,
                      GraphService graphService,
                      ColoursService coloursService, Population population) {
        this.algorithmFunctionsService = algorithmFunctionsService;
        this.graphService = graphService;
        this.coloursService = coloursService;
        this.population = population;
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING_SHORTEST_PATHS);
        printMessage("Starting calculating shortest paths distances");
//        final var shortestPathsDistances = graphService.getShortestPathsDistances(algorithmTask);

        population.initializePopulation(algorithmTask);
        int epochNumber = 0;

        while (shouldContinue(epochNumber)) {
            population.calculateEachIndividualFitnessScore();

            epochNumber++;
        }
    }

    private boolean shouldContinue(int epochNumber) {
        return epochNumber < NUMBER_OF_EPOCHS;
    }
}
