/*
 * Copyright (c) 8/12/2019. Maedi Laziman, Email: maedilaziman@gmail.com.
 */

package com.maedi.soft.ino.base;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressLint("TimberArgCount")
public class ListElement<E> extends ArrayList<E> implements Serializable {

    private E element;

    public ListElement()
    {
        super();
    }

    public ListElement(E element)
    {
        super();
        this.element = element;
    }

    @Override
    public boolean add(E elem)
    {
        super.add(elem);
        return true;
    }
}
