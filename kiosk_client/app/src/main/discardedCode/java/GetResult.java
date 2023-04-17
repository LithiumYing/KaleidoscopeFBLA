/*
 * Likely not to be used for state FBLA
 * This works standalone but not with App.java within an actionListener, I have to figure out why
 */

package kiosk_client.app.src.main.discardedCode.java;

import javax.swing.SwingWorker;

public class GetResult extends SwingWorker<String, Object> {
    private final GetQRCode qrCode;

    public GetResult(GetQRCode qrCode) {
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

    public static void main(String[] args) {
        GetQRCode qrCode = new GetQRCode();
        qrCode.execute();
    }
}
