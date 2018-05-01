package cf.vozhuo.app.broswser;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cf.vozhuo.app.broswser.download.DownloadActivity;
import cf.vozhuo.app.broswser.download.DownloadUtil;

public class ConfirmDialogFragment extends DialogFragment {

    @BindView(R.id.bt_back)
    Button bt_back;
    @BindView(R.id.bt_confirm)
    Button bt_confirm;
    @BindView(R.id.ll_content)
    LinearLayout ll_content;
    @BindView(R.id.tv_confirm_title)
    TextView tv_confirm_title;

    @OnClick(R.id.bt_back) void clickBack() {
        dismiss();
    }

//    @OnClick(R.id.bt_confirm) void clickConfirm(EditText fileName, String url) {
//
//        String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                .getAbsolutePath() + File.separator + fileName;
//        if(DownloadUtil.isFileExists(destPath)) {
//            warning.setVisibility(View.VISIBLE);
//        } else {
//            MainActivity.instance.doDownload(fileName.getText().toString(), url);
//            dismiss();
//        }
//    }

    private EditText fileName;
    private TextView warning;
    private TextView fileSize;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm, null);
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
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if(bundle.getString("download") != null) {
                tv_confirm_title.setText("下载");

                warning = new TextView(getContext());
                warning.setText("已存在同名文件");
                warning.setTextSize(12);
                warning.setTextColor(Color.RED);
                warning.setVisibility(View.GONE);

                fileName = new EditText(getContext());
                fileName.setText(bundle.getString("fileName"));
                fileName.setBackgroundResource(R.drawable.shape_textarea);

                fileSize = new TextView(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                fileSize.setLayoutParams(lp);
                fileSize.setText(bundle.getString("fileSize"));
                fileSize.setTextSize(12);

                ll_content.addView(warning);
                ll_content.addView(fileName);
                ll_content.addView(fileSize);

                final String url = bundle.getString("url");

                bt_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                .getAbsolutePath() + File.separator + fileName;
                        if(DownloadUtil.isFileExists(destPath)) {
                            warning.setVisibility(View.VISIBLE);
                        } else {
                            MainActivity.instance.doDownload(fileName.getText().toString(), url);
                            dismiss();
                        }
                    }
                });
            } else if (bundle.getString("download_clear") != null) {
                tv_confirm_title.setText("删除");
                TextView tv = new TextView(getContext());

                int count = bundle.getInt("count");
                if(count == 0) tv.setText("删除所有项目？");
                else tv.setText(String.format("删除%o个项目？", count));

                tv.setTextColor(Color.BLACK);

                final CheckBox cb = new CheckBox(getContext());
                cb.setText("包括源文件");
                cb.setTextSize(12);

                ll_content.addView(tv);
                ll_content.addView(cb);

                bt_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((DownloadActivity)getActivity()).deleteDownload(cb.isChecked());
                        dismiss();
                    }
                });
            }
        }
    }
}
