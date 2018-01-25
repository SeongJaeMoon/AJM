package app.cap.ajm.Activity;

import android.os.Build;
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
import app.cap.ajm.Helper.SMSDBhelper;
import app.cap.ajm.R;
import app.cap.ajm.Service.SensorService;
import butterknife.ButterKnife;

public class AccActivity extends AppCompatActivity{
    private Button start,stop,addContacts,removes;
    private ListView lv;
    private EditText edit;
    private SQLiteDatabase sql;
    private static final String _ID = "id";
    private static final String TABLE_NAME = "ContactList";
    private static final String COLUMN_CONTACT  = "contact";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS};
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }


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
        Toast.makeText(getApplicationContext(),getString(R.string.sms_alert),Toast.LENGTH_LONG).show();

        start.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            String count = "SELECT count(*) FROM "+TABLE_NAME;
            Cursor mcursor = sql.rawQuery(count, null);
            mcursor.moveToFirst();
            int icount = mcursor.getInt(0);
            if(icount>0)
            {
                Toast.makeText(getApplicationContext(),getString(R.string.start_service_safe),Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(getApplicationContext(), SensorService.class);
                startService(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(),getString(R.string.at_least_number),Toast.LENGTH_SHORT).show();
            }
            mcursor.close();
        }
    });
        stop.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Toast.makeText(getApplication(),getString(R.string.stop_service_safe),Toast.LENGTH_LONG).show();
            Intent intent= new Intent(getApplicationContext(), SensorService.class);
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
                Toast.makeText(getApplicationContext(),getString(R.string.reset),Toast.LENGTH_SHORT).show();
            }
            else
                {
                addNewContact(edit.getText().toString());
                Toast.makeText(getApplicationContext(),getString(R.string.emergency_add)+"\n"+edit.getText().toString(),Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(),getString(R.string.remove_all),Toast.LENGTH_SHORT).show();
            Intent intent= new Intent(getApplicationContext(), SensorService.class);
            stopService(intent);
        }
    });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
   }

    public void addNewContact(String contact){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CONTACT,contact);
        sql.insert(TABLE_NAME,null,cv);
    }

    public void removeContact(String contact){
        sql.delete(TABLE_NAME, "contact"+"=?",new String[]{contact});
    }

    public Cursor getAllContacts(){
        return sql.query(TABLE_NAME,null,null,null,null,null,COLUMN_CONTACT);
    }
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
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
