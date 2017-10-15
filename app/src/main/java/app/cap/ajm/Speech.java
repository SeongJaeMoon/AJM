package app.cap.ajm;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class Speech {
    private static TextToSpeech tts;
    private static CharSequence sc_str;
    private static String st_str;

    public void Talk(String str){
        sc_str = str;
        st_str = str;
        tts = new TextToSpeech(AJMapp.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.getDefault());
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        tts.speak(sc_str, TextToSpeech.QUEUE_FLUSH, null, null);
                    }else{
                        tts.speak(st_str, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }else {
                    System.out.println("TTS Error");
                }
            }
        });
    }
    public TextToSpeech getTTS(){
        return tts;
    }
}
