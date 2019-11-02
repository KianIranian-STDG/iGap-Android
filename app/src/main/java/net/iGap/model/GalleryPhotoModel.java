package net.iGap.model;

import androidx.annotation.Nullable;

public class GalleryPhotoModel {

    private long id ;
    private String address ;
    private boolean isSelect ;

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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GalleryPhotoModel){
            return ((GalleryPhotoModel) obj).id == this.id ;
        }
        return super.equals(obj);
    }
}
