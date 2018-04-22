package cf.vozhuo.app.broswser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.webkit.WebSettings;
import android.webkit.WebView;


import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cf.vozhuo.app.broswser.adapter.TabAdapter;
import cf.vozhuo.app.broswser.favorites.FavHisDao;
import cf.vozhuo.app.broswser.search_history.SearchActivity;
import cf.vozhuo.app.broswser.tab.BrowserWebViewFactory;
import cf.vozhuo.app.broswser.tab.Tab;
import cf.vozhuo.app.broswser.tab.TabController;
import cf.vozhuo.app.broswser.tab.UiController;
import cf.vozhuo.app.broswser.tab.WebViewFactory;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements UiController {

    public static MainActivity instance;
    private static final int REQUEST_CODE = 0;
    private WebSettings settings;
    private EditText editText;

    TabAdapter mTabAdapter;

    TabController mTabController;
    private Tab mActiveTab;
    private WebViewFactory mFactory;
    private boolean mIsInMain = true;
    private RecyclerView mRecyclerView;

    @BindView(R.id.tvPagerNum)
    TextView mTabNum;

    @BindView(R.id.contentWrapper)
    ConstraintLayout mContentWrapper;

    @BindView(R.id.searchProgress)
    ProgressBar searchProgress;

    @BindView(R.id.siteTitle)
    TextView siteTitle;

    @BindView(R.id.mainView)
    ConstraintLayout mainView;

    @BindView(R.id.searchBox)
    ConstraintLayout searchBox;

    @BindView(R.id.BottomBar)
    ConstraintLayout mBottomBar;

    private boolean mTabsManagerUIShown = false;

    @OnClick({R.id.searchBox, R.id.siteTitle})
    public void showSearchBar() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        if(!mIsInMain) {
            Log.e(TAG, "showSearchBar: " + mIsInMain);
            intent.putExtra("siteInfo", mActiveTab.getUrl());
        }
        startActivityForResult(intent, REQUEST_CODE);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == 1) {
            String query = data.getStringExtra("query");
            if(query != null) {
                load(query);
                data.removeExtra("query"); //解决返回时再次搜索的问题
            }
        }
    }

    @OnClick(R.id.ivMenu)
    public void showMainSettings(View view) {
        FragmentManager fm = getSupportFragmentManager();
        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
        bottomDialogFragment.show(fm, "fragment_bottom_dialog");
    }
    //for Fragment use
    public void refreshPage() {
        mActiveTab.reloadPage();
    }
    public String getPageUrl() {
        return mActiveTab.getUrl();
    }
    public String getPageTitle() {
        return mActiveTab.getTitle();
    }
    public void setNoImage(boolean noImage) {
        for (Tab tab : mTabController.getTabs()) { //遍历所有Tab，进行WebView设置
            WebSettings settings = tab.getWebView().getSettings();
            settings.setLoadsImagesAutomatically(false);
        }
    }

    @Override
    public void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
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
        instance = this;
    }

    private void addTab(boolean second) {
        if(second) switchToMain();

        Tab tab = mTabController.createNewTab();
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);

        mTabAdapter.setlastSelectedPos();
    }

    private void removeTab(int index) {
        mTabController.removeTab(index);
    }
    private long mExitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (mActiveTab.getWebView().canGoBack()) {
                mActiveTab.getWebView().goBack();//返回上个页面
            } else {
                if (mTabController.getTabCount() == 1) {
                    if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();// 更新mExitTime
                    } else {
                        System.exit(0);
                    }
                } else mTabController.removeTab(mActiveTab);
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

    private void showTabs() {
        mTabAdapter.updateData(mTabController.getTabs());
        Log.e(TAG, "showTabs: " + mTabController.getTabCount() + " Current tab: " + mTabController.getCurrentPosition());

        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true); //倒序
//        layout.setReverseLayout(true);
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
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);

        //创建默认的线性LayoutManager
        mRecyclerView = contentView.findViewById(R.id.showTabList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true); //item高度固定

        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                500);
        popupWindow.setContentView(contentView);
        popupWindow.setAnimationStyle(R.style.anim_pop);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        ImageView iv_addTab = contentView.findViewById(R.id.addTab);
        iv_addTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTab(true);
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

    @Override
    public void selectTab(Tab tab) {
        removeWebView();
        mActiveTab = tab;
        mTabController.setActiveTab(mActiveTab);
        if(!mActiveTab.isBlank()) {
            updateSearchBar();
            switchToTab();
            Log.e(TAG, "switchToTab:");
        } else {
            siteTitle.setText("");
            switchToMain();
            Log.e(TAG, "switchToMain:");
        }
        Log.e(TAG, "onSelect :: key =:" + tab.getId());
        popupWindow.dismiss();
    }

    private void updateSearchBar() {
        int progress = mActiveTab.getPageLoadProgress();
        if (progress == 100) {
            searchProgress.setVisibility(View.GONE);
        }
        siteTitle.setText(mActiveTab.getTitle());
    }

    @Override
    public void closeTab(Tab tab) {
        Log.e(TAG, "closeTab: "+ tab.getId());
        mTabController.removeTab(tab);
        mTabAdapter.updateData(mTabController.getTabs());
        if(mTabController.getTabCount() <= 0) {
            popupWindow.dismiss();
            addTab(true);
        }
    }

    public void load(String url) {
        if (mActiveTab != null) {
            mActiveTab.clearWebHistory();
            mActiveTab.loadUrl(url, null,true);
            switchToTab();
        }
    }

    private void switchToTab() {
        if(mainView.getParent() != null) {
            mContentWrapper.removeView(mainView);
        }
        WebView view = mActiveTab.getWebView();
        Log.e(TAG,"switchToTab ----------" + mainView.getParent() +",view.getParent()= ;" + view.getParent() +",view =:" + view.getTitle());
        if(view.getParent() == null) {
            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            if(lp == null){
                lp = new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
            }
            mContentWrapper.addView(view, lp);
        }
        mIsInMain = false;
    }
    private void switchToMain(){
        if(mainView.getParent() == null){
            mContentWrapper.addView(mainView);
        }
        mainView.bringToFront();
        removeWebView();
        mActiveTab.stopLoading();
        mIsInMain = true;
    }
    //移除当前WebView
    private void removeWebView() {
        WebView view = mActiveTab.getWebView();
        if(view != null) {
            mContentWrapper.removeView(view);
        }
    }
    @Override
    public void onPageStarted(Tab tab, WebView webView, Bitmap favicon) {
        if(mIsInMain) {
            searchProgress.setVisibility(View.GONE);
        } else {
            searchProgress.setVisibility(View.VISIBLE);
        }
        siteTitle.setText(tab.getUrl());
    }
    private static final String TABLE = "histories";
    @Override
    public void onPageFinished(Tab tab) {
        searchProgress.setVisibility(View.INVISIBLE);

        FavHisDao favHisDao = new FavHisDao(this, TABLE);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.CHINA);
        String currentTime = format.format(new Date());
        favHisDao.insert(null, mActiveTab.getTitle(),
                mActiveTab.getUrl(), currentTime);

//        mTabAdapter.notifyDataSetChanged();
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

    @Override
    public void onFavicon(Tab tab, WebView view, Bitmap icon) {

    }
}