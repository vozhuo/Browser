package cf.vozhuo.app.broswser;

import android.content.Context;
import android.content.SharedPreferences;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cf.vozhuo.app.broswser.tab.SettingEntity;

public class SettingAdapter extends BaseMultiItemQuickAdapter<SettingEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SettingAdapter(List<SettingEntity> data) {
        super(data);
        addItemType(SettingEntity.CHECKBOX, R.layout.menu_switch_item);
        addItemType(SettingEntity.TEXT, R.layout.layout_menu_item);
    }

    @Override
    protected void convert(final BaseViewHolder helper, SettingEntity item) {
        SharedPreferences sp = mContext.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        switch (helper.getItemViewType()) {
            case SettingEntity.TEXT:
                helper.setText(R.id.tv_menu_title, item.getContent());
                switch (item.getContent()) {
                    case "搜索引擎":
                        helper.setText(R.id.tv_menu_content, sp.getString("search_engine", "百度"));
                        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                            @Override
                            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                                switch (key) {
                                    case "search_engine":
                                        helper.setText(R.id.tv_menu_content, sharedPreferences.getString(key, "百度"));
                                        break;
                                    default: break;
                                }
                            }
                        });
                        break;
                    case "设置UA":
                        helper.setText(R.id.tv_menu_content, sp.getString("ua", "Android"));
                        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                            @Override
                            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                                switch (key) {
                                    case "ua":
                                        helper.setText(R.id.tv_menu_content, sharedPreferences.getString(key, "Android"));
                                        break;
                                    default: break;
                                }
                            }
                        });
                        break;
                }
                break;
            case SettingEntity.CHECKBOX:
                helper.setText(R.id.switch_default, item.getContent());
                break;
        }
    }
}