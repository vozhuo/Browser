package tk.vozhuo.browser.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.inf.IEntity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import tk.vozhuo.browser.R;
import tk.vozhuo.browser.ui.activity.DownloadActivity;
import tk.vozhuo.browser.utils.DownloadUtil;

public class DownloadAdapter extends BaseQuickAdapter<DownloadEntity, BaseViewHolder> {
    private boolean isShowBox = false;
    private SparseBooleanArray sba = new SparseBooleanArray();
    public DownloadAdapter(@Nullable List<DownloadEntity> data) {
        super(R.layout.item_download, data);
        initSba();
    }
    public void initSba() {
        Log.e(TAG, "initSba: "+ getItemCount());
        for (int i = 0; i < getItemCount(); i++) {
            sba.put(i, false);
        }
    }
    @Override
    protected void convert(final BaseViewHolder helper, DownloadEntity item) {
        helper.setText(R.id.tv_download_name, DownloadUtil.getFileName(item.getDownloadPath()))
                .setText(R.id.tv_download_size, DownloadUtil.getFileSize(item.getFileSize()))
                .setOnCheckedChangeListener(R.id.cb_download, new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Log.e(TAG, "onCheckedChanged: " + isChecked + " " + sba.size());
                        sba.put(helper.getLayoutPosition(), isChecked);
                    }
                })
                .setChecked(R.id.cb_download, sba.get(helper.getLayoutPosition()))
                .setGone(R.id.cb_download, isShowBox)
                .setGone(R.id.iv_download_control, !item.isComplete());

        ImageButton control = helper.getView(R.id.iv_download_control);
        TextView speed = helper.getView(R.id.tv_download_speed);
        //文件未下载完成
        if(!item.isComplete()) {
            if(item.getState() == IEntity.STATE_RUNNING) {
                control.setSelected(false);
            } else {
                speed.setText("暂停");
                control.setSelected(true);
            }
        }
    }
    //设置是否显示CheckBox
    public void setShowBox() {
        //取反
        isShowBox = !isShowBox;
        initSba();
        notifyDataSetChanged();
    }

    //返回集合给Activity
    public SparseBooleanArray getSba() {
        return sba;
    }
}