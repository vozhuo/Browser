package cf.vozhuo.app.broswser.tab;

public interface UiController extends WebViewController {
//    void onWebsiteIconClicked(String url);
    void selectTab(Tab tab);
    void closeTab(Tab tab);
    void onTabCountChanged();
    void onTabDataChanged(Tab tab);
}
