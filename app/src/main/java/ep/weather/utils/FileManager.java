package ep.weather.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import ep.weather.aidl.WeatherData;

/**
 * Created by Eduardo on 28/05/2015.
 * NOT IN USE.
 * This class save and loads data from internal memory
 * This was the first approach to save and manage the weather info.
 * For reuse, change HashMap<Long, WeatherData> to Object or whathever.
 */
public final class FileManager {
    public static boolean isDataSaved = false;
    private FileManager() {}

    private final static String TAG = "CacheData";


    public static void deleteData(Context context, String key)throws IOException {
        File file = context.getFileStreamPath(key);
        boolean delete = file.delete();
        Log.i(TAG, "file "+key+" was deleted");
    }


    public static void saveWeatherMapDataInCache
    (Context context, String key, HashMap<Long, WeatherData> weatherDataMap) throws IOException {

        if (!weatherDataMap.isEmpty()) {
            FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(weatherDataMap);
            Log.i(TAG, "city save is " + key);
            isDataSaved = true;
            oos.close();
            fos.flush();
            fos.close();
        }
    }

    public static HashMap<Long, WeatherData> loadWeatherDataMapFromCache
    (Context context, String key) throws IOException, ClassNotFoundException {

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
             fis = context.openFileInput(key);
             ois = new ObjectInputStream(fis);
            return (HashMap<Long, WeatherData>) ois.readObject();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "the file was empty. The key: "
                    + key +" isn't valid but was saved previously",e);
        } finally {
            if (fis != null && ois != null) {
                fis.close();
                ois.close();
            }
        }
        return null;
    }


}
