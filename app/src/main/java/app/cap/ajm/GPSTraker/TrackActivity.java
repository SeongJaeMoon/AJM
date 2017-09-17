package app.cap.ajm.GPSTraker;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.Toast;
import app.cap.ajm.Adapter.TrackAdapter;
import app.cap.ajm.R;

public class TrackActivity extends AppCompatActivity {
    private TextView textView;
    private ListView listView;
    private ProgressDialog progressDialog;
    private int i;
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
                i = -1;
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
                alertDialog.setTitle(getString(R.string.what));
                alertDialog.setMessage(getString(R.string.category));
                alertDialog.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        try {
                            TrackDBhelper trackDBhelper1 = new TrackDBhelper(getApplicationContext());
                            trackDBhelper1.open();
                            Cursor cursor = trackDBhelper1.fetchAllListOrderBYDec();
                            TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                            Cursor cursor1 = (Cursor)trackAdapter.getItem(position);
                            int index = cursor1.getInt(cursor1.getColumnIndex(TrackDBhelper.KEY_ROWID));
                            trackDBhelper1.removeList(index);
                            Cursor newcursor = trackDBhelper1.fetchAllListOrderBYDec();
                            trackAdapter.changeCursor(newcursor);
                            listView.setAdapter(trackAdapter);
                            trackDBhelper1.close();
                            i--;
                            textView.setText(getString(R.string.save_how_many)+" "+ i);
                            Toast.makeText(getApplicationContext(),getString(R.string.remove_category),Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.show_map), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TrackDBhelper trackDBhelper1 = new TrackDBhelper(getApplicationContext());
                                trackDBhelper1.open();
                                Cursor cursor = trackDBhelper1.fetchAllListOrderBYDec();
                                TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                                Cursor cursor1 = (Cursor)trackAdapter.getItem(position);
                                String startTime = cursor1.getString(cursor1.getColumnIndex(TrackDBhelper.KEY_START_TIME));
                                String endTime = cursor1.getString(cursor1.getColumnIndex(TrackDBhelper.KEY_END_TIME));
                                Intent intent = new Intent(TrackActivity.this, MapActivity.class);
                                intent.putExtra("startTime", startTime);
                                intent.putExtra("endTime", endTime);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setNeutralButton(getString(R.string.close), new DialogInterface.OnClickListener() {
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
