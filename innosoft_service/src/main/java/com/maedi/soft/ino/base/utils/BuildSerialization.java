/*
 * Copyright (c) 8/12/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base.utils;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import timber.log.Timber;

@SuppressLint("TimberArgCount")
public class BuildSerialization<E> implements Serializable {

    private final String TAG = "BuildSerialization";

    public interface CommBuildSerialization<E>
    {
        public void getDeserialization(E data);
    }

    private CommBuildSerialization<E> listener;

    public void setListener(CommBuildSerialization<E> listen)
    {
        this.listener = listen;
    }

    private FragmentActivity f;

    public BuildSerialization(FragmentActivity fr)
    {
        this.f = fr;
    }

    public void serialization(E data, Object filename) {
        try {
            File cDir = f.getBaseContext().getCacheDir();
            FileOutputStream fileOut = new FileOutputStream(new File(cDir.getPath() + "/" + filename));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            out.close();
            fileOut.close();

        } catch (IOException i) {
            i.printStackTrace();
            Timber.d(TAG+" error set serialization IO "+i.getMessage());
        }
    }

    public void deserialization(E typeFunc, Object filename) {
        try {
            File cDir = f.getBaseContext().getCacheDir();
            FileInputStream fileIn = new FileInputStream(new File(cDir.getPath() + "/" + filename));
            ObjectInputStream in = new ObjectInputStream(fileIn);
            typeFunc = (E) in.readObject();

            listener.getDeserialization((E) typeFunc);
            in.close();
            fileIn.close();

        } catch (IOException i) {
            i.printStackTrace();
            Timber.d(TAG+" error get deserialization IO "+i.getMessage());
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    }
}
