package cf.vozhuo.app.broswser;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class NoticeDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView tv_notice;
    private LinearLayout notice_content;
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
//            dialog.setTitle("搜索引擎");
            window.setWindowAnimations(R.style.animate_dialog);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    enum Search_engine {
        百度,谷歌,必应,搜狗
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tv_notice = view.findViewById(R.id.tv_notice);
        notice_content = view.findViewById(R.id.notice_content);
        Bundle bundle = getArguments();
        String []engine = new String[]{"百度", "谷歌", "必应", "搜狗"};
        String []ua = new String[]{"Android", "PC", "iPhone"};
        if (bundle != null) {
            if(bundle.getString("search_engine") != null) {
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
                        getFragmentManager().beginTransaction().remove(NoticeDialogFragment.this).commit();
                    }
                });

            } else if (bundle.getString("ua") != null) {
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
                        getFragmentManager().beginTransaction().remove(NoticeDialogFragment.this).commit();
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            default:
                break;
        }
    }
}
