package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.input.MouseEvent;

public class ImageEditorLineHandler extends ImageEditorGeometryHandler {

    @Override
    public void mouseDragged(ImageEditorController c, MouseEvent mouseEvent) {
        c.getGc().drawImage(canvasSnapshot, 0, 0);
        c.getGc().strokeLine(c.getStartX(), c.getStartY(), mouseEvent.getX(), mouseEvent.getY());
    }
}
