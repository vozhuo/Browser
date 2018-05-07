package cf.vozhuo.app.broswser.adapter;

//public class TabAdapter extends RecyclerAdapter<Tab> {
//    private UiController mController;
////    TabController mTabController;
//    private int mCurrent;
//    private List<Tab> mTabs;
//
//    public TabAdapter(Context context, UiController controller) {
//        super(context);
//        mController = controller;
//        mTabs = new ArrayList<>();
//        mCurrent = -1;
//    }
//    @Override
//    public Tab getItem(int position) {
//        return super.getItem(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return super.getItemCount();
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new TabViewHolder(mInflater.inflate(R.layout.item_tab, parent, false));
//    }
//
//    public long getItemId(int position){
//        return position;
//    }
//    public void setCurrent(int index){
//        mCurrent = index;
//    }
//    @Override
//    public void bindView(Tab tab, int position, RecyclerView.ViewHolder holder) {
//        TabViewHolder pagerViewHolder = (TabViewHolder) holder;
//        pagerViewHolder.itemView.setSelected(lastSelectedPos == position);
//        pagerViewHolder.bind(tab,position);
//    }
//
//    public void setlastSelectedPos() {
//        lastSelectedPos = getItemCount();
//    }
//    private int lastSelectedPos = 0;
//
//    class TabViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        @BindView(R.id.tv_tab_title)
//        TextView tv_title;
//        @BindView(R.id.tabClose)
//        ImageView iv_close;
//        @BindView(R.id.iv_tab_icon)
//        ImageView iv_tab_icon;
//        int position;
//        Tab tab;
//
//        public TabViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if(v == iv_close){
//                if(mController != null) {
//                    mController.closeTab(tab);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, getItemCount());
//                    v.setVisibility(View.GONE);
//                }
//            } else if(v == tv_title) {
//                if(mController != null) {
//                    mController.selectTab(tab);
//                    if(position == lastSelectedPos) return;
//                    notifyItemChanged(lastSelectedPos);
//                    Log.e(TAG, "notifyItemChanged: " + lastSelectedPos + " " + position);
//                    lastSelectedPos = position;
//                    notifyItemChanged(lastSelectedPos);
//                }
//            }
//        }
//        public void bind(Tab tab, int position) {
//            String title = tab.getTitle();
//            tv_title.setText(title);
//            iv_tab_icon.setImageBitmap(tab.getFavicon());
//            iv_tab_icon.setOnClickListener(this);
//            tv_title.setOnClickListener(this);
//            iv_close.setOnClickListener(this);
//            this.tab = tab;
//            this.position = position;
//        }
//    }
//}

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.tab.Tab;

public class TabAdapter extends BaseItemDraggableAdapter<Tab, BaseViewHolder> {
    private int lastSelectedPos = RecyclerView.NO_POSITION;

    public TabAdapter(List<Tab> data) {
        super(R.layout.item_tab, data);
    }

    public int getLastSelectedPos() {
        return lastSelectedPos;
    }

    public void setLastSelectedPos(int lastSelectedPos) {
        this.lastSelectedPos = lastSelectedPos;
    }

    @Override
    protected void convert(BaseViewHolder helper, Tab item) {
        helper.setText(R.id.tv_tab_title, item.getTitle())
                .setImageBitmap(R.id.iv_tab_icon, item.getFavicon())
                .addOnClickListener(R.id.tabClose)
                .itemView.setSelected(getLastSelectedPos() == helper.getLayoutPosition());
        Log.e(TAG, "convert: "+ getLastSelectedPos());
    }
}