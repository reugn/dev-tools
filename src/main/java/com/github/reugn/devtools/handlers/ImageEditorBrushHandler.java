package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ImageEditorBrushHandler implements ImageEditorMouseEventHandler {

    @Override
    public void mousePressed(ImageEditorController c, MouseEvent mouseEvent) {
        c.setStartCoordinates(mouseEvent);
        double lineWidth = c.lineWidthChoiceBox.getValue();
        Color color = getColor(c);
        c.getGc().setFill(color);
        c.getGc().setStroke(color);
        c.getGc().setLineWidth(lineWidth);
        c.getGc().setLineDashes(0);
        c.getGc().fillOval(c.getStartX() - lineWidth / 2, c.getStartY() - lineWidth / 2,
                lineWidth, lineWidth);
    }

    protected Color getColor(ImageEditorController c) {
        return c.colorPicker.getValue();
    }

    @Override
    public void mouseReleased(ImageEditorController c, MouseEvent mouseEvent) {
        c.addSnapshot();
    }

    @Override
    public void mouseDragged(ImageEditorController c, MouseEvent mouseEvent) {
        c.updateCoordinates(mouseEvent);
        double lineWidth = c.lineWidthChoiceBox.getValue();
        c.getGc().fillOval(mouseEvent.getX() - lineWidth / 2, mouseEvent.getY() - lineWidth / 2,
                lineWidth, lineWidth);
        c.getGc().strokeLine(c.getStartX(), c.getStartY(), mouseEvent.getX(), mouseEvent.getY());
        c.setStartCoordinates(mouseEvent);
    }
}
