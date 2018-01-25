package app.cap.ajm.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SMSDBhelper {
    
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;
    private static final String DATABASE_NAME = "smslist.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "ContactList";
    public static final String COLUMN_CONTACT  = "contact";
    public static final String _ID = "id";
    private final Context mCtx;

    private static final String DATABASE_CREATE =
        "CREATE TABLE " +
            TABLE_NAME + "("+
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +","+
            COLUMN_CONTACT + " TEXT NOT NULL" + ");";
    
    public SMSDBhelper(Context context){
               this.mCtx =context;
    }

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
        }
    }

       public SMSDBhelper open() throws SQLException {
        mDbHelper = new SMSDBhelper.DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public void addNewContact(String contact){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CONTACT,contact);
        mDb.insert(TABLE_NAME,null,cv);
    }

    public void removeContact(String contact){
        mDb.delete(TABLE_NAME, "contact"+"=?",new String[]{contact});
    }

    public Cursor getAllContacts(){
        return mDb.query(TABLE_NAME,null,null,null,null,null,COLUMN_CONTACT);
    }

}
