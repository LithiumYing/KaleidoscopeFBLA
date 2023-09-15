package io.github.lithiumying.kioskclient;

/*
 * creates a webview panel with the url that gets added to a JFrame using JavaFX
 */

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class webview extends JPanel {

    private JFXPanel jfxPanel;
    private WebEngine webEngine;

    /**
     * This sets up the webview panel, which is a JPanel
     * 
     * @param url the website you want to webview
     */
    public webview(String url) {
        initComponents(url);
    }

    /**
     * kills JavaFX
     */
    public void exit() {
        Platform.exit();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.getContentPane().add(new webview("https://kaleidoscope-fbla.herokuapp.com/kioskhelp"));
            frame.setMinimumSize(new Dimension(800, 600));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Add a window listener to properly shut down the JavaFX application
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Platform.exit();
                }
            });

            frame.setVisible(true);
        });

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.getContentPane().add(new webview("https://kaleidoscope-fbla.herokuapp.com/kioskhelp"));
            frame.setMinimumSize(new Dimension(800, 600));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Add a window listener to properly shut down the JavaFX application
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Platform.exit();
                }
            });

            frame.setVisible(true);
        });

    }

    /**
     * initializes the components
     * 
     * @param url the website you want to webview
     */
    private void initComponents(String url) {
        jfxPanel = new JFXPanel();
        setLayout(new BorderLayout());
        add(jfxPanel, BorderLayout.CENTER);

        // Run on the JavaFX application thread
        Platform.runLater(() -> {

            WebView browser = new WebView();
            webEngine = browser.getEngine();
            webEngine.load(url);

            Scene scene = new Scene(browser);
            jfxPanel.setScene(scene);
        });
    }
}