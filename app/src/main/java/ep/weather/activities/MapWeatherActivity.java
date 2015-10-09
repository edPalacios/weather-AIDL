package ep.weather.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ep.weather.R;
import ep.weather.aidl.WeatherData;
import ep.weather.utils.CacheManager;

public class MapWeatherActivity extends FragmentActivity implements OnMapReadyCallback  {

    private final static String TAG = "MapWeatherActivity";
//    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private WeatherData weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_weather);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        double lon = getIntent().getDoubleExtra("lon", 0);
        double lat = getIntent().getDoubleExtra("lat", 0);
        double temp = getIntent().getDoubleExtra("temp", 0);
        String city = getIntent().getStringExtra("city");
        weatherData = new WeatherData(city, temp, lon, lat);

        if (weatherData.mName != null) {
            Log.d(TAG, "info loaded");
        }


    }
    private long convertKevinToCelsius(WeatherData data) {
        return Math.round(data.mTemp - 272.15);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(weatherData.mLat, weatherData.mLon))
                .title(weatherData.mName)
                .snippet(String.valueOf(convertKevinToCelsius(weatherData))+ "\u2103")
        ).showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(weatherData.mLat, weatherData.mLon)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CacheManager.fullCleanCacheDir(this);
        Log.d(TAG, "cache cleaned");
    }
}
