package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.paint.Color;

public class ImageEditorEraserHandler extends ImageEditorBrushHandler {

    @Override
    protected Color getColor(ImageEditorController c) {
        return Color.WHITE;
    }
}
