package tk.vozhuo.browser.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class TabRecyclerView extends RecyclerView {
    public TabRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec(1080, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
