package cf.vozhuo.app.broswser.favorites;

import java.io.Serializable;

public class FavHisEntity implements Serializable {
    private int id;
    private String title;
    private String url;
    private String time;

    public FavHisEntity(int id, String title, String url, String time) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.time = time;
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
}
