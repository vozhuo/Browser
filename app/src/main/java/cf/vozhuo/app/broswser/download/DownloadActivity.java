package cf.vozhuo.app.broswser.download;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cf.vozhuo.app.broswser.ConfirmDialogFragment;
import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.favorites.SQLiteHelper;

import static android.content.ContentValues.TAG;

public class DownloadActivity extends AppCompatActivity implements DownloadController{

    @BindView(R.id.toolbar_download)
    Toolbar toolbar;
    @BindView(R.id.showDownloadList)
    RecyclerView mRecyclerView;
    @BindView(R.id.ib_download_clear)
    ImageButton ib_download_clear;

    Map<Integer, Boolean> map;
    @OnClick(R.id.ib_download_clear) void onClick() {
        int count = 0;
        map = mAdapter.getMap();
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)) {
                Log.e("TAG", "你选了第：" + i + "项");
//                Log.e(TAG, "onClick: " + mList.get(i).getKey());
                count++;
            }
        }
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("download_clear", "download_clear");
        bundle.putInt("count", count);

        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getSupportFragmentManager(), "fragment_confirm_dialog");

    }
    private DownloadDao downloadDao;
    private SQLiteHelper openHelper;
    private List<MyDownloadEntity> list = new ArrayList<>();
    private List<DownloadEntity> mList = new ArrayList<>();
    DownloadAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ButterKnife.bind(this);

        Aria.download(this).register();
//        Aria.get(this).getDownloadConfig().setConvertSpeed(true);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        openHelper = new SQLiteHelper(this);
        openHelper.getReadableDatabase();
        downloadDao = new DownloadDao(this);

        list = downloadDao.queryAll();
        mList = Aria.download(this).getTaskList();

        if(!(mList == null || mList.isEmpty())) {
            ib_download_clear.setVisibility(View.VISIBLE);
        }

        mAdapter = new DownloadAdapter(this, this);
        mAdapter.setData(mList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.setRecyclerViewOnItemClickListener(new DownloadAdapter.RecyclerViewOnItemClickListener() {
//            @Override
//            public void onItemClickListener(View view, int position) {
//                //点击事件
//                //设置选中的项
//                mAdapter.setSelectItem(position);
//                //打开文件
//                DownloadAdapter.DownloadHolder holder = (DownloadAdapter.DownloadHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(position));
//                Intent intent = new Intent();
//                File file = new File(holder.data.getDownloadPath());
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
//                intent.setAction(Intent.ACTION_VIEW);//动作，查看
//                intent.setDataAndType(Uri.fromFile(file), DownloadUtil.getMIMEType(file));//设置类型
//                startActivity(intent);
//            }
//
//            @Override
//            public boolean onItemLongClickListener(View view, int position) {
//                //长按事件
//                mAdapter.setShowBox();
//                //设置选中的项
//                mAdapter.setSelectItem(position);
//                mAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });
    }
    private int p;

    public void setP(int p) {
        this.p = p;
    }

    public int getP() {
        return p;
    }

    @Download.onTaskRunning protected void running(DownloadTask task) {
        DownloadAdapter.DownloadHolder holder;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            holder = (DownloadAdapter.DownloadHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
            if(task.getKey().equals(holder.data.getKey())) {
                holder.progressBar.setProgress(task.getPercent());
                holder.iv_download_control.setVisibility(View.VISIBLE);
                holder.iv_download_control.setSelected(false);
                holder.tv_download_speed.setText(task.getConvertSpeed());
            }
        }
    }
    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        DownloadAdapter.DownloadHolder holder;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            holder = (DownloadAdapter.DownloadHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(i));
            if(task.getKey().equals(holder.data.getKey())) {
                holder.iv_download_control.setVisibility(View.INVISIBLE);
                holder.progressBar.setProgress(0);
                holder.tv_download_speed.setText("");
                holder.data.setComplete(true);
            }
        }
        Toast.makeText(MainActivity.instance, "下载完成", Toast.LENGTH_SHORT).show();
    }
//    @Download.onTaskStop void taskStop(DownloadTask task) {
//        com.arialyy.aria.core.download.DownloadEntity entity = task.getEntity();
//        entity.
//    }
    @Override
    public void suspendTask(String url, int position) {
        DownloadAdapter.DownloadHolder holder = (DownloadAdapter.DownloadHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(position));
        Log.e(TAG, "suspendTask: "+ Aria.download(this).load(url).isRunning());

        if(Aria.download(this).load(url).isRunning()) {
            Aria.download(this).load(url).stop();
            Log.e(TAG, "suspendTask: "+p);
//            mAdapter.setProcess(p);
            holder.tv_download_speed.setText("暂停");
        } else {
            Aria.download(this).load(url).resume();
            holder.tv_download_speed.setText("开始");
        }

    }
    boolean isSelectMode = false;
    @Override
    public void selectTask(String url, int position) {

        DownloadAdapter.DownloadHolder holder = (DownloadAdapter.DownloadHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(position));
        //点击事件
        //设置选中的项
        mAdapter.setSelectItem(position);
        //打开文件
        if(!isSelectMode) {
            Intent intent = new Intent();
            File file = new File(holder.data.getDownloadPath());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
            intent.setAction(Intent.ACTION_VIEW);//动作，查看
            intent.setDataAndType(Uri.fromFile(file), DownloadUtil.getMIMEType(file));//设置类型
            startActivity(intent);
        }
    }

    @Override
    public void showBox(String url, int position) {
        isSelectMode = true;
        //长按事件
        mAdapter.setShowBox();
        //设置选中的项
        mAdapter.setSelectItem(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getProcess() {
        return p;
    }

    @Override
    public boolean isRunningTask(String url) {
        return Aria.download(this).load(url).isRunning();
    }

    public void deleteDownload(boolean isDeleteFile) {
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i)) {
                String url = mList.get(i).getKey();
                Log.e(TAG, "deleteDownload: "+url);
                Aria.download(this).load(url).cancel(isDeleteFile);
                mAdapter.notifyItemRemoved(i);
                mAdapter.removeData(i, false);
                mAdapter.notifyItemRangeChanged(i, mAdapter.getItemCount());
            }
        }
        if(map.size() == 0) { //未选择，视为全选
            Aria.download(this).removeAllTask(true);
            mAdapter.clearData();
        }
        Log.e(TAG, "deleteDownload: ");
        mAdapter.setShowBox(); //收回多选框
        mAdapter.notifyDataSetChanged();
    }
}
