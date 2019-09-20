package com.github.reugn.devtools.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonSearchState {
    private String searchText;
    private String target;
    private List<SearchSpan> found;
    private int current;
    private int total;

    public JsonSearchState(String text, String target) {
        this.searchText = text;
        this.target = target;
        this.found = new ArrayList<>();
        this.current = 0;
        this.total = 0;
        setAll();
    }

    private void setAll() {
        if (searchText.isEmpty()) return;
        Matcher m = Pattern.compile(searchText).matcher(target);
        while (m.find()) {
            found.add(new SearchSpan(m.start(), m.start() + searchText.length()));
            total++;
        }
    }

    public Optional<SearchSpan> next() {
        if (total == 0) return Optional.empty();
        current = current + 1 > total ? 1 : current + 1;
        return Optional.of(found.get(current - 1));
    }

    public Optional<SearchSpan> prev() {
        if (total == 0) return Optional.empty();
        current = current - 1 > 0 ? current - 1 % total : total;
        return Optional.of(found.get(current - 1));
    }

    public boolean isValid(String text, String target) {
        return searchText.equals(text) && this.target.equals(target);
    }

    @Override
    public String toString() {
        return current + " from " + total;
    }

    public static class SearchSpan {
        private int from;
        private int to;

        SearchSpan(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }
    }
}
