package app.cap.ajm;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.NavigationView;
import app.cap.ajm.About.AboutActivity;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.noob.noobcameraflash.managers.NoobCameraManager;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.speech.tts.TextToSpeech;
import app.cap.ajm.Compatable.SearchActivity;
import app.cap.ajm.GPSTraker.TrackActivity;
import app.cap.ajm.Map.RouteActivity;
import app.cap.ajm.Prox.GpsServices;
import app.cap.ajm.Prox.TimeTask;
import github.vatsal.easyweather.Helper.ForecastCallback;
import github.vatsal.easyweather.Helper.TempUnitConverter;
import github.vatsal.easyweather.Helper.WeatherCallback;
import github.vatsal.easyweather.WeatherMap;
import github.vatsal.easyweather.retrofit.models.ForecastResponseModel;
import github.vatsal.easyweather.retrofit.models.WeatherResponseModel;
import butterknife.Bind;
import butterknife.ButterKnife;
import app.cap.ajm.Adapter.TrackAdapter;
import app.cap.ajm.GPSTraker.TrackDBhelper;

public class MainActivity extends AppCompatActivity implements LocationListener, TextToSpeech.OnInitListener {
    public final String weather_id = BuildConfig.OWM_API_KEY;
    private final String weather_city = "seoul";
    private BackPressCloseHandler backPressCloseHandler;
    private SharedPreferences sharedPreferences;
    private LocationManager mLocationManager;
    private static Data data;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private RecyclerView mainRecyclerView;
    private Boolean fboolean = true;
    private View views;
    Location lastlocation = new Location("last");
    private FloatingActionButton fab;
    private FloatingActionButton refresh;
    private FloatingActionButton holder;
    private ProgressBarCircularIndeterminate progressBarCircularIndeterminate;
    public static Context context;
    private TextView accuracy;
    private TextView currentSpeed;
    private TextView maxSpeed;
    private TextView averageSpeed;
    private TextView distance;
    private TextView calorie;
    private int trees;
    Handler handler;
    private Chronometer time;
    private Data.onGpsServiceUpdate onGpsServiceUpdate;
    private boolean firstfix;
    private TextToSpeech tts;
    private boolean hasFlash;
    private boolean turnFlash;
    private String gpsValue;
    private boolean ishold;
    private SoundPool soundPool;
    private boolean isSound = false;
    double lat, lng;
    @Bind(R.id.weather2)
    TextView weather2;
    @Bind(R.id.weather5)
    TextView weather5;
    private int MY_DATA_CHECK_CODE = 0;
    private String startAddr, startTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //!!혹시 재 사용 할지도 모르는 코드들 바로 여기에 모아두기!!
        //updateNotification(false);
        //soundIds = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        //final MediaPlayer mp = MediaPlayer.create(this, R.raw.bikebell);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SEND_SMS,
                        Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }
        }
        handler = new Handler();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        views = getWindow().getDecorView();
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        turnFlash = false;
        NoobCameraManager.getInstance().init(this);
        backPressCloseHandler = new BackPressCloseHandler(this);
        ButterKnife.bind(this);
        data = new Data(onGpsServiceUpdate);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.mainNavigationView);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button phonecall = (Button) findViewById(R.id.phonecall);
        holder = (FloatingActionButton) findViewById(R.id.holder);
        mainRecyclerView = (RecyclerView) findViewById(R.id.xrvMainRecyclerView);
        refresh = (FloatingActionButton) findViewById(R.id.refresh);
        refresh.setVisibility(View.INVISIBLE);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        //자동 서비스 꺼짐 선택이 되지 않았을 경우 && 서비스가 실행 중일 경우
        if (!sharedPreferences.getBoolean("autoservice",false)&&isServiceRunning(GpsServices.class)){
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_pause));
        }else {
            fab.setVisibility(View.INVISIBLE);
        }
        ishold = false;
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        //getAppKeyHash();
                    final String starts = getIntent().getStringExtra("start");
                    if (starts!=null && starts.equals("start")) {
                        try {
                            Toast.makeText(getApplicationContext(),getString(R.string.ajm_support), Toast.LENGTH_LONG).show();
                            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                            {
                                drawerLayout.openDrawer(GravityCompat.START);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        onGpsServiceUpdate = new Data.onGpsServiceUpdate() {
            @Override
            public void update() {
                maxSpeed.setText(data.getMaxSpeed());
                distance.setText(data.getDistance());
                calorie.setText(data.getCalorieMeter());
                if (sharedPreferences.getBoolean("auto_average", false)) {
                    averageSpeed.setText(data.getAverageSpeedMotion());
                } else {
                    averageSpeed.setText(data.getAverageSpeed());
                }
            }
        };


        accuracy = (TextView) findViewById(R.id.accuracy);
        maxSpeed = (TextView) findViewById(R.id.maxSpeed);
        averageSpeed = (TextView) findViewById(R.id.averageSpeed);
        distance = (TextView) findViewById(R.id.distance);
        time = (Chronometer) findViewById(R.id.time);
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        progressBarCircularIndeterminate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndeterminate);
        calorie = (TextView) findViewById(R.id.calorie);

        time.setText("00:00:00");
        time.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            boolean isPair = true;

            @Override
            public void onChronometerTick(Chronometer chrono) {
                long time;
                if (data.isRunning()) {
                    time = SystemClock.elapsedRealtime() - chrono.getBase();
                    data.setTime(time);
                } else {
                    time = data.getTime();
                }
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                chrono.setText(hh + ":" + mm + ":" + ss);

                if (data.isRunning()) {
                    chrono.setText(hh + ":" + mm + ":" + ss);
                } else {
                    if (isPair) {
                        isPair = false;
                        chrono.setText(hh + ":" + mm + ":" + ss);
                    } else {
                        isPair = true;
                        chrono.setText("");
                    }
                }

            }
        });

        currentSpeed.setOnTouchListener(new View.OnTouchListener() {
            int rawsound = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.w("MAIN:", "ActionDown");
                        try {
                            rawsound = R.raw.bikebell;
                            /*//rawsound = R.raw.siren;
                            Log.w("MAIN: ", "Sound "+String.valueOf(rawsound));
                            if (!isSound&&!ishold){
                                Log.w("Main: ", " !isSound");
                            soundPool.load(MainActivity.this, rawsound, 1);
                            isSound = true;
                            }
                            else*/ if(isSound&&!ishold){
                                soundPool.play(rawsound, 1f, 1f, 0, 0, 1f);
                                Log.w("Main: ", "soundPlay");
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        return false;
                }
                return false;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_direction:
                        startActivity(new Intent(MainActivity.this, RouteActivity.class));
                        Toast.makeText(getApplicationContext(), getString(R.string.mobile_data), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_weather:
                        startActivity(new Intent(MainActivity.this, Weathers.class));
                        Toast.makeText(getApplicationContext(), getString(R.string.mobile_data), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_route:
                        startActivity(new Intent(MainActivity.this, TrackActivity.class));
                        Toast.makeText(getApplicationContext(), getString(R.string.route_data), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_compatable:
                        startActivity(new Intent(MainActivity.this, SearchActivity.class));
                        Toast.makeText(getApplicationContext(), getString(R.string.mobile_data), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_alarm:
                        Intent intent = new Intent(MainActivity.this, Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_sms:
                        startActivity(new Intent(MainActivity.this, AccActivity.class));
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                }
                return true;
            }
        });
            //<- 119 -->
        phonecall.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                showCallDialog();
            }
        });
   }

    public void onFabClick(View v) {
        if (!data.isRunning()) {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_pause));
            data.setRunning(true);
            time.setBase(SystemClock.elapsedRealtime() - data.getTime());
            time.start();
            data.setFirstTime(true);
            speakWords("헬멧 착용하셨나요?");
            Intent intent = new Intent(getApplicationContext(), GpsServices.class);
            startService(intent);
            refresh.setVisibility(View.INVISIBLE);
            if (sharedPreferences.getBoolean("route",false)) {
                try {
                    AJMapp ajMapp = (AJMapp) getApplicationContext();
                    ajMapp.setStartAddr(getGeocode(lat, lng));
                    ajMapp.setStartTime(getCurrentSec());
                    TrackDBhelper trackDBhelper = new TrackDBhelper(this);
                    trackDBhelper.open();
                    trackDBhelper.trackDBlocationStart(getCurrentSec(), lat, lng);
                    trackDBhelper.close();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "정상적으로 경로 저장이 실행되지 않았습니다." ,Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_play));
            data.setRunning(false);
            Intent stop = new Intent(getApplicationContext(), GpsServices.class);
            stopService(stop);
            refresh.setVisibility(View.VISIBLE);
        }
    }

    public void onRefreshClick(View v) {
        if (sharedPreferences.getBoolean("route", false)){
            try {
                TrackDBhelper trackDBhelper = new TrackDBhelper(this);
                trackDBhelper.open();
                AJMapp ajMapp = (AJMapp) getApplicationContext();
                //출발주소, 도착주소, 출발시간, 도착시간, 평균속도, 칼로리, 거리, 온도, 습도
                trackDBhelper.trackDBallFetch(ajMapp.getStartAddr(), getGeocode(lat, lng), ajMapp.getStartTime(), getCurrentSec(),
                        data.getAverageSpeed().toString(), data.getCalorieMeter().toString(), data.getDistance().toString(), weather5.getText().toString(), weather2.getText().toString());
                trackDBhelper.trackDBlocationStop(getCurrentSec(), lat, lng);
                trackDBhelper.close();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "정상적으로 경로 저장이 실행되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        resetData();
        Intent intent = new Intent(getApplicationContext(), GpsServices.class);
        stopService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsDisabledDialog();
        }
        loadWeather(weather_city);
        firstfix = true;
        if (!data.isRunning()) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString("data", "");
            data = gson.fromJson(json, Data.class);
        }
        if (data == null) {
            data = new Data(onGpsServiceUpdate);
        } else {
            data.setOnGpsServiceUpdate(onGpsServiceUpdate);
        }
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.getAllProviders().indexOf(LocationManager.GPS_PROVIDER) >= 0
                && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED){
            gpsValue = sharedPreferences.getString("gps_level", "default");
            try {
                switch (gpsValue) {
                    case "default":
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                        Log.w("Main", "GPSprovider Default");
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
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }else if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED){
            if (mLocationManager!=null){
                mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.w("Main", "NetworkProvider Request");
            }
            gpsValue = sharedPreferences.getString("gps_level", "default");
            try {
                switch (gpsValue) {
                    case "default":
                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                        Log.w("Main", "NetworkProvider Default");
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
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sharedPreferences.getBoolean("autoservice", false)) {
            mLocationManager.removeUpdates(this);
        }
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        prefsEditor.putString("data", json);
        prefsEditor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mLocationManager.removeUpdates(this);
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        if (sharedPreferences.getBoolean("autoservice", false)){
            Intent stop1 = new Intent(getApplicationContext(), TimeTask.class);
            Intent stop2 = new Intent(getApplicationContext(), GpsServices.class);
            stopService(stop1);
            stopService(stop2);
            resetData();
            mLocationManager.removeUpdates(this);
        }
        showNotification("안전모", "자전거 이용으로 " + trees + "그루의 낙엽송을 심었습니다:)");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_flash) {
            if (!hasFlash) {
                Toast.makeText(MainActivity.this, "죄송합니다. 이 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
            //NoobCameraManager.getInstance().takePermissions();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !turnFlash) {
                        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId = null;
                        try {
                            cameraId = camManager.getCameraIdList()[0];
                            camManager.setTorchMode(cameraId, true);
                            turnFlash = true;//Turn ON
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    } else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && turnFlash) {
                        CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        String cameraId = null;
                        try {
                            cameraId = camManager.getCameraIdList()[0];
                            camManager.setTorchMode(cameraId, false);
                            turnFlash = false;
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(final Location location){
        lat = location.getLatitude();
        lng = location.getLongitude();
        if (location.hasAccuracy()) {
            SpannableString s = new SpannableString(String.format(Locale.KOREA, "%.0f", location.getAccuracy()) + "m");
            //if (location.getAccuracy() > 30) {
            //    Toast.makeText(MainActivity.this, "위치 오차가 큽니다. (실내에선 위치 측정이 어려울 수 있습니다.)", Toast.LENGTH_LONG).show();
            //}
            s.setSpan(new RelativeSizeSpan(0.75f), s.length() - 1, s.length(), 0);
            accuracy.setText(s);

            if (firstfix) {
                fab.setVisibility(View.VISIBLE);
                if (!data.isRunning() && !maxSpeed.getText().equals("")) {
                    refresh.setVisibility(View.VISIBLE);
                }
                firstfix = false;
            }
        } else {
            firstfix = true;
        }

        if (location.hasSpeed()) {
            progressBarCircularIndeterminate.setVisibility(View.GONE);
            String speed = String.format(Locale.KOREA, "%.0f", location.getSpeed() * 3.6) + "km/h";

            if (sharedPreferences.getBoolean("miles_per_hour", false)) { // 마일로 변경
                speed = String.format(Locale.KOREA, "%.0f", location.getSpeed() * 3.6 * 0.62137119) + "mi/h";
            }
            SpannableString s = new SpannableString(speed);
            s.setSpan(new RelativeSizeSpan(0.25f), s.length() - 4, s.length(), 0);
            currentSpeed.setText(s);

            if (25 <= location.getSpeed() * 3.6 && !tts.isSpeaking() && data.isRunning()) {
                showNotification("과속운행", "속도를 줄여주세요");
                speakWords("속도를 줄여주세요");
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (views.getVisibility() == View.VISIBLE && fboolean) {
                                views.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                                fboolean = false;
                            } else if (!fboolean) {
                                views.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                fboolean = true;
                            }
                        }
                    });
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (20 > location.getSpeed() * 3.6 && !fboolean) {
                views.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }

            if (20 <= location.getSpeed() * 3.6 && !ishold) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                holder.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_lock_black_24dp));
                ishold = true;
            }
            double distance = lastlocation.distanceTo(location);
            if (distance > 0 && distance > 30) {
                trees = 1;
            } else if (distance > 50) {
                trees = 2;
            } else if (distance == 0 || distance < 30) {
                trees = 0;
            } else if (distance > 100) {
                trees = 3;
            }
        }
    }

    public void showGpsDisabledDialog(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(getString(R.string.action_settings));
        alertDialog.setMessage(getString(R.string.please_enable_gps));
        alertDialog.setPositiveButton(getString(R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void showCallDialog(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("119 전화 걸기");
        alertDialog.setMessage("전화를 거시겠습니까? 장난 전화는 금물입니다!");
        alertDialog.setPositiveButton("걸기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:119"));
                        try {
                            startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
            }
        });
        alertDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void resetData(){
        fab.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_action_play));
        refresh.setVisibility(View.INVISIBLE);
        time.stop();
        maxSpeed.setText("");
        averageSpeed.setText("");
        distance.setText("");
        calorie.setText("");
        time.setText("00:00:00");

        data = new Data(onGpsServiceUpdate);
    }

    public static Data getData() {
        return data;
    }

    @Override
    public void onBackPressed() {
        //Intent a = new Intent(Intent.ACTION_MAIN);
        //a.addCategory(Intent.CATEGORY_HOME);
        //a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(a);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            //super.onBackPressed();
            backPressCloseHandler.onBackPressed();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }
    @Override
    public void onProviderEnabled(String s) {
    }
    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP&&!ishold)
            {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                holder.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_lock_black_24dp));
                ishold = true;
                Toast.makeText(getApplicationContext(),"화면 잠금 실행",Toast.LENGTH_SHORT).show();
                return true;
            }
            else if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP&&ishold)
            {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                holder.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_lock_open_black_24dp));
                ishold = false;
                Toast.makeText(getApplicationContext(),"화면 잠금 해제",Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{notifyIntent} , PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.mainlogo2)
                .setTicker("안전모")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setContentText(message)
                .setPriority(Notification.PRIORITY_MAX) .build();
        notification.defaults |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }



    private void speakWords(String speech) {
        tts.speak(speech, TextToSpeech.LANG_COUNTRY_AVAILABLE, null,null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == MY_DATA_CHECK_CODE) {
        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
            tts = new TextToSpeech(this,this);
            //tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            //  @Override
            //  public void onInit(int status) {
            //      if (status == TextToSpeech.SUCCESS)
            //      {
            //          if (tts.isLanguageAvailable(Locale.KOREA) == TextToSpeech.LANG_AVAILABLE)
            //              tts.setLanguage(Locale.KOREA);
            //      }
            //      else if (status == TextToSpeech.ERROR)
            //      {
            //          Toast.makeText(getApplicationContext(), "음성 안내를 사용할 수 없습니다... 개발자에게 메일로 문의하세요.", Toast.LENGTH_LONG).show();
            //      }
            //  }
            // });
        }
    }
    else {
        Intent installTTSIntent = new Intent();
        installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        startActivity(installTTSIntent);
        }
    }

    public void onInit(int initStatus) {

        if (initStatus == TextToSpeech.SUCCESS) {
            if(tts.isLanguageAvailable(Locale.KOREA)==TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.KOREA);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(getApplicationContext(), "음성 안내를 사용할 수 없습니다... 개발자에게 메일로 문의하세요.", Toast.LENGTH_LONG).show();
        }
    }

    public void loadWeather(String city) {
        WeatherMap weatherMap = new WeatherMap(this, weather_id);
        weatherMap.getCityWeather(city, new WeatherCallback() {
            @Override
            public void success(WeatherResponseModel response) {
                populateWeather(response);
            }

            @Override
            public void failure(String message) {
                //Toast.makeText(getApplicationContext(),"날씨 정보 업데이트에 실패하였습니다.",Toast.LENGTH_LONG).show();
            }
        });
        weatherMap.getCityForecast(city, new ForecastCallback() {
            @Override
            public void success(ForecastResponseModel response) {
                //ForecastResponseModel responseModel = response;
            }

            @Override
            public void failure(String message) {
            }
        });
    }

    private void populateWeather(WeatherResponseModel response) {
        weather2.setText(response.getMain().getHumidity()+ "%");
        weather5.setText(TempUnitConverter.convertToCelsius(response.getMain().getTemp()).intValue() + " °C");
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(1).build();
            Log.w("Main: ", "onStart SoundPool");
        }else{
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC ,0);
        }
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                sampleId = soundPool.load(getApplicationContext(), R.raw.bikebell, 1);
                soundPool.play(sampleId, 1f, 1f, 0, 0, 1f);
                Log.w("Main: ", "SoundPool Load Complete!");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundPool.release();
        soundPool = null;
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key:", "*****"+something+"*****");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
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

    public String getGeocode(double lat, double lng) {
        String address = null;
        final Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address>addr = null;
        try{
                addr = geocoder.getFromLocation(lat, lng, 1);
                if (addr!=null&&addr.size()>0){
                    address = addr.get(0).getThoroughfare().toString();
                    Log.w("Main :", address);
                }
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "시작 주소 취득 실패"
                    , Toast.LENGTH_LONG).show();
        }
        return address;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}


