package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ImageEditorCropHandler extends ImageEditorRectangleHandler {

    @Override
    public void mousePressed(ImageEditorController c, MouseEvent mouseEvent) {
        super.mousePressed(c, mouseEvent);
        c.getGc().setStroke(Color.GRAY);
        c.getGc().setLineWidth(2);
        c.getGc().setLineDashes(7);
        fill = false;
    }

    @Override
    public void mouseReleased(ImageEditorController c, MouseEvent mouseEvent) {
        if (mouseEvent.getX() > c.getGc().getCanvas().getWidth()
                || mouseEvent.getY() > c.getGc().getCanvas().getHeight()
                || mouseEvent.getX() < 0
                || mouseEvent.getY() < 0
                || c.getStartX() == mouseEvent.getX()
                || c.getStartY() == mouseEvent.getY()) {
            if (c.getStartX() != mouseEvent.getX() && c.getStartY() != mouseEvent.getY()) {
                c.getGc().drawImage(canvasSnapshot, 0, 0);
            }
            return;
        }

        PixelPair p = PixelPair.from(c.getStartX(), c.getStartY(), mouseEvent.getX(), mouseEvent.getY());
        WritableImage cropped = new WritableImage(canvasSnapshot.getPixelReader(),
                (int) p.x1, (int) p.y1, (int) p.x2, (int) p.y2);

        c.getGc().setLineDashes(0);
        c.drawImage(cropped);
        c.addSnapshot();
    }
}
