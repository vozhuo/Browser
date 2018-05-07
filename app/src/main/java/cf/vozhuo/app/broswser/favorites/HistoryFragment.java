package cf.vozhuo.app.broswser.favorites;

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

import java.util.ArrayList;
import java.util.List;

import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.adapter.HistoriesAdapter;
import cf.vozhuo.app.broswser.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment{
    private static final String TABLE = "histories";
    private static FavHisDao favHisDao;
    private HistoriesAdapter mAdapter;

    private FragmentHistoryBinding binding;

    public void onClick(View view) {
        favHisDao.deleteAll();
        mAdapter.setNewData(new ArrayList<FavHisEntity>());
        Toast.makeText(getContext(), "清理成功", Toast.LENGTH_SHORT).show();
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
        ImageView iv_clear_history = binding.ivClearHistory;

        favHisDao = new FavHisDao(getContext(), TABLE);

        List<FavHisEntity> list = favHisDao.queryAll();
        if(list == null || list.size() == 0) {
            iv_clear_history.setVisibility(View.GONE);
        }
        mAdapter = new HistoriesAdapter(R.layout.item_browse_history, list);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mAdapter);

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
