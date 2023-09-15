package io.github.lithiumying.kioskclient;

import com.google.zxing.*;
import com.google.zxing.Dimension;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import java.awt.image.BufferedImage;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.RescaleOp;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.javafx.application.PlatformImpl;

import javafx.collections.ObservableList;

import java.net.URL;

import javax.swing.text.*;

public class Main {
    // entire screen's dimensions. Everything is scaled off the screen (hopefully)
    private java.awt.Dimension dimensions = new java.awt.Dimension();
    // this is the color that will be in the background
    // fonts
    private final Font boldedTimesNewRoman = new Font("ROMAN_BASELINE", Font.BOLD, 16);
    private final Font timesNewRoman = new Font("ROMAN_BASELINE", Font.PLAIN, 16);
    private final Font boldedItalicArial = new Font("Arial", Font.BOLD | Font.ITALIC, 30);

    // Event and Student names and IDs
    private String eventId;
    private String currId;

    // atomic variables to make it thread safe
    private AtomicReference<String> prevId = new AtomicReference<>("");
    // private AtomicBoolean scannedDuring = new AtomicBoolean(false);

    // all available events and event IDs
    private ArrayList<String> eventIdList = new ArrayList<String>();
    private ArrayList<String> eventList = new ArrayList<String>();
    // if the internet cuts out, used to store the scanned in student IDs
    private ArrayList<String> notSentStudentId = new ArrayList<String>();
    private HashSet<String> checkInNotSentStudentId = new HashSet<String>();
    private HashSet<String> checkOutNotSentStudentId = new HashSet<String>();

    // makes sure the help "?" button doesn't open multiple windows
    private boolean isHelpWindowOpenEventSelect = false;
    private boolean isHelpWindowOpenQRCode = false;
    // determines if the offline mode message has been shown or not, only show once
    // when the program is offline
    private boolean offlineModeMessageShown = false;

