package cf.vozhuo.app.broswser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_setting)
    Toolbar toolbar;

    @BindView(R.id.switchBrowser)
    SwitchCompat switchBrowser;

    @BindView(R.id.tv_browser_state)
    TextView tv_browser_state;

    @OnCheckedChanged(R.id.switchBrowser)
    public void clearDefaultAndSet(boolean isChecked) {
        if (isChecked) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
            ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            if(!"android".equals(info.activityInfo.packageName)) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + info.activityInfo.packageName)), 0);
            } else {
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(hasDefaultBrowser()) {

        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://")));
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

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"));
        ResolveInfo info = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if(getPackageName().equals(info.activityInfo.packageName)) {
            tv_browser_state.setText("已设置默认浏览器");
        } else {
            if(!"android".equals(info.activityInfo.packageName)) {
                tv_browser_state.setText("默认浏览器包名为：" + info.activityInfo.packageName);
            } else{
                tv_browser_state.setText("未设置默认浏览器");
            }

        }




    }
}
