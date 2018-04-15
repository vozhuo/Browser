package cf.vozhuo.app.broswser.search_history;

public interface OnSearchHistoryListener {
    void onDelete(String key);
    void onSelect(String content);
}
