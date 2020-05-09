package com.agh.bsct.algorithm.algorithms.AAlgorithm;

import com.agh.bsct.algorithm.services.graph.GraphNode;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CrossoverService {

    private final int crossoverPoint;

    public CrossoverService(int crossoverPoint) {
        this.crossoverPoint = crossoverPoint;
    }

    public ArrayList<PopulationIndividual> getCrossoveredPopulationIndividuals(
            ArrayList<PopulationIndividual> parentPopulationIndividuals) {
        var crossoveredPopulationIndividuals = new ArrayList<PopulationIndividual>();

        for (int i = 0; i < parentPopulationIndividuals.size(); i += 2) {
            var firstParent = parentPopulationIndividuals.get(i);
            var secondParent = parentPopulationIndividuals.get(i + 1);

            var crossoveredChildren = getCrossoveredChildren(firstParent, secondParent);

            crossoveredPopulationIndividuals.add(crossoveredChildren.getFirstChild());
            crossoveredPopulationIndividuals.add(crossoveredChildren.getSecondChild());
        }

        return crossoveredPopulationIndividuals;
    }

    private CrossoveredChildren getCrossoveredChildren(PopulationIndividual firstParent,
                                                       PopulationIndividual secondParent) {
        var firstParentChromosome = firstParent.getIndividualNodes();
        var secondParentChromosome = secondParent.getIndividualNodes();

        var firstChild = getChild(firstParentChromosome, secondParentChromosome);
        var secondChild = getChild(secondParentChromosome, firstParentChromosome);

        return new CrossoveredChildren(firstChild, secondChild);
    }

    private PopulationIndividual getChild(ArrayList<GraphNode> chromosomeA, ArrayList<GraphNode> chromosomeB) {
        var firstChromosomePart = getFirstChromosomePart(chromosomeB);
        var secondChromosomePart = getSecondChromosomePart(chromosomeA);

        return new PopulationIndividual(getChromosome(firstChromosomePart, secondChromosomePart));
    }

    private ArrayList<GraphNode> getFirstChromosomePart(ArrayList<GraphNode> firstParentChromosome) {
        return firstParentChromosome.stream()
                .limit(crossoverPoint)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<GraphNode> getSecondChromosomePart(ArrayList<GraphNode> firstParentChromosome) {
        return firstParentChromosome.stream()
                .skip(crossoverPoint)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ArrayList<GraphNode> getChromosome(ArrayList<GraphNode> firstChromosomePart,
                                               ArrayList<GraphNode> secondChromosomePart) {
        var chromosome = new ArrayList<GraphNode>();
        chromosome.addAll(firstChromosomePart);
        chromosome.addAll(secondChromosomePart);
        return chromosome;
    }
}
