package cf.vozhuo.app.broswser.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import java.util.Stack;

import cf.vozhuo.app.broswser.MyWebView;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.tab.Tab;
import cf.vozhuo.app.broswser.tab.UiController;

public class TabAdapter extends BaseItemDraggableAdapter<Tab, BaseViewHolder> {
    private Stack<Tab> mLastTab = new Stack<>();
    private int currentPos = RecyclerView.NO_POSITION;
    private UiController mController;

    public TabAdapter(List<Tab> data, UiController mController) {
        super(R.layout.item_tab, data);
        this.mController = mController;
    }
    @Override
    protected void convert(BaseViewHolder helper, Tab item) {
        helper.setText(R.id.tv_tab_title, item.getTitle())
                .setImageBitmap(R.id.iv_tab_icon, item.getFavicon())
                .addOnClickListener(R.id.tabClose)
                .itemView.setSelected(getCurrentPos() == helper.getLayoutPosition());
    }

    public Tab createNewTab() {
        final MyWebView w = mController.getWebViewFactory().createWebView();
        Tab t = new Tab(mController, w, null);
        addData(t);
        if (mController != null) {
            mController.onTabCountChanged();
        }
        return t;
    }
    public void setActiveTab(Tab tab) {
        Tab t = getItem(currentPos);
        WebView webView;
        if (t == null) {
            webView = null;
        } else {
            webView = t.getWebView();
        }
        if(webView != null && webView.getParent() != null){
            ((ViewGroup) webView.getParent()).removeView(webView);
        }

        if(mLastTab.contains(tab)) {
            mLastTab.remove(tab);
        }
        mLastTab.push(tab);
        currentPos = getData().indexOf(tab);
    }

    public void removeTab(int pos) {
        Tab t = getItem(pos);
        if (t == null) {
            return;
        }
        Tab current = getItem(currentPos);
        remove(pos);
        mLastTab.remove(t);
        if (current == t && getItemCount() > 0) {
            mController.selectTab(mLastTab.peek());
            currentPos = getData().indexOf(mLastTab.peek());
        } else {
            currentPos -= 1;
        }
        t.destroy();

        if (mController != null) {
            mController.onTabCountChanged();
        }
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }
}