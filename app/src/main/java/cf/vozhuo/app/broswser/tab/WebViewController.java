package cf.vozhuo.app.broswser.tab;

import android.graphics.Bitmap;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;

public interface WebViewController {
    WebViewFactory getWebViewFactory();
    void onSetWebView(Tab tab, WebView view);
    void onPageStarted(Tab tab, WebView webView, Bitmap favicon);
    boolean shouldOverrideUrlLoading(Tab tab, WebView view, String url);
    void onPageFinished(Tab tab);
    void onProgressChanged(Tab tab);
    void onReceivedTitle(Tab tab,final String title);
    void onFavicon(Tab tab,WebView view,Bitmap icon);
    boolean onJsAlert(String message, JsResult result);
    boolean onJsConfirm(String message, JsResult result);
    boolean onJsPrompt(String message, String defaultValue, JsPromptResult result);
}