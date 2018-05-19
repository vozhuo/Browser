package tk.vozhuo.browser.utils;

import android.os.Build;
import android.webkit.WebView;

public class JSUtil {
    private String value;
    public static void getSelectedData(WebView webView) {
        String js = "(function getSelectedText() {"+
                "var txt;"+
                "if (window.getSelection) {"+
                "txt = window.getSelection().toString();"+
                "} else if (window.document.getSelection) {"+
                "txt = window.document.getSelection().toString();"+
                "} else if (window.document.selection) {"+
                "txt = window.document.selection.createRange().text;"+
                "}"+
                "JSInterface.result(txt);"+
                "})()";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //android系统4.4以上的时候调用js方法用这个
            webView.evaluateJavascript("javascript:" + js, null);
        } else {
            webView.loadUrl("javascript:" + js);
        }
    }
}
