package cf.vozhuo.app.broswser.favorites;

import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cf.vozhuo.app.broswser.R;

public class HistoryFragment extends Fragment implements HistoriesController{
    private static final String TABLE = "histories";
    private static FavHisDao favHisDao;
    private SQLiteHelper openHelper;
    private List<FavHisEntity> list = new ArrayList<>();

    @BindView(R.id.showHisList)
    RecyclerView mRecyclerView;
    @BindView(R.id.iv_clear_history)
    ImageView iv_clear_history;

    @OnClick(R.id.iv_clear_history)
    void onClick() {
        deleteHistory();
        Toast.makeText(getActivity(), "清理成功", Toast.LENGTH_SHORT).show();
    }
    HistoriesAdapter mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        openHelper = new SQLiteHelper(getContext());
        SQLiteDatabase db = openHelper.getReadableDatabase();
        favHisDao = new FavHisDao(getContext(), TABLE);

        list = favHisDao.queryAll();
        if(list == null) {
            iv_clear_history.setVisibility(View.GONE);
        }

        mAdapter = new HistoriesAdapter(getContext(), this);
        mAdapter.updateData(list);

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
    }

    @Override
    public void delete(FavHisEntity histories) {
        favHisDao.delete(histories.getUrl());
        mAdapter.removeData(histories, false);
    }

    public static void deleteHistory() {
        favHisDao.deleteAll();
    }
}
