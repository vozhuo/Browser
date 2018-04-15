package cf.vozhuo.app.broswser.search_history.model;

import android.content.Context;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.search_history.storage.BaseHistoryStorage;
import cf.vozhuo.app.broswser.search_history.storage.SearchBean;
import cf.vozhuo.app.broswser.search_history.storage.SpHistoryStorage;

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
