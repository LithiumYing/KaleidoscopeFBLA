package kiosk_client.app.src.main.discardedCode.java.QRCodeReader;

import javax.swing.*;
import java.awt.*;
import com.github.nkzawa.socketio.client.*;
import com.github.nkzawa.emitter.Emitter;
import io.github.lithiumying.kioskclient.*;
import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class QRCodeScanner extends JFrame {

    private JLabel label;
    private Socket socket;
    private boolean connected;

    public QRCodeScanner() {
        super("QR Code Scanner");

        // Initialize the label
        label = new JLabel();
        label.setPreferredSize(new Dimension(400, 400));
        add(label);

        // Initialize the socket
        socket = IO.socket("http://localhost:3000");

        // Set up the socket listeners
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Connected to server");
                connected = true;
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Disconnected from server");
                connected = false;
            }
        }).on("qrCode", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Received QR code: " + args[0]);
                if (connected) {
                    // Send the QR code to the server
                    socket.emit("qrCode", args[0]);
                }
            }
        });

        // Start the socket
        socket.connect();

        // Start the QR code scanner
        Html5Qrcode qrCodeReader = new Html5Qrcode("qrCodeScanner");
        qrCodeReader.start(
                new Html5Qrcode.ResultCallback() {
                    @Override
                    public void onResult(String result) {
                        // Display the result on the label
                        SwingUtilities.invokeLater(() -> {
                            label.setText(result);
                        });

                        // Send the QR code to the server
                        if (connected) {
                            socket.emit("qrCode", result);
                        }
                    }
                },
                new Html5Qrcode.ExceptionCallback() {
                    @Override
                    public void onError(Exception error) {
                        System.out.println("Error: " + error.getMessage());
                    }
                });

        // Close the socket and the QR code scanner when the frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    socket.close();
                    qrCodeReader.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Show the frame
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new QRCodeScanner();
    }
}
