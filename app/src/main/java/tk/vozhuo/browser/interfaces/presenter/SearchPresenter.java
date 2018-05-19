package tk.vozhuo.browser.interfaces.presenter;

public interface SearchPresenter {
    void remove(String key);
    void clear();
    void sortHistory();
    void search(String value);
    void fuzzySearch(String value);
}
