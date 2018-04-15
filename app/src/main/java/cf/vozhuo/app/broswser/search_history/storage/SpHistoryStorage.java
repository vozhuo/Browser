package cf.vozhuo.app.broswser.search_history.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class SpHistoryStorage extends BaseHistoryStorage {

    private Context context;
    public static final String SEARCH_HISTORY = "search_history";
    private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
    private int MAX_HISTORY = 5;
    private static SpHistoryStorage instance;

    private SpHistoryStorage(Context context, int max_history) {
        this.context = context.getApplicationContext();
        this.MAX_HISTORY = max_history;
    }

    public static synchronized SpHistoryStorage getInstance(Context context, int max_history) {
        if (instance == null) {
            synchronized (SpHistoryStorage.class) {
                if (instance == null) {
                    instance = new SpHistoryStorage(context, max_history);
                }
            }
        }
        return instance;
    }

    @Override
        public void save(String value) {
        Map<String, String> histories = (Map<String, String>) getAll();
        for (Map.Entry<String, String> entry : histories.entrySet()) {
            if (value.equals(entry.getValue())) {
                remove(entry.getKey());
            }
        }
        put(generateKey(), value);
    }

    @Override
    public void remove(String key) {
        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    @Override
    public void clear() {
        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public String generateKey() {
        return mFormat.format(new Date());
    }

    @Override
    public ArrayList<SearchBean> sortHistory() {
//        Map<String, ?> allHistory = getAll();
        ArrayList<SearchBean> mResults = new ArrayList<>();
        Map<String, String> hisAll = (Map<String, String>) getAll();

        Object[] keys = hisAll.keySet().toArray();
        Arrays.sort(keys);
        int key_len = keys.length;

        int his_len = key_len > MAX_HISTORY ? MAX_HISTORY : key_len;
        for (int i = 1; i <= his_len; i++) {
            mResults.add(new SearchBean((String) keys[key_len - i], hisAll.get(keys[key_len - i])));
        }
        return mResults;
    }

    @Override
    public ArrayList<SearchBean> contains_key(String s) {
        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);

        ArrayList<SearchBean> mResults = new ArrayList<>();

        Map<String, String> contains = (Map<String, String>) getAll();
        for (Map.Entry<String, String> entry : contains.entrySet()) {
            if (entry.getValue().contains(s)) {
                mResults.add(new SearchBean(entry.getKey(), entry.getValue()));
                //remove(entry.getKey());
            }
        }

//        Object[] keys = contains.keySet().toArray();
//        Arrays.sort(keys);
//        int key_len = keys.length;
//
//        int his_len = key_len > MAX_HISTORY ? MAX_HISTORY : key_len;
//        for (int i = 1; i <= his_len; i++) {
//            mResults.add(new SearchBean((String) keys[key_len - i], contains.get(keys[key_len - i])));
//        }
        return mResults;
    }

    public Map<String, ?> getAll() {
        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    public void put(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(SEARCH_HISTORY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
