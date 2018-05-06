package cf.vozhuo.app.broswser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.aria.core.Aria;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cf.vozhuo.app.broswser.adapter.TabAdapter;
import cf.vozhuo.app.broswser.databinding.ActivityMainBinding;
import cf.vozhuo.app.broswser.download.DownloadActivity;
import cf.vozhuo.app.broswser.download.DownloadUtil;
import cf.vozhuo.app.broswser.favorites.FavHisDao;
import cf.vozhuo.app.broswser.favorites.FavHisEntity;
import cf.vozhuo.app.broswser.search_history.SearchActivity;
import cf.vozhuo.app.broswser.settings.SettingActivity;
import cf.vozhuo.app.broswser.tab.BrowserWebViewFactory;
import cf.vozhuo.app.broswser.tab.Tab;
import cf.vozhuo.app.broswser.tab.TabController;
import cf.vozhuo.app.broswser.tab.UiController;
import cf.vozhuo.app.broswser.tab.WebViewFactory;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements UiController{

    public static MainActivity instance;
    private static final int REQUEST_CODE = 0;

    private FavHisDao favHisDao;
    private static final String TABLE_HIS = "histories";
    private static final String TABLE_QA = "quickAccess";
    private boolean isEditMode = false;
    HomeAdapter mHomeAdapter;
    TabAdapter mTabAdapter;

    static TabController mTabController;
    private Tab mActiveTab;
    private WebViewFactory mFactory;
    private boolean mIsInMain = true;
    private RecyclerView mRecyclerView;
    private List<FavHisEntity> mList = new ArrayList<>();

    SwipeRefreshLayout refreshLayout;
    TextView mTabNum;
    ConstraintLayout mContentWrapper;
    ProgressBar searchProgress;
    TextView siteTitle;
    ConstraintLayout mainView;
    View searchBox;
    ConstraintLayout mBottomBar;
    RecyclerView mQARecyclerView;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMenu:
                BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
                bottomDialogFragment.show(getSupportFragmentManager(), "fragment_bottom_dialog");
                break;
            case R.id.tvPagerNum:
                showPopupWindow(view);
                showTabs();
                break;
            case R.id.siteTitle:
            case R.id.searchBox:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                if(!mIsInMain) {
                    intent.putExtra("siteInfo", mActiveTab.getUrl());
                }
                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(0, 0);
                break;
        }
    }
    public boolean onLongClick(View view) {
        addTab(true);
        return true;
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
//                Log.e(TAG, "setNoImage: 无图");
            } else {
                settings.setLoadsImagesAutomatically(true);
//                Log.e(TAG, "setNoImage: 有图");
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
    private NetWorkStateReceiver mNetworkChangeListener;
    @Override
    protected void onStart() {
        super.onStart();
        //注册Receiver，监听网络变化
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(mNetworkChangeListener, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkChangeListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.setHandlers(this);
        binding.bottomBar.setHandlers(this);
        refreshLayout = binding.refreshLayout;
        mTabNum = binding.bottomBar.tvPagerNum;
        mContentWrapper = binding.contentWrapper;
        searchProgress = binding.bottomBar.searchProgress;
        siteTitle = binding.bottomBar.siteTitle;
        mainView = binding.mainView;
        searchBox = binding.searchBox;
        mBottomBar = binding.bottomBar.includeBar;
        mQARecyclerView = binding.showQuickAccessList;

        mTabAdapter = new TabAdapter();
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
                            if(!loadingFinished) {
                                Log.e(TAG, "onFling: 未加载完成");
                                mActiveTab.loadUrl(mActiveTab.getCurrentUrl(), null, false); //未加载完成
                            }
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
                ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Confirm", "download");
                bundle.putString("fileName", URLUtil.guessFileName(url, contentDisposition, mimeType));
                bundle.putString("fileSize", DownloadUtil.getFileSize(contentLength));
                bundle.putString("url", url);
                confirmDialogFragment.setArguments(bundle);
                confirmDialogFragment.show(getSupportFragmentManager(), "fragment_confirm_dialog");

                fileUrl = url;
                size = DownloadUtil.getFileSize(contentLength);
            }
        });

        if(mIsInMain) refreshLayout.setEnabled(false);

        favHisDao = new FavHisDao(this, TABLE_QA);

        mList = favHisDao.queryAll();
        mHomeAdapter = new HomeAdapter(mList);

        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mHomeAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mQARecyclerView);
        // 开启拖拽
        mHomeAdapter.enableDragItem(itemTouchHelper, R.id.ib_qa_icon, true);
        mHomeAdapter.setOnItemDragListener(onItemDragListener);
        // 开启滑动删除
