package cf.vozhuo.app.broswser.favorites;


import android.app.Dialog;
import android.databinding.DataBindingUtil;
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

import java.io.Serializable;

import cf.vozhuo.app.broswser.R;
import cf.vozhuo.app.broswser.databinding.FragmentFavModifyBinding;

public class ModifyFragment extends DialogFragment {
    private FavHisEntity favorites;
    private FragmentFavModifyBinding binding;

    public void onClick(View view) {
        favorites.setTitle(binding.etTitle.getText().toString());
        favorites.setUrl(binding.etUrl.getText().toString());
        Bundle bundle = getArguments();
        ((FavoriteFragment)getParentFragment()).updateFavorite(favorites, bundle.getInt("position")); //调用FavoriteFragment的方法
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Serializable serializable = bundle.getSerializable("favorites");
            favorites = (FavHisEntity) serializable;
        }
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_fav_modify, container, false);
        binding.setHandlers(this);
        return binding.getRoot();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.etTitle.setText(favorites.getTitle());
        binding.etUrl.setText(favorites.getUrl());
    }
}
