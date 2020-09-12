package org.cocos2dx.javascript.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import org.cocos2dx.javascript.AppActivity;
import org.cocos2dx.javascript.ICocosInterface;
import org.cocos2dx.lib.Cocos2dxDialog;

/**
 * Created by yuguotao at 2020/9/3,8:37 PM
 */
public class CocosService extends Service {

    private Cocos2dxDialog mCocos2dxDialog;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    ICocosInterface.Stub mBinder = new ICocosInterface.Stub() {

        @Override
        public void showPetDialog() throws RemoteException {
            Log.e("AppActivity", "remote showPetDialog");
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mCocos2dxDialog != null && mCocos2dxDialog.isShowing()) {
                        return;
                    }
                    mCocos2dxDialog = new Cocos2dxDialog(getApplicationContext());
                    if (Build.VERSION.SDK_INT >= 25) {
                        mCocos2dxDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                    } else {
                        mCocos2dxDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    }
                    mCocos2dxDialog.show();
//                    Intent intent = new Intent(CocosService.this, AppActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                }
            });
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("AppActivity", "CocosService->onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}
