import android.content.ContentValues;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkTask extends AsyncTask<Void, Void, String> {

    private String url;
    private ContentValues values;

    public NetworkTask(String url, ContentValues values) {

        this.url = url;
        this.values = values;
    }

    @Override
    public String doInBackground(Void... params) {

        String result = null;
        try{
            URL _url = new URL(url);
            result = showresult(_url);
        } catch(Exeption e){
            result = "ERROR";
        }

        return result;
    }

    private String showresult(URL __url) throws IOException {
        InputStream stream = null;
        OutputStream os = null;
        HttpsURLConnection connection = null;
        
        String result = null;
        String strParams = "regisername=tempString";
        try {
            connection = (HttpsURLConnection) __url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setDoInput(true);

            //Transmit data by writing to the stream
            os = connection.getOutputStream();
            if(os != null){
                os.write(strParams.getBytes("UTF-8"));
                os.flush();
            }

            //
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException("HTTP error code: " + responseCode);
            
            //Get data by reading iputStream
            stream = connection.getInputStream();
            result = readStream(stream, 500);
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (os != null) {
                os.close();
            }
            if (in != null){
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS, pct);
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