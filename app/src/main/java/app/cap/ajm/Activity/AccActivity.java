package app.cap.ajm.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import app.cap.ajm.Helper.SMSDBhelper;
import app.cap.ajm.R;
import app.cap.ajm.Service.SensorService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemLongClick;

public class AccActivity extends AppCompatActivity{

    @BindView(R.id.add) Button addContacts;
    @BindView(R.id.start) Button start;
    @BindView(R.id.stop) Button stop;
    @BindView(R.id.removeAll) Button removeAll;
    @BindView(R.id.contacts) ListView lv;
    @BindView(R.id.editText) EditText edit;
    private List<String> list = new ArrayList<>();
    private SMSDBhelper smsdBhelper;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);

        ButterKnife.bind(this);


        smsdBhelper = new SMSDBhelper(this);
        smsdBhelper.open();
        final Cursor cursor = smsdBhelper.getAllContacts();
        Toast.makeText(getApplicationContext(), getString(R.string.sms_alert), Toast.LENGTH_LONG).show();

        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex(SMSDBhelper.COLUMN_CONTACT));
                list.add(data);
            } while (cursor.moveToNext());
        }
        arrayAdapter = new ArrayAdapter<>(this, R.layout.simplerow);
        arrayAdapter.addAll(list);
        lv.setAdapter(arrayAdapter);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
   }

   @OnClick(R.id.add) void onClickAdd(){
       List<String> list = new ArrayList<>();
       if(edit.getText().toString().length() == 0
               || edit.getText().toString().equals("119")
               ||!isString(edit.getText().toString())) {
           Toast.makeText(getApplicationContext(),getString(R.string.reset),Toast.LENGTH_SHORT).show();
       }
       else {
           smsdBhelper.addNewContact(edit.getText().toString());
           Toast.makeText(getApplicationContext(),getString(R.string.emergency_add)+"\n"+edit.getText().toString(),Toast.LENGTH_SHORT).show();
           Cursor cursor = smsdBhelper.getAllContacts();
           if (cursor.moveToFirst()) {
               do{
                   String data = cursor.getString(cursor.getColumnIndex(SMSDBhelper.COLUMN_CONTACT));
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

   @OnClick(R.id.start) void onClickStartService(){
       Cursor mcursor = smsdBhelper.getAllContacts();
       mcursor.moveToFirst();
       int icount = mcursor.getInt(0);
       if(icount>0) {
           Toast.makeText(getApplicationContext(),getString(R.string.start_service_safe),Toast.LENGTH_SHORT).show();
           Intent intent= new Intent(getApplicationContext(), SensorService.class);
           startService(intent);
       }
       else {
           Toast.makeText(getApplicationContext(),getString(R.string.at_least_number),Toast.LENGTH_SHORT).show();
       }
       mcursor.close();
   }

   @OnItemLongClick(R.id.contacts) boolean onLongClickListview(AdapterView<?> adapterView, View view, int i, long l){
       smsdBhelper.removeContact(lv.getItemAtPosition(i).toString());
       Object remove = lv.getAdapter().getItem(i);
       ArrayAdapter arrayAdapter = (ArrayAdapter)lv.getAdapter();
       arrayAdapter.remove(remove);
       return false;
   }

   @OnClick(R.id.removeAll) void onClickRemoveAll(){
       Cursor cursor = smsdBhelper.getAllContacts();
       if(cursor.moveToFirst()){
           do {
               String data = cursor.getString(cursor.getColumnIndex(SMSDBhelper.COLUMN_CONTACT));
               list.remove(data);
               smsdBhelper.removeContact(data);
            }while(cursor.moveToNext());
       }cursor.close();

       ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simplerow);
       lv.setAdapter(arrayAdapter);

       Toast.makeText(getApplicationContext(),getString(R.string.remove_all),Toast.LENGTH_SHORT).show();
       Intent intent= new Intent(getApplicationContext(), SensorService.class);
       stopService(intent);
   }

    @Override
   public void onPause(){
        super.onPause();
       if (smsdBhelper!=null){
           smsdBhelper.close();
       }
   }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (smsdBhelper!=null){
            smsdBhelper.close();
        }
    }

    public static boolean isString(String s) {
        try{
            Integer.parseInt(s);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }
}
