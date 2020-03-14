package com.agh.bsct.algorithm.services.graph;

import com.agh.bsct.algorithm.services.graph.initializer.GraphInitializer;
import com.agh.bsct.algorithm.services.graphdata.GraphDataService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.agh.bsct.algorithm.services.graph.initializer.GraphInitializer.SRC_TEST_RESOURCES_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FloydWarshallAlgorithmTest {

    private final GraphInitializer graphInitializer = new GraphInitializer();
    private final GraphService graphService = new GraphService(new GraphDataService());

    @Test
    void shouldBeTheSameWhenMilocinIsCalculatedMultipleTimes() {
        final String filename = SRC_TEST_RESOURCES_PATH + "milocin.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename, 10);
    }

    @Test
    void shouldBeTheSameWhenLancutIsCalculatedMultipleTimes() {
        final String filename = SRC_TEST_RESOURCES_PATH + "lancut.txt";

        shouldBeTheSameWhenCalculatedMultipleTimes(filename, 3);
    }

    private void shouldBeTheSameWhenCalculatedMultipleTimes(String filename, int loopCount) {
        var graph = graphInitializer.initGraph(filename);
        graphService.replaceGraphWithItsLargestConnectedComponent(graph);

        var shortestPathsDistancesToCompare = graphService.calculateShortestPathsDistances(graph);

        for (int i = 0; i < loopCount; i++) {
            var shortestPathsDistances = graphService.calculateShortestPathsDistances(graph);

            for (Map<Long, Double> currentNodeShortestPathsDistance : shortestPathsDistances.values()) {
                for (Double distance : currentNodeShortestPathsDistance.values()) {
                    assertNotEquals(Double.MAX_VALUE, distance);
                }
            }

            assertEquals(shortestPathsDistancesToCompare.size(), shortestPathsDistances.size());
            assertEquals(shortestPathsDistancesToCompare, shortestPathsDistances);
        }
    }
}
