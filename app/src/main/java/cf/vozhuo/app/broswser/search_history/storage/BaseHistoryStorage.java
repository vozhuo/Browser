package cf.vozhuo.app.broswser.search_history.storage;

import java.util.ArrayList;

public abstract class BaseHistoryStorage {

    public abstract void save(String value);
    public abstract void remove(String key);
    public abstract void clear();
    public abstract String generateKey();
    public abstract ArrayList<SearchBean> sortHistory();
    public abstract ArrayList<SearchBean> contains_key(String s);
}
