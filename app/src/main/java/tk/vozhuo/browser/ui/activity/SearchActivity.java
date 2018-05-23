package tk.vozhuo.browser.ui.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

import tk.vozhuo.browser.R;
import tk.vozhuo.browser.adapter.SearchHistoryAdapter;
import tk.vozhuo.browser.databinding.ActivitySearchBinding;
import tk.vozhuo.browser.interfaces.presenter.SearchPresenter;
import tk.vozhuo.browser.interfaces.presenter.SearchPresenterImpl;
import tk.vozhuo.browser.entity.SearchBean;
import tk.vozhuo.browser.interfaces.MySearchView;
import tk.vozhuo.browser.utils.SPUtil;


public class SearchActivity extends AppCompatActivity implements MySearchView {

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
        SPUtil.setDayNightMode(this);
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
        MainActivity.instance.load(SPUtil.getSearchUrl(this, value));
        finish();
    }

    private void search(String value) {
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