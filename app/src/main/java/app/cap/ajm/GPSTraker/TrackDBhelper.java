package app.cap.ajm.GPSTraker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import app.cap.ajm.Prox.SMSContact;

public class TrackDBhelper{

    private static final String TAG ="TrackDBhelper";

    public static final String KEY_ROWID = "_id";
    public static final String KEY_START_ADDR = "startAddr";
    public static final String KEY_END_ADDR = "endAddr";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_END_TIME = "endTime";
    public static final String KEY_AVG_SPEED = "avgSpeed";
    public static final String KEY_CALORIE = "calorie";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_TEMP = "temp";
    public static final String KEY_WET = "wet";
    public static final String KEY_RUNNING_TIME = "runtime";
    public static final String KEY_COORDINATE_X = "_x";
    public static final String KEY_COORDINATE_Y = "_y";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "trackdb";
    private static final String DATABASE_TABLE = "track";
    private static final String DATABASE_TABLE_MAP = "map";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "CREATE TABLE "+ DATABASE_TABLE +" ("
                    +KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +KEY_START_ADDR+" TEXT,"
                    +KEY_START_TIME+" TEXT,"
                    +KEY_END_ADDR+" TEXT,"
                    +KEY_END_TIME+" TEXT,"
                    +KEY_AVG_SPEED+" TEXT,"
                    +KEY_CALORIE+ " TEXT,"
                    +KEY_DISTANCE+" TEXT,"
                    +KEY_TEMP+" TEXT,"
                    +KEY_WET+" TEXT"+ ");";

    private static final String DATABASE_CREATE_START =
            "CREATE TABLE "+ DATABASE_TABLE +" ("
                    +KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +KEY_START_ADDR+" TEXT,"
                    +KEY_START_TIME+" TEXT," + ");";

    private static final String DATABASE_CREATE_END =
            "CREATE TABLE "+ DATABASE_TABLE +" ("
                    +KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +KEY_END_ADDR+" TEXT,"
                    +KEY_END_TIME+" TEXT,"
                    +KEY_AVG_SPEED+" TEXT,"
                    +KEY_CALORIE+ " TEXT,"
                    +KEY_DISTANCE+" TEXT,"
                    +KEY_TEMP+" TEXT,"
                    +KEY_WET+" TEXT"+ ");";

    private static final String DATABASE_CREATE_MAP =
            "CREATE TABLE "+ DATABASE_TABLE_MAP+" ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_START_TIME + " TEXT,"
                    + KEY_END_TIME + " TEXT,"
                    + KEY_RUNNING_TIME + " TEXT,"
                    + KEY_COORDINATE_X+" REAL,"
                    + KEY_COORDINATE_Y+" REAL"+ ");";

