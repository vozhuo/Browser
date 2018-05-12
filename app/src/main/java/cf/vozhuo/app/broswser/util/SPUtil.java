package cf.vozhuo.app.broswser.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

public class SPUtil {
    private static SharedPreferences sp;
    public static void setNightMode(Context context) {
        sp = context.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        if(sp.getBoolean("dark_state", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    public static boolean isNoTrackMode(Context context) {
        sp = context.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        return sp.getBoolean("track_state", false);
    }
    public static boolean isNoImageMode(Context context) {
        sp = context.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        return sp.getBoolean("image_state", false);
    }

    public static String getUA(Context context) {
        sp = context.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        return sp.getString("ua", "Android");
    }
}