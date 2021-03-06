package com.jd.jrapp.other.pet.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.alibaba.idst.token.AccessToken;
import com.alibaba.idst.util.NlsClient;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
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
     * ??????Get??????????????????
     *
     * @param keyStr:???????????????????????????
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
        //??????ListView?????????Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { //listAdapter.getCount()????????????????????????
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //????????????View ?????????
            totalHeight += listItem.getMeasuredHeight(); //??????????????????????????????
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        //listView.getDividerHeight()???????????????????????????????????????
        //params.height??????????????????ListView???????????????????????????
        listView.setLayoutParams(params);
    }

    /**
     * ????????????????????????05???10???
     */
    public static String formatRecordTime(long recTime, long maxRecordTime) {
        int time = (int) ((maxRecordTime - recTime) / 1000);
        int minute = time / 60;
        int second = time % 60;
        //return String.format("%2d???%2d???", minute, second);
        return String.format("%2d:%2d", minute, second);
    }

}
