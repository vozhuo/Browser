//package cf.vozhuo.app.broswser;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.webkit.WebView;
//
//public class WebView extends WebView {
//    private Context mContext;
//    private static String value;
//    public WebView(Context context) {
//        super(context);
//        mContext = context;
//    }
//
//    public WebView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mContext = context;
//    }
//
//    public WebView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        mContext = context;
//    }
//
////    @Override
////    public ActionMode startActionMode(ActionMode.Callback callback) {
////       ActionMode actionMode = super.startActionMode(callback);
////        return resolveMode(actionMode);
////    }
////
////    @Override
////    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
////        ActionMode actionMode = super.startActionMode(callback);
////        return resolveMode(actionMode);
////    }
////    private ActionMode resolveMode(ActionMode actionMode) {
////        if(actionMode != null) {
////            final Menu menu = actionMode.getMenu();
////            menu.clear();
////
////            for (int i = 0; i < title.length; i++) {
////                menu.add(Menu.NONE, i, Menu.NONE, title[i]);
////            }
////            for (int i = 0; i < title.length; i++) {
////                MenuItem item = menu.getItem(i);
////                item.setOnMenuItemClickListener(mListener);
////            }
////            mActionMode = actionMode;
////        }
////        return actionMode;
////    }
////    private static boolean doSearch = false;
////    private MenuItem.OnMenuItemClickListener mListener = new MenuItem.OnMenuItemClickListener() {
////            @Override
////            public boolean onMenuItemClick(MenuItem menuItem) {
////                getSelectedData();
////                switch (menuItem.getItemId()) {
////                    case 0:
////                        Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
////                        break;
////                    case 1:
////                        doSearch = true;
////                        new Handler().postDelayed(new Runnable() {
////                            public void run() {
////                                if(value != null)
////                                    MainActivity.instance.loadFromSelect(SPUtil.getSearchUrl(mContext, value));
////                                }
////                            }, 100);
////                        break;
////                    default: return false;
////                }
////                releaseActionMode();
////                return true;
////            }
////    };
////    private final String title[] = {"复制", "搜索"};
////    private ActionMode mActionMode;
////    private void releaseActionMode() {
////        if (mActionMode != null) {
////            mActionMode.finish();
////            mActionMode = null;
////        }
////    }
//}
