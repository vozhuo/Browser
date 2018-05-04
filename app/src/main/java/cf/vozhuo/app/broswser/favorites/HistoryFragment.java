package cf.vozhuo.app.broswser.favorites;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;

public class HistoryFragment extends Fragment{
    private static final String TABLE = "histories";
    private static FavHisDao favHisDao;
    private List<FavHisEntity> list = new ArrayList<>();
    HistoriesAdapter mAdapter;

    @BindView(R.id.showHisList)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_clear_history)
    ImageView iv_clear_history;

    @OnClick(R.id.iv_clear_history)
    void onClick() {
        favHisDao.deleteAll();
        mAdapter.setNewData(new ArrayList<FavHisEntity>());
        Toast.makeText(getActivity(), "清理成功", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        favHisDao = new FavHisDao(getContext(), TABLE);

        list = favHisDao.queryAll();
        if(list == null || list.size() == 0) {
            iv_clear_history.setVisibility(View.GONE);
        }
        mAdapter = new HistoriesAdapter(R.layout.history_list_item, list);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 10, 10,10);//设置item偏移
            }
        });

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
