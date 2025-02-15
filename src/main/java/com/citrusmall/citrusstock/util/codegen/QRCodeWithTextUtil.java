package com.citrusmall.citrusstock.util.codegen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class QRCodeWithTextUtil {

    public static byte[] addTextBelowQRCode(BufferedImage qrImage, String text) throws Exception {
        int qrWidth = qrImage.getWidth();
        int qrHeight = qrImage.getHeight();
        int textAreaHeight = 60; // Increased height for text visibility

        int combinedHeight = qrHeight + textAreaHeight;
        BufferedImage combined = new BufferedImage(qrWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = combined.createGraphics();
        // Enable anti-aliasing for smooth text rendering
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill background with white color
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, qrWidth, combinedHeight);

        // Draw the QR code at the top
        g.drawImage(qrImage, 0, 0, null);

        // Set up the font and text color
        Font font = new Font("Arial", Font.BOLD, 16);
        g.setFont(font);
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();

        // Calculate coordinates to center the text horizontally
        int textWidth = fm.stringWidth(text);
        int x = (qrWidth - textWidth) / 2;
        int y = qrHeight + ((textAreaHeight - fm.getHeight()) / 2) + fm.getAscent();

        // Draw the text
        g.drawString(text, x, y);
        g.dispose();

        // Convert combined image to byte array in PNG format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(combined, "png", baos);
        return baos.toByteArray();
    }
}
