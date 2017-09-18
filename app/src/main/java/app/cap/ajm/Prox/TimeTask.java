package app.cap.ajm.Prox;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;
import java.util.Locale;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import app.cap.ajm.R;

public class TimeTask extends Service implements SensorEventListener, TextToSpeech.OnInitListener {
    private SharedPreferences sharedPreferences;
    double latitude, longitude;
    LocationManager locationManager;
    LocationListener locationListener;
    Handler handler = new Handler(Looper.getMainLooper());
    private Handler mPeriodicEventHandler = new Handler();
    private final int PERIODIC_EVENT_TIMEOUT = 3000;
    //private final int PERIODIC_EVENT_TIMEON = 5000;
    private Timer fuseTimer = new Timer();
    private int sendCount = 0;
    private char sentRecently = 'N';
    //3가지 센서 융합 - 변수:
    // 자이로 센서의 각속도
    private float[] gyro = new float[3];
    private float degreeFloat;
    private float degreeFloat2;
    //자이로 센서 데이터의 회전 행렬
    private float[] gyroMatrix = new float[9];

    // 자이로 행렬으로부터의 방위각
    private float[] gyroOrientation = new float[3];

    // 자기장 벡터
    private float[] magnet = new float[3];

    // 가속도계 벡터
    private float[] accel = new float[3];

    // 가속도계와 자기장으로부터의 방위각
    private float[] accMagOrientation = new float[3];

    //3가지 센서를 합친 것의 방위각
    private float[] fusedOrientation = new float[3];

    //가속도계와 자기장센서의 기준 회전 행렬
    private float[] rotationMatrix = new float[9];

    public static final float EPSILON = 0.000000001f;

    public static final int TIME_CONSTANT = 30;
    public static final float FILTER_COEFFICIENT = 0.98f;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private boolean initState = true;

    //센서변수:
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senProximity;
    private SensorEvent mSensorEvent;

    private Runnable doPeriodicTask = new Runnable() {
        public void run() {
            sentRecently = 'N';
        }
    };

    public TimeTask() {

    }

