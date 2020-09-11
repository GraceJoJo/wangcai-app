package org.cocos2dx.javascript;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuguotao at 2020/9/5,8:19 PM
 */
public class JavaCocosBridge {
    static String TAG = "JavaCocosBridge";

    private static ArrayMap<String, List<CallJavaListener>> mListeners = new ArrayMap(4);

    /**
     * java端调用cocos端方法
     * @param jsonStr
     */
    public static void callCocos(String jsonStr) {
        Log.e(TAG, "android native, call Cocos:" + jsonStr);
        Cocos2dxJavascriptJavaBridge.evalString("CocosJavaBridge.callCocos('"+jsonStr+"')");
    }

    public static void callCocos(String type, String param) {
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            if (!TextUtils.isEmpty(param)) {
                jsonObject.put("param",param);
            }
            callCocos(jsonObject.toString());
        }catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * cocos端调用java端方法
     *
     * @param jsonStr, {"type":"1", "param":""}
     *
     */
    public static void callJava(String jsonStr) {
        // 事件处理
        Log.e(TAG, "callJava:"+jsonStr);
        dispatchCallToJava(jsonStr);
    }

    private static void dispatchCallToJava(String jsonStr) {
        String type = null;
        String param = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            type = jsonObject.optString("type");
            param = jsonObject.optString("param");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(type)) {
            return;
        }
        List<CallJavaListener> listenrList = mListeners.get(type);
        if ( listenrList != null && listenrList.size() > 0){
            for (CallJavaListener listenr: listenrList) {
                listenr.onCall(param);
            }
        }
    }

    public static void registCallJavaListener(String type, CallJavaListener listener) {
        List<CallJavaListener> listenerList = mListeners.get(type);
        if (listenerList == null) {
            listenerList = new ArrayList<>();
            mListeners.put(type, listenerList);
        }
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public static void unRegistCallJavaListener(String type, CallJavaListener listener) {
        List<CallJavaListener> listenerList = mListeners.get(type);
        if (listenerList != null) {
            listenerList.remove(listener);
        }
    }

    public interface CallJavaListener{
        void onCall(String param);
    }
}
