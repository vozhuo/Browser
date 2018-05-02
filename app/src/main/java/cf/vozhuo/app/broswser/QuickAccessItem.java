package cf.vozhuo.app.broswser;

import android.graphics.Bitmap;

public class QuickAccessItem {
    private String title;
    private String url;
    private Bitmap favicon;

    public QuickAccessItem(String title, String url) {
        this.title = title;
        this.url = url;
//        this.favicon = favicon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getFavicon() {
        return favicon;
    }

    public void setFavicon(Bitmap favicon) {
        this.favicon = favicon;
    }
}
