package vn.edu.hcmus.stargallery.Helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRHelper {
    private Activity activity;
    private QRScanListener qrScanListener;

    public QRHelper(Activity activity) {
        this.activity = activity;
    }
    public void setQRScanListener(QRScanListener listener) {
        this.qrScanListener = listener;
    }
    public interface QRScanListener {
        void onQRScanSuccess(String scannedData);
        void onQRScanFailure();
    }
    public void scanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setOrientationLocked(false); // Allow both portrait and landscape
        integrator.initiateScan();
    }

    public void handleScanResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    if (qrScanListener != null) {
                        qrScanListener.onQRScanFailure();
                    }
                } else {
                    String scannedData = result.getContents();
                    if (qrScanListener != null) {
                        qrScanListener.onQRScanSuccess(scannedData);
                    }
                }
            }
        }
    }
}