//        mHomeAdapter.enableSwipeItem();
//        mHomeAdapter.setOnItemSwipeListener(onItemSwipeListener);
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);

//        layoutManager.setReverseLayout(true);
//        mQARecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mQARecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mQARecyclerView.setAdapter(mHomeAdapter);
        mHomeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FavHisEntity item = (FavHisEntity)adapter.getItem(position);
                if(item != null) load(item.getUrl());
            }
        });
        mHomeAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                isEditMode = true;
                mHomeAdapter.setShowClose();
                return true;
            }
        });
        mHomeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.ib_qa_close) {
                    FavHisEntity item = (FavHisEntity)adapter.getItem(position);
                    Log.e(TAG, "onItemChildClick: "+ item.getUrl());
                    favHisDao.delete(item.getUrl());
                    mHomeAdapter.remove(position);
                }
            }
        });

        mTabAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e(TAG, "onItemClick: "+ position);
                    selectTab(mTabAdapter.getItem(position));
                if(mTabAdapter.getLastSelectedPos() != position) {
                    mTabAdapter.notifyItemChanged(mTabAdapter.getLastSelectedPos());
                    mTabAdapter.setLastSelectedPos(position);
                    mTabAdapter.notifyItemChanged(mTabAdapter.getLastSelectedPos());
                }
            }
        });
        mTabAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.tabClose) {
                    removeTab(mTabAdapter.getItem(position));
                }
            }
        });
    }
    private OnItemDragListener onItemDragListener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {

        }
    };
    private OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        mList = favHisDao.queryAll();
        mHomeAdapter.setNewData(mList);
    }

    private String size;
    private String fileUrl;
    public void doDownload(String fileName, String url) {
        String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + File.separator + fileName;
        Log.e(TAG, "doDownload: "+ destPath + size + fileUrl);

        Aria.download(this)
                .load(url)
                .setFilePath(destPath)
                .start();
        Aria.get(this).getDownloadConfig().setConvertSpeed(true);

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
        Log.e(TAG, "addTab: " + mTabController.getCurrentPosition());
        mTabAdapter.setLastSelectedPos(mTabController.getCurrentPosition());
    }

    private PopupWindow popupWindow;
    private View contentView;
    private void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_tab_list, null);
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);

        //创建默认的线性LayoutManager
        mRecyclerView = contentView.findViewById(R.id.showTabList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        popupWindow = new PopupWindow(contentView, ConstraintLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(contentView);
        popupWindow.setAnimationStyle(R.style.anim_pop);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

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
    private void showTabs() {
        mTabAdapter.setNewData(mTabController.getTabs());
        Log.e(TAG, "showTabs: " + mTabController.getTabCount() + " Current tab: " + mTabController.getCurrentPosition());

        LinearLayoutManager layout = new LinearLayoutManager(contentView.getContext());
        layout.setStackFromEnd(true); //倒序
//        layout.setReverseLayout(true);
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mTabAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, 0) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(10, 20, 10,20);//设置item偏移
            }
        });
    }
    private boolean isShouldExit(View v, MotionEvent event) {
        if (v != null && (v instanceof ImageButton)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private GestureDetector mGesture;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
            if(ev.getAction() == MotionEvent.ACTION_DOWN && isEditMode) { //退出QuickAccess编辑模式
            Log.e(TAG, "dispatchTouchEvent: "+getCurrentFocus());
                isEditMode = false;
                if(isShouldExit(getCurrentFocus(), ev)) {
                    mHomeAdapter.setShowClose();
                    return true;
                }
        }
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
            if(isEditMode) { //退出QuickAccess编辑模式
                mHomeAdapter.setShowClose();
//                mHomeAdapter.notifyDataSetChanged();
                isEditMode = false;
                return true;
            }

            if (mActiveTab != null) {
                if (mActiveTab.canGoBack()) {
                    Log.e(TAG, mActiveTab.getCurrentUrl() + "---" + mActiveTab.getPreUrl());
                    if (mActiveTab.getPreUrl().equals(Tab.DEFAULT_BLANK_URL)) { //到达最前页
                        if(!loadingFinished) mActiveTab.loadUrl(mActiveTab.getCurrentUrl(), null, false);
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

    @Override
    public void selectTab(Tab tab) {
        if(mActiveTab == tab) {
            popupWindow.dismiss();
            return;
        }
        removeWebView();
        mActiveTab = tab;

        mTabController.setActiveTab(mActiveTab);
        if(!mActiveTab.isBlank()) {
            updateSearchBar();
            switchToTab();
        } else {
            siteTitle.setText("");
            switchToMain();
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
    public void removeTab(Tab tab) {
        Log.e(TAG, "removeTab: "+ mTabController.getCurrentPosition());
        if(mActiveTab == tab) { //移除当前Tab，选择上一个创建的Tab显示
            if(mTabController.getTabCount() > 1) {
                selectTab(mTabController.getTab(
                        mTabController.getCurrentPosition()-1));
            } else {
                addTab(true);
                popupWindow.dismiss();
            }
        }
        mTabAdapter.remove(mTabController.getTabPosition(tab));
        mTabController.removeTab(tab); //执行Destroy前移除WebView，解决内存泄漏的问题
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

//        Log.e(TAG,"switchToTab ----------" + mainView.getParent() +",view.getParent()= ;" + view.getParent() +",view =:" + view.getTitle());
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
        removeWebView();
        if(mainView.getParent() == null){
            mContentWrapper.addView(mainView);
        }
//        mainView.bringToFront();
        mActiveTab.stopLoading();
        mIsInMain = true;
        siteTitle.setVisibility(View.INVISIBLE);
        searchProgress.setVisibility(View.GONE);
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
            Log.e(TAG, "onPageStarted: ");
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
        Log.e(TAG, "shouldOverrideUrlLoading: "+ url);

        if (!(url.startsWith("http") || url.startsWith("https") || url.startsWith("ftp"))) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if(getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) { //应用已安装
                ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Confirm", "open_app");
                bundle.putString("eventUrl", url);
                confirmDialogFragment.setArguments(bundle);
                confirmDialogFragment.show(getSupportFragmentManager(), "fragment_confirm_dialog");
                Log.e(TAG, "fragment_confirm_dialog");
            } else {
                Toast.makeText(getContext(), "应用未安装", Toast.LENGTH_SHORT)
                        .show();
            }
        }
//        tab.loadUrl(url, null, false); //解决goBack无效的问题
        return true;
    }

    @Override
    public void onPageFinished(Tab tab) {
        searchProgress.setVisibility(View.GONE);
        if (!redirect) {
            loadingFinished = true;
        }
        if (loadingFinished && !redirect) {

        } else {
            redirect = false;
        }
        refreshLayout.setRefreshing(false);
        darkMode();
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieStr = cookieManager.getCookie(tab.getUrl()); // 获取到cookie字符串值
        mTabAdapter.notifyDataSetChanged();
    }

    public void darkMode() {
        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        Boolean darkMode = sp.getBoolean("dark_state", false);
        if(darkMode) {
            mActiveTab.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);"
                    + "var style = document.createElement('style');"
                    + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + nightCode + "');"
                    + "parent.appendChild(style)" + "})();", null, false);
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
        mTabAdapter.notifyDataSetChanged();
    }

    private void saveAsHistory(Tab tab) {
        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        Boolean noTrack = sp.getBoolean("track_state", false);
        if (!noTrack && NetworkUtil.isNetworkConnected(this) && !mIsInMain) {
            FavHisDao favHisDao = new FavHisDao(this, TABLE_HIS);
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
        mTabAdapter.notifyDataSetChanged();
    }

    public static void ClearCache() {
        for (Tab tab : mTabController.getTabs()) { //遍历所有Tab，进行WebView设置
            tab.getWebView().clearCache(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(intent.getBooleanExtra("SetDefault", false)) {
            intent = new Intent(MainActivity.this, SettingActivity.class);
            intent.putExtra("NewIntent", "NewIntent");
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
}