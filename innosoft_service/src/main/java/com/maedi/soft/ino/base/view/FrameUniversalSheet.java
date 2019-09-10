/*
 * Copyright (c) 8/28/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.maedi.soft.ino.R;

public class FrameUniversalSheet extends FrameLayout {

    private Context context;
    private Paint paint;

    public FrameUniversalSheet(Context context) {
        super(context);
        this.context = context;
        initialize();
    }

    public FrameUniversalSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
    }

    public FrameUniversalSheet(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initialize();
    }

    private void initialize() {
        setWillNotDraw(false);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.em_black_54));
        RectF rect = new RectF(0, 0, w, h);
        canvas.drawRect(rect, paint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return false so that the event will be passed to child views.
        return false;
    }
}