package com.agh.bsct.algorithm.algorithms.AAlgorithm;

import com.agh.bsct.algorithm.services.graph.GraphNode;

import java.util.Objects;
import java.util.Set;

public class PopulationIndividual {

    private final Set<GraphNode> populationIndividualNodes;

    public PopulationIndividual(Set<GraphNode> populationIndividualNodes) {
        this.populationIndividualNodes = populationIndividualNodes;
    }

    public static void calculateFitnessScore(PopulationIndividual populationIndividual) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopulationIndividual that = (PopulationIndividual) o;
        return Objects.equals(populationIndividualNodes, that.populationIndividualNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(populationIndividualNodes);
    }
}
