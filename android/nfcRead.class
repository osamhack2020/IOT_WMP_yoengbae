import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(){
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String id = byteArrayToHexString(myTag.getId());
    }

}
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