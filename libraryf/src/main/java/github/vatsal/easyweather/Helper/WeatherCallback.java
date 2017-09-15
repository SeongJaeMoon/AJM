package github.vatsal.easyweather.Helper;

import github.vatsal.easyweather.retrofit.models.WeatherResponseModel;


public abstract class WeatherCallback {

    public abstract void success(WeatherResponseModel response);

    public abstract void failure(String message);
}