    public static TextToSpeech tts;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        tts = new TextToSpeech(this, this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        mPeriodicEventHandler.removeCallbacks(doPeriodicTask);
        senSensorManager.unregisterListener(this);
        sendCount = 0;
        locationManager.removeUpdates(locationListener);
    }
    @Override
    public void onInit(int initStatus) {
        if (initStatus == TextToSpeech.SUCCESS) {
            if(Locale.getDefault().getLanguage().equals("ko")&&tts.isLanguageAvailable(Locale.KOREAN)==TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.KOREAN);
            else if (Locale.getDefault().getLanguage().equals("en")&&tts.isLanguageAvailable(Locale.ENGLISH)==TextToSpeech.LANG_AVAILABLE){
                tts.setLanguage(Locale.ENGLISH);
            }
            else if (Locale.getDefault().getLanguage().equals("ja")&&tts.isLanguageAvailable(Locale.JAPANESE)==TextToSpeech.LANG_AVAILABLE){
                tts.setLanguage(Locale.JAPANESE);
            }
            else if(Locale.getDefault().getLanguage().equals("zh")&&tts.isLanguageAvailable(Locale.CHINESE)==TextToSpeech.LANG_AVAILABLE){
                tts.setLanguage(Locale.CHINESE);
            }
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(getApplicationContext(), getString(R.string.tts_not_setup), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //위치 정보를 획득하기 위해 Location Listener와 Location Manager 등록
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TimeTask.this.getApplicationContext(), "위치 권한이 설정 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);

        String locationProvider = LocationManager.NETWORK_PROVIDER;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(TimeTask.this.getApplicationContext(), "위치 권한이 설정 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        latitude = locationManager.getLastKnownLocation(locationProvider).getLatitude();
        longitude = locationManager.getLastKnownLocation(locationProvider).getLongitude();
        onTaskRemoved(intent);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null) {
            initListeners();
            fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(), 1000, TIME_CONSTANT);
        }
        else {
            Toast.makeText(getApplicationContext(), "이 스마트폰은 가속도계 센서를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
        }

        return START_STICKY;
    }

    public void initListeners() {
        senSensorManager.registerListener(this,
                senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        senSensorManager.registerListener(this,
                senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);

        senSensorManager.registerListener(this,
                senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // 새로운 가속도계 데이터를 가속도계 배열에 복사
                // 새로운 방위각 계산
                System.arraycopy(sensorEvent.values, 0, accel, 0, 3);
                calculateAccMagOrientation();
                break;
            case Sensor.TYPE_GYROSCOPE:
                // 자이로 데이터 처리
                gyroFunction(sensorEvent);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                //새로운 자기장 데이터를 배열에 복사
                System.arraycopy(sensorEvent.values, 0, magnet, 0, 3);
                break;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void calculateAccMagOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
            SensorManager.getOrientation(rotationMatrix, accMagOrientation);
        }
    }

    private void getRotationVectorFromGyro(float[] gyroValues,
                                           float[] deltaRotationVector,
                                           float timeFactor) {
        float[] normValues = new float[3];

        //샘플의 각속도를 계산
        float omegaMagnitude =
                (float) Math.sqrt(gyroValues[0] * gyroValues[0] +
                        gyroValues[1] * gyroValues[1] +
                        gyroValues[2] * gyroValues[2]);

        //축을 얻기에 충분히 큰 경우, 회전 벡터를 표준화
        if (omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }


        //timestep에 의해 이 축을 중심으로 각속도와 통합
        //이 샘플에서 시간 경과에 따른 델타 값의 회전변환을 얻으려면
        //델타 회전의 축각 표현의 변환 필요
        //회전 행렬로 변환하기 전에 쿼터니언으로 변환
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }

    public void gyroFunction(SensorEvent event) {
        // 첫 번째 가속도계 / 자기장 방향이 획득 될 때까지 시작하지 않음
        if (accMagOrientation == null)
            return;

        // 자이로 회전 배열 값을 초기화
        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
            initState = false;
        }

        // 새 자이로 값을 자이로 배열에 복사
        // 원 자이로 데이터를 회전 벡터로 변환
        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            System.arraycopy(event.values, 0, gyro, 0, 3);
            getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
        }

        //측정 완료, 다음 시간 간격으로 현재 시간 저장
        timestamp = event.timestamp;

        //회전 벡터를 회전 행렬로 변환
        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

        //회전 벡터를 회전 행렬로 변환
        gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

        //회전 행렬에서 자이로 스코프 기반 방향을 얻는다.
        SensorManager.getOrientation(gyroMatrix, gyroOrientation);
    }

    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        //x 축 (피치)에 대한 회전배열
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        //y 축 (롤)에 대한 회전배열
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        //z 축에 대한 회전 (방위각)배열
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        //회전 순서는 y, x, z (롤, 피치, 방위각)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
            fusedOrientation[0] =
                    FILTER_COEFFICIENT * gyroOrientation[0]
                            + oneMinusCoeff * accMagOrientation[0];

            fusedOrientation[1] =
                    FILTER_COEFFICIENT * gyroOrientation[1]
                            + oneMinusCoeff * accMagOrientation[1];

            fusedOrientation[2] =
                    FILTER_COEFFICIENT * gyroOrientation[2]
                            + oneMinusCoeff * accMagOrientation[2];

            //**********위험 감지**********
            double SMV = Math.sqrt(accel[0] * accel[0] + accel[1] * accel[1] + accel[2] * accel[2]);

            if (SMV > 35) {
                if (sentRecently == 'N') {
                    Log.d("Accelerometer vector:", "" + SMV);
                    degreeFloat = (float) (fusedOrientation[1] * 180 / Math.PI);
                    degreeFloat2 = (float) (fusedOrientation[2] * 180 / Math.PI);
                    if (degreeFloat < 0)
                        degreeFloat = degreeFloat * -1;
                    if (degreeFloat2 < 0)
                        degreeFloat2 = degreeFloat2 * -1;
                    if (degreeFloat > 30 || degreeFloat2 > 30) {
                        Log.d("Degree1:", "" + degreeFloat);
                        Log.d("Degree2:", "" + degreeFloat2);
                        speekword(getString(R.string.fall_detect));

                        Intent intent = new Intent(TimeTask.this, DialogActivity.class);
                        intent.putExtra("lastlat",latitude);
                        intent.putExtra("lastlon",longitude);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    else
                        {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TimeTask.this.getApplicationContext(), getString(R.string.be_careful), Toast.LENGTH_LONG).show();
                                speekword(getString(R.string.be_careful_tts));
                                Log.d("Send!", "센서 값 변화!!!!! " + sendCount);
                            }
                        });
                        sendCount++;
                    }
                    sentRecently='Y';
                    mPeriodicEventHandler.postDelayed(doPeriodicTask, PERIODIC_EVENT_TIMEOUT);
                }
            }
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
        }
    }

    private void speekword(String str){
        tts.speak(str, TextToSpeech.LANG_COUNTRY_AVAILABLE, null, null);
    }
}



