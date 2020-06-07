package com.maedi.soft.ino.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.maedi.soft.ino.R;
import com.maedi.soft.ino.base.utils.PixelCalc;

import timber.log.Timber;

public class SimpleProgressBarLoadingData extends RelativeLayout {

    private Context context;

    private float progressBarWidth = 0;

    private float progressBarHeight = 0;

    private int progressBarVisibility = 0;

    // default value for width of progress bar
    private int defValProgressBarWidth = 50;

    // default value for height of progress bar
    private int defValProgressBarHeight = 50;

    // default value for height of progress bar
    private int defValProgressBarVisibility = 0;

    private ProgressBar progressBar;

    private final int delayFinishDrawLayout = 800;

    private boolean hasFinishDrawLayout = false;

    private SimpleProgressBarLoadingData parentLayout;

    public interface CommInterface
    {
        void progressBarHasVisible(ProgressBar progressBar);

        void progressBarHasGone(ProgressBar progressBar);
    }

    private CommInterface listener;

    public void setListener(CommInterface listener)
    {
        this.listener = listener;
    }

    public SimpleProgressBarLoadingData(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public SimpleProgressBarLoadingData(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);

        TypedArray attrProgressBar = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SimpleProgressBarLoadingData,
                0, 0
        );
        try {
            if(null == attrProgressBar)
            {
                progressBarWidth = PixelCalc.DpToPixel(defValProgressBarWidth, context);
                progressBarHeight = PixelCalc.DpToPixel(defValProgressBarHeight, context);
                progressBarVisibility = defValProgressBarVisibility;
            }
            else
            {
                progressBarWidth = attrProgressBar.getDimension(R.styleable.SimpleProgressBarLoadingData_widthProgressBar, defValProgressBarWidth);
                progressBarHeight = attrProgressBar.getDimension(R.styleable.SimpleProgressBarLoadingData_heightProgressBar, defValProgressBarHeight);
                progressBarVisibility = attrProgressBar.getInt(R.styleable.SimpleProgressBarLoadingData_visibilityProgressBar, defValProgressBarVisibility);
            }
        } finally {
            attrProgressBar.recycle();
        }
    }

    public SimpleProgressBarLoadingData(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context);
    }

    private void init(Context ctx)
    {
        progressBar = new ProgressBar(ctx);
        parentLayout = this;

        ViewTreeObserver vto = parentLayout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (parentLayout.getMeasuredWidth() > 0)
                {
                    hasFinishDrawLayout = true;
                    parentLayout.getViewTreeObserver().removeOnPreDrawListener(this);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.width = (int) progressBarWidth;
                    params.height = (int) progressBarHeight;
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

                    if(progressBarVisibility == 1) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                    }
                    progressBar.setLayoutParams(params);
                    parentLayout.addView(progressBar);
                }
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

    public void showProgressBar()
    {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if(hasFinishDrawLayout)
            {
                progressBar.setVisibility(View.VISIBLE);
                if(null != listener)listener.progressBarHasVisible(progressBar);
            }
            else
            {
                //need more times to wait until finish draw layout
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                    if(null != listener)listener.progressBarHasVisible(progressBar);
                }, delayFinishDrawLayout);
            }

        }, delayFinishDrawLayout);
    }

    public void hideProgressBar()
    {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if(hasFinishDrawLayout)
            {
                progressBar.setVisibility(View.GONE);
                if(null != listener)listener.progressBarHasGone(progressBar);
            }
            else
            {
                //need more times to wait until finish draw layout
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    if(null != listener)listener.progressBarHasGone(progressBar);
                }, delayFinishDrawLayout);
            }

        }, delayFinishDrawLayout);
    }
}