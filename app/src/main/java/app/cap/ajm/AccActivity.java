package app.cap.ajm;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

import app.cap.ajm.Adapter.SMSDBhelper;
import app.cap.ajm.Prox.SMSContact;
import app.cap.ajm.Prox.TimeTask;

public class AccActivity extends AppCompatActivity {

    private Button start,stop,addContacts,removes;
    ListView lv;
    EditText edit;
    private SQLiteDatabase sql;
    String provider;
    private static final String _ID = "id";
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS};
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        lv = (ListView) findViewById(R.id.contacts);
        edit = (EditText) findViewById(R.id.editText);
        addContacts = (Button) findViewById(R.id.add);
        removes = (Button)findViewById(R.id.removeAll);
        SMSDBhelper smsdBhelper = new SMSDBhelper(this);
        sql = smsdBhelper.getWritableDatabase();
        final Cursor cursor = getAllContacts();
        final ArrayAdapter<String> arrayAdapter;

        Toast.makeText(getApplicationContext(),"이 서비스는 SMS 발신 요금이 발생할 수 있습니다.",Toast.LENGTH_LONG).show();

        start.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Log.d("Start Button", "Pressed");
            String count = "SELECT count(*) FROM "+SMSContact.TABLE_NAME;
            Cursor mcursor = sql.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);
            if(icount>0)
            {
                Toast.makeText(getApplicationContext(),"안전모 기능을 실행합니다.",Toast.LENGTH_SHORT).show();
                speakWords("안전모 기능을 실행합니다.");
                Intent intent= new Intent(getApplicationContext(), TimeTask.class);
                startService(intent);

            }
            else
            {
                Toast.makeText(getApplicationContext(),"최소한 하나의 번호 지정이 필요합니다. 다시 시도하세요.",Toast.LENGTH_SHORT).show();
            }
            mcursor.close();
        }
    });
        stop.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            speakWords("안전모 기능을 종료합니다.");
            Toast.makeText(getApplication(),"안전모 기능을 종료합니다.",Toast.LENGTH_LONG).show();
            Intent intent= new Intent(getApplicationContext(), TimeTask.class);
            stopService(intent);
        }
    });

    ArrayList<String> list = new ArrayList<>();
        addContacts.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            ArrayList<String> list = new ArrayList<>();
            Log.d("Adding Contacts", addContacts.toString());
            //DB에 저장
            if(edit.getText().toString().length() == 0
                    || edit.getText().toString().equals("119")
                    ||!isString(edit.getText().toString()))
            {
                Toast.makeText(getApplicationContext(),"다시 입력해주세요.",Toast.LENGTH_SHORT).show();
            }
            else
                {
                addNewContact(edit.getText().toString());
                Toast.makeText(getApplicationContext(),"긴급 번호가 추가되었습니다."+"\n"+edit.getText().toString(),Toast.LENGTH_SHORT).show();
                Cursor cursor = getAllContacts();
                if (cursor.moveToFirst())
                {
                    do{
                        String data = cursor.getString(cursor.getColumnIndex("contact"));
                        list.add(data);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simplerow);
                arrayAdapter.addAll(list);;
                lv.setAdapter(arrayAdapter);
            }
            edit.setText("");
        }
    });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.v("long clicked","pos: " + i  + " long value is :"+l);
            removeContact(lv.getItemAtPosition(i).toString());
            Object remove = lv.getAdapter().getItem(i);
            ArrayAdapter arrayAdapter1 = (ArrayAdapter)lv.getAdapter();
            arrayAdapter1.remove(remove);
            return false;
        }
    });
        if (cursor.moveToFirst()){
        do{
            String data = cursor.getString(cursor.getColumnIndex("contact"));
            list.add(data);
        }while(cursor.moveToNext());
    }
        arrayAdapter = new ArrayAdapter<>(this, R.layout.simplerow);
        arrayAdapter.addAll(list);
        lv.setAdapter(arrayAdapter);


    removes.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> list = new ArrayList<>();
            Cursor cursor = getAllContacts();
            if(cursor.moveToFirst()){
                do {
                    String data = cursor.getString(cursor.getColumnIndex("contact"));
                    list.remove(data);
                    removeContact(data);
                    Log.i("removeList",data);
                }while(cursor.moveToNext());
            }cursor.close();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simplerow);
            lv.setAdapter(arrayAdapter);
            Toast.makeText(getApplicationContext(),"전체 번호가 삭제되었습니다.",Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(getApplicationContext(), TimeTask.class);
            stopService(intent);
        }
    });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
   }

    public long addNewContact(String contact){
        ContentValues cv = new ContentValues();
        cv.put(SMSContact.COLUMN_CONTACT,contact);
        return sql.insert(SMSContact.TABLE_NAME,null,cv);
    }

    public void removeContact(String contact){
        sql.delete(SMSContact.TABLE_NAME, "contact"+"=?",new String[]{contact});
    }

    public Cursor getAllContacts(){
        return sql.query(SMSContact.TABLE_NAME,null,null,null,null,null,SMSContact.COLUMN_CONTACT);
    }

    private int MY_DATA_CHECK_CODE = 1;

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
                            Toast.makeText(getApplicationContext(), "음성 안내를 사용할 수 없습니다... 개발자에게 메일로 문의하세요.", Toast.LENGTH_LONG).show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts=null;
        }
    }

    public static boolean isString(String s)
    {
        try{
            Integer.parseInt(s);
            return true;
        }catch (NumberFormatException e)
        {
            return false;
        }
    }

}
