package ep.weather.aidl;

import ep.weather.aidl.WeatherResults;

/**
 * Interface defining the method that the WeatherServiceAsync will
 * implement to provide asynchronous access to the Weather Web
 * service.
 */
interface WeatherRequest {
   /**
    * A one-way (non-blocking) call to the WeatherServiceAsync that
    * retrieves information about an weather from the Weather Web
    * service.  The WeatherServiceAsync subsequently uses the
    * WeatherResults parameter to return a List of WeatherData
    * containing the results from the Web service back to the
    * WeatherActivity.
    */
    oneway void expandWeather (in String weather,
                                  in WeatherResults results);
}
