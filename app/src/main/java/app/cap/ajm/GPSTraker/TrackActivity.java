package app.cap.ajm.GPSTraker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("동기화중...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        setContentView(R.layout.activity_track);
        final TrackDBhelper trackDBhelper = new TrackDBhelper(this);
        listView = (ListView)findViewById(R.id.trackListview);
        textView = (TextView)findViewById(R.id.trackTitle);
        trackDBhelper.open();
        Cursor cursor = trackDBhelper.fetchAllList();
        if (cursor.getCount()==0){
            textView.setText("저장 된 기록이 없습니다.");
            progressDialog.cancel();
            trackDBhelper.close();
        }
        else {
        cursor = trackDBhelper.fetchAllList();
        int i = 0;
        while(!cursor.isAfterLast()){
            TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
            listView.setAdapter(trackAdapter);
            i++;
            cursor.moveToNext();
            }
            cursor.close();
            trackDBhelper.close();
            textView.setText("저장된 기록 : 총 "+ i +" 개" );
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

    public class ProgressDlg extends AsyncTask<Integer, String, Integer> {
        private ProgressDialog pDlg;
        private Context con;
        public ProgressDlg(Context context) {
            con = context;
        }
        @Override
        protected void onPreExecute(){
            pDlg = new ProgressDialog(con);
            pDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDlg.setMessage("동기화중...");
            pDlg.show();
            super.onPreExecute();
        }
        @Override
        protected Integer doInBackground(Integer...params) {
            final int taskCnt = params[0];
            publishProgress("max", Integer.toString(taskCnt));
            for (int i = 0; i < taskCnt; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                publishProgress("progress", Integer.toString(i), "Task" + Integer.toString(i)+ "nuber");
            }
            return taskCnt;
        }
        @Override
        protected void onProgressUpdate(String...values){
        if (values[0].equals("progress")){
            pDlg.setProgress(Integer.parseInt(values[1]));
            pDlg.setMessage(values[2]);
        }else if(values[0].equals("max")){
            pDlg.setMax(Integer.parseInt(values[1]));
        }
        }
        @Override
        protected void onPostExecute(Integer integer){
            pDlg.dismiss();
            Toast.makeText(con, Integer.toString(integer)+ "total sum", Toast.LENGTH_SHORT).show();
        }
    }
}
