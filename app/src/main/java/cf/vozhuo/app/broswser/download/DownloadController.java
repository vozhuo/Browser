package cf.vozhuo.app.broswser.download;

public interface DownloadController {
    void suspendTask(String url, int position);
    void selectTask(String url, int position);
    void showBox(String url, int position);
    int getProcess();
    boolean isRunningTask(String url);
}
