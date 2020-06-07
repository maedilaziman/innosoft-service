/*
 * Copyright (c) 8/25/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.func_interface;

import android.content.Intent;
import android.view.View;

import com.maedi.soft.ino.base.store.MapDataParcelable;

/**
 * Created by Maedi on 5/13/2019.
 */

public interface ActivityListener<T> {

    void setAnimationOnOpenActivity(T firstAnim, T secondAnim);

    void setAnimationOnCloseActivity(T firstAnim, T secondAnim);

    //void setDataArgs(Intent args, Parcelable parcel);

    View setViewTreeObserverActivity();

    void getViewTreeObserverActivity();

    Intent setResultIntent();

    String getTagDataIntentFromActivity();

    void getMapDataIntentFromActivity(MapDataParcelable parcleable);

    MapDataParcelable setMapDataIntentToNextActivity(MapDataParcelable parcleable);

}
