package com.github.reugn.devtools.services;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AsciiServiceImpl implements AsciiService {

    @Override
    public String convert(String text, String asciiChar, int textHeight, int fontStyle, String fontName) {
        Font font = new Font(fontName, fontStyle, textHeight);
        int imageWidth = imageWidth(text, font);
        BufferedImage image = new BufferedImage(imageWidth, textHeight, BufferedImage.TYPE_BYTE_BINARY);
        Graphics graphics = image.getGraphics();
        graphics.setFont(font);
        graphics.drawString(text, 0, bPos(graphics, font));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < textHeight; i++) {
            for (int j = 0; j < imageWidth; j++)
                builder.append(image.getRGB(j, i) == Color.WHITE.getRGB() ? asciiChar : " ");
            builder.append("\n");
        }
        return builder.toString();
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
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
        Graphics graphics = image.getGraphics();
        graphics.setFont(font);
        return graphics.getFontMetrics().stringWidth(text);
    }

    private int bPos(Graphics graphics, Font font) {
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.getAscent() - metrics.getDescent();
    }
}
