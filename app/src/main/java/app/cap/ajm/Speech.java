package app.cap.ajm;


import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class Speech {
    private static TextToSpeech tts;
    private static CharSequence SC_str;
    private static String speakword;

    public static void Talk(String str){
        SC_str = str;
        tts = new TextToSpeech(AJMapp.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.getDefault());
                    tts.speak(SC_str, TextToSpeech.QUEUE_FLUSH, null,null);
                }
            }
        });
    }
    public TextToSpeech getTTS(){
        return tts;
    }
}
