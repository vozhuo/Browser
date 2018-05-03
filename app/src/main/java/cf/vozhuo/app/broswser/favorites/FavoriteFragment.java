package cf.vozhuo.app.broswser.favorites;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;

public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";
    private static final String TABLE = "favorites";
    private FavHisDao favHisDao;
    private List<FavHisEntity> list = new ArrayList<>();
    private static final String TABLE_QA = "quickAccess";
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
        favHisDao = new FavHisDao(getContext(), TABLE);

        list = favHisDao.queryAll();
        mAdapter = new FavoritesAdapter(R.layout.favorite_list_item, list);

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

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e(TAG, "setOnItemClickListener: ");
                FavHisEntity item = mAdapter.getItem(position);
                MainActivity.instance.load(item.getUrl()); //调用MainActivity的方法
                getActivity().finish(); //结束FavHisActivity
                getActivity().overridePendingTransition(0, 0);
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                final FavHisEntity favorites = (FavHisEntity) adapter.getItem(position);
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View contentView = inflater.inflate(R.layout.pop_favorite_edit, null);
                final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);

                View v = view.findViewById(R.id.fav_web_title);
                v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int mShowMorePopupWindowWidth = -v.getMeasuredWidth();
                int mShowMorePopupWindowHeight = -v.getMeasuredHeight();
                popupWindow.showAsDropDown(v, mShowMorePopupWindowWidth, mShowMorePopupWindowHeight);

                TextView tv_del = contentView.findViewById(R.id.fav_del);
                TextView tv_mod = contentView.findViewById(R.id.fav_mod);
                TextView tv_add = contentView.findViewById(R.id.fav_add);
                tv_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        favHisDao.delete(favorites.getUrl());
                        mAdapter.remove(position);
                        popupWindow.dismiss();
                    }
                });
                tv_mod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modify(favorites, position);
                        popupWindow.dismiss();
                    }
                });
                tv_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add(favorites);
                        Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
                return true;
            }
        });
    }

    public void delete(FavHisEntity favorites, int position) {
        favHisDao.delete(favorites.getUrl());
        mAdapter.remove(position);
    }

    public void add(FavHisEntity favorites) {
        favHisDao = new FavHisDao(getContext(), TABLE_QA);
        favHisDao.insert(null, favorites.getTitle(), favorites.getUrl(), null, favorites.getFavicon());
    }

    public void modify(FavHisEntity favorites, int position) {
        ModifyFragment modifyFragment = new ModifyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("favorites", favorites);
        bundle.putInt("position", position);
        modifyFragment.setArguments(bundle);
        modifyFragment.show(getChildFragmentManager(), "fragment_modify_dialog"); //创建子Fragment
    }

    public void updateFavorite(FavHisEntity favorites, int position) {
        favHisDao.update(favorites.getTitle(), favorites.getUrl());
        mAdapter.refreshNotifyItemChanged(position);
    }
}
