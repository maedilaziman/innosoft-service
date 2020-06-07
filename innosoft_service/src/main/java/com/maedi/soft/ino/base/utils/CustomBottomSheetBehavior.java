package com.maedi.soft.ino.base.utils;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomBottomSheetBehavior <V extends View> extends BottomSheetBehavior<V> {

    private boolean allowDragging = true;

    public void setAllowDragging(boolean allowDragging) {
        this.allowDragging = allowDragging;
    }

    public CustomBottomSheetBehavior() {
        super();
    }

    /**
     * Default constructor for inflating BottomSheetBehaviors from layout.
     */
    public CustomBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!allowDragging) {
            return false;
        }

        return super.onInterceptTouchEvent(parent, child, event);
    }
}
