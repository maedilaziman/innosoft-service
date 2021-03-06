# About
innosoft-service aims to help produce an easily usable implementation of api service
</br>
</br>
[![](https://jitpack.io/v/maedilaziman/innosoft-service.svg)](https://jitpack.io/#maedilaziman/innosoft-service)
</br>
<h2>Dependency</h2>
Add this in your root build.gradle file (not your module build.gradle file):
</br>
</br>
<pre><span class="pl-en">allprojects</span> {
	repositories {
        maven { url <span class="pl-s"><span class="pl-pds">"</span>https://jitpack.io<span class="pl-pds">"</span></span> }
    }
}</pre>
Then, add the library to your module build.gradle
</br>
</br>
<pre><span class="pl-en">dependencies</span> {
    implementation <span class="pl-s"><span class="pl-pds">'</span>com.github.maedilaziman:innosoft-service:1.0.0<span class="pl-pds">'</span></span>
}</pre>
Release 1.0.1
</br>
</br>
<pre><span class="pl-en">dependencies</span> {
    implementation <span class="pl-s"><span class="pl-pds">'</span>com.github.maedilaziman:innosoft-service:1.0.1<span class="pl-pds">'</span></span>
}</pre>
Release 1.0.2
</br>
</br>
<pre><span class="pl-en">dependencies</span> {
    implementation <span class="pl-s"><span class="pl-pds">'</span>com.github.maedilaziman:innosoft-service:1.0.2<span class="pl-pds">'</span></span>
}</pre>
Release 1.0.3
</br>
</br>
<pre><span class="pl-en">dependencies</span> {
    implementation <span class="pl-s"><span class="pl-pds">'</span>com.github.maedilaziman:innosoft-service:1.0.3<span class="pl-pds">'</span></span>
}</pre>
<h2>Features</h2>
<ul>
<li>Call API Service with request parameter.</li>
<li>Call API Service with Json Body.</li>
<li>Call API Service to upload File/Image.</li>
<li>Customize Loading dialog when call API Service.</li>
</ul>
<h2>Features release 1.0.1</h2>
You can easily make sheets from top, bottom, left or right
<ul>
<li>Create sheet from top</li>
<li>Create sheet from Bottom</li>
<li>Create sheet from Left</li>
<li>Create sheet from Right</li>
</ul>
<h2>Features release 1.0.2</h2>
You can easily make simple and beauty dialog
<ul>
<li>Create simple and beauty dialog</li>
<li>Create custom dialog</li>
</ul>
<h2>Features release 1.0.3</h2>
<ul>
<li>Add secure activities</li>
<li>Handle null data</li>
<li>Handle offline network connection</li>
</ul>
<h2>Usage</h2>
<h3>API Service</h3>
First you need to set @HeaderService property in Application context class,</br>and if you want to set analytics, you just add @SetAnalytics default is true.</br>
you can look this example, Application context class:
</br>
</br>
<pre>import android.app.Application;<br />import com.maedi.soft.ino.base.annotation.BuilderAnnotations.SetAnalytics;<br />import com.maedi.soft.ino.base.annotation.BuilderAnnotations.HeaderService;<br /><br />public class AppContext extends Application {<br /><br />    //key and value is your api key - default is empty<br />    //baseUrl is your base url server - this baseUrl cannot empty<br />    //timeout is connection timeout, default is 60 seconds (60000)<br />    @HeaderService(key="", value="", baseUrl="https://jsonplaceholder.typicode.com/", timeout = "60000")<br />    String dataApiKey;<br /><br />    //default set analytics is true<br />    @SetAnalytics(enabled = false)<br />    public void setAnalytics(String valueTag){<br />    }<br /><br />    @Override<br />    public void onCreate() {<br />        super.onCreate();<br />    }<br />}</pre>

for usage in activity, you can look this example activity class:
</br>
<pre>package com.maedi.example.easy.service;<br /><br />import android.annotation.SuppressLint;<br />import android.app.Activity;<br />import android.content.Intent;<br />import android.os.Bundle;<br />import android.support.v4.app.FragmentActivity;<br />import android.support.v7.widget.RecyclerView;<br />import android.view.KeyEvent;<br />import android.view.View;<br />import android.widget.Toast;<br />import com.maedi.soft.ino.base.BuildActivity;<br />import com.maedi.soft.ino.base.annotation.BuilderAnnotations.PostFieldParam;<br />import com.maedi.soft.ino.base.annotation.processor.DataProcessor;<br />import com.maedi.soft.ino.base.func_interface.ActivityListener;<br />import com.maedi.soft.ino.base.func_interface.ServicesListener;<br />import com.maedi.soft.ino.base.presenter.ApiServicePresent;<br />import com.maedi.soft.ino.base.store.MapDataParcelable;<br />import com.maedi.soft.ino.base.utils.CallApiService;<br />import com.maedi.soft.ino.base.utils.EasyData;<br />import com.maedi.soft.ino.base.view.UniversalSheet;<br />import butterknife.BindView;<br />import butterknife.ButterKnife;<br />import butterknife.OnClick;<br />import timber.log.Timber;<br /><br />public class LaunchActivity extends BuildActivity&lt;View&gt; implements ActivityListener&lt;Integer&gt;, ServicesListener {<br /><br />    private final String TAG = this.getClass().getName();<br /><br />    private FragmentActivity f;<br /><br />    @BindView(R.id.universal_sheet)<br />    UniversalSheet universalSheet;<br /><br />    @BindView(R.id.listView)<br />    RecyclerView listView;<br /><br />    @OnClick(R.id.post1)<br />    public void FetchWithQuery() {<br />        hitFetchWithQuery("exampleFetchWithQuery");<br />    }<br /><br />    @OnClick(R.id.post2)<br />    public void FetchWithRequestBody() {<br />        String[] valueField = new String[]{"1"};<br />        hitFetchWithRequestBody("exampleFetchWithRequestBody", valueField);<br />    }<br /><br />    private ApiServicePresent apiServicePresent;<br /><br />    private void hitFetchWithQuery(String methodName)<br />    {<br />        CallApiService callApiService = new CallApiService.BuildFunction().showLoadingContentDialog(true, new CallApiService.BuildFunction.CommPropertyLoadingContentDialogListener() {<br />            @Override<br />            public boolean cancelableOnTouchOutside() {<br />                return false;<br />            }<br /><br />            @Override<br />            public boolean cancelable() {<br />                return false;<br />            }<br />        }).build(this);<br />        callApiService.fetchDataResponse(f);<br /><br />        apiServicePresent = new ApiServicePresent(callApiService);<br />        apiServicePresent.sendRequestData_withRetrofit_RxJava(RetrofitApiService.class, methodName, "1");<br />    }<br /><br />    private void hitFetchWithRequestBody(String methodName, String[] valueField)<br />    {<br />        CallApiService callApiService = new CallApiService.BuildFunction().showLoadingContentDialog(true, new CallApiService.BuildFunction.CommPropertyLoadingContentDialogListener() {<br />            @Override<br />            public boolean cancelableOnTouchOutside() {<br />                return false;<br />            }<br /><br />            @Override<br />            public boolean cancelable() {<br />                return false;<br />            }<br />        }).setPostDataParamListener(<br />                new CallApiService.CommPostDataParamCallApiServiceListener() {<br /><br />                    @Override<br />                    public Object setDataParam(<br />                            @PostFieldParam(fieldParam={<br />                                    "userId"<br />                            }) Object param<br />                    ) {<br />                        DataProcessor.CommDataProcessor listenDataProcessor = new DataProcessor.CommDataProcessor() {<br /><br />                            @Override<br />                            public EasyData bindServiceType() {<br />                                return EasyData.BIND_METHOD_PARAMETER;<br />                            }<br /><br />                            @Override<br />                            public boolean buildString() {<br />                                return false;<br />                            }<br /><br />                            @Override<br />                            public boolean buildJsonObject() {<br />                                return true;<br />                            }<br /><br />                            @Override<br />                            public boolean buildMultipart() {<br />                                return false;<br />                            }<br />                        };<br />                        DataProcessor dataProcessor = new DataProcessor(listenDataProcessor);<br />                        return dataProcessor.getObjectFieldFromProcessor(this.getClass(), param);<br />                    }<br /><br />                }).build(this);<br /><br />        callApiService.fetchDataResponse(f);<br />        String postFieldData = (String)callApiService.buildDataParam(valueField);<br />        Timber.d(TAG+" get dataForRequest - "+postFieldData);<br /><br />        apiServicePresent = new ApiServicePresent(callApiService);<br />        apiServicePresent.sendRequestData_withRetrofit_RequestBody(RetrofitApiService.class, methodName, postFieldData);<br />    }<br /><br />    @Override<br />    public int baseContentView() {<br />        return R.layout.activity_main;<br />    }<br /><br />    @Override<br />    public ActivityListener createListenerForActivity() {<br />        return this;<br />    }<br /><br />    @Override<br />    public void onCreateActivity(Bundle savedInstanceState) {<br />        f = this;<br />        ButterKnife.bind(this);<br />    }<br /><br />    @Override<br />    public void onActivityResume() {<br /><br />    }<br /><br />    @Override<br />    public void onActivityPause() {<br /><br />    }<br /><br />    @Override<br />    public void onActivityStop() {<br /><br />    }<br /><br />    @Override<br />    public void onActivityDestroy() {<br /><br />    }<br /><br />    @Override<br />    public boolean onActivityKeyDown(int keyCode, KeyEvent event) {<br />        return false;<br />    }<br /><br />    @Override<br />    public void onActivityFinish() {<br /><br />    }<br /><br />    @Override<br />    public void onActivityRestart() {<br /><br />    }<br /><br />    @Override<br />    public void onActivitySaveInstanceState(Bundle outState) {<br /><br />    }<br /><br />    @Override<br />    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {<br /><br />    }<br /><br />    @Override<br />    public void onActivityRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {<br /><br />    }<br /><br />    @Override<br />    public void onActivityMResult(int requestCode, int resultCode, Intent data) {<br /><br />    }<br /><br />    @Override<br />    public boolean onActivitySecure() {<br />        return false;<br />    }<br /><br />    @Override<br />    public int setPermission() {<br />        return 0;<br />    }<br /><br />    @Override<br />    public boolean setAnalytics() {<br />        return false;<br />    }<br /><br />    @Override<br />    public void onBuildActivityCreated() {<br /><br />    }<br /><br />    @Override<br />    public void setAnimationOnOpenActivity(Integer firstAnim, Integer secondAnim) {<br />        overridePendingTransition(firstAnim, secondAnim);<br />    }<br /><br />    @Override<br />    public void setAnimationOnCloseActivity(Integer firstAnim, Integer secondAnim) {<br />        overridePendingTransition(firstAnim, secondAnim);<br />    }<br /><br />    @Override<br />    public View setViewTreeObserverActivity() {<br />        return null;<br />    }<br /><br />    @Override<br />    public void getViewTreeObserverActivity() {<br /><br />    }<br /><br />    @Override<br />    public Intent setResultIntent() {<br />        return null;<br />    }<br /><br />    @Override<br />    public String getTagDataIntentFromActivity() {<br />        return null;<br />    }<br /><br />    @Override<br />    public void getMapDataIntentFromActivity(MapDataParcelable parcleable) {<br /><br />    }<br /><br />    @Override<br />    public MapDataParcelable setMapDataIntentToNextActivity(MapDataParcelable parcleable) {<br />        return null;<br />    }<br /><br />    @Override<br />    public void successPostGetData(Object data) {<br />        Timber.d(TAG + " - ON SUCCESS FETCH DATA [ RETROFIT ] - "+data);<br />        Toast.makeText(this, "SUCCESS FETCH DATA: "+data.toString(), Toast.LENGTH_SHORT).show();<br />    }<br /><br />    @Override<br />    public void errorPostGetData(Object data) {<br />        Timber.d(TAG + " - ON ERROR FETCH DATA [ RETROFIT ] - "+data);<br />        Toast.makeText(this, "ERROR FETCH DATA: "+data.toString(), Toast.LENGTH_SHORT).show();<br />    }<br /><br />    @Override<br />    public boolean verifyDataNonNullOrZero(boolean isDataHasNullOrZero) {<br />        return false;<br />    }<br /><br />}</pre>

if you want to upload image, you can call like this:
</br>
<pre>private void uploadFiles(String methodName, List&lt;?&gt; listMultipart)<br />{<br /><br />    CallApiService callApiService = new CallApiService.BuildFunction()<br />            .setPostDataMultipartListener(<br />                    new CallApiService.CommPostDataMultipartCallApiServiceListener() {<br /><br />                        @Override<br />                        public Object setDataMultipart(<br />                                @MultipartParam(value={<br />                                        "userId",<br />                                        "image_files"<br />                                }, type={<br />                                        "text/plain",<br />                                        "image/*"}) Object param<br />                        ) {<br />                            DataProcessor.CommDataProcessor listenDataProcessor = new DataProcessor.CommDataProcessor() {<br /><br />                                @Override<br />                                public EasyData bindServiceType() {<br />                                    return EasyData.BIND_METHOD_PARAMETER;<br />                                }<br /><br />                                @Override<br />                                public boolean buildString() {<br />                                    return false;<br />                                }<br /><br />                                @Override<br />                                public boolean buildJsonObject() {<br />                                    return false;<br />                                }<br /><br />                                @Override<br />                                public boolean buildMultipart() {<br />                                    return true;<br />                                }<br />                            };<br />                            DataProcessor dataProcessor = new DataProcessor(listenDataProcessor);<br />                            return dataProcessor.getObjectFieldFromProcessor(this.getClass(), param);<br />                        }<br /><br />                    })<br />            .setCommFileUploadListener(<br />                    new CallApiService.CommFileUploadCallApiServiceListener()<br />                    {<br />                        @Override<br />                        public void startUploadFiles() {<br />                        }<br /><br />                        @Override<br />                        public void showThumbnail(Object selectedFile) {<br /><br />                        }<br /><br />                        @Override<br />                        public void showErrorMessage(String message) {<br /><br />                        }<br /><br />                        @Override<br />                        public void uploadCompleted(String responses) {<br /><br />                        }<br /><br />                        @Override<br />                        public void setUploadProgress(int currentProgress, int totalProgress, int numberFileUploaded) {<br />                            runOnUiThread(new Runnable() {<br />                                @Override<br />                                public void run() {<br />                                    //set progress bar here<br />                                    //progressBar.setProgress(totalProgress);<br />                                }<br />                            });<br />                        }<br />    }).build(this);<br /><br />    callApiService.fetchDataResponse(this);<br />    Map mapMultipart =  (Map)callApiService.buildDataMultipart(listMultipart);<br />    apiServicePresent = new ApiServicePresent(callApiService);<br />    apiServicePresent.sendRequestData_withRetrofit_UploadFile(RetrofitApiService.class, methodName, mapMultipart);<br /><br />}</pre>
<h3>Universal Sheet - Release 1.0.1</h3>
Add universal sheet layout to your activity layout.
</br>notice: your top level layout must be not in LinearLayout.</br>
you can look this activity layout example:
</br>
</br>
<pre>&lt;com.maedi.soft.ino.base.view.FrameUniversalSheet<br />    android:id="@+id/layout_main_leftdialog"<br />    android:layout_width="match_parent"<br />    android:layout_height="match_parent"<br />    android:orientation="vertical"<br />    android:visibility="gone"&gt;<br /><br />    &lt;com.maedi.soft.ino.base.view.UniversalSheet<br />        android:id="@+id/layout_submain_leftdialog"<br />        android:layout_width="match_parent"<br />        android:layout_height="match_parent"<br />        android:orientation="vertical"<br />        app:uniSheetPos="bottom"<br />        app:uniSheetStartSize="200dp"<br />        app:uniSheetLength="400dp"<br />        app:uniSheetRadius="10dp"<br />        app:uniSheetBackgroundColor="@color/white"<br />        app:uniSheetHeader="@layout/your header layout"<br />        app:uniSheetHeaderSize="80dp"&gt;<br /><br />        &lt;android.support.v4.widget.NestedScrollView<br />            android:id="@+id/nested_sv"<br />            android:orientation="vertical"<br />            android:layout_width="match_parent"<br />            android:layout_height="wrap_content"&gt;<br /><br />        &lt;LinearLayout<br />            android:layout_width="match_parent"<br />            android:layout_height="wrap_content"<br />            android:orientation="vertical"&gt;<br />       &lt;/LinearLayout&gt;
<br />&lt;/android.support.v4.widget.NestedScrollView&gt;<br /><br />  &lt;/com.maedi.soft.ino.base.view.UniversalSheet&gt;<br /><br />&lt;/com.maedi.soft.ino.base.view.FrameUniversalSheet&gt;</pre>
And this example call Universal Sheet from your activity
<br/>
<pre><p>@BindView(R.id.universal_sheet)
UniversalSheet universalSheet;
<br />@OnClick(R.id.open)
public void open() {
&nbsp; &nbsp; universalSheet.opened();
}

you can implement Interface Universal Sheet<br />UniversalSheet.CommUniversalSheetListener callback = new UniversalSheet.CommUniversalSheetListener(){<br />	@Override&nbsp; public void onOpened() { }<br />	@Override&nbsp; public void onClosed() {&nbsp; }<br />&nbsp; &nbsp; 	@Override&nbsp; public void onHidden() {&nbsp; }<br />&nbsp; &nbsp; &nbsp; &nbsp; @Override&nbsp; public void getHeaderView(View v) {//v is your header view }
};<br />universalSheet.setUniversalSheetCallBack(callback);</p>
</pre>
<h3>Free Dialog - Release 1.0.2</h3>
Show default free dialog example:
<br />
<pre>new FreeDialog.BuilderFreeDialog()<br />        .setWindowSize(FreeDialog.WindowSize.WRAP_CONTENT)<br />        .setLayout(FreeDialog.Layout.DEFAULT)<br />        .setTextPositifButton("Ok")<br />        .setTextNegativeButton("Cancel")<br />        .setBodyText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +<br />                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +<br />                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris")<br />        .setActionButtonListener(new FreeDialog.CommFreeDialogActionButtonListener() {<br />            @Override<br />            public void ok() { }<br />            @Override<br />            public void cancel() { }<br /><br />            @Override<br />            public void clickIcon(FreeDialog dialog) {<br />                dialog.dismiss();<br />            }<br />        })<br />        .build().show(getSupportFragmentManager(), null);</pre>
<br />
You can see more detailed examples in the <p><a href="https://github.com/maedilaziman/innosoft-service/tree/master/app/src/main/java/com/maedi/example/easy/service">sample app</a></p>
<br />
That's it!
</br>
<h2>License</h2>
<pre><code>Copyright 2019 Maedi Laziman
</br>
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
</br>
   http://www.apache.org/licenses/LICENSE-2.0
</br>
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.</code></pre>

