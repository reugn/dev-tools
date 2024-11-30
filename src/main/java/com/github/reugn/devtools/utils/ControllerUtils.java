package com.github.reugn.devtools.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public final class ControllerUtils {

    public static List<Integer> range(int min, int max, int step) {
        return IntStream.iterate(min, operand -> operand <= max, operand -> operand + step)
                .boxed().collect(toList());
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = newImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return newImage;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf + 1);
    }
}
