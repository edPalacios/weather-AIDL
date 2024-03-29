package ep.weather.aidl;

import ep.weather.aidl.WeatherData;
import java.util.List;

/**
 * Interface defining the method that receives callbacks from the
 * WeatherServiceAsync.
 */
interface WeatherResults {
    /**
     * This one-way (non-blocking) method allows WeatherServiceAsync
     * to return the List of WeatherData results associated with a
     * one-way WeatherRequest.callWeatherRequest() call.
     */
   oneway void sendResults(in List<WeatherData> results);

    /**
     * This one-way (non-blocking) method allows WeatherServiceAsync
     * to return an error String if the Service fails for some reason.
     */
    oneway void sendError(in String reason);
}
