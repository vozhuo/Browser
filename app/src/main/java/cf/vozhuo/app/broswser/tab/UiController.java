package cf.vozhuo.app.broswser.tab;

public interface UiController extends WebViewController {
    void selectTab(Tab tab);
    void onTabCountChanged();
}
