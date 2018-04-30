package cf.vozhuo.app.broswser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SettingActivity extends AppCompatActivity implements SettingController{

    private static final int REQUEST_CODE = 0;
    private static final String TAG = "SettingActivity";

    private List<String> list = new ArrayList<>();

    SettingAdapter mAdapter;

    @BindView(R.id.showMenuList)
    RecyclerView mRecyclerView;

    @BindView(R.id.toolbar_setting)
    Toolbar toolbar;

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

    @Override
    public boolean isDefaultBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
        ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return getPackageName().equals(info.activityInfo.packageName);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Log.e(TAG, "onCreate: " + getPackageName());
        list.add("设置默认浏览器");
        list.add("搜索引擎");
        list.add("设置UA");
        list.add("清理缓存");

        mAdapter = new SettingAdapter(this, this);
        mAdapter.updateData(list);

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

    }

    @Override
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
