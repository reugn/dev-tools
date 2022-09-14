package com.github.reugn.devtools.services;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AsciiServiceImpl implements AsciiService {

    @Override
    public String convert(String text, String asciiChar, int textHeight, int fontStyle, String fontName) {
        Font font = new Font(fontName, fontStyle, textHeight);
        int imageWidth = imageWidth(text, font);
        BufferedImage image = new BufferedImage(imageWidth, textHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setFont(font);
        g.drawString(text, 0, bPos(g, font));
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < textHeight; i++) {
            for (int j = 0; j < imageWidth; j++)
                buff.append(image.getRGB(j, i) == Color.WHITE.getRGB() ? asciiChar : " ");
            buff.append("\n");
        }
        return buff.toString();
    }

    @Override
    public int getStyleByName(String styleName) {
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

    private int imageWidth(String text, Font font) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setFont(font);
        return graphics.getFontMetrics().stringWidth(text);
    }

    private int bPos(Graphics graphics, Font font) {
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.getAscent() - metrics.getDescent();
    }
}
