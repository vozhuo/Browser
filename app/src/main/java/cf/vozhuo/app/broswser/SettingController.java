package cf.vozhuo.app.broswser;

public interface SettingController {
    void showFragment(int position);
    void clearDefaultAndSet(boolean checked);
    boolean isDefaultBrowser();
}
