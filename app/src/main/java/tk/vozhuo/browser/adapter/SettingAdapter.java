package tk.vozhuo.browser.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import tk.vozhuo.browser.R;
import tk.vozhuo.browser.ui.activity.SettingActivity;
import tk.vozhuo.browser.entity.SettingEntity;

public class SettingAdapter extends BaseMultiItemQuickAdapter<SettingEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SettingAdapter(List<SettingEntity> data) {
        super(data);
        addItemType(SettingEntity.CHECKBOX, R.layout.item_setting_switch);
        addItemType(SettingEntity.TEXT, R.layout.item_setting_notice);
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
                        break;
                    case "设置UA":
                        helper.setText(R.id.tv_menu_content, sp.getString("ua", "Android"));
                        break;
                        default:break;
                }
                break;
            case SettingEntity.CHECKBOX:
                Log.e(TAG, "convert: "+((SettingActivity)mContext).isDefaultBrowser());
                helper.setText(R.id.switch_default, item.getContent())
                        .setChecked(R.id.switch_default, ((SettingActivity)mContext).isDefaultBrowser())
                        .addOnClickListener(R.id.switch_default);
                break;
                default:break;
        }
    }
}