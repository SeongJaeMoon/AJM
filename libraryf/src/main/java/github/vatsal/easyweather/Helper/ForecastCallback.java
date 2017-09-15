package github.vatsal.easyweather.Helper;

import github.vatsal.easyweather.retrofit.models.ForecastResponseModel;

public abstract class ForecastCallback {

    public abstract void success(ForecastResponseModel response);

    public abstract void failure(String message);
}
