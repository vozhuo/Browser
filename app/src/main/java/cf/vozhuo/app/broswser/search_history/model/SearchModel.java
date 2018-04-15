package cf.vozhuo.app.broswser.search_history.model;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.search_history.storage.SearchBean;

public interface SearchModel {
    void save(String value);
    void search(String value, OnSearchListener onSearchListener);
    void remove(String key);
    void clear();
    void sortHistory(OnSearchListener onSearchListener);
    void fuzzySearch(String value, OnSearchListener onSearchListener);
}