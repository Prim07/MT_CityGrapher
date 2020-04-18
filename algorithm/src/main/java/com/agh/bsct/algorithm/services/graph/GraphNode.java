package com.agh.bsct.algorithm.services.graph;

import com.agh.bsct.api.models.citydata.GeographicalNodeDTO;
import com.agh.bsct.api.models.graphdata.NodeColour;

import java.util.Objects;

public class GraphNode {

    private long id;
    private int weight;
    private GeographicalNodeDTO geographicalNodeDTO;
    private NodeColour nodeColour;


    public GraphNode(Long id, Integer weight, GeographicalNodeDTO geographicalNodeDTO) {
        this.id = id;
        this.weight = weight;
        this.geographicalNodeDTO = geographicalNodeDTO;
        this.nodeColour = NodeColour.createDefaultColour();
    }

    public GraphNode(Long id, Integer weight) {
        this.id = id;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public NodeColour getNodeColour() {
        return nodeColour;
    }

    public GeographicalNodeDTO getGeographicalNodeDTO() {
        return geographicalNodeDTO;
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
