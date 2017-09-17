package app.cap.ajm;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import java.util.Locale;

public class Data {
    private boolean isRunning;
    private long time;
    private long timeStopped;
    private boolean isFirstTime;
    private double calorie;
    private double calorieK;
    private double distanceKm;
    private double distanceM;
    private double curSpeed;
    private double maxSpeed;
    private int weight;
    private onGpsServiceUpdate onGpsServiceUpdate;

    public interface onGpsServiceUpdate{
        public void update();
    }

    public void setOnGpsServiceUpdate(onGpsServiceUpdate onGpsServiceUpdate){
        this.onGpsServiceUpdate = onGpsServiceUpdate;
    }

    public void update(){
        onGpsServiceUpdate.update();
    }

    public Data() {
        isRunning = false;
        distanceKm = 0;
        distanceM = 0;
        curSpeed = 0;
        maxSpeed = 0;
        timeStopped = 0;
        calorie = 0;
        calorieK =0;
        weight = 0;
    }

    public Data(onGpsServiceUpdate onGpsServiceUpdate){
        this();
        setOnGpsServiceUpdate(onGpsServiceUpdate);
    }

    public void addDistance(double distance){
        distanceM = distanceM + distance;
        distanceKm = distanceM / 1000f;
    }

    public SpannableString getDistance(){
        SpannableString s;
        if (distanceKm < 1) {
            s = new SpannableString(String.format(Locale.KOREA,"%.0f", distanceM) + "m");
            s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 1, s.length(), 0);
        }
        else
            {
            s = new SpannableString(String.format(Locale.KOREA,"%.3f", distanceKm) + "Km");
            s.setSpan(new RelativeSizeSpan(0.5f), s.length()-2, s.length(), 0);
        }
        return s;
    }

    public SpannableString getMaxSpeed() {
        SpannableString s = new SpannableString(String.format(Locale.KOREA,"%.0f", maxSpeed) + "km/h");
        s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 4, s.length(), 0);
        return s;
    }

    public SpannableString getCalorieMeter(){
        SpannableString s;
        if (calorieK<1){
            s = new SpannableString(String.format(Locale.KOREA,"%.0f", distanceM) + "cal");
            s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 3, s.length(), 0);
        }
        else {
            s = new SpannableString((String.format(Locale.KOREA, "%.0f", calorie) + "kcal"));
            s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 4, s.length(), 0);
        }
        return s;
    }

    public SpannableString getAverageSpeed(){

        double average = ((distanceM / (time / 1000)) * 3.6);
        SpannableString s;
        if (time > 0)
        {
            s = new SpannableString(String.format(Locale.KOREA,"%.0f", average) + "km/h");
        }
        else
            {
            s = new SpannableString(0 + "km/h");
        }
        s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 4, s.length(), 0);
        return s;
    }

    public SpannableString getAverageSpeedMotion(){
        double motionTime = time - timeStopped;
        SpannableString s;
        if (motionTime < 0)
        {
            s = new SpannableString(0 + "km/h");
        }
        else
        {
            double average = ((distanceM / ( (time - timeStopped) / 1000)) * 3.6);
            s = new SpannableString(String.format(Locale.KOREA,"%.0f", average) + "km/h");
        }
        s.setSpan(new RelativeSizeSpan(0.5f), s.length() - 4, s.length(), 0);
        return s;
    }

    public void setCurSpeed(double curSpeed) {
        this.curSpeed = curSpeed;
        if (curSpeed > maxSpeed){
            maxSpeed = curSpeed;
        }
    }

    public double returnDistance(){
        return distanceM;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setTimeStopped(long timeStopped) {
        this.timeStopped += timeStopped;
    }

    public double getCurSpeed() {
        return curSpeed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getCalorie(){return calorie;}
    /*칼로리 계산 몸무게x 칼로리소비계수x 평균속도km/h */
    public void setCalorie(int weight) {
        double averageSpeed = ((distanceM / (time / 1000)) * 3.6);
        Log.w("Data : ", "DistanceM&&K: "+String.valueOf(distanceM)+", "+ String.valueOf(distanceKm)+" averageSpeed: " + averageSpeed);
        if (distanceM>10 && averageSpeed > 5 && averageSpeed < 13){
            calorie = weight * 0.0430 * averageSpeed;
        }
        else if (distanceM >10 && averageSpeed >= 13 && averageSpeed <= 15) {
            calorie = weight * 0.0650 * averageSpeed;
        }
        else if (distanceM >10 && averageSpeed >= 16 && averageSpeed <= 18)
        {
            calorie = weight * 0.0783 * averageSpeed;
        }
        else if(distanceM >10 && averageSpeed >= 19 && averageSpeed <=21 )
        {
            calorie = weight * 0.0939 * averageSpeed;
        }
        else if(distanceM >10 && averageSpeed >= 22 && averageSpeed <=23 )
        {
            calorie = weight * 0.113 * averageSpeed;
        }
        else if(distanceM >10 && averageSpeed >= 24 && averageSpeed <=25 )
        {
            calorie = weight * 0.124 * averageSpeed;
        }
        else if(distanceM >10 && averageSpeed >= 26 && averageSpeed <= 28 )
        {
            calorie = weight * 0.136 * averageSpeed;
        }
        else if(distanceM >10 && averageSpeed >= 29 && averageSpeed <=31 )
        {
            calorie = weight * 0.179 * averageSpeed;
        }
        else if(distanceM > 10 && averageSpeed >32)
        {
            calorie = weight * 0.196 * averageSpeed;
        }
    }

    public void addCalorie(double cal){
        calorie = calorie + cal;
        if (cal>10000||Double.isNaN(cal)||Double.isInfinite(cal)){
            cal = 0;
            calorie = cal;
        }
        calorieK = calorie / 1000f;
    }
}

