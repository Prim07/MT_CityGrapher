package com.agh.bsct.algorithm.algorithms.GAlgorithm;

import com.agh.bsct.algorithm.algorithms.IAlgorithm;
import com.agh.bsct.algorithm.services.algorithms.LatestChangesService;
import com.agh.bsct.algorithm.services.colours.ColoursService;
import com.agh.bsct.algorithm.services.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.agh.bsct.algorithm.algorithms.dummylogger.DummyLogger.printMessage;

@Component
@Scope("prototype")
@Qualifier(GAlgorithm.GENETIC_QUALIFIER)
public class GAlgorithm implements IAlgorithm {

    public static final String GENETIC_QUALIFIER = "geneticAlgorithm";

    private static final int QUEUE_SIZE = 50;

    private final GraphService graphService;
    private final ColoursService coloursService;

    @Autowired
    public GAlgorithm(GraphService graphService, ColoursService coloursService) {
        this.graphService = graphService;
        this.coloursService = coloursService;
    }

    @Override
    public void run(AlgorithmTask algorithmTask) {
        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING_SHORTEST_PATHS);
        printMessage("Starting calculating shortest paths distances");
        final var shortestPathsDistances = graphService.getShortestPathsDistances(algorithmTask);
        var population = new Population(algorithmTask, shortestPathsDistances);

        int epochNumber = 0;

        algorithmTask.setStatus(AlgorithmCalculationStatus.CALCULATING);
        population.initializePopulation();
        var latestChangesService = new LatestChangesService(QUEUE_SIZE);

        while (latestChangesService.shouldIterate()) {
            population.calculateEachIndividualFitnessScore();
            population.sortByFitnessScore();
            boolean wasUpdated = population.updateBestState();
            population.chooseParentsPopulation();
            population.crossoverParents();
            population.mutateParents();
            population.updatePopulation();

            latestChangesService.add(wasUpdated);
            if (wasUpdated) {
                PopulationIndividual globalBestIndividual = population.getGlobalBestIndividual();
                algorithmTask.setHospitals(globalBestIndividual.getIndividualNodes());
                algorithmTask.setFitnessScore(globalBestIndividual.getFitnessScore());
            }

            epochNumber++;
            if (wasUpdated) {
                printMessage("Epoch: " + epochNumber
                        + " | Score: " + population.getGlobalBestIndividual().getFitnessScore().toString());
            }
        }

        var globalBestIndividual = population.getGlobalBestIndividual();
        printMessage("GA Best state value: " + globalBestIndividual.getFitnessScore()
                + " after " + epochNumber + " epochs");
        algorithmTask.setHospitals(globalBestIndividual.getIndividualNodes());
        algorithmTask.setFitnessScore(globalBestIndividual.getFitnessScore());
        coloursService.updateColoursInNodes(algorithmTask, shortestPathsDistances);
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
    }
}
