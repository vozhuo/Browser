package cf.vozhuo.app.broswser.adapter;

import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arialyy.aria.core.download.DownloadEntity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.download.DownloadActivity;
import cf.vozhuo.app.broswser.util.DownloadUtil;

//public class DownloadAdapter extends RecyclerAdapter<DownloadEntity> {
//
//
//    private List<DownloadEntity> mList;
//    private DownloadController mController;
//    private boolean isShowBox = false;
//    private Map<Integer, Boolean> map = new HashMap<>();
////    private RecyclerViewOnItemClickListener onItemClickListener;
//
//    public DownloadAdapter(Context context, DownloadController controller) {
//        super(context);
//
//        mList = new ArrayList<>();
//        mController = controller;
//        initMap();
//    }
//
//    //初始化map集合,默认为不选中
//    private void initMap() {
//        for (int i = 0; i < mList.size(); i++) {
//            map.put(i, false);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return super.getItemCount();
//    }
//
//    @Override
//    public void bindView(DownloadEntity data, int position, RecyclerView.ViewHolder holder) {
//        ((DownloadHolder)holder).bind(data, position);
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.item_download,
//                parent, false);
////        view.setOnClickListener(this);
////        view.setOnLongClickListener(this);
//        return new DownloadHolder(view);
//    }
//
////    @Override
////    public void onClick(View v) {
////        if (onItemClickListener != null) {
////            //注意这里使用getTag方法获取数据
////            if(v == null) Log.e(TAG, "v null");
////           Log.e(TAG, "v.getTag() null" + v.getId() + " ");
//////            onItemClickListener.onItemClickListener(v, (Integer) v.getTag());
////        }
////    }
//
////    @Override
////    public boolean onLongClick(View v) {
////        //不管显示隐藏，清空状态
////        initMap();
////        return onItemClickListener != null && onItemClickListener.onItemLongClickListener(v, (Integer) v.getTag());
////    }
//
////    //设置点击事件
////    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
////        this.onItemClickListener = onItemClickListener;
////    }
//
//    //设置是否显示CheckBox
//    public void setShowBox() {
//        //取反
//        isShowBox = !isShowBox;
//    }
//
//    //点击item选中CheckBox
//    public void setSelectItem(int position) {
//        //对当前状态取反
//        if (map.get(position)) {
//            map.put(position, false);
//        } else {
//            map.put(position, true);
//        }
//        notifyItemChanged(position);
//    }
//
//    //返回集合给Activity
//    public Map<Integer, Boolean> getMap() {
//        return map;
//    }
//
////    //接口回调设置点击事件
////    public interface RecyclerViewOnItemClickListener {
////        //点击事件
////        void onItemClickListener(View view, int position);
////
////        //长按事件
////        boolean onItemLongClickListener(View view, int position);
////    }
//
//    class DownloadHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
//
//        @BindView(R.id.tv_download_size)
//        TextView tv_download_size;
//        @BindView(R.id.tv_download_name)
//        TextView tv_download_name;
//        @BindView(R.id.progressBar)
//        ProgressBar progressBar;
//        @BindView(R.id.iv_download_control)
//        ImageView iv_download_control;
//        @BindView(R.id.tv_download_speed)
//        TextView tv_download_speed;
//        @BindView(R.id.cb_download)
//        CheckBox cb_download;
//
//        private View itemView;
//        DownloadEntity data;
//        int position;
//
//        DownloadHolder(View itemView) {
//            super(itemView);
//            this.itemView = itemView;
//            ButterKnife.bind(this, itemView);
//        }
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.iv_download_control:
//                    iv_download_control.setSelected(!iv_download_control.isSelected());
//                    mController.suspendTask(data.getUrl(), position);
//                    break;
//                case R.id.tv_download_name:
//                case R.id.tv_download_size:
//                    mController.selectTask(data.getUrl(), position);
//                    break;
//            }
//        }
//        @Override
//        public boolean onLongClick(View v) {
//            switch (v.getId()) {
//                case R.id.tv_download_name:
//                case R.id.tv_download_size:
//                    mController.showBox(data.getUrl(), position);
//                    break;
//            }
//            return false;
//        }
//
//        void bind(DownloadEntity data, final int position) {
//            this.data = data;
//            this.position = position;
//
//            iv_download_control.setOnClickListener(this);
//            tv_download_name.setOnClickListener(this);
//            tv_download_name.setOnLongClickListener(this);
//            tv_download_size.setOnClickListener(this);
//            tv_download_size.setOnLongClickListener(this);
//
//            tv_download_name.setText(DownloadUtil.getFileName(data.getDownloadPath()));
//
////            Log.e(TAG, "bind: "+ data.getFileSize() + data.getCurrentProgress());
//            tv_download_size.setText(DownloadUtil.getFileSize(data.getFileSize()));
//            //文件未下载完成
//            if(!data.isComplete()) {
//                iv_download_control.setVisibility(View.VISIBLE);
//                if(mController.isRunningTask(data.getUrl())) {  //正在下载
//                    iv_download_control.setSelected(false);
//                } else { //已暂停
//                    Log.e(TAG, "暂停");
////                    progressBar.setProgress(getProcess());
//                    tv_download_speed.setText("暂停");
//                    iv_download_control.setSelected(true);
//                }
//            }
//
//            if (isShowBox) {
//                cb_download.setVisibility(View.VISIBLE);
//            } else {
//                cb_download.setVisibility(View.INVISIBLE);
//            }
//            cb_download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    map.put(position, isChecked);
//                }
//            });
//            // 设置CheckBox的状态
//            if (map.get(position) == null) {
//                map.put(position, false);
//            }
//            cb_download.setChecked(map.get(position));
//        }
//    }
//}
public class DownloadAdapter extends BaseQuickAdapter<DownloadEntity, BaseViewHolder> {
    private boolean isShowBox = false;
    private SparseBooleanArray sba = new SparseBooleanArray();
    public DownloadAdapter(@Nullable List<DownloadEntity> data) {
        super(R.layout.item_download, data);

        //初始化map集合,默认为不选中
        if(data != null) {
            for (int i = 0; i < data.size(); i++) {
                sba.put(i, false);
            }
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, DownloadEntity item) {
        helper.setText(R.id.tv_download_name, DownloadUtil.getFileName(item.getDownloadPath()))
                .setText(R.id.tv_download_size, DownloadUtil.getFileSize(item.getFileSize()));

        ImageButton control = helper.getView(R.id.iv_download_control);
        CheckBox cb_download = helper.getView(R.id.cb_download);
        TextView speed = helper.getView(R.id.tv_download_speed);

        final int position = helper.getLayoutPosition();
        //文件未下载完成
        if(!item.isComplete()) {
            helper.setGone(R.id.iv_download_control, true);
            if(DownloadActivity.instance.isTaskRunning(item.getKey())) {
                control.setSelected(false);
            } else {
                speed.setText("暂停");
                control.setSelected(true);
            }
        }
        if (isShowBox) {
            cb_download.setVisibility(View.VISIBLE);
        } else {
            cb_download.setVisibility(View.INVISIBLE);
        }

        cb_download.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sba.put(position, isChecked);
            }
        });
//        // 设置CheckBox的状态
//        if (sba.get(position) == null) {
//            sba.put(position, false);
//        }
        cb_download.setChecked(sba.get(position));
    }

        //设置是否显示CheckBox
    public void setShowBox() {
        //取反
        isShowBox = !isShowBox;

    }

    //点击item选中CheckBox
    public void setSelectItem(int position) {
        //对当前状态取反
        if (sba.get(position)) {
            sba.put(position, false);
        } else {
            sba.put(position, true);
        }
        notifyItemChanged(position);
    }

    //返回集合给Activity
    public SparseBooleanArray getSba() {
        return sba;
    }
}