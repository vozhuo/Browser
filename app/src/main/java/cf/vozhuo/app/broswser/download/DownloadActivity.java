package cf.vozhuo.app.broswser.download;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cf.vozhuo.app.broswser.ConfirmDialogFragment;
import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.adapter.DownloadAdapter;
import cf.vozhuo.app.broswser.databinding.ActivityDownloadBinding;
import cf.vozhuo.app.broswser.util.DownloadUtil;
import cf.vozhuo.app.broswser.util.SPUtil;

import static android.content.ContentValues.TAG;

public class DownloadActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    public static DownloadActivity instance;
    private List<DownloadEntity> mList = new ArrayList<>();
    private DownloadAdapter mAdapter;
    private boolean isSelectMode = false;
    private SparseBooleanArray sba;

    public void onClick(View view) {
        int count = 0;
        sba = mAdapter.getSba();
        for (int i = 0; i < sba.size(); i++) {
            if (sba.get(i)) {
                Log.e("TAG", "你选了第：" + i + "项");
                count++;
            }
        }
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Confirm", "download_clear");
        bundle.putInt("count", count);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getSupportFragmentManager(), "fragment_confirm_dialog");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        SPUtil.setNightMode(this);
        ActivityDownloadBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_download);
        binding.setHandlers(this);
        mRecyclerView = binding.showDownloadList;

        Aria.download(this).register();

        setSupportActionBar(binding.toolbarDownload);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbarDownload.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mList = Aria.download(this).getTaskList();

        mAdapter = new DownloadAdapter(mList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        if(!(mList == null || mList.isEmpty())) {
            binding.ibDownloadClear.setVisibility(View.VISIBLE);
        } else {
            mAdapter.setEmptyView(R.layout.view_nodata, (ViewGroup) binding.getRoot());
        }
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DownloadEntity item = mAdapter.getItem(position);
                String url = item.getKey();
                TextView tv = view.findViewById(R.id.tv_download_speed);
                if(isSelectMode) { //选择删除模式
                    mAdapter.setSelectItem(position);
                } else {
                    if(!item.isComplete()) { //任务未完成，执行开始、暂停
                        switchState(url);
                    } else { //已完成，执行文件打开
                        Intent intent = new Intent();
                        File file = new File(item.getDownloadPath());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
                        intent.setAction(Intent.ACTION_VIEW);//动作，查看
                        intent.setDataAndType(Uri.fromFile(file), DownloadUtil.getMIMEType(file));//设置类型
                        startActivity(intent);
                    }
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                //进入选择删除模式
                isSelectMode = true;
                mAdapter.setShowBox();
                //设置选中的项
                mAdapter.setSelectItem(position);
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Download.onTaskRunning protected void running(DownloadTask task) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            DownloadEntity item = mAdapter.getItem(i);
            ProgressBar progress = (ProgressBar) mAdapter.getViewByPosition(mRecyclerView, i, R.id.progressBar);
            ImageView control = (ImageView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.iv_download_control);
            TextView speed = (TextView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.tv_download_speed);

            if(task.getKey().equals(item.getKey())) {
                progress.setProgress(task.getPercent());
                control.setVisibility(View.VISIBLE);
                control.setSelected(false);
                speed.setText(task.getConvertSpeed());
            }
        }
    }
    @Download.onTaskStop void taskStop(DownloadTask task) {
        Log.e(TAG, "taskStop: ");
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            DownloadEntity item = mAdapter.getItem(i);
            ProgressBar progress = (ProgressBar) mAdapter.getViewByPosition(mRecyclerView, i, R.id.progressBar);
            ImageView control = (ImageView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.iv_download_control);
            TextView speed = (TextView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.tv_download_speed);

            if(task.getKey().equals(item.getKey())) {
                progress.setProgress(task.getPercent());
                control.setVisibility(View.VISIBLE);
                control.setSelected(true);
                speed.setText("暂停");
            }
        }
    }
    @Download.onTaskResume void taskResume(DownloadTask task) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            DownloadEntity item = mAdapter.getItem(i);
            ProgressBar progress = (ProgressBar) mAdapter.getViewByPosition(mRecyclerView, i, R.id.progressBar);
            ImageButton control = (ImageButton) mAdapter.getViewByPosition(mRecyclerView, i, R.id.iv_download_control);
            TextView speed = (TextView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.tv_download_speed);

            if(task.getKey().equals(item.getKey())) {
                progress.setProgress(task.getPercent());
                control.setVisibility(View.VISIBLE);
                control.setSelected(false);
                speed.setText("开始");
            }
        }
    }
    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            DownloadEntity item = mAdapter.getItem(i);
            ProgressBar progress = (ProgressBar) mAdapter.getViewByPosition(mRecyclerView, i, R.id.progressBar);
            ImageView control = (ImageView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.iv_download_control);
            TextView speed = (TextView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.tv_download_speed);

            if(task.getKey().equals(item.getKey())) {
                progress.setProgress(0);
                control.setVisibility(View.INVISIBLE);
                speed.setText("");
                item.setComplete(true);
            }
        }
        Toast.makeText(MainActivity.instance, "下载完成", Toast.LENGTH_SHORT).show();
    }
    @Download.onTaskFail void taskFail(DownloadTask task) {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            DownloadEntity item = mAdapter.getItem(i);
            ImageView control = (ImageView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.iv_download_control);
            TextView speed = (TextView) mAdapter.getViewByPosition(mRecyclerView, i, R.id.tv_download_speed);

            if(task.getKey().equals(item.getKey())) {
                speed.setText("下载失败");
                control.setSelected(true);
            }
        }
    }
    public void switchState(String url) {
        if(Aria.download(this).load(url).isRunning()) { //正在下载，点击暂停
            Aria.download(this).load(url).stop();
        } else { //暂停状态，点击下载
            Aria.download(this).load(url).resume();
        }
    }
    public boolean isTaskRunning(String url) {
        return Aria.download(this).load(url).isRunning();
    }
    public void deleteDownload(boolean isDeleteFile) {
        for (int i = 0; i < sba.size(); i++) {
            if (sba.get(i)) {
                String url = mList.get(i).getKey();
                Log.e(TAG, "deleteDownload: "+url);
                Aria.download(this).load(url).cancel(isDeleteFile);
                mAdapter.remove(i);
            }
        }
        if(sba.size() == 0) { //未选择，视为全选
            Aria.download(this).removeAllTask(true);
            mAdapter.setNewData(new ArrayList<DownloadEntity>());
        }
       if(isSelectMode) mAdapter.setShowBox(); //收回多选框
    }

    public void removeRecord() {
        Aria.download(this).removeAllTask(false);
    }
}
