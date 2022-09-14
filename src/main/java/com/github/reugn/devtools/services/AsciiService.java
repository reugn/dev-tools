package com.github.reugn.devtools.services;

public interface AsciiService {

    String convert(String text, String asciiChar, int textHeight, int fontStyle, String fontName);

    int getStyleByName(String styleName);
}
