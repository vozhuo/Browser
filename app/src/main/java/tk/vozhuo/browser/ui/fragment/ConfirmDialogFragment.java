package tk.vozhuo.browser.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import tk.vozhuo.browser.ui.activity.MainActivity;
import tk.vozhuo.browser.R;
import tk.vozhuo.browser.databinding.FragmentConfirmBinding;
import tk.vozhuo.browser.ui.activity.DownloadActivity;

public class ConfirmDialogFragment extends DialogFragment {

    private static final String TAG = "ConfirmDialogFragment";
    private EditText fileName;
    private TextView fileSize;
    private Bundle bundle;
    private CheckBox cb;
    public static FragmentConfirmBinding binding;
    private static final String DOWNLOAD = "download";
    private static final String CLEAR = "download_clear";
    private static final String OPEN = "open_app";
    private static final String ALERT = "alert";
    private static final String CONFIRM = "confirm";
    private static final String PROMPT = "prompt";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_confirm, container, false);
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout ll_content = binding.llContent;
        TextView tv_confirm_title = binding.tvConfirmTitle;
        binding.setHandlers(this);

        bundle = getArguments();
        String content = bundle.getString("Confirm");
        if (content != null) {
            switch (content) {
                case DOWNLOAD:
                    tv_confirm_title.setText("下载");

                    fileName = new EditText(getContext());
                    fileName.setText(bundle.getString("fileName"));
                    fileName.setBackgroundResource(R.drawable.shape_textarea);

                    fileSize = new TextView(getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.CENTER;
                    fileSize.setLayoutParams(lp);
                    fileSize.setText(bundle.getString("fileSize"));
                    fileSize.setTextSize(12);

                    ll_content.addView(fileName);
                    ll_content.addView(fileSize);
                    break;
                case CLEAR:
                    tv_confirm_title.setText("删除");
                    TextView tv_del = new TextView(getContext());

                    int count = bundle.getInt("count");
                    if(count == 0) tv_del.setText("删除所有项目？");
                    else tv_del.setText(String.format("删除%o个项目？", count));

                    tv_del.setTextColor(getResources().getColor(R.color.textColor));

                    cb = new CheckBox(getContext());
                    cb.setText("包括源文件");
                    cb.setTextSize(12);

                    ll_content.addView(tv_del);
                    ll_content.addView(cb);
                    break;
                case OPEN:
                    tv_confirm_title.setText("打开");
                    TextView tv_open = new TextView(getContext());
                    tv_open.setText("是否允许打开外部应用？");
                    tv_open.setTextColor(getResources().getColor(R.color.textColor));
                    ll_content.addView(tv_open);
                    break;
                case ALERT:
                    tv_confirm_title.setText("来自网页的提示信息");
                    TextView tv_message = new TextView(getContext());
                    tv_message.setText(bundle.getString("message"));
                    ll_content.addView(tv_message);
                    break;
                case CONFIRM:
                    tv_confirm_title.setText("来自网页的确认信息");
                    TextView tv_confirm = new TextView(getContext());
                    tv_confirm.setText(bundle.getString("message"));
                    ll_content.addView(tv_confirm);
                    break;
                case PROMPT:
                    tv_confirm_title.setText("来自网页的输入信息");
                    TextView tv_prompt = new TextView(getContext());
                    editText = new EditText(getContext());

                    tv_prompt.setText(bundle.getString("message"));
                    editText.setText(bundle.getString("defaultValue"));

                    ll_content.addView(tv_prompt);
                    ll_content.addView(editText);
                    break;
                    default: break;
            }
        }
    }
    private EditText editText;
    private JsResult result = MainActivity.instance.getJsResult();
    private JsPromptResult promptResult = MainActivity.instance.getJsPromptResult();

    public void onClick(View view) {
        String content = getArguments().getString("Confirm");
        if(view.getId() == R.id.bt_confirm) {
            switch (content) {
                case DOWNLOAD:
                    ((MainActivity) getContext()).doDownload(fileName.getText().toString(), bundle.getString("url"));
                    break;
                case CLEAR:
                    ((DownloadActivity) getContext()).deleteDownload(cb.isChecked());
                    break;
                case OPEN:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bundle.getString("eventUrl"))));
                    break;
                case ALERT:
                case CONFIRM:
                    result.confirm();
                    break;
                case PROMPT:
                    promptResult.confirm(editText.getText().toString());
                    break;
            }
            dismiss();
        } else if(view.getId() == R.id.bt_back) {
            switch (content) {
                case CONFIRM:
                    result.cancel();
                    break;
                case PROMPT:
                    promptResult.cancel();
                    break;
            }
            dismiss();
        }
    }
}
