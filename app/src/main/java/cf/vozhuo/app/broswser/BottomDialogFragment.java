package cf.vozhuo.app.broswser;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

public class BottomDialogFragment extends DialogFragment implements View.OnClickListener {

    private boolean flag;
    private ImageView imageView;
    private CheckBox checkBox;
    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_option, null); //载入fragment_option
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
        view.findViewById(R.id.setting).setOnClickListener(this);
        view.findViewById(R.id.setImage).setOnClickListener(this);
        view.findViewById(R.id.ie3).setOnClickListener(this);
        view.findViewById(R.id.ie4).setOnClickListener(this);
        view.findViewById(R.id.ie5).setOnClickListener(this);
        view.findViewById(R.id.ie6).setOnClickListener(this);

        webView = getActivity().findViewById(R.id.web_holder);
//        init(view);
        SharedPreferences sp = getActivity().getSharedPreferences("image_config", Context.MODE_PRIVATE);
        Boolean state = sp.getBoolean("image_state", false);
        checkBox = view.findViewById(R.id.setImage);
        checkBox.setChecked(state);
    }

    @Override
    public void onClick(View v) {
        v.setSelected(!v.isSelected());
        int id = v.getId();
        switch (id) {
            case R.id.ie3:
            case R.id.ie4:
            case R.id.ie5:
            case R.id.ie6:
                Toast.makeText(getActivity(), "IE", LENGTH_SHORT).show();
                break;
            case R.id.setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                getFragmentManager().beginTransaction().remove(BottomDialogFragment.this).commit();
                break;
            case R.id.setImage:
                WebSettings settings = webView.getSettings();
//                settings.setJavaScriptEnabled(true);

                NetworkInfo.State wifiState = null;
                NetworkInfo.State mobileState = null;

                ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context
                        .CONNECTIVITY_SERVICE);
                wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

                Log.e("TAG","wifi状态:" + wifiState + "\n mobile状态:" + mobileState);

                SharedPreferences sp = getActivity().getSharedPreferences("image_config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                checkBox = v.findViewById(R.id.setImage);
                if(checkBox.isChecked()) { //进入智能无图模式
                    if(wifiState == NetworkInfo.State.CONNECTED) {
                        settings.setLoadsImagesAutomatically(true);
                    } else {
                        Log.e(TAG, "YESSSSSSSSS: ");
                        settings.setLoadsImagesAutomatically(false);
                    }
                    editor.putBoolean("image_state", true);
                    editor.apply();

                } else {
                    settings.setLoadsImagesAutomatically(true);
                    editor.putBoolean("image_state", false);
                    editor.apply();
                    Log.e(TAG, "ONNNNNNNNNN: ");
                }
//                if(v.getContentDescription().equals("CLOSE")) {
//                    v.setContentDescription("OPEN");
//                    Log.e(TAG, "onClick111: " + v.getContentDescription());
//                } else if(v.getContentDescription().equals("OPEN")) {
//                    v.setContentDescription("CLOSE");
//                    Log.e(TAG, "onClick222: " + v.getContentDescription());
//                }
        }
    }


//    protected void save(Boolean flag, View view) {
//
//
//        if(!flag) { //智能无图关闭->开启
//
//            Log.e(TAG, "init: YESSSSSSSSSSSSSS");
//           // settings.setLoadsImagesAutomatically(false);
//
//            imageButton = view.findViewById(R.id.setImage);
//            imageButton.setImageResource(R.drawable.image_show);
//        } else { //智能无图开启->关闭
//
//            Log.e(TAG, "init: NOOOOOOOOOOOOOOO");
//           // settings.setLoadsImagesAutomatically(true);
//
//            imageButton = view.findViewById(R.id.setImage);
//            imageButton.setImageResource(R.drawable.image_disable);
//        }
//    }
}