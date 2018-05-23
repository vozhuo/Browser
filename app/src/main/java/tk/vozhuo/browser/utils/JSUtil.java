package tk.vozhuo.browser.utils;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;

import tk.vozhuo.browser.R;

public class JSUtil {
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

    public static void loadNightCode(Context context, WebView webView) {
        InputStream is = context.getResources().openRawResource(R.raw.night);
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
        String code = Base64.encodeToString(buffer, Base64.NO_WRAP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" + "style.type = 'text/css';"
                    + "style.innerHTML = window.atob('" + code + "');" + "parent.appendChild(style)" + "})();", null);
        } else {
            webView.loadUrl("javascript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" + "style.type = 'text/css';"
                    + "style.innerHTML = window.atob('" + code + "');" + "parent.appendChild(style)" + "})();");
        }
    }
}
