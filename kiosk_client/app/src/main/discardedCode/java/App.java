package kiosk_client.app.src.main.discardedCode.java;

import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import com.google.zxing.qrcode.encoder.QRCode;

import io.github.lithiumying.kioskclient.GraphQLClient;

import java.util.EnumMap;
import java.util.Map;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

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
import java.awt.image.BufferedImage;

import org.dataloader.Try;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

// import com.apollographql.apollo3.ApolloClient;
// import com.apollographql.apollo3.api.ApolloResponse;
// import com.apollographql.apollo3.api.Query;
// import com.apollographql.apollo3.exception.ApolloException;
// // import com.apollographql.apollo3.kotlin.coroutines.toFlow;
// import com.apollographql.apollo3.network.http.HttpNetworkTransport;
// import okhttp3.OkHttpClient;
// import java.util.List;
// import java.util.concurrent.Flow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;

// import org.graalvm.polyglot.Context;
// import org.graalvm.polyglot.Value;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

import java.util.concurrent.Future;

// import graphql.ExecutionResult;
// import graphql.GraphQL;
// import graphql.schema.GraphQLSchema;
// import graphql.schema.StaticDataFetcher;
// import graphql.schema.idl.RuntimeWiring;
// import graphql.schema.idl.SchemaGenerator;
// import graphql.schema.idl.SchemaParser;
// import graphql.schema.idl.TypeDefinitionRegistry;

// import com.apollographql.apollo3.cache.normalized.NormalizedCache;
// import com.apollographql.apollo3.cache.http.HttpCache;

// import org.jdatepicker.JDatePicker;

