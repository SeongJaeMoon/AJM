package app.cap.ajm;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class AJMapp extends MultiDexApplication{
    public String startAddr;
    public String startTime;
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        mContext = getApplicationContext();
    }
    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public void setStartAddr(String startAddr){
        this.startAddr = startAddr;
    }
    public String getStartAddr(){
        return startAddr;
    }
    public void setStartTime(String startTime){this.startTime = startTime;}
    public String getStartTime(){
        return startTime;
    }
    public static Context getContext() {
        return mContext;
    }
}
