package artispective.blogspot.com.ng.artispective.utils;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * Created by Nobest on 10/06/2016.
 */
public class DetailView extends LinearLayout {

    private boolean isScrolling;
    private int touchSlop;

    public DetailView(Context context) {
        super(context);
    }

    public DetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP ){
            isScrolling = false;
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (isScrolling) {
                    return true;
                }

                final int xDiff = calculateHorizontalDiff(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    private int calculateHorizontalDiff(MotionEvent ev) {
        int diff = ev.getHistorySize();
        return 0;
    }
}
