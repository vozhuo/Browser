package cf.vozhuo.app.broswser.tab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import cf.vozhuo.app.broswser.MainActivity;
import cf.vozhuo.app.broswser.R;

public class Tab {
    public static final String DEFAULT_BLANK_URL = "about:blank";
    private final static String TAG = "TAB";
    private static final int INITIAL_PROGRESS = 5;
    private static final int MSG_CAPTURE = 42;
    private boolean mWillBeClosed = false;
    private static Bitmap sDefaultFavicon; //默认网站图标

    private long mId = -1;

    // WebView controller
    WebViewController mWebViewController;
    private boolean mUpdateThumbnail;
    //
    Context mContext;

    // Main WebView wrapper
    private View mContainer;
    // Main WebView
    private WebView mMainView;
    // Subwindow container
    private View mSubViewContainer;
    // Subwindow WebView
    private WebView mSubView;
    // Saved bundle for when we are running low on memory. It contains the
    // information needed to restore the WebView if the user goes back to the
    // tab.
    private Bundle mSavedState;

    // If true, the tab is in page loading state (after onPageStarted,
    // before onPageFinsihed)
    private boolean mInPageLoad;

    // The last reported progress of the current page
    private int mPageLoadProgress;

    // The time the load started, used to find load page time
    private long mLoadStartTime;

//    private int mCaptureWidth;
//    private int mCaptureHeight;
//
//    private Bitmap mCapture;
//    private Handler mHandler;
//    private boolean mUpdateThumbnail;

    public String mSavePageTitle;
    public String mSavePageUrl;
    // save page
    HashMap<Integer, Long> mSavePageJob;
    private PageState mCurrentState;

