package cf.vozhuo.app.broswser.favorites;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.tab.RecyclerAdapter;

public class HistoriesAdapter extends RecyclerAdapter<FavHisEntity> {

    private List<FavHisEntity> mHistories;
    private HistoriesController mController;

    public HistoriesAdapter(Context context, HistoriesController controller) {
        super(context);
        mController = controller;
        mHistories = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void bindView(FavHisEntity histories, int position, RecyclerView.ViewHolder holder) {
        HistoriesAdapter.HistoryHolder viewHolder = (HistoriesAdapter.HistoryHolder)holder;
        viewHolder.bind(histories, position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoriesAdapter.HistoryHolder(mInflater.inflate(R.layout.history_list_item,
                parent, false));
    }

    class HistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ib_his_close)
        ImageButton ib;
        @BindView(R.id.tv_his_title)
        TextView tv;

        FavHisEntity histories;
        int position;
        HistoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.ib_his_close:
                    mController.delete(histories);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    break;
                case R.id.tv_his_title:
                    MainActivity.instance.load(histories.getUrl()); //调用MainActivity的方法
                    ((FavHisActivity)mContext).finish(); //结束FavHisActivity
                    ((FavHisActivity)mContext).overridePendingTransition(0, 0);
                    break;
            }
        }

        private void bind(FavHisEntity histories, int position) {
            tv.setText(histories.getTitle());
            tv.setOnClickListener(this);
            ib.setOnClickListener(this);
            this.histories = histories;
            this.position = position;
        }
    }

}