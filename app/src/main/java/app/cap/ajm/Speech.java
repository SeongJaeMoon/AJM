package app.cap.ajm;

import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class Speech {
    private static TextToSpeech tts;
    private static CharSequence sc_str;

    public static void Talk(String str){
        sc_str = str;
        tts = new TextToSpeech(AJMapp.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.getDefault());
                    tts.speak(sc_str, TextToSpeech.QUEUE_FLUSH, null,null);
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
