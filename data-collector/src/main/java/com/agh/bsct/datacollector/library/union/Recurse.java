package com.agh.bsct.datacollector.library.union;

public enum Recurse {
    UP('<'),
    DOWN('>');

    private char recurse;

    Recurse(char recurse) {
        this.recurse = recurse;
    }

    @Override
    public String toString() {
        return String.valueOf(this.recurse);
    }
}
