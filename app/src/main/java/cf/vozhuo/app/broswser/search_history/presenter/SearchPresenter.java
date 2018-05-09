package cf.vozhuo.app.broswser.search_history.presenter;

public interface SearchPresenter {
    void remove(String key);
    void clear();
    void sortHistory();
    void search(String value);
    void fuzzySearch(String value);
}
