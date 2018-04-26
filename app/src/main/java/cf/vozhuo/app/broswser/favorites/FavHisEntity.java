package cf.vozhuo.app.broswser.favorites;

import android.graphics.Bitmap;

import java.io.Serializable;

public class FavHisEntity implements Serializable {
    private int id;
    private String title;
    private String url;
    private String time;
    private byte[] favicon;

    public FavHisEntity(int id, String title, String url, String time, byte[] favicon) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.time = time;
        this.favicon = favicon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public byte[] getFavicon() {
        return favicon;
    }

    public void setFavicon(byte[] favicon) {
        this.favicon = favicon;
    }
}