    // 用来存储页面信息
    static final String ID = "_id";
    static final String CURRENT_URL = "currentUrl";
    static final String CURRENT_TITLE = "currentTitle";
    private boolean mInForeground;
    private static Paint sAlphaPaint = new Paint();
    private Stack<String> mBrowsedHistory = new Stack<>();
    private Stack<String> mForwardHistory = new Stack<>();
    static {
        sAlphaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        sAlphaPaint.setColor(Color.TRANSPARENT);
    }
    // 获取默认网页图标
    private static synchronized Bitmap getDefaultFavicon(Context context) {
        if (sDefaultFavicon == null) {
            sDefaultFavicon = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.web);
        }
        return sDefaultFavicon;
    }

    // 构造WebViewClient
    private final WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mInPageLoad = true;
            mUpdateThumbnail = true;
            mPageLoadProgress = INITIAL_PROGRESS;
            mCurrentState = new PageState(mContext, url, favicon);
            mLoadStartTime = SystemClock.uptimeMillis();
            mWebViewController.onPageStarted(Tab.this,view,favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return mWebViewController.shouldOverrideUrlLoading(Tab.this, view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            syncCurrentState(view,url);
            if(url != null && url.equals(mSavePageUrl)){
                mCurrentState.mTitle = mSavePageTitle;
                mCurrentState.mUrl = mSavePageUrl;
            }
            mWebViewController.onPageFinished(Tab.this);
        }
    };

    // 构造 WebChromeClient
    private WebChromeClient mWebChromeClient = new WebChromeClient(){
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mPageLoadProgress = newProgress;
            if (newProgress == 100) {
                mInPageLoad = false;
                syncCurrentState(view, view.getUrl());
            }
            mWebViewController.onProgressChanged(Tab.this);
            if (mUpdateThumbnail && newProgress == 100) {
                mUpdateThumbnail = false;
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return mWebViewController.onJsAlert(message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return mWebViewController.onJsConfirm(message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return mWebViewController.onJsPrompt(message, defaultValue, result);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mCurrentState.mTitle = title;
            mWebViewController.onReceivedTitle(Tab.this, title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            mCurrentState.mFavicon = icon;
            mWebViewController.onFavicon(Tab.this, view, icon);
        }
    };

    public void loadBlank(){
        loadUrl(DEFAULT_BLANK_URL,null,false);
    }
    public Tab(WebViewController webViewController,WebView view){
        this(webViewController,view,null);
    }
    public Tab(WebViewController webViewController,Bundle state){
        this(webViewController,null,state);
    }

    public Tab(WebViewController webViewController,WebView view,Bundle state){
        mSavePageJob = new HashMap<Integer, Long>();
        mWebViewController = webViewController;
        mContext = MainActivity.instance;
        mCurrentState = new PageState(mContext);
        mInPageLoad = false;

        restoreState(state);
        if (getId() == -1) {
            mId++;
        }
        setWebView(view);
        mBrowsedHistory.push(DEFAULT_BLANK_URL);
//        currentPos = mBrowsedHistory.size();
    }

    private void restoreState(Bundle state){
        mSavedState = state;
        if(mSavedState == null){
            return;
        }
        mId = state.getLong(ID);
        String url = state.getString(CURRENT_URL);
        String title = state.getString(CURRENT_TITLE);
        mCurrentState = new PageState(mContext,url,title);
    }

    /**
     * This is used to get a new ID when the tab has been preloaded, before it is displayed and
     * added to TabControl. Preloaded tabs can be created before restoreInstanceState, leading
     * to overlapping IDs between the preloaded and restored tabs.
     */
    public void refreshIdAfterPreload() {
        mId++;
    }

    public void setController(WebViewController ctl) {
        mWebViewController = ctl;
    }
    public long getId() {
        return mId;
    }
    void setWebView(WebView w) {
        setWebView(w, true);
    }

    /**
     * Sets the WebView for this tab, correctly removing the old WebView from
     * the container view.
     */
    void setWebView(WebView w, boolean restore) {
        if (mMainView == w) {
            return;
        }
        mWebViewController.onSetWebView(this, w);

        if (mMainView != null) {
//            mMainView.setPictureListener(null);
            if (w != null) {
                syncCurrentState(w, null);
            } else {
                mCurrentState = new PageState(mContext);
            }
        }
        // set the new one
        mMainView = w;
        // attach the WebViewClient, WebChromeClient and DownloadListener
        if (mMainView != null) {
            mMainView.setWebViewClient(mWebViewClient);
            mMainView.setWebChromeClient(mWebChromeClient);
//            TabController tc = mWebViewController.getTabController();
            /*
            if (tc != null && tc.getOnThumbnailUpdatedListener() != null) {
                mMainView.setPictureListener(this);
            }
            */
            if (restore && (mSavedState != null)) {
                // restoreUserAgent();
                WebBackForwardList restoredState
                        = mMainView.restoreState(mSavedState);
                if (restoredState == null || restoredState.getSize() == 0) {
                    Log.w(TAG, "Failed to restore WebView state!");
                    loadUrl(mCurrentState.mOriginalUrl, null,true);
                }
                mSavedState = null;
            }
        }
    }

    /**
     * Destroy the tab's main WebView and subWindow if any
     */
    public void destroy() {
        if (mMainView != null) {
            dismissSubWindow();
            // save the WebView to call destroy() after detach it from the tab
            WebView webView = mMainView;
            setWebView(null);
            webView.destroy();
        }
        /// M: add for save page @ {
        if (mSavePageJob == null) {
            return;
        }
        if (mSavePageJob.size() != 0) {
            // new CancelSavePageTask().execute();
        }
        /// @ }
    }
    /// @ }

    /**
     * Dismiss the subWindow for the tab.
     */
    void dismissSubWindow() {
        if (mSubView != null) {
            mSubView.destroy();
            mSubView = null;
            mSubViewContainer = null;
        }
    }

    void resume() {
        if (mMainView != null) {
            setupHwAcceleration(mMainView);
            mMainView.onResume();
            if (mSubView != null) {
                mSubView.onResume();
            }
        }
    }
    void pause() {
        if (mMainView != null) {
            mMainView.onPause();
            if (mSubView != null) {
                mSubView.onPause();
            }
        }
    }
    void putInForeground() {
        if (mInForeground) {
            return;
        }
        mInForeground = true;
        resume();
    }
    void putInBackground() {
//        Log.e(TAG,"putInBackground ------- mInForeground =:" + mInForeground);
        if (!mInForeground) {
            return;
        }
        mInForeground = false;
        pause();
        mMainView.setOnCreateContextMenuListener(null);
        if (mSubView != null) {
            mSubView.setOnCreateContextMenuListener(null);
        }
    }

    boolean inForeground() {
        return mInForeground;
    }

    /**
     * Return the main window of this tab. Note: if a tab is freed in the
     * background, this can return null. It is only guaranteed to be
     * non-null for the current tab.
     * @return The main WebView of this tab.
     */
    public WebView getWebView() {
        return mMainView;
    }

    void setViewContainer(View container) {
        mContainer = container;
    }
    public String getUrl() {
        return mCurrentState.mUrl;
    }

    public boolean checkUrlNotNull(){
        return mCurrentState.checkUrlNotNull();
    }
    public String getCurrentUrl(){
//        for(int i = 0 ;i < mBrowsedHistory.size();i++){
//            Log.e(TAG,"getCurrentUrl :: 第 " + i +"项  :" + mBrowsedHistory.elementAt(i));
//        }
        return mBrowsedHistory.peek();
    }
    public String getPreUrl(){
        int size = mBrowsedHistory.size();
        int pre = size - 2;
        if(pre >= 0){
            return mBrowsedHistory.elementAt(pre);
        }
        return DEFAULT_BLANK_URL;
    }
    public String getOriginalUrl() {
        if (mCurrentState.mOriginalUrl == null) {
            return getUrl();
        }
        return mCurrentState.mOriginalUrl;
    }
    /**
     * Get the title of this tab.
     */
    public String getTitle() {
        if (mCurrentState.mTitle == null && mInPageLoad) {
            return mContext.getString(R.string.title_bar_loading);
        }
        return mCurrentState.mTitle;
    }
    /**
     * Get the favicon of this tab.
     */
    public Bitmap getFavicon() {
        if (mCurrentState.mFavicon != null) {
            return mCurrentState.mFavicon;
        }
        return getDefaultFavicon(mContext);
    }

    public byte[] getFaviconBytes() {
        Bitmap bitmap = getFavicon();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public int getPageLoadProgress(){
        return mPageLoadProgress;
    }
    public boolean isBlank(){
        return mBrowsedHistory.peek().equals(DEFAULT_BLANK_URL);
    }
    public void insertBlank(){
        mBrowsedHistory.push(DEFAULT_BLANK_URL);
        for(int i = 0 ;i < mBrowsedHistory.size();i++){
            Log.e(TAG,"insertBlank :: 第 " + i +"项  :" + mBrowsedHistory.elementAt(i));
        }
    }
    public void clearWebHistory(){
        mMainView.clearHistory();
        mMainView.clearCache(true);
        mMainView.loadUrl(DEFAULT_BLANK_URL);
    }
    public void pushForwardHistory(String url) {
        mForwardHistory.push(url);
    }
    public void popBrowsedHistory(){
        mBrowsedHistory.pop();
    }
    public void showHistory(){
        for(int i = 0 ;i < mBrowsedHistory.size();i++){
            Log.e(TAG,"SHOW " + mBrowsedHistory.elementAt(i));
        }
    }

    public void clearTabData(){
        mCurrentState.mUrl = DEFAULT_BLANK_URL;
        mCurrentState.mOriginalUrl = DEFAULT_BLANK_URL;
        mCurrentState.mTitle = mContext.getString(R.string.defaultWebTitle);

        mBrowsedHistory.clear();
        insertBlank();
    }
    /**
     * @return TRUE if onPageStarted is called while onPageFinished is not
     *         called yet.
     */
    boolean inPageLoad() {
        return mInPageLoad;
    }
    /**
     * @return The Bundle with the tab's state if it can be saved, otherwise null
     */
    public Bundle saveState() {
        // If the WebView is null it means we ran low on memory and we already
        // stored the saved state in mSavedState.
        if (mMainView == null) {
            return mSavedState;
        }

        if (TextUtils.isEmpty(mCurrentState.mUrl)) {
            return null;
        }

        mSavedState = new Bundle();
        WebBackForwardList savedList = mMainView.saveState(mSavedState);
        if (savedList == null || savedList.getSize() == 0) {
            Log.w(TAG, "Failed to save back/forward list for "
                    + mCurrentState.mUrl);
        }

        mSavedState.putLong(ID, mId);
        mSavedState.putString(CURRENT_URL, mCurrentState.mUrl);
        mSavedState.putString(CURRENT_TITLE, mCurrentState.mTitle);
        return mSavedState;
    }
//    public Bitmap getScreenshot() {
//        synchronized (Tab.this) {
//            return mCapture;
//        }
//    }

    private void setupHwAcceleration(View web) {
        if (web == null) return;
        // 这里需要用户自己设置
        if (true) {
            web.setLayerType(View.LAYER_TYPE_NONE, null);
        } else {
            web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
    public void stopLoading(){
        if(mMainView != null && inPageLoad()){
            mMainView.stopLoading();
        }
    }

    public void reloadPage(){
        mMainView.reload();
    }
    private void syncCurrentState(WebView view,String url){
        if(mWillBeClosed){
            return;
        }
        mCurrentState.mUrl = view.getUrl();
        if(mCurrentState.mUrl == null){
            mCurrentState.mUrl = mContext.getString(R.string.defaultWebTitle);
        }
        mCurrentState.mOriginalUrl = view.getOriginalUrl();
        mCurrentState.mTitle = view.getTitle();
//        mCurrentState.mFavicon = view.getFavicon();
    }
    public void add(String url) {
        if(!mBrowsedHistory.peek().equals(url)) mBrowsedHistory.push(url);
//        currentPos = mBrowsedHistory.size();
        if(!mForwardHistory.isEmpty()) mForwardHistory.clear();
    }
    public void loadUrl(String url, Map<String, String> headers, boolean record) {
        if (mMainView != null) {
            mPageLoadProgress = INITIAL_PROGRESS;
            mInPageLoad = true;
            mWebViewController.onPageStarted(this, mMainView, null);
            try{
                mMainView.loadUrl(url, headers);
                if(record) mBrowsedHistory.push(url);
                for(int i = 0 ;i < mBrowsedHistory.size();i++){
                    Log.e(TAG,"loadUrl :: 第 " + i +"项  :" + mBrowsedHistory.elementAt(i) + " ,size =:" +  mBrowsedHistory.size());
                }
            }catch(SecurityException e){
                e.printStackTrace();
            }
        }
    }

//    private int currentPos;
//
//    public int getCurrentPos() {
//        return currentPos;
//    }
//
//    public void setCurrentPos(int currentPos) {
//        this.currentPos = currentPos;
//    }

    public boolean canGoBack() {
//        for(int i = 0 ;i < mBrowsedHistory.size();i++){
//            Log.e(TAG,"canGoBack :: 第 " + i +"项  :" + mBrowsedHistory.elementAt(i) + " ,size =:" +  mBrowsedHistory.size());
//        }
        boolean isBlank = DEFAULT_BLANK_URL.equals(mBrowsedHistory.peek());
        boolean isSingle = mBrowsedHistory.size() == 1;

//        Log.e(TAG,"canGoBack :: " + currentPos);
        return mMainView != null && !(isSingle && isBlank);
//        return mMainView != null && !isBlank;

    }

    public boolean canGoForward() {
//        boolean isBlank = DEFAULT_BLANK_URL.equals(mBrowsedHistory.peek());
//        boolean isSingle = (mBrowsedHistory.size() == 1);
//        boolean isLast = (currentPos == mBrowsedHistory.size());

        return mMainView != null && !mForwardHistory.isEmpty();
    }

    private boolean isGoBack = false;

    public void setGoBack(boolean goBack) {
        isGoBack = goBack;
    }

    public boolean isGoBack() {
        return isGoBack;
    }

    public void goBack() {
        if (mMainView != null) {

            mForwardHistory.push(mBrowsedHistory.peek());
            mBrowsedHistory.pop();

//            currentPos -= 1;
//            Log.e(TAG, "goBack: "+ currentPos + " " + mBrowsedHistory.peek());
//            mMainView.loadUrl(mBrowsedHistory.elementAt(currentPos - 1));
            mMainView.loadUrl(mBrowsedHistory.peek());
            isGoBack = true;
//            WebBackForwardList list = mMainView.copyBackForwardList();
//            String url;
//            for (int i = 0; i < list.getSize(); i++) {
//                url = list.getItemAtIndex(i).getUrl();
//                Log.e(TAG, "WebBackForwardList: 第 " + i +"项  :" + url);
//            }
//            mMainView.goBack();
            for(int i = 0 ;i < mBrowsedHistory.size();i++) {
                Log.e(TAG,"goBack :: 第 " + i +"项  :" + mBrowsedHistory.elementAt(i) + " ,size =:" +  mBrowsedHistory.size());
            }
        }
    }

    public void goForward() {
        if (mMainView != null) {
//            currentPos += 1;
//            Log.e(TAG, "goBack: "+ currentPos + " " + mBrowsedHistory.elementAt(currentPos - 1));
//            mMainView.loadUrl(mBrowsedHistory.elementAt(currentPos - 1));
            mMainView.loadUrl(mForwardHistory.peek());
            mBrowsedHistory.push(mForwardHistory.peek());
            mForwardHistory.pop();
            isGoBack = true;
        }
    }

    public static class PageState{
        String mUrl;
        String mOriginalUrl;
        String mTitle;
        Bitmap mFavicon;
        PageState(Context context){
            this(context,"",getDefaultFavicon(context));
        }
        PageState(Context context,String url,Bitmap favicon){
            this(url,context.getString(R.string.defaultWebTitle),favicon);
        }
        PageState(Context context,String url,String title){
            this(url,title,getDefaultFavicon(context));
        }
        PageState(String url,String title,Bitmap favicon){
            mUrl = mOriginalUrl = url;
            mTitle = title;
            mFavicon = favicon;
        }
        boolean checkUrlNotNull(){
            return !TextUtils.isEmpty(mUrl);
        }
    }
}