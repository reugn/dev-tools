package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ImageEditorEyedropperHandler implements ImageEditorMouseEventHandler {

    @Override
    public void mousePressed(ImageEditorController c, MouseEvent mouseEvent) {
        PixelReader pixelReader = c.getCurrentSnapshot().getPixelReader();
        Color color = pixelReader.getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
        c.colorPicker.setValue(color);
    }

    @Override
    public void mouseReleased(ImageEditorController c, MouseEvent mouseEvent) {
        // no-op
    }

    @Override
    public void mouseDragged(ImageEditorController c, MouseEvent mouseEvent) {
        c.updateCoordinates(mouseEvent);
    }
}
