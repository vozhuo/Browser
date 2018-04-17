package cf.vozhuo.app.broswser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
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
import android.view.ViewGroup;
import android.view.WindowManager;
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

    private static final int RESULT_CODE = 0;
    private WebSettings settings;
    private EditText editText;

    TabAdapter mTabAdapter;

    TabController mTabController;
    private Tab mActiveTab;
    private WebViewFactory mFactory;
    private boolean mIsAnimating = false;
    private boolean mIsInMain = true;


    @BindView(R.id.tvPagerNum)
    TextView mTabNum;

    @BindView(R.id.contentWrapper)
    ConstraintLayout mContentWrapper;

//    @BindView(R.id.web_holder)
//    WebView web_holder;

//    @BindView(R.id.iv_logo)
//    ImageView logo;

    @BindView(R.id.searchProgress)
    ProgressBar searchProgress;

    @BindView(R.id.siteTitle)
    TextView siteTitle;

//    @BindView(R.id.mainTitle)
//    ConstraintLayout mainTitle;

//    @BindView(R.id.searchBox)
//    ConstraintLayout searchBox;

    @BindView(R.id.BottomBar)
    ConstraintLayout mBottomBar;

    private boolean mTabsManagerUIShown = false;

    @OnClick(R.id.siteTitle)
    public void showSearchBar() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        if(!mIsInMain) {
            Log.e(TAG, "showSearchBar: " + mIsInMain);
            intent.putExtra("siteInfo", mActiveTab.getUrl());
        }
        startActivityForResult(intent, RESULT_CODE);
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
//        webView.saveState(outState);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String query = getIntent().getStringExtra("query");
        Log.e(TAG, "onResume: " + query);
        if(query != null) {
            load(query);
            getIntent().removeExtra("query"); //解决返回时再次搜索的问题
        }
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
           addTab(false);
        }
    }

    private void addTab(boolean second) {
        Log.e(TAG,"addTab = ;-----------");

        mIsInMain = true;
        if(second) {
            WebView old = mActiveTab.getWebView();
            old.onPause();
            old.setVisibility(View.GONE);
        }
        Tab tab = mTabController.createNewTab();
        mActiveTab = tab;

        mTabController.setActiveTab(mActiveTab);
        WebView newView = mActiveTab.getWebView();
        if(newView != null) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) newView.getLayoutParams();
            if(lp == null){
                lp = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
//            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.dimen_48dp);
            mContentWrapper.addView(newView,lp);
        }
        Log.e(TAG, "First Web: "+ mActiveTab.getWebView());
    }

    private void removeTab(int index) {
        mTabController.removeTab(index);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (mActiveTab.getWebView().canGoBack()) {
                mActiveTab.getWebView().goBack();//返回上个页面
            } else {
                mTabController.removeTab(mActiveTab);
                if (mTabController.getTabCount() <= 0) {
                    finish();
                }
            }
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
        Log.e(TAG, "showTabs: " + mTabController.getTabCount() + " Current tab: " + mTabController.getCurrentPosition());


//        Tab tab = mTabAdapter.getItem(mTabController.getCurrentPosition();

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true); //倒序
        layout.setReverseLayout(true);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mTabAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 20, 10,20);//设置item偏移
            }
        });
    }

    private PopupWindow popupWindow;
    private void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_site_list, null);

//        mRecyclerView = contentView.findViewById(R.id.showTabList);

        //创建默认的线性LayoutManager
        mRecyclerView = contentView.findViewById(R.id.showTabList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true); //item高度固定

        popupWindow = new PopupWindow(contentView, ConstraintLayout.LayoutParams.MATCH_PARENT,
                500);
        Log.e(TAG, "mBottomBar: " + mBottomBar.getHeight() + " " + mBottomBar.getMeasuredHeight());
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);
        popupWindow.setContentView(contentView);
        popupWindow.setAnimationStyle(R.style.anim_pop);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.YELLOW));

        int[] location = new int[2];
        mBottomBar.getLocationOnScreen(location);
