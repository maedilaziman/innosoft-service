package com.maedi.soft.ino.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.maedi.soft.ino.R;
import com.maedi.soft.ino.base.func_interface.ActivityListener;
import com.maedi.soft.ino.base.store.MapDataParcelable;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * Created by Maedi on 5/13/2019.
 */

@SuppressLint("TimberArgCount")
public abstract class BaseActivity<T> extends AppCompatActivity {

    private final String TAG = "BASE_ACTIVITY";

    private WeakReference<BaseActivity<T>> wrFActivity;

    private boolean isViewVisible;

    private final int firstAnimationOpenActivity = R.anim.slide_in_zeroto_one_fr_right;

    private final int secondAnimationOpenActivity = R.anim.slide_out_oneto_zero_to_left;

    private final int firstAnimationFinishActivity = R.anim.slide_in_zeroto_one_fr_left;

    private final int secondAnimationFinishActivity = R.anim.slide_out_oneto_zero_to_right;

    public abstract int baseContentView();

    public abstract ActivityListener createListenerForActivity();

    public abstract void onCreateActivity(Bundle savedInstanceState);

    public abstract void onActivityCreated();

    public abstract void onActivityResume();

    public abstract void onActivityPause();

    public abstract void onActivityStop();

    public abstract void onActivityDestroy();

    public abstract boolean onActivityKeyDown(int keyCode, KeyEvent event);

    public abstract void onActivityFinish();

    public abstract void onActivityRestart();

    public abstract void onActivitySaveInstanceState(Bundle outState);

    public abstract void onActivityRestoreInstanceState(Bundle savedInstanceState);

    public abstract void onActivityRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);

    public abstract void onActivityMResult(int requestCode, int resultCode, Intent data);

    public abstract boolean onActivitySecure();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = baseContentView();
        setContentView(layout);
        onCreateActivity(savedInstanceState);
        wrFActivity = new WeakReference<>(this);
        isViewVisible = true;
        if(onActivitySecure())setActivitySecure(true);else setActivitySecure(false);
        createListenerForActivity().setAnimationOnOpenActivity(firstAnimationOpenActivity, secondAnimationOpenActivity);
    }

    //called when activity is becoming visible to the user
    @Override
    public void onStart() {
        super.onStart();
        if(isViewVisible)
        {
            final View v = createListenerForActivity().setViewTreeObserverActivity();
            if(null == v)
            {
                Timber.d(TAG + " - NOTHING TO SET VIEW TREE OBSERVER");
            }
            else {
                ViewTreeObserver vto = v.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                        {
                            v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        else
                        {
                            v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        createListenerForActivity().getViewTreeObserverActivity();
                    }
                });
            }

            Intent intent = getIntent();
            if(null == intent)
            {
                Timber.d(TAG + " - NO BUNDLE INTENT DATA FROM ACTIVITY");
            }
            else
            {
                String tag_intent = createListenerForActivity().getTagDataIntentFromActivity();
                if(null == tag_intent)
                {
                    Timber.d(TAG + " - NO TAG INTENT DATA FROM ACTIVITY");
                }
                else
                {
                    if(null != intent.getExtras())
                    {
                        MapDataParcelable dataParcel = intent.getExtras().getParcelable(tag_intent);
                        if (null == dataParcel) {
                            Timber.d(TAG + " - NO PARCELABLE INTENT DATA FROM ACTIVITY");
                        } else {
                            createListenerForActivity().getMapDataIntentFromActivity(dataParcel);
                        }
                    }
                }
            }

            onActivityCreated();
        }
    }

    //called when activity will start interacting with the user
    @Override
    public void onResume() {
        super.onResume();
        onActivityResume();
    }

    //called when activity is not visible to the user
    //whether finish is call or not this method always call if activity not visible,
    //if finish call so this method called after finish method called
    @Override
    protected void onPause() {
        super.onPause();
        isViewVisible = false;
        onActivityPause();
    }

    //called when activity is no longer visible to the user
    @Override
    public void onStop() {
        super.onStop();
        isViewVisible = false;
        onActivityStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {}
        return onActivityKeyDown(keyCode, event) ? true : super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        Intent result = createListenerForActivity().setResultIntent();
        if(null == result)
        {
            Timber.d(TAG + " - NO RESULT INTENT");
        }
        else
        {
            setResult(RESULT_OK, result);
        }
        super.finish();
        createListenerForActivity().setAnimationOnCloseActivity(firstAnimationFinishActivity, secondAnimationFinishActivity);
        onActivityFinish();
    }

    //called after your activity is stopped, prior to start
    @Override
    protected void onRestart() {
        super.onRestart();
        onActivityRestart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        onActivitySaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onActivityRestoreInstanceState(savedInstanceState);
    }

    //called before the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        onActivityDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        onActivityRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onActivityMResult(requestCode, resultCode, data);
    }

    protected void setActivitySecure(boolean secure)
    {
        if(secure)
        {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}
