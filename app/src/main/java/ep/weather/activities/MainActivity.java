package ep.weather.activities;

import ep.weather.operations.WeatherOps;
import ep.weather.operations.WeatherOpsImpl;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

/**
 * The main Activity that prompts the user for Weather to expand via
 * various implementations of WeatherServiceSync and
 * WeatherServiceAsync and view via the results.  Extends
 * LifecycleLoggingActivity so its lifecycle hook methods are logged
 * automatically.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Provides weather-related operations.
     */
    private WeatherOps mWeatherOps;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., runtime
     * configuration changes.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Create the WeatherOps object one time.
        mWeatherOps = new WeatherOpsImpl(this);

        // Initiate the service binding protocol.
        mWeatherOps.bindService();
    }

    /**
     * Hook method called by Android when this Activity is
     * destroyed.
     */
    @Override
    protected void onDestroy() {
        // Unbind from the Service.
        mWeatherOps.unbindService();

        // Always call super class for necessary operations when an
        // Activity is destroyed.
        super.onDestroy();
    }

    /**
     * Hook method invoked when the screen orientation changes.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mWeatherOps.onConfigurationChanged(newConfig);
    }

    /*
     * Synchronous service started through the WeatherOpsInterface (implemented in WeatherOpsIml)
     */
    public void expandWeatherSync(View v) {
        mWeatherOps.expandWeatherSync(v);
    }

    /*
     * Asynchronous service started through the WeatherOpsInterface (implemented in WeatherOpsIml)
     */
    public void expandWeatherAsync(View v) {
        mWeatherOps.expandWeatherAsync(v);
    }


    public void openMap(View view) {
        mWeatherOps.openMapAsync(view);
    }


}
