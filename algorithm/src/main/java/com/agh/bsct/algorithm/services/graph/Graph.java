package com.agh.bsct.algorithm.services.graph;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Graph {

    private Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap;

    public Graph(Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        this.nodeToEdgesIncidenceMap = nodeToEdgesIncidenceMap;
    }

    public Map<GraphNode, List<GraphEdge>> getIncidenceMap() {
        return nodeToEdgesIncidenceMap;
    }

    public void setNodeToEdgesIncidenceMap(Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap) {
        this.nodeToEdgesIncidenceMap = nodeToEdgesIncidenceMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return Objects.equals(nodeToEdgesIncidenceMap, graph.nodeToEdgesIncidenceMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeToEdgesIncidenceMap);
    }
}
