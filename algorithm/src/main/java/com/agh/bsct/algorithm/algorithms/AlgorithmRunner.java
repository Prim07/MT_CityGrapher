package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.agh.bsct.algorithm.algorithms.BFAlgorithm.BRUTE_FORCE_QUALIFIER;
import static com.agh.bsct.algorithm.algorithms.SAAlgorithm.SIMULATED_ANNEALING_QUALIFIER;

@Component
public class AlgorithmRunner {

    private static final String SA_ALGORITHM_SYMBOL = "sa";
    private static final String BF_ALGORITHM_SYMBOL = "bf";

    private final IAlgorithm saAlgorithm;
    private final IAlgorithm bfAlgorithm;

    @Autowired
    public AlgorithmRunner(@Qualifier(SIMULATED_ANNEALING_QUALIFIER) IAlgorithm saAlgorithm,
                           @Qualifier(BRUTE_FORCE_QUALIFIER) IAlgorithm bfAlgorithm) {
        this.saAlgorithm = saAlgorithm;
        this.bfAlgorithm = bfAlgorithm;
    }

    public void run(AlgorithmTask algorithmTask) {
        String algorithmType = algorithmTask.getAlgorithmType();
        if (SA_ALGORITHM_SYMBOL.equals(algorithmType)) {
            saAlgorithm.run(algorithmTask);
        } else if (BF_ALGORITHM_SYMBOL.equals(algorithmType)) {
            bfAlgorithm.run(algorithmTask);
        } else {
            throw new IllegalStateException("Cannot find IAlgorithm implementation for type: " + algorithmType);
        }
    }

}
