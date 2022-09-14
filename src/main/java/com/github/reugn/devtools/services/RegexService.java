package com.github.reugn.devtools.services;

import com.github.reugn.devtools.models.RegexResult;

import java.util.List;

public interface RegexService {

    RegexResult match(String regex, String target, List<String> flagList);
}
