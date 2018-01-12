package app.cap.ajm.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//직각교차로, 사고다발지역, 어린이보호구역, 보행자전용도로
public class ListAdapter{

    public static final String KEY_ROWID = "_id";
    public static final String KEY_CONTENT_NAME = "content_nm";
    public static final String KEY_COORDINATE_X = "coor_x";
    public static final String KEY_COORDINATE_Y = "coor_y";

    private static final String TAG = ListAdapter.class.getSimpleName();
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "forefirebase";
    private static final String DATABASE_TABLE = "lists";
    private static final int DATABASE_VERSION = 5;

    private static final String DATABASE_CREATE =
            "CREATE TABLE "+ DATABASE_TABLE +" ("
                    +KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +KEY_CONTENT_NAME+" TEXT,"
                    +KEY_COORDINATE_X+" TEXT,"
                    +KEY_COORDINATE_Y+" TEXT"+");";

    private final Context mCtx;

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

    public ListAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public ListAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }



    //레코드 생성
    public long createList(String content_list, String list_x, String list_y) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_CONTENT_NAME, content_list);
        initialValues.put(KEY_COORDINATE_X, list_x);
        initialValues.put(KEY_COORDINATE_Y, list_y);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }


    //모든 레코드 반환
    public Cursor fetchAllList() {
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_CONTENT_NAME, KEY_COORDINATE_X,
                KEY_COORDINATE_Y}, null, null, null, null,null);
    }
}
