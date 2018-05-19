package tk.vozhuo.browser.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class SettingEntity implements MultiItemEntity {
    public static final int CHECKBOX = 1;
    public static final int TEXT = 2;
    private int itemType;
    private String content;

    public SettingEntity(String content, int itemType) {
        this.itemType = itemType;
        this.content = content  ;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
