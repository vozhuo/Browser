package tk.vozhuo.browser.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arialyy.aria.core.download.DownloadEntity;
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
    }

    @Override
    protected void convert(final BaseViewHolder helper, DownloadEntity item) {
        helper.setText(R.id.tv_download_name, DownloadUtil.getFileName(item.getDownloadPath()))
                .setText(R.id.tv_download_size, DownloadUtil.getFileSize(item.getFileSize()));

        ImageButton control = helper.getView(R.id.iv_download_control);
        AppCompatCheckBox cb_download = helper.getView(R.id.cb_download);
        TextView speed = helper.getView(R.id.tv_download_speed);

        final int position = helper.getLayoutPosition();
        //文件未下载完成
        if(!item.isComplete()) {
            helper.setGone(R.id.iv_download_control, true);
            if(DownloadActivity.instance.isTaskRunning(item.getKey())) {
                control.setSelected(false);
            } else {
                speed.setText("暂停");
                control.setSelected(true);
            }
        }
        if (isShowBox) {
            cb_download.setVisibility(View.VISIBLE);
        } else {
            cb_download.setVisibility(View.INVISIBLE);
        }

        cb_download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sba.put(position, isChecked);
            }
        });

        cb_download.setChecked(sba.get(position));
    }

        //设置是否显示CheckBox
    public void setShowBox() {
        //取反
        isShowBox = !isShowBox;
    }

    //点击item选中CheckBox
    public void setSelectItem(int position) {
        //对当前状态取反
        if (sba.get(position)) {
            sba.put(position, false);
        } else {
            sba.put(position, true);
        }
        notifyItemChanged(position);
    }

    //返回集合给Activity
    public SparseBooleanArray getSba() {
        return sba;
    }
}