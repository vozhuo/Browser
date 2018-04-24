package cf.vozhuo.app.broswser;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cf.vozhuo.app.broswser.tab.RecyclerAdapter;

import static android.content.ContentValues.TAG;

public class SettingAdapter extends RecyclerAdapter<String> {

    private static final int ITEM_TYPE_SWITCH = 0;
    private static final int ITEM_TYPE_TEXT = 1;
    private List<String> mTitles;
    private SettingController mController;
    public SettingAdapter(Context context, SettingController controller) {
        super(context);
        mTitles = new ArrayList<>();
        mController = controller;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return ITEM_TYPE_SWITCH;
        } else return ITEM_TYPE_TEXT;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void bindView(String data, int position, RecyclerView.ViewHolder holder) {
        if(holder instanceof SwitchHolder) {
            ((SwitchHolder) holder).bind(data, position);
        } else if(holder instanceof TextHolder) {
            ((TextHolder) holder).bind(data, position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE_SWITCH) {
            return new SwitchHolder(mInflater.inflate(R.layout.menu_switch_item,
                    parent, false));
        } else {
            return new TextHolder(mInflater.inflate(R.layout.layout_menu_item,
                    parent, false));
        }

    }
    class SwitchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.switch_default)
        SwitchCompat switch_default;

        String data;
        int position;
        SwitchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.switch_default:
                    mController.clearDefaultAndSet(switch_default.isChecked());
                    break;
            }
        }

        public void bind(String data, int position) {
            switch_default.setText(data);
            switch_default.setChecked(mController.isDefaultBrowser());
            switch_default.setOnClickListener(this);
            this.data = data;
            this.position = position;
        }
    }

    class TextHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_menu_title)
        TextView title;
        @BindView(R.id.tv_menu_content)
        TextView content;
        @BindView(R.id.iv_menu_forward)
        ImageView forward;
        @BindView(R.id.menu_item)
        ConstraintLayout menu_item;
        String data;
        int position;
        TextHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_item:
                    mController.showFragment(position);
                    break;
                    default:break;
            }
        }

        public void bind(String data, int position) {
            title.setText(data);
            title.setTextColor(Color.BLACK);
            menu_item.setOnClickListener(this);

            SharedPreferences sp = mContext.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);

            if(position == 1) {
                content.setText(sp.getString("search_engine", "百度"));
                sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        switch (key) {
                        case "search_engine":
                            content.setText(sharedPreferences.getString(key, "百度"));
                            break;
                        default: break;
                        }
                    }
                });
            }
            else if (position == 2) {
                content.setText(sp.getString("ua", "Android"));
                sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        switch (key) {
                            case "ua":
                                content.setText(sharedPreferences.getString(key, "Android"));
                                break;
                            default: break;
                        }
                    }
                });
            }


            this.data = data;
            this.position = position;
        }
    }
}
