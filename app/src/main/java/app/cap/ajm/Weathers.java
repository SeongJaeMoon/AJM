package app.cap.ajm;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import app.cap.ajm.Adapter.WeatherDBHelper;
import butterknife.BindView;
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
    @BindView(R.id.weather_title)
    TextView weatherTitle;
    @BindView(R.id.refresh)
    ImageButton refresh;
    @BindView(R.id.weather_icon)
    ImageView weatherIcon;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.condition)
    TextView condition;
    @BindView(R.id.temp)
    TextView temp;
    @BindView(R.id.tvHumidity)
    TextView tvHumidity;
    @BindView(R.id.tvPressure)
    TextView tvPressure;
    @BindView(R.id.tvWind)
    TextView tvWind;
    @BindView(R.id.tvWindDeg)
    TextView tvWindDeg;
    @BindView(R.id.et_city)
    EditText etCity;
    @BindView(R.id.tv_go)
    TextView tvGo;
    @BindView(R.id.textLayout)
    LinearLayout textLayout;
    @BindView(R.id.humidity_desc)
    TextView humidityDesc;
    @BindView(R.id.pres_desc)
    TextView presDesc;
    @BindView(R.id.ws_desc)
    TextView wsDesc;
    @BindView(R.id.wd_desc)
    TextView wdDesc;
    @BindView(R.id.ll_extraWeather)
    LinearLayout llExtraWeather;
    @BindView(R.id.weatherCard)
    CardView weatherCard;
    private WeatherDBHelper weatherDBHelper;
    private List<app.cap.ajm.Weather>weathers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        loadWeather(city);
        weatherDBHelper = new WeatherDBHelper(this);
        weathers = new ArrayList<app.cap.ajm.Weather>();
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
        tvHumidity.setText(response.getMain().getHumidity() + "%");
        tvPressure.setText(response.getMain().getPressure() + " hPa");
        tvWind.setText(response.getWind().getSpeed() + "m/s");
        tvWindDeg.setText(response.getWind().getDeg() + "°");
        if(response.getName().equals("Seoul")&& Locale.getDefault().getLanguage().equals("ko")){
           location.setText("서울");
        }else{
            location.setText(response.getName());
        }
        String key = weather[0].getMain();
        if (Locale.getDefault().getLanguage().equals("ko")) {
            String value = getWeatherValue(key);
            if(value !=null){
                condition.setText(value);
            }else{
                condition.setText(key);
            }
        }else {
            condition.setText(key);
        }
        String link = weather[0].getIconLink();
        Picasso.with(this).load(link).into(weatherIcon);
    }

    @OnClick(R.id.tv_go)
    public void go() {
        city = etCity.getText().toString().trim();
        loadWeather(city);
    }

    public String getWeatherValue(String key){

        String value = null;
        weatherDBHelper.open();
        weathers = weatherDBHelper.fetchForList();
        for(app.cap.ajm.Weather w : weathers){
            if(w.getKey().equals(key))
                value = w.getValue();
        }
        return value;
    }
}
