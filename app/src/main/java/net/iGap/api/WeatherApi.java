package net.iGap.api;

import net.iGap.model.discoveryWeather.Cities;
import net.iGap.model.discoveryWeather.WeatherInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WeatherApi {

    @GET("get-cities")
    Call<List<Cities>> getCities();

    @GET("get-weather/5ea90ada146c9f23a0c54c4f")
    Call<WeatherInfo> getWeatherInfo();

}
