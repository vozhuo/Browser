package tk.vozhuo.browser.interfaces;

import tk.vozhuo.browser.entity.Tab;

public interface UiController extends WebViewController {
    void selectTab(Tab tab);
    void onTabCountChanged();
}
