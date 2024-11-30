package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

import static java.lang.String.format;

public abstract class ImageEditorGeometryHandler implements ImageEditorMouseEventHandler {

    protected WritableImage canvasSnapshot;
    protected boolean fill;

    @Override
    public void mousePressed(ImageEditorController c, MouseEvent mouseEvent) {
        canvasSnapshot = c.takeCanvasSnapshot();
        fill = c.fillCheckBox.isSelected();
        setLineDashes(c);

        c.setStartCoordinates(mouseEvent);
        c.getGc().setFill(c.colorPicker.getValue());
        c.getGc().setStroke(c.colorPicker.getValue());
        c.getGc().setLineWidth(c.lineWidthChoiceBox.getValue());
    }

    private void setLineDashes(ImageEditorController c) {
        String[] dashedValues = c.dashedTextField.getText().split(":");
        try {
            double[] dashes = Arrays.stream(dashedValues).map(Double::parseDouble)
                    .mapToDouble(Double::doubleValue).toArray();
            c.getGc().setLineDashes(dashes);
        } catch (Exception e) {
            String message = getErrorMessage(c.dashedTextField.getText());
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.showAndWait();
        }
    }

    private String getErrorMessage(String dashedValue) {
        if (dashedValue.isEmpty()) {
            return "Dashed value is not specified";
        }
        return format("Invalid dashed value: %s\nMust be numeric, or a colon-separated pattern," +
                " e.g. 4:2, 10:5:5:5", dashedValue);
    }

    @Override
    public void mouseReleased(ImageEditorController c, MouseEvent mouseEvent) {
        c.addSnapshot();
    }

    protected static class PixelPair {
        protected final double x1;
        protected final double y1;
        protected final double x2;
        protected final double y2;

        private PixelPair(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        protected static PixelPair from(double x1, double y1, double x2, double y2) {
            if (x2 - x1 < 0 && y2 - y1 < 0) {
                return new PixelPair(x2, y2, x1 - x2, y1 - y2);
            } else if (x2 - x1 < 0 && y2 - y1 >= 0) {
                return new PixelPair(x2, y1, x1 - x2, y2 - y1);
            } else if (x2 - x1 >= 0 && y2 - y1 < 0) {
                return new PixelPair(x1, y2, x2 - x1, y1 - y2);
            }
            return new PixelPair(x1, y1, x2 - x1, y2 - y1);
        }
    }
}
