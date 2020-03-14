package com.agh.bsct.algorithm.services.graph;

import com.agh.bsct.algorithm.services.graph.initializer.GraphInitializer;
import com.agh.bsct.algorithm.services.graphdata.GraphDataService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.agh.bsct.algorithm.services.graph.initializer.GraphInitializer.SRC_TEST_RESOURCES_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LargestConnectedComponentTest {

    private final GraphInitializer graphInitializer = new GraphInitializer();
    private final GraphService graphService = new GraphService(new GraphDataService());

    @Test
    void shouldBeTheSameWhenMilocinIsCalculatedMultipleTimes() {
        final String filename = SRC_TEST_RESOURCES_PATH + "milocin.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    @Test
    void shouldBeTheSameWhenLancutIsCalculatedMultipleTimes() {
        final String filename = SRC_TEST_RESOURCES_PATH + "lancut.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    @Test
    void shouldBeTheSameWhenTarnowIsCalculatedMultipleTimes() {
        final String filename = SRC_TEST_RESOURCES_PATH + "tarnow.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename);
    }

    private void shouldBeTheSameWhenCalculatedMultipleTimes(String filename) {
        var graph = graphInitializer.initGraph(filename);

        Map<GraphNode, List<GraphEdge>> incidenceMap = graph.getIncidenceMap();
        var largestConnectedComponentToCompare = graphService.findLargestConnectedComponent(incidenceMap);

        int loopCount = 100;
        for (int i = 0; i < loopCount; i++) {
            var largestConnectedComponent = graphService.findLargestConnectedComponent(incidenceMap);

            assertEquals(largestConnectedComponentToCompare.size(), largestConnectedComponent.size());
            assertEquals(largestConnectedComponentToCompare, largestConnectedComponent);
        }
    }
}
