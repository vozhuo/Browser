package cf.vozhuo.app.broswser.favorites;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cf.vozhuo.app.broswser.BottomDialogFragment;
import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.search_history.view.MySearchView;
import cf.vozhuo.app.broswser.tab.Tab;

public class ModifyFragment extends DialogFragment {
    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_url)
    EditText et_url;
    @BindView(R.id.bt_confirm)
    Button bt_confirm;

    private FavoritesEntity favorites;

    private ModifyListener mListener;
    public interface ModifyListener {
       void sendEntity(FavoritesEntity favorites);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ModifyListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Serializable serializable = bundle.getSerializable("favorites");
            favorites = (FavoritesEntity) serializable;
        }
        return inflater.inflate(R.layout.modify_favorite, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            window.setWindowAnimations(R.style.animate_dialog);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @OnClick(R.id.bt_confirm)
    void updateFavorite() {
        favorites.setTitle(et_title.getText().toString());
        favorites.setUrl(et_url.getText().toString());
        mListener.sendEntity(favorites); //数据传送入FavoriteActivity
        getFragmentManager().beginTransaction().remove(ModifyFragment.this).commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        et_title.setText(favorites.getTitle());
        et_url.setText(favorites.getUrl());
    }
}
