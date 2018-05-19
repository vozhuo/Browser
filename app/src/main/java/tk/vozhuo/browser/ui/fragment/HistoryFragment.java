package tk.vozhuo.browser.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import tk.vozhuo.browser.db.SQLiteHelper;
import tk.vozhuo.browser.ui.activity.MainActivity;
import tk.vozhuo.browser.R;
import tk.vozhuo.browser.adapter.HistoriesAdapter;
import tk.vozhuo.browser.databinding.FragmentHistoryBinding;
import tk.vozhuo.browser.entity.FavHisEntity;
import tk.vozhuo.browser.db.FavHisDao;

public class HistoryFragment extends Fragment{
    private static FavHisDao favHisDao;
    private HistoriesAdapter mAdapter;
    private ImageView iv_clear_history;
    private FragmentHistoryBinding binding;

    public void onClick(View view) {
        favHisDao.deleteAll();
        mAdapter.setNewData(null);
        Toast.makeText(getContext(), "清理成功", Toast.LENGTH_SHORT).show();
        mAdapter.setEmptyView(R.layout.view_nodata, (ViewGroup) binding.getRoot());
        iv_clear_history.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_history, container, false);
        binding.setHandlers(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = binding.showHisList;
        iv_clear_history = binding.ivClearHistory;

        favHisDao = new FavHisDao(getContext(), SQLiteHelper.TABLE_HIS);

        List<FavHisEntity> list = favHisDao.queryAll();
        mAdapter = new HistoriesAdapter(R.layout.item_browse_history, list);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mAdapter);

        if(list == null || list.size() == 0) {
            iv_clear_history.setVisibility(View.GONE);
            mAdapter.setEmptyView(R.layout.view_nodata, (ViewGroup) binding.getRoot());
        }

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FavHisEntity item = mAdapter.getItem(position);
                MainActivity.instance.load(item.getUrl()); //调用MainActivity的方法
                getActivity().finish(); //结束Activity
                getActivity().overridePendingTransition(0, 0);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                FavHisEntity item = mAdapter.getItem(position);
                if(view.getId() == R.id.ib_his_close) {
                    favHisDao.delete(item.getUrl());
                    mAdapter.remove(position);
                }
            }
        });
    }

    public static void deleteHistory() {
        favHisDao.deleteAll();
    }
}
