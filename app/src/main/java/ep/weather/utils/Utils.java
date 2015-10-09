package ep.weather.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ep.weather.aidl.WeatherData;

import ep.weather.jsonweather.JsonWeather;
import ep.weather.jsonweather.Weather;
import ep.weather.jsonweather.WeatherJSONParser;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * @class Utils
 *
 * @brief Handles the actual downloading of Weather information from
 *        the Weather web service.
 */
public class Utils {
    /**
     * Logging tag used by the debugger. 
     */
    private final static String TAG = Utils.class.getCanonicalName();

    /**
     * URL to the Weather web service.
     */
    private final static String sWeather_Web_Service_URL =
       "http://api.openweathermap.org/data/2.5/weather?q=";
    /**
     * URL to image icon Weather API Servie
     */
    private final static String IMAGE_URL = "http://openweathermap.org/img/w/";


    /**
     * Obtain the Weather information.
     * 
     * @return The information that responds to your current weather search.
     */
    public static List<WeatherData> getResults(final String weatherCity) {
        // Create a List that will return the WeatherData obtained
        // from the Weather Service web service.
        final List<WeatherData> returnList =
            new ArrayList<WeatherData>();

        // A List of JsonWeather objects.
        List<JsonWeather> jsonWeathers = null;
//        InputStream in;
        try {
            // Append the location to create the full URL.
            final URL url =
            new URL(sWeather_Web_Service_URL
                    + weatherCity);

            // Opens a connection to the Weather Service.
            HttpURLConnection urlConnection =
                    (HttpURLConnection) url.openConnection();
            Log.d(TAG, "status code: "+ urlConnection.getResponseCode());
            // Sends the GET request and reads the Json results.
            try (InputStream in =
                         new BufferedInputStream(urlConnection.getInputStream())) {
                // Create the parser.
                final WeatherJSONParser parser = new WeatherJSONParser();

                // Parse the Json results and create JsonWeather data
                // objects.
//
                jsonWeathers = parser.parseJsonStream(in);
                in.close();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonWeathers != null && jsonWeathers.size() > 0 && !jsonWeathers.isEmpty()) {
            // Convert the JsonWeather data objects to our WeatherData
            // object, which can be passed between processes.
            String iconCode = "";
            for (JsonWeather jsonWeather : jsonWeathers) {
                for (Weather weather : jsonWeather.getWeather()) {
                    iconCode = weather.getIcon();
                }

                returnList.add(new WeatherData(
                        jsonWeather.getName(),
                        iconCode,
                        jsonWeather.getWind().getSpeed(),
                        jsonWeather.getWind().getDeg(),
                        jsonWeather.getMain().getTemp(),
                        jsonWeather.getMain().getHumidity(),
                        jsonWeather.getSys().getSunrise(),
                        jsonWeather.getSys().getSunset(),
                        jsonWeather.getCoord().getLon(),
                        jsonWeather.getCoord().getLat(),
                        jsonWeather.getSys().getCountry()
                 )
                );

            }
            return returnList;
        }  else
            return null;
    }

    /**
     * Method that download the weather icon
     * @param imageCode
     * @return
     */
    public static byte[] getIconWeather(String imageCode) {
        byte [] iconByte;
        HttpURLConnection iconHttpURLConnection = null;
        InputStream iconInputStream;
        try {
            final URL urlIcon = new URL(IMAGE_URL + imageCode + ".png");
            iconHttpURLConnection = (HttpURLConnection) urlIcon.openConnection();
            iconInputStream = iconHttpURLConnection.getInputStream();
            iconByte = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int byteSize;
            while ( (byteSize = iconInputStream.read(iconByte)) > 0 ) {
                byteArrayOutputStream.write(iconByte, 0, byteSize);
            }
            if (byteArrayOutputStream.toByteArray() != null && byteArrayOutputStream.toByteArray().length > 0) {
                Log.d(TAG, "icon weather downloaded successfully");
                return  byteArrayOutputStream.toByteArray();
            }
            iconInputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "error downloading the icon from API ", e);
        } finally {
            if (iconHttpURLConnection != null) {
                iconHttpURLConnection.disconnect();
            }
        }

        return null;
    }


    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
           (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
