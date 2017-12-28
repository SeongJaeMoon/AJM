package app.cap.ajm;

public class Weather {

    private String key;
    private String value;

    public Weather(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return key;
    }

    public String getValue(){
        return value;
    }

}
