package com.naivekun.cloudscan;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CaptureActivity extends Activity {

    private String SERVER_URL;
    private DecoratedBarcodeView barcodeView;
    private TextView decodeMessage;
    private String lastDecodedMessage;
    private Button testConnection;
    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastDecodedMessage)) {
                return;
            }
            lastDecodedMessage = result.getText();
            int status = postURLToServer(lastDecodedMessage);
            if (status > 0) {
                setDecodeMessage("URL Updated: "+lastDecodedMessage.substring(lastDecodedMessage.length()>8?lastDecodedMessage.length()-8:0));
            } else {
                setDecodeMessage("invalid url!");
            }

//            Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    private void setDecodeMessage(String msg) {
        decodeMessage.setText(msg);
    }

    private int postURLToServer(String url) {
        int status;
        try {
            URL server_post_url = new URL(SERVER_URL+"/url/add");
            HttpURLConnection conn = (HttpURLConnection) server_post_url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("url", url);
            OutputStream os = conn.getOutputStream();
//            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(jsonParam.toString());
            bw.flush();
            bw.close();
//            os.writeBytes(jsonParam.toString());
            os.close();
            status = conn.getResponseCode();

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        SERVER_URL = getIntent().getStringExtra("server_addr");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        lastDecodedMessage = "";
        barcodeView = findViewById(R.id.barcode_scanner);
        testConnection = findViewById(R.id.button_testconn);
        testConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ret = postURLToServer("conntestTESTTTTTTT!");
                if (ret > 0) {
                    setDecodeMessage("conn test ok!");
                }
            }
        });
        decodeMessage = findViewById(R.id.text_status);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

}
