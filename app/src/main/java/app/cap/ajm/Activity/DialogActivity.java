package app.cap.ajm.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
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
import app.cap.ajm.Helper.SMSDBhelper;
import app.cap.ajm.R;

public class DialogActivity extends AppCompatActivity{

    private Handler handler;
    private final Context context = this;
    private String phoneNum = "";
    private String textMsg;
    private String prevNumber;
    private SQLiteDatabase sqls;
    private static final String TABLE_NAME = "ContactList";
    private static final String COLUMN_CONTACT  = "contact";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        SMSDBhelper dpHelper = new SMSDBhelper(this);
        sqls = dpHelper.getReadableDatabase();
        handler = new Handler();
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
                List<String> itemIds = new ArrayList<>();
                Cursor cursor = getAllContacts();
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {
                        String data = cursor.getString(cursor.getColumnIndex("contact"));
                        itemIds.add(data);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                for(String s : itemIds){
                    phoneNum = s;
                    if (!phoneNum.equals(prevNumber) && phoneNum != null && ContextCompat.checkSelfPermission(getApplicationContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED &&
                            ContextCompat.checkSelfPermission(getApplicationContext(),
                                    android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_DENIED) {
                        textMsg = getString(R.string.accident) + "http://maps.google.com/?q=" + String.valueOf(latitude) + "," + String.valueOf(longitude);
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
                handler.removeCallbacksAndMessages(null);
                finish();
            }
        }, 7000);
    }
    public Cursor getAllContacts(){
        return sqls.query(TABLE_NAME,null,null,null,null,null,COLUMN_CONTACT);
    }
}
