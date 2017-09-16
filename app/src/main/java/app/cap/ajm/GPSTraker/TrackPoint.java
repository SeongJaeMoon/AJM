package app.cap.ajm.GPSTraker;


public class TrackPoint {
    private double lat;
    private double lng;

    public TrackPoint(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }
    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }
}
