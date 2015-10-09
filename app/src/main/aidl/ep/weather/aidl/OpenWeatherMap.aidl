package ep.weather.aidl;
import ep.weather.aidl.WeatherData;

interface OpenWeatherMap {
    oneway void openMap(in WeatherData data);
}
