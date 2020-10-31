package com.example.wmp;


import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private TextView output;
    private EditText number;
    private CheckBox checkBox;
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.output);
        number = (EditText) findViewById(R.id.number);
        checkBox = (checkBox) findViewById(R.id.check);

        adapter = NfcAdapter.getDefaultAdapter(this);
        if (adapter == null) {
            //NFC 미지원단말
            Toast.makeText(getApplicationContext(), "NFC를 지원하지 않는 단말기입니다.", Toast.LENGTH_SHORT).show();
        } else{
            if(!adapter.isEnabled())
                Toast.makeText(getApplicationContext(), "NFC 켜주세요", Toast.LENGTH_SHORT).show();
            else
                pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        //펜딩인텐트
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String tagID = byteArrayToHexString(myTag.getId());//NFC tag ID

        //url, parameter
        String myUrl = "http://172.16.5.4:5550/";
        if (checkBox.isChecked()) {
            myUrl = myUrl + "register";
        }
        String user = "name=" + Secure.getString(context.contentResolver, Secure.ANDROID_ID) + tagID + "&pw=" + number.getText();
        NetworkTask task = new NetworkTask(myUrl);
        task.execute(user);
    }

    String byteArrayToHexString(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02x ", b & 0xff));
        return sb.toString();
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {

        private String url;

        public NetworkTask(String url) {
            super();
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            try{
                URL _url = new URL(url);
                result = showResult(_url, params[0]);
            } catch(Exception e){
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            output.setText(s);
        }

        private String showResult(URL __url, String para) throws IOException {
            InputStream stream = null;
            OutputStream os = null;
            HttpURLConnection connection = null;

            String _result;
            try {
                connection = (HttpURLConnection) __url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
                connection.setRequestProperty("Accept-Charset", "UTF-8");

                //Transmit data by writing to the stream
                os = connection.getOutputStream();
                if(os != null){
                    os.write(para.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    throw new IOException("HTTP error code: " + connection.getResponseCode());

                //Get data by reading iputStream
                stream = connection.getInputStream();
                _result = readStream(stream, 500);
            } finally {
                // Close Stream and disconnect HTTPS connection.
                if (os != null) {
                    os.close();
                }
                if (stream != null){
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return _result;
        }

        private String readStream(InputStream stream, int maxLength) throws IOException {
            String result = null;
            // Read InputStream using the UTF-8 charset.
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            // Create temporary buffer to hold Stream data with specified max length.
            char[] buffer = new char[maxLength];
            // Populate temporary buffer with Stream data.
            int numChars = 0;
            int readSize = 0;
            while (numChars < maxLength && readSize != -1) {
                numChars += readSize;
                readSize = reader.read(buffer, numChars, buffer.length - numChars);
            }
            if (numChars != -1) {
                // The stream was not empty.
                // Create String that is actual length of response body if actual length was less than
                // max length.
                numChars = Math.min(numChars, maxLength);
                result = new String(buffer, 0, numChars);
            }
            return result;
        }
    }
}
    /*
    public static String byteArrayToHexString(byte[] b) {
    int len = b.length;
    String data = new String();
     for (int i = 0; i < len; i++){
         data += Integer.toHexString((b[i] >> 4) & 0xf);
         data += Integer.toHexString(b[i] & 0xf);
     }
     return data;

     */
