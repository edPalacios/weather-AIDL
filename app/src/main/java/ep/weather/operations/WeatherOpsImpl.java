package ep.weather.operations;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import ep.weather.R;
import ep.weather.activities.MainActivity;
import ep.weather.activities.MapWeatherActivity;

import ep.weather.aidl.OpenWeatherMap;
import ep.weather.aidl.WeatherCall;
import ep.weather.aidl.WeatherData;
import ep.weather.aidl.WeatherMapRequest;
import ep.weather.aidl.WeatherRequest;
import ep.weather.aidl.WeatherResults;
import ep.weather.services.WeatherMapServiceAsync;
import ep.weather.services.WeatherServiceAsync;
import ep.weather.services.WeatherServiceSync;
import ep.weather.utils.CacheManager;
import ep.weather.utils.GenericServiceConnection;
import ep.weather.utils.Serializer;
import ep.weather.utils.Utils;
import ep.weather.utils.WeatherDataArrayAdapter;

/**
 *  a new cleanup thread is spawned for each call); on the other hand, you are not even taking
 *  advantage of it because you always perform the web service call *before* doing a cache lookup...
 */

/**
 * This class implements all the acronym-related operations defined in
 * the WeatherOps interface.
 */
public class WeatherOpsImpl implements WeatherOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<MainActivity> mActivity;

    /**
     * The ListView that will display the results to the user.
     */
    protected WeakReference<ListView> mListView;

    /**
     * Acronym entered by the user.
     */
    protected WeakReference<EditText> mEditText;

    /**
     * List of results to display (if any).
     */
    protected List<WeatherData> mResults;

    /**
     * A custom ArrayAdapter used to display the list of WeatherData
     * objects.
     */
    protected WeakReference<WeatherDataArrayAdapter> mAdapter;

    protected String cityToCheckWeather;
    protected String currentCity;
    protected long previousTimeStamp;
    protected final static long TIME_TO_CHECK = 10000;
    protected String iconWeatherKey;
    protected TimerHandlerThread timerHandlerThread;
    protected WeakReference<Button> mMapButton;
    protected List<WeatherData> weatherDataListForMap;
    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the WeatherServiceSync Service using bindService().
     */
    private GenericServiceConnection<WeatherCall> mServiceConnectionSync;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the WeatherServiceAsync Service using bindService().
     */
    private GenericServiceConnection<WeatherRequest> mServiceConnectionAsync;

    private GenericServiceConnection<WeatherMapRequest> mServiceMapConnectionAsync;

    /**
     * Constructor initializes the fields.
     */
    public WeatherOpsImpl(MainActivity activity) {
        // Initialize the WeakReference.
        mActivity = new WeakReference<>(activity);

        // Finish the initialization steps.
        initializeViewFields();
        initializeNonViewFields();
    }

    /**
     * Initialize the View fields, which are all stored as
     * WeakReferences to enable garbage collection.
     */
    private void initializeViewFields() {
        Log.d(TAG, "initializeViewFields");
        // Get references to the UI components.
        mActivity.get().setContentView(R.layout.main_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = new WeakReference<>
            ((EditText) mActivity.get().findViewById(R.id.editText1));

        // Store the ListView for displaying the results entered.
        mListView = new WeakReference<>
            ((ListView) mActivity.get().findViewById(R.id.listView1));

        // Create a local instance of our custom Adapter for our
        // ListView.
        mAdapter = new WeakReference<>
            (new WeatherDataArrayAdapter(mActivity.get()));

        // Set the adapter to the ListView.
        mListView.get().setAdapter(mAdapter.get());

        mMapButton = new WeakReference<Button>
                ((Button) mActivity.get().findViewById(R.id.button3));

    }

    /**
     * (Re)initialize the non-view fields (e.g.,
     * GenericServiceConnection objects).
     */
    private void initializeNonViewFields() {
        Log.d(TAG, "initializeNonViewFields");
        mServiceConnectionSync =
            new GenericServiceConnection<WeatherCall>(WeatherCall.class);

        mServiceConnectionAsync =
            new GenericServiceConnection<WeatherRequest>(WeatherRequest.class);

        mServiceMapConnectionAsync =
                new GenericServiceConnection<WeatherMapRequest>(WeatherMapRequest.class);

        // Display results if any (due to runtime configuration change).
        if (mResults != null)
            displayResults(mResults);
        previousTimeStamp = 0;
        cityToCheckWeather = "";
        currentCity = "";
        timerHandlerThread = new TimerHandlerThread(mActivity.get().getCacheDir());
        weatherDataListForMap = new ArrayList<>();
    }

    /**
     * Called after a runtime configuration change occurs.
     */
    public void onConfigurationChanged(Configuration newConfig) {
        // Checks the orientation of the screen.
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            Log.d(TAG,
                    "Now running in landscape mode");
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            Log.d(TAG,
                  "Now running in portrait mode");
    }

    /**
     * Initiate the service binding protocol.
     */
    @Override
    public void bindService() {
        Log.d(TAG, "calling bindService()");

        // Launch the Weather Bound Services if they aren't already
        // running via a call to bindService(), which binds this
        // activity to the WeatherService* if they aren't already
        // bound.
        if (mServiceConnectionSync.getInterface() == null)
            mActivity.get().bindService
                (WeatherServiceSync.makeIntent(mActivity.get()),
                 mServiceConnectionSync,
                 Context.BIND_AUTO_CREATE);

        if (mServiceConnectionAsync.getInterface() == null)
            mActivity.get().bindService
                    (WeatherServiceAsync.makeIntent(mActivity.get()),
                            mServiceConnectionAsync,
                            Context.BIND_AUTO_CREATE);

        if (mServiceMapConnectionAsync.getInterface() == null)
            mActivity.get().bindService
                    (WeatherMapServiceAsync.makeIntent(mActivity.get()),
                            mServiceMapConnectionAsync,
                            Context.BIND_AUTO_CREATE);
    }

    /**
     * Initiate the service unbinding protocol.
     */
    @Override
    public void unbindService() {
        Log.d(TAG, "calling unbindService()");

        // Unbind the Async Service if it is connected.
        if (mServiceConnectionAsync.getInterface() != null)
            timerHandlerThread.quit(); // stop timer thread
            mActivity.get().unbindService
                    (mServiceConnectionAsync);


        // Unbind the Sync Service if it is connected.
        if (mServiceConnectionSync.getInterface() != null)
            timerHandlerThread.quit(); // stop timer thread
            mActivity.get().unbindService
                    (mServiceConnectionSync);

        if (mServiceMapConnectionAsync.getInterface() != null)
        mActivity.get().unbindService
                (mServiceMapConnectionAsync);
    }

    /*BUTTON START MAP ASYNC SERVICE******
        * Initiate the asynchronous map weather
        */
    public void openMapAsync (View v) {
        Log.d(TAG, "openMapAsync");
        WeatherMapRequest mapRequest = mServiceMapConnectionAsync.getInterface();
        if (mapRequest != null) {
            try {
                //instance of the aidl method with OpeWeatherMap aidl interface
                //weatherDataListForMap always keeps the last valid result
                mapRequest.weatherMapRequest(weatherDataListForMap, openWeatherMap);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException:" + e.getMessage());
            }
            Utils.showToast(mActivity.get(),"Loading Google Maps");
            if (v.isPressed()) {
                v.setVisibility(View.GONE);
            }
        }
    }


    /*BUTTON START ASYNC SERVICE******
     * Initiate the asynchronous weather lookup when the user presses
     * the "Look Up Async" button.
     */
    public void expandWeatherAsync(View v) {
        Log.d(TAG, "expandWeatherAsync");
        //reference to WeatherRequest AIDL interface
        WeatherRequest weatherRequest =
            mServiceConnectionAsync.getInterface();

        if (weatherRequest != null) {
            // Get the city entered by the user.
            cityToCheckWeather = mEditText.get().getText().toString();
            resetDisplay();

            try {
                // Invoke a one-way AIDL call, which does not block
                // the client.  The results are returned via the
                // sendResults() method of the mWeatherResults
                // callback object, which runs in a Thread from the
                // Thread pool managed by the Binder framework.
                weatherRequest.expandWeather(cityToCheckWeather,
                        mWeatherResults);
                Log.d(TAG, " weatherRequest.expandWeather");
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException:" + e.getMessage());
            }
        } else {
            Log.d(TAG, "weatherRequest was null.");
        }
    }

    /*
    BUTTON START SYNC SERVICE******
     * Initiate the synchronous weather lookup when the user presses
     * the "Look Up Sync" button.
     */
    public void expandWeatherSync(View v) {
        Log.d(TAG, "expandWeatherSync");
        final WeatherCall weatherCall =
            mServiceConnectionSync.getInterface();

        if (weatherCall != null) {
            // Get the city entered by the user.
//            final String
            cityToCheckWeather = mEditText.get().getText().toString();
            resetDisplay();



            // Use an anonymous AsyncTask to download the Weather data
            // in a separate thread and then display any results in
            // the UI thread.
            new AsyncTask<String, Void, List<WeatherData>> () {

                /**
                 * Weather we're trying to expand.
                 */
                private String mWeather;

                /**
                 * Retrieve the expanded weather results via a
                 * synchronous two-way method call, which runs in a
                 * background thread to avoid blocking the UI thread.
                 */
                protected List<WeatherData> doInBackground(String... acronyms) {
                    Log.d(TAG, "AsyncTask doInBackground");
                    List<WeatherData> weatherDataList;
                    try {
                        mWeather = acronyms[0];
                        weatherDataList = weatherCall.expandWeather(mWeather);
                       return getWeatherData(weatherDataList,cityToCheckWeather);

                    } catch (RemoteException | ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                /**
                 * Display the results in the UI Thread.
                 */
                protected void onPostExecute(List<WeatherData> weatherDataList) {
                    Log.d(TAG, "AsyncTask onPostExecute");
                    if (weatherDataList != null && weatherDataList.size() > 0 && !weatherDataList.isEmpty()) {
                        displayResults(weatherDataList);
                        saveDataToCache(weatherDataList, cityToCheckWeather);
                        mMapButton.get().setVisibility(View.VISIBLE);
                    }
                    else
                        Utils.showToast(mActivity.get(),
                                        "No data available for "
                                        + mWeather
                                        + " found");
                }
                // Execute the AsyncTask to expand the weather without
                // blocking the caller.
            }.execute(cityToCheckWeather);
        } else {
            Log.d(TAG, "mWeatherCall was null.");
        }
    }

    /**
     * Start a new Activity displaying the postition via Lat-Long
     * and the temp
     */
    private OpenWeatherMap.Stub openWeatherMap = new OpenWeatherMap.Stub() {
        @Override
        public void openMap(WeatherData data) throws RemoteException {
            Log.d(TAG, " OpenWeatherMap.Stub openWeatherMap");
            final Intent intent = new Intent(mActivity.get(), MapWeatherActivity.class);

            intent.putExtra("lon", data.mLon);
            intent.putExtra("lat", data.mLat);
            intent.putExtra("city", data.mName);
            intent.putExtra("temp", data.mTemp);

            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mActivity.get().startActivity(intent);
                }
            });

        }
    };


    /**
     * The implementation of the WeatherResults AIDL Interface, which
     * will be passed to the Weather Web service using the
     * WeatherRequest.expandWeather() method.
     *
     * This implementation of WeatherResults.Stub plays the role of
     * Invoker in the Broker Pattern since it dispatches the upcall to
     * sendResults().
     */
    private WeatherResults.Stub mWeatherResults = new WeatherResults.Stub() {
            /**
             * This method is invoked by the WeatherServiceAsync to
             * return   the results back to the WeatherActivity.
             */
            @Override
            public void sendResults(final List<WeatherData> weatherDataList)
                throws RemoteException {
                Log.d(TAG, "WeatherResults.Stub sendResults");
                 List<WeatherData> weatherDataListAsync = new ArrayList<>();
                try {
                   weatherDataListAsync = getWeatherData(weatherDataList,cityToCheckWeather);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }


                // Since the Android Binder framework dispatches this
                // method in a background Thread we need to explicitly
                // post a runnable containing the results to the UI
                // Thread, where it's displayed.
                final List<WeatherData> finalWeatherDataListAsync = weatherDataListAsync;
                mActivity.get().runOnUiThread(new Runnable() {
                    public void run() {

                        displayResults(finalWeatherDataListAsync);
                        weatherDataListForMap = finalWeatherDataListAsync;
                        mMapButton.get().setVisibility(View.VISIBLE);

                    }
                });
                saveDataToCache(weatherDataList, cityToCheckWeather);

            }


        /**
             * This method is invoked by the WeatherServiceAsync to
             * return error results back to the WeatherActivity.
             */
            @Override
            public void sendError(final String reason)
                throws RemoteException {

                // Since the Android Binder framework dispatches this
                // method in a background Thread we need to explicitly
                // post a runnable containing the results to the UI
                // Thread, where it's displayed.
                mActivity.get().runOnUiThread(new Runnable() {
                        public void run() {
                            Utils.showToast(mActivity.get(),
                                            reason);
                        }
                    });
            }
	};

    /**
     * Method that check if we have to read the data from cache if user request the same info
     * within 10 seconds
     * @param currentTimeStamp
     * @return
     */
    private boolean isDataInCache(long currentTimeStamp) {
        return ((currentTimeStamp - previousTimeStamp) < TIME_TO_CHECK);
    }

    /**
     * Method to get weather from API or cache
     * @param weatherDataList
     * @param cityToCheckWeather
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws RemoteException
     */
    private List<WeatherData> getWeatherData(List<WeatherData> weatherDataList, String cityToCheckWeather)
            throws IOException, ClassNotFoundException, RemoteException {
        //new thread to count 10 seconds time
        settingNewThreadTimer();
        //start new timeStamp record
        long currentTimeStamp = getCurrentTimeStamp();
        //check if user click same city in less than 10 seconds
        if (isDataInCache(currentTimeStamp)
                && currentCity.equalsIgnoreCase(cityToCheckWeather)
                && !cityToCheckWeather.isEmpty()) {
            Log.d(TAG, "reading city " + cityToCheckWeather + " from cache ");
            //save check in case timer has cleaned cache files
            if (CacheManager.getDirSize(mActivity.get().getCacheDir()) <= 0) {
                Log.d(TAG, "all data was deleted by timer. New API request");
                loadWeatherIcon(weatherDataList); //as the cache has been deleted we need to download again the icon weather
            } else {
                readFromCache(weatherDataList, cityToCheckWeather);
            }
        } else {
            Log.d(TAG, "reading city " + cityToCheckWeather + " from API web");
            loadWeatherIcon(weatherDataList);

        }
        updateFields(cityToCheckWeather, currentTimeStamp);
        weatherDataListForMap = weatherDataList;
        return weatherDataList;
    }

    private long getCurrentTimeStamp() {
        Date currentDatePerClick = new Date();
        return currentDatePerClick.getTime();
    }

    /**
     *  method that start the timer thread
     */
    private void settingNewThreadTimer() {
        TimerHandlerThread timerHandlerThread
                = new TimerHandlerThread(mActivity.get().getCacheDir());
        synchronized (timerHandlerThread) {
            timerHandlerThread.startCountDownToCleanCache();
        }
    }

    /**
     *  method that sets som fields to keep tracking the citys, countries
     * @param cityToCheckWeather
     * @param currentTimeStamp
     */
    private void updateFields(String cityToCheckWeather, long currentTimeStamp) {
        currentCity = cityToCheckWeather;
        previousTimeStamp = currentTimeStamp;
        iconWeatherKey = cityToCheckWeather;
    }

    /**
     *  method that
     * Downloads the icon from the Weather API and store it into the WeatherData object
     * @param weatherDataList
     */
    private void loadWeatherIcon(List<WeatherData> weatherDataList) {
        if (weatherDataList != null && !weatherDataList.isEmpty()) {
            weatherDataList.get(0).mIcon = Utils.getIconWeather(weatherDataList.get(0).mIconCode);
        }
    }

    /**
     *  method that reads from the cache when user request the same place within 10 secs
     * @param weatherDataList
     * @param cityToCheckWeather
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readFromCache(List<WeatherData> weatherDataList, String cityToCheckWeather)
            throws IOException, ClassNotFoundException {

        byte[] bytesToMap = CacheManager.loadDataFromCache(mActivity.get(), cityToCheckWeather);
        HashMap<Long, WeatherData> weatherDataMapFromCache
                = (HashMap<Long,WeatherData>) Serializer.deserialize(bytesToMap);
        if (!weatherDataList.isEmpty() && weatherDataMapFromCache != null) {
            weatherDataList.clear();
            weatherDataList.add(weatherDataMapFromCache.get(previousTimeStamp));
        }
    }

    /**
     * Method to save data in internal device memory
     * @param weatherDataList
     * @param cityToCheckWeather
     */
