package io.github.lithiumying.kioskclient;

import com.google.zxing.*;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.text.*;

public class Main {

    // entire screen's dimensions. Everything is scaled off the screen (hopefully)
    public static java.awt.Dimension dimensions = new java.awt.Dimension();
    // this is the color that will be in the background
    public static final Color backgroundColor = Color.DARK_GRAY;
    // fonts
    public static final Font boldedTimesNewRoman = new Font("ROMAN_BASELINE", Font.BOLD, 16);
    public static final Font timesNewRoman = new Font("ROMAN_BASELINE", Font.PLAIN, 16);
    public static final Font boldedItalicArial = new Font("Arial", Font.BOLD | Font.ITALIC, 30);

    // Event and Student names and IDs
    public static String event;
    public static String eventId;
    public static String currStudent;
    public static String currId;
    public static AtomicReference<String> prevId = new AtomicReference<>("");
    public static AtomicBoolean scannedDuring = new AtomicBoolean(false);

    // all available events and event IDs
    public static ArrayList<String> eventIdList = new ArrayList<String>();
    public static ArrayList<String> eventList = new ArrayList<String>();

    public static void main(String[] args) {
        Main app = new Main();
        app.getDimensions();
        app.changeAllFont();
        // app.soundTestScreen();

        try {
            app.getDimensions();
            app.changeAllFont();
            GraphQLClient.getAllEvent(eventList, eventIdList);
            System.out.println(eventIdList);
            app.soundTestScreen();
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

        // adds everything to the frame
        eventChooseScreen.getContentPane().add(confirmButton);
        eventChooseScreen.getContentPane().add(eventDropdown);
        eventChooseScreen.getContentPane().add(eventLabel);

        // listener section for the continue button
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // gets all the necessary info from the screen before going to next screen
                event = eventList.get(eventDropdown.getSelectedIndex());
                eventId = eventIdList.get(eventDropdown.getSelectedIndex());
                System.out.println(eventId);
                eventChooseScreen.dispose();
                // advances to the student sign in screen
                signinScreen();

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
        confirmButton.setBounds((int) (0.445 * dimensions.getWidth()), (int) (0.5 * dimensions.getHeight()),
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

        JFrame signinScreen = new JFrame("Kaleidoscope");
        signinScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Webcam webcam = Webcam.getDefault(); // Generate Webcam Object
        webcam.setViewSize(new java.awt.Dimension(640, 480));
        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setMirrored(false);
        webcamPanel.setBounds((int) (dimensions.getWidth() / 2) - 320, (int) (dimensions.getHeight() / 2) - 240, 640,
                480);
        signinScreen.add(webcamPanel);

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
        StyledDocument doc = signinLabel.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        signinScreen.add(signinLabel);

        JTextPane signinTitle = new JTextPane();
        signinTitle.setText(
                "Event Sign In");
        // 10 px buffer to the webcam panel
        signinTitle.setBounds((int) (dimensions.getWidth() / 2) - 320, (int) (dimensions.getHeight() / 2) - 290,
                640,
                50);
        signinTitle.setOpaque(false);
        signinTitle.setEditable(false);
        signinTitle.setFocusable(false);
        signinTitle.setAlignmentX(JTextPane.CENTER_ALIGNMENT);
        signinTitle.setAlignmentY(JTextPane.BOTTOM_ALIGNMENT);
        StyledDocument titleDoc = signinTitle.getStyledDocument();
        Style style = titleDoc.addStyle("myStyle", null);
        StyleConstants.setFontFamily(style, boldedItalicArial.getFamily());
        StyleConstants.setFontSize(style, boldedItalicArial.getSize());
        titleDoc.setCharacterAttributes(0, titleDoc.getLength(), style, true);
        signinScreen.add(signinTitle);

        signinScreen.setLayout(null);
        fullScreen(signinScreen);
        signinScreen.setVisible(true);

        // starts a new thread to scan for QR codes using an infinite loop
        Thread qrCodeScanner = new Thread(() -> {
            while (true) {
                BufferedImage image = webcamPanel.getImage();

                // if you remove this line the program will not work
                System.out.println(image);

                // if the camera isn't open yet, don't try to read the image
                if (image == null) {
                    continue;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                // try to read the QR code from the image
                try {
                    Result result = new MultiFormatReader().decode(bitmap);
                    // if there is a QR code in the image, check in the student
                    if (result != null) {
                        currId = result.getText();
                        if (!prevId.get().equals(currId)) {
                            checkInStudent(currId);
                            prevId.set(currId);
                        }
                    }
                } catch (NotFoundException e) {
                    // no QR code in image
                }
            }
        });

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (scannedDuring.get())
                    continue;
                if (prevId.get().equals(currId)) {
                    prevId.set("");
                }
                scannedDuring.set(false);
            }
        });
        thread.start();
        qrCodeScanner.start();
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
        // if the studentId is not an int, play the denied sound
        if (!isInteger(studentId)) {
            SoundPlayer.playDeniedSound();
            return;
        }
        try {
            currStudent = GraphQLClient.checkStudentId(currId);
            if (!currStudent.equals("Not Found")) {
                // tries to update and the boolean reflects if it was successful
                boolean isCompleted = GraphQLClient.updateStudentToEvent(eventId, currId);
                // if it was successful, play the accepted sound
                if (isCompleted) {
                    // testing
                    // JOptionPane.showMessageDialog(null, "New Student!");
                    // play accepted sound here
                    SoundPlayer.playAcceptedSound();
                }
                // falls through to the else statement if it was not successful
                else {
                    // JOptionPane.showMessageDialog(null, "Denied" + currId);
                    // play error sound here
                    SoundPlayer.playDeniedSound();
                }
            }
            // if the studentId is not in the database, play the denied sound
            else {
                // play error sound here
                SoundPlayer.playDeniedSound();
            }
        }
        // plays the denied sound it the GraphQL call fails
        catch (Exception E) {
            // play error sound here
            SoundPlayer.playDeniedSound();
        }
    }
}