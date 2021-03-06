package tk.vozhuo.browser.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tk.vozhuo.browser.R;
import tk.vozhuo.browser.databinding.ActivityFavHisBinding;
import tk.vozhuo.browser.ui.fragment.FavoriteFragment;
import tk.vozhuo.browser.ui.fragment.HistoryFragment;
import tk.vozhuo.browser.utils.SPUtil;

public class FavHisActivity extends AppCompatActivity {
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPUtil.setDayNightMode(this);
        ActivityFavHisBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_fav_his);

        setSupportActionBar(binding.tbFavHis);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.tbFavHis.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fragments.add(new FavoriteFragment());
        fragments.add(new HistoryFragment());

        final String []content = new String[]{"书签", "历史"};
        binding.viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return content[position];
            }
        });
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }
}