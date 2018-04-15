package cf.vozhuo.app.broswser.search_history.storage;

import java.io.Serializable;

public class SearchBean implements Serializable {
    private String content;
    private String time;

    SearchBean(String time, String content) {
        this.time = time;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
