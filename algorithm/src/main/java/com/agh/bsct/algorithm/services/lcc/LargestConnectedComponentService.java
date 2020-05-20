package com.agh.bsct.algorithm.services.lcc;

import com.agh.bsct.algorithm.controllers.mapper.GraphDataMapper;
import com.agh.bsct.algorithm.services.graph.GraphService;
import com.agh.bsct.api.models.graphdata.GraphDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LargestConnectedComponentService {

    private final GraphService graphService;
    private final GraphDataMapper graphDataMapper;

    @Autowired
    public LargestConnectedComponentService(GraphService graphService, GraphDataMapper graphDataMapper) {
        this.graphService = graphService;
        this.graphDataMapper = graphDataMapper;
    }

    public void replaceWithLargestConnectedComponent(GraphDataDTO graphDataDTO) {
        var graph = graphDataMapper.mapToGraph(graphDataDTO);
        graphService.replaceGraphWithItsLargestConnectedComponent(graph, graphDataDTO);
    }
}
