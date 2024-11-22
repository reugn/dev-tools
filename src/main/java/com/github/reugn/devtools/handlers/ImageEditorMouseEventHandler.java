package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.input.MouseEvent;

public interface ImageEditorMouseEventHandler {

    default void mouseMoved(ImageEditorController c, MouseEvent mouseEvent) {
        c.updateCoordinates(mouseEvent);
    }

    void mousePressed(ImageEditorController c, MouseEvent mouseEvent);

    void mouseReleased(ImageEditorController c, MouseEvent mouseEvent);

    void mouseDragged(ImageEditorController c, MouseEvent mouseEvent);

    @SuppressWarnings("unused")
    default void mouseExited(ImageEditorController c, MouseEvent mouseEvent) {
        c.coordinatesLabel.setText("");
    }
}
