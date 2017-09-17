package app.cap.ajm.Prox;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import app.cap.ajm.Adapter.SMSDBhelper;
import app.cap.ajm.R;

public class DialogActivity extends AppCompatActivity {
    private Handler handler;
    final Context context = this;
    String phoneNum = "";
    String textMsg;
    private String prevNumber;
    private SQLiteDatabase sqls;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        SMSDBhelper dpHelper = new SMSDBhelper(this);
        sqls = dpHelper.getReadableDatabase();
        handler = new Handler();
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                dialog.dismiss();
                finish();

            }
        });
        dialog.show();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                double latitude = intent.getExtras().getDouble("lastlat");
                double longitude = intent.getExtras().getDouble("lastlon");
                List itemIds = new ArrayList<>();
                Cursor cursor = getAllContacts();
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        String data = cursor.getString(cursor.getColumnIndex("contact"));
                        itemIds.add(data);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                Iterator it = itemIds.iterator();
                while (it.hasNext()) {
                    phoneNum = it.next().toString();
                    if (!phoneNum.equals(prevNumber) && phoneNum != null && ContextCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED &&
                            ContextCompat.checkSelfPermission(getApplicationContext(),
                                    android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_DENIED) {
                        textMsg = "사고 발생:" + "http://maps.google.com/?q=" + String.valueOf(latitude) + "," + String.valueOf(longitude);
                        try {
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(phoneNum, null, textMsg, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Message", textMsg + "<" + phoneNum + ">");
                        prevNumber = phoneNum;
                    }
                }
                Toast.makeText(getApplicationContext(),getString(R.string.send_message),Toast.LENGTH_LONG).show();
                speakWords(getString(R.string.send_message));
                handler.removeCallbacksAndMessages(null);
                finish();
            }
        }, 7000);
    }
    private int MY_DATA_CHECK_CODE = 0;

    private void speakWords(String speech) {
        tts.speak(speech, TextToSpeech.LANG_COUNTRY_AVAILABLE, null,null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

                tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS)
                        {
                            if (tts.isLanguageAvailable(Locale.KOREA) == TextToSpeech.LANG_AVAILABLE)
                                tts.setLanguage(Locale.KOREA);
                        }
                        else if (status == TextToSpeech.ERROR)
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.tts_not_setup), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    public Cursor getAllContacts(){
        return sqls.query(SMSContact.TABLE_NAME,null,null,null,null,null,SMSContact.COLUMN_CONTACT);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.shutdown();
            tts.stop();
            tts=null;
        }
    }
}
