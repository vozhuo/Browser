package cf.vozhuo.app.broswser.tab;

import android.graphics.Bitmap;
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
}