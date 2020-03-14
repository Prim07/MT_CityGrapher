package com.agh.bsct.algorithm.algorithms;

import com.agh.bsct.algorithm.services.runner.algorithmtask.AlgorithmTask;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(AntAlgorithm.ANT_QUALIFIER)
public class AntAlgorithm implements IAlgorithm {

    static final String ANT_QUALIFIER = "antAlgorithm";

    @Override
    public void run(AlgorithmTask algorithmTask) {
        throw new IllegalStateException("not implemented");
    }
}
