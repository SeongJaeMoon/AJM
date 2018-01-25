package app.cap.ajm.Model;

public class Route {

    private String origin;
    private String destinantion;

    public Route(String origin, String destinantion){
        this.origin = origin;
        this.destinantion = destinantion;
    }

    public String getOrigin(){
        return origin;
    }

    public String getDestinantion(){
        return destinantion;
    }
}
