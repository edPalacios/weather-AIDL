package ep.weather.aidl;

import ep.weather.aidl.OpenWeatherMap;
import java.util.List;
import ep.weather.aidl.WeatherData;

interface WeatherMapRequest {
    oneway void weatherMapRequest (in List<WeatherData> weatherList,
                                  in OpenWeatherMap openMapCallback);
}
