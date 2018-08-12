package com.sulibo.services;

import android.os.Looper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;

/**
 * Created by 水明 on 2018/1/10.
 */

public class ActivityResultBus extends Bus {



    private static ActivityResultBus instance;

    public static ActivityResultBus getInstance() {
        if (instance == null)
            instance = new ActivityResultBus();
        return instance;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void postQueue(final Object obj) {
        Log.i("mytags"," 在这里3");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("mytags"," 在这里4");
                ActivityResultBus.getInstance().post(obj);
            }
        });
    }
}
