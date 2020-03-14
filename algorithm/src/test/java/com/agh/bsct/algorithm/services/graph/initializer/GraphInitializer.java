package com.agh.bsct.algorithm.services.graph.initializer;

import com.agh.bsct.algorithm.services.graph.Graph;
import com.agh.bsct.algorithm.services.graph.GraphEdge;
import com.agh.bsct.algorithm.services.graph.GraphNode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphInitializer {

    public static final String SRC_TEST_RESOURCES_PATH = "src/test/resources/";

    private Map<String, Graph> filenameToGraph = new HashMap<>();

    public Graph initGraph(String filename) {
        if (!filenameToGraph.containsKey(filename)) {
            var graph = loadGraphFromFile(filename);
            filenameToGraph.put(filename, graph);
        }

        return filenameToGraph.get(filename);
    }

    private Graph loadGraphFromFile(String filename) {
        Map<GraphNode, List<GraphEdge>> nodeToEdgesIncidenceMap = new HashMap<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            String linesForOneMapElement = null;
            while (line != null) {

                if (line.equals("") || line.matches("\\s+")) {
                    String[] stringIds = linesForOneMapElement.split("\\s+");

                    GraphNode graphNode = null;
                    List<GraphEdge> graphEdges = new ArrayList<>();

                    for (String stringId : stringIds) {
                        Long id = Long.valueOf(stringId);

                        if (graphNode == null) {
                            graphNode = new GraphNode(id, 1);
                        } else {
                            graphEdges.add(new GraphEdge(new GraphNode(id, 1), 1));
                        }
                    }

                    linesForOneMapElement = null;

                    nodeToEdgesIncidenceMap.put(graphNode, graphEdges);

                } else {
                    linesForOneMapElement = (linesForOneMapElement == null) ? line : (linesForOneMapElement + " " + line);
                }

                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Graph(nodeToEdgesIncidenceMap);
    }

}
