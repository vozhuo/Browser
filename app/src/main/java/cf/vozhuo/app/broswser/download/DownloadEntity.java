package cf.vozhuo.app.broswser.download;

public class DownloadEntity {
    private int id;
    private String name;
    private String size;
    private String url;
    private String path;

    public DownloadEntity(int id, String url, String name, String size, String path) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.path = path;
        this.size = size;
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
