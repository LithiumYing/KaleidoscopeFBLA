package io.github.lithiumying.kioskclient;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Camera {
    private final JPanel panel;
    private final Timer timer;
    private final BufferedImage image;

    public Camera() {
        // Initialize the panel and image
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        panel.setPreferredSize(new Dimension(400, 400));
        image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);

        // Initialize the timer to capture an image every 100ms
        timer = new Timer(100, e -> {
            // TODO: Capture an image from the camera and store it in the image
            // For this example, just fill the image with a random color
            Graphics g = image.getGraphics();
            g.setColor(new java.awt.Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.dispose();

            // Repaint the panel with the new image
            panel.repaint();
        });
        timer.start();
    }

    public BufferedImage capture() {
        // Return a copy of the image to avoid race conditions
        return new BufferedImage(image.getColorModel(), image.copyData(null), image.isAlphaPremultiplied(), null);
    }

    public void release() {
        timer.stop();
    }

    public JPanel getPanel() {
        return panel;
    }
}