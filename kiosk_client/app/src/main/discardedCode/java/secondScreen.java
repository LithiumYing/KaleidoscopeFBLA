package kiosk_client.app.src.main.discardedCode.java;

public class secondScreen {

    /*
     * This code is an experimental idea for a new login screen if we make nationals
     * Likely will require a lot of work and rewriting a different QR Code Scanner
     */

    public void secondScreen() {
        // set up second screen here
        JFrame secondScreen = new JFrame();
        secondScreen.setLayout(null);
        secondScreen.setResizable(false);
        // secondScreen.setAlwaysOnTop(true);

        fullScreen(secondScreen);

        // gets rid of the header
        // secondScreen.setUndecorated(true);

        // sets the background to dark gray
        secondScreen.setBackground(Color.DARK_GRAY);
        // disables alt-f4
        // secondScreen.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        scanQRCode scanner = new scanQRCode();
        Future<String> scannedQRCode = scanner.getQRCode(secondScreen, (int) dimensions.getWidth(),
                (int) dimensions.getHeight());

        JButton alternativeLoginButton = new JButton("Or Login with Student ID");
        alternativeLoginButton.setBounds((int) (0.5 * dimensions.getWidth() - 100),
                (int) (0.8 * dimensions.getHeight()), 200, 25);
        alternativeLoginButton.setBackground(Color.LIGHT_GRAY);
        alternativeLoginButton.setForeground(Color.BLACK);

        // manual thingy
        alternativeLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manualStudentId(secondScreen);
            }
        });

        secondScreen.getContentPane().add(alternativeLoginButton);

        secondScreen.setAlwaysOnTop(true);
        secondScreen.setVisible(true);

        try {
            String input = scannedQRCode.get();
            System.out.println("QRCode value: " + input);
            if (isInteger(input)) {
                currId = Integer.parseInt(input);
                currStudent = GraphQLClient.checkStudentId(currId);
                if (!currStudent.equals("Not Found")) {
                    currId = Integer.parseInt(input);
                    greetingScreen(secondScreen);
                } else {
                    incorrectPasswordScreen(secondScreen, "Invalid Student ID");
                }
            } else {
                incorrectPasswordScreen(secondScreen, "Invalid Student ID");
            }

        } catch (Exception e) {
            System.out.println("Error in getting QRCode value");
            // probably shouldn't ever get here????
        }
    }

}
