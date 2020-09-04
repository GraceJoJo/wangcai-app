package com.jd.jrapp.other.pet.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.alibaba.idst.token.AccessToken;
import com.alibaba.idst.util.NlsClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Author: chenghuan15
 * Date: 2020/9/1
 * Time: 5:33 PM
 */

public class AppManager {
    private static Gson gson;
    public static AppManager appManager = null;
    public static final String NLS_App_KEY = "v3iVhMbn6SK7hVLD";//appKey
    public static final String NLS_ACCESS_KEY = "LTAI4GH4R2RBEyJiSbmJ5cJS";//AccessKey
    public static final String NLS_ACCESS_SECRET = "tDoIOFF4NmitymMgPcmkxkD7tfj6Ew";
    public static AccessToken accessToken;
    public static NlsClient client;

    public static synchronized AppManager getInstance() {
        if (appManager == null) {
            appManager = new AppManager();
        }
        return appManager;
    }

    public void getNLSToken() {
        client = new NlsClient();
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            accessToken = new AccessToken(NLS_ACCESS_KEY, NLS_ACCESS_SECRET);
            try {

                accessToken.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public static Object fromJson(String json, Type typeOfT) {
        if (null == gson) {
            gson = new Gson();
        }
        try {
            return gson.fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送Get请求到服务器
     *
     * @param keyStr:接口地址（带参数）
     * @return
     */
    public static String getServiceInfo(String keyStr) {
        String httpUrl = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + keyStr;
        String strResult = "";
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            strResult = buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResult;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

}
