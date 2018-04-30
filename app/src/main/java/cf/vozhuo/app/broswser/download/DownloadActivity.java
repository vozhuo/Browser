package cf.vozhuo.app.broswser.download;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.favorites.FavHisEntity;
import cf.vozhuo.app.broswser.favorites.SQLiteHelper;

import static android.content.ContentValues.TAG;

public class DownloadActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_download)
    Toolbar toolbar;
    @BindView(R.id.showDownloadList)
    RecyclerView mRecyclerView;

//    AppCompatSeekBar seekBar;

    private DownloadDao downloadDao;
    private SQLiteHelper openHelper;
    private List<DownloadEntity> list = new ArrayList<>();

    DownloadAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ButterKnife.bind(this);

        Aria.download(this).register();

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
        mAdapter = new DownloadAdapter(this);
        mAdapter.setData(list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    //在这里处理任务执行中的状态，如进度进度条的刷新
    @Download.onTaskRunning protected void running(DownloadTask task) {

        int p = task.getPercent();	//任务进度百分比
        String speed = task.getConvertSpeed();	//转换单位后的下载速度，单位转换需要在配置文件中打开
        long speed1 = task.getSpeed(); //原始byte长度速度

//        mAdapter.setProgress(task.getEntity());
        DownloadAdapter.DownloadHolder holder = (DownloadAdapter.DownloadHolder) mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(0));
        holder.seekBar.setProgress(task.getPercent());
        Log.e(TAG, "running: "+ p + "" + speed);
    }

    @Download.onTaskComplete void taskComplete(DownloadTask task) {
        Toast.makeText(MainActivity.instance, "下载完成", Toast.LENGTH_SHORT).show();
    }
}
