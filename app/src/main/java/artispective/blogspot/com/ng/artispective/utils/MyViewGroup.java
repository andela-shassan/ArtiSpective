package artispective.blogspot.com.ng.artispective.utils;


import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class MyViewGroup extends ViewGroup {

    private int mTouchSlop;
    ViewConfiguration vc;
    private boolean mIsScrolling;

    public MyViewGroup(Context context) {
        super(context);
        vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        final int pointerId = ev.getPointerId(ev.getActionIndex());
        final int action = MotionEventCompat.getActionMasked(ev);

        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsScrolling = false;
            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if (mIsScrolling) {
                    // We're currently scrolling, so yes, intercept the touch event!
                    return true;
                }

                // If the user has dragged her finger horizontally more than
                // the touch slop, start the scroll

                // left as an exercise for the reader
                final int xDiff = calculateDistanceX(ev, pointerId);
                final int yDiff = calculateDistanceY(ev, pointerId);

                // Touch slop should be calculated using ViewConfiguration constants.
                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    // Start scrolling!
                    mIsScrolling = true;
                    return true;
                }
                break;
            }

        }

        // In general, we don't want to intercept touch events. They should be handled by the child view.
        return false;
    }

    private int calculateDistanceY(MotionEvent ev, int pointerId) {
        int historySize = ev.getHistorySize();
        float oldY = ev.getHistoricalY(pointerId, historySize);
        float newY = ev.getX();
        return (int) (newY - oldY);
    }

    private int calculateDistanceX(MotionEvent ev, int pointerId) {
        int historySize = ev.getHistorySize();
        float oldX = ev.getHistoricalX(pointerId, historySize);
        float newX = ev.getX();
        return (int) (newX - oldX);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
        // scroll this container).
        // This method will only be called if the touch event was intercepted in
        // onInterceptTouchEvent
        return false;
    }
}