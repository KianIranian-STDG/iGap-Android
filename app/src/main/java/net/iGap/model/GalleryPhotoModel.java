package net.iGap.model;

import android.util.Log;

import androidx.annotation.Nullable;

public class GalleryPhotoModel {

    private long id ;
    private String address ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GalleryPhotoModel){
            return ((GalleryPhotoModel) obj).getId() == this.id ;
        }
        return super.equals(obj);
    }
}
