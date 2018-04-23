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
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cf.vozhuo.app.broswser.favorites.FavHisActivity;
import cf.vozhuo.app.broswser.favorites.FavHisDao;

import static android.content.ContentValues.TAG;

public class BottomDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TABLE = "favorites";
    private boolean flag;
    private ImageView imageView;
    private CheckBox cb_image;
    private CheckBox collect;
    private CheckBox cb_track;
    private CheckBox cb_dark;
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
        view.findViewById(R.id.mark).setOnClickListener(this);
        view.findViewById(R.id.collect).setOnClickListener(this);
        view.findViewById(R.id.track).setOnClickListener(this);
        view.findViewById(R.id.refresh).setOnClickListener(this);
        view.findViewById(R.id.dark).setOnClickListener(this);

        //检测智能无图模式是否开启
        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        Boolean state = sp.getBoolean("image_state", false);
        cb_image = view.findViewById(R.id.setImage);
        cb_image.setChecked(state);

        //检测无痕模式是否开启
        sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        state = sp.getBoolean("track_state", false);
        cb_track = view.findViewById(R.id.track);
        cb_track.setChecked(state);

        //检测是否已收藏
        favHisDao = new FavHisDao(getContext(), TABLE);
        collect = view.findViewById(R.id.collect);
        collect.setChecked(favHisDao.queryURL(((MainActivity)getActivity()).getPageUrl()));

        //检测夜间模式是否开启
        sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        state = sp.getBoolean("dark_state", false);
        cb_dark = view.findViewById(R.id.dark);
        cb_dark.setChecked(state);
    }

    private FavHisDao favHisDao;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.refresh:
                ((MainActivity)getActivity()).refreshPage();
                getFragmentManager().beginTransaction().remove(BottomDialogFragment.this).commit();
                break;
            case R.id.mark:
                startActivity(new Intent(getActivity(), FavHisActivity.class));
                getFragmentManager().beginTransaction().remove(BottomDialogFragment.this).commit();
                break;
            case R.id.collect:
                collectClick();
                break;
            case R.id.setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                getFragmentManager().beginTransaction().remove(BottomDialogFragment.this).commit();
                break;
            case R.id.setImage:
                setImageClick();
            case R.id.track:
                trackClick();
            case R.id.dark:
                darkClick();
                break;
        }
    }

    private void darkClick() {
        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(cb_dark.isChecked()) { //进入夜间模式
            ((MainActivity)getActivity()).setNoTrack();
            editor.putBoolean("dark_state", true);
            editor.apply();

        } else {
            editor.putBoolean("dark_state", false);
            editor.apply();
        }
    }

    private void trackClick() {
        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(cb_track.isChecked()) { //进入无痕浏览
            ((MainActivity)getActivity()).setNoTrack();
            editor.putBoolean("track_state", true);
            editor.apply();

        } else {
            editor.putBoolean("track_state", false);
            editor.apply();
        }
    }

    private void setImageClick() {
        SharedPreferences sp = getActivity().getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        NetworkInfo.State wifiState = null;
        NetworkInfo.State mobileState = null;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context
                .CONNECTIVITY_SERVICE);
        wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//
//        Log.e("TAG","wifi状态:" + wifiState + "\n mobile状态:" + mobileState);
        if(cb_image.isChecked()) { //进入智能无图模式
            if(!(wifiState == NetworkInfo.State.CONNECTED)) {
                ((MainActivity)getActivity()).setNoImage(true);
            }
            editor.putBoolean("image_state", true);
            editor.apply();

        } else {
            editor.putBoolean("image_state", false);
            editor.apply();
            Log.e(TAG, "ONNNNNNNNNN: ");
        }
    }
    private void collectClick() {
        if(collect.isChecked()) { //收藏
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.CHINA);
            String currentTime = format.format(new Date());
            favHisDao.insert(null, ((MainActivity)getActivity()).getPageTitle(),
                    ((MainActivity)getActivity()).getPageUrl(), currentTime);
        } else { //取消收藏
            favHisDao.delete(((MainActivity)getActivity()).getPageUrl());
        }
    }
}