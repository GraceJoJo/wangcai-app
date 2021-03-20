package com.jd.jrapp.other.marathon2020demo;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jd.jrapp.other.pet.ui.PetFloatWindow;
import com.jd.jrapp.other.pet.utils.AppManager;
import com.jd.jrapp.other.pet.utils.DisplayUtil;
import com.jd.jrapp.other.pet.utils.SensorManagerHelper;


public class MainActivity extends AppCompatActivity {
    // 要申请的权限
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        DisplayUtil.fitImage((Activity) this, (ImageView) findViewById(R.id.btn), DisplayUtil.getScreenWidth(this), DisplayUtil.getScreenHeight(this));
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });
    }

    private void requestPermissions() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions, 321);
            } else {
                PetFloatWindow.Companion.getInstance().checkAndShow(this);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String str = AppManager.getServiceInfo("你好啊");
//                        Log.e("--------", str);
//                    }
//                }).start();
                new SensorManagerHelper(MainActivity.this).setOnShakeListener(new SensorManagerHelper.OnShakeListener() {
                    @Override
                    public void onShake() {
                        PetFloatWindow.Companion.getInstance().showRedEnvelopDialog();
                    }
                });
            }
        } else {
            PetFloatWindow.Companion.getInstance().checkAndShow(this);
        }
    }
}