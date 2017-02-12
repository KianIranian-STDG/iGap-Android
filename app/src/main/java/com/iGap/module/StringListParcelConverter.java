package com.iGap.module;

import android.os.Parcel;
import com.iGap.activities.ActivityChat;
import com.iGap.realm.RealmString;
import org.parceler.Parcels;

// Specific class for a RealmList<Bar> field
public class StringListParcelConverter extends RealmListParcelConverter<RealmString> {

    @Override
    public void itemToParcel(RealmString input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public RealmString itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(ActivityChat.class.getClassLoader()));
    }
}