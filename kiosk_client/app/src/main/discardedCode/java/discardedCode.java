package kiosk_client.app.src.main.discardedCode.java;

public class discardedCode {

    // This is a QR Code Scanner that will pop up a window, but will not work.
    public String setUpQRCodeScanner() {
        GetQRCode qrCode = new GetQRCode();
        qrCode.execute();
        return "bad";
        // This needs to be another class and rewritten
        SwingWorker<String, Object> getResult = new SwingWorker<String, Object>() {
            private final GetQRCode qrCode;

            public getResult(GetQRCode qrCode) {
                this.qrCode = qrCode;
            }

            @Override
            protected String doInBackground() throws Exception {
                return qrCode.get();
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println("Error");
                }
            }
        };
        getResult.execute();
        try {
            System.out.println(getResult.get());
            return getResult.get();
        } catch (Exception e) {
            System.out.println("Error");
            return "Not Found";
        }

    }
}
