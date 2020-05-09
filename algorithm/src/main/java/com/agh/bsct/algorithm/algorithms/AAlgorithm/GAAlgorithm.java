package com.agh.bsct.algorithm.algorithms.AAlgorithm;

import com.agh.bsct.algorithm.algorithms.IAlgorithm;
import com.agh.bsct.algorithm.services.algorithms.LatestChangesService;
import com.agh.bsct.algorithm.services.colours.ColoursService;
import com.agh.bsct.algorithm.services.graph.GraphService;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmCalculationStatus;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.agh.bsct.algorithm.algorithms.dummylogger.DummyLogger.printMessage;

@Component
@Qualifier(GAAlgorithm.GENETIC_QUALIFIER)
public class GAAlgorithm implements IAlgorithm {

    public static final String GENETIC_QUALIFIER = "geneticAlgorithm";

    private static final int QUEUE_SIZE = 50;

    private final GraphService graphService;
    private final ColoursService coloursService;
    private final LatestChangesService latestChangesService;

    @Autowired
    public GAAlgorithm(GraphService graphService, ColoursService coloursService) {
        this.graphService = graphService;
        this.coloursService = coloursService;
        this.latestChangesService = new LatestChangesService(QUEUE_SIZE);
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
                algorithmTask.setHospitals(population.getGlobalBestIndividual().getIndividualNodes());
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
        coloursService.updateColoursInNodes(algorithmTask, shortestPathsDistances);
        algorithmTask.setStatus(AlgorithmCalculationStatus.SUCCESS);
    }
}
