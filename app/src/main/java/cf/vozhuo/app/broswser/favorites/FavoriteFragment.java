package cf.vozhuo.app.broswser.favorites;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.adapter.FavoritesAdapter;
import cf.vozhuo.app.broswser.databinding.FragmentFavoriteBinding;

public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";
    private static final String TABLE = "favorites";
    private FavHisDao favHisDao;
    private static final String TABLE_QA = "quickAccess";
    private FavoritesAdapter mAdapter;
    private FragmentFavoriteBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_favorite, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favHisDao = new FavHisDao(getContext(), TABLE);

        List<FavHisEntity> list = favHisDao.queryAll();
        mAdapter = new FavoritesAdapter(R.layout.item_favorite, list);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        binding.showFavList.setLayoutManager(layout);
        binding.showFavList.setAdapter(mAdapter);

        if(list == null || list.size() == 0) {
            mAdapter.setEmptyView(R.layout.view_nodata, (ViewGroup) binding.getRoot());
        }

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
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
                View contentView = inflater.inflate(R.layout.pop_favorite_option, null);
                final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);

                view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int mShowMorePopupWindowWidth = -view.getMeasuredWidth();
                int mShowMorePopupWindowHeight = -view.getMeasuredHeight();
                popupWindow.showAsDropDown(view, mShowMorePopupWindowWidth, mShowMorePopupWindowHeight);

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
        MainActivity.addToMain(favorites);
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
