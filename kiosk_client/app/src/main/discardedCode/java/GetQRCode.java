package kiosk_client.app.src.main.discardedCode.java;

import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;

import graphql.kickstart.execution.StaticGraphQLRootObjectBuilder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.Reader;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// import org.hibernate.validator.internal.util.privilegedactions.GetResolvedMemberMethods;

import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.FileWriter;

import java.lang.reflect.InvocationTargetException;

public class GetQRCode extends SwingWorker<String, Object> {
    private JFrame frame;
    private Webcam webcam;
    private JPanel contentPane;
    private WebcamPanel panel;

    public GetQRCode() {
        this.frame = new JFrame("Searching For QR Code. . .");
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                webcam.close();
            }
        });
        this.webcam = Webcam.getDefault();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);

        webcam.setViewSize(new java.awt.Dimension(640, 480));
        panel = new WebcamPanel(webcam);
        panel.setMirrored(false);

        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new java.awt.Dimension(640, 480));
        contentPane.add(panel);

        frame.setContentPane(contentPane);

        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(contentPane);
            frame.pack();
            frame.setVisible(true);
        });
    }

    @Override
    public String doInBackground() {

        // try {
        // SwingUtilities.invokeAndWait(() -> {
        // frame.setContentPane(contentPane);
        // frame.pack();
        // frame.setVisible(true);

        // });
        // } catch (InterruptedException e) {
        // // Handle the interruption
        // Thread.currentThread().interrupt();
        // } catch (InvocationTargetException e) {
        // // Handle the exception thrown by the task
        // Throwable cause = e.getCause();
        // // Log an error message, re-throw the exception, or take some other action
        // }

        /*
         * loops through infinitely by getting a image from the webcam and scanning it
         * for a QR Code until they find one
         */
        while (true) {
            BufferedImage image = webcam.getImage();
            System.out.println(image);
            if (image == null) {
                continue;
            }

            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                Result result = new MultiFormatReader().decode(bitmap);
                if (result != null) {
                    frame.dispose();
                    webcam.close();
                    return result.getText();
                }
            } catch (NotFoundException e) {
                // no QR code in image
            }

            // Check if the worker has been cancelled
            if (isCancelled()) {
                frame.dispose();
                webcam.close();
                return null;
            }
        }

        // if this is returned, something is **really** wrong
    }

    @Override
    protected void done() {
        try {
            get();

        } catch (Exception e) {
        }

        System.out.println("closing");
        webcam.close();
        frame.dispose();
    }

    public JFrame getJFrame() {
        return frame;
    }

    public JPanel getContentPane() {
        return (JPanel) frame.getContentPane();
    }

    public static void main(String[] args) {
        GetQRCode qrCode = new GetQRCode();
        qrCode.execute();
        // try {
        // qrCode.done();
        // String input = qrCode.get();
        // System.out.println(".get() output = " + input);
        // } catch (Exception e1) {
        // System.out.println("Error");
        // }

    }
}