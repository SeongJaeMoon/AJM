package app.cap.ajm.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import app.cap.ajm.Model.TrackPoint;

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
                    +KEY_CALORIE+ " REAL,"
                    +KEY_DISTANCE+" REAL,"
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

    private List<TrackPoint> trackPoints;
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
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

    //종료할 때 출발 주소<>시간, 도착 주소<>시간, 평속, 칼로리, 거리, 온도, 습도 저장
    public long trackDBallFetch(String startAddr, String endAddr, String startTime, String endTime, String avgSpeed, double calroie, double distance, String temp, String wet){
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

    //출발 시간 between 도착 시간 사이에 있는 값 다가져오기
    public List<TrackPoint> fetchBetweenTime(String start, String end){
        trackPoints = new ArrayList<>();
        mDbHelper = new DatabaseHelper(mCtx);
        mDb =  mDbHelper.getReadableDatabase();
        Cursor res = mDb.rawQuery("select * from " + DATABASE_TABLE_MAP+" where "+ KEY_RUNNING_TIME +" between "+"'"+start+"'"+" and "+"'"+end+"'", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            double lat = res.getDouble(res.getColumnIndex(KEY_COORDINATE_X));
            double lng = res.getDouble(res.getColumnIndex(KEY_COORDINATE_Y));
            trackPoints.add(new TrackPoint(lat, lng));
            res.moveToNext();
        }
        if (res.getCount()==0){
            return null;
        }
        res.close();
        return trackPoints;
    }

    //_id로 내림차순 정렬하여 모두 가져오기 3, 2, 1- - -.
    public Cursor fetchAllListOrderBYDec() {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        Cursor res =  mDb.rawQuery( "select * from "+ DATABASE_TABLE +" order by "+KEY_ROWID + " desc", null);
        return res;
}

//_id로 값 삭제하기
    public int removeList(int id){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        return mDb.delete(DATABASE_TABLE, "_id = ?",new String[]{String.valueOf(id)});
    }

}
