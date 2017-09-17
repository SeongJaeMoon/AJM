package app.cap.ajm;

import android.widget.Toast;
import android.app.Activity;

import java.util.Locale;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;
    public BackPressCloseHandler(Activity context)
    {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 3000)
        {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 3000)
        {
            activity.finish();
            toast.cancel(); }
    }
    public void showGuide()
    {
        if (Locale.getDefault().getLanguage().equals("ko")) {
            toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        }else if(Locale.getDefault().getLanguage().equals("en")){
            toast = Toast.makeText(activity, "Press the \'Back button\' again to exit.",Toast.LENGTH_SHORT);
        }else if (Locale.getDefault().getLanguage().equals("ja")){
            toast = Toast.makeText(activity, "\'戻る\'ボタンをもう一度押すと終了します。",Toast.LENGTH_SHORT);
        }else if (Locale.getDefault().getLanguage().equals("zh")){
            toast = Toast.makeText(activity, "再次按返回按钮退出。",Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}

