package com.example.wmp;

import android.content.ContentValues;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


private class NetworkTask extends AsyncTask<Void, Void, String> {

    private String url;
    private ContentValues values;

    public NetworkTask(String url, ContentValues values) {
        super();
        this.url = url;
        this.values = values;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result;
        try{
            URL _url = new URL(url);
            result = showResult(_url);
        } catch(Exception e){
            result = null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
    }

    private String showResult(URL __url) throws IOException {
        InputStream stream = null;
        OutputStream os = null;
        HttpURLConnection connection = null;

        String _result;
        String strParams = "regisername=tempString";
        try {
            connection = (HttpURLConnection) __url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            //Transmit data by writing to the stream
            os = connection.getOutputStream();
            if(os != null){
                os.write(strParams.getBytes(StandardCharsets.UTF_8));
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