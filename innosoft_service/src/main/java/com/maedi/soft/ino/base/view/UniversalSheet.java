/*
 * Copyright (c) 8/27/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ScrollView;

import com.maedi.soft.ino.R;
import com.maedi.soft.ino.base.utils.PixelCalc;
import com.maedi.soft.ino.base.utils.ScreenSize;
import java.lang.ref.WeakReference;

import timber.log.Timber;

public class UniversalSheet<T> extends Sheet implements Sheet.CommSheetListener, View.OnTouchListener {

    //private final String TAG = this.getClass().getName()+"_UNIVERSAL_SHEET_INFO - ";

    private Context context;

    private float radius = 0;

    private float startSize = 0;

    private float length = 0;

    private String strLength;

    private int sheetPos = 0;

    private int backgroundColor;

    private boolean radTopLeft;

    private boolean radTopRight;

    private boolean radBottomRight;

    private boolean radBottomLeft;

    private int header;

    private float headerSize;

    private FrameUniversalSheet.LayoutParams params;

    private int sheetMargin;

    private int actualMargin;

    private int defaultSheetMargin = 0;

    private int Position_X;

    private int Position_Y;

    private int X;

    private int Y;

    private int marginToDestroy = -1;

    private float mMaximumVelocity;

    //private float mMinimumVelocity;

    private VelocityTracker mVelocityTracker;

    private int marginTopBottom;

    private int marginLeftRight;

    private final float MIN_SWIPE_DISTANCE_X = 5;

    private final float MIN_SWIPE_DISTANCE_Y = 5;

    private final float MIN_SWIPE_DISTANCE_X_PLUS = 10;

    private final float MIN_SWIPE_DISTANCE_Y_PLUS = 10;

    private final int delayMillDismisSheet = 400;

    private final int defaultAnimDestroy = 200;

    private final int defaultAnimMovement = 100;

    private int maxSheetSize;

    private boolean mTouchScrollingChild;

    private boolean isUserScrolling;

    private boolean isMaxScrollingTop;

    private boolean isMaxScrollingBottom;

    private boolean isRecycler_UserScrolling;

    private boolean isMaxRecycler_ScrollingLeft;

    private boolean isMaxRecycler_ScrollingRight;

    private int downX = 0;

    private int downY = 0;

    private NestedScrollView.OnScrollChangeListener nestedScrollListener;

    private RecyclerView.OnScrollListener scrollListener;

    private WeakReference<View> mNestedScrollChildRef;

    private WeakReference<View> mRecyclerViewChildRef;

    private GestureDetectorCompat gestureDetector;

    private UniversalSheet universalSheet;

    private int[] constantSheetMargin;

    private View headerView;

    public interface CommUniversalSheetListener
    {
        void onOpened();

        void onClosed();

        void onHidden();

        void getHeaderView(View v);
    }

    private CommUniversalSheetListener listener;

    public void setUniversalSheetCallBack(CommUniversalSheetListener listener)
    {
        this.listener = listener;
    }

    public UniversalSheet(Context context) {
        super(context);
        this.context = context;
        setUp();
    }

    public UniversalSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setUp();
        TypedArray attr = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.UniversalSheet, 0, 0
        );
        try {
            if(null == attr)
            {
                sheetPos = 0;
                radius = 0;
                length = getWidth();
                startSize = ScreenSize.instance(context).getHeight() / 2;
                backgroundColor = context.getResources().getColor(R.color.white);
                header = -1;
                headerSize = 0;
            }
            else {

                sheetPos = attr.getInt(R.styleable.UniversalSheet_uniSheetPos, 0);
                radius = attr.getDimension(R.styleable.UniversalSheet_uniSheetRadius, 0);
                startSize = attr.getDimension(R.styleable.UniversalSheet_uniSheetStartSize, 0);
                strLength = attr.getString(R.styleable.UniversalSheet_uniSheetLength);
                backgroundColor = attr.getColor(R.styleable.UniversalSheet_uniSheetBackgroundColor, 0xffffff);
                header = attr.getResourceId(R.styleable.UniversalSheet_uniSheetHeader, -1);
                headerSize = attr.getDimension(R.styleable.UniversalSheet_uniSheetHeaderSize, 0);

                if (strLength.equalsIgnoreCase("-1"))
                {
                    length = attr.getInt(R.styleable.UniversalSheet_uniSheetLength, -1);
                }
                else
                {
                    length = attr.getDimension(R.styleable.UniversalSheet_uniSheetLength, 0);
                }
                if (sheetPos == 0 || sheetPos == 1) {
                    defaultSheetMargin = (int) (ScreenSize.instance(context).getHeight() - startSize);
                    sheetMargin = defaultSheetMargin;
                    actualMargin = sheetMargin;
                } else {
                    defaultSheetMargin = (int) (ScreenSize.instance(context).getWidth() - startSize);
                    sheetMargin = defaultSheetMargin;
                }
                setRadiusPos(sheetPos);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error: "+e.getCause()+", with message: "+e.getMessage());
        }
        finally {
            attr.recycle();
        }
        setListener(this);
    }

    private void setRadiusPos(int sheetPos)
    {
        switch (sheetPos)
        {
            case 0:
                radBottomLeft = true;
                radBottomRight = true;
                maxSheetSize = ScreenSize.instance(context).getHeight() - ((int) startSize / 2);
                break;
            case 1:
                radTopLeft = true;
                radTopRight = true;
                maxSheetSize = ScreenSize.instance(context).getHeight() - ((int) startSize / 2);
                break;
            case 2:
                radTopLeft = true;
                radBottomLeft = true;
                maxSheetSize = ScreenSize.instance(context).getWidth() - ((int) startSize / 2);
                break;
            case 3:
                radTopRight = true;
                radBottomRight = true;
                maxSheetSize = ScreenSize.instance(context).getWidth() - ((int) startSize / 2);
                break;
        }
    }

    public UniversalSheet(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setUp();
    }

    private void setUp()
    {
        universalSheet = this;
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        //mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }

    @Override
    protected UniversalSheet consumer() {
        return this;
    }

    @Override
    protected void observed() {
        params = new FrameUniversalSheet.LayoutParams(FrameUniversalSheet.LayoutParams.MATCH_PARENT, FrameUniversalSheet.LayoutParams.MATCH_PARENT);
        switch(sheetPos)
        {
            case 0 :// sheet pos is top
                if(length < 0)
                {
                    length = getHeight();
                }
                marginLeftRight = ((int) (ScreenSize.instance(context).getWidth() - length)) / 2;
                if(marginLeftRight < 0)marginLeftRight=0;
                params.setMargins(marginLeftRight, 0, marginLeftRight, defaultSheetMargin);
                params.gravity = Gravity.TOP;
                constantSheetMargin = new int[]{marginLeftRight, 0, marginLeftRight, defaultSheetMargin, Gravity.TOP};
                break;
            case 1 :// sheet pos is bottom
                if(length < 0)
                {
                    length = getHeight();
                }
                marginLeftRight = ((int) (ScreenSize.instance(context).getWidth() - length)) / 2;
                if(marginLeftRight < 0)marginLeftRight=0;
                params.setMargins(marginLeftRight, defaultSheetMargin, marginLeftRight, 0);
                params.gravity = Gravity.BOTTOM;
                constantSheetMargin = new int[]{marginLeftRight, defaultSheetMargin, marginLeftRight, 0, Gravity.BOTTOM};
                break;
            case 2 :// sheet pos is left
                if(length < 0)
                {
                    length = getWidth();
                }
                marginTopBottom = ((int) (ScreenSize.instance(context).getHeight() - length)) / 2;
                if(marginTopBottom < 0)marginTopBottom=0;
                params.setMargins(defaultSheetMargin, marginTopBottom, 0, marginTopBottom);
                params.gravity = Gravity.RIGHT;
                constantSheetMargin = new int[]{defaultSheetMargin, marginTopBottom, 0, marginTopBottom, Gravity.RIGHT};
                break;
            case 3 :// sheet pos is right
                if(length < 0)
                {
                    length = getWidth();
                }
                marginTopBottom = ((int) (ScreenSize.instance(context).getHeight() - length)) / 2;
                if(marginTopBottom < 0)marginTopBottom=0;
                params.setMargins(0, marginTopBottom, defaultSheetMargin, marginTopBottom);//l,t,r,b
                params.gravity = Gravity.LEFT;
                constantSheetMargin = new int[]{0, marginTopBottom, defaultSheetMargin, marginTopBottom, Gravity.LEFT};
                break;
            default:
                break;
        }
        setLayoutParams(params);
    }

    @Override
    public void parentAction() {
        parentAction((View) universalSheet.getParent());
    }

    @Override
    public void feelTouch() {
        universalSheet.setOnTouchListener(this);
        int countChild = universalSheet.getChildCount();
        if(countChild > 1)
        {
            throw new RuntimeException("Universal Sheet can only have one child layout");
        }
        else
        {
            layoutValidation(universalSheet.getChildAt(0), 0);
        }

        if(header > -1)
        {
            if(countChild > 1)universalSheet.removeViewAt(0);
            LayoutInflater inflater = LayoutInflater.from(context);
            headerView = inflater.inflate(header, null);
            //max header size is 70 dp
            if (headerSize == 0 || PixelCalc.PixelToDP(context, (int) headerSize) > 70) {
                headerSize = PixelCalc.DpToPixel(70, context);
            }
            params = new FrameUniversalSheet.LayoutParams(FrameUniversalSheet.LayoutParams.MATCH_PARENT, (int) headerSize);
            headerView.setLayoutParams(params);
            universalSheet.addView(headerView, 0);
            if(null != listener)listener.getHeaderView(headerView);
        }

        mRecyclerViewChildRef = new WeakReference<>(findSpecChild(universalSheet, "recyclerview"));
        mNestedScrollChildRef = new WeakReference<>(findSpecChild(universalSheet, "nestedscrollview"));
    }

    private RecyclerView dummyRecycler;
    private void layoutValidation(View view, int pos) {
        if(pos == 0) {
            if (view instanceof NestedScrollView) {
                for (int i=0, count = ((ViewGroup) view).getChildCount(); i < count; i++) {
                    layoutValidation(((ViewGroup) view).getChildAt(i), 1);
                }
            }
            else
            {
                throw new RuntimeException("NestedScrollView must be a parent layout !");
            }
        }
        else
        {
            if (view instanceof NestedScrollView) {
                throw new RuntimeException("NestedScrollView cannot be more than one in UniversalSheet !");
            }
            if (view instanceof RecyclerView) {
                if(null == dummyRecycler)
                    dummyRecycler = (RecyclerView) view;
                else
                {
                    throw new RuntimeException("RecyclerView cannot be more than one in UniversalSheet !");
                }
            }
            if (view instanceof ListView || view instanceof ScrollView) {
                throw new RuntimeException("ListView or ScrollView is not allowed inside UniversalSheet !");
            }
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                for (int i=0, count = group.getChildCount(); i < count; i++) {
                    layoutValidation(group.getChildAt(i), 1);
                }
            }
        }
    }

    private View findSpecChild(View view, String tag) {
        if (view instanceof RecyclerView &&
                tag.equalsIgnoreCase("recyclerview")) {
            RecyclerView recv = (RecyclerView) view;
            recv.setNestedScrollingEnabled(false);
            return view;
        }

        if (view instanceof NestedScrollView &&
                tag.equalsIgnoreCase("nestedscrollview")) {
            return view;
        }
        else if (view instanceof NestedScrollView &&
                tag.equalsIgnoreCase("recyclerview")) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View scrollingChild = findSpecChild(group.getChildAt(i), tag);
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View scrollingChild = findSpecChild(group.getChildAt(i), tag);
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }

    @Override
    public int setBackgroundColorWindow() {
        return backgroundColor;
    }

    @Override
    public int setWidth() {
        return getWidth();
    }

    @Override
    public int setHeight() {
        return getHeight();
    }

    @Override
    public float setRadius() {
        return radius;
    }

    @Override
    public boolean setTopLeft() {
        return radTopLeft;
    }

    @Override
    public boolean setTopRight() {
        return radTopRight;
    }

    @Override
    public boolean setBottomRight() {
        return radBottomRight;
    }

    @Override
    public boolean setBottomLeft() {
        return radBottomLeft;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        X = (int) event.getRawX();
        Y = (int) event.getRawY();
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_OUTSIDE:
                break;
            case (MotionEvent.ACTION_DOWN) :
                //////////////////////////////////////////////////////////////////////////////////////////////////
                // user finger first time down in universal sheet, not in child
                // default scrolling view visible is top, so set max scrolling top to true
                //////////////////////////////////////////////////////////////////////////////////////////////////
                if(sheetPos == 0 || sheetPos == 1) {
                    if (null == nestedScrollListener) {
                        isMaxScrollingTop = true;
                    }
                }
                else
                {
                    actualMargin = -1;
                }
                resetpos(event);
                break;
            case (MotionEvent.ACTION_UP) :
                if (sheetMargin > maxSheetSize)
                {
                    destroySheet(sheetMargin);
                } else if (sheetMargin > defaultSheetMargin && sheetMargin <= maxSheetSize)
                {
                    smoothAnimateMarginChange(sheetMargin, defaultSheetMargin, defaultAnimMovement);
                }
                break;
            case (MotionEvent.ACTION_MOVE) :
                break;
            case (MotionEvent.ACTION_CANCEL) :
                break;
        }
        if(null ==  gestureDetector)
        {
            gestureDetector = new GestureDetectorCompat(context, new GestureFeel());
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void smoothAnimateMarginChange(int fromMargin, int toMargin, int duration)
    {
        ValueAnimator varl = ValueAnimator.ofInt(fromMargin, toMargin);
        varl.setDuration(duration);
        varl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                processSheetPos_animateMarginChange(animation);
            }
        });
        varl.start();
    }

    private void destroySheet(int sheetMargin)
    {
        int toMargin = processSheetPos_destroySheet();
        smoothAnimateMarginChange(sheetMargin, toMargin, defaultAnimDestroy);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                openSheet(false);
            }
        }, delayMillDismisSheet);
    }

    private void openSheet(boolean show)
    {
        int[] animWhenOpen = processSheetPos_onOpenWithAnimation();
        Animation animation = null;
        View v = (View) universalSheet.getParent();
        if(show)
        {
            if(null != params) {
                processSheetPos_openNewWindow();
            }
            v.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(context, animWhenOpen[0]);
        }
        else
        {
            v.setVisibility(View.GONE);
            animation = AnimationUtils.loadAnimation(context, animWhenOpen[1]);
        }
        animation.setDuration(500);
        universalSheet.startAnimation(animation);
    }

    private class GestureFeel implements GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //this will only be called after the detector is confident that the user's first tap is not followed
            //by a second tap leading to a double-tap gesture
            //if you don't need double taps, than onSingleTapUp will give you the event faster
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if(sheetPos == 2 || sheetPos == 3)
            {
                if(sheetPos == 2)
                {
                    sheetMargin = X - Position_X;
                }
                else if(sheetPos == 3)
                {
                    sheetMargin = ScreenSize.instance(context).getWidth() - (X - Position_X);
                }
                int m = PixelCalc.DpToPixel(1, context);
                if (sheetMargin < m)
                {
                    sheetMargin = m;
                }
                int index = e2.getActionIndex();
                int pointerId = e2.getPointerId(index);
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int pixel = (int) Math.abs(VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId));
                smoothAnimateMarginChange(sheetMargin-pixel, sheetMargin, pixel);
            }
            else
            {
                if(sheetPos == 0)
                {
                    sheetMargin = ScreenSize.instance(context).getHeight() - (Y - Position_Y);
                    actualMargin = sheetMargin;
                }
                else if(sheetPos == 1)
                {
                    sheetMargin = Y - Position_Y;
                    actualMargin = sheetMargin;
                }
                int m = PixelCalc.DpToPixel(1, context);
                if (sheetMargin < m)
                {
                    sheetMargin = m;
                    if(sheetPos == 0 || sheetPos == 1)actualMargin = m;
                }
                int index = e2.getActionIndex();
                int pointerId = e2.getPointerId(index);
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int pixel = (int) Math.abs(VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId));
                smoothAnimateMarginChange(sheetMargin-pixel, sheetMargin, pixel);
            }
            marginToDestroy = sheetMargin;

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaX = e1.getRawX() - e2.getRawX();
            float deltaY = e1.getRawY() - e2.getRawY();
            float deltaXAbs = Math.abs(deltaX);
            float deltaYAbs = Math.abs(deltaY);
            boolean destroy = false;

            if(deltaXAbs >= MIN_SWIPE_DISTANCE_X)
            {
                int posActualX = 0;
                int toMargin = 0;
                if(deltaX > 0)
                {
                    if(sheetPos == 2)
                    {
                        toMargin = PixelCalc.DpToPixel(1, context);
                        posActualX = X - Position_X;
                    }
                    else if(sheetPos == 3)
                    {
                        toMargin = defaultSheetMargin;
                        posActualX = ScreenSize.instance(context).getWidth() - (X - Position_X);
                        if (posActualX > defaultSheetMargin)
                        {
                            destroy = true;
                        }
                    }

                }else
                {
                    if(sheetPos == 2)
                    {
                        toMargin = defaultSheetMargin;
                        posActualX = X - Position_X;
                        if (posActualX > defaultSheetMargin)
                        {
                            destroy = true;
                        }
                    }
                    else if(sheetPos == 3)
                    {
                        toMargin = PixelCalc.DpToPixel(1, context);
                        posActualX = ScreenSize.instance(context).getWidth() - (X - Position_X);
                    }
                }
                if(sheetPos == 2 || sheetPos == 3)
                {
                    if(destroy)
                    {
                        if(sheetPos == 2) sheetMargin = X - Position_X;
                        else if(sheetPos == 3) sheetMargin = ScreenSize.instance(context).getWidth() - (X - Position_X);
                        destroySheet(sheetMargin);
                    }
                    else {
                        int pixel = (int) Math.abs(velocityX) == 0 ? 1 : (int) Math.abs(velocityX);
                        int speed = Math.abs(pixel / posActualX == 0 ? 1 : posActualX);
                        int duration = speed == 0 || speed == 1 ? 200 : (speed > 4 ? 450 : speed * 100);
                        marginToDestroy = toMargin;
                        smoothAnimateMarginChange(sheetMargin, toMargin, duration);
                    }
                }
            }

            if(deltaYAbs >= MIN_SWIPE_DISTANCE_Y) {

                int posActualY = 0;
                int toMargin = 0;
                if (deltaY > 0) {
                    if(sheetPos == 0)
                    {
                        toMargin = defaultSheetMargin;
                        actualMargin = toMargin;
                        posActualY = ScreenSize.instance(context).getHeight() - (Y - Position_Y);
                        if (posActualY > defaultSheetMargin)
                        {
                            destroy = true;
                        }
                    }
                    else if(sheetPos == 1)
                    {
                        toMargin = PixelCalc.DpToPixel(1, context);
                        actualMargin = toMargin;
                        posActualY = Y - Position_Y;
                    }

                } else {
                    if(sheetPos == 0)
                    {
                        toMargin = PixelCalc.DpToPixel(1, context);
                        actualMargin = toMargin;
                        posActualY = ScreenSize.instance(context).getHeight() - (Y - Position_Y);
                    }
                    else if(sheetPos == 1)
                    {
                        toMargin = defaultSheetMargin;
                        actualMargin = toMargin;
                        posActualY = Y - Position_Y;
                        if (posActualY > defaultSheetMargin)
                        {
                            destroy = true;
                        }
                    }
                }

                if(sheetPos == 0 || sheetPos == 1) {
                    if(destroy)
                    {
                        if(sheetPos == 0)sheetMargin = ScreenSize.instance(context).getHeight() - (Y - Position_Y);
                        else if(sheetPos == 1)sheetMargin = Y - Position_Y;
                        actualMargin = sheetMargin;
                        destroySheet(sheetMargin);
                    }
                    else {
                        int pixel = (int) Math.abs(velocityY) == 0 ? 1 : (int) Math.abs(velocityY);
                        int speed = Math.abs(pixel / posActualY == 0 ? 1 : posActualY);
                        int duration = speed == 0 || speed == 1 ? 200 : (speed > 4 ? 450 : speed * 100);
                        actualMargin = toMargin;
                        marginToDestroy = actualMargin;
                        smoothAnimateMarginChange(sheetMargin, toMargin, duration);
                    }
                }
            }

            return true;
        }
    }

    private void parentAction(View v)
    {
        if(v instanceof FrameUniversalSheet) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (marginToDestroy == -1) marginToDestroy = sheetMargin;
                    destroySheet(marginToDestroy);
                }
            });
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        X = (int) event.getRawX();
        Y = (int) event.getRawY();
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mTouchScrollingChild = false;
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if (null != mRecyclerViewChildRef.get()) {
                    //////////////////////////////////////////////////////////////////////////////////
                    // CHECK IF USER ENABLING NESTED SCROLLING,
                    // IF USER ENABLING IS TRUE SO RESET TO FALSE,
                    // BECAUSE WE NOT ALLOWING USER ENABLING NESTED SCROLL
                    //////////////////////////////////////////////////////////////////////////////////
                    RecyclerView recyclerView = (RecyclerView) mRecyclerViewChildRef.get();
                    if(recyclerView.isNestedScrollingEnabled())
                    {
                        recyclerView.setNestedScrollingEnabled(false);
                    }
                }
                downX = (int) event.getX();
                downY = (int) event.getY();
                mTouchScrollingChild = false;
                if(sheetPos == 0 || sheetPos == 1) {
                    if (null == nestedScrollListener) {
                        isMaxScrollingTop = true;
                    }
                }
                else
                {
                    actualMargin = -1;
                }
                resetpos(event);
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                float dx = event.getX() - downX;
                float dy = event.getY() - downY;
                mTouchScrollingChild = true;
                float xAbs = Math.abs(dx);
                float yAbs = Math.abs(dy);
                if(sheetPos == 2 || sheetPos == 3) {
                    if (xAbs >= MIN_SWIPE_DISTANCE_X_PLUS) {
                        if (null != mRecyclerViewChildRef.get() &&
                                isViewInBounds(mRecyclerViewChildRef.get(), (int) event.getRawX(), (int) event.getRawY())) {

                            RecyclerView recyclerView = (RecyclerView) mRecyclerViewChildRef.get();
                            if (recyclerView.getLayoutManager().canScrollHorizontally()) {
                                if (dx > 0) {
                                    // is scrolling left to right ?
                                    if (isMaxRecycler_ScrollingLeft) {
                                        mTouchScrollingChild = false;
                                        return true;
                                    }
                                } else {
                                    // is scrolling right to left ?
                                    if (isMaxRecycler_ScrollingRight) {
                                        mTouchScrollingChild = false;
                                        return true;
                                    }
                                }
                                mTouchScrollingChild = true;
                                return false;
                            } else {
                                mTouchScrollingChild = false;
                                return true;
                            }
                        } else {
                            mTouchScrollingChild = false;
                            return true;
                        }
                    }
                }

                if(yAbs >= MIN_SWIPE_DISTANCE_Y_PLUS)
                {
                    if (dy > 0) {
                        // is scrolling top to bottom ?
                        if (isMaxScrollingTop || actualMargin > PixelCalc.DpToPixel(1, context)) {
                            mTouchScrollingChild = false;
                            return true;
                        }
                    } else {
                        // is scrolling bottom to top ?
                        if (isMaxScrollingBottom || actualMargin > PixelCalc.DpToPixel(1, context)) {
                            mTouchScrollingChild = false;
                            return true;
                        }
                    }
                }

                return false;
            }
            default: {
                return false;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        super.dispatchTouchEvent(event);
        if(null ==  gestureDetector)
        {
            gestureDetector = new GestureDetectorCompat(context, new GestureFeel());
        }
        if(mTouchScrollingChild)
        {
            return false;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void resetpos(MotionEvent e)
    {
        processSheetPos_resetpos();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }

        if(sheetPos == 0 || sheetPos == 1) {
            if (null != mNestedScrollChildRef.get()) {
                NestedScrollView nestedScrollView = (NestedScrollView) mNestedScrollChildRef.get();
                if (null == nestedScrollListener) {
                    nestedScrollListener = new NestedScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            int distanceScrollBottom = scrollY - oldScrollY;//scrollY > oldScrollY
                            int distanceScrollTop = oldScrollY - scrollY;//scrollY < oldScrollY
                            if (distanceScrollBottom > 5) {
                                isMaxScrollingBottom = false;
                                isMaxScrollingTop = false;
                            }
                            if (distanceScrollTop > 5) {
                                isMaxScrollingTop = false;
                                isMaxScrollingBottom = false;
                            }

                            if (scrollY == 0) {
                                // is scrolling top to bottom ?
                                mTouchScrollingChild = false;
                                isMaxScrollingTop = true;
                                isMaxScrollingBottom = false;
                            }

                            if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                    scrollY > oldScrollY) {
                                // is scrolling bottom to top ?
                                mTouchScrollingChild = false;
                                isMaxScrollingTop = false;
                                isMaxScrollingBottom = true;
                            }
                        }
                    };
                    nestedScrollView.setOnScrollChangeListener(nestedScrollListener);
                }
            }
        }

        if (null != mRecyclerViewChildRef.get()) {
            ////////////////////////////////////////////////////////////////////////////////
            // is view bounds must be call once in this method,
            // aims that this view bounds can run properly when called in another method
            // this is weired
            ////////////////////////////////////////////////////////////////////////////////
            isViewInBounds(mRecyclerViewChildRef.get(), (int) e.getRawX(), (int) e.getRawY());

            RecyclerView recyclerView = (RecyclerView) mRecyclerViewChildRef.get();
            if(recyclerView.getLayoutManager().canScrollHorizontally()) {
                if (null == scrollListener) {
                    scrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                                isRecycler_UserScrolling = true;
                            }
                            else if (newState == RecyclerView.SCROLL_STATE_IDLE) {//No scrolling is done
                                isRecycler_UserScrolling = false;
                            }
                            else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                                //User has lifted his finger, and the animation is now slowing down
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            if (isRecycler_UserScrolling) {
                                if (dx > 0) {
                                    isMaxRecycler_ScrollingLeft = false;
                                    isMaxRecycler_ScrollingRight = false;
                                }
                                if (dx < 0) {
                                    isMaxRecycler_ScrollingRight = false;
                                    isMaxRecycler_ScrollingLeft = false;
                                }
                                if (!recyclerView.canScrollHorizontally(-1)) {
                                    // is scrolling left to right ?
                                    isMaxRecycler_ScrollingLeft = true;
                                    isMaxRecycler_ScrollingRight = false;
                                    mTouchScrollingChild = false;
                                } else if (!recyclerView.canScrollHorizontally(1)) {
                                    // is scrolling right to left ?
                                    isMaxRecycler_ScrollingLeft = false;
                                    isMaxRecycler_ScrollingRight = true;
                                    mTouchScrollingChild = false;
                                }
                            }
                        }
                    };
                    recyclerView.addOnScrollListener(scrollListener);
                }
            }
        }
    }

    private int[] processSheetPos_onOpenWithAnimation()
    {
        int animIn = 0;
        int animOut = 0;
        switch (sheetPos)
        {
            case 0 :
                animIn = R.anim.slide_in_zeroto_one_fr_top;
                animOut = R.anim.slide_out_oneto_zero_to_bottom;
                break;
            case 1 :
                animIn = R.anim.slide_in_zeroto_one_fr_bottom;
                animOut = R.anim.slide_out_oneto_zero_to_top;
                break;
            case 2 :
                animIn = R.anim.slide_in_zeroto_one_fr_right;
                animOut = R.anim.slide_out_oneto_zero_to_left;
                break;
            case 3 :
                animIn = R.anim.slide_in_zeroto_one_fr_left;
                animOut = R.anim.slide_out_oneto_zero_to_right;
                break;
        }

        return new int[]{animIn, animOut};
    }

    private void processSheetPos_openNewWindow()
    {
        switch(sheetPos)
        {
            case 0:
                marginToDestroy = constantSheetMargin[3];
                smoothAnimateMarginChange(ScreenSize.instance(context).getHeight(), constantSheetMargin[3], defaultAnimDestroy);
                break;
            case 1:
                marginToDestroy = constantSheetMargin[1];
                smoothAnimateMarginChange(ScreenSize.instance(context).getHeight(), constantSheetMargin[1], defaultAnimDestroy);
                break;
            case 2:
                marginToDestroy = constantSheetMargin[0];
                smoothAnimateMarginChange(ScreenSize.instance(context).getHeight(), constantSheetMargin[0], defaultAnimDestroy);
                break;
            case 3:
                marginToDestroy = constantSheetMargin[2];
                smoothAnimateMarginChange(ScreenSize.instance(context).getHeight(), constantSheetMargin[2], defaultAnimDestroy);
                break;
        }
    }

    private void processSheetPos_animateMarginChange(ValueAnimator animation)
    {
        FrameUniversalSheet.LayoutParams params = (FrameUniversalSheet.LayoutParams) universalSheet.getLayoutParams();
        switch(sheetPos)
        {
            case 0:
                params.bottomMargin = (Integer) animation.getAnimatedValue();
                break;
            case 1:
                params.topMargin = (Integer) animation.getAnimatedValue();
                break;
            case 2:
                params.leftMargin = (Integer) animation.getAnimatedValue();
                break;
            case 3:
                params.rightMargin = (Integer) animation.getAnimatedValue();
                break;
        }
        universalSheet.setLayoutParams(params);
        universalSheet.requestLayout();
    }

    private int processSheetPos_destroySheet()
    {
        int toMargin = 0;
        switch(sheetPos)
        {
            case 0:
                toMargin = ScreenSize.instance(context).getHeight();
                break;
            case 1:
                toMargin = ScreenSize.instance(context).getHeight();
                break;
            case 2:
                toMargin = ScreenSize.instance(context).getWidth();
                break;
            case 3:
                toMargin = ScreenSize.instance(context).getWidth();
                break;
        }

        return toMargin;
    }

    private void processSheetPos_resetpos()
    {
        FrameUniversalSheet.LayoutParams layoutParams = (FrameUniversalSheet.LayoutParams) universalSheet.getLayoutParams();
        switch (sheetPos)
        {
            case 0:
                Position_Y = Y - universalSheet.getHeight();
                break;
            case 1:
                Position_Y = Y - layoutParams.topMargin;
                break;
            case 2:
                Position_X = X - layoutParams.leftMargin;
                break;
            case 3:
                Position_X = X - universalSheet.getWidth();
                break;
        }
    }

    private Rect outRect = null;
    private int[] locationView = new int[2];
    public boolean isViewInBounds(View view, int x, int y){
        if(null == outRect)outRect = new Rect();
        view.getDrawingRect(outRect);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                view.getLocationOnScreen(locationView);
            }
        }, 60);
        outRect.offset(locationView[0], locationView[1]);
        boolean iscontainsEvent = outRect.contains(x, y);
        view.getGlobalVisibleRect(outRect);

        return iscontainsEvent;
    }

    public void closed()
    {
        if (marginToDestroy == -1) marginToDestroy = sheetMargin;
        destroySheet(marginToDestroy);
        if(null != listener)listener.onClosed();
    }

    public void opened()
    {
        openSheet(true);
        if(null != listener)listener.onOpened();
    }

    public void hidden()
    {
        View v = (View) universalSheet.getParent();
        v.setVisibility(View.GONE);
        if(null != listener)listener.onHidden();
    }
}