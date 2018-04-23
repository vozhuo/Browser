package cf.vozhuo.app.broswser.tab;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import static android.content.ContentValues.TAG;

public class BrowserWebViewFactory implements WebViewFactory {
    private final Context mContext;
    public BrowserWebViewFactory(Context context) {
        mContext = context;
    }
    private WebView instantiateWebView(AttributeSet attrs, int defStyle) {
        return new WebView(mContext, attrs, defStyle);
    }
    @Override
    public WebView createWebView() {
        WebView w = instantiateWebView(null, android.R.attr.webViewStyle);
        initWebViewSettings(w);
        return w;
    }
    private static final String APP_CACHE_DIRNAME = "cache";
    protected void initWebViewSettings(WebView w) {
        w.setScrollbarFadingEnabled(true);
        w.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        // Enable the built-in zoom
        WebSettings webSettings = w.getSettings();
        //设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        //支持JS
        webSettings.setJavaScriptEnabled(true);
        //设置渲染的优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //支持多窗口
        webSettings.setSupportMultipleWindows(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        String cacheDirPath = mContext.getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;
        //设置  Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);

        //加载图片
        SharedPreferences sp = mContext.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        Boolean state = sp.getBoolean("image_state", false);
        if(state) {
            webSettings.setLoadsImagesAutomatically(false);
            Log.e(TAG, "NO Image ");
        } else {
            Log.e(TAG, "YES");
        }
        //无痕浏览
        sp = mContext.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        state = sp.getBoolean("track_state", false);
        if(!state) {
            Log.e(TAG, "initWebViewSettings: 无痕关闭");
            //开启 database storage API 功能
            webSettings.setDatabaseEnabled(true);
            // 开启 DOM storage API 功能
            webSettings.setDomStorageEnabled(true);
            //开启 Application Caches 功能
            webSettings.setAppCacheEnabled(true);
        } else {
            Log.e(TAG, "initWebViewSettings: 无痕开启");
        }
        //设置UA
        sp = mContext.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        String ua = sp.getString("ua", "Android");
//       switch (ua) {
//           case "Android":
//               ua_string = "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36";
//               break;
//           case "PC":
//               ua_string = "";
//               break;
//           case "iPhone":
//               ua_string = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/603.1.23 (KHTML, like Gecko) Version/10.0 Mobile/14E5239e Safari/602.1";
//               break;
//       }
        webSettings.setUserAgentString(ua);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        /// M: Add to disable overscroll mode
        w.setOverScrollMode(View.OVER_SCROLL_NEVER);

        final PackageManager pm = mContext.getPackageManager();
        boolean supportsMultiTouch =
                pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)
                        || pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);
        w.getSettings().setDisplayZoomControls(!supportsMultiTouch);
        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptThirdPartyCookies(w, cookieManager.acceptCookie());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            // Remote Web Debugging is always enabled, where available.
            WebView.setWebContentsDebuggingEnabled(true);
        }
//        w.loadUrl("file:///android_asset/index.html");
    }
}
