package cf.vozhuo.app.broswser.favorites;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.tab.RecyclerAdapter;
import cf.vozhuo.app.broswser.tab.Tab;

public class FavoritesAdapter extends RecyclerAdapter<FavoritesEntity> {

    private List<FavoritesEntity> mFavorites;

    public FavoritesAdapter(Context context) {
        super(context);
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

        FavoritesEntity favorites;
        int position;
        FavoritesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
        }

        void bind(FavoritesEntity favorites, int position) {
            tv.setText(favorites.getTitle());
            this.favorites = favorites;
            this.position = position;
        }
    }
}
