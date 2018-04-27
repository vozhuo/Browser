package cf.vozhuo.app.broswser.search_history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.search_history.presenter.SearchPresenter;
import cf.vozhuo.app.broswser.search_history.presenter.SearchPresenterImpl;
import cf.vozhuo.app.broswser.search_history.storage.SearchBean;
import cf.vozhuo.app.broswser.search_history.view.MySearchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SearchActivity extends AppCompatActivity implements MySearchView {

    private static final int RESULT_CODE = 1;
    private static SearchPresenter mSearchPresenter;
    private SearchHistoryAdapter searchHistoryAdapter;
    private ArrayList<SearchBean> histories = new ArrayList<>();

    @BindView(R.id.searchView)
    EditText_Clear searchView;
    @BindView(R.id.listView_history)
    ListView listViewHistory;
    @BindView(R.id.clear_history)
    TextView clear_history;
    @BindView(R.id.search_history)
    ConstraintLayout search_history;
    @BindView(R.id.btn_search_check)
    TextView search_check;
    public static SearchActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        ButterKnife.bind(this);
        instance = this;
//        showSoftInputFromWindow(SearchActivity.this, searchView);
        //initStatusBar();
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
        searchHistoryAdapter = new SearchHistoryAdapter(this, histories);
        searchHistoryAdapter.setOnSearchHistoryListener(new OnSearchHistoryListener() {
            @Override
            public void onDelete(String key) {
                mSearchPresenter.remove(key);
            }

            @Override
            public void onSelect(String content) {
              //  searchView.setText(content);
//                searchView.setSelection(content.length());
                search(content);
            }
        });
        listViewHistory.setAdapter(searchHistoryAdapter);
        searchView.addTextChangedListener(textWatcher);
        mSearchPresenter.sortHistory();
    }

    @Override
    public void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    /**
     * 初始化手机状态栏，保持状态栏和手机app的颜色主题一致
     */
    private void initStatusBar() {
        //将手机状态栏透明化，
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {

        }
    }

    @Override
    public void showHistories(ArrayList<SearchBean> results) {
        clear_history.setVisibility(0 != results.size() ? View.VISIBLE : View.GONE);
        searchHistoryAdapter.refreshData(results);
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
//        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
//        intent.putExtra("query", value);
//
//        setResult(RESULT_CODE, intent); //回传数据
        finish();
    }

    @Override
    public void showContains(ArrayList<SearchBean> results) {
        clear_history.setVisibility(0 != results.size() ? View.VISIBLE : View.GONE);
        searchHistoryAdapter.refreshData(results);
    }

    @OnClick({R.id.btn_search_check, R.id.clear_history})
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

    public void search(String value) {
        if (!TextUtils.isEmpty(value)) {
//            // 先隐藏键盘
//            ((InputMethodManager) searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
//                    .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(),
//                            InputMethodManager.HIDE_NOT_ALWAYS);
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