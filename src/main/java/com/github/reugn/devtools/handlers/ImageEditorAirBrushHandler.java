package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.String.format;

public class ImageEditorAirBrushHandler extends ImageEditorBrushHandler {

    private double density;

    private static List<Pixel> getLinePixels(int x1, int y1, int x2, int y2) {
        List<Pixel> pixels = new ArrayList<>();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = (dx > dy ? dx : -dy) / 2;

        int x = x1;
        int y = y1;

        while (true) {
            pixels.add(new Pixel(x, y));
            if (x == x2 && y == y2) break;
            int e2 = err;
            if (e2 > -dx) {
                err -= dy;
                x += sx;
            }
            if (e2 < dy) {
                err += dx;
                y += sy;
            }
        }

        return pixels;
    }

    @Override
    public void mousePressed(ImageEditorController c, MouseEvent mouseEvent) {
        setDensity(c);
        c.setStartCoordinates(mouseEvent);
        double lineWidth = c.lineWidthChoiceBox.getValue();
        c.getGc().setFill(c.colorPicker.getValue());
        c.getGc().setStroke(c.colorPicker.getValue());
        c.getGc().setLineWidth(lineWidth);

        drawPixel(c, new Pixel((int) mouseEvent.getX(), (int) mouseEvent.getY()));
    }

    private void setDensity(ImageEditorController c) {
        String densityText = c.densityTextField.getText();
        try {
            density = Double.parseDouble(densityText);
        } catch (NumberFormatException e) {
            String message = format("Density value %s must be numeric", densityText);
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.showAndWait();
        }
    }

    @Override
    public void mouseDragged(ImageEditorController c, MouseEvent mouseEvent) {
        c.updateCoordinates(mouseEvent);
        drawPixel(c, new Pixel((int) mouseEvent.getX(), (int) mouseEvent.getY()));
        List<Pixel> pixels = getLinePixels((int) c.getStartX(), (int) c.getStartY(),
                (int) mouseEvent.getX(), (int) mouseEvent.getY());

        for (Pixel pixel : pixels) {
            drawPixel(c, pixel);
        }

        c.setStartCoordinates(mouseEvent);
    }

    private void drawPixel(ImageEditorController c, Pixel p) {
        int lineWidth = c.lineWidthChoiceBox.getValue();
        Color color = c.colorPicker.getValue();
        if (lineWidth == 1) {
            if (density >= 1 || ThreadLocalRandom.current().nextDouble(0, 1) < density) {
                c.getGc().getPixelWriter().setColor(p.x, p.y, color);
            }
            return;
        }
        int highX = p.x + lineWidth / 2;
        int lowX = p.x - lineWidth / 2;
        int highY = p.y + lineWidth / 2;
        int lowY = p.y - lineWidth / 2;
        int fillPixels = (int) (lineWidth * density);
        if (fillPixels == 0 && ThreadLocalRandom.current().nextDouble(0, 1) < density) {
            fillPixels = 1;
        }
        for (int i = 0; i < fillPixels; i++) {
            int randX = ThreadLocalRandom.current().nextInt(lowX, highX);
            int randY = ThreadLocalRandom.current().nextInt(lowY, highY);
            c.getGc().getPixelWriter().setColor(randX, randY, color);
        }
    }

    private static class Pixel {
        private final int x;
        private final int y;

        private Pixel(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
