package app.cap.ajm.Service;

import android.annotation.TargetApi;
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
import android.os.Build;
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
import java.util.HashMap;
import java.util.Locale;
import app.cap.ajm.Model.Data;
import app.cap.ajm.Helper.TrackDBhelper;
import app.cap.ajm.Activity.MainActivity;
import app.cap.ajm.R;

public class GPSService extends Service implements LocationListener, TextToSpeech.OnInitListener{

    private static String TAG = GPSService.class.getSimpleName();
    private LocationManager mLocationManager;
    private SharedPreferences sharedPreferences;
    Location lastlocation = new Location("last");
    Data data;
    double currentLon = 0;
    double currentLat = 0;
    double lastLon = 0;
    double lastLat = 0;
    PendingIntent contentIntent;
    Context context;
    private DatabaseReference ref;
    private GeoFire geoFire;
    private TrackDBhelper trackDBhelper;
    private Query query;
    private TextToSpeech tts;
    @Override
    public void onCreate() {
        ref = FirebaseDatabase.getInstance().getReference();
        geoFire = new GeoFire(ref);
        trackDBhelper = new TrackDBhelper(this);
        trackDBhelper.open();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        updateNotification(false);
        tts = new TextToSpeech(this, this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&& ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {

           String gpsValue = sharedPreferences.getString("gps_level", "default");
            switch (gpsValue) {
                case "default":
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
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
           String gpsValue = sharedPreferences.getString("gps_level", "default");
            switch (gpsValue) {
                case "default":
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
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
    public void onLocationChanged(Location location) {
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
           String weight = sharedPreferences.getString("weight_value", "0");
            if (Integer.parseInt(weight) != 0){
                data.addCalorie(Integer.parseInt(weight));
            }
            if (sharedPreferences.getBoolean("route", false)){
                try {
                    trackDBhelper.trackDBlocationRunning(getCurrentSec(),lastLat, lastLon);
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
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
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
                                                 if (!tts.isSpeaking()&&Locale.getDefault().getLanguage().equals("ko")){
                                                     speak("전방에 "+content+"입니다. 주의하세요.");
                                                 }
                                                 else if (!tts.isSpeaking()&&Locale.getDefault().getLanguage().equals("en")){
                                                     switch (content){
                                                         case "직각교차로": speak("Be careful at the intersection point ahead.");
                                                             break;
                                                         case "보행자도로": speak("Be careful at the pedestrian road ahead");
                                                             break;
                                                         case "어린이보호구역": speak("Be careful at the child protection area");
                                                             break;
                                                         case "사고다발지역" : speak("Be careful at the accident prone area");
                                                             break;
                                                         default: speak("Be careful at the test"); //테스트용
                                                             break;
                                                     }
                                                 }
                                                 else if (!tts.isSpeaking()&&Locale.getDefault().getLanguage().equals("ja")){
                                                        switch (content){
                                                            case "직각교차로": speak("前方に交差点です注意してください");
                                                                break;
                                                            case "보행자도로": speak("前方に歩行者道路に注意してください");
                                                                break;
                                                            case "어린이보호구역": speak("前方に子供の保護区域に注意してください");
                                                                break;
                                                            case "사고다발지역" :speak("前方に事故多発地域に注意してくださ//TEST용");
                                                                break;
                                                            default: speak("前方に事故多発地域に注意してください");//TEST용
                                                                break;
                                                        }
                                                 }else if(!tts.isSpeaking()&&Locale.getDefault().getLanguage().equals("zh")){
                                                     switch (content){
                                                         case "직각교차로":speak("小心前方的路口");
                                                             break;
                                                         case "보행자도로":speak("小心行人专用道路");
                                                             break;
                                                         case "어린이보호구역":speak("注意前面的儿童保护区");
                                                             break;
                                                         case "사고다발지역" :speak("在你面前要小心很多事故");
                                                             break;
                                                         default: speak("在你面前要小心很多事故"); //TEST용
                                                             break;
                                                     }
                                                 }
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
                                 speak(getString(R.string.left_alert));
                                Toast.makeText(getApplicationContext(), getString(R.string.left_alert), Toast.LENGTH_SHORT).show();
                             }

                             @Override
                             public void onKeyMoved(String key, GeoLocation location){
                                 Toast.makeText(getApplicationContext(), getString(R.string.moved_alert), Toast.LENGTH_SHORT).show();
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
    public String getCurrentSec(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String getTime = sdf.format(date);
        return getTime;
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS)
        {
            if (tts.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.getDefault());
        }
        else if (status == TextToSpeech.ERROR)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.tts_not_setup), Toast.LENGTH_LONG).show();
        }
    }
    private void speak(String s){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ttsGreater21(s);
        }else{
            ttsUnder20(s);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}

