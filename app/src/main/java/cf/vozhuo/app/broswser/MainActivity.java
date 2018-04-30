package cf.vozhuo.app.broswser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;


import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cf.vozhuo.app.broswser.adapter.TabAdapter;
import cf.vozhuo.app.broswser.download.DownloadActivity;
import cf.vozhuo.app.broswser.download.DownloadDao;
import cf.vozhuo.app.broswser.download.DownloadUtil;
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

    static TabController mTabController;
    private Tab mActiveTab;
    private WebViewFactory mFactory;
    private boolean mIsInMain = true;
    private RecyclerView mRecyclerView;

//    @BindView(R.id.iv_gesture_back)
//    ImageView iv_gesture_back;

    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

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

    @OnClick(R.id.ivMenu)
    public void showMainSettings(View view) {
        FragmentManager fm = getSupportFragmentManager();
        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
        bottomDialogFragment.show(fm, "fragment_bottom_dialog");
    }
    private boolean isReload = false;
    //for Fragment use
    public void refreshPage() {
        if(mActiveTab != null) {
            mActiveTab.reloadPage();
            isReload = true;
        }
    }
    public String getPageUrl() {
        return mActiveTab.getUrl();
    }
    public String getPageTitle() {
        return mActiveTab.getTitle();
    }
    public byte[] getPageFavicon() {
        return mActiveTab.getFaviconBytes();
    }
    public void setNoImage(boolean noImage) { //智能无图设置
        for (Tab tab : mTabController.getTabs()) { //遍历所有Tab，进行WebView设置
            WebSettings settings = tab.getWebView().getSettings();
            if(noImage) {
                settings.setLoadsImagesAutomatically(false);
                Log.e(TAG, "setNoImage: 无图");
            } else {
                settings.setLoadsImagesAutomatically(true);
                Log.e(TAG, "setNoImage: 有图");
            }
        }
    }
    private boolean isTrack = true;
    public void setNoTrack() { //无痕浏览设置
        for (Tab tab : mTabController.getTabs()) {
            WebSettings settings = tab.getWebView().getSettings();
            settings.setDatabaseEnabled(false);
            settings.setAppCacheEnabled(false);
            settings.setDomStorageEnabled(false);
        }
        isTrack = false;
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

        InputStream is = getContext().getResources().openRawResource(R.raw.night);
        byte[] buffer = new byte[0];
        try {
            buffer = new byte[is.available()];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        nightCode = Base64.encodeToString(buffer, Base64.NO_WRAP);


        //配置refreshLayout
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });
        refreshLayout.setProgressViewOffset(true, 0, 100);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorAccent));

        mGesture = new GestureDetector(new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > 150) { //左滑，前进
                    Log.e(TAG, "onFling: 左 " + mActiveTab.canGoForward());
                    if(mActiveTab.canGoForward()) {
                        if(mIsInMain) {
                            switchToTab();
                        }
                        mActiveTab.goForward();
                        updateSearchBar();
                        return true;
                    }
                }
                if (e1.getX() - e2.getX() < -150) { //右滑，后退
                    Log.e(TAG, "onFling: " + mActiveTab.canGoBack());
                    if (mActiveTab.canGoBack()) {

                        Log.e(TAG, mActiveTab.getCurrentUrl() + "---" + mActiveTab.getPreUrl());
                        if (mActiveTab.getPreUrl().equals(Tab.DEFAULT_BLANK_URL)) { //到达最前页
                            if (!mIsInMain) {
                                mActiveTab.pushForwardHistory(mActiveTab.getCurrentUrl());
                                mActiveTab.popBrowsedHistory();
                                switchToMain(); //返回至主页
                            }
                        } else { //正常返回
                            mActiveTab.goBack();
                        }
                        updateSearchBar();
                        return true;
                    }
                }
                return false;
            }
        });

        mActiveTab.getWebView().setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

                Log.e("onDownloadStart", "url===" + url + "---contentDisposition=" + contentDisposition +  "---mimitype" + fileName);
                NoticeDialogFragment noticeDialogFragment = new NoticeDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("download", "download");
                bundle.putString("fileName", URLUtil.guessFileName(url, contentDisposition, mimeType));
                bundle.putString("fileSize", DownloadUtil.getFileSize(contentLength));
                bundle.putString("url", url);
                noticeDialogFragment.setArguments(bundle);
                noticeDialogFragment.show(getSupportFragmentManager(), "fragment_notice_dialog");

                fileUrl = url;
                size = DownloadUtil.getFileSize(contentLength);
            }
        });
    }


    private String size;
    private String fileUrl;
    public void doDownload(String fileName, String url) {
        String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + File.separator + fileName;
        Log.e(TAG, "doDownload: "+ destPath + size + fileUrl);
//        new DownloadTask().execute(url, destPath);
        Aria.download(this)
                .load(url)
                .setFilePath(destPath)
                .start();

        DownloadDao downloadDao = new DownloadDao(this);
        downloadDao.insert(null, url, fileName, size ,destPath);

        Snackbar.make(mContentWrapper, "正在下载",
                Snackbar.LENGTH_LONG).setAction("点击查看", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            }
        }).show();
    }

    private String nightCode;
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

    @OnLongClick(R.id.tvPagerNum)
    boolean longClick(View view) {
        addTab(true);
        return true;
    }
    @OnClick(R.id.tvPagerNum)
    void clickPagerNum(View view) {
        showPopupWindow(view);
        showTabs();
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
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(contentView);
        popupWindow.setAnimationStyle(R.style.anim_pop);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 75);

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
    private int startX;
    private int scrollSize = 100;
    private GestureDetector mGesture;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final WebView webView = mActiveTab.getWebView();

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(webView.getScrollY() <= 0) { //WebView滑动到顶部时开启下拉刷新
                            refreshLayout.setEnabled(true);
                        } else {
                            refreshLayout.setEnabled(false);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }

        });
        mGesture.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
    private long mExitTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mActiveTab != null) {
                if (mActiveTab.canGoBack()) {
                    Log.e(TAG, mActiveTab.getCurrentUrl() + "---" + mActiveTab.getPreUrl());
                    if (mActiveTab.getPreUrl().equals(Tab.DEFAULT_BLANK_URL)) { //到达最前页
                        if (!mIsInMain) {
                            switchToMain(); //返回至主页
                        }
                    } else { //正常返回
                        mActiveTab.goBack();
                    }
                    updateSearchBar();
                    return true;
                } else {

                    if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();// 更新mExitTime
                        return true;
                    } else {
                        Log.e(TAG, "dispatchKeyEvent: ");
                        System.exit(0);
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
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
        Log.e(TAG, "updateSearchBar: "+ mActiveTab.getTitle());
        siteTitle.setText(mActiveTab.getTitle());
    }

    @Override
    public void closeTab(Tab tab) {
        Log.e(TAG, "closeTab: "+ mTabController.getCurrentPosition());
        if(mActiveTab == tab && mTabController.getTabCount()>1) { //移除当前Tab，选择上一个创建的Tab显示
            selectTab(mTabController.getTab(
                    mTabController.getCurrentPosition()-1));
        }
        mTabController.removeTab(tab);
        mTabAdapter.removeData(tab, false);

        if(mTabController.getTabCount() <= 0) {
            popupWindow.dismiss();
            addTab(true);
        }
    }

    public void load(String url) {
        if (mActiveTab != null) {
            mActiveTab.clearWebHistory();
            mActiveTab.loadUrl(url, null,false);
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
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
            mContentWrapper.addView(view, lp);
        }
        siteTitle.setVisibility(View.VISIBLE);
        siteTitle.setText(view.getTitle());
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
        siteTitle.setVisibility(View.INVISIBLE);
    }
    //移除当前WebView
    private void removeWebView() {
        WebView view = mActiveTab.getWebView();
        if(view != null) {
            mContentWrapper.removeView(view);
        }
    }
    boolean loadingFinished = true;
    boolean redirect = false;

    @Override
    public void onPageStarted(Tab tab, WebView webView, Bitmap favicon) {
        if(mIsInMain) {
            searchProgress.setVisibility(View.GONE);
//            siteTitle.setText(tab.getTitle());
        } else {
            searchProgress.setVisibility(View.VISIBLE);
            siteTitle.setText(tab.getUrl());
        }
        darkMode();
        loadingFinished = false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(Tab tab, WebView view, String url) {
        if (!loadingFinished) {
            redirect = true;
        }
        loadingFinished = false;
//        tab.loadUrl(url, null, false); //解决goBack无效的问题
        return true;
    }
    private static final String TABLE = "histories";
    @Override
    public void onPageFinished(Tab tab) {
        searchProgress.setVisibility(View.INVISIBLE);
        if (!redirect) {
            loadingFinished = true;
        }
        if (loadingFinished && !redirect) {

        } else {
            redirect = false;
        }
        refreshLayout.setRefreshing(false);
//        darkMode();
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieStr = cookieManager.getCookie(tab.getUrl()); // 获取到cookie字符串值
    }

    public void darkMode() {
        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        Boolean darkMode = sp.getBoolean("dark_state", false);
        if(darkMode) {
            mActiveTab.getWebView().loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);"
                    + "var style = document.createElement('style');"
                    + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + nightCode + "');"
                    + "parent.appendChild(style)" + "})();");
        }
    }
    @Override
    public void onProgressChanged(Tab tab) {
        searchProgress.setProgress(tab.getPageLoadProgress());
        darkMode();
    }

    @Override
    public void onReceivedTitle(Tab tab, String title) {
        siteTitle.setText(title);
        Log.e(TAG, "onReceivedTitle: " + tab.getUrl() + " " + tab.getTitle() + redirect + isReload);
        if(!redirect && !isReload &&!tab.isGoBack()) {
            Log.e(TAG, "ADD");
            tab.add(tab.getUrl());
            saveAsHistory(tab);
        }
        tab.showHistory();
        isReload = false;
        tab.setGoBack(false);
    }

    private void saveAsHistory(Tab tab) {
        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        Boolean noTrack = sp.getBoolean("track_state", false);
        if (!noTrack && NetworkUtil.isNetworkConnected(this) && !mIsInMain) {
            FavHisDao favHisDao = new FavHisDao(this, TABLE);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.CHINA);
            String currentTime = format.format(new Date());
            String url = tab.getUrl();
            if(favHisDao.queryURL(url)) { //判断是否已记录相同的URL
                favHisDao.updateTime(url, currentTime); //更新访问时间
            } else {
                if(!tab.getTitle().equals("about:blank"))
                    favHisDao.insert(null, tab.getTitle(),
                        url, currentTime, tab.getFaviconBytes());
            }
        }
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

    public static void ClearCache() {
        for (Tab tab : mTabController.getTabs()) { //遍历所有Tab，进行WebView设置
            tab.getWebView().clearCache(true);
        }
    }
}