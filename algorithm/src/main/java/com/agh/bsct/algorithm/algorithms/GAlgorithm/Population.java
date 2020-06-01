package com.agh.bsct.algorithm.algorithms.GAlgorithm;

import com.agh.bsct.algorithm.services.algorithms.AlgorithmFunctionsService;
import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.graph.ShortestPathsDistances;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;

public class Population {

    private static final int DEFAULT_POPULATION_SIZE = 100;
    private static final int PERCENTAGE_OF_POPULATION_TO_BE_CHOSEN_AS_PARENTS = 50;
    private static final int PARENTS_POPULATION_SIZE = calculateParentsPopulationSize();
    private static final double FITNESS_SCORE_ROUNDING_PRECISION = 0.001;
    private final Random random;
    private final AlgorithmFunctionsService algorithmFunctionsService;
    private final CrossoverService crossoverService;
    private final MutationService mutationService;
    private final ArrayList<PopulationIndividual> parentPopulationIndividuals
            = new ArrayList<>(PARENTS_POPULATION_SIZE);
    private final ArrayList<PopulationIndividual> childrenPopulationIndividuals =
            new ArrayList<>(PARENTS_POPULATION_SIZE);
    private final int numberOfResults;
    private final ShortestPathsDistances shortestPathsDistances;
    private final ArrayList<GraphNode> allGraphNodes;
    private LinkedHashSet<PopulationIndividual> populationIndividuals = new LinkedHashSet<>();
    private PopulationIndividual globalBestIndividual;

    public Population(AlgorithmTask algorithmTask, ShortestPathsDistances shortestPathsDistances) {
        this.shortestPathsDistances = shortestPathsDistances;
        this.numberOfResults = algorithmTask.getNumberOfResults();
        this.allGraphNodes = new ArrayList<>(algorithmTask.getGraph().getIncidenceMap().keySet());
        this.random = new Random();
        this.algorithmFunctionsService = new AlgorithmFunctionsService();
        this.crossoverService = new CrossoverService(algorithmTask.getNumberOfResults() / 2);
        this.mutationService = new MutationService(allGraphNodes);
    }

    @SuppressWarnings("ConstantConditions")
    private static int calculateParentsPopulationSize() {
        return (DEFAULT_POPULATION_SIZE % 2 == 0)
                ? DEFAULT_POPULATION_SIZE * PERCENTAGE_OF_POPULATION_TO_BE_CHOSEN_AS_PARENTS / 100
                : (DEFAULT_POPULATION_SIZE + 1) * PERCENTAGE_OF_POPULATION_TO_BE_CHOSEN_AS_PARENTS / 100;
    }

    public PopulationIndividual getGlobalBestIndividual() {
        return globalBestIndividual;
    }

    private void setGlobalBestIndividual(PopulationIndividual newBestIndividual) {
        globalBestIndividual = PopulationIndividual.getCopyOf(newBestIndividual);
    }

    public void initializePopulation() {
        setRandomIndividualsInPopulation(0, getPopulationSize(allGraphNodes.size(), numberOfResults));
        setGlobalBestIndividual(populationIndividuals.stream().findFirst().orElseThrow());
    }

    public void setRandomIndividualsInPopulation(int fromIndex, int toIndex) {
        int numberOfAllGraphNodes = allGraphNodes.size();

        for (int i = fromIndex; i < toIndex; ++i) {
            var individualNodes = new LinkedHashSet<GraphNode>();
            for (int k = 0; k < numberOfResults; ++k) {
                var graphNodeMemberCandidate = allGraphNodes.get(random.nextInt(numberOfAllGraphNodes));
                if (!individualNodes.contains(graphNodeMemberCandidate)) {
                    individualNodes.add(graphNodeMemberCandidate);
                } else {
                    k--;
                }
            }

            var populationIndividual = new PopulationIndividual(newArrayList(individualNodes));

            if (!populationIndividuals.contains(populationIndividual)) {
                populationIndividuals.add(populationIndividual);
            } else {
                i--;
            }
        }
    }

    public void calculateEachIndividualFitnessScore() {
        populationIndividuals.forEach(individual ->
                individual.setFitnessScore(algorithmFunctionsService.calculateFunctionValue(
                        shortestPathsDistances, individual.getIndividualNodes())));
    }

    public void sortByFitnessScore() {
        populationIndividuals = populationIndividuals.stream()
                .sorted(Comparator.comparingDouble(PopulationIndividual::getFitnessScore))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public boolean updateBestState() {
        PopulationIndividual currentBestIndividual = populationIndividuals.stream().findFirst().orElseThrow();

        if (isStateBetter(currentBestIndividual)) {
            setGlobalBestIndividual(currentBestIndividual);
            return true;
        }

        return false;
    }

    private boolean isStateBetter(PopulationIndividual currentBestIndividual) {
        var candidateFitnessScore = currentBestIndividual.getFitnessScore();
        var bestFitnessScore = globalBestIndividual.getFitnessScore();

        return Math.abs(candidateFitnessScore - bestFitnessScore) > FITNESS_SCORE_ROUNDING_PRECISION
                && candidateFitnessScore < bestFitnessScore;
    }

    public void chooseParentsPopulation() {
        parentPopulationIndividuals.clear();
        parentPopulationIndividuals.addAll(populationIndividuals.stream()
                .limit(PARENTS_POPULATION_SIZE)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    public void crossoverParents() {
        childrenPopulationIndividuals.clear();
        childrenPopulationIndividuals.addAll(
                crossoverService.getCrossoveredPopulationIndividuals(parentPopulationIndividuals));
    }

    public void mutateParents() {
        mutationService.mutate(parentPopulationIndividuals);
    }

    public void updatePopulation() {
        populationIndividuals.clear();
        populationIndividuals.addAll(childrenPopulationIndividuals);
        setRandomIndividualsInPopulation(populationIndividuals.size(), DEFAULT_POPULATION_SIZE);
    }

    private int getPopulationSize(int numberOfAllGraphNodes, int numberOfResults) {
        int numberOfPossibleCombinations = calculateNewtonValue(numberOfAllGraphNodes, numberOfResults);
        return Math.min(numberOfPossibleCombinations, DEFAULT_POPULATION_SIZE);
    }

    private int calculateNewtonValue(int n, int k) {
        try {
            return Math.toIntExact(binomialCoefficient(n, k));
        } catch (ArithmeticException e) {
            return Integer.MAX_VALUE;
        }
    }
}
