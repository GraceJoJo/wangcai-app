package com.jd.jrapp.other.pet.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;

import java.lang.reflect.Field;

/**
 * Created by yuguotao at 2020/9/3,11:29 AM
 */
public class FixAndroidOSystem {
    public static final String TAG = "FixAndroidOSystem";

    public FixAndroidOSystem() {
    }

    public static void fix(Activity activity) {
        if (Build.VERSION.SDK_INT == 26 && activity.getApplicationInfo().targetSdkVersion > 26 && isFixedOrientation(activity)) {
            TypedArray ta = activity.obtainStyledAttributes(new int[]{16842840, 16843763, 16842839});
            boolean isTranslucentOrFloating = isTranslucentOrFloating(ta);
            ta.recycle();
            if (isTranslucentOrFloating) {
                try {
                    Field fieldParams = Activity.class.getDeclaredField("mActivityInfo");
                    fieldParams.setAccessible(true);
                    ActivityInfo mActivityInfo = (ActivityInfo)fieldParams.get(activity);
                    mActivityInfo.screenOrientation = 3;
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }
        }

    }

    private static boolean isTranslucentOrFloating(TypedArray attributes) {
        boolean isTranslucent = attributes.getBoolean(0, false);
        boolean isSwipeToDismiss = !attributes.hasValue(0) && attributes.getBoolean(1, false);
        boolean isFloating = attributes.getBoolean(2, false);
        return isFloating || isTranslucent || isSwipeToDismiss;
    }

    private static boolean isFixedOrientation(Activity activity) {
        int orientation = activity.getRequestedOrientation();
        return isFixedOrientationLandscape(orientation) || isFixedOrientationPortrait(orientation) || orientation == 14;
    }

    private static boolean isFixedOrientationLandscape(int orientation) {
        return orientation == 0 || orientation == 6 || orientation == 8 || orientation == 11;
    }

    private static boolean isFixedOrientationPortrait(int orientation) {
        return orientation == 1 || orientation == 7 || orientation == 9 || orientation == 12;
    }
}
