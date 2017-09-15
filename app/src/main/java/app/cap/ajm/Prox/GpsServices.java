package app.cap.ajm.Prox;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.cap.ajm.Data;
import app.cap.ajm.GPSTraker.TrackDBhelper;
import app.cap.ajm.MainActivity;
import app.cap.ajm.R;

public class GpsServices extends Service implements LocationListener, TextToSpeech.OnInitListener {

    private static String TAG = GpsServices.class.getSimpleName();
    private LocationManager mLocationManager;
    private SharedPreferences sharedPreferences;
    Location lastlocation = new Location("last");
    Data data;
    private String gpsValue;
    double currentLon = 0;
    double currentLat = 0;
    double lastLon = 0;
    double lastLat = 0;
    private String weight;
    private String isOn;
    PendingIntent contentIntent;
    Context context;
    private DatabaseReference ref;
    private GeoFire geoFire;
    public static TextToSpeech mTTS;
    private TrackDBhelper trackDBhelper;
    private Query query;
    @Override
    public void onCreate() {
        //mProgressDialog = new ProgressDialog(this);
        //mProgressDialog.setTitle("준비중..");
        //mProgressDialog.setMessage("로딩 준비 중...");
        //mProgressDialog.setCancelable(false);
        ref = FirebaseDatabase.getInstance().getReference();
        geoFire = new GeoFire(ref);
        Intent checkTTSIntent = new Intent();
        trackDBhelper = new TrackDBhelper(this);
        trackDBhelper.open();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mTTS = new TextToSpeech(this, this);
        onInit(mTTS.setLanguage(Locale.KOREA));
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        updateNotification(false);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&& ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            //if (mLocationManager!=null){
            //    mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //    Log.w("GPS", "LastKnown "+String.valueOf(mLocationManager));
            //}
            gpsValue = sharedPreferences.getString("gps_level", "default");
            switch (gpsValue) {
                case "default":
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                    Log.w("GPS", "default" + "****");
                    break;
                case "high":
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);
                    break;
                case "low":
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7000, 0, this);
                    break;
                case "middle":
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, this);
                    break;
            }
        }else if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)&&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED){
            //if (mLocationManager!=null){
            //    mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //    Log.w("Network", "LastKnown "+String.valueOf(mLocationManager));
            //}
            gpsValue = sharedPreferences.getString("gps_level", "default");
            switch (gpsValue) {
                case "default":
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                    Log.w("NETWORK", "default" + "****");
                    break;
                case "high":
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, this);
                    break;
                case "low":
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 7000, 0, this);
                    break;
                case "middle":
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, this);
                    break;
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.v("Timetask", "onInit");
            int result = mTTS.setLanguage(Locale.KOREA);
            if (result == TextToSpeech.LANG_NOT_SUPPORTED ||
                    result == TextToSpeech.LANG_MISSING_DATA) {
                Log.v("onInit", "언어를 지원하지 않는다!!");
            } else if (mTTS.isLanguageAvailable(Locale.KOREA) == TextToSpeech.LANG_AVAILABLE) {
                mTTS.setLanguage(Locale.KOREA);
            }
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(GpsServices.this, "TTS를 사용할 수 없습니다...", Toast.LENGTH_LONG).show();
            Intent installTTSIntent = new Intent();
            installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installTTSIntent);
        }
    }

    private void speakword(String str) {
        mTTS.speak(str, TextToSpeech.LANG_COUNTRY_AVAILABLE, null, null);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w(TAG, "onLocationChanged");
        data = MainActivity.getData();
        if (data.isRunning()) {
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();

            if (data.isFirstTime()) {
                lastLat = currentLat;
                lastLon = currentLon;
                data.setFirstTime(false);
            }

            lastlocation.setLatitude(lastLat);
            lastlocation.setLongitude(lastLon);
            double distance = lastlocation.distanceTo(location);

            if (location.getAccuracy() < distance) {
                data.addDistance(distance);

                lastLat = currentLat;
                lastLon = currentLon;
            }

            if (location.hasSpeed()) {
                data.setCurSpeed(location.getSpeed() * 3.6);
                if (location.getSpeed() == 0) {
                    new isStillStopped().execute();
                }
            }
            weight = sharedPreferences.getString("weight_value", "0");
            int to = Integer.parseInt(weight);
            if (to != 0){
                data.setCalorie(to);
                data.addCalorie(data.getCalorie());
            }
            Log.w(TAG, "addCalorie: "+ data.getCalorie());
            if (sharedPreferences.getBoolean("route", false)){
                try {
                    trackDBhelper.trackDBlocationRunning(getCurrentSec(),lastLat, lastLon);
                    Log.w(TAG, "RunningTime&lat,lng: " + getCurrentSec() + ", "+ lastLat+", "+ lastLon);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            data.update();
            updateNotification(true);
            if(sharedPreferences.getBoolean("alert",false)){
                getLibrary();
            }
        }
    }

    public void updateNotification(boolean asData) {
        Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setContentTitle(getString(R.string.running))
                .setSmallIcon(R.drawable.mainlogo2)
                .setContentIntent(contentIntent);

        if (asData) {
            builder.setContentText(String.format(getString(R.string.notification), data.getCalorieMeter(), data.getDistance()));
        } else {
            builder.setContentText(String.format(getString(R.string.notification), '-', '-'));
        }
        Notification notification = builder.build();
        startForeground(R.string.noti_id, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.w(TAG, "START_STICKY");
        context = getApplicationContext();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(this);
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
            mTTS = null;
        }
        if (sharedPreferences.getBoolean("route", false)) {
            try {
                trackDBhelper.trackDBlocationStop(getCurrentDateTime(), lastLat, lastLon);
                trackDBhelper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //stopSelf();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    class isStillStopped extends AsyncTask<Void, Integer, String> {
        int timer = 0;

        @Override
        protected String doInBackground(Void... unused) {
            try {
                while (data.getCurSpeed() == 0) {
                    Thread.sleep(1000);
                    timer++;
                }
            } catch (InterruptedException t) {
                return ("The sleep operation failed");
            }
            return ("return object when task is finished");
        }

        @Override
        protected void onPostExecute(String message) {
            data.setTimeStopped(timer);
        }
    }

    //geofire
     public void getLibrary(){
         try {
             ref = FirebaseDatabase.getInstance().getReference();
             DatabaseReference refs = ref.child("geofire");
             final DatabaseReference refs2 = ref.child("name");
             //Log.w(TAG, String.valueOf(refs));
             geoFire = new GeoFire(refs);
                         //geoquery 객체 생성 0.02 --> 20m 반경
                         GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lastLat, lastLon), 0.02);
                            Log.w("GEOQUERY 내위치!"+": ", lastLat+" , "+lastLon);
                         geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                             //Firebase Key값에서 Location을 찾았을 경우 발생
                             @Override
                             public void onKeyEntered(String key, GeoLocation location) {
                                 //Firebase location/key/name -> 위치정보이름
                                 query = ref.child("location").child(key).child("name");
                                 query.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(DataSnapshot dataSnapshot) {
                                         if (dataSnapshot.exists()){
                                             try {
                                                 //Firebase에서 위치 정보 가져오기
                                                 String content = dataSnapshot.getValue(String.class);
                                                 if (!mTTS.isSpeaking())
                                                 speakword("전방에 "+content+ "입니다. 주의하세요.");
                                                 //Toast.makeText(getApplicationContext(), content + " 에 접근중입니다. 주의하세요.", Toast.LENGTH_SHORT).show();
                                                 Log.w(TAG,"content: "+ String.valueOf(content));

                                             }catch (Exception e){
                                                 e.printStackTrace();
                                             }
                                         }
                                     }
                                     @Override
                                     public void onCancelled(DatabaseError databaseError) {
                                         System.out.println("DatabaseError: " + databaseError);
                                     }
                                 });
                             }

                             @Override
                             public void onKeyExited(String key) {
                                    query = ref.child("location").child(key).child("name");
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(DataSnapshot dataSnapshot) {
                                         if (dataSnapshot.exists()){
                                             try {
                                                 //Firebase에서 위치 정보 가져오기
                                                 String content = dataSnapshot.getValue(String.class);
                                                 Toast.makeText(getApplicationContext(), content + "에서 벗어납니다.", Toast.LENGTH_SHORT).show();
                                                 Log.w(TAG,"content: "+ String.valueOf(content));

                                             }catch (Exception e){
                                                 e.printStackTrace();
                                             }
                                         }
                                     }
                                     @Override
                                     public void onCancelled(DatabaseError databaseError) {
                                         System.out.println("DatabaseError: " + databaseError);
                                     }
                                 });
                             }

                             @Override
                             public void onKeyMoved(String key, GeoLocation location){
                                 query = ref.child("location").child(key).child("name");
                                 query.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(DataSnapshot dataSnapshot) {
                                         if (dataSnapshot.exists()){
                                             try {
                                                 //Firebase에서 위치 정보 가져오기
                                                 String content = dataSnapshot.getValue(String.class);
                                                 Toast.makeText(getApplicationContext(), content + "에 진입하였습니다.", Toast.LENGTH_SHORT).show();
                                                 Log.w(TAG,"content: "+ String.valueOf(content));
                                             }catch (Exception e){
                                                 e.printStackTrace();
                                             }
                                         }
                                     }
                                     @Override
                                     public void onCancelled(DatabaseError databaseError) {
                                         System.out.println("DatabaseError: " + databaseError);
                                     }
                                 });
                             }

                             @Override
                             public void onGeoQueryReady() {
                                 Log.w(TAG, "The Query is Ready");
                             }

                             @Override
                             public void onGeoQueryError(DatabaseError error) {
                                 System.out.println("Database Error: " + error);
                             }
                         });
                     }catch (Exception e){
             e.printStackTrace();
         }
     }

    public String getCurrentDateTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);
        String getTime = sdf.format(date);
        return getTime;
    }

    public String getCurrentSec(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String getTime = sdf.format(date);
        return getTime;
    }
}