    private HashMap hp;
    private ArrayList<TrackConstans>tracklist;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
            //        + ", which will destroy all old data");
            //db.execSQL("DROP TABLE IF EXISTS lists");
            //onCreate(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            //db.execSQL(DATABASE_CREATE_END);
            db.execSQL(DATABASE_CREATE_MAP);
        }
    }
    public TrackDBhelper(Context context){
        this.mCtx =context;
    }

    public TrackDBhelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long trackDBallFetch(String startAddr, String endAddr, String startTime, String endTime, String avgSpeed, String calroie, String distance, String temp, String wet){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_START_ADDR, startAddr);
        contentValues.put(KEY_END_ADDR, endAddr);
        contentValues.put(KEY_START_TIME, startTime);
        contentValues.put(KEY_END_TIME, endTime);
        contentValues.put(KEY_AVG_SPEED, avgSpeed);
        contentValues.put(KEY_CALORIE, calroie);
        contentValues.put(KEY_DISTANCE, distance);
        contentValues.put(KEY_TEMP, temp);
        contentValues.put(KEY_WET, wet);
        return mDb.insert(DATABASE_TABLE, null, contentValues);
    }

    //시작할 때 주소, 시간 저장
    public long trackDBstartFetch(String startAddr, String startTime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_START_ADDR, startAddr);
        contentValues.put(KEY_START_TIME, startTime);
        Log.w("TRACKDBHELPER: ", KEY_ROWID);
        return mDb.insert(DATABASE_TABLE, null, contentValues);
    }
    //종료할 때 저장 주소, 시간, 평속, 칼로리, 거리, 온도, 습도 저장
    public long trackDBendFetch(String endAddr,String endTime, String avgSpeed, String calroie, String distance, String temp, String wet){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_END_ADDR, endAddr);
        contentValues.put(KEY_END_TIME, endTime);
        contentValues.put(KEY_AVG_SPEED, avgSpeed);
        contentValues.put(KEY_CALORIE, calroie);
        contentValues.put(KEY_DISTANCE, distance);
        contentValues.put(KEY_TEMP, temp);
        contentValues.put(KEY_WET, wet);
        return mDb.insert(DATABASE_TABLE, null, contentValues);
    }

    //시작할 때 위치, 시간 저장
    public long trackDBlocationStart(String startTime, double lat, double lng){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_START_TIME, startTime);
        contentValues.put(KEY_COORDINATE_X, lat);
        contentValues.put(KEY_COORDINATE_Y, lng);
        return mDb.insert(DATABASE_TABLE_MAP, null, contentValues);
    }
    //종료할 때 위치, 시간 저장
    public long trackDBlocationStop(String endTime, double lat, double lng){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_END_TIME, endTime);
        contentValues.put(KEY_COORDINATE_X, lat);
        contentValues.put(KEY_COORDINATE_Y, lng);
        return mDb.insert(DATABASE_TABLE_MAP, null, contentValues);
    }
    //달리는 중일 때 시간, 위치 저장
    public long trackDBlocationRunning(String time, double lat, double lng){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_RUNNING_TIME, time);
        contentValues.put(KEY_COORDINATE_X,lat);
        contentValues.put(KEY_COORDINATE_Y, lng);
        return mDb.insert(DATABASE_TABLE_MAP, null, contentValues);
    }
    //id로 값 가져오기
    public Cursor getData(int id){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        Cursor res =  mDb.rawQuery( "select * from "+ DATABASE_TABLE +" where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(mDb, DATABASE_TABLE);
        return numRows;
    }

    public ArrayList<TrackConstans>getAllitem(){
        tracklist = new ArrayList<>();
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        Cursor res = mDb.rawQuery("select * from "+DATABASE_TABLE, null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            String stAddr = res.getString(res.getColumnIndex(KEY_START_ADDR));
            String edAddr = res.getString(res.getColumnIndex(KEY_END_ADDR));
            String stTime = res.getString(res.getColumnIndex(KEY_START_TIME));
            String edTime = res.getString(res.getColumnIndex(KEY_END_TIME));
            String avg = res.getString(res.getColumnIndex(KEY_AVG_SPEED));
            String cal = res.getString(res.getColumnIndex(KEY_CALORIE));
            String dis = res.getString(res.getColumnIndex(KEY_DISTANCE));
            String temp = res.getString(res.getColumnIndex(KEY_TEMP));
            String wet = res.getString(res.getColumnIndex(KEY_WET));
            tracklist.add(new TrackConstans(stAddr, edAddr, stTime, edTime,
                    Double.parseDouble(avg), Double.parseDouble(cal), Double.parseDouble(dis),
                    Double.parseDouble(temp), Double.parseDouble(wet)));
            res.moveToNext();
        }
        res.close();
        return tracklist;
    }
    //모든 값 가져오기
    public Cursor fetchAllList() {
        return mDb.query(DATABASE_TABLE,
                new String[]{KEY_ROWID, KEY_START_ADDR, KEY_END_ADDR, KEY_START_TIME, KEY_END_TIME, KEY_AVG_SPEED, KEY_CALORIE, KEY_DISTANCE, KEY_TEMP, KEY_WET},
                null, null, null, null,null);
    }
    //_id로 내림차순 정렬하여 모두 가져오기 3, 2, 1- - -.
    public Cursor fetchAllListOrderBYDec() {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        Cursor res =  mDb.rawQuery( "select * from "+ DATABASE_TABLE +" order by "+KEY_ROWID + " desc", null);
        return res;
}

    public int removeList(int id){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        return mDb.delete(DATABASE_TABLE, "_id = ?",new String[]{String.valueOf(id)});
        //Toast.makeText(getApplicationContext(),"번호가 삭제되었습니다.",Toast.LENGTH_SHORT).show();
    }
    //id로 값 가져오기
    public Cursor fetchByID(int id){
        Cursor cursor = mDb.query(true, DATABASE_TABLE, new String[]{
                KEY_ROWID, KEY_START_ADDR, KEY_END_ADDR, KEY_START_TIME, KEY_END_TIME, KEY_AVG_SPEED, KEY_CALORIE, KEY_DISTANCE
        },id +"= '" + KEY_ROWID+"' ", null,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }
}
