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

//                context.setTheme(R.style.NightTheme);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                context.setTheme(R.style.AppTheme);
        }
    }
}