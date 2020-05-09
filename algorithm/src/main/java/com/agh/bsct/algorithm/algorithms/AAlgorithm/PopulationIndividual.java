package com.agh.bsct.algorithm.algorithms.AAlgorithm;

import com.agh.bsct.algorithm.services.graph.GraphNode;

import java.util.ArrayList;
import java.util.Objects;

public class PopulationIndividual {

    private final ArrayList<GraphNode> individualNodes;
    private Double fitnessScore = Double.MAX_VALUE;

    public PopulationIndividual(ArrayList<GraphNode> individualNodes) {
        this.individualNodes = individualNodes;
    }

    public static PopulationIndividual getCopyOf(PopulationIndividual givenIndividual) {
        var copy = new PopulationIndividual(givenIndividual.getIndividualNodes());
        copy.setFitnessScore(givenIndividual.getFitnessScore());
        return copy;
    }

    public Double getFitnessScore() {
        return fitnessScore;
    }

    public void setFitnessScore(Double fitnessScore) {
        this.fitnessScore = fitnessScore;
    }

    public ArrayList<GraphNode> getIndividualNodes() {
        return individualNodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopulationIndividual that = (PopulationIndividual) o;
        return Objects.equals(individualNodes, that.individualNodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(individualNodes);
    }
}
