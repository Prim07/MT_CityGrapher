package com.agh.bsct.algorithm.services.graph;

import java.util.Objects;

public class GraphNode {

    private long id;
    private int weight;

    public GraphNode(Long id, Integer weight) {
        this.id = id;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return id == graphNode.id &&
                weight == graphNode.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weight);
    }
}
