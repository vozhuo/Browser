package tk.vozhuo.browser.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import tk.vozhuo.browser.ui.activity.MainActivity;
import tk.vozhuo.browser.R;
import tk.vozhuo.browser.databinding.FragmentNoticeBinding;
import tk.vozhuo.browser.ui.activity.DownloadActivity;
import tk.vozhuo.browser.ui.activity.SearchActivity;
import tk.vozhuo.browser.ui.activity.SettingActivity;

public class NoticeDialogFragment extends DialogFragment {

    private FragmentNoticeBinding binding;
    private SparseBooleanArray sba = new SparseBooleanArray(5);
    private final static String []engine = new String[]{"百度", "谷歌", "必应", "搜狗"};
    private final static String []ua = new String[]{"Android", "PC", "iPhone"};
    private final static String []clear = new String[]{"搜索记录", "Cookies", "历史记录", "缓存文件", "下载记录"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_notice, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.TransDialog); //无边框
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setWindowAnimations(R.style.animate_dialog);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        sp.registerOnSharedPreferenceChangeListener(mListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            ((SettingActivity)getActivity()).notify(key);
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        sp.unregisterOnSharedPreferenceChangeListener(mListener);
    }

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tv_notice = binding.tvNotice;
        LinearLayout notice_content = binding.noticeContent;

        sp = getContext().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        editor = sp.edit();

        final Bundle bundle = getArguments();
        if (bundle != null) {
            if(bundle.getString("search_engine") != null) {
                tv_notice.setText("选择搜索引擎");
                String search_engine = sp.getString("search_engine", "百度");

                RadioGroup radioGroup = new RadioGroup(getContext());
                RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
                notice_content.addView(radioGroup, lp);

                radioGroup.setOrientation(RadioGroup.HORIZONTAL);
                radioGroup.setGravity(Gravity.CENTER);
                for (int i = 0; i < 4; i++) {
                    AppCompatRadioButton btn = new AppCompatRadioButton(getContext());
                    btn.setId(i);

//                    btn.setTextColor(android.R.attr.textColorPrimary);
                    btn.setText(engine[i]);
                    if(search_engine.equals(engine[i])) btn.setChecked(true);
                    radioGroup.addView(btn);
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        AppCompatRadioButton btn = group.findViewById(checkedId);
                        editor.putString("search_engine", btn.getText().toString());
                        editor.apply();
                        dismiss();
                    }
                });

            } else if (bundle.getString("ua") != null) {
                tv_notice.setText("选择UA");

                String user_engine = sp.getString("ua", "Android");

                RadioGroup radioGroup = new RadioGroup(getContext());
                RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
                notice_content.addView(radioGroup, lp);

                radioGroup.setOrientation(RadioGroup.HORIZONTAL);
                radioGroup.setGravity(Gravity.CENTER);
                for (int i = 0; i < 3; i++) {
                    AppCompatRadioButton btn = new AppCompatRadioButton(getContext());
                    btn.setId(i);
                    btn.setText(ua[i]);
                    if(user_engine.equals(ua[i])) btn.setChecked(true);
                    radioGroup.addView(btn);
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        AppCompatRadioButton btn = group.findViewById(checkedId);
                        editor.putString("ua", btn.getText().toString());
                        editor.apply();
                        dismiss();
                    }
                });
            } else if (bundle.getString("clear") != null) {
                notice_content.setOrientation(LinearLayout.VERTICAL);
                tv_notice.setText("清理记录");

                for (int i = 0; i < 5; i++) {
                    AppCompatCheckBox checkBox = new AppCompatCheckBox(getContext());
                    checkBox.setId(i);
                    checkBox.setText(clear[i]);
                    checkBox.setOnCheckedChangeListener(listener);
                    if(i == 0 || i == 2) checkBox.setChecked(true);
                    notice_content.addView(checkBox);
                }
                Button submit = new Button(getContext());
                submit.setText("确认");
                submit.setTextSize(16);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity=Gravity.CENTER_HORIZONTAL;
                submit.setBackgroundResource(R.drawable.shape_button);
                notice_content.addView(submit, lp);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(sba.get(0)) {
                            SearchActivity.ClearSearch();
                        } else if(sba.get(1)) {
                            CookieSyncManager.createInstance(getContext());
                            CookieSyncManager.getInstance().startSync();
                            CookieManager.getInstance().removeSessionCookie();
                        } else if(sba.get(2)) {
                            HistoryFragment.deleteHistory();
                        } else if(sba.get(3)) {
                            MainActivity.ClearCache();
                        } else if(sba.get(4)) { //清除下载记录
                            DownloadActivity.instance.removeRecord();
                        }
                        dismiss();
                        Toast.makeText(getContext(), "清理成功",  Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }
    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sba.put(buttonView.getId(), isChecked);
        }
    };
}
