package app.cap.ajm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.core.GeoHash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import app.cap.ajm.Adapter.ListAdapter;
import app.cap.ajm.Adapter.WeatherDBHelper;
import jxl.Sheet;
import jxl.Workbook;

public class SplashActivity extends AppCompatActivity {
    private WeatherDBHelper weatherDBHelper;
    private ListAdapter listAdapter;
    private Handler handler;
    private ProgressBar progressBar;
    private GeoFire geoFire, geoFire1;
    private DatabaseReference ref;
    private static final String TAG = SplashActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        /*엑셀파일 자바로 가져오기*/
        //this.listAdapter = new ListAdapter(this);
        //listAdapter.open();
        //Cursor cursor = listAdapter.fetchAllList();
        //Log.w("How many places? ", String.valueOf(cursor.getCount()));
        //if (cursor.getCount() == 0)
        //    excelToList();
        //listAdapter.close();
        //cursor.close();

        /*엑셀파일 자바로 가져오기 (날씨 정보 SQLite 저장용!)*/
        this.weatherDBHelper = new WeatherDBHelper(this);
        weatherDBHelper.open();
        Cursor cursor = weatherDBHelper.fetchAllList();
        Log.w("How many Value?:", String.valueOf(cursor.getCount()));
        if (cursor.getCount() == 0)
            excelToWeather();
        weatherDBHelper.close();
        cursor.close();

        handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                progressBar.setProgress(25);
                if (!isConnected) {Toast.makeText(getApplicationContext(), getString(R.string.no_internet_conection), Toast.LENGTH_LONG).show();}
                /*Firebase 데이터 삭제*/
                //ref = FirebaseDatabase.getInstance().getReference();
                //ref.child("geofire").removeValue();
                //ref.child("location").removeValue();

                /*엑셀파일 Firebase로 저장*/
                //ListToFirebase();

                /*테스트 만들기*/
                //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                //String pushId = databaseReference.child("geofire").push().getKey();
                //String pushId1 = databaseReference.child("location").push().getKey();
                //geoFire = new GeoFire(databaseReference.child("geofire"));
                //geoFire.setLocation(pushId, new GeoLocation(37.260163, 127.024974));
                //GeoHash geoHash = new GeoHash(37.260163, 127.024974);
                //Map<String, Object>updates = new HashMap<>();
                //updates.put("location/"+ pushId1 + "/geohash", geoHash.getGeoHashString());
                //updates.put("location/"+ pushId1 + "/name", "테스트");
                //updates.put("geofire/" + pushId + "/g", geoHash.getGeoHashString());
                //updates.put("geofire/"+ pushId + "/l", Arrays.asList(37.260163, 127.024974));
                //databaseReference.updateChildren(updates);

                progressBar.setProgress(50);
                SharedPreferences mPref = getSharedPreferences("isFirst", MODE_PRIVATE);
                Boolean bfirst = mPref.getBoolean("isFirst", true);
                progressBar.setProgress(100);
                if (bfirst && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putBoolean("isFirst", false).apply();
                    Intent intent = new Intent(SplashActivity.this, PermissionActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                    Log.w("Splash", "first");
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        }, 3000);
    }

    private void excelToList() {
        Workbook workbook = null;
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("pois.xls");
            workbook = Workbook.getWorkbook(is);
            if (workbook != null) {
               Sheet sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = sheet.getColumns();
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                    int nColumnStartIndex = 0;
                    listAdapter.open();
                    for (int nRow = nRowStartIndex + 1; nRow <= nRowEndIndex; nRow++) {
                        String content_nm = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String coor_x = sheet.getCell(nColumnStartIndex + 1, nRow).getContents();
                        String coor_y = sheet.getCell(nColumnStartIndex + 2, nRow).getContents();
                        listAdapter.createList(content_nm, coor_x, coor_y);
                    }
                    listAdapter.close();

                } else {
                    Log.i(TAG, "Sheet is null!!");
                }
            } else {
                Log.i(TAG, "WorkBook is null!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    private void excelToWeather(){
        Workbook workbook = null;
        try{
            InputStream is = getBaseContext().getResources().getAssets().open("weathers.xls");
            workbook = Workbook.getWorkbook(is);
            if (workbook != null) {
                Sheet sheet = workbook.getSheet(0);
                if (sheet != null) {
                    int nMaxColumn = sheet.getColumns();
                    int nRowStartIndex = 0;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                    int nColumnStartIndex = 0;
                    weatherDBHelper.open();
                    for (int nRow = nRowStartIndex - 1; nRow <= nRowEndIndex; nRow++) {
                        String key = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String value = sheet.getCell(nColumnStartIndex + 1, nRow).getContents();
                        weatherDBHelper.createList(key, value);
                    }
                    weatherDBHelper.close();
                } else {
                    Log.i(TAG, "Sheet is null!!");
                }
            }else{
                    Log.i(TAG, "Woorkbook is null!!!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    private void ListToFirebase() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            listAdapter = new ListAdapter(this);
            listAdapter.open();
            Cursor cursor = listAdapter.fetchAllList();
            cursor.moveToFirst();
            try {
                while (!cursor.isAfterLast()){
                   String name = cursor.getString(cursor.getColumnIndex(listAdapter.KEY_CONTENT_NAME));
                   String lat = cursor.getString(cursor.getColumnIndex(listAdapter.KEY_COORDINATE_X));
                   String lng = cursor.getString(cursor.getColumnIndex(listAdapter.KEY_COORDINATE_Y));
                            String pushId = databaseReference.child("geofire").push().getKey();
                            geoFire = new GeoFire(databaseReference.child("geofire"));
                            geoFire.setLocation(pushId, new GeoLocation(Double.parseDouble(lat),Double.parseDouble(lng)));
                            GeoHash geoHash = new GeoHash(Double.parseDouble(lat),Double.parseDouble(lng));
                            Map<String, Object>updates = new HashMap<>();
                            updates.put("location/"+ pushId + "/geohash", geoHash.getGeoHashString());
                            updates.put("location/"+ pushId + "/name", name);
                            updates.put("geofire/" + pushId + "/g", geoHash.getGeoHashString());
                            updates.put("geofire/"+ pushId + "/l", Arrays.asList(Double.parseDouble(lat),Double.parseDouble(lng)));
                            databaseReference.updateChildren(updates);
                    cursor.moveToNext();
                }
                cursor.close();
                listAdapter.close();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "오류 발생" + e, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "오류 발생" + e, Toast.LENGTH_LONG).show();
        }
    }
}

