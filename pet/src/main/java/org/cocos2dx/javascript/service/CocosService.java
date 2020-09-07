package org.cocos2dx.javascript.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by yuguotao at 2020/9/3,8:37 PM
 */
public class CocosService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("AppActivity", "CocosService->onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}
