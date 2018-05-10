package cf.vozhuo.app.broswser.search_history;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.adapter.SearchHistoryAdapter;
import cf.vozhuo.app.broswser.databinding.ActivitySearchBinding;
import cf.vozhuo.app.broswser.search_history.presenter.SearchPresenter;
import cf.vozhuo.app.broswser.search_history.presenter.SearchPresenterImpl;
import cf.vozhuo.app.broswser.search_history.storage.SearchBean;
import cf.vozhuo.app.broswser.search_history.view.MySearchView;
import cf.vozhuo.app.broswser.util.SPUtil;


public class SearchActivity extends AppCompatActivity implements MySearchView {

    private static final int RESULT_CODE = 1;
    private static final String TAG = "SearchActivity";
    private static SearchPresenter mSearchPresenter;
    private SearchHistoryAdapter mAdapter;
    private ArrayList<SearchBean> histories = new ArrayList<>();

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private TextView clear_history;

    private EditText search_text;
    public static SearchActivity instance;
    private ActivitySearchBinding binding;
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_history:
                mSearchPresenter.clear();
                break;
            case R.id.tv_http:
                Log.e(TAG, "onClick: " + binding.tvHttp.getText());
                search_text.append(binding.tvHttp.getText());
                break;
            case R.id.tv_https:
                search_text.append(binding.tvHttps.getText());
                break;
            case R.id.tv_com:
                search_text.append(binding.tvCom.getText());
                break;
            case R.id.tv_cn:
                search_text.append(binding.tvCn.getText());
                break;
        }
    }
    private String getContent() {
        return getIntent().getStringExtra("siteInfo");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPUtil.setNightMode(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setHandlers(this); //设置点击监听

        searchView = binding.searchView;
        mRecyclerView = binding.showSearchList;
        clear_history = binding.clearHistory;


        instance = this;
        searchView.setIconified(false);
//        searchView.setSubmitButtonEnabled(true);
        search_text = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        search_text.setTextSize(14);
        if(getContent() != null) {
            searchView.setQuery(getContent(), false);
            search_text.selectAll();
        }

        mSearchPresenter = new SearchPresenterImpl(this, this);
        mAdapter = new SearchHistoryAdapter(R.layout.item_search_history, histories);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SearchBean item = mAdapter.getItem(position);
                TextView tv = view.findViewById(R.id.tv_search_history);
                if(item != null) {
                    tv.setText(item.getContent());
                    search(item.getContent());
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        searchView.setOnQueryTextListener(textWatcher);
        mSearchPresenter.sortHistory();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void showContains(ArrayList<SearchBean> results) {
        clear_history.setVisibility(0 != results.size() ? View.VISIBLE : View.GONE);
        mAdapter.setNewData(results);
    }

    @Override
    public void searchSuccess(String value) {
        SharedPreferences sp = getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        String search_engine = sp.getString("search_engine", "百度");
        String search_string;
        switch (search_engine) {
            case "百度":
                search_string = "https://www.baidu.com/s?ie=UTF-8&wd=";
                break;
            case "谷歌":
                search_string = "https://www.google.com/search?q=";
                break;
            case "必应":
                search_string = "https://bing.com/search?q=";
                break;
            case "搜狗":
                search_string = "https://www.sogou.com/web?query=";
                break;
            default:
                search_string = "https://www.baidu.com/s?ie=UTF-8&wd=";
                break;
        }
        //https://blog.csdn.net/myth13141314/article/details/68940911
        if(URLUtil.isValidUrl(value) || Patterns.WEB_URL.matcher(value).matches()) {
            if(!(URLUtil.isHttpsUrl(value) || URLUtil.isHttpsUrl(value))) {
                value = "http://" + value;
            }
        } else {
            if(value.equals("test")) {
                value = "file:///android_asset/test.html";
            } else
                value = search_string + value;
        }
        MainActivity.instance.load(value);
        finish();
    }

    public void search(String value) {
        if (!TextUtils.isEmpty(value)) {
            mSearchPresenter.search(value);
        }
    }

    private SearchView.OnQueryTextListener textWatcher = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            search(searchView.getQuery().toString().trim());
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.isEmpty()) {
                binding.searchHistory.setVisibility(View.VISIBLE);
                mSearchPresenter.sortHistory();
            } else {
                mSearchPresenter.fuzzySearch(newText.trim());
            }
            return true;
        }
    };

    public static void ClearSearch() {
        if(mSearchPresenter != null) {
            mSearchPresenter.clear();
        }
    }
}