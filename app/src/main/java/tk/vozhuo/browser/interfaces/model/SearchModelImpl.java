package tk.vozhuo.browser.interfaces.model;

import android.content.Context;

import tk.vozhuo.browser.base.BaseHistoryStorage;
import tk.vozhuo.browser.db.SpHistoryStorage;

public class SearchModelImpl implements SearchModel {
    private BaseHistoryStorage historyStorage;

    public SearchModelImpl(Context context, int historyMax) {
        historyStorage = SpHistoryStorage.getInstance(context, historyMax);
    }

    @Override
    public void save(String value) {
        historyStorage.save(value);
    }

    @Override
    public void search(String value, OnSearchListener onSearchListener) {
        onSearchListener.searchSuccess(value);
    }
    @Override
    public void remove(String key) {
        historyStorage.remove(key);
    }

    @Override
    public void clear() {
        historyStorage.clear();
    }

    @Override
    public void sortHistory(OnSearchListener onSearchListener) {
        onSearchListener.onSortSuccess(historyStorage.sortHistory());
    }

    @Override
    public void fuzzySearch(String value, OnSearchListener onSearchListener) {
        onSearchListener.fuzzySearchSuccess(historyStorage.contains_key(value));
//        return historyStorage.contains_key(value);
    }
}
