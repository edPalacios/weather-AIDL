package ep.weather.aidl;

import ep.weather.aidl.WeatherData;
import java.util.List;

/**
 * Interface defining the method that the WeatherServiceSync will
 * implement to provide synchronous access to the Weather Web service.
 */
interface WeatherCall {
   /**
    * A two-way (blocking) call to the WeatherServiceSync that
    * retrieves information about an weather from the Weather Web
    * service and returns a list of WeatherData containing the results
    * from the Web service back to the WeatherActivity.
    */
  List<WeatherData> expandWeather (in String weather);
}
