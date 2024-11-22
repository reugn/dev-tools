package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.input.MouseEvent;

public class ImageEditorRectangleHandler extends ImageEditorGeometryHandler {

    @Override
    public void mouseDragged(ImageEditorController c, MouseEvent mouseEvent) {
        c.getGc().drawImage(canvasSnapshot, 0, 0);
        PixelPair p = PixelPair.from(c.getStartX(), c.getStartY(), mouseEvent.getX(), mouseEvent.getY());
        if (fill) {
            c.getGc().fillRect(p.x1, p.y1, p.x2, p.y2);
        } else {
            c.getGc().strokeRect(p.x1, p.y1, p.x2, p.y2);
        }
    }
}
