package cf.vozhuo.app.broswser;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cf.vozhuo.app.broswser.download.DownloadActivity;
import cf.vozhuo.app.broswser.favorites.HistoryFragment;
import cf.vozhuo.app.broswser.search_history.SearchActivity;

import static android.content.ContentValues.TAG;

public class NoticeDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView tv_notice;
    private LinearLayout notice_content;
    MainActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notice, null);
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tv_notice = view.findViewById(R.id.tv_notice);
        notice_content = view.findViewById(R.id.notice_content);
        final Bundle bundle = getArguments();
        String []engine = new String[]{"百度", "谷歌", "必应", "搜狗"};
        String []ua = new String[]{"Android", "PC", "iPhone"};
        String []clear = new String[]{"搜索记录", "帐号密码", "Cookies", "历史记录", "缓存文件"};
        if (bundle != null) {
            if(bundle.getString("search_engine") != null) {
                notice_content.setOrientation(LinearLayout.HORIZONTAL);
                tv_notice.setText("选择搜索引擎");
                tv_notice.setTextColor(Color.BLACK);

                SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
                String search_engine = sp.getString("search_engine", "百度");

                RadioGroup radioGroup = new RadioGroup(getContext());
                RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
                notice_content.addView(radioGroup, lp);

                radioGroup.setOrientation(RadioGroup.HORIZONTAL);
                radioGroup.setGravity(Gravity.CENTER);
                for (int i = 0; i < 4; i++) {
                    RadioButton btn = new RadioButton(getContext());
                    btn.setId(i);
                    btn.setTextColor(Color.BLACK);
                    btn.setText(engine[i]);
                    if(search_engine.equals(engine[i])) btn.setChecked(true);
                    radioGroup.addView(btn);
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton btn = group.findViewById(checkedId);
                        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("search_engine", btn.getText().toString());
                        editor.apply();
                        dismiss();
                    }
                });

            } else if (bundle.getString("ua") != null) {
                notice_content.setOrientation(LinearLayout.HORIZONTAL);
                tv_notice.setText("选择UA");
                tv_notice.setTextColor(Color.BLACK);

                SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
                String user_engine = sp.getString("ua", "Android");

                RadioGroup radioGroup = new RadioGroup(getContext());
                RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
                notice_content.addView(radioGroup, lp);

                radioGroup.setOrientation(RadioGroup.HORIZONTAL);
                radioGroup.setGravity(Gravity.CENTER);
                for (int i = 0; i < 3; i++) {
                    RadioButton btn = new RadioButton(getContext());
                    btn.setId(i);
                    btn.setTextColor(Color.BLACK);
                    btn.setText(ua[i]);
                    if(user_engine.equals(ua[i])) btn.setChecked(true);
                    radioGroup.addView(btn);
                }
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton btn = group.findViewById(checkedId);
                        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("ua", btn.getText().toString());
                        editor.apply();
                        dismiss();
                    }
                });
            } else if (bundle.getString("clear") != null) {
                notice_content.setOrientation(LinearLayout.VERTICAL);
                tv_notice.setText("清除缓存");
                tv_notice.setTextColor(Color.BLACK);

                for (int i = 0; i < 5; i++) {
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setId(i);
                    checkBox.setText(clear[i]);
                    checkBox.setOnCheckedChangeListener(listener);
                    if(i == 0 || i == 3 || i == 4) checkBox.setChecked(true);
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
                        if(isCheck[0]) {
                            SearchActivity.ClearSearch();
                        } else if(isCheck[1]) {

                        } else if(isCheck[2]) {
                            CookieSyncManager.createInstance(getContext());
                            CookieSyncManager.getInstance().startSync();
                            CookieManager.getInstance().removeSessionCookie();
                        } else if(isCheck[3]) {
                            HistoryFragment.deleteHistory();
                        } else if(isCheck[4]) {
                            MainActivity.ClearCache();
                        }
                        dismiss();
                        Toast.makeText(getContext(), "清理成功",  Toast.LENGTH_SHORT).show();
                    }
                });

            } else if(bundle.getString("download") != null) { //下载Fragment
                notice_content.setOrientation(LinearLayout.VERTICAL);
                tv_notice.setText("下载");
                tv_notice.setTextColor(Color.BLACK);
                final EditText fileName = new EditText(getContext());
                TextView fileSize = new TextView(getContext());
                Button back = new Button(getContext());
                Button confirm = new Button(getContext());

                back.setText("返回");
                back.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                back.setTextSize(14);
                confirm.setText("确认");
                confirm.setTextSize(14);
                confirm.setTextColor(Color.BLUE);
                confirm.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                RelativeLayout layout = new RelativeLayout(getContext());
                RelativeLayout.LayoutParams lp_left
                        = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams lp_right
                        = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp_left.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lp_right.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layout.addView(back, lp_left);
                layout.addView(confirm, lp_right);

                fileName.setText(bundle.getString("fileName"));
                fileName.setBackgroundResource(R.drawable.shape_textarea);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                fileSize.setLayoutParams(lp);
                fileSize.setText(bundle.getString("fileSize"));
                fileSize.setTextSize(12);

                notice_content.addView(fileName);
                notice_content.addView(fileSize);
                notice_content.addView(layout);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
                final String url = bundle.getString("url");
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       MainActivity.instance.doDownload(fileName.getText().toString(), url);
                       dismiss();
                    }
                });
            }
        }
    }
    private boolean []isCheck = new boolean[5];
    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            isCheck[buttonView.getId()] = isChecked;
        }
    };
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            default:
                break;
        }
    }
}
