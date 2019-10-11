package com.github.reugn.devtools.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexService {

    private RegexService() {
    }

    public static String match(String regex, String target, List<String> flagList) {
        Pattern pattern = Pattern.compile(regex, calculateFlags(flagList));
        Matcher matcher = pattern.matcher(target);
        StringBuilder builder = new StringBuilder();
        boolean isGlobal = flagList.contains("global");
        while (matcher.find()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                builder.append(i)
                        .append(": ")
                        .append(target, matcher.start(i), matcher.end(i))
                        .append("\n");
            }
            builder.append("\n");
            if (!isGlobal) break;
        }
        return builder.toString();
    }

    private static int calculateFlags(List<String> flagList) {
        int flags = 0;
        if (flagList.contains("multiline")) flags |= Pattern.MULTILINE;
        if (flagList.contains("insensitive")) flags |= Pattern.CASE_INSENSITIVE;
        if (flagList.contains("unicode")) flags |= Pattern.UNICODE_CASE;
        return flags;
    }
}
