package com.agh.bsct.algorithm.algorithms.AAlgorithm;

class CrossoveredChildren {

    private final PopulationIndividual firstChild;
    private final PopulationIndividual secondChild;

    CrossoveredChildren(PopulationIndividual firstChild, PopulationIndividual secondChild) {
        this.firstChild = firstChild;
        this.secondChild = secondChild;
    }

    PopulationIndividual getFirstChild() {
        return firstChild;
    }

    PopulationIndividual getSecondChild() {
        return secondChild;
    }
}
