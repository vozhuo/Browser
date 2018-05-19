package tk.vozhuo.browser.interfaces;

import java.util.ArrayList;

import tk.vozhuo.browser.entity.SearchBean;

public interface MySearchView {
    void searchSuccess(String value);
    void showContains(ArrayList<SearchBean> results);
}
