package com.agh.bsct.algorithm.services.colours;

import com.agh.bsct.algorithm.services.graph.GraphNode;
import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.stereotype.Service;

@Service
public class ColoursService {

    public void updateColoursInNodes(AlgorithmTask algorithmTask) {
        algorithmTask.getGraph().getIncidenceMap().keySet().stream()
                .map(GraphNode::getNodeColour)
                .forEach(nodeColour -> {
                    nodeColour.setR(0);
                    nodeColour.setG(0);
                    nodeColour.setB(0);
                });
    }
}
