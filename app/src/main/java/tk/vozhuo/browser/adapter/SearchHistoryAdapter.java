package tk.vozhuo.browser.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import tk.vozhuo.browser.R;
import tk.vozhuo.browser.entity.SearchBean;

public class SearchHistoryAdapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {

    public SearchHistoryAdapter(int layoutResId, @Nullable List<SearchBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchBean item) {
        helper.setText(R.id.tv_search_history, item.getContent());
    }
}