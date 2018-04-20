package cf.vozhuo.app.broswser.favorites;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.BottomDialogFragment;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.adapter.TabAdapter;

public class FavoriteActivity extends AppCompatActivity implements FavoritesController, ModifyFragment.ModifyListener {
    private static final String DB_NAME = "favorites.db";
    private static final String TAG = "FavoriteActivity";
    private FavoritesDao favoritesDao;
    private FavoritesSQLiteOpenHelper openHelper;
    private List<FavoritesEntity> list = new ArrayList<>();

    FavoritesAdapter mAdapter;

    @BindView(R.id.showFavList)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_favorite)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_favorite);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        openHelper = new FavoritesSQLiteOpenHelper(this);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        favoritesDao = new FavoritesDao(this);

        list = favoritesDao.queryAllFavorites();

        mAdapter = new FavoritesAdapter(this, this);
        mAdapter.updateData(list);

        LinearLayoutManager layout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 10, 10,10);//设置item偏移
            }
        });
    }

    @Override
    public void modify(FavoritesEntity favorites) {
        ModifyFragment modifyFragment = new ModifyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("favorites", favorites);
        modifyFragment.setArguments(bundle);
        modifyFragment.show(getSupportFragmentManager(), "fragment_modify_dialog");
    }

    @Override
    public void delete(FavoritesEntity favorites) {
        favoritesDao.delete(favorites.getUrl());
        mAdapter.updateData(favoritesDao.queryAllFavorites());
    }

    @Override
    public void sendEntity(FavoritesEntity favorites) {
        favoritesDao.update(favorites.getTitle(), favorites.getUrl());
        mAdapter.updateData(favoritesDao.queryAllFavorites());
    }
}
