package cf.vozhuo.app.broswser.download;

public class MyDownloadEntity {
    private int id;
    private String name;
    private String size;
    private String url;
    private String path;
    private String speed;
    private int progress;

    public MyDownloadEntity(int id, String url, String name, String size, String path) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.path = path;
        this.size = size;
//        this.speed = speed;
//        this.progress = progress;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
