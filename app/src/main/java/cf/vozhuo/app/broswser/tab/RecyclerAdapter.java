package cf.vozhuo.app.broswser.tab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected final Context mContext;
    protected final LayoutInflater mInflater;
    private List<T> mData;
    public RecyclerAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mData = new ArrayList<>();
    }
    public void setData(List<T> data){
        mData.clear();
        if(data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }
    public void updateData(List<T> data){
        setData(data);
    }
    public List<T> getData(){
        return mData;
    }
    public void addData(int index,T t,boolean notify){
        if(index < 0 || index > mData.size()){
            return;
        }
        mData.add(index,t);
        if(notify) notifyDataSetChanged();
    }
    public void addData(T t,boolean notify){
        mData.add(t);
        if(notify) notifyDataSetChanged();
    }
    public void removeData(T t,boolean notify){
        if(mData.contains(t)) {
            mData.remove(t);
            if (notify) notifyDataSetChanged();
        }
    }
    public void removeData(int index,boolean notify){
        if(index < 0 || index > mData.size()){
            return;
        }
        mData.remove(index);
        if(notify){
            notifyDataSetChanged();
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T data = mData.get(position);
        bindView(data,position,holder);
    }

    public abstract void bindView(T data, int position, RecyclerView.ViewHolder holder);

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public T getItem(int position){
        return mData.get(position);
    }
}
