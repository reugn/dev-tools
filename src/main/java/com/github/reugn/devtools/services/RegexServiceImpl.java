package com.github.reugn.devtools.services;

import com.github.reugn.devtools.models.RegexResult;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexServiceImpl implements RegexService {

    @Override
    public RegexResult match(String regex, String target, List<String> flagList) {
        Pattern pattern = Pattern.compile(regex, calculateFlags(flagList));
        Matcher matcher = pattern.matcher(target);

        boolean isGlobal = flagList.contains("global");
        List<Pair<Integer, Integer>> fullMatch = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                builder.append(i)
                        .append(": ")
                        .append(target, matcher.start(i), matcher.end(i))
                        .append("\n");
            }
            builder.append("\n");
            fullMatch.add(new Pair<>(matcher.start(0), matcher.end(0)));
            if (!isGlobal) break;
        }
        return new RegexResult(builder.toString(), fullMatch);
    }

    private int calculateFlags(List<String> flagList) {
        int flags = 0;
        if (flagList.contains("multiline")) flags |= Pattern.MULTILINE;
        if (flagList.contains("insensitive")) flags |= Pattern.CASE_INSENSITIVE;
        if (flagList.contains("unicode")) flags |= Pattern.UNICODE_CASE;
        return flags;
    }
}
