package cf.vozhuo.app.broswser.search_history;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.databinding.ActivitySearchBinding;
import cf.vozhuo.app.broswser.search_history.presenter.SearchPresenter;
import cf.vozhuo.app.broswser.search_history.presenter.SearchPresenterImpl;
import cf.vozhuo.app.broswser.search_history.storage.SearchBean;
import cf.vozhuo.app.broswser.search_history.view.MySearchView;


public class SearchActivity extends AppCompatActivity implements MySearchView {

    private static final int RESULT_CODE = 1;
    private static SearchPresenter mSearchPresenter;
    private SearchHistoryAdapter mAdapter;
    private ArrayList<SearchBean> histories = new ArrayList<>();

    private EditText_Clear searchView;
    private RecyclerView mRecyclerView;
    private TextView clear_history;
    private ConstraintLayout search_history;
    private TextView search_check;

    public static SearchActivity instance;

//    @OnClick({R.id.btn_search_check, R.id.clear_history})

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search_check:
                String value = searchView.getText().toString().trim();
                search(value);
                break;
            case R.id.clear_history:
                mSearchPresenter.clear();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySearchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setHandlers(this); //设置点击监听

        searchView = binding.searchView;
        mRecyclerView = binding.showSearchList;
        clear_history = binding.clearHistory;
        search_history = binding.searchHistory;
        search_check = binding.btnSearchCheck;

        instance = this;

        searchView.setText(getIntent().getStringExtra("siteInfo"));
        searchView.selectAll();
        mSearchPresenter = new SearchPresenterImpl(this, this);
        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(searchView.getText().toString());
                    return true;
                }
                return false;
            }
        });

        mAdapter = new SearchHistoryAdapter(R.layout.search_list_item, histories);
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

        searchView.addTextChangedListener(textWatcher);
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
            if(!(value.startsWith("http://") || value.startsWith("https://"))) {
                value = "http://" + value;
            }
        } else {
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence c, int start, int before, int count) {
            if (c.toString().length()==0) {
                search_history.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
            if(TextUtils.isEmpty(s)) {
                search_history.setVisibility(View.VISIBLE);
                mSearchPresenter.sortHistory();
            } else {
                mSearchPresenter.fuzzySearch(s.toString().trim());
            }
        }
    };

    public static void ClearSearch() {
        if(mSearchPresenter != null) mSearchPresenter.clear();
    }
}