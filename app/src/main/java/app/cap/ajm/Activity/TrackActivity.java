package app.cap.ajm.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.Toast;
import app.cap.ajm.Helper.TrackAdapter;
import app.cap.ajm.Helper.TrackDBhelper;
import app.cap.ajm.R;

public class TrackActivity extends AppCompatActivity {
    private TextView textView;
    private ListView listView;
    private int i = -1;
    private final TrackDBhelper trackDBhelper = new TrackDBhelper(this);
    private final Cursor cursor = trackDBhelper.fetchAllListOrderBYDec();
    private final TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.running));
        progressDialog.show();
        progressDialog.setCancelable(false);
        setContentView(R.layout.activity_track);
        textView = (TextView)findViewById(R.id.trackTitle);
        listView = (ListView)findViewById(R.id.trackListview);

        trackDBhelper.open();
    try {
        if (cursor.getCount() == 0) {
            textView.setText(getString(R.string.donot_save));
            progressDialog.cancel();
            trackDBhelper.close();
            cursor.close();
        } else {
            while (!cursor.isAfterLast()) {
                //TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                listView.setAdapter(trackAdapter);
                i++;
                cursor.moveToNext();
            }
            textView.setText(getString(R.string.save_how_many) + " " + i);
            trackDBhelper.close();
            cursor.close();
            progressDialog.cancel();
        }
    }catch(Exception e){
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }finally {
        if(cursor!=null)
            cursor.close();
    }
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(TrackActivity.this);
                alertDialog.setTitle(getString(R.string.category));
                alertDialog.setItems(new CharSequence[]{getString(R.string.remove), getString(R.string.show_map), getString(R.string.about_pageSHARE), getString(R.string.close)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:try {
                                        Cursor cursor1 = (Cursor) trackAdapter.getItem(position);
                                        int index = cursor1.getInt(cursor1.getColumnIndex(TrackDBhelper.KEY_ROWID));

                                        trackDBhelper.removeList(index);
                                        trackAdapter.changeCursor(cursor);
                                        listView.setAdapter(trackAdapter);

                                        textView.setText(getString(R.string.save_how_many) + " " + (--i));
                                        Toast.makeText(getApplicationContext(), getString(R.string.remove_category), Toast.LENGTH_SHORT).show();
                                        cursor1.close();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), getString(R.string.error_default)+e, Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                    case 1:
                                        try {
                                            Cursor cursor1 = (Cursor) trackAdapter.getItem(position);
                                            String startTime = cursor1.getString(cursor1.getColumnIndex(TrackDBhelper.KEY_START_TIME));
                                            String endTime = cursor1.getString(cursor1.getColumnIndex(TrackDBhelper.KEY_END_TIME));

                                            Intent intent = new Intent(TrackActivity.this, MapActivity.class);
                                            intent.putExtra("startTime", startTime);
                                            intent.putExtra("endTime", endTime);
                                            startActivity(intent);

                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), getString(R.string.error_default)+e, Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 2:
                                        try {
                                            Cursor cursor1 = (Cursor) trackAdapter.getItem(position);
                                            String shareStartTime = cursor1.getString(cursor1.getColumnIndex(TrackDBhelper.KEY_START_TIME));
                                            String shareEndTime = cursor1.getString(cursor1.getColumnIndex(TrackDBhelper.KEY_END_TIME));
                                            double shareDistance = cursor1.getDouble(cursor1.getColumnIndex(TrackDBhelper.KEY_DISTANCE));
                                            double shareCalorie = cursor1.getDouble(cursor1.getColumnIndex(TrackDBhelper.KEY_CALORIE));

                                            Intent intents = new Intent(Intent.ACTION_SEND);
                                            intents.setType("text/plain");
                                            intents.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_pageSHARE));
                                            intents.putExtra(Intent.EXTRA_TEXT, getString(R.string.startTime) +" "+shareStartTime +"\n"+ getString(R.string.endTime) +" "+
                                                    shareEndTime + "\n"+ getString(R.string.distance)+ " " + String.valueOf(Math.ceil(shareDistance)) + "\n" + getString(R.string.calorie)+ " " + String.valueOf(Math.ceil(shareCalorie)));
                                            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(Intent.createChooser(intents, getTitle()));
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), getString(R.string.error_default)+e, Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 3:
                                        dialog.cancel();
                                        break;
                                }
                            }
                        });
                alertDialog.create().show();
                return false;
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (cursor!=null){
            cursor.close();
        }
    }
}
