package cf.vozhuo.app.broswser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import cf.vozhuo.app.broswser.util.SPUtil;

public class MyWebView extends WebView {
    private Context mContext;
    private static String value;
    public MyWebView(Context context) {
        super(context);
        mContext = context;
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
       ActionMode actionMode = super.startActionMode(callback);
        return resolveMode(actionMode);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        ActionMode actionMode = super.startActionMode(callback);
        return resolveMode(actionMode);
    }
    private ActionMode resolveMode(ActionMode actionMode) {
        if(actionMode != null) {
            final Menu menu = actionMode.getMenu();
            menu.clear();

            for (int i = 0; i < title.length; i++) {
                menu.add(Menu.NONE, i, Menu.NONE, title[i]);
            }
            for (int i = 0; i < title.length; i++) {
                MenuItem item = menu.getItem(i);
                item.setOnMenuItemClickListener(mListener);
            }
            mActionMode = actionMode;
        }
        return actionMode;
    }
    private static boolean doSearch = false;
    private MenuItem.OnMenuItemClickListener mListener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                getSelectedData();
                switch (menuItem.getItemId()) {
                    case 0:
                        Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        doSearch = true;
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if(value != null)
                                    MainActivity.instance.loadFromSelect(SPUtil.getSearchUrl(mContext, value));
                                }
                            }, 100);
                        break;
                    default: return false;
                }
                releaseActionMode();
                return true;
            }
    };
    private final String title[] = {"复制", "搜索"};
    private ActionMode mActionMode;
    private void releaseActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }
    private void getSelectedData() {
        String js = "(function getSelectedText() {"+
                "var txt;"+
                "if (window.getSelection) {"+
                "txt = window.getSelection().toString();"+
                "} else if (window.document.getSelection) {"+
                "txt = window.document.getSelection().toString();"+
                "} else if (window.document.selection) {"+
                "txt = window.document.selection.createRange().text;"+
                "}"+
                "JSInterface.getText(txt);"+
                "})()";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //android系统4.4以上的时候调用js方法用这个
            evaluateJavascript("javascript:" + js, null);
        } else {
            loadUrl("javascript:" + js);
        }
    }

    public static class WebAppInterface {
        private static final String TAG = "WebAppInterface";
        Context mContext;
        public WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void getText(String text) {
            value = text;
            Log.e("dsds", "getText: "+ text + doSearch);
            ClipboardManager clipboard = (ClipboardManager)
                    mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("copy", text);
            clipboard.setPrimaryClip(clip);
//            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();

//            if(doSearch) {
//                Log.e(TAG, "getText: " + text);
//
//                MainActivity.instance.load(SPUtil.getSearchUrl(mContext, text));
//                doSearch = false;
//            }
        }
    }
}
