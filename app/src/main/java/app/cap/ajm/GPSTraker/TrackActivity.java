package app.cap.ajm.GPSTraker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.Toast;


import java.util.ArrayList;

import app.cap.ajm.Adapter.TrackAdapter;
import app.cap.ajm.MainActivity;
import app.cap.ajm.R;

public class TrackActivity extends AppCompatActivity {
    private TextView textView;
    private ListView listView;
    private ArrayList<TrackConstans>trackConstanses;
    private ArrayList<TrackPoint>trackPoints;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("동기화중...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        setContentView(R.layout.activity_track);
        textView = (TextView)findViewById(R.id.trackTitle);
        listView = (ListView)findViewById(R.id.trackListview);
        TrackDBhelper trackDBhelper = new TrackDBhelper(this);
        trackDBhelper.open();
        Cursor cursor = trackDBhelper.fetchAllListOrderBYDec();
        if (cursor.getCount()==0){
            textView.setText("저장 된 기록이 없습니다.");
            progressDialog.cancel();
            trackDBhelper.close();
        }
        else {
            try {
                int i = -1;
                while (!cursor.isAfterLast()) {
                    TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                    listView.setAdapter(trackAdapter);
                    i++;
                    cursor.moveToNext();
                }

                textView.setText("저장된 기록 : 총 "+ i +" 개" );
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
                alertDialog.setTitle("목록 삭제");
                alertDialog.setMessage("해당 목록을 삭제하시겠습니까?");
                alertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        try {
                            TrackDBhelper trackDBhelper1 = new TrackDBhelper(getApplicationContext());
                            trackDBhelper1.open();
                            Cursor cursor = trackDBhelper1.fetchAllListOrderBYDec();
                            TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                            Cursor cursor1 = (Cursor)trackAdapter.getItem(position);
                            Log.w("TRACK_POSITION:", String.valueOf(position));
                            int index = cursor1.getInt(cursor1.getColumnIndex(TrackDBhelper.KEY_ROWID));
                            trackDBhelper1.removeList(index);
                            trackDBhelper1.close();
                            listView.setAdapter(trackAdapter);
                            Toast.makeText(getApplicationContext(),"기록이 삭제 되었습니다.",Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
                return false;
            }
        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
