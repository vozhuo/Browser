package tk.vozhuo.browser.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import tk.vozhuo.browser.entity.FavHisEntity;
import tk.vozhuo.browser.utils.BitmapUtil;
import tk.vozhuo.browser.R;

public class FavoritesAdapter extends BaseQuickAdapter<FavHisEntity, BaseViewHolder>{

    public FavoritesAdapter(int layoutResId, @Nullable List<FavHisEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FavHisEntity item) {
        helper.setText(R.id.fav_web_title, item.getTitle() != null ? item.getTitle() : item.getUrl())
                .setImageBitmap(R.id.iv_web_icon, BitmapUtil.getImage(item.getFavicon()));
    }
}