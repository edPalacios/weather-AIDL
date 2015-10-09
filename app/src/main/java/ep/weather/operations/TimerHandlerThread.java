package ep.weather.operations;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;

import ep.weather.utils.CacheManager;

/**
 * Created by Eduardo on 03/06/2015.
 */
public class TimerHandlerThread extends HandlerThread {

    private static final String TAG = "TimerHandlerThread";
    private Handler handler = null;
    private File file;


    public TimerHandlerThread(File file) {
        super(TAG);
        start();
        handler = new Handler(getLooper());
        this.file = file;
    }

    synchronized void notifyThreadStart() {
        notify();
    }

    public void startCountDownToCleanCache() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyThreadStart();
                final long[] time = {10000};
                CountDownTimer countDownTimer = new CountDownTimer(time[0],1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        time[0] = millisUntilFinished;
                    }

                    @Override
                    public void onFinish() {
                        CacheManager.manualCleanCacheDir(file);
                    }
                };
                countDownTimer.start();
            }
        });
        try {
            wait();
        }
        catch (InterruptedException e) {
            Log.e(TAG, "wait was interrupted");
        }
    }


}
