package cf.vozhuo.app.broswser;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
            for (String aTitle : title) {
                menu.add(aTitle);
            }
            for (int i = 0; i < title.length; i++) {
                MenuItem item = menu.getItem(i);
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String title = menuItem.getTitle().toString();
                        getSelectedData(title); //获取选中的h5页面的文本
                        releaseActionMode();
                        return true;
                    }
                });
            }
            mActionMode = actionMode;
        }
        return actionMode;
    }
    private String title[] = {"复制", "搜索"};
    private ActionMode mActionMode;
    private void releaseActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }
    private void getSelectedData(String title) {
        String js = "(function getSelectedText() {" +
                "var txt;" +
                "var title = \"" + title + "\";" +
                "if (window.getSelection) {" +
                "txt = window.getSelection().toString();" +
                "} else if (window.document.getSelection) {" +
                "txt = window.document.getSelection().toString();" +
                "} else if (window.document.selection) {" +
                "txt = window.document.selection.createRange().text;" +
                "}" +
                "ActionModeJavaScript.callback(txt,title);" +           //回调java方法将js获取的结果传递过去
                "})()";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //android系统4.4以上的时候调用js方法用这个
            evaluateJavascript("javascript:" + js, null);
        } else {
            loadUrl("javascript:" + js);
        }
    }
}
