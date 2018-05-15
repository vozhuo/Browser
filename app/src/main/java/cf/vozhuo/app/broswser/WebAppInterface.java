package cf.vozhuo.app.broswser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.webkit.JavascriptInterface;

public class WebAppInterface {
    private Context mContext;
    private static String value;
    public WebAppInterface(Context c) {
        mContext = c;
    }
    @JavascriptInterface
    public void result(String text) {
        value = text;
        ClipboardManager clipboard = (ClipboardManager)
                mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy", text);
        clipboard.setPrimaryClip(clip);
    }

    public static String getValue() {
        return value;
    }
}
