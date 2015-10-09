package ep.weather.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Eduardo on 03/06/2015.
 * Handle save and load cache of data.
 * Use it with Serializer.class
 */
public class CacheManager {

    private static final String TAG = "CacheManager";
    private static final long MAX_SIZE = 1048576L; // 5MB  5242880L 1MB 1048576

    private CacheManager() {
    }

    /**
     * Write data in cache
     * @param context
     * @param data
     * @param key
     * @throws IOException
     */
    public static void saveDataInCache(Context context, byte[] data, String key) throws IOException {
        File cacheDir = context.getCacheDir();
        long size = getDirSize(cacheDir);
        long newSize = data.length + size;
        if (newSize > MAX_SIZE) { // auto clean cache if max allowed its full
            cleanDir(cacheDir, newSize - MAX_SIZE);
            Log.d(TAG, "clean cache");
        }
        File file = new File(cacheDir, key);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            fileOutputStream.write(data);
        }
        finally {
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }

    /**
     * Read data from cache
     * @param context
     * @param key
     * @return
     * @throws IOException
     */
    public static byte[] loadDataFromCache(Context context, String key) throws IOException {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, key);
        if (!file.exists()) {
            Log.d(TAG, "file no exist");
            return null;
        }
        byte[] data = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            fileInputStream.read(data);
        }
        finally {
            fileInputStream.close();
        }

        return data;
    }

    /**
     * Delete data in save method if max size if full
     * @param dir
     * @param bytes
     */
    private static void cleanDir(File dir, long bytes) {
        long bytesDeleted = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            bytesDeleted += file.length();
            file.delete();
            if (bytesDeleted >= bytes) {
                break;
            }
        }
    }

    /**
     * Manual delete if needed with an associated file
     * @param dir
     */
    public static void manualCleanCacheDir(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
            Log.d(TAG, "file deleted " + file.getName());
        }
    }

    /**
     * Manual full delete
     * @param context
     */
    public static void fullCleanCacheDir(Context context) {
        File dir = context.getCacheDir();
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
            Log.d(TAG, "file deleted " + file.getName());
        }
    }

    /**
     * Size stored in our cache
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }


        return size;
    }

}