//
    private void saveDataToCache(List<WeatherData> weatherDataList, String cityToCheckWeather) {
        HashMap<Long, WeatherData> weatherDataMapAsync = new HashMap<Long, WeatherData>();
        weatherDataMapAsync.put(previousTimeStamp, weatherDataList.get(0));
        try {
            Log.d(TAG, "Saving data in cache");
            byte [] weatherMapToBytes = Serializer.serialize(weatherDataMapAsync);
            CacheManager.saveDataInCache(mActivity.get(), weatherMapToBytes, cityToCheckWeather);
        } catch (IOException e) {
            Log.e(TAG, "Error saving data or bitmap in cache", e);
        }
    }


    /**
     * Display the results to the screen.
     *
     * @param results
     *            List of Results to be displayed.
     */
    private void displayResults(List<WeatherData> results) {
        mResults = results;
        // Set/change data set.
        mAdapter.get().clear();
        mAdapter.get().addAll(mResults);
        mAdapter.get().notifyDataSetChanged();

    }

    /**
     * Reset the display prior to attempting to expand a new weather.
     */
    private void resetDisplay() {
        Utils.hideKeyboard(mActivity.get(),
                           mEditText.get().getWindowToken());
        mResults = null;
        mAdapter.get().clear();
        mAdapter.get().notifyDataSetChanged();
    }


}
