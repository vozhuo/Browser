package cf.vozhuo.app.broswser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;


import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cf.vozhuo.app.broswser.adapter.TabAdapter;
import cf.vozhuo.app.broswser.search_history.SearchActivity;
import cf.vozhuo.app.broswser.tab.BrowserWebViewFactory;
import cf.vozhuo.app.broswser.tab.Tab;
import cf.vozhuo.app.broswser.tab.TabController;
import cf.vozhuo.app.broswser.tab.UiController;
import cf.vozhuo.app.broswser.tab.WebViewFactory;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements UiController {

    private WebSettings settings;
    private EditText editText;

    TabAdapter mTabAdapter;

    TabController mTabController;
    private Tab mActiveTab;
    private WebViewFactory mFactory;
    private boolean mIsAnimating = false;
    private boolean mIsInMain = true;
    private RecyclerView.LayoutManager mLayoutManager;
//    @BindView(R.id.view_transparent)
private View transparent;

    @BindView(R.id.tvPagerNum)
    TextView mTabNum;

//    @BindView(R.id.stackView)
//    MYStackView mStackView;

//    @BindView(R.id.flPagersManager)
//    FrameLayout mTabsManagerLayout;

//    @BindView(R.id.searchText)
//    TextView searchText;

    @BindView(R.id.searchProgress)
    ProgressBar searchProgress;

    @BindView(R.id.siteTitle)
    TextView siteTitle;

//    @BindView(R.id.MainTitle)
//    ConstraintLayout mainTitle;

    @BindView(R.id.web_holder)
    WebView webView;

    @BindView(R.id.BottomBar)
    ConstraintLayout mBottomBar;

    private boolean mTabsManagerUIShown = false;

    @OnClick(R.id.siteTitle)
    public void showSearchBar() {
        startActivity(new Intent(MainActivity.this, SearchActivity.class));
        overridePendingTransition(0, 0);
    }

    @OnClick(R.id.ivMenu)
    public void showMainSettings(View view) {
        FragmentManager fm = getSupportFragmentManager();
        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
        bottomDialogFragment.show(fm, "fragment_bottom_dialog");
    }

    @Override
    public void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        webView.saveState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mTabAdapter = new TabAdapter(this, this);
        mTabController = new TabController(this, this);

        mFactory = new BrowserWebViewFactory(this);
        // 先建立一个tab标记主页
        if (mTabController.getTabCount() <= 0) {
            addTab();
        }

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }

//        settings = webView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setSupportMultipleWindows(true); //支持多窗口
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//
//        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                searchProgress.setVisibility(View.VISIBLE);
//                siteTitle.setText(url);
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                searchProgress.setVisibility(View.GONE);
//                siteTitle.setText(view.getTitle());
//                super.onPageFinished(view, url);
//            }
//        });
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
//                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
//            }
//
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                searchProgress.setProgress(newProgress);
//                super.onProgressChanged(view, newProgress);
//            }
//        });
//        String query = getIntent().getStringExtra("query");
//
//        if (query == null) {
////            webview.loadUrl("file:///android_asset/index.html");
//        } else {
//            mainTitle.setVisibility(View.GONE);
//            webView.setVisibility(View.VISIBLE);
//            webView.loadUrl(query);
//        }
    }

    private void addTab() {
        Log.e(TAG,"addTab = ;-----------");
        Tab tab = mTabController.createNewTab();
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);
    }

    private void removeTab(int index) {
        mTabController.removeTab(index);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()){
            webView.goBack();//返回上个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.tvPagerNum)
    void clickPagerNum(View view) {
        showPopupWindow(view);
        showTabs();
    }

    private RecyclerView mRecyclerView;
    private void showTabs() {
        mTabAdapter.updateData(mTabController.getTabs());
        Log.e(TAG, "showTabs: " + mTabController.getTabCount());

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true); //倒序
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mTabAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 15, 10,15);//设置item偏移
            }
        });
    }

    private void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_site_list, null);

        mRecyclerView = contentView.findViewById(R.id.showTabList);

        //创建默认的线性LayoutManager
        RecyclerView mRecyclerView = contentView.findViewById(R.id.showTabList);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true); //item高度固定

        final PopupWindow popupWindow = new PopupWindow(contentView, ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);

        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);

        popupWindow.setContentView(contentView);
        popupWindow.setAnimationStyle(R.style.anim_pop);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int popupWidth = contentView.getMeasuredWidth();
        int popupHeight =  contentView.getMeasuredHeight();
        int[] location = new int[2];

        popupWindow.showAtLocation(mBottomBar, Gravity.CENTER, 0,400);

        ImageView iv_addTab = contentView.findViewById(R.id.addTab);
        iv_addTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "ADD PAGE----------");
                addTab();
                popupWindow.dismiss();
            }
        });

        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
    }
//    @Override
//    public void onWebsiteIconClicked(String url) {
//
//    }
    @Override
    public void selectTab(Tab tab) {

    }

    @Override
    public void closeTab(Tab tab) {
        Log.e(TAG, "closeTab: "+ mTabController.getTabPosition(tab));
        mTabController.removeTab(tab);
    }

    @Override
    public void onTabCountChanged() {
        mTabNum.setText("" + mTabController.getTabCount()); // 更新页面数量
    }

    @Override
    public void onTabDataChanged(Tab tab) {
        mTabAdapter.notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public TabController getTabController() {
        return mTabController;
    }

    @Override
    public WebViewFactory getWebViewFactory() {
        return mFactory;
    }

    @Override
    public void onSetWebView(Tab tab, WebView view) {
       // view.loadUrl("file:///android_asset/index.html");
//        tab.loadUrl("https://baidu.com");
        view = webView;

        String query = getIntent().getStringExtra("query");
        if (query == null) {
            view.loadUrl("file:///android_asset/index.html");
        } else {
            view.loadUrl(query);
        }
    }

    @Override
    public void onPageStarted(Tab tab, WebView webView, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(Tab tab) {
        searchProgress.setVisibility(View.INVISIBLE);
        mTabAdapter.notifyDataSetChanged();
    }

    @Override
    public void onProgressChanged(Tab tab) {
        searchProgress.setProgress(tab.getPageLoadProgress());
    }

    @Override
    public void onReceivedTitle(Tab tab, String title) {

    }

//
//    @Override
//    public void SendMessageValue(Boolean value) {
//        settings = webView.getSettings();
//       // image = value;
//        if(value) {
//            Log.e(TAG, "init: WUTU");
//            settings.setLoadsImagesAutomatically(false); //无图
//        } else {
//            Log.e(TAG, "init: YOUTU");
//            settings.setLoadsImagesAutomatically(true);
//        }
//    }
}