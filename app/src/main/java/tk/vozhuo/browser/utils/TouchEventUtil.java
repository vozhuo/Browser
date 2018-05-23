package tk.vozhuo.browser.utils;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class TouchEventUtil {
    public static boolean isShouldExit(View v, MotionEvent event) {
        if (v != null && (v instanceof ImageButton)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }
}