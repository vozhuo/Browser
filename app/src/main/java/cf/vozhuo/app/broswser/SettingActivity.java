package cf.vozhuo.app.broswser;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import cf.vozhuo.app.broswser.databinding.ActivitySettingBinding;
import cf.vozhuo.app.broswser.tab.SettingEntity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SettingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private static final String TAG = "SettingActivity";

    private List<SettingEntity> list = new ArrayList<>();
    private SettingAdapter mAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        finishActivity(REQUEST_CODE);
//        if(hasDefaultBrowser()) {
//
//        } else {
        if(requestCode == REQUEST_CODE) startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("http://")), REQUEST_CODE);
//        }
    }

    public boolean isDefaultBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
        ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return getPackageName().equals(info.activityInfo.packageName);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        Toolbar toolbar = binding.toolbarSetting;
        RecyclerView mRecyclerView = binding.showMenuList;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list.add(new SettingEntity("设置默认浏览器", 1));
        list.add(new SettingEntity("搜索引擎", 2));
        list.add(new SettingEntity("设置UA", 2));
        list.add(new SettingEntity("清理缓存", 2));

        mAdapter = new SettingAdapter(list);

        LinearLayoutManager layout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, 0) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(20, 20, 10, 20);
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SettingEntity item = mAdapter.getItem(position);
                if(item != null) {
                    switch (item.getItemType()) {
                        case 1:
                            SwitchCompat switchCompat = view.findViewById(R.id.switch_default);
                            Log.e(TAG, "onItemClick: " + switchCompat.isChecked());

                            clearDefaultAndSet(switchCompat.isChecked());
                            break;
                        case 2:
                            showFragment(position);
                            break;
                    }
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.switch_default) {
                    SwitchCompat switchCompat = view.findViewById(R.id.switch_default);
                    Log.e(TAG, "onItemChildClick: " + switchCompat.isChecked());
                    clearDefaultAndSet(switchCompat.isChecked());
                }
            }
        });
    }

    public void clearDefaultAndSet(boolean isChecked) {
        if (isChecked) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            if("android".equals(info.activityInfo.packageName)) { //未设置默认浏览器
                startActivity(intent);
            } else {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + info.activityInfo.packageName)), REQUEST_CODE);
            }
        }
    }

    public void showFragment(int position) {
        FragmentManager fm = getSupportFragmentManager();
        NoticeDialogFragment noticeDialogFragment = new NoticeDialogFragment();
        Bundle bundle = new Bundle();
        if(position == 1) {
            bundle.putString("search_engine", "search_engine");
        } else if(position == 2) {
            bundle.putString("ua", "ua");
        } else if(position == 3) {
            bundle.putString("clear", "clear");
        }
        noticeDialogFragment.setArguments(bundle);
        noticeDialogFragment.show(fm, "fragment_notice_dialog");
    }
}
