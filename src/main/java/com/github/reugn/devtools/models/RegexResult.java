package com.github.reugn.devtools.models;

import javafx.util.Pair;

import java.util.List;

public class RegexResult {

    private final String matchSummary;
    private final List<Pair<Integer, Integer>> fullMatchIndexes;

    public RegexResult(String matchSummary, List<Pair<Integer, Integer>> fullMatchIndexes) {
        this.matchSummary = matchSummary;
        this.fullMatchIndexes = fullMatchIndexes;
    }

    public String getMatchSummary() {
        return matchSummary;
    }

    public List<Pair<Integer, Integer>> getFullMatchIndexes() {
        return fullMatchIndexes;
    }
}
