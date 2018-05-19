package tk.vozhuo.browser.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
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

import tk.vozhuo.browser.R;
import tk.vozhuo.browser.db.SQLiteHelper;
import tk.vozhuo.browser.interfaces.WebAppInterface;
import tk.vozhuo.browser.adapter.QuickAccessAdapter;
import tk.vozhuo.browser.adapter.TabAdapter;
import tk.vozhuo.browser.widget.TabPopupWindow;
import tk.vozhuo.browser.broadcast.NetWorkStateReceiver;
import tk.vozhuo.browser.databinding.ActivityMainBinding;
import tk.vozhuo.browser.databinding.PopHitTestBinding;
import tk.vozhuo.browser.databinding.PopTabListBinding;
import tk.vozhuo.browser.db.FavHisDao;
import tk.vozhuo.browser.entity.FavHisEntity;
import tk.vozhuo.browser.interfaces.BrowserWebViewFactory;
import tk.vozhuo.browser.entity.Tab;
import tk.vozhuo.browser.widget.TabRecyclerView;
import tk.vozhuo.browser.interfaces.UiController;
import tk.vozhuo.browser.interfaces.WebViewFactory;
import tk.vozhuo.browser.ui.fragment.ConfirmDialogFragment;
import tk.vozhuo.browser.ui.fragment.MenuDialogFragment;
import tk.vozhuo.browser.utils.DownloadUtil;
import tk.vozhuo.browser.utils.JSUtil;
import tk.vozhuo.browser.utils.NetworkUtil;
import tk.vozhuo.browser.utils.SPUtil;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements UiController{

    public static MainActivity instance;
    private static final int REQUEST_CODE = 0;

    private FavHisDao favHisDao;
    private boolean isEditMode = false;
    static QuickAccessAdapter mQuickAccessAdapter;
    static TabAdapter mTabAdapter;

    private Tab mActiveTab;
    private WebViewFactory mFactory;
    private boolean mIsInMain = true;
    private TabRecyclerView mRecyclerView;
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
    private ItemTouchHelper itemTouchHelper;
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMenu:
                MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
                menuDialogFragment.show(getSupportFragmentManager(), "fragment_bottom_dialog");
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
            case R.id.addTab:
                addTab(true);
                popupWindow.dismiss();
            case R.id.tv_backWindow:
                Tab tab = mTabAdapter.createNewTab();
                tab.loadUrl(hitUrl, null, false);
                hitPopup.dismiss();
                break;
            case R.id.tv_newWindow:
                addTab(false);
                load(hitUrl);
                hitPopup.dismiss();
                break;
            case R.id.tv_copy:
                ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                if (cmb != null) {
                    cmb.setPrimaryClip(ClipData.newPlainText("hitUrl", hitUrl));
                }
                Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
                hitPopup.dismiss();
                break;
        }
    }

    public boolean onLongClick(View view) {
        addTab(true);
        return true;
    }

    //    private boolean isReload = false;
    //for Fragment use
    public void refreshPage() {
        if(mActiveTab != null) {
            mActiveTab.reloadPage();
//            isReload = true;
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
        for (Tab tab : mTabAdapter.getData()) { //遍历所有Tab，进行WebView设置
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
        for (Tab tab : mTabAdapter.getData()) {
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mNetworkChangeListener, filter);
    }
    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNetworkChangeListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPUtil.setNightMode(this);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.setHandlers(this);
        binding.bottomBar.setHandlers(this);
        refreshLayout = binding.refreshLayout;
        mTabNum = binding.bottomBar.pagesNum;
        mContentWrapper = binding.contentWrapper;
        searchProgress = binding.bottomBar.searchProgress;
        siteTitle = binding.bottomBar.siteTitle;
        mainView = binding.mainView;
        searchBox = binding.searchBox;
        mBottomBar = binding.bottomBar.includeBar;
        mQARecyclerView = binding.showQuickAccessList;
        instance = this;

        mTabAdapter = new TabAdapter(null, this);
//        mTabController = new TabController(this, this, mTabAdapter);
        mFactory = new BrowserWebViewFactory(this);

        // 先建立一个tab标记主页
        if (mTabAdapter.getItemCount() <= 0) {
           addTab(false);
        }

        InputStream is = this.getResources().openRawResource(R.raw.night);
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

        favHisDao = new FavHisDao(this, SQLiteHelper.TABLE_QA);

        mList = favHisDao.queryAll();
        mQuickAccessAdapter = new QuickAccessAdapter(mList);

        //开启拖拽
        itemTouchHelper = new ItemTouchHelper(new ItemDragAndSwipeCallback(mQuickAccessAdapter));
        itemTouchHelper.attachToRecyclerView(mQARecyclerView);
        mQuickAccessAdapter.enableDragItem(itemTouchHelper, R.id.ib_qa_icon, true);
        mQuickAccessAdapter.setOnItemDragListener(onQADragListener);

        mQARecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mQARecyclerView.setAdapter(mQuickAccessAdapter);
        mQuickAccessAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FavHisEntity item = (FavHisEntity)adapter.getItem(position);
                if(item != null) load(item.getUrl());
            }
        });
        mQuickAccessAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                isEditMode = true;
                mQuickAccessAdapter.setShowClose();
                return true;
            }
        });
        mQuickAccessAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.ib_qa_close) {
                    FavHisEntity item = (FavHisEntity)adapter.getItem(position);
                    favHisDao.delete(item.getUrl());
                    mQuickAccessAdapter.remove(position);
                }
            }
        });

        mTabAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectTab(mTabAdapter.getItem(position));
                if(mTabAdapter.getCurrentPos() != position) {
                    mTabAdapter.notifyItemChanged(mTabAdapter.getCurrentPos());
                    mTabAdapter.setCurrentPos(position);
                    mTabAdapter.notifyItemChanged(mTabAdapter.getCurrentPos());
                }
            }
        });
        mTabAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId() == R.id.tabClose) {
                    removeTab(position);
                }
            }
        });

        mActiveTab.getWebView().setOnLongClickListener(onLongClickListener);
    }

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            WebView.HitTestResult result = ((WebView)v).getHitTestResult();
            if (result == null)
                return false;
            switch(result.getType()) {
                case WebView.HitTestResult.EDIT_TEXT_TYPE: // 文字
                    break;
                case WebView.HitTestResult.PHONE_TYPE: // 拨号
                    break;
                case WebView.HitTestResult.EMAIL_TYPE: // Email
                    break;
                case WebView.HitTestResult.GEO_TYPE: // 地图
                    break;
                case WebView.HitTestResult.SRC_ANCHOR_TYPE: // 超链接
                    hitUrl = result.getExtra();
                    showHitPopupWindow(v);
                    break;
                case WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE: // 带有链接的图片
                case WebView.HitTestResult.IMAGE_TYPE: // 处理长按图片的菜单项 }
                    return true;
                case WebView.HitTestResult.UNKNOWN_TYPE: //未知
                    Log.e(TAG, "onLongClick: UNKNOWN_TYPE");
                    break;
            }
            return false;
        }
    };

    private PopupWindow hitPopup;
    private String hitUrl;
    private void showHitPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PopHitTestBinding binding = DataBindingUtil.inflate(inflater, R.layout.pop_hit_test, null, false);
        binding.setHandlers(this);
        contentView = binding.getRoot();

        hitPopup = new PopupWindow(contentView, ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        hitPopup.setOutsideTouchable(true);
        hitPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        hitPopup.showAtLocation(view, Gravity.NO_GRAVITY, touchX, touchY);
    }

    private OnItemDragListener onQADragListener = new OnItemDragListener() {
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
    private OnItemSwipeListener onTAbSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {}

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {}

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            removeTab(pos);
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) { }
    };
    public static void addToMain(FavHisEntity data) {
        mQuickAccessAdapter.addData(data);
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

        mActiveTab = mTabAdapter.createNewTab();
        mTabAdapter.setActiveTab(mActiveTab);
//        mTabAdapter.setLastSelectedPos(mTabController.getCurrentPosition());
    }

    private TabPopupWindow popupWindow;
    private View contentView;
    private PopTabListBinding binding;
    private void showPopupWindow(View view) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.pop_tab_list, null, false);
        binding.setHandlers(this);
        contentView = binding.getRoot();

        popupWindow = new TabPopupWindow(contentView);
        popupWindow.show((View) view.getParent());
    }
    private void showTabs() {
        Log.e(TAG, "showTabs: " + mTabAdapter.getCurrentPos());
        LinearLayoutManager layout = new LinearLayoutManager(contentView.getContext());
        layout.setStackFromEnd(true); //倒序
        //layout.setReverseLayout(true);
        Log.e(TAG, "showTabs: " + mContentWrapper.getMeasuredHeight());

        mRecyclerView = binding.showTabList;
        mRecyclerView.setLayoutManager(layout);
        mRecyclerView.setAdapter(mTabAdapter);
        // 开启Tab滑动删除
        itemTouchHelper = new ItemTouchHelper(new ItemDragAndSwipeCallback(mTabAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mTabAdapter.enableSwipeItem();
        mTabAdapter.setOnItemSwipeListener(onTAbSwipeListener);
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
    private int touchX;
    private int touchY;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN && isEditMode) { //退出QuickAccess编辑模式
            isEditMode = false;
            if(isShouldExit(getCurrentFocus(), ev)) {
                mQuickAccessAdapter.setShowClose();
                return true;
            }
        }
        final WebView webView = mActiveTab.getWebView();
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchX = (int) event.getRawX();
                touchY = (int) event.getRawY();
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
    private ActionMode mActionMode = null;
    @Override
    public void onActionModeStarted(ActionMode mode) {
        if (mActionMode == null) {
            mActionMode = mode;
            Menu menu = mode.getMenu();
            menu.clear();
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.select, menu);

            menu.findItem(R.id.search).setOnMenuItemClickListener(mListener);
            menu.findItem(R.id.copy).setOnMenuItemClickListener(mListener);
//            for (int i = 0; i < 2; i++) {
//                MenuItem item = menu.getItem(i);
//                item.setOnMenuItemClickListener(mListener);
//            }
        }
        super.onActionModeStarted(mode);
    }
    private MenuItem.OnMenuItemClickListener mListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                JSUtil.getSelectedData(mActiveTab.getWebView());
                Log.e(TAG, "onMenuItemClick: "+menuItem.getItemId());
                switch (menuItem.getItemId()) {
                    case R.id.copy:
                        Toast.makeText(instance, "复制成功", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.search:
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if(WebAppInterface.getValue() != null)
                                    mActiveTab.loadUrl(SPUtil.getSearchUrl(instance, WebAppInterface.getValue()),
                                            null,true);
//                                    loadFromSelect();
                                }
                            }, 100);
                        break;
                    default: return false;
                }
                releaseActionMode();
                return true;
            }
    };

    private void releaseActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }
    @Override
    public void onActionModeFinished(ActionMode mode) {
        releaseActionMode();
//        mActiveTab.getWebView().clearFocus();
        super.onActionModeFinished(mode);
    }

    private long mExitTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(isEditMode) { //退出QuickAccess编辑模式
                isEditMode = false;
                mQuickAccessAdapter.setShowClose();
                return true;
            }
            if(popupWindow != null && popupWindow.isFocusable()) {
                popupWindow.dismiss();
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
//        removeWebView();
        mActiveTab = tab;
        mTabAdapter.setActiveTab(mActiveTab);
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

    public void removeTab(int position) {
        Log.e(TAG, "removeTab: " + mTabAdapter.getItemCount());
        if(mTabAdapter.getItemCount() == 1) {
            addTab(true);
            popupWindow.dismiss();
        }
        mTabAdapter.removeTab(position);
    }

    public void load(String url) {
        if (mActiveTab != null) {
//            mActiveTab.clearWebHistory();
            mActiveTab.loadUrl(url, null,true);
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
            if(mActiveTab == tab) {
                searchProgress.setVisibility(View.VISIBLE);
                if(!hasTitle) siteTitle.setText(tab.getUrl());
            }
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

        if (!(URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url) )) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if(this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) { //应用已安装
                ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Confirm", "open_app");
                bundle.putString("eventUrl", url);
                confirmDialogFragment.setArguments(bundle);
                confirmDialogFragment.show(getSupportFragmentManager(), "fragment_confirm_dialog");
            } else {
                Toast.makeText(this, "应用未安装", Toast.LENGTH_SHORT)
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
//        CookieManager cookieManager = CookieManager.getInstance();
//        String cookieStr = cookieManager.getCookie(tab.getUrl()); // 获取到cookie字符串值
        mTabAdapter.notifyDataSetChanged();
    }

    public void darkMode() {
//        SharedPreferences sp = getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
//        Boolean darkMode = sp.getBoolean("dark_state", false);
//        if(darkMode) {
//            mActiveTab.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);"
//                    + "var style = document.createElement('style');"
//                    + "style.type = 'text/css';" + "style.innerHTML = window.atob('" + nightCode + "');"
//                    + "parent.appendChild(style)" + "})();", null, false);
//        }
    }
    @Override
    public void onProgressChanged(Tab tab) {
        searchProgress.setProgress(tab.getPageLoadProgress());
        darkMode();
    }
    private boolean hasTitle = false;
    @Override
    public void onReceivedTitle(Tab tab, String title) {
        if(mActiveTab == tab) {
            hasTitle = true;
            siteTitle.setText(title);
        }
        Log.e(TAG, "onReceivedTitle: " + tab.getUrl() + " " + tab.getTitle() + redirect);
        if(!redirect &&!tab.isGoBack()) {
            Log.e(TAG, "ADD");
            tab.add(tab.getUrl());
            saveAsHistory(tab);
        }
        tab.showHistory();
//        isReload = false;
        tab.setGoBack(false);
        mTabAdapter.notifyDataSetChanged();
    }

    private void saveAsHistory(Tab tab) {
        SharedPreferences sp = getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        Boolean noTrack = sp.getBoolean("track_state", false);
        if (!noTrack && NetworkUtil.isNetworkConnected(this) && !mIsInMain) {
            FavHisDao favHisDao = new FavHisDao(this, SQLiteHelper.TABLE_HIS);
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
        mTabNum.setText(String.valueOf(mTabAdapter.getItemCount())); // 更新页面数量
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

    @Override
    public boolean onJsAlert(String message, final JsResult result) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Confirm", "alert");
        bundle.putString("message", message);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getSupportFragmentManager(), "fragment_confirm_dialog");

        setJsResult(result);
        return true;
    }

    @Override
    public boolean onJsConfirm(String message, JsResult result) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Confirm", "confirm");
        bundle.putString("message", message);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getSupportFragmentManager(), "fragment_confirm_dialog");
        setJsResult(result);
        return true;
    }

    @Override
    public boolean onJsPrompt(String message, String defaultValue, JsPromptResult result) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Confirm", "prompt");
        bundle.putString("message", message);
        bundle.putString("defaultValue", defaultValue);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(getSupportFragmentManager(), "fragment_confirm_dialog");
        setJsResult(result);
        return true;
    }

    private JsResult result;
    private JsPromptResult promptResult;
    public void setJsResult(JsResult result) {
       this.result = result;
    }
    public void setJsResult(JsPromptResult promptResult) {
        this.promptResult = promptResult;
    }
    public JsResult getJsResult() {
        return result;
    }
    public JsPromptResult getJsPromptResult() {
        return promptResult;
    }
    public static void ClearCache() {
        for (Tab tab : mTabAdapter.getData()) { //遍历所有Tab，进行WebView设置
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