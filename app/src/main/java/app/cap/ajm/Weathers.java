package app.cap.ajm;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import github.vatsal.easyweather.Helper.ForecastCallback;
import github.vatsal.easyweather.Helper.TempUnitConverter;
import github.vatsal.easyweather.Helper.WeatherCallback;
import github.vatsal.easyweather.WeatherMap;
import github.vatsal.easyweather.retrofit.models.ForecastResponseModel;
import github.vatsal.easyweather.retrofit.models.Weather;
import github.vatsal.easyweather.retrofit.models.WeatherResponseModel;

import static app.cap.ajm.BuildConfig.OWM_API_KEY;

public class Weathers extends AppCompatActivity {

    public final String APP_ID = OWM_API_KEY;
    String city = "Seoul";
    String theCity = "서울";
    @Bind(R.id.weather_title)
    TextView weatherTitle;
    @Bind(R.id.refresh)
    ImageButton refresh;
    @Bind(R.id.weather_icon)
    ImageView weatherIcon;
    @Bind(R.id.location)
    TextView location;
    @Bind(R.id.condition)
    TextView condition;
    @Bind(R.id.temp)
    TextView temp;
    @Bind(R.id.tvHumidity)
    TextView tvHumidity;
    @Bind(R.id.tvPressure)
    TextView tvPressure;
    @Bind(R.id.tvWind)
    TextView tvWind;
    @Bind(R.id.tvWindDeg)
    TextView tvWindDeg;
    @Bind(R.id.et_city)
    EditText etCity;
    @Bind(R.id.tv_go)
    TextView tvGo;
    @Bind(R.id.textLayout)
    LinearLayout textLayout;
    @Bind(R.id.humidity_desc)
    TextView humidityDesc;
    @Bind(R.id.pres_desc)
    TextView presDesc;
    @Bind(R.id.ws_desc)
    TextView wsDesc;
    @Bind(R.id.wd_desc)
    TextView wdDesc;
    @Bind(R.id.ll_extraWeather)
    LinearLayout llExtraWeather;
    @Bind(R.id.weatherCard)
    CardView weatherCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        loadWeather(city);

    }

    @OnClick(R.id.refresh)
    public void refresh() {
        loadWeather(city);
    }

    private void loadWeather(final String city) {
        WeatherMap weatherMap = new WeatherMap(this, APP_ID);
        weatherMap.getCityWeather(city, new WeatherCallback() {

            @Override
            public void success(WeatherResponseModel response) {
                populateWeather(response);
            }

            @Override
            public void failure(String message) {

            }
        });

        weatherMap.getCityForecast(city, new ForecastCallback() {
            @Override
            public void success(ForecastResponseModel response) {
                ForecastResponseModel responseModel = response;
            }

            @Override
            public void failure(String message) {

            }
        });
    }

    private void populateWeather(WeatherResponseModel response) {

        Weather weather[] = response.getWeather();
        condition.setText(weather[0].getMain());
        temp.setText(TempUnitConverter.convertToCelsius(response.getMain().getTemp()).intValue() + " °C");
        //location.setText(response.getName());
        tvHumidity.setText(response.getMain().getHumidity() + "%");
        tvPressure.setText(response.getMain().getPressure() + " hPa");
        tvWind.setText(response.getWind().getSpeed() + "m/s");
        tvWindDeg.setText(response.getWind().getDeg() + "°");
        if(response.getName().equals("Seoul")&& Locale.getDefault().getLanguage().equals("ko")){
           location.setText("서울");
        }else{
            location.setText(response.getName());
        }
        if (Locale.getDefault().getLanguage().equals("ko")) {
            switch (weather[0].getMain()) {
                case "Mist":
                    condition.setText("안개");
                    break;
                case "Clouds":
                    condition.setText("구름 낀 하늘");
                    break;
                case "Clear":
                    condition.setText("맑음");
                    break;
                case "Haze":
                    condition.setText("연무");
                    break;
                case "Windy":
                    condition.setText("바람부는");
                    break;
                case "Shower drizzle":
                    condition.setText("소나기");
                    break;
                case "Heavy intensity rain":
                    condition.setText("강한 비");
                    break;
                case "Very heavy rain":
                    condition.setText("매우 강한 비");
                    break;
                case "Snow":
                    condition.setText("눈");
                    break;
                case "Light snow":
                    condition.setText("가벼운 눈");
                    break;
                case "Heavy snow":
                    condition.setText("강한 눈");
                    break;
                case "Sand":
                    condition.setText("모래 먼지");
                    break;
                default:
                    condition.setText(weather[0].getMain());
                    break;
            }
        }else {
            condition.setText(weather[0].getMain());
        }
        String link = weather[0].getIconLink();
        Picasso.with(this).load(link).into(weatherIcon);
    }

    @OnClick(R.id.tv_go)
    public void go() {
        city = etCity.getText().toString().trim();
        loadWeather(city);
    }
}
