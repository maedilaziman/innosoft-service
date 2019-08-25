package com.maedi.soft.ino.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;

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

    public abstract int baseContentView();

    public abstract ActivityListener createListenerForActivity();

    public abstract void onCreateActivity(Bundle savedInstanceState);

    public abstract void onActivityCreated();

    public abstract void onActivityResume();

    public abstract void onActivityPause();

    public abstract void onActivityStop();

    public abstract void onActivityDestroy();

    public abstract void onActivityKeyDown(int keyCode, KeyEvent event);

    public abstract void onActivityFinish();

    public abstract void onActivityRestart();

    public abstract void onActivitySaveInstanceState(Bundle outState);

    public abstract void onActivityRestoreInstanceState(Bundle savedInstanceState);

    public abstract void onActivityRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults);

    public abstract void onActivityMResult(int requestCode, int resultCode, Intent data);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d(TAG + " - ON CREATE CALLED");

        int layout = baseContentView();
        setContentView(layout);
        onCreateActivity(savedInstanceState);

        wrFActivity = new WeakReference<>(this);
        isViewVisible = true;

        createListenerForActivity().setAnimationOnOpenActivity(firstAnimationOpenActivity, secondAnimationOpenActivity);
    }

    //called when activity is becoming visible to the user
    @Override
    public void onStart() {
        super.onStart();
        Timber.d(TAG + " - ON START CALLED - VISIBLE TO USER : "+isViewVisible);
        if(isViewVisible)
        {
            final View v = createListenerForActivity().setViewTreeObserverActivity();
            if(null == v)
            {
                Timber.d(TAG + " - ON START CALLED NO SET VIEW TREE OBSERVER");
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
                Timber.d(TAG + " - ON START CALLED NO BUNDLE INTENT DATA FROM ACTIVITY");
            }
            else
            {
                String tag_intent = createListenerForActivity().getTagDataIntentFromActivity();
                if(null == tag_intent)
                {
                    Timber.d(TAG + " - ON START CALLED NO TAG INTENT DATA FROM ACTIVITY");
                }
                else
                {
                    MapDataParcelable dataParcel = intent.getExtras().getParcelable(tag_intent);
                    if (null == dataParcel) {
                        Timber.d(TAG + " - ON START CALLED NO PARCELABLE INTENT DATA FROM ACTIVITY");
                    } else {
                        createListenerForActivity().getMapDataIntentFromActivity(dataParcel);
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
        Timber.d(TAG + " - ON RESUME CALLED");
        onActivityResume();
    }

    //called when activity is not visible to the user
    //whether finish is call or not this method always call if activity not visible,
    //if finish call so this method called after finish method called
    @Override
    protected void onPause() {
        super.onPause();
        isViewVisible = false;
        Timber.d(TAG + " - ON PAUSE CALLED - VISIBLE TO USER : "+isViewVisible);
        onActivityPause();
    }

    //called when activity is no longer visible to the user
    @Override
    public void onStop() {
        super.onStop();
        isViewVisible = false;
        Timber.d(TAG + " - ON STOP CALLED - VISIBLE TO USER : "+isViewVisible);
        onActivityStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {}
        Timber.d(TAG + " - ON KEYDOWN CALLED");
        onActivityKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        Intent result = createListenerForActivity().setResultIntent();
        if(null == result)
        {
            Timber.d(TAG + " - ON FINISH CALLED NO RESULT INTENT");
        }
        else
        {
            setResult(RESULT_OK, result);
        }
        super.finish();
        Timber.d(TAG + " - ON FINISH CALLED");
        createListenerForActivity().setAnimationOnCloseActivity(firstAnimationOpenActivity, secondAnimationOpenActivity);
        onActivityFinish();
    }

    //called after your activity is stopped, prior to start
    @Override
    protected void onRestart() {
        super.onRestart();
        Timber.d(TAG + " - ON RESTART CALLED");
        onActivityRestart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d(TAG + " - ON SAVE INSTANCE STATE CALLED");
        onActivitySaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Timber.d(TAG + " - ON RESTORE INSTANCE STATE CALLED");
        onActivityRestoreInstanceState(savedInstanceState);
    }

    //called before the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d(TAG + " - ON DESTROY CALLED");
        onActivityDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Timber.d(TAG + " - ON ACTIVITY REQUEST PERMISSIONS CALLED");
        onActivityRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d(TAG + " - ON ACTIVITY RESULT CALLED");
        onActivityMResult(requestCode, resultCode, data);
    }

}
