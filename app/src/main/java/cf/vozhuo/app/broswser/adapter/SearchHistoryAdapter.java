package cf.vozhuo.app.broswser.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.search_history.storage.SearchBean;

public class SearchHistoryAdapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {

    public SearchHistoryAdapter(int layoutResId, @Nullable List<SearchBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchBean item) {
        helper.setText(R.id.tv_search_history, item.getContent());
    }
}