package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.agh.bsct.algorithm.algorithms.AAlgorithm.GAAlgorithm.GENETIC_QUALIFIER;
import static com.agh.bsct.algorithm.algorithms.BFAlgorithm.BRUTE_FORCE_QUALIFIER;
import static com.agh.bsct.algorithm.algorithms.SAAlgorithm.SIMULATED_ANNEALING_QUALIFIER;

@Component
public class AlgorithmRunner {

    private static final String SA_ALGORITHM_TYPE = "sa";
    private static final String GA_ALGORITHM_TYPE = "ga";
    private static final String BF_ALGORITHM_TYPE = "bf";

    private final Map<String, IAlgorithm> typeToImplementation = new HashMap<>();

    @Autowired
    public AlgorithmRunner(@Qualifier(SIMULATED_ANNEALING_QUALIFIER) IAlgorithm saAlgorithm,
                           @Qualifier(GENETIC_QUALIFIER) IAlgorithm aAlgorithm,
                           @Qualifier(BRUTE_FORCE_QUALIFIER) IAlgorithm bfAlgorithm) {
        typeToImplementation.put(SA_ALGORITHM_TYPE, saAlgorithm);
        typeToImplementation.put(GA_ALGORITHM_TYPE, aAlgorithm);
        typeToImplementation.put(BF_ALGORITHM_TYPE, bfAlgorithm);
    }

    public void run(AlgorithmTask algorithmTask) {
        String algorithmType = algorithmTask.getAlgorithmType();
        validateAlgorithmType(algorithmType);
        typeToImplementation.get(algorithmType).run(algorithmTask);
    }

    private void validateAlgorithmType(String algorithmType) {
        if (!typeToImplementation.containsKey(algorithmType)) {
            throw new IllegalStateException("Cannot find IAlgorithm implementation for type: " + algorithmType);
        }
    }
}
