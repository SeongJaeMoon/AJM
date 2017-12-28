package app.cap.ajm.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.cap.ajm.GPSTraker.TrackDBhelper;
import app.cap.ajm.GPSTraker.TrackPoint;
import app.cap.ajm.Weather;

public class WeatherDBHelper {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_FOR_VALUE = "key"; // key
    public static final String KEY_CONTENT_NAME = "value";  //value

    private static final String TAG = WeatherDBHelper.class.getSimpleName();

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "weathers" ;//weather DB
    private static final String DATABASE_TABLE = "weatherTable";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE =
            "CREATE TABLE "+ DATABASE_TABLE +" ("
                    +KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +KEY_FOR_VALUE+" TEXT,"
                    +KEY_CONTENT_NAME+" TEXT"+");";

    private final Context ctx;
    private List<app.cap.ajm.Weather> weathers;
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS lists");
            onCreate(db);
        }

        //최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~onCreate~~~~~~~~~~~~~~~~~~~~~~~~~");
            db.execSQL(DATABASE_CREATE);
        }
    }

    public WeatherDBHelper(Context ctx){
        this.ctx = ctx;
    }

    public WeatherDBHelper open() throws SQLException{
        mDbHelper = new DatabaseHelper(ctx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDbHelper.close();
    }

    public long createList(String key, String value) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FOR_VALUE, key);
        initialValues.put(KEY_CONTENT_NAME, value);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public List<app.cap.ajm.Weather> fetchForList(){
         weathers = new ArrayList<>();
        mDbHelper = new WeatherDBHelper.DatabaseHelper(ctx);
        mDb =  mDbHelper.getReadableDatabase();
        Cursor res = mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FOR_VALUE, KEY_CONTENT_NAME}, null, null, null, null, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String key = res.getString(res.getColumnIndex(KEY_FOR_VALUE));
            String value = res.getString(res.getColumnIndex(KEY_CONTENT_NAME));
            weathers.add(new Weather(key, value));
            res.moveToNext();
        }
        if (res.getCount()==0){
            return null;
        }
        res.close();
        return weathers;
    }


    //모든 레코드 반환
    public Cursor fetchAllList() {
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_FOR_VALUE, KEY_CONTENT_NAME}, null, null, null, null,null);
    }
}
