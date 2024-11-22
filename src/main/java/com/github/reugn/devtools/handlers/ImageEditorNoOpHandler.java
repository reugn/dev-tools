package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.input.MouseEvent;

public class ImageEditorNoOpHandler implements ImageEditorMouseEventHandler {

    @Override
    public void mousePressed(ImageEditorController c, MouseEvent mouseEvent) {
        // no-op
    }

    @Override
    public void mouseReleased(ImageEditorController c, MouseEvent mouseEvent) {
        // no-op
    }

    @Override
    public void mouseDragged(ImageEditorController c, MouseEvent mouseEvent) {
        // no-op
    }
}
