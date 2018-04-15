package cf.vozhuo.app.broswser.search_history.presenter;


import java.util.ArrayList;

import cf.vozhuo.app.broswser.search_history.storage.SearchBean;

public interface SearchPresenter {
    void remove(String key);
    void clear();
    void sortHistory();
    void search(String value);
    void fuzzySearch(String value);
}
