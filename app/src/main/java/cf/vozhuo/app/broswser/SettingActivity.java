package cf.vozhuo.app.broswser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class SettingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "SettingActivity";
    @BindView(R.id.iv_engine)
    ImageView iv_engine;

    @BindView(R.id.toolbar_setting)
    Toolbar toolbar;

    @BindView(R.id.switchBrowser)
    SwitchCompat switchBrowser;

    @BindView(R.id.tv_engine)
    TextView tv_engine;

    @BindView(R.id.ib_ua)
    ImageButton ib_ua;

    @BindView(R.id.tv_ua_text)
    TextView tv_ua_text;

    @OnClick(R.id.iv_engine)
    public void changeEngine() {
        FragmentManager fm = getSupportFragmentManager();
        NoticeDialogFragment bottomDialogFragment = new NoticeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("search_engine", "search_engine");
        bottomDialogFragment.setArguments(bundle);
        bottomDialogFragment.show(fm, "fragment_notice_dialog");
    }
    @OnClick(R.id.ib_ua)
    public void changeUA() {
        FragmentManager fm = getSupportFragmentManager();
        NoticeDialogFragment bottomDialogFragment = new NoticeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ua", "ua");
        bottomDialogFragment.setArguments(bundle);
        bottomDialogFragment.show(fm, "fragment_notice_dialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case "search_engine":
                        tv_engine.setText(sharedPreferences.getString(key, "百度"));
                        break;
                    case "ua":
                        tv_ua_text.setText(sharedPreferences.getString(key, "Android"));
                        break;
                        default: break;
                }
            }
        });
        tv_engine.setText(sp.getString("search_engine", "百度"));
        tv_ua_text.setText(sp.getString("ua", "Android"));
    }

    @OnCheckedChanged(R.id.switchBrowser)
    public void clearDefaultAndSet(boolean isChecked) {
        if (isChecked) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            if("android".equals(info.activityInfo.packageName)) { //未设置默认浏览器

                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(0, 0);
            } else {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + info.activityInfo.packageName)), 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        finishActivity(REQUEST_CODE);
        if(hasDefaultBrowser()) {

        } else {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("http://")), REQUEST_CODE);
        }
    }

    public boolean hasDefaultBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
        ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return !"android".equals(info.activityInfo.packageName);
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

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
        ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);


//        if(getPackageName().equals(info.activityInfo.packageName)) {
//            tv_browser_state.setText("已设置默认浏览器");
//        } else {
//            if(!"android".equals(info.activityInfo.packageName)) {
//                tv_browser_state.setText("默认浏览器包名为：" + info.activityInfo.packageName);
//            } else{
//                tv_browser_state.setText("未设置默认浏览器");
//            }
//
//        }

    }
}
