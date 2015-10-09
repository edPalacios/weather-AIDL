package ep.weather.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import ep.weather.aidl.OpenWeatherMap;
import ep.weather.aidl.WeatherData;
import ep.weather.aidl.WeatherMapRequest;

/**
 * Created by Eduardo on 05/06/2015.
 */
public class WeatherMapServiceAsync extends LifecycleLoggingService {



    /**
     * Factory method that makes an Intent used to start the
     * WeatherServiceAsync when passed to bindService().
     *
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        Log.d("WeatherMapServiceAsync", "makeIntent");
        return new Intent(context,
                WeatherMapServiceAsync.class);
    }

    /**
     * Called when a client (e.g., WeatherActivity) calls
     * bindService() with the proper Intent.  Returns the
     * implementation of WeatherRequest, which is implicitly cast as
     * an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("WeatherMapServiceAsync", "onBind");
        return mWeatherMapRequestImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface
     * WeatherRequest, which extends the Stub class that implements
     * WeatherRequest, thereby allowing Android to handle calls across
     * process boundaries.  This method runs in a separate Thread as
     * part of the Android Binder framework.
     *
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */

    WeatherMapRequest.Stub mWeatherMapRequestImpl = new WeatherMapRequest.Stub() {
        @Override
        public void weatherMapRequest(List<WeatherData> weatherList, OpenWeatherMap openMapCallback)
                throws RemoteException {
            Log.d("WeatherMapServiceAsync", "WeatherRequest.Stub weatherMapRequest");

            double lon = 0;
            double lat = 0 ;
            double temp = 0;
            String city = "";
            for (WeatherData data : weatherList) {
                lon = data.mLon;
                lat = data.mLat;
                temp = data.mTemp;
                city = data.mName;
            }
            WeatherData weatherData = new WeatherData(city, temp, lon, lat);
            openMapCallback.openMap(weatherData);
        }

    };
}
