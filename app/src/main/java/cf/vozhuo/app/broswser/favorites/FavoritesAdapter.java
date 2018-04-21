package cf.vozhuo.app.broswser.favorites;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.tab.RecyclerAdapter;
import cf.vozhuo.app.broswser.tab.Tab;

public class FavoritesAdapter extends RecyclerAdapter<FavoritesEntity> {

    private List<FavoritesEntity> mFavorites;
    private FavoritesController mController;

    public FavoritesAdapter(Context context, FavoritesController controller) {
        super(context);
        mController = controller;
        mFavorites = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void bindView(FavoritesEntity favorites, int position, RecyclerView.ViewHolder holder) {
        FavoritesHolder viewHolder = (FavoritesHolder)holder;
        viewHolder.bind(favorites, position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoritesHolder(mInflater.inflate(R.layout.favorite_list_item, parent, false));
    }

    class FavoritesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.fav_web_title)
        TextView tv;
        @BindView(R.id.iv_web_icon)
        ImageView iv;
        @BindView(R.id.iv_edit)
        ImageView iv_edit;

        FavoritesEntity favorites;
        int position;
        FavoritesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.fav_web_title:
                    Toast.makeText(mContext, favorites.getUrl(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_edit:
                    LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View contentView = mInflater.inflate(R.layout.pop_favorite_edit, null);

                    final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setFocusable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setOutsideTouchable(true);

                    v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int mShowMorePopupWindowWidth = -v.getMeasuredWidth();
                    int mShowMorePopupWindowHeight = -v.getMeasuredHeight();
                    popupWindow.showAsDropDown(iv_edit,mShowMorePopupWindowWidth, mShowMorePopupWindowHeight);

                    TextView tv_del = contentView.findViewById(R.id.fav_del);
                    TextView tv_mod = contentView.findViewById(R.id.fav_mod);
                    tv_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mController.delete(favorites);
//                            notifyDataSetChanged();
                            popupWindow.dismiss();
                        }
                    });
                    tv_mod.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mController.modify(favorites);
                            popupWindow.dismiss();
                        }
                    });
                    break;
            }
        }

        void bind(FavoritesEntity favorites, int position) {
            tv.setText(favorites.getTitle());
            tv.setOnClickListener(this);
            iv_edit.setOnClickListener(this);
            this.favorites = favorites;
            this.position = position;
        }
    }
}