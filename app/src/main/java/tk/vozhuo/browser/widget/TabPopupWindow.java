package tk.vozhuo.browser.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import tk.vozhuo.browser.R;

public class TabPopupWindow extends PopupWindow {
    public TabPopupWindow(View contentView) {
        super(contentView, ConstraintLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        setOutsideTouchable(true);
        setAnimationStyle(R.style.anim_pop);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void show(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, view.getMeasuredHeight());
    }
}