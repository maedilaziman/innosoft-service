/*
 * Copyright (c) 8/29/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public abstract class Sheet<T> extends LinearLayout {

    private Context context;
    private Paint paint;
    private Sheet sheet;

    protected abstract T consumer();

    protected abstract void observed();

    protected abstract void parentAction();

    protected abstract void feelTouch();

    protected interface CommSheetListener
    {
        int setBackgroundColorWindow();

        int setWidth();

        int setHeight();

        float setRadius();

        boolean setTopLeft();

        boolean setTopRight();

        boolean setBottomRight();

        boolean setBottomLeft();
    }

    private CommSheetListener listener;

    protected void setListener(CommSheetListener listener)
    {
        this.listener = listener;
    }

    protected Sheet(Context context) {
        super(context);
        this.context = context;
        init();
    }

    protected Sheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    protected Sheet(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init()
    {
        sheet = (Sheet) consumer();
        sheet.setWillNotDraw(false);
        paint = new Paint();
        paint.setAntiAlias(true);
        observ();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(listener.setBackgroundColorWindow());
        Path path = setWindow(listener.setWidth(), listener.setHeight(), listener.setRadius(), listener.setTopLeft(), listener.setTopRight(), listener.setBottomRight(), listener.setBottomLeft());
        canvas.drawPath(path, paint);
    }

    private ViewTreeObserver observ()
    {
        ViewTreeObserver obs = sheet.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (sheet.getMeasuredWidth() > 0)
                {
                    sheet.getViewTreeObserver().removeOnPreDrawListener(this);
                    inFrameUniversalSheet(sheet);
                    observed();
                    parentAction();
                    feelTouch();
                }
                return true;
            }
        });
        return obs;
    }

    private void inFrameUniversalSheet(View view)
    {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof FrameUniversalSheet.LayoutParams))
        {
            throw new IllegalArgumentException("The view is not a child of FrameUniversalSheet");
        }
    }

    private Path setWindow(int width, int height, float radius, boolean topLeft, boolean topRight,
                           boolean bottomRight, boolean bottomLeft)
    {

        final Path path = new Path();
        final float[] radii = new float[8];

        if (topLeft)
        {
            radii[0] = radius;
            radii[1] = radius;
        }

        if (topRight)
        {
            radii[2] = radius;
            radii[3] = radius;
        }

        if (bottomRight)
        {
            radii[4] = radius;
            radii[5] = radius;
        }

        if (bottomLeft)
        {
            radii[6] = radius;
            radii[7] = radius;
        }

        path.addRoundRect(new RectF(0, 0, width, height),
                radii, Path.Direction.CW);

        return path;
    }
}
