package cf.vozhuo.app.broswser.search_history;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.search_history.storage.SearchBean;

public class SearchHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<SearchBean> mHistories;
    private ArrayList<SearchBean> Histories;

    public SearchHistoryAdapter(Context context, ArrayList<SearchBean> histories) {
        mContext = context;
        mHistories = histories;
    }

    public void refreshData(ArrayList<SearchBean> histories) {
        mHistories.clear();
        mHistories = histories;
        notifyDataSetChanged();
    }

    public void searchData(ArrayList<SearchBean> histories) {

    }
    @Override
    public int getCount() {
        return mHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return mHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_list_item, null);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.tv_searchhistory);
//            holder.layoutClose = (LinearLayout) convertView.findViewById(R.id.ll_search_close);
            holder.layout = convertView.findViewById(R.id.search_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mHistories.get(position).getContent());

//        holder.layoutClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onSearchHistoryListener != null) {
//                    onSearchHistoryListener.onDelete(mHistories.get(position).getTime());
//                }
//            }
//        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSearchHistoryListener != null) {
                    onSearchHistoryListener.onSelect(mHistories.get(position).getContent());
                }
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        private LinearLayout layout;
        public TextView textView;
//        public LinearLayout layoutClose;
    }

    public void setOnSearchHistoryListener(OnSearchHistoryListener onSearchHistoryListener) {
        this.onSearchHistoryListener = onSearchHistoryListener;
    }

    private OnSearchHistoryListener onSearchHistoryListener;
}