//        popupWindow.showAsDropDown(mContentWrapper);
        Log.e(TAG, "mBottomBar: " + location[1]);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1]-mBottomBar.getMeasuredHeight());

//        transparent = contentView.findViewById(R.id.view_transparent);
//        transparent.setVisibility(View.VISIBLE);

//        mContentWrapper.setBackgroundColor(Color.BLACK);
//        mContentWrapper.setAlpha(0.1f);

        ImageView iv_addTab = contentView.findViewById(R.id.addTab);
        iv_addTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTab(true);
                popupWindow.dismiss();
//                mContentWrapper.setBackgroundColor(Color.TRANSPARENT);
//                mContentWrapper.setAlpha(1f);
            }
        });

        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();
//                    mContentWrapper.setBackgroundColor(Color.TRANSPARENT);
//                    mContentWrapper.setAlpha(1f);
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
//        int index = mTabController.getTabPosition(tab);
//        WebView oldview = mActiveTab.getWebView();
//        oldview.onPause();
//        oldview.setVisibility(View.GONE);
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);

//        if(!mActiveTab.isBlank()) {
        siteTitle.setText(mActiveTab.getTitle());

        WebView newview = mActiveTab.getWebView();
//        newview.setVisibility(View.VISIBLE);
//        newview.onResume();
//        } else {
//            switchToMain();
//            Log.e(TAG,"switchToMain");
//        }
//        if(newview.getParent() == null) {
//            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) newview.getLayoutParams();
//            if(lp == null){
//                lp = new ConstraintLayout.LayoutParams(
//                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
//                        ViewGroup.LayoutParams.MATCH_PARENT);
//            }
////            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.dimen_48dp);
//            mContentWrapper.addView(newview,lp);
//        }
        popupWindow.dismiss();
        Log.e(TAG, "onSelect :: key =:" + tab.getId());
    }

    @Override
    public void closeTab(Tab tab) {
        Log.e(TAG, "closeTab: "+ tab.getId());
        mTabController.removeTab(tab);
        if(mTabController.getTabCount() <= 0) {
            addTab(true);
        }
        mTabAdapter.updateData(mTabController.getTabs());
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
    }

    private void load(String url) {
        if (mActiveTab != null) {
            mActiveTab.clearWebHistory();
            mActiveTab.loadUrl(url, null,true);
            switchToTab();
        }
    }

    private void switchToTab(){
        WebView view = mActiveTab.getWebView();
        if(view.getParent() != null) {
            mContentWrapper.removeView(view);
        }
//        Log.e(TAG,"switchToTab ----------" + mainTitle.getParent() +",view.getParent()= ;" + view.getParent() +",view =:" + view.getTitle());
        if(view != null && view.getParent() == null) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            if(lp == null){
                lp = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
            }
//            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.dimen_48dp);
            mContentWrapper.addView(view);
        }
//        mIsInMain = false;
    }
    private void switchToMain(){
        WebView view = mActiveTab.getWebView();
        if (view != null) {
            mContentWrapper.removeView(view);
        }
//        if(mainTitle.getParent() == null) {
//            mContentWrapper.addView(mainTitle);
//        }
//        Log.e(TAG,"switchToMain :: getParent() =:" + mainTitle.getParent() + "view ：" + mActiveTab.getWebView());
        mActiveTab.stopLoading();
        //  mActiveTab.loadBlank();
        mIsInMain = true;
    }

    @Override
    public void onPageStarted(Tab tab, WebView webView, Bitmap favicon) {
//        if(mIsInMain) {
//            searchProgress.setVisibility(View.GONE);
//        } else {
            searchProgress.setVisibility(View.VISIBLE);
//        }
        siteTitle.setText(tab.getUrl());
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
        siteTitle.setText(title);
        Log.e(TAG, "onReceivedTitle: "+ tab.getUrl() + " " + tab.getTitle());
    }
}