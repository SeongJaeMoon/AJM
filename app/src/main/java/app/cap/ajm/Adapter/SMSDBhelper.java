package app.cap.ajm.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SMSDBhelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smslist.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "ContactList";
    public static final String COLUMN_CONTACT  = "contact";
    public static final String _ID = "id";

    public SMSDBhelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + "("+
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +","+
                COLUMN_CONTACT + " TEXT NOT NULL" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w("onUpgrading!!!!!", "Upgrading database from version " + i + " to " + i1
                + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ContactList");
        onCreate(sqLiteDatabase);
        Log.i("DB","onUpgrade DB SMS!!!!!!!!!!!!!!");
    }
}
