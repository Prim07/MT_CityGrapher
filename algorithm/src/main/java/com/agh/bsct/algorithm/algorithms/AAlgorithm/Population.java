package com.agh.bsct.algorithm.algorithms.AAlgorithm;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.math3.util.CombinatoricsUtils.binomialCoefficient;

@Service
public class Population {

    private static final int DEFAULT_POPULATION_SIZE = 10000;
    private final Set<PopulationIndividual> populationIndividuals = new HashSet<>();

    private final Random random;

    @Autowired
    public Population() {
        this.random = new Random();
    }

    public void initializePopulation(AlgorithmTask algorithmTask) {
        List<GraphNode> allGraphNodes = new ArrayList<>(algorithmTask.getGraph().getIncidenceMap().keySet());
        int numberOfAllGraphNodes = allGraphNodes.size();
        Integer numberOfResults = algorithmTask.getNumberOfResults();

        for (int i = 0; i < getPopulationSize(numberOfAllGraphNodes, numberOfResults); ++i) {
            Set<GraphNode> populationMemberNodes = new HashSet<>();
            for (int k = 0; k < numberOfResults; ++k) {
                int i1 = random.nextInt(numberOfAllGraphNodes);
                GraphNode graphNodeMemberCandidate = allGraphNodes.get(i1);
                if (!populationMemberNodes.contains(graphNodeMemberCandidate)) {
                    populationMemberNodes.add(graphNodeMemberCandidate);
                } else {
                    k--;
                }
            }
            PopulationIndividual newPopulationIndividual = new PopulationIndividual(populationMemberNodes);

            if (!populationIndividuals.contains(newPopulationIndividual)) {
                populationIndividuals.add(newPopulationIndividual);
            } else {
                i--;
            }
        }
    }

    private int getPopulationSize(int numberOfAllGraphNodes, int numberOfResults) {
        int numberOfPossibleCombinations = calculateNewtonValue(numberOfAllGraphNodes, numberOfResults);
        return Math.min(numberOfPossibleCombinations, DEFAULT_POPULATION_SIZE);
    }

    private int calculateNewtonValue(int n, int k) {
        return Math.toIntExact(binomialCoefficient(n, k));
    }

    public void calculateEachIndividualFitnessScore() {
        populationIndividuals.forEach(PopulationIndividual::calculateFitnessScore);
    }
}
