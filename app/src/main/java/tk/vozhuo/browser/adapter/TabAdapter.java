package tk.vozhuo.browser.adapter;

import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import tk.vozhuo.browser.R;
import tk.vozhuo.browser.entity.Tab;
import tk.vozhuo.browser.interfaces.web.UiController;

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
        final WebView w = mController.getWebViewFactory().createWebView();
        Tab t = new Tab(mController, w, null);
        addData(t);
        if (mController != null) {
            mController.onTabCountChanged();
        }
        return t;
    }
    public Tab createNewTab(Bundle state) {
        final WebView w = mController.getWebViewFactory().createWebView();
        Tab t = new Tab(mController, w, state);
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

    private static final String POSITIONS = "positions";
    private static final String CURRENT = "current";
    private static long sNextId = 1;
    /**
     * save the tab state:
     * current position
     * position sorted array of tab ids
     * for each tab id, save the tab state
     * @param outState
     */
    public void saveState(Bundle outState) {
        final int numTabs = getItemCount();
        if (numTabs == 0) {
            return;
        }
        long[] ids = new long[numTabs];
        int i = 0;
        for (Tab tab : getData()) {
            Bundle tabState = tab.saveState();
            if (tabState != null) {
                ids[i++] = tab.getId();
                String key = Long.toString(tab.getId());
                outState.putBundle(key, tabState);
            } else {
                ids[i++] = -1;
                // Since we won't be restoring the thumbnail, delete it
                // tab.deleteThumbnail();
            }
        }
        if (!outState.isEmpty()) {
            outState.putLongArray(POSITIONS, ids);
            Tab current = getItem(currentPos);
            long cid = -1;
            if (current != null) {
                cid = current.getId();
            }
            outState.putLong(CURRENT, cid);
        }
    }
    /**
     * Check if the state can be restored.  If the state can be restored, the
     * current tab id is returned.  This can be passed to restoreState below
     * in order to restore the correct tab.  Otherwise, -1 is returned and the
     * state cannot be restored.
     */
    public long canRestoreState(Bundle inState, boolean restoreIncognitoTabs) {
        final long[] ids = (inState == null) ? null : inState.getLongArray(POSITIONS);
        if (ids == null) {
            return -1;
        }
        final long oldcurrent = inState.getLong(CURRENT);
        long current = -1;
        if (restoreIncognitoTabs || (hasState(oldcurrent, inState))) {
            current = oldcurrent;
        } else {
            // pick first non incognito tab
            for (long id : ids) {
                if (hasState(id, inState)) {
                    current = id;
                    break;
                }
            }
        }
        return current;
    }

    private boolean hasState(long id, Bundle state) {
        if (id == -1) return false;
        Bundle tab = state.getBundle(Long.toString(id));
        return ((tab != null) && !tab.isEmpty());
    }
    /**
     * Restore the state of all the tabs.
     * @param currentId The tab id to restore.
     * @param inState The saved state of all the tabs.
     * @param restoreIncognitoTabs Restoring private browsing tabs
     * @param restoreAll All webviews get restored, not just the current tab
     *        (this does not override handling of incognito tabs)
     */
    public void restoreState(Bundle inState, long currentId,
                             boolean restoreIncognitoTabs, boolean restoreAll) {
        if (currentId == -1) {
            return;
        }
        long[] ids = inState.getLongArray(POSITIONS);
        long maxId = -Long.MAX_VALUE;
//        LongSparseArray tabMap = new LongSparseArray();
        HashMap<Long, Tab> tabMap = new HashMap<>();
        for (long id : ids) {
            if (id > maxId) {
                maxId = id;
            }
            final String idkey = Long.toString(id);
            Bundle state = inState.getBundle(idkey);
            if (state == null || state.isEmpty()) {
                // Skip tab
                continue;
            }  else if (id == currentId || restoreAll) {
                Tab t = createNewTab(state);
                if (t == null) {
                    // We could "break" at this point, but we want
                    // sNextId to be set correctly.
                    continue;
                }
                tabMap.put(id, t);
                // Me must set the current tab before restoring the state
                // so that all the client classes are set.
                if (id == currentId) {
//                    setCurrentTab(t);
                }
            } else {
                // Create a new tab and don't restore the state yet, add it
                // to the tab list
                Tab t = new Tab(mController, state);
                tabMap.put(id, t);
                addData(t);
//                mTabs.add(t);
                if (mController != null) {
                    mController.onTabCountChanged();
                }
            }
        }

        // make sure that there is no id overlap between the restored
        // and new tabs
        sNextId = maxId + 1;
//
//        if (mCurrentTab == -1) {
//            if (getTabCount() > 0) {
//                setCurrentTab(getTab(0));
//            }
//        }
    }
}