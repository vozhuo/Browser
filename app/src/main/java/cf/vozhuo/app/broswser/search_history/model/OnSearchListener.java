package cf.vozhuo.app.broswser.search_history.model;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.search_history.storage.SearchBean;

public interface OnSearchListener {
    void onSortSuccess(ArrayList<SearchBean> results);
    void searchSuccess(String value);
    void fuzzySearchSuccess(ArrayList<SearchBean> results);
}