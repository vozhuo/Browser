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
//        return new TabViewHolder(mInflater.inflate(R.layout.tab_list_item, parent, false));
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.tab.Tab;

public class TabAdapter extends BaseQuickAdapter<Tab, BaseViewHolder> {
    private int lastSelectedPos = 0;

    public int getLastSelectedPos() {
        return lastSelectedPos;
    }

    public void setLastSelectedPos(int lastSelectedPos) {
        this.lastSelectedPos = lastSelectedPos;
    }

    public TabAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Tab item) {
        helper.setText(R.id.tv_tab_title, item.getTitle())
                .setImageBitmap(R.id.iv_tab_icon, item.getFavicon())
                .addOnClickListener(R.id.tabClose)
                .itemView.setSelected(lastSelectedPos == helper.getLayoutPosition());
    }
}