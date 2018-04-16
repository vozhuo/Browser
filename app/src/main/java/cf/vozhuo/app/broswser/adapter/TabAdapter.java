package cf.vozhuo.app.broswser.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.tab.RecyclerAdapter;
import cf.vozhuo.app.broswser.tab.Tab;
import cf.vozhuo.app.broswser.tab.UiController;


public class TabAdapter extends RecyclerAdapter<Tab> {
    private UiController mController;
    private int mCurrent;
    private List<Tab> mTabs;

    public TabAdapter(Context context, UiController controller) {
        super(context);
        mController = controller;
        mTabs = new ArrayList<>();
        mCurrent = -1;
    }
    @Override
    public Tab getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TabViewHolder(mInflater.inflate(R.layout.tab_list_item, parent, false));
    }

    public long getItemId(int position){
        return position;
    }
    public void setCurrent(int index){
        mCurrent = index;
    }
    @Override
    public void bindView(Tab tab, int position, RecyclerView.ViewHolder holder) {
        TabViewHolder pagerViewHolder = (TabViewHolder) holder;
        pagerViewHolder.bind(tab,position);
    }

    class TabViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_tab_title)
        TextView tv_title;
        @BindView(R.id.tabClose)
        ImageView iv_close;
        int position;
        Tab tab;

        public TabViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            tv_title.findViewById(R.id.tv_tab_title);
//            iv_close.findViewById(R.id.tabClose);
        }
        @Override
        public void onClick(View v) {
            if(v == iv_close){
                if(mController != null){
                    mController.closeTab(tab);
                }
            } else if(v == tv_title) {
                if(mController != null){
                    tv_title.setTextColor(Color.BLUE);
                    mController.selectTab(tab);
                }
            }
        }
        public void bind(Tab tab, int position) {
            String title = tab.getTitle();
            tv_title.setText(title);
            tv_title.setOnClickListener(this);
            iv_close.setOnClickListener(this);
            this.tab = tab;
            this.position = position;
        }
    }
}