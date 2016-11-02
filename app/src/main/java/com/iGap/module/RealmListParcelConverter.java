package com.iGap.module;

import io.realm.RealmList;
import io.realm.RealmObject;
import org.parceler.converter.CollectionParcelConverter;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 11/1/2016.
 */

// Abstract class for working with RealmLists
public abstract class RealmListParcelConverter<T extends RealmObject>
    extends CollectionParcelConverter<T, RealmList<T>> {
    @Override public RealmList<T> createCollection() {
        return new RealmList<T>();
    }
}