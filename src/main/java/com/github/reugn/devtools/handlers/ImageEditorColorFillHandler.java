package com.github.reugn.devtools.handlers;

import com.github.reugn.devtools.controllers.ImageEditorController;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ImageEditorColorFillHandler implements ImageEditorMouseEventHandler {

    @Override
    public void mousePressed(ImageEditorController c, MouseEvent mouseEvent) {
        final PixelReader pixelReader = c.getCurrentSnapshot().getPixelReader();
        final PixelWriter pixelWriter = c.getGc().getPixelWriter();
        final Color checkColor = pixelReader.getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
        final Color setColor = c.colorPicker.getValue();

        WritableImage image = c.getCurrentSnapshot();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        boolean[][] visited = new boolean[width][height];
        Queue<Pixel> pixels = new LinkedList<>();
        pixels.add(new Pixel((int) mouseEvent.getX(), (int) mouseEvent.getY()));

        while (!pixels.isEmpty()) {
            Pixel p = pixels.poll();
            if (!visited[p.x][p.y] && pixelReader.getColor(p.x, p.y).equals(checkColor)) {
                pixelWriter.setColor(p.x, p.y, setColor);
                pixels.addAll(p.getAdjacent(width, height));
                visited[p.x][p.y] = true;
            }
        }
    }

    @Override
    public void mouseReleased(ImageEditorController c, MouseEvent mouseEvent) {
        c.addSnapshot();
    }

    @Override
    public void mouseDragged(ImageEditorController c, MouseEvent mouseEvent) {
        c.updateCoordinates(mouseEvent);
    }

    private static class Pixel {
        private final int x;
        private final int y;

        private Pixel(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private List<Pixel> getAdjacent(int width, int height) {
            List<Pixel> adjacentPixels = new ArrayList<>();
            if (x > 0) {
                adjacentPixels.add(new Pixel(x - 1, y));
            }
            if (x < width - 1) {
                adjacentPixels.add(new Pixel(x + 1, y));
            }
            if (y > 0) {
                adjacentPixels.add(new Pixel(x, y - 1));
            }
            if (y < height - 1) {
                adjacentPixels.add(new Pixel(x, y + 1));
            }
            return adjacentPixels;
        }
    }
}
