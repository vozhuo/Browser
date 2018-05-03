package cf.vozhuo.app.broswser;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cf.vozhuo.app.broswser.favorites.FavHisEntity;

public class HomeAdapter extends BaseQuickAdapter<FavHisEntity, BaseViewHolder>{

    private Boolean isShowClose = false;

    public HomeAdapter(int layoutResId, @Nullable List<FavHisEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FavHisEntity item) {
        helper.setText(R.id.tv_qa_title, item.getTitle())
            .setImageBitmap(R.id.ib_qa_icon,BitmapUtil.getImage(item.getFavicon()))
            .addOnClickListener(R.id.ib_qa_close);

        if(isShowClose) helper.setGone(R.id.ib_qa_close, true);
        else helper.setGone(R.id.ib_qa_close, false);
    }

    public void setShowClose() {
        isShowClose = !isShowClose;
        notifyDataSetChanged();
    }
}