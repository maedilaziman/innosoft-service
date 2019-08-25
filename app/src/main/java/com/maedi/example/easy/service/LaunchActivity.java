package com.maedi.example.easy.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.maedi.soft.ino.base.BuildActivity;
import com.maedi.soft.ino.base.annotation.BuilderAnnotations.PostFieldParam;
import com.maedi.soft.ino.base.annotation.processor.DataProcessor;
import com.maedi.soft.ino.base.func_interface.ActivityListener;
import com.maedi.soft.ino.base.func_interface.ServicesListener;
import com.maedi.soft.ino.base.presenter.ApiServicePresent;
import com.maedi.soft.ino.base.store.MapDataParcelable;
import com.maedi.soft.ino.base.utils.CallApiService;
import com.maedi.soft.ino.base.utils.EasyData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

@SuppressLint("TimberArgCount")
public class LaunchActivity extends BuildActivity<View> implements ActivityListener<Integer>, ServicesListener {

    private final String TAG = this.getClass().getName();

    @BindView(R.id.post3)
    Button button3;

    @OnClick(R.id.post1)
    public void FetchWithQuery() {
        hitFetchWithQuery("exampleFetchWithQuery");
    }

    @OnClick(R.id.post2)
    public void FetchWithRequestBody() {
        String[] valueField = new String[]{"1"};
        hitFetchWithRequestBody("exampleFetchWithRequestBody", valueField);
    }

    private ApiServicePresent apiServicePresent;

    private void hitFetchWithQuery(String methodName)
    {
        CallApiService callApiService = new CallApiService.BuildFunction().showLoadingContentDialog(true, new CallApiService.BuildFunction.CommPropertyLoadingContentDialogListener() {
            @Override
            public boolean cancelableOnTouchOutside() {
                return false;
            }

            @Override
            public boolean cancelable() {
                return false;
            }
        }).build(this);
        callApiService.fetchDataResponse(this);

        apiServicePresent = new ApiServicePresent(callApiService);
        apiServicePresent.sendRequestData_withRetrofit_RxJava(RetrofitApiService.class, methodName, "1");
    }

    private void hitFetchWithRequestBody(String methodName, String[] valueField)
    {
        CallApiService callApiService = new CallApiService.BuildFunction().showLoadingContentDialog(true, new CallApiService.BuildFunction.CommPropertyLoadingContentDialogListener() {
            @Override
            public boolean cancelableOnTouchOutside() {
                return false;
            }

            @Override
            public boolean cancelable() {
                return false;
            }
        }).setPostDataParamListener(
                new CallApiService.CommPostDataParamCallApiServiceListener() {

                    @Override
                    public Object setDataParam(
                            @PostFieldParam(fieldParam={
                                    "userId"
                            }) Object param
                    ) {
                        DataProcessor.CommDataProcessor listenDataProcessor = new DataProcessor.CommDataProcessor() {

                            @Override
                            public EasyData bindServiceType() {
                                return EasyData.BIND_METHOD_PARAMETER;
                            }

                            @Override
                            public boolean buildString() {
                                return false;
                            }

                            @Override
                            public boolean buildJsonObject() {
                                return true;
                            }

                            @Override
                            public boolean buildMultipart() {
                                return false;
                            }
                        };
                        DataProcessor dataProcessor = new DataProcessor(listenDataProcessor);
                        return dataProcessor.getObjectFieldFromProcessor(this.getClass(), param);
                    }

                }).build(this);

        callApiService.fetchDataResponse(this);
        String postFieldData = (String)callApiService.buildDataParam(valueField);
        Timber.d(TAG+" get dataForRequest - "+postFieldData);

        apiServicePresent = new ApiServicePresent(callApiService);
        apiServicePresent.sendRequestData_withRetrofit_RequestBody(RetrofitApiService.class, methodName, postFieldData);
    }

    @Override
    public int baseContentView() {
        return R.layout.activity_main;
    }

    @Override
    public ActivityListener createListenerForActivity() {
        return this;
    }

    @Override
    public void onCreateActivity(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityPause() {

    }

    @Override
    public void onActivityStop() {

    }

    @Override
    public void onActivityDestroy() {

    }

    @Override
    public void onActivityKeyDown(int keyCode, KeyEvent event) {

    }

    @Override
    public void onActivityFinish() {

    }

    @Override
    public void onActivityRestart() {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onActivityMResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public int setPermission() {
        return 0;
    }

    @Override
    public boolean setAnalytics() {
        return false;
    }

    @Override
    public void onBuildActivityCreated() {

    }

    @Override
    public void setAnimationOnOpenActivity(Integer firstAnim, Integer secondAnim) {
        overridePendingTransition(firstAnim, secondAnim);
    }

    @Override
    public void setAnimationOnCloseActivity(Integer firstAnim, Integer secondAnim) {
        overridePendingTransition(firstAnim, secondAnim);
    }

    @Override
    public View setViewTreeObserverActivity() {
        return null;
    }

    @Override
    public void getViewTreeObserverActivity() {

    }

    @Override
    public Intent setResultIntent() {
        return null;
    }

    @Override
    public String getTagDataIntentFromActivity() {
        return null;
    }

    @Override
    public void getMapDataIntentFromActivity(MapDataParcelable parcleable) {

    }

    @Override
    public MapDataParcelable setMapDataIntentToNextActivity(MapDataParcelable parcleable) {
        return null;
    }

    @Override
    public void successPostGetData(Object data) {
        Timber.d(TAG + " - ON SUCCESS FETCH DATA [ RETROFIT ] - "+data);
        Toast.makeText(this, "SUCCESS FETCH DATA: "+data.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void errorPostGetData(Object data) {
        Timber.d(TAG + " - ON ERROR FETCH DATA [ RETROFIT ] - "+data);
        Toast.makeText(this, "ERROR FETCH DATA: "+data.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean verifyDataNonNullOrZero(boolean isDataHasNullOrZero) {
        return false;
    }
}
