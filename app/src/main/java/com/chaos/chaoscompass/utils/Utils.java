package com.chaos.chaoscompass.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;

import com.chaos.chaoscompass.AppDelegate;
import com.chaos.chaoscompass.R;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;

/*import miui.os.SystemProperties;
import miui.util.AppConstants;*/

public class Utils {
    private static final String CHANNEL_NAME = "mi_stat_channel";
    private static final String DEFAULT_CHANNEL_MIUI = "miui";
    private static final String EMPTY_CHANNEL_VALUE = "${channel_value}";
    private static final String HIDE_GESTURE_LINE = "hide_gesture_line";
    //private static final String[] LANGUAGE_LIST
    // = CompassApplication.getAppContext().getResources().getStringArray(R.array.compass_locale_language);
    private static final String USE_GESTURE_VERSION_THREE = "use_gesture_version_three";
    private static String sChannelValue;
    private static final HashMap<String, Typeface> sTypefaceMap = new HashMap<>();

    public static float normalizeDegree(float f) {
        return (f + 720.0f) % 360.0f;
    }

    public static final Typeface getTypeface(AssetManager assetManager, String str) {
        Typeface typeface = (Typeface) sTypefaceMap.get(str);
        if (typeface != null) {
            return typeface;
        }
        Typeface createFromAsset = Typeface.createFromAsset(assetManager, str);
        sTypefaceMap.put(str, createFromAsset);
        return createFromAsset;
    }

    public static boolean isNetworkAvailable(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
    }

    public static boolean isWifi(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (activeNetworkInfo == null || activeNetworkInfo.getType() != 1) {
            return false;
        }
        return true;
    }

    public static double calculateSLP(double d, double d2) {
        return d2 / Math.pow(1.0d - (d / 44330.0d), 5.255d);
    }

    public static String getLocationString(double d) {
        int i = (int) d;
        int i2 = (int) ((d - ((double) i)) * 3600.0d);
        int i3 = i2 / 60;
        int i4 = i2 % 60;
        /*String[] strArr = LANGUAGE_LIST;
        if (strArr != null && strArr.length > 0) {
            String language = Locale.getDefault().getLanguage();
            for (String equals : LANGUAGE_LIST) {
                if (TextUtils.equals(equals, language)) {
                    return AppDelegate.getAppContext().getString(R.string.altitude_longitude_Degree_format, new Object[]{Integer.valueOf(i), Integer.valueOf(i3), Integer.valueOf(i4)});
                }
            }
        }*/

        Context context = AppDelegate.getAppContext();

        // TODO: 8/28/2020 make sure it wont get exception
        return context != null ? String.format(Locale.US, context
                        .getString(R.string.altitude_longitude_Degree_format),
                i, i3, i4) : "";
    }

    public static void dismissDialog(AlertDialog alertDialog) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public static String formatToArabicNums(Context context, int i, float f) {
        /*if (Math.round(f) == 0) {
            f = 0.0f;
        }
        String[] strArr = LANGUAGE_LIST;
        if (strArr != null && strArr.length > 0) {
            String language = Locale.getDefault().getLanguage();
            for (String equals : LANGUAGE_LIST) {
                if (TextUtils.equals(equals, language)) {
                    return context.getString(i, new Object[]{Float.valueOf(f)});
                }
            }
        }*/
        return String.format(Locale.US, context.getString(i), new Object[]{Float.valueOf(f)});
    }

    public static boolean isRTL() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    public static int getStatusBarHeight(Context context) {
        int identifier = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return 0 + context.getApplicationContext().getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int getNavigationBarHeight(Activity activity) {
        /*if (activity.isInMultiWindowMode()) {
            return 0;
        }*/
        Context context = activity.getWindow().getContext();
        int i = isInFullWindowGestureMode(context) ? (!isMiuiXIISdkSupported() || !isSupportGestureLine(context) || !isEnableGestureLine(context)) ? 0 : getNavigationBarHeightFromProp(context) : getNavigationBarHeightFromProp(context);
        if (i < 0) {
            i = 0;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" getNavigationBarHeight = ");
        sb.append(i);
        Log.d("Utils", sb.toString());
        return i;
    }

    public static int getNavigationBarHeightFromProp(Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int dimensionPixelSize = identifier > 0 ? resources.getDimensionPixelSize(identifier) : 0;
        StringBuilder sb = new StringBuilder();
        sb.append("getNavigationBarHeightFromProp = ");
        sb.append(dimensionPixelSize);
        Log.i("DisplayUtils", sb.toString());
        return dimensionPixelSize;
    }

    public static boolean isSupportGestureLine(Context context) {
        try {
            Method declaredMethod = Class.forName("android.provider.MiuiSettings$Global").getDeclaredMethod("getBoolean", new Class[]{ContentResolver.class, String.class});
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(null, new Object[]{context.getContentResolver(), USE_GESTURE_VERSION_THREE})).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEnableGestureLine(Context context) {
        return Global.getInt(context.getContentResolver(), HIDE_GESTURE_LINE, 0) == 0;
    }

    public static int getScreenHeight(Display display) {
        Point point = new Point();
        if (VERSION.SDK_INT >= 17) {
            display.getRealSize(point);
        } else {
            display.getSize(point);
        }
        display.getRealSize(point);
        return point.y;
    }

    public static boolean hasNotchScreen() {
        int i;
        try {
            i = 0;//SystemProperties.getInt("ro.miui.notch", 0);
        } catch (Exception e) {
            e.printStackTrace();
            i = 0;
        }
        if (i > 0) {
            return true;
        }
        return false;
    }

    public static boolean hasHideNotchScreen() {
        return false/*Global.getInt(CompassApplication.getAppContext().getContentResolver(), "force_black", 0) == 1*/;
    }

    public static boolean isInFullWindowGestureMode(Context context) {
        return Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
    }

    public static boolean isAtLeastAndroidP() {
        return VERSION.SDK_INT >= 28;
    }

    public static String getChannelValue(Context context) {
        if (TextUtils.isEmpty(sChannelValue)) {
            try {
                sChannelValue = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.getString(CHANNEL_NAME);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(sChannelValue) || sChannelValue.equals(EMPTY_CHANNEL_VALUE)) {
                sChannelValue = DEFAULT_CHANNEL_MIUI;
            }
        }
        return sChannelValue;
    }

    public static int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static boolean isInARLan() {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("ar");
    }

    public static boolean isInFaIRLan() {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("fa");
    }

    public static boolean isMiuiXIISdkSupported() {
        return false/*AppConstants.getSdkLevel(AppConstants.getCurrentApplication(), "com.miui.core") >= 20*/;
    }
}
