package app.cap.ajm.GPSTraker;

import android.content.Context;
import android.content.res.Resources;

import org.parceler.Parcel;

import app.cap.ajm.R;

@Parcel
public class TrackConstans {
    public String startAddr;
    public String endAddr;
    public String startTime;
    public String endTime;
    public double avgSpeed;
    public double calorie;
    public double distance;
    public double temp;
    public double wet;

    public double pLat;
    public double pLng;

    public TrackConstans(String startTime, String endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }
    //<-값 가져오기->
    public String getStartAddr(){return startAddr;}
    public String getEndAddr(){return endAddr;}
    public String getStartTime(){return startTime;}
    public String getEndTime(){return endTime;}
    public double getAvgSpeed(){return avgSpeed;}
    public double getCalorie(){return calorie;}
    public double getDistances(){return distance;}
    public double getpLat(){return pLat;}
    public double getpLng(){return pLng;}
    public double getTemp(){return temp;}
    public double getWet(){return wet;}
    //<-값 설정->
    public void setStartAddr(String startAddr){this.startAddr = startAddr;}
    public void setEndAddr(String endAddr){this.endAddr = endAddr;}
    public void setStartTime(String startTime){this.startTime = startTime;}
    public void setEndTime(String endTime){this.endTime = endTime;}
    public void setAvgSpeed(double avgSpeed){this.avgSpeed = avgSpeed;}
    public void setCalories(double calorie){this.calorie = calorie;}
    public void setDistances(double distance){this.distance = distance;}
    public void setpLat(double pLat){this.pLat = pLat;}
    public void setpLng(double pLng){this.pLng = pLng;}
    public void setTemp(double temp){this.temp = temp;}
    public void setWet(double wet){this.wet = wet;}
}
