package com.agh.bsct.algorithm.services.graph;

import com.agh.bsct.api.models.graphdata.NodeColour;

import java.util.Objects;

public class GraphNode {

    private long id;
    private int weight;
    private NodeColour nodeColour;

    public GraphNode(Long id, Integer weight) {
        new GraphNode(id, weight, NodeColour.defaultColour());
    }

    public GraphNode(Long id, Integer weight, NodeColour nodeColour) {
        this.id = id;
        this.weight = weight;
        this.nodeColour = nodeColour;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return id == graphNode.id &&
                weight == graphNode.weight &&
                Objects.equals(nodeColour, graphNode.nodeColour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight, nodeColour);
    }
}
