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
        slogan.setText("안전모");
        version.setText("v" + BuildConfig.VERSION_NAME);
    }


    @Override @SuppressWarnings("SpellCheckingInspection")
    protected void onItemsCreated(@NonNull Items items) {
        /* @formatter:off */
        items.add(new Category("소개"));
        items.add(new Card(getString(R.string.about_page).replace(" ", "\u00A0"), "공유"));
        items.add(new Line());
        items.add(new Category("도움말"));
        items.add(new Contributor(R.drawable.ic_my_location_black_48dp,"GPS측정은 어떻게 이루어지나요?", "GPS 측정은 스마트폰에 내장된 GPS 측정 장치를 통해 이루어지게 됩니다. GPS 감도를 높일수록 배터리 사용량이 증가하게 됩니다. 지도를 사용하는 기능은 배터리 소모량을 증가시킬 수 있습니다.".replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_directions_bike_black_48dp ,"길 찾기 기능은 어떻게 이루어지나요?",
                "길 찾기는 카카오내비 앱을 연동하여 이루어지며 카카오내비 앱의 미 설치시 설치 화면으로 넘어갑니다.".replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_trending_up_black_48dp,"경로 저장 기능은 어떻게 이루어지나요?", "경로 저장은 사용자의 활동을 감지하여 사용자가 머무르거나 지나간 지역이 표시되며, 통계치를 확인 할 수 있습니다.".replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_sms_black_48dp,"안전 서비스 기능은 어떻게 이루어지나요?", "안전 서비스는 스마트폰에 내장된 센서로 사용자의 넘어짐을 감지하며 넘어짐 감지시 사용자에게 응답을 요구하고, 일정 시간 동안 응답이 없을 경우에 사용자가 등록한 번호로 위치를 SMS로 전송하는 기능입니다.".replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_keyboard_voice_black_48dp,"음성 안내 기능은 어떻게 이루어지나요?",
                "음성 안내 기능은 스마트폰에 내장된 TTS(Text to Speech) 음성합성기술을 이용하며, 스마트폰 기종에 따라 지원 폭이 상이할 수 있습니다. 음성 안내가 나오지 않을 경우 설정 창에서 진동이나 무음을 소리로 바꿔주세요. 지속적으로 음성 안내가 나오지 않을 경우 개발자에게 메일로 문의 부탁드립니다.".replace(" ","\u00A0")));
        items.add(new Contributor(R.drawable.ic_warning_black_48dp,"위험구간 안내 기능은 어떻게 이루어지나요?",
                "위험 구간 안내 서비스는 스마트폰에 내장된 GPS인식 장치로 이루어지며, 시작 버튼을 누르게 되면 실행됩니다. 이 기능은 배터리 사용량을 증가 시킬 수 있습니다.(위치 오차로 인해 간혹 위험 알림이 제대로 작동하지 않을 수 있습니다.) 기본 속도계 화면에선 속도를 측정하여 25km 이상의 속도로 운행시 과속운행 주의 음성 안내와 화면 전환 안내가 발생합니다. 또한, 속도가 20km이상이 되면 화면 터치가 불가능 하며 정지하여 볼륨업(소리 키우기)버튼을 클릭하여 화면 잠금을 해제해주세요.".replace(" ","\u00A0")));

        items.add(new Category("주의 사항"));
        items.add(new Contributor(R.drawable.mainlogo2, "안전모팀", "이 앱은 사용자들의 자전거 이용의 안전성을 높이기 위해 개발된 앱입니다. 무리한 앱 사용을 지양하고 항상 안전한 라이딩 부탁드릴게요! :)".replace(" ","\u00A0")));
        items.add(new Line());

        items.add(new Category("오픈 소스 라이센스"));
        items.add(new License("MultiType", "drakeet",License.APACHE_2,"https://github.com/drakeet/MultiType"));
        items.add(new License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"));
        items.add(new License("EasyWeather","code-crusher",License.APACHE_2, "https://github.com/code-crusher/EasyWeather"));
        items.add(new License("SpeedMeter","flyingrub",License.GPL_V2,"https://github.com/flyingrub/SpeedMeter"));
        items.add(new License("GPStracker","CarlBarks",License.APACHE_2,"https://github.com/CarlBarks/GPS-and-Activity-Tracker"));
        items.add(new License("picasso","square",License.APACHE_2, "https://github.com/square/picasso"));
        items.add(new License("okhttp","square", License.APACHE_2,"https://github.com/square/okhttp"));
        items.add(new License("butternife","JakeWharton",License.APACHE_2,"https://github.com/JakeWharton/butterknife"));
        items.add(new License("NoobCameraFlash","abhi347",License.GPL_V3, "com.github.Abhi347:NoobCameraFlash:0.0.1"));
        items.add(new License("parcler","johncarl81",License.APACHE_2,"https://github.com/johncarl81/parceler"));
        items.add(new License("FloatingActionButton","makovkastar",License.MIT,"https://github.com/makovkastar/FloatingActionButton"));
        items.add(new License("AndroidSwipeLayout","daimajia",License.MIT,"https://github.com/daimajia/AndroidSwipeLayout"));
        items.add(new License("geofire-java","firebase",License.MIT,"https://github.com/firebase/geofire-java"));
        items.add(new License("CicleImageView","hdodenhof",License.APACHE_2,"https://github.com/hdodenhof/CircleImageView"));
        items.add(new License("gson","google",License.APACHE_2,"https://github.com/google/gson"));

        //items.add(new License("AJM","안전모",License.APACHE_2,""));안전모와 관련된 오픈소스 라이센스 다음API, OpenWeatherMap API, GoogleMap Places for Android API
        items.add(new Line());
        items.add(new Category("공공 데이터 출처"));
        items.add(new Contributor(R.drawable.ic_android_black_48dp,"출처",
                        " 날씨 정보 - OpenWeatherMap 서울시 데이터\n 교차로정보 - ITS표준노드링크시스템\n " +
                        "보행자전용도로 - 서울시 열린데이터 광장\n 사고다발지역 - 서울시 열린데이터 광장\n 어린이 보호구역 - 공공데이터 포털\n 보행자 전용도로- 서울시 시설관리공단"));

        items.add(new Line());
        items.add(new Category("자전거 관리 상식"));
        items.add(new Contributor(R.drawable.ic_star_black_48dp,"자전거 관리는 이렇게!",
                "1.타이어가 너무 닳지 않았는지 검사.\n2.브레이크 살펴보기.\n3.서스펜션 포크 볼트 확인.\n4.체인과 스프라켓의 윤활유 확인하기.\n5.베어링에 그리스 재주입. " +
                        "\n마지막으로 일주일에 한번은 고생한 자전거를 위해 자전거를 닦아주세요! 안전하고 간지나는 라이딩 하시길 바라겠습니다 :)"));

        // 공공데이터 출처(날씨정보(OpenWeatherMap API), 직각교차로정보(ITS-표준노드링크시스템),
        // 보행자전용도로(서울시 열린데이터 광장),
        // 사고다발지역(서울시 열린데이터 광장), 어린이보호구역정보(공공 데이터 포털),
        // 보행자전용도로(서울시시설관리공단),
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


