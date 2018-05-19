package tk.vozhuo.browser.interfaces.model;

import java.util.ArrayList;

import tk.vozhuo.browser.entity.SearchBean;

public interface OnSearchListener {
    void onSortSuccess(ArrayList<SearchBean> results);
    void searchSuccess(String value);
    void fuzzySearchSuccess(ArrayList<SearchBean> results);
}