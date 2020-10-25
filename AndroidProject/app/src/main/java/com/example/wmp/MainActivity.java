package com.example.wmp;


import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private TextView output;
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        output = (TextView) findViewById(R.id.output);

        adapter = NfcAdapter.getDefaultAdapter(this);
        if (adapter == null) {
            //NFC 미지원단말
            Toast.makeText(getApplicationContext(), "NFC를 지원하지 않는 단말기입니다.", Toast.LENGTH_SHORT).show();
        } else{
            if(!adapter.isEnabled())
                Toast.makeText(getApplicationContext(), "NFC 켜주세요", Toast.LENGTH_SHORT).show();
            else
                pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

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
        String id = byteArrayToHexString(myTag.getId());
        output.setText(id);
    }

    String byteArrayToHexString(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02x ", b & 0xff));
        return sb.toString();
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
//이거 되면 적당히 복붙해서 메인액티비티에 추가하기
/*
xml 추가
<uses-permission android:name="android.permission.NFC" />
<uses-sdk android:minSdkVersion="10"/>

<intent-filter>
        <action android:name="android.nfc.action.NDEF_DISCOVERED"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="text/plain" />
</intent-filter>

if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
    Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    String id = byteArrayToHexString(myTag.getId());
}

enableForegroundDispatch()
*/
