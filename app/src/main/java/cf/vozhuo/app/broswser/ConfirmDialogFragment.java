package cf.vozhuo.app.broswser;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import cf.vozhuo.app.broswser.databinding.FragmentConfirmBinding;
import cf.vozhuo.app.broswser.download.DownloadActivity;

public class ConfirmDialogFragment extends DialogFragment {

    private EditText fileName;
    private TextView fileSize;
    private Bundle bundle;
    private CheckBox cb;
    private FragmentConfirmBinding binding;
    private static final String DOWNLOAD = "download";
    private static final String CLEAR = "download_clear";
    private static final String OPEN = "open_app";
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
//        ButterKnife.bind(this, view);
        Button bt_confirm = binding.btConfirm;
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

//                    final String url = bundle.getString("url");
//
//                    bt_confirm.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                                    .getAbsolutePath() + File.separator + fileName;
//                                MainActivity.instance.doDownload(fileName.getText().toString(), url);
//                                dismiss();
//                        }
//                    });
                    break;
                case CLEAR:
                    tv_confirm_title.setText("删除");
                    TextView tv_del = new TextView(getContext());

                    int count = bundle.getInt("count");
                    if(count == 0) tv_del.setText("删除所有项目？");
                    else tv_del.setText(String.format("删除%o个项目？", count));

                    tv_del.setTextColor(Color.BLACK);

                    cb = new CheckBox(getContext());
                    cb.setText("包括源文件");
                    cb.setTextSize(12);

                    ll_content.addView(tv_del);
                    ll_content.addView(cb);

//                    bt_confirm.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            ((DownloadActivity)getActivity()).deleteDownload(cb.isChecked());
//                            dismiss();
//                        }
//                    });
                    break;
                case OPEN:
                    tv_confirm_title.setText("打开");
                    TextView tv_open = new TextView(getContext());
                    tv_open.setText("是否允许打开外部应用？");
                    tv_open.setTextColor(Color.BLACK);
                    ll_content.addView(tv_open);

//                    final String event = ;
//                    bt_confirm.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    });
                    break;
                    default: break;
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                dismiss();
                break;
            case R.id.bt_confirm:
                String content = getArguments().getString("Confirm");
                if(content.equals(DOWNLOAD)) {
                    ((MainActivity)getContext()).doDownload(fileName.getText().toString(), bundle.getString("url"));
                } else if(content.equals(CLEAR)) {
                    ((DownloadActivity)getContext()).deleteDownload(cb.isChecked());
                } else if(content.equals(OPEN)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bundle.getString("eventUrl"))));
                }
                dismiss();
                break;
                default: break;
        }
    }
}
