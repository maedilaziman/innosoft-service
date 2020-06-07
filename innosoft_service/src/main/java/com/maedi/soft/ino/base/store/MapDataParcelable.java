/*
 * Copyright (c) 8/25/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.store;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class MapDataParcelable<E1, E2> implements Parcelable {

    private Map<E1, E2> map;

    public static final Creator<MapDataParcelable> CREATOR = new Creator<MapDataParcelable>() {
        @Override
        public MapDataParcelable createFromParcel(Parcel in) {
            return new MapDataParcelable(in);
        }

        @Override
        public MapDataParcelable[] newArray(int size) {
            return new MapDataParcelable[size];
        }
    };

    public MapDataParcelable(){
        //this for create bundle data
        map = new HashMap<E1, E2>();
    }

    protected MapDataParcelable(Parcel in) {
        //this for read data after we bundle data
        //map must be initial again for read data
        map = new HashMap<E1, E2>();
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(map.size());
        for (E1 s : map.keySet()) {
            dest.writeValue(s);
            dest.writeValue(map.get(s));
        }
    }

    public void readFromParcel(Parcel in) {
        int count = in.readInt();
        E1 keyVal;
        E2 mapVal;
        for (int i = 0; i < count; i++) {
            keyVal = (E1) in.readValue(null);
            mapVal = (E2) in.readValue(null);
            map.put(keyVal, mapVal);
        }
    }

    public E2 get(E1 key) {
        return map.get(key);
    }

    public void put(E1 key, E2 value) {
        map.put(key, value);
    }

    public Map<E1, E2> getMap(){
        return map;
    }
}
