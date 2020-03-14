package com.agh.bsct.algorithm.services.graph;

import java.util.Objects;

public class GraphEdge {

    /*
     * In GraphEdge there's no start node,
     * because the class is used only as an element of nodeToEdgedIncidenceMap in Graph class.
     * Start node is the key of the map.
     */

    private GraphNode endGraphNode;
    private double weight;

    public GraphEdge(GraphNode endGraphNode, double weight) {
        this.endGraphNode = endGraphNode;
        this.weight = weight;
    }

    public GraphNode getEndGraphNode() {
        return endGraphNode;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphEdge graphEdge = (GraphEdge) o;
        return Double.compare(graphEdge.weight, weight) == 0 &&
                Objects.equals(endGraphNode, graphEdge.endGraphNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endGraphNode, weight);
    }
}
