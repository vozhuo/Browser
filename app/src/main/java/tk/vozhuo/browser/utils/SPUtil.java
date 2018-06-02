package tk.vozhuo.browser.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.util.Patterns;
import android.webkit.URLUtil;

public class SPUtil {
    private static SharedPreferences sp;
    public static void setDayNightMode(Context context) {
        if(isNightMode(context)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    public static boolean isNightMode(Context context) {
        sp = context.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        return sp.getBoolean("dark_state", false);
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

    public static String getSearchUrl(Context context, String value) {
        sp = context.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        String search_string;
        String result;
        switch (sp.getString("search_engine", "百度")) {
            case "百度":
                search_string = "https://www.baidu.com/s?ie=UTF-8&wd=";
                break;
            case "谷歌":
                search_string = "https://www.google.com/search?q=";
                break;
            case "必应":
                search_string = "https://bing.com/search?q=";
                break;
            case "搜狗":
                search_string = "https://www.sogou.com/web?query=";
                break;
            default:
                search_string = "https://www.baidu.com/s?ie=UTF-8&wd=";
                break;
        }
        if(URLUtil.isValidUrl(value) || Patterns.WEB_URL.matcher(value).matches()) {
            if(!(URLUtil.isHttpsUrl(value) || URLUtil.isHttpsUrl(value))) {
                result = "http://" + value;
            } else {
                result = value;
            }
        } else {
            result = search_string + value;
        }
        return result;
    }
}