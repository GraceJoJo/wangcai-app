package org.cocos2dx.javascript;

import android.util.Log;

import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;

/**
 * Created by yuguotao at 2020/9/5,8:19 PM
 */
public class JavaCocosBridge {
    static String TAG = "JavaCocosBridge";

    /**
     * java端调用cocos端方法
     * @param jsonStr
     */
    public static void callCocos(String jsonStr) {
        Cocos2dxJavascriptJavaBridge.evalString("CocosJavaBridge.callCocos('"+jsonStr+"')");
    }

    /**
     * cocos端调用java端方法
     *
     * @param jsonStr
     */
    public static void callJava(String jsonStr) {
        // TODO: 事件处理
        Log.e(TAG, "callJava:"+jsonStr);

    }
}