// import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class App {
    // set the admin password here
    private final static String adminPassword = "12345678";

    // change the initial year here
    public static final int startYear = 2023;
    // change the # of years here (has to be greater than 0)
    public static final int continueYear = 20;

    // entire screen's dimensions. Everything is scaled off the screen (hopefully)
    public static java.awt.Dimension dimensions = new java.awt.Dimension();
    // this is the color that will be in the background
    public static final Color backgroundColor = Color.DARK_GRAY;
    // fonts
    public static final Font boldedTimesNewRoman = new Font("ROMAN_BASELINE", Font.BOLD, 12);
    public static final Font timesNewRoman = new Font("ROMAN_BASELINE", Font.PLAIN, 12);

    // Event name
    public static String event;
    public static String eventId;
    public static String currStudent;
    public static String currId;

    public static ArrayList<String> eventIdList;
    public static ArrayList<String> eventList;

    public static void main(String[] args) {
        App app = new App();
        try {
            eventIdList = GraphQLClient.getAllEventId();
            eventList = GraphQLClient.getEventFromId(eventIdList);
            System.out.println(eventIdList);
            app.firstScreen();
        } catch (Exception e) {
            // should never do this but just in case
            System.out.println(
                    "Cannot find the server. Please check your internet connection and try again.");
            System.exit(0);
        }
    }

    public void firstScreen() {
        // dropdown arrays
        final String[] months = { "Month", "January", "February", "March", "April", "May", "June", "July", "August",
                "September",
                "October", "November", "December" };
        final String[] days = { "Day", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17",
                "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" };

        final String[] hours = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
        final String[] minutes = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
                "14", "15", "16", "17",
                "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32",
                "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
                "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" };
        final String[] AM_PM = { "AM", "PM" };

        // ApolloClient.Builder builder = new ApolloClient.Builder()
        // .serverUrl("https://kaleidoscope-fbla.herokuapp.com/graphql/").okHttpClient(new
        // OkHttpClient()).build();

        // // Optionally, set a normalized cache
        // NormalizedCache.configureApolloClientBuilder(builder, new
        // MemoryCacheFactory(10 * 1024 * 1024, -1),
        // TypePolicyCacheKeyGenerator.INSTANCE, FieldPolicyCacheResolver.INSTANCE,
        // false);

        // ApolloClient client = builder.build();

        // // creates an instance
        // App app = new App();

        getDimensions();
        changeAllFont();
        String[] year = new String[continueYear];
        for (int i = 0; i < continueYear; i++) {
            year[i] = Integer.toString(i + startYear);
        }

        // Whatever you do, do not remove this
        JFrame backgroundScreen = new JFrame();
        backgroundScreen.setUndecorated(true);
        backgroundScreen.getContentPane().setBackground(backgroundColor);
        backgroundScreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        fullScreen(backgroundScreen);
        // exitButtonDark(backgroundScreen, (int) (dimensions.getWidth()));
        // backgroundScreen.setLayout(null);
        backgroundScreen.setVisible(true);

        JFrame firstScreen = new JFrame(); // the parameter in jframe doesn't matter, since it doesn't show up
        // gets rid of the header
        firstScreen.setUndecorated(true);
        // sets the background to dark gray
        firstScreen.getContentPane().setBackground(backgroundColor);
        // disables alt + f4
        firstScreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // UtilDateModel model = new UtilDateModel();
        // JDatePanelImpl datePanel = new JDatePanelImpl(model);
        // JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);

        // declares an icon and and a box for the icon to be in
        // work on this later, I'll make a temporary solution
        // Icon exitIcon = new ImageIcon("exitIcon.svg");

        // creates new text area to input the event name

        JLabel eventLabel = new JLabel("Event Name:");
        eventLabel.setBounds((int) (0.5 * dimensions.getWidth() - 210), (int) (0.35 * dimensions.getHeight() - 15), 105,
                55);
        eventLabel.setBackground(backgroundColor);
        eventLabel.setForeground(Color.LIGHT_GRAY);
        JTextField eventName = new JTextField("");
        eventName.setBounds((int) (0.5 * dimensions.getWidth() - 125), (int) (0.35 * dimensions.getHeight()), 255, 25);
        eventName.setBackground(Color.LIGHT_GRAY);
        eventName.setForeground(Color.BLACK);
        eventName.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setBounds((int) (0.5 * dimensions.getWidth() - 180), (int) (0.37 * dimensions.getHeight()) + 15, 45,
                55);
        dateLabel.setBackground(backgroundColor);
        dateLabel.setForeground(Color.LIGHT_GRAY);
        // dateLabel.setBorder(BorderFactory.createLineBorder(backgroundColor));

        // creates dropdowns
        JComboBox<String> monthDropdown = new JComboBox<String>(months);
        // sets the month dropdown that the top left corner is 3% along the width and
        // the height
        // the extra 1% width is the padding between the buttons
        // the extra 2% height is the padding Sfrom the eventName text field
        monthDropdown.setBounds((int) (0.5 * dimensions.getWidth() - 125), (int) (0.37 * dimensions.getHeight()) + 25,
                90, 35);
        JComboBox<String> dayDropdown = new JComboBox<String>(days);
        dayDropdown.setBounds((int) (0.51 * dimensions.getWidth() - 35), (int) (0.37 * dimensions.getHeight()) + 25, 55,
                35);

        JComboBox<String> yearDropdown = new JComboBox<String>(year);
        yearDropdown.setBounds((int) (0.52 * dimensions.getWidth() + 20), (int) (0.37 * dimensions.getHeight()) + 25,
                60, 35);

        // creates a label for start time with 2% padding
        JLabel startTime = new JLabel("Start Time: ");
        startTime.setBounds((int) (0.5 * dimensions.getWidth() - 210), (int) (0.39 * dimensions.getHeight()) + 55 + 15,
                105, 55);
        startTime.setBackground(backgroundColor);
        startTime.setForeground(Color.LIGHT_GRAY);
        // startTime.setBorder(BorderFactory.createLineBorder(backgroundColor))

        JComboBox<String> startTime_hour = new JComboBox<String>(hours);
        startTime_hour.setBounds((int) (0.52 * dimensions.getWidth() - 155),
                (int) (0.39 * dimensions.getHeight()) + 80,
                55, 35);

        JComboBox<String> startTime_minute = new JComboBox<String>(minutes);
        startTime_minute.setBounds((int) (0.53 * dimensions.getWidth() - 100),
                (int) (0.39 * dimensions.getHeight()) + 80,
                55, 35);

        JComboBox<String> startTime_AM_PM = new JComboBox<String>(AM_PM);
        startTime_AM_PM.setBounds((int) (0.54 * dimensions.getWidth() - 45),
                (int) (0.39 * dimensions.getHeight()) + 80,
                55, 35);

        // creates a label for end time with 2% padding
        JLabel endTime = new JLabel("End Time: ");
        endTime.setBounds((int) (0.5 * dimensions.getWidth() - 200), (int) (0.41 * dimensions.getHeight()) + 95,
                85, 55);
        endTime.setBackground(backgroundColor);
        endTime.setForeground(Color.LIGHT_GRAY);
        // endTime.setBorder(BorderFactory.createLineBorder(backgroundColor))

        JComboBox<String> endTime_hour = new JComboBox<String>(hours);
        endTime_hour.setBounds((int) (0.52 * dimensions.getWidth() - 155),
                (int) (0.41 * dimensions.getHeight()) + 105,
                55, 35);

        JComboBox<String> endTime_minute = new JComboBox<String>(minutes);
        endTime_minute.setBounds((int) (0.53 * dimensions.getWidth() - 100),
                (int) (0.41 * dimensions.getHeight()) + 105,
                55, 35);

        JComboBox<String> endTime_AM_PM = new JComboBox<String>(AM_PM);
        endTime_AM_PM.setBounds((int) (0.54 * dimensions.getWidth() - 45),
                (int) (0.41 * dimensions.getHeight()) + 105,
                55, 35);

        // creates the exitButton
        exitButtonDark(firstScreen, (int) dimensions.getWidth());

        JButton confirmButton = new JButton("Continue"); // could use abetter word
        // sets the exit button so that it is the height and width are 5% of the entire
        // screen.
        // reformat later
        confirmButton.setBounds((int) (0.445 * dimensions.getWidth()), (int) (0.70 * dimensions.getHeight()),
                (int) (0.09 * dimensions.getWidth()), (int) (0.03 * dimensions.getHeight()));
        confirmButton.setBackground(Color.LIGHT_GRAY);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusable(false);
        confirmButton.setOpaque(true);
        // confirmButton.setBorderPainted();

        // adds everything to the frame
        firstScreen.getContentPane().add(confirmButton);
        firstScreen.getContentPane().add(eventName);
        firstScreen.getContentPane().add(dateLabel);
        firstScreen.getContentPane().add(monthDropdown);
        firstScreen.getContentPane().add(dayDropdown);
        firstScreen.getContentPane().add(yearDropdown);
        firstScreen.getContentPane().add(startTime);
        firstScreen.getContentPane().add(endTime);
        firstScreen.getContentPane().add(eventLabel);
        firstScreen.getContentPane().add(startTime_hour);
        firstScreen.getContentPane().add(startTime_minute);
        firstScreen.getContentPane().add(startTime_AM_PM);
        firstScreen.getContentPane().add(endTime_hour);
        firstScreen.getContentPane().add(endTime_minute);
        firstScreen.getContentPane().add(endTime_AM_PM);

        // listener section for the continue button
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // gets all the necessary info from the screen before going to next screen
                event = eventName.getText();
                // String monthString = monthDropdown.getText();

                // get dropdown stuff here

                // send info to server here

                // hides the first screen
                firstScreen.setVisible(false);
                firstScreen.dispose();
                // advances to second screen
                secondScreen();
            }
        });

        fullScreen(firstScreen);
        firstScreen.setLayout(null);
        firstScreen.setVisible(true);

    }

    public void secondScreen() {
        // send the info to server here

        // set up second screen here
        JFrame secondScreen = new JFrame();

        // gets rid of the header
        secondScreen.setUndecorated(true);
        // sets the background to dark gray
        secondScreen.getContentPane().setBackground(backgroundColor);
        // disables alt-f4
        secondScreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // creates the verification thing
        // creates a password area
        JPasswordField studentIDInput = new JPasswordField();
        // halfway on the screen and slightly up from the center
        studentIDInput.setBounds((int) (0.5 * dimensions.getWidth()) - 50, (int) ((0.4 * dimensions.getHeight())), 100,
                25);
        studentIDInput.setBackground(Color.LIGHT_GRAY);
        studentIDInput.setForeground(Color.BLACK);
        // padding
        studentIDInput.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // creates the view checkbox
        JCheckBox viewCheckBox = new JCheckBox("View Student ID");
        // 25 is just from the previous button
        // 1% padding
        viewCheckBox.setBounds((int) (0.5 * dimensions.getWidth()) - 50, (int) (0.41
                * dimensions.getHeight()) + 25,
                200,
                25);
        // no border
        viewCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // no icky white dotted box
        viewCheckBox.setFocusable(false);
        // change background color
        viewCheckBox.setBackground(backgroundColor);
        viewCheckBox.setForeground(Color.LIGHT_GRAY);
        viewCheckBox.setOpaque(true);

        // creates the "view" button
        JButton enterButton = new JButton("Enter");
        // 1% padding from the password field
        enterButton.setBounds((int) (0.51 * dimensions.getWidth()) + 50, (int) (0.4 *
                dimensions.getHeight()), 50, 25);
        enterButton.setBackground(Color.LIGHT_GRAY);
        enterButton.setForeground(Color.BLACK);
        // no border
        enterButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // no icky white dotted box
        enterButton.setFocusable(false);
        enterButton.setOpaque(true);

        JButton alternativeLoginButton = new JButton("Or Login with QR Code");
        alternativeLoginButton.setBounds((int) (0.5 * dimensions.getWidth() - 100),
                (int) (0.5 * dimensions.getHeight()), 200, 25);
        alternativeLoginButton.setBackground(Color.LIGHT_GRAY);
        enterButton.setForeground(Color.BLACK);

        JLabel passwordLabel = new JLabel("Student ID: ");
        // 1% padding
        passwordLabel.setBounds((int) ((0.49 * dimensions.getWidth()) - 110), (int) (0.4 * dimensions.getHeight()), 100,
                25);
        // sets the text vertically to the center
        passwordLabel.setVerticalAlignment(SwingConstants.CENTER);
        passwordLabel.setForeground(Color.LIGHT_GRAY);

        // adds the elements created previously
        secondScreen.getContentPane().add(studentIDInput);
        secondScreen.getContentPane().add(viewCheckBox);
        secondScreen.getContentPane().add(enterButton);
        secondScreen.getContentPane().add(passwordLabel);
        secondScreen.getContentPane().add(alternativeLoginButton);

        // adds exit button
        exitButtonDark(secondScreen, (int) (dimensions.getWidth()));

        // if viewCheckBox is clicked, show password
        viewCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (viewCheckBox.isSelected())
                    studentIDInput.setEchoChar((char) 0);
                else
                    studentIDInput.setEchoChar('\u2022');
            }
        });

        // qr code reader thingy
        alternativeLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String input = null;
                try {

                    if (isInteger(input)) {
                        currId = input;
                        try {
                            currStudent = GraphQLClient.checkStudentId(currId);
                            if (!currStudent.equals("Not Found")) {
                                greetingScreen(secondScreen);
                            } else {
                                incorrectPasswordScreen(secondScreen, "Invalid QR Code");
                            }
                        } catch (Exception E) {
                            incorrectPasswordScreen(secondScreen, "Invalid QR Code");
                        }
                    } else {
                        incorrectPasswordScreen(secondScreen, "Invalid QR Code");
                    }
                } catch (Exception E) {
                    incorrectPasswordScreen(secondScreen, "Invalid QR Code");
                }
            }

        });

        // checks if enter key is pressed extract the student id
        studentIDInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // get the student ID and check with server

                try {

                    String input = String.valueOf(studentIDInput.getPassword());
                    if (isInteger(input)) {
                        currId = input;
                        currStudent = GraphQLClient.checkStudentId(currId);
                        if (!currStudent.equals("Not Found")) {
                            greetingScreen(secondScreen);
                        } else {
                            incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                        }
                    } else {
                        incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                    }
                }

                catch (Exception E) {
                    incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                }
            }
        });

        // listener for clicks on the confirm button
        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // get the student ID and check with server
                try {

                    String input = String.valueOf(studentIDInput.getPassword());
                    if (isInteger(input)) {
                        currId = input;
                        currStudent = GraphQLClient.checkStudentId(currId);
                        if (!currStudent.equals("Not Found"))
                            greetingScreen(secondScreen);
                        else {
                            incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                        }
                    } else {
                        incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                    }
                }

                catch (Exception E) {
                    incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                }
            }

        });

        secondScreen.getContentPane().add(enterButton);

        fullScreen(secondScreen);
        secondScreen.setLayout(null);
        secondScreen.setVisible(true);
    }

    public void manualStudentId(JFrame secondScreen) {
        // creates JFrame
        JFrame verification = new JFrame();

        // makes sure this is on the top
        verification.setAlwaysOnTop(true);

        // do not change the background color

        // disables alt+f4
        verification.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // creates a password area
        JPasswordField passwordInput = new JPasswordField();
        // halfway on the screen and slightly up from the center
        passwordInput.setBounds((int) (0.1 * dimensions.getWidth()) - 50, (int) ((0.16 * dimensions.getHeight())), 100,
                25);
        passwordInput.setBackground(Color.LIGHT_GRAY);
        passwordInput.setForeground(Color.BLACK);
        // padding
        passwordInput.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // creates the view checkbox
        JCheckBox viewCheckBox = new JCheckBox("View Student ID");
        // 25 is just from the previous button
        // 1% padding
        viewCheckBox.setBounds((int) (0.1 * dimensions.getWidth()) - 50, (int) (0.17 * dimensions.getHeight()) + 25,
                200, 25);
        // no border
        viewCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // no icky white dotted box
        viewCheckBox.setFocusable(false);
        viewCheckBox.setOpaque(true);

        // creates the "view" button
        JButton enterButton = new JButton("Enter");
        // 1% padding from the enter button
        enterButton.setBounds((int) (0.11 * dimensions.getWidth()) + 50, (int) (0.16 * dimensions.getHeight()), 50, 25);
        enterButton.setBackground(Color.LIGHT_GRAY);
        enterButton.setForeground(Color.BLACK);
        // no border
        enterButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // no icky white dotted box
        enterButton.setFocusable(false);
        enterButton.setOpaque(true);

        JLabel passwordLabel = new JLabel("Student ID:");
        // 1% padding
        passwordLabel.setBounds((int) ((0.09 * dimensions.getWidth()) - 110), (int) (0.16 * dimensions.getHeight()),
                100, 25);
        // sets the text vertically to the center
        passwordLabel.setVerticalAlignment(SwingConstants.CENTER);
        // default text black, default background color

        // adds the elements created previously
        verification.getContentPane().add(passwordInput);
        verification.getContentPane().add(viewCheckBox);
        verification.getContentPane().add(enterButton);
        verification.getContentPane().add(passwordLabel);

        // adds exit button
        exitButtonLight(verification, (int) (0.20 * dimensions.getWidth()));

        // if viewCheckBox is clicked, show password
        viewCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (viewCheckBox.isSelected())
                    passwordInput.setEchoChar((char) 0);
                else
                    passwordInput.setEchoChar('\u2022');
            }
        });

        // checks if enter key is pressed extract the student id
        passwordInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // get the student ID and check with server

                try {

                    String input = String.valueOf(passwordInput.getPassword());
                    if (isInteger(input)) {
                        currId = input;
                        currStudent = GraphQLClient.checkStudentId(currId);
                        if (!currStudent.equals("Not Found")) {
                            verification.dispose();
                            greetingScreen(secondScreen);
                        } else {
                            verification.dispose();
                            incorrectPasswordScreen(verification, "Invalid Student ID");
                        }
                    } else {
                        verification.dispose();
                        incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                    }
                }

                catch (Exception E) {
                    verification.dispose();
                    incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                }
            }
        });

        // listener for clicks on the confirm button
        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // get the student ID and check with server
                try {

                    String input = String.valueOf(passwordInput.getPassword());
                    if (isInteger(input)) {
                        currId = input;
                        currStudent = GraphQLClient.checkStudentId(currId);
                        if (!currStudent.equals("Not Found")) {
                            verification.dispose();
                            greetingScreen(secondScreen);
                        } else {
                            verification.dispose();
                            incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                        }
                    } else {
                        verification.dispose();
                        incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                    }
                }

                catch (Exception E) {
                    verification.dispose();
                    incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                }
            }

        });

        // sets size of frame
        verification.setSize((int) (0.20 * dimensions.getWidth()), (int) (0.40 * dimensions.getHeight()));
        // sets the location to the center of the screen
        verification.setLocation((int) (0.4 * dimensions.getWidth()), (int) (0.3 * dimensions.getHeight()));
        // get's rid of header
        verification.setUndecorated(true);
        // sets up and makes the screen visible
        verification.setLayout(null);
        verification.setVisible(true);
    }

    public void greetingScreen(JFrame frame) {
        frame.dispose();

        JFrame greetingScreen = new JFrame("greetingScreen");

        // gets rid of header
        greetingScreen.setUndecorated(true);
        // sets background color
        greetingScreen.getContentPane().setBackground(backgroundColor);
        // disables alt-f4
        greetingScreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // creates exitButton
        exitButtonDark(greetingScreen, (int) dimensions.getWidth());

        JButton confirmButton = new JButton("Confirm"); // could use a better word
        // sets the exit button so that it is the height and width are 5% of the entire
        // screen.
        // reformat later
        confirmButton.setBounds((int) (0.445 * dimensions.getWidth()), (int) (0.70 * dimensions.getHeight()),
                (int) (0.09 * dimensions.getWidth()), (int) (0.03 * dimensions.getHeight()));
        confirmButton.setBackground(Color.LIGHT_GRAY);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusable(false);
        confirmButton.setOpaque(true);
        // confirmButton.setBorderPainted();

        // adds everything to the frame
        greetingScreen.getContentPane().add(confirmButton);

        fullScreen(greetingScreen);
        // removes layout manager
        greetingScreen.setLayout(null);
        greetingScreen.setVisible(true);

        // update the student

        // listener section for the continue button
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                greetingScreen.dispose();
                secondScreen();
                // hides the first screen
            }
        });

        greetingScreen.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    greetingScreen.dispose();
                    secondScreen();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // nothing is supposed to be here
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    greetingScreen.dispose();
                    secondScreen();
                }
            }
        });

    }

    public void verification(String action) {
        // creates JFrame
        JFrame verification = new JFrame();

        // makes sure this is on the top
        verification.setAlwaysOnTop(true);

        // do not change the background color

        // disables alt+f4
        verification.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // creates a password area
        JPasswordField passwordInput = new JPasswordField();
        // halfway on the screen and slightly up from the center
        passwordInput.setBounds((int) (0.1 * dimensions.getWidth()) - 50, (int) ((0.16 * dimensions.getHeight())), 100,
                25);
        passwordInput.setBackground(Color.LIGHT_GRAY);
        passwordInput.setForeground(Color.BLACK);
        // padding
        passwordInput.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // creates the view checkbox
        JCheckBox viewCheckBox = new JCheckBox("View Password");
        // 25 is just from the previous button
        // 1% padding
        viewCheckBox.setBounds((int) (0.1 * dimensions.getWidth()) - 50, (int) (0.17 * dimensions.getHeight()) + 25,
                200, 25);
        // no border
        viewCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // no icky white dotted box
        viewCheckBox.setFocusable(false);
        viewCheckBox.setOpaque(true);

        // creates the "view" button
        JButton enterButton = new JButton("Enter");
        // 1% padding from the enter button
        enterButton.setBounds((int) (0.11 * dimensions.getWidth()) + 50, (int) (0.16 * dimensions.getHeight()), 50, 25);
        enterButton.setBackground(Color.LIGHT_GRAY);
        enterButton.setForeground(Color.BLACK);
        // no border
        enterButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // no icky white dotted box
        enterButton.setFocusable(false);
        enterButton.setOpaque(true);

        JLabel passwordLabel = new JLabel("Password:");
        // 1% padding
        passwordLabel.setBounds((int) ((0.09 * dimensions.getWidth()) - 100), (int) (0.16 * dimensions.getHeight()),
                100, 25);
        // sets the text vertically to the center
        passwordLabel.setVerticalAlignment(SwingConstants.CENTER);
        // default text black, default background color

        // adds the elements created previously
        verification.getContentPane().add(passwordInput);
        verification.getContentPane().add(viewCheckBox);
        verification.getContentPane().add(enterButton);
        verification.getContentPane().add(passwordLabel);

        // adds exit button
        exitButtonLight(verification, (int) (0.20 * dimensions.getWidth()));

        // if viewCheckBox is clicked, show password
        viewCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (viewCheckBox.isSelected())
                    passwordInput.setEchoChar((char) 0);
                else
                    passwordInput.setEchoChar('\u2022');
            }
        });

        // checks if enter key is pressed, and enters the password
        passwordInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // checks if the password is equal
                // if so, exit the program, if not, go to next screen
                if (Arrays.equals(passwordInput.getPassword(), adminPassword.toCharArray()))
                    System.exit(0);
                else
                    incorrectPasswordScreen(verification, "Incorrect Password");
            }
        });

        // listener for clicks on the confirm button
        enterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // checks if the password is equal
                // if so, exit the program, if not, go to next screen
                if (Arrays.equals(passwordInput.getPassword(), adminPassword.toCharArray()))
                    switch (action) {
                        case "exit":
                            System.exit(0);
                        case "settings":

                    }
                else
                    incorrectPasswordScreen(verification, "Incorrect Password");
            }
        });

        // sets size of frame
        verification.setSize((int) (0.20 * dimensions.getWidth()), (int) (0.40 * dimensions.getHeight()));
        // sets the location to the center of the screen
        verification.setLocation((int) (0.4 * dimensions.getWidth()), (int) (0.3 * dimensions.getHeight()));
        // get's rid of header
        verification.setUndecorated(true);
        // sets up and makes the screen visible
        verification.setLayout(null);
        verification.setVisible(true);
    }

    public void incorrectPasswordScreen(JFrame frame, String message) {
        // gets rid of the verification screen
        if (message.equals("Incorrect Password")) {
            System.out.println("Password frame disposed");
            frame.dispose();
        }

        // new JFrame
        JFrame incorrectPasswordScreen = new JFrame();
        incorrectPasswordScreen.setAlwaysOnTop(true);

        // disables alt+f4
        incorrectPasswordScreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel incorrectPassword = new JLabel(message);
        // sets the label to the center
        incorrectPassword.setBounds((int) (0.1 * dimensions.getWidth()) - 100, (int) ((0.16 * dimensions.getHeight())),
                200, 25);
        // text alignment to the center
        incorrectPassword.setHorizontalAlignment(SwingConstants.CENTER);
        incorrectPassword.setVerticalAlignment(SwingConstants.CENTER);
        // don't change anything else

        JButton confirmButton = new JButton("Confirm");
        // 1% padding
        confirmButton.setBounds((int) (0.1 * dimensions.getWidth()) - 50, (int) (0.17 * dimensions.getHeight()) + 25,
                100, 25);
        // sets an empty border
        confirmButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        // adjusting the colors and such
        confirmButton.setBackground(Color.LIGHT_GRAY);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusable(false);
        confirmButton.setOpaque(true);

        // checks if anything happens to the enter key
        // if so, close the screen
        incorrectPasswordScreen.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    incorrectPasswordScreen.dispose();
                    // if (message.equals("Invalid Student ID"))
                    // secondScreen();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // nothing is supposed to be here
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    incorrectPasswordScreen.dispose();
                    // if (message.equals("Invalid Student ID"))
                    // secondScreen();
                }
            }
        });

        // if confirm button is pressed
        // dispose the incorrectPasswordScreen
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                incorrectPasswordScreen.dispose();
            }
        });

        // adds exit button
        exitButtonLight(incorrectPasswordScreen, (int) (0.20 * dimensions.getWidth()));

        // adds all the buttons
        incorrectPasswordScreen.getContentPane().add(incorrectPassword);
        incorrectPasswordScreen.getContentPane().add(confirmButton);

        // sets size of frame
        incorrectPasswordScreen.setSize((int) (0.20 * dimensions.getWidth()), (int) (0.40 * dimensions.getHeight()));
        // sets the location to the center of the screen
        incorrectPasswordScreen.setLocation((int) (0.4 * dimensions.getWidth()), (int) (0.3 * dimensions.getHeight()));
        // gets rid of header
        incorrectPasswordScreen.setUndecorated(true);
        // does something to the layout, but idk what it does
        incorrectPasswordScreen.setLayout(null);
        incorrectPasswordScreen.setVisible(true);
        // gets rid of the header
    }

    // creates exit button for dark frames
    public void exitButtonDark(JFrame screen, int screenWidth) {
        // new exitButton
        JButton exitButton = new JButton("\u2715");

        exitButton.setBounds((int) (screenWidth - (0.032 * dimensions.getWidth())), 0,
                (int) (0.032 * dimensions.getWidth()),
                (int) (0.04 * dimensions.getHeight()));
        exitButton.setFont(timesNewRoman);
        // stuff adjusting the button
        exitButton.setBackground(backgroundColor);
        exitButton.setForeground(Color.LIGHT_GRAY);
        // sets no focus
        exitButton.setFocusable(false);
        // exitButton.setOpaque(true);
        // no border
        exitButton.setBorderPainted(false);

        // adds it to the screen
        screen.getContentPane().add(exitButton);

        // hover things
        // app.hover(exitButton);

        // listener section for exit button
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // enter password to exit
                verification("exit");
            }
        });

    }

    // creates button for light screen (default) colored frames
    // for verification screens only
    public void exitButtonLight(JFrame screen, int screenWidth) {
        // new exitButton
        JButton exitButton = new JButton("\u2715");

        exitButton.setBounds((int) (screenWidth - (0.032 * dimensions.getWidth())), 0,
                (int) (0.032 * dimensions.getWidth()),
                (int) (0.04 * dimensions.getHeight()));
        exitButton.setFont(timesNewRoman);
        // stuff adjusting the button
        exitButton.setForeground(Color.DARK_GRAY);
        // sets no focus
        exitButton.setFocusable(false);
        exitButton.setContentAreaFilled(false);
        // no border
        exitButton.setBorderPainted(false);

        // hover things
        // app.hover(exitButton);

        // adds it to the screen
        screen.getContentPane().add(exitButton);

        // listener section for exit button
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // this is for the verification screens only, just dispose
                screen.dispose();
                // System.exit(0);
            }
        });

    }

    // goes back to first screen
    public void settings(JFrame currentFrame, JFrame settingsFrame) {
        currentFrame.setVisible(false);
        settingsFrame.setVisible(true);
    }

    // gets the dimensions of the screen
    public void getDimensions() {
        dimensions = Toolkit.getDefaultToolkit().getScreenSize();
    }

    // sets the frame to fullscreen, helps readability
    public void fullScreen(JFrame frame) {
        // sets the frame to the entire screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // frame.setSize((int)dimensions.getWidth(), (int)dimensions.getHeight());
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // exit button hover effect for exitButton
    // currently is bugged
    public void hover(JButton exitButton) {
        // gets the width and height of exitButton
        int width = (int) (0.03 * dimensions.getWidth());
        int height = (int) (0.03 * dimensions.getHeight());

        // create a BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        // draw on the BufferedImage
        g.setColor(Color.RED);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.setFont(timesNewRoman);
        g.drawString(exitButton.getText(), 10, 30);

        // create an ImageIcon
        ImageIcon redIcon = new ImageIcon(image);

        // set the ImageIcon as hover Icon
        exitButton.setRolloverIcon(redIcon);
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // changes all the font
    public void changeAllFont() {
        UIManager.put("Button.font", boldedTimesNewRoman);
        UIManager.put("ToggleButton.font", boldedTimesNewRoman);
        UIManager.put("RadioButton.font", boldedTimesNewRoman);
        UIManager.put("CheckBox.font", boldedTimesNewRoman);
        UIManager.put("ColorChooser.font", boldedTimesNewRoman);
        UIManager.put("ComboBox.font", boldedTimesNewRoman);
        UIManager.put("Label.font", boldedTimesNewRoman);
        UIManager.put("List.font", boldedTimesNewRoman);
        UIManager.put("MenuBar.font", boldedTimesNewRoman);
        UIManager.put("MenuItem.font", boldedTimesNewRoman);
        UIManager.put("RadioButtonMenuItem.font", boldedTimesNewRoman);
        UIManager.put("CheckBoxMenuItem.font", boldedTimesNewRoman);
        UIManager.put("Menu.font", boldedTimesNewRoman);
        UIManager.put("PopupMenu.font", boldedTimesNewRoman);
        UIManager.put("OptionPane.font", boldedTimesNewRoman);
        UIManager.put("Panel.font", boldedTimesNewRoman);
        UIManager.put("ProgressBar.font", boldedTimesNewRoman);
        UIManager.put("ScrollPane.font", boldedTimesNewRoman);
        UIManager.put("Viewport.font", boldedTimesNewRoman);
        UIManager.put("TabbedPane.font", boldedTimesNewRoman);
        UIManager.put("Table.font", boldedTimesNewRoman);
        UIManager.put("TableHeader.font", boldedTimesNewRoman);
        UIManager.put("TextField.font", boldedTimesNewRoman);
        UIManager.put("PasswordField.font", boldedTimesNewRoman);
        UIManager.put("TextArea.font", boldedTimesNewRoman);
        UIManager.put("TextPane.font", boldedTimesNewRoman);
        UIManager.put("EditorPane.font", boldedTimesNewRoman);
        UIManager.put("TitledBorder.font", boldedTimesNewRoman);
        UIManager.put("ToolBar.font", boldedTimesNewRoman);
        UIManager.put("ToolTip.font", boldedTimesNewRoman);
        UIManager.put("Tree.font", boldedTimesNewRoman);
    }

    public static String scanQRCode() {
        JFrame frame = new JFrame("Looking for QR Code. . .");
        // makes the window invisible
        // frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(640, 480);
        // frame.setBackground(Color.BLACK);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new java.awt.Dimension(640, 480));
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setMirrored(false);

        JPanel contentPane = new JPanel();
        contentPane.setPreferredSize(new java.awt.Dimension(640, 480));
        contentPane.add(panel);

        frame.setContentPane(contentPane);

        // JLabel label = new JLabel("Searching for QR Code...");
        // label.setBounds(10, 10, 620, 460);

        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                webcam.close();
            }
        });

        while (webcam.isOpen()) {
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
                    System.out.println(result.getText());
                    return result.getText();
                }
            } catch (NotFoundException e) {
                // no QR code in image
            }

            // // improves performance on slower machines
            // try {
            // Thread.sleep(100);
            // } catch (InterruptedException e) {

            // }
        }

        // if this is returned, something is **really** wrong
        return "No QR Code Found";

    }

}