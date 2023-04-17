package kiosk_client.app.src.main.discardedCode.java;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Dimension;
import com.github.sarxos.webcam.Webcam;
import javax.swing.JFrame;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.LuminanceSource;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Result;
import com.google.zxing.MultiFormatReader;
import javax.swing.JOptionPane;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.Color;
import javax.swing.*;

public class ScanQRCode {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    // private JFrame jFrame = new JFrame();
    private static java.awt.Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();

    // puts the QR code in the middle of the screen
    public Future<String> getQRCode(int width, int height, JFrame jFrame) {
        Callable<String> scanTask = () -> {
            Webcam webcam = Webcam.getDefault(); // Generate Webcam Object
            webcam.setViewSize(new java.awt.Dimension(640, 480));
            WebcamPanel webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setMirrored(false);
            webcamPanel.setBounds((int) (width / 2) - 320, (int) (height / 2) - 240, 640,
                    480);
            jFrame.add(webcamPanel);
            jFrame.setVisible(true);

            // jFrame.addWindowListener(new WindowAdapter() {
            // @Override
            // public void windowIconified(WindowEvent e) {
            // // the window is minimized, terminate the task here
            // jFrame.dispose();
            // }
            // });

            // jFrame.addWindowListener(new WindowAdapter() {
            // public void windowClosing(WindowEvent e) {
            // webcam.close();
            // }
            // });

            /*
             * loops through infinitely by getting a image from the webcam and scanning it
             * for a QR Code until they find one
             */
            do {
                try {
                    BufferedImage image = webcam.getImage();
                    System.out.println(image);
                    if (image == null) {
                        continue;
                    }
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Result result = new MultiFormatReader().decode(bitmap);
                    if (result.getText() != null) {
                        jFrame.setVisible(false);
                        jFrame.dispose();
                        webcam.close();
                        return result.getText();
                    }

                } catch (Exception e) {
                }
            } while (true);
        };
        return executor.submit(scanTask);
    } // End of scanQRCode

    // public void setVisible

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(100, 100);
        frame.setVisible(true);
        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("Hello");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}