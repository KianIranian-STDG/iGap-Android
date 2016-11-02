package com.iGap.module;

import android.os.Parcel;
import com.iGap.realm.RealmString;
import com.nightonke.boommenu.Bar;
import org.parceler.Parcels;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 11/1/2016.
 */

// Specific class for a RealmList<Bar> field
public class StringListParcelConverter extends RealmListParcelConverter<RealmString> {

    @Override public void itemToParcel(RealmString input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override public RealmString itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(Bar.class.getClassLoader()));
    }
}