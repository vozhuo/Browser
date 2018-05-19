package tk.vozhuo.browser.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import tk.vozhuo.browser.R;
import tk.vozhuo.browser.entity.FavHisEntity;
import tk.vozhuo.browser.utils.BitmapUtil;

public class QuickAccessAdapter extends BaseItemDraggableAdapter<FavHisEntity, BaseViewHolder> {
    private Boolean isShowClose = false;

    public QuickAccessAdapter(@Nullable List<FavHisEntity> data) {
        super(R.layout.item_quick_access, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FavHisEntity item) {
        helper.setText(R.id.tv_qa_title, item.getTitle())
            .setImageBitmap(R.id.ib_qa_icon, BitmapUtil.getImage(item.getFavicon()))
            .addOnClickListener(R.id.ib_qa_close);

        if(isShowClose) helper.setGone(R.id.ib_qa_close, true);
        else helper.setGone(R.id.ib_qa_close, false);
    }

    public void setShowClose() {
        isShowClose = !isShowClose;
        notifyDataSetChanged();
    }
}