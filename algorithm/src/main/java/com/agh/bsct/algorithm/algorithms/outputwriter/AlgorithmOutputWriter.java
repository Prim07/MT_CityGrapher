package com.agh.bsct.algorithm.algorithms.outputwriter;

public interface AlgorithmOutputWriter {

    String OUTPUT_FILES_BASE_DIRECTORY = "algorithm/src/main/resources/written-values/";

    void initializeResources(String algorithmTaskId);

    void writeLineIfEnabled(String key, String... values);

    void closeResources();

}