    public Main() {
        getDimensions();
        changeAllFont();
        try {
            SoundPlayer.warmup();
            GraphQLClient.getAllEvent(eventList, eventIdList);
            System.out.println(eventIdList);
            // creates an invisible JFrame to fix a bug
            // DO NOT REMOVE
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame();
                frame.getContentPane().add(new webview("https://kaleidoscope-fbla.herokuapp.com/kioskhelp"));
                frame.setMinimumSize(new java.awt.Dimension(850, 600));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                // frame.setVisible(true);
            });
            soundTestScreen();
        } catch (Exception e) {
            // should never do this but just in case
            JOptionPane.showMessageDialog(null,
                    "Could not connect to server, please check your internet connection and try again. ");
            // System.exit(1);
        }
    }

    /**
     * Creates and displays the eventChoose screen, which prompts a user to choose
     * from a
     * list of events.
     */
    public void eventChoose() {
        // dropdown arrays

        JFrame eventChooseScreen = new JFrame("Kaleidescope Set Up");
        eventChooseScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel eventLabel = new JLabel("Event: ");
        eventLabel.setBounds((int) (0.45 * dimensions.getWidth() - 175), (int) (0.40 * dimensions.getHeight() - 15),
                105,
                55);

        // the dropdown that shows all the events i a dropdown
        JComboBox<String> eventDropdown = new JComboBox<String>(eventList.toArray(new String[eventList.size()]));
        // sets the month dropdown that the top left corner is 3% along the width and
        // the height
        // the extra 1% width is the padding between the buttons
        // the extra 2% height is the padding Sfrom the eventName text field
        eventDropdown.setBounds((int) (0.45 * dimensions.getWidth() - 100), (int) (0.40 * dimensions.getHeight() - 7.5),
                400,
                40);

        // creates the confirm button
        JButton confirmButton = new JButton("Continue"); // could use abetter word
        // sets the exit button so that it is the height and width are 5% of the entire
        // screen.
        // reformat later
        confirmButton.setBounds((int) (0.445 * dimensions.getWidth()), (int) (0.52 * dimensions.getHeight()),
                (int) (0.09 * dimensions.getWidth()), (int) (0.03 * dimensions.getHeight()));
        confirmButton.setBackground(Color.LIGHT_GRAY);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusable(false);
        confirmButton.setOpaque(true);
        // confirmButton.setBorderPainted();

        JButton questionButton = new JButton("?");
        questionButton.setBounds((int) dimensions.getWidth() - 65, (int) dimensions.getHeight() - 125, 45, 45);
        questionButton.setOpaque(true);
        questionButton.setFocusable(false);
        questionButton.setBackground(Color.LIGHT_GRAY);
        questionButton.setForeground(Color.BLACK);

        // adds everything to the frame
        eventChooseScreen.getContentPane().add(confirmButton);
        eventChooseScreen.getContentPane().add(eventDropdown);
        eventChooseScreen.getContentPane().add(eventLabel);
        eventChooseScreen.add(questionButton);

        // listener section for the continue button
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // gets all the necessary info from the screen before going to next screen
                eventId = eventIdList.get(eventDropdown.getSelectedIndex());
                System.out.println(eventId);
                eventChooseScreen.dispose();
                // advances to the student sign in screen
                signinScreen();

            }
        });
        // listener section for the help frame
        questionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isHelpWindowOpenEventSelect) {
                    isHelpWindowOpenEventSelect = true;
                    createHelpWindowWebview();
                }
            }
        });

        // sets up the first screen then sets it visible
        fullScreen(eventChooseScreen);
        eventChooseScreen.setLayout(null);
        eventChooseScreen.setVisible(true);
    }

    /**
     * Creates and displays the second screen in which the user tests the sound
     */
    public void soundTestScreen() {
        JFrame soundTestScreen = new JFrame("Kaleidescope Sound Test");
        soundTestScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fullScreen(soundTestScreen);

        // creates a text box
        JTextPane soundTestLabel = new JTextPane();
        soundTestLabel.setText(
                "This is a sound test. Please make sure your speakers are on, and adjust so that both sounds are to an appropriate volume. Each button will play its respective sound once.");
        soundTestLabel.setBounds((int) (0.34 * dimensions.getWidth()), (int) (0.42 * dimensions.getHeight()) - 100,
                (int) (0.32 * dimensions.getWidth()), 100);
        soundTestLabel.setOpaque(false);
        soundTestLabel.setEditable(false);
        soundTestLabel.setFocusable(false);
        soundTestLabel.setAlignmentX(JTextPane.CENTER_ALIGNMENT);
        soundTestLabel.setAlignmentY(JTextPane.CENTER_ALIGNMENT);
        // styling
        StyledDocument doc = soundTestLabel.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        JButton acceptedButtonTest = new JButton("Test Accepted Sound");
        // 2 % vertical and 1% horizontal buffer
        acceptedButtonTest.setBounds((int) (0.35 * dimensions.getWidth()), (int) (0.42 * dimensions.getHeight()),
                (int) (0.14 * dimensions.getWidth()), (int) (0.03 * dimensions.getHeight()));
        acceptedButtonTest.setBackground(Color.LIGHT_GRAY);
        acceptedButtonTest.setForeground(Color.BLACK);
        acceptedButtonTest.setFocusable(false);
        acceptedButtonTest.setOpaque(true);
        JButton deniedButtonTest = new JButton("Test Denied Sound");
        // 2 % vertical and 1% horizontal buffer
        deniedButtonTest.setBounds((int) (0.51 * dimensions.getWidth()), (int) (0.42 * dimensions.getHeight()),
                (int) (0.14 * dimensions.getWidth()), (int) (0.03 * dimensions.getHeight()));
        deniedButtonTest.setBackground(Color.LIGHT_GRAY);
        deniedButtonTest.setForeground(Color.BLACK);
        deniedButtonTest.setFocusable(false);
        deniedButtonTest.setOpaque(true);

        JButton confirmButton = new JButton("Continue"); // could use a better word
        // sets the exit button so that it is the height and width are 5% of the entire
        // screen.
        confirmButton.setBounds((int) (0.445 * dimensions.getWidth()), (int) (0.52 * dimensions.getHeight()),
                (int) (0.09 * dimensions.getWidth()), (int) (0.03 * dimensions.getHeight()));
        confirmButton.setBackground(Color.LIGHT_GRAY);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusable(false);
        confirmButton.setOpaque(true);

        // listener section for the continue button
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                soundTestScreen.dispose();
                eventChoose();
            }
        });

        // listener section for the acceptedButtonTest, which is used to play the
        // accepted sound 5 times
        acceptedButtonTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundPlayer.testAcceptedSound(1);
            }
        });

        // listener section for the deniedButtonTest, which is used to play the denied
        // sound 5 times
        deniedButtonTest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundPlayer.testDeniedSound(1);
            }
        });

        soundTestScreen.getContentPane().add(confirmButton);
        soundTestScreen.getContentPane().add(soundTestLabel);
        soundTestScreen.getContentPane().add(acceptedButtonTest);
        soundTestScreen.getContentPane().add(deniedButtonTest);

        soundTestScreen.setLayout(null);
        soundTestScreen.setVisible(true);
    }

    /**
     * Creates and displays the screen students signing in will see. This includes a
     * QR Code Reader that checks in/out the student and give audio cues whether a
     * check in/out was successful or not
     */
    public void signinScreen() {
        // already have a help for this, we can show this later in the presentation if
        // the glare is too large.
        // try {
        // BrightnessManager.setBrightness(25);
        // } catch (Exception E) {
        // E.printStackTrace();
        // }
        JFrame signinScreen = new JFrame("Kaleidoscope");
        signinScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Webcam webcam = Webcam.getDefault(); // Generate Webcam Object
        webcam.setViewSize(new java.awt.Dimension(640, 480));
        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setMirrored(false);
        webcamPanel.setBounds((int) (dimensions.getWidth() / 2) - 320, (int) (dimensions.getHeight() / 2) - 240, 640,
                480);
        signinScreen.add(webcamPanel);

        // creates the text pane
        JTextPane signinLabel = new JTextPane();
        signinLabel.setText(
                "Please scan your QR code from the Kaleidoscope Student App to sign in");
        signinLabel.setBounds((int) (dimensions.getWidth() / 2) - 320, (int) (dimensions.getHeight() / 2) + 250, 640,
                50);
        signinLabel.setOpaque(false);
        signinLabel.setEditable(false);
        signinLabel.setFocusable(false);
        signinLabel.setAlignmentX(JTextPane.CENTER_ALIGNMENT);
        signinLabel.setAlignmentY(JTextPane.TOP_ALIGNMENT);
        // styling
        StyledDocument doc = signinLabel.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        signinScreen.add(signinLabel);

        // creates the title for the kiosk app
        JTextPane signinTitle = new JTextPane();
        signinTitle.setText(
                "Event Sign In/Out");
        // 10 px buffer to the webcam panel
        signinTitle.setBounds((int) (dimensions.getWidth() / 2) - 320, (int) (dimensions.getHeight() / 2) - 290,
                640,
                50);
        signinTitle.setOpaque(false);
        signinTitle.setEditable(false);
        signinTitle.setFocusable(false);
        signinTitle.setAlignmentX(JTextPane.CENTER_ALIGNMENT);
        signinTitle.setAlignmentY(JTextPane.BOTTOM_ALIGNMENT);
        // styling
        StyledDocument titleDoc = signinTitle.getStyledDocument();
        Style style = titleDoc.addStyle("myStyle", null);
        StyleConstants.setFontFamily(style, boldedItalicArial.getFamily());
        StyleConstants.setFontSize(style, boldedItalicArial.getSize());
        titleDoc.setCharacterAttributes(0, titleDoc.getLength(), style, true);
        signinScreen.add(signinTitle);

        // help button
        JButton questionButton = new JButton("?");
        questionButton.setBounds((int) dimensions.getWidth() - 65, (int) dimensions.getHeight() - 125, 45, 45);
        questionButton.setOpaque(true);
        questionButton.setFocusable(false);
        questionButton.setBackground(Color.LIGHT_GRAY);
        questionButton.setForeground(Color.BLACK);
        signinScreen.add(questionButton);

        // listener section for the help frame
        questionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isHelpWindowOpenQRCode) {
                    isHelpWindowOpenQRCode = true;
                    createHelpWindow();
                }
            }
        });

        signinScreen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Define the action to be performed when the window is closing
                String student;
                try {
                    for (String i : notSentStudentId) {
                        student = GraphQLClient.checkStudentId(i);
                        if (!student.equals("Not Found")) {
                            // tries to update and the boolean reflects if it was successful
                            // don't need to play sound becasue this is sent asynchronously
                            boolean isCompleted = GraphQLClient.updateStudentToEvent(eventId, i);
                        }
                    }
                } catch (Exception E) {
                    // add in "student IDs have not been sent here" if have time
                    // HERE
                    // in reality it shouldn't ever come here
                }
            }
        });

        signinScreen.setLayout(null);
        fullScreen(signinScreen);
        signinScreen.setVisible(true);

        // starts a new thread to scan for QR codes using an infinite loop
        Thread qrCodeScanner = new Thread(() -> {
            int filterThresholdHigh = 0;
            while (true) {
                BufferedImage image = webcamPanel.getImage();

                // if you remove this line the program will not work
                System.out.println(image);

                // if the camera isn't open yet, don't try to read the image
                if (image == null) {
                    continue;
                }

                BufferedImage adjustedImage = image;

                // Image processing for QR Code Scanner
                if (filterThresholdHigh == 0) {
                    // passthrough here, no image processing
                } else if (filterThresholdHigh == 1)
                    adjustedImage = filter(200, adjustedImage);
                else if (filterThresholdHigh == 2) {
                    // passthrough here, no image processing
                } else if (filterThresholdHigh == 3)
                    adjustedImage = filter(20, adjustedImage);

                LuminanceSource source = new BufferedImageLuminanceSource(adjustedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                // try to read the QR code from the adjusted image
                try {
                    Result result = new MultiFormatReader().decode(bitmap);
                    // if there is a QR code in the image, check in the student
                    if (result != null) {
                        currId = result.getText();
                        // JOptionPane.showMessageDialog(webcamPanel, currId);
                        if (!prevId.get().equals(currId)) {
                            // JOptionPane.showMessageDialog(null, filterThresholdHigh, null,
                            // JOptionPane.PLAIN_MESSAGE, new ImageIcon(adjustedImage));
                            checkInStudent(currId);
                            prevId.set(currId);
                        }
                    }
                } catch (NotFoundException e) {
                    // no QR code in image
                }
                filterThresholdHigh = ++filterThresholdHigh % 4;
            }
        });
        qrCodeScanner.start();

        // resets the qr code scanner
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // if (scannedDuring.get())
                // continue;
                if (prevId.get().equals(currId)) {
                    prevId.set("");
                }
                // scannedDuring.set(false);
            }
        });
        thread.start();
    }

    /**
     * Assits the user to select an event
     */
    public void createHelpWindowWebview() {
        SwingUtilities.invokeLater(() -> {
            JFrame helpFrame = new JFrame("Help");
            helpFrame.setAlwaysOnTop(true);
            // hosts a webview
            webview help = new webview("https://kaleidoscope-fbla.herokuapp.com/kioskhelp");
            helpFrame.getContentPane().add(help);
            helpFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // sets the window so that it is not open again
                    isHelpWindowOpenEventSelect = false;
                    // help.exit();
                }
            });

            helpFrame.setMinimumSize(new java.awt.Dimension(850, 600));
            helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            helpFrame.setVisible(true);

        });

    }

    /**
     * Assists the user when the QR Code Scanner is not working
     */
    public void createHelpWindow() {
        JFrame helpFrame = new JFrame("Troubleshooting");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setBounds((int) (dimensions.getWidth() / 2 - 200), (int) (dimensions.getHeight() / 2 - 175), 400,
                250);
        helpFrame.setAlwaysOnTop(true);
        helpFrame.setLayout(null);

        JTextPane helpText = new JTextPane();
        helpText.setText(
                "If the QR code reader is not working, this might be due to the glare. To reduce glare, try the following methods:\n\n1. Tilt the phone at an angle to reduce the glare\n\n2. Reduce the screen brightness\n\n3. If you are outside, move to a shaded area ");
        helpText.setBounds(5, 5, 380, 280);
        helpText.setOpaque(false);
        helpText.setEditable(false);
        helpText.setFocusable(false);
        helpText.setAlignmentX(JTextPane.CENTER_ALIGNMENT);
        helpText.setAlignmentY(JTextPane.CENTER_ALIGNMENT);
        StyledDocument doc = helpText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        // float lineSpacing = 2.0f;
        // StyleContext styleContext = StyleContext.getDefaultStyleContext();
        // AttributeSet paragraphAttributes =
        // styleContext.addAttribute(SimpleAttributeSet.EMPTY,
        // StyleConstants.LineSpacing, lineSpacing);
        // helpText.setParagraphAttributes(paragraphAttributes, false);
        helpFrame.add(helpText);

        helpFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isHelpWindowOpenQRCode = false;
            }
        });

        helpFrame.setVisible(true);
    }

    public void createOfflineNotification() {
        JFrame offlineNotification = new JFrame("Offline Mode");
        offlineNotification.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        offlineNotification.setBounds((int) (dimensions.getWidth() / 2 - 200), (int) (dimensions.getHeight() / 2 - 175),
                400,
                200);
        offlineNotification.setAlwaysOnTop(true);
        offlineNotification.setLayout(null);

        JTextPane offlineModeText = new JTextPane();
        offlineModeText.setText(
                "Offline Mode has been turned on. If this is not intended, check your internet connection. The student IDs will be stored and sent later when you reconnect. Please do not close the program before you reconnect to the internet.");
        offlineModeText.setBounds(5, 5, 380, 110);
        offlineModeText.setOpaque(false);
        offlineModeText.setEditable(false);
        offlineModeText.setFocusable(false);
        offlineModeText.setAlignmentX(JTextPane.CENTER_ALIGNMENT);
        offlineModeText.setAlignmentY(JTextPane.CENTER_ALIGNMENT);
        StyledDocument doc = offlineModeText.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        // float lineSpacing = 2.0f;
        // StyleContext styleContext = StyleContext.getDefaultStyleContext();
        // AttributeSet paragraphAttributes
        // styleContext.addAttribute(SimpleAttributeSet.EMPTY,
        // StyleConstants.LineSpacing, lineSpacing);
        // offlineModeText.setParagraphAttributes(paragraphAttributes, false);

        JButton confirmButton = new JButton("OK"); // could use a better word
        // sets the exit button so that it is the height and width are 5% of the entire
        // screen.
        confirmButton.setBounds(170, 120,
                60, 30);
        confirmButton.setBackground(Color.LIGHT_GRAY);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusable(false);
        confirmButton.setOpaque(true);

        // listener section for the continue button
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                offlineNotification.dispose();
            }
        });

        offlineNotification.add(offlineModeText);
        offlineNotification.add(confirmButton);
        offlineNotification.setVisible(true);
    }

    /**
     * Sets the JFrame frame to the entire screen (there is going to be a bar of
     * widgets at the top)
     * 
     * @param frame JFrame frame that you want to be full screen
     */
    public void fullScreen(JFrame frame) {
        // sets the frame to the entire screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // frame.setSize((int)dimensions.getWidth(), (int)dimensions.getHeight());
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * sets the dimensions of the screen to java.awt.Dimensions
     */
    public void getDimensions() {
        dimensions = Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Checks if the string you entered is an int (not Integer), returns a true if
     * it is an int, returns a false elsewise
     * 
     * @param str String
     * @return boolean
     */
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Changes all the fonts in the UI to Times New Roman
     */
    public void changeAllFont() {
        final String[] components = {
                "Button.font", "ToggleButton.font", "RadioButton.font",
                "CheckBox.font", "ColorChooser.font", "ComboBox.font",
                "Label.font", "List.font", "MenuBar.font", "MenuItem.font",
                "RadioButtonMenuItem.font", "CheckBoxMenuItem.font", "Menu.font",
                "PopupMenu.font", "OptionPane.font", "Panel.font",
                "ProgressBar.font", "ScrollPane.font", "Viewport.font",
                "TabbedPane.font", "Table.font", "TableHeader.font",
                "TextField.font", "PasswordField.font", "TextArea.font",
                "TextPane.font", "EditorPane.font", "TitledBorder.font",
                "ToolBar.font", "ToolTip.font", "Tree.font"
        };
        for (String component : components) {
            UIManager.put(component, boldedTimesNewRoman);
        }
    }

    /**
     * When a new ID is scanned in, this method will try and and check in/out a
     * student. If successful, it will play the accepted sound, if not, it will play
     * the denied sound.
     * 
     * @param studentId String studentId of the student you want to check in/out
     */
    public void checkInStudent(String studentId) {
        // Thread warmupThread = new Thread(() -> {
        // SoundPlayer.warmup();
        // });
        // warmupThread.start();
        SoundPlayer.warmup();
        // if the studentId is not an int, play the denied sound
        String student;
        if (!isInteger(studentId)) {
            SoundPlayer.playDeniedSound();
            return;
        }
        try {
            student = GraphQLClient.checkStudentId(studentId);
            offlineModeMessageShown = false;
            if (!student.equals("Not Found")) {
                // tries to update and the boolean reflects if it was successful
                boolean isCompleted = GraphQLClient.updateStudentToEvent(eventId, studentId);
                // if it was successful, play the accepted sound
                if (isCompleted) {
                    // testing
                    // JOptionPane.showMessageDialog(null, "New Student!");
                    // play accepted sound here
                    SoundPlayer.playAcceptedSound();
                }
                // falls through to the else statement if it was not successful
                else {
                    // JOptionPane.showMessageDialog(null, "Denied" + studentId);
                    // play error sound here
                    SoundPlayer.playDeniedSound();
                }
            }
            // if the studentId is not in the database, play the denied sound
            else {
                // play error sound here
                SoundPlayer.playDeniedSound();
            }

            // all of this assumes that the request was sucessful. If so, go one by one,
            // removing the values one by one, so that when one fails, it will still remain
            // in the list
            while (notSentStudentId.size() > 0) {
                String tempStudentId = notSentStudentId.get(0);
                student = GraphQLClient.checkStudentId(tempStudentId);
                if (!student.equals("Not Found")) {
                    // tries to update and the boolean reflects if it was successful
                    // don't need to play sound becasue this is sent asynchronously
                    boolean isCompleted = GraphQLClient.updateStudentToEvent(eventId, tempStudentId);
                }
                notSentStudentId.remove(0);
                if (!checkInNotSentStudentId.remove(tempStudentId)) {
                    checkOutNotSentStudentId.remove(tempStudentId);
                }
            }
        }

        // plays the denied sound it the GraphQL call fails
        // this would probably be due to the program being offline
        catch (Exception E) {
            // log into ArrayList
            if (!offlineModeMessageShown) {
                createOfflineNotification();
                offlineModeMessageShown = true;
            }
            if (checkInNotSentStudentId.contains(studentId)) {
                if (checkOutNotSentStudentId.contains(studentId)) {
                    // already in both hashsets (students scanned both in and out), so don't do
                    // anything
                } else {// only checked in, not checked out
                    notSentStudentId.add(studentId);
                    checkOutNotSentStudentId.add(studentId);
                }
            } else {
                notSentStudentId.add(studentId);
                checkInNotSentStudentId.add(studentId);
            }
            SoundPlayer.playAcceptedSound();
        }
    }

    /**
     * 
     * This method filters and only takes the pixels of the an image below a certain
     * threshold. The brightness of the pixels is calculated below the average of
     * red, green, and blue
     * 
     * @param threshold only take the pixels that the is lower than the average of
     *                  red, blue, and green
     * @param original  BufferedImage you want to be filtered
     * @return BufferedImage that is successfully filtered
     */
    public BufferedImage filter(int threshold, BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = original.getRGB(x, y);
                Color color = new Color(pixel);
                int brightness = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                if (brightness <= threshold) {
                    filteredImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    filteredImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        // filteredImage now contains only pixels above the threshold in black and all
        // other pixels in white

        return filteredImage;
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}