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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;

public class FavoriteFragment extends Fragment implements FavoritesController {
    private static final String TAG = "FavoriteFragment";
    private static final String TABLE = "favorites";
    private FavHisDao favHisDao;
    private SQLiteHelper openHelper;
    private List<FavHisEntity> list = new ArrayList<>();

    FavoritesAdapter mAdapter;

    @BindView(R.id.showFavList)
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        openHelper = new SQLiteHelper(getContext());
        SQLiteDatabase db = openHelper.getReadableDatabase();
        favHisDao = new FavHisDao(getContext(), TABLE);

        list = favHisDao.queryAll();
        mAdapter = new FavoritesAdapter(getContext(), this);
        mAdapter.updateData(list);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 15, 10,10);//设置item偏移
            }
        });
    }

    @Override
    public void modify(FavHisEntity favorites) {
        ModifyFragment modifyFragment = new ModifyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("favorites", favorites);
        modifyFragment.setArguments(bundle);
        modifyFragment.show(getChildFragmentManager(), "fragment_modify_dialog"); //创建子Fragment
    }

    @Override
    public void delete(FavHisEntity favorites) {
        favHisDao.delete(favorites.getUrl());
        mAdapter.removeData(favorites, false);
    }
    private static final String TABLE_QA = "quickAccess";
    @Override
    public void add(FavHisEntity favorites) {
        favHisDao = new FavHisDao(getContext(), TABLE_QA);
        favHisDao.insert(null, favorites.getTitle(),
                favorites.getUrl(), favorites.getTime(), favorites.getFavicon());
    }

    public void updateFavorite(FavHisEntity favorites) {
        favHisDao.update(favorites.getTitle(), favorites.getUrl());
        mAdapter.updateData(favHisDao.queryAll());
    }
}
