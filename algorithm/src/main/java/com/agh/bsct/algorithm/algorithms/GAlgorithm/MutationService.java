package com.agh.bsct.algorithm.algorithms.GAlgorithm;

import com.agh.bsct.algorithm.services.graph.GraphNode;

import java.util.ArrayList;
import java.util.Random;

public class MutationService {

    private static final int PERCENTAGE_PROBABILITY_OF_MUTATION = 10;

    private final ArrayList<GraphNode> allGraphNodes;
    private final Random random;

    public MutationService(ArrayList<GraphNode> allGraphNodes) {
        this.allGraphNodes = allGraphNodes;
        this.random = new Random();
    }


    public void mutate(ArrayList<PopulationIndividual> populationIndividuals) {
        populationIndividuals.stream()
                .filter(populationIndividual -> shouldMutate())
                .forEach(this::mutate);
    }

    private boolean shouldMutate() {
        return random.nextInt(100) < PERCENTAGE_PROBABILITY_OF_MUTATION;
    }

    private void mutate(PopulationIndividual populationIndividual) {
        var individualNodes = populationIndividual.getIndividualNodes();
        individualNodes.remove(random.nextInt(individualNodes.size()));
        individualNodes.add(allGraphNodes.get(random.nextInt(allGraphNodes.size())));
    }
}
