package cf.vozhuo.app.broswser.search_history.view;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.search_history.storage.SearchBean;

public interface MySearchView {
    void searchSuccess(String value);
    void showContains(ArrayList<SearchBean> results);
}
