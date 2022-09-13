package com.github.reugn.devtools.utils;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Elements {

    public static final Border alertBorder = new Border(new BorderStroke(Color.rgb(0x80, 0x00, 0x00),
            BorderStrokeStyle.SOLID, new CornerRadii(3), BorderWidths.DEFAULT));

    private Elements() {
    }
}
