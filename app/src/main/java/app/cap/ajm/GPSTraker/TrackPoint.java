package app.cap.ajm.GPSTraker;


public class TrackPoint {
    private String runTime;
    private double lat;
    private double lng;

    public TrackPoint(String runTime, double lat, double lng){
        this.runTime = runTime;
        this.lat = lat;
        this.lng = lng;
    }
    public void setRunTime(String runTime){
        this.runTime = runTime;
    }
    public void setLat(double lat){
        this.lat = lat;
    }
    public void setLng(double lng){
        this.lng = lng;
    }
    public String getRunTime(){
        return runTime;
    }
    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }
}
