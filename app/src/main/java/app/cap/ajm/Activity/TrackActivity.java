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
    private ProgressDialog progressDialog;
    private int i = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.running));
        progressDialog.show();
        progressDialog.setCancelable(false);
        setContentView(R.layout.activity_track);
        textView = (TextView)findViewById(R.id.trackTitle);
        listView = (ListView)findViewById(R.id.trackListview);
        final TrackDBhelper trackDBhelper = new TrackDBhelper(this);
        trackDBhelper.open();
        Cursor cursor = trackDBhelper.fetchAllListOrderBYDec();
        if (cursor.getCount()==0){
            textView.setText(getString(R.string.donot_save));
            progressDialog.cancel();
            trackDBhelper.close();
        }
        else {
            try {
                while (!cursor.isAfterLast()) {
                    TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                    listView.setAdapter(trackAdapter);
                    i++;
                    cursor.moveToNext();
                }
                textView.setText(getString(R.string.save_how_many)+" "+ i);
                trackDBhelper.close();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
            progressDialog.cancel();
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
                                        TrackDBhelper trackDBhelper = new TrackDBhelper(getApplicationContext());
                                        trackDBhelper.open();
                                        Cursor cursor = trackDBhelper.fetchAllListOrderBYDec();
                                        TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                                        Cursor cursor1 = (Cursor) trackAdapter.getItem(position);
                                        int index = cursor1.getInt(cursor1.getColumnIndex(TrackDBhelper.KEY_ROWID));
                                        trackDBhelper.removeList(index);
                                        Cursor newcursor = trackDBhelper.fetchAllListOrderBYDec();
                                        trackAdapter.changeCursor(newcursor);
                                        listView.setAdapter(trackAdapter);
                                        trackDBhelper.close();
                                        i--;
                                        textView.setText(getString(R.string.save_how_many) + " " + i);
                                        Toast.makeText(getApplicationContext(), getString(R.string.remove_category), Toast.LENGTH_SHORT).show();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), getString(R.string.error_default)+e, Toast.LENGTH_SHORT).show();
                                    }
                                        break;
                                    case 1:
                                        try {
                                            TrackDBhelper trackDBhelper1 = new TrackDBhelper(getApplicationContext());
                                            trackDBhelper1.open();
                                            Cursor cursor2 = trackDBhelper1.fetchAllListOrderBYDec();
                                            TrackAdapter trackAdapter1 = new TrackAdapter(getApplicationContext(), cursor2);
                                            Cursor cursor3 = (Cursor) trackAdapter1.getItem(position);
                                            String startTime = cursor3.getString(cursor3.getColumnIndex(TrackDBhelper.KEY_START_TIME));
                                            String endTime = cursor3.getString(cursor3.getColumnIndex(TrackDBhelper.KEY_END_TIME));
                                            Intent intent = new Intent(TrackActivity.this, MapActivity.class);
                                            intent.putExtra("startTime", startTime);
                                            intent.putExtra("endTime", endTime);
                                            startActivity(intent);
                                            trackDBhelper1.close();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), getString(R.string.error_default)+e, Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case 2:
                                        try {
                                            TrackDBhelper trackDBhelper2 = new TrackDBhelper(getApplicationContext());
                                            trackDBhelper2.open();
                                            Cursor cursor4 = trackDBhelper2.fetchAllListOrderBYDec();
                                            TrackAdapter trackAdapter2 = new TrackAdapter(getApplicationContext(), cursor4);
                                            Cursor cursor5 = (Cursor) trackAdapter2.getItem(position);
                                            String shareStartTime = cursor5.getString(cursor5.getColumnIndex(TrackDBhelper.KEY_START_TIME));
                                            String shareEndTime = cursor5.getString(cursor5.getColumnIndex(TrackDBhelper.KEY_END_TIME));
                                            double shareDistance = cursor5.getDouble(cursor5.getColumnIndex(TrackDBhelper.KEY_DISTANCE));
                                            double shareCalorie = cursor5.getDouble(cursor5.getColumnIndex(TrackDBhelper.KEY_CALORIE));
                                            Intent intents = new Intent(Intent.ACTION_SEND);
                                            intents.setType("text/plain");
                                            intents.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.about_pageSHARE));
                                            intents.putExtra(Intent.EXTRA_TEXT, getString(R.string.startTime) +" "+shareStartTime +"\n"+getString(R.string.endTime) +" "+ shareEndTime + "\n"+ getString(R.string.distance)+ " " + String.valueOf(Math.ceil(shareDistance)) + "\n" + getString(R.string.calorie)+ " " + String.valueOf(Math.ceil(shareCalorie)));
                                            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(Intent.createChooser(intents, getTitle()));
                                            trackDBhelper2.close();
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
    }
}
