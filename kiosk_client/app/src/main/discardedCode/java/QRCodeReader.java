package kiosk_client.app.src.main.discardedCode.java;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import io.github.lithiumying.kioskclient.Camera;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.ChecksumException;

public class QRCodeReader extends JFrame {
    private JLabel label;

    public QRCodeReader() {
        // Initialize the label
        label = new JLabel();
        label.setPreferredSize(new Dimension(1000, 1000));
        add(label);

        // Start the camera thread
        AtomicBoolean isRunning = new AtomicBoolean(true);
        Thread thread = new Thread(() -> {
            Camera camera = new Camera();
            while (isRunning.get()) {
                // Capture an image from the camera
                BufferedImage image = camera.capture();
                System.out.println(image);
                // Decode the QR code from the image
                String result = decodeQRCode(image);
                if (result != null) {
                    // Display the result on the label
                    SwingUtilities.invokeLater(() -> {
                        label.setIcon(new ImageIcon(image));
                        label.setText(result);
                    });
                }
            }
            // Release the camera resources
            camera.release();
        });
        thread.start();

        // Close the thread when the frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                isRunning.set(false);
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Show the frame
        pack();
        setVisible(true);
    }

    private String decodeQRCode(BufferedImage image) {

        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Reader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap);
            return result.getText();
        } catch (NotFoundException e) {
            return null;
        } catch (ChecksumException c) {
            return null;
        } catch (FormatException f) {
            return null;
        }
    }

    public static void main(String[] args) {
        new QRCodeReader();
    }
}
