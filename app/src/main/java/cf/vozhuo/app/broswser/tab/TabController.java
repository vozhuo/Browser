//package cf.vozhuo.app.broswser.tab;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.ViewGroup;
//import android.webkit.WebView;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Stack;
//
//import cf.vozhuo.app.broswser.R;
//import cf.vozhuo.app.broswser.adapter.TabAdapter;
//
//public class TabController {
//    private final String TAG = "TabController";
//    private static long sNextId = 1;
//
//    // Maximum number of tabs.
//    private int mMaxTabs;
//    // Private array of WebViews that are used as tabs.
////    private List<Tab> mTabs;
//    // Queue of most recently viewed tabs.
//    private ArrayList<Tab> mTabQueue;
//    // Current position in mTabs.
//    private int mCurrentTab = -1;
//    /// @ }
//    // the main browser controller
//    private UiController mController;
//    /**
//     * Construct a new TabControl object
//     */
//    private TabAdapter mAdapter;
//    public TabController(Context context, UiController controller, TabAdapter adapter) {
//        mController = controller;
//        mMaxTabs = context.getResources().getInteger(R.integer.max_tab_count);
//        mAdapter = adapter;
//        mTabQueue = new ArrayList<Tab>(mMaxTabs);
//    }
//    synchronized static long getNextId(){
//        return sNextId ++;
//    }
//    /**
//     * Return the current tab's main WebView. This will always return the main
//     * WebView for a given tab and not a subwindow.
//     * @return The current tab's WebView.
//     */
//    public WebView getCurrentWebView() {
//        Tab t = getTab(mCurrentTab);
//        if (t == null) {
//            return null;
//        }
//        return t.getWebView();
//    }
//    /**
//     * return the list of tabs
//     */
//    public List<Tab> getTabs() {
//        return mAdapter.getData();
//    }
//    /**
//     * Return the tab at the specified position.
//     * @return The Tab for the specified position or null if the tab does not
//     *         exist.
//     */
//    public Tab getTab(int position) {
//        if (position >= 0 && position < mAdapter.getItemCount()) {
//            return mAdapter.getItem(position);
//        }
//        return null;
//    }
//    /**
//     * Return the current tab.
//     * @return The current tab.
//     */
//    public Tab getCurrentTab() {
//        return getTab(mCurrentTab);
//    }
//    /**
//     * Return the current tab position.
//     * @return The current tab position
//     */
//    public int getCurrentPosition() {
//        return mCurrentTab;
//    }
//    /**
//     * Given a Tab, find it's position
//     * @return position of Tab or -1 if not found
//     */
////    public int getTabPosition(Tab tab) {
////        if (tab == null) {
////            return -1;
////        }
////        return mTabs.indexOf(tab);
////    }
//
//    public Tab createNewTab() {
//        return createNewTab(null);
//    }
//    public Tab createNewTab(Bundle state) {
//
//        final WebView w = createNewWebView();
//
//        // Create a new tab and add it to the tab list
//        Tab t = new Tab(mController, w, state);
//        mAdapter.addData(t);
////        mTabs.add(t);
//        if (mController != null) {
//            mController.onTabCountChanged();
//        }
//        // Initially put the tab in the background.
//        t.putInBackground();
//        return t;
//    }
//
//    /**
//     * Remove the tab from the list. If the tab is the current tab shown, the
//     * last created tab will be shown.
//     */
//    public boolean removeTab(int pos) {
//        Tab t = mAdapter.getItem(pos);
//        if (t == null) {
//            return false;
//        }
//        // Grab the current tab before modifying the list.
//        Tab current = getCurrentTab();
//
//        // Remove t from our list of tabs.
//        mAdapter.remove(pos);
//        // Remove it from the queue of viewed tabs.
//        mTabQueue.remove(t);
//
//        mLastTab.remove(t);
//        // Put the tab in the background only if it is the current one.
//        if (current == t) {
//            t.putInBackground();
//            setActiveTab(mLastTab.peek());
//            //修改为上一次点击的Tab
////            setCurrentTab(mLastTab.peek());
////            mCurrentTab = getTabPosition(mLastTab.peek());
//        } else {
//            // If a tab that is earlier in the list gets removed, the current
//            // index no longer points to the correct tab.
////            mCurrentTab = getTabPosition(current);
////            Log.e(TAG,"removeTab mCurrentTab =:" + mCurrentTab +",getTabCount() =:" + getTabCount());
////            if(mCurrentTab >= getTabCount()){
////                mCurrentTab --;
////            }
//        }
//
//        // destroy the tab
//        t.destroy();
//
//        if (mController != null) {
//            mController.onTabCountChanged();
//        }
//        return true;
//    }
//
////    /**
////     * Returns the number of tabs created.
////     * @return The number of tabs created.
////     */
////    public int getTabCount() {
////        return mTabs.size();
////    }
//    /**
//     * Creates a new WebView and registers it with the global settings.
//     */
//    private WebView createNewWebView() {
//        return mController.getWebViewFactory().createWebView();
//    }
//
//    /**
//     * Put the current tab in the background and set newTab as the current tab.
//     * @param newTab The new tab. If newTab is null, the current tab is not
//     *               set.
//     */
//    public boolean setCurrentTab(Tab newTab) {
//        return setCurrentTab(newTab, false);
//    }
//
//    /**
//     * If force is true, this method skips the check for newTab == current.
//     */
//    private boolean setCurrentTab(Tab newTab, boolean force) {
//        Tab current = getTab(mCurrentTab);
//        if (current == newTab && !force) {
//            return true;
//        }
//        if (current != null) {
//            current.putInBackground();
//            mCurrentTab = -1;
//        }
//        if (newTab == null) {
//            return false;
//        }
//
//        // Move the newTab to the end of the queue
//        int index = mTabQueue.indexOf(newTab);
//        if (index != -1) {
//            mTabQueue.remove(index);
//        }
//        mTabQueue.add(newTab);
//
//        // Display the new current tab
////        mCurrentTab = mTabs.indexOf(newTab);
//        WebView mainView = newTab.getWebView();
//        boolean needRestore = mainView == null;
//        if (needRestore) {
//            // Same work as in createNewTab() except don't do new Tab()
//            mainView = createNewWebView();
//            newTab.setWebView(mainView);
//        }
//        newTab.putInForeground();
//        return true;
//    }
//    private Stack<Tab> mLastTab = new Stack<>();
//    // Used by Tab.onJsAlert() and friends
//    public void setActiveTab(Tab tab) {
////        mCurrentTab = mTabs.indexOf(tab);
//        WebView webView = getCurrentWebView();
//        if(webView != null && webView.getParent() != null){
//            ((ViewGroup)webView.getParent()).removeView(webView);
//        }
//
//        if(mLastTab.contains(tab)) {
//            mLastTab.remove(tab);
//        }
//        mLastTab.push(tab);
//    }
//}