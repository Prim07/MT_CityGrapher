package com.agh.bsct.algorithm.services.algorithms;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public class LatestChangesService {

    private final int queueSize;
    private final CircularFifoQueue<Boolean> latestChanges;

    public LatestChangesService(Integer queueSize) {
        this.queueSize = queueSize;
        this.latestChanges = getLatestChanges();
    }

    public void add(boolean change) {
        latestChanges.add(change);
    }

    public boolean shouldIterate() {
        for (var i = 0; i < queueSize; i++) {
            if (latestChanges.get(i).equals(Boolean.TRUE)) {
                return true;
            }
        }

        return false;
    }

    private CircularFifoQueue<Boolean> getLatestChanges() {
        var lastChanges = new CircularFifoQueue<Boolean>(queueSize);

        for (var i = 0; i < queueSize; i++) {
            lastChanges.add(Boolean.TRUE);
        }

        return lastChanges;
    }
}
