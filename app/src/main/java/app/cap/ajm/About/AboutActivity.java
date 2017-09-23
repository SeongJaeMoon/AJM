package app.cap.ajm.About;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import me.drakeet.multitype.Items;
import me.drakeet.support.about.AbsAboutActivity;
import me.drakeet.support.about.BuildConfig;
import me.drakeet.support.about.Card;
import me.drakeet.support.about.Category;
import me.drakeet.support.about.Contributor;
import me.drakeet.support.about.License;
import me.drakeet.support.about.Line;
import me.drakeet.support.about.R;
/**
 * @author drakeet
 */
public class AboutActivity extends AbsAboutActivity {

    @Override @SuppressLint("SetTextI18n")
    protected void onCreateHeader(ImageView icon, TextView slogan, TextView version) {
        icon.setImageResource(R.drawable.mainlogo2);
        slogan.setText(getString(R.string.about_pageApp));
        version.setText("v" + BuildConfig.VERSION_NAME);
    }


    @Override @SuppressWarnings("SpellCheckingInspection")
    protected void onItemsCreated(@NonNull Items items) {
        /* @formatter:off */
        items.add(new Category(getString(R.string.about_pageHi)));
        items.add(new Card(getString(R.string.about_page).replace(" ", "\u00A0"), getString(R.string.about_pageSHARE)));
        items.add(new Line());
        items.add(new Category(getString(R.string.about_pageHelp)));
        items.add(new Contributor(R.drawable.ic_my_location_black_48dp,getString(R.string.about_pageHow_gps), getString(R.string.about_pageGPS).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_directions_bike_black_48dp ,getString(R.string.about_pageHow_Direct),getString(R.string.about_pageDirect).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_trending_up_black_48dp,getString(R.string.about_pageHow_Route), getString(R.string.about_pageRoute).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_sms_black_48dp,getString(R.string.about_pageHow_Save), getString(R.string.about_pageSave).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_keyboard_voice_black_48dp,getString(R.string.about_pageHow_TTS),
                getString(R.string.about_pageTTS).replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_warning_black_48dp,getString(R.string.about_pageHow_Alert), getString(R.string.about_pageAlerts).replace(" ","\u00A0")));
        items.add(new Category(getString(R.string.about_pageAlert)));
        items.add(new Contributor(R.drawable.mainlogo2, getString(R.string.about_pageAJM), getString(R.string.about_pageAJM_Alert).replace(" ","\u00A0")));
        items.add(new Line());
        items.add(new Category(getString(R.string.about_pageLicense)));
        items.add(new License("MultiType", "drakeet",License.APACHE_2,"https://github.com/drakeet/MultiType"));
        items.add(new License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"));
        items.add(new License("EasyWeather","code-crusher",License.APACHE_2, "https://github.com/code-crusher/EasyWeather"));
        items.add(new License("SpeedMeter","flyingrub",License.GPL_V2,"https://github.com/flyingrub/SpeedMeter"));
        items.add(new License("Fall_Detection","swift2891",License.APACHE_2,"https://github.com/swift2891/Fall_Detection"));
        items.add(new License("picasso","square",License.APACHE_2, "https://github.com/square/picasso"));
        items.add(new License("okhttp","square", License.APACHE_2,"https://github.com/square/okhttp"));
        items.add(new License("volley","google", License.APACHE_2, "https://github.com/google/volley"));
        items.add(new License("butternife","JakeWharton",License.APACHE_2,"https://github.com/JakeWharton/butterknife"));
        items.add(new License("NoobCameraFlash","abhi347",License.GPL_V3, "https://github.com/Abhi347/NoobCameraFlash"));
        items.add(new License("parcler","johncarl81",License.APACHE_2,"https://github.com/johncarl81/parceler"));
        items.add(new License("FloatingActionButton","makovkastar",License.MIT,"https://github.com/makovkastar/FloatingActionButton"));
        items.add(new License("AndroidSwipeLayout","daimajia",License.MIT,"https://github.com/daimajia/AndroidSwipeLayout"));
        items.add(new License("geofire-java","firebase",License.MIT,"https://github.com/firebase/geofire-java"));
        items.add(new License("CicleImageView","hdodenhof",License.APACHE_2,"https://github.com/hdodenhof/CircleImageView"));
        items.add(new License("commons-lang3","Apache",License.APACHE_2,"https://mvnrepository.com/artifact/org.apache.commons/commons-lang3/3.3.2"));
        items.add(new License("commons-math3","Apache",License.APACHE_2,"https://mvnrepository.com/artifact/org.apache.commons:commons-math3:3.6.1"));
        items.add(new License("gson","google",License.APACHE_2,"https://github.com/google/gson"));
        items.add(new License("Google Play Services API",License.GOOGLE, License.APACHE_2, "https://developers.google.com/android/reference"));
        items.add(new License("material-design-icons","google",License.APACHE_2,"https://github.com/google/material-design-icons"));
        items.add(new License("Kakao Open SDK",License.KAKAO,License.APACHE_2,"https://developers.kakao.com/docs/sdk"));
        items.add(new License("AJM","안전모",License.APACHE_2+", (CCL) "+License.CCL,"https://github.com/SeongJaeMoon/AJM"));

        items.add(new Line());
        items.add(new Category(getString(R.string.about_pagePL)));
        items.add(new Contributor(R.drawable.ic_android_black_48dp,getString(R.string.about_pageWhere), getString(R.string.about_pageWhere_is)));

        items.add(new Line());
        items.add(new Category(getString(R.string.about_pageCS)));
        items.add(new Contributor(R.drawable.ic_star_black_48dp,getString(R.string.about_pageBike), getString(R.string.about_pageBikeis)));
    }
    @Override
    protected void onActionClick(View action) {
        onClickShare();
    }


    public void onClickShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "안전모");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.about_page));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }
}


