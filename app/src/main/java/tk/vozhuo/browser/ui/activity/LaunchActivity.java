package tk.vozhuo.browser.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) { //解决SingleTask模式移除其他Activity的问题
        super.onCreate(savedInstanceState);
        startActivity(new Intent(LaunchActivity.this, MainActivity.class));
        finish();
    }
}
