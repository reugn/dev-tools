package com.github.reugn.devtools.services;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AsciiService {

    private AsciiService() {
    }

    public static String convert(String text, String c, int textHeight, int fontStyle, String fontName) {
        Font font = new Font(fontName, fontStyle, textHeight);
        int imageWidth = imageWidth(text, font);
        BufferedImage image = new BufferedImage(imageWidth, textHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setFont(font);
        g.drawString(text, 0, bPos(g, font));
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < textHeight; i++) {
            for (int j = 0; j < imageWidth; j++)
                buff.append(image.getRGB(j, i) == Color.WHITE.getRGB() ? c : " ");
            buff.append("\n");
        }
        return buff.toString();
    }

    public static int getStyleByName(String styleName) {
        switch (styleName) {
            case "PLAIN":
                return Font.PLAIN;
            case "BOLD":
                return Font.BOLD;
            case "ITALIC":
                return Font.ITALIC;
        }
        return Font.PLAIN;
    }

    private static int imageWidth(String text, Font font) {
        BufferedImage im = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics g = im.getGraphics();
        g.setFont(font);
        return g.getFontMetrics().stringWidth(text);
    }

    private static int bPos(Graphics g, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        return metrics.getAscent() - metrics.getDescent();
    }
}
