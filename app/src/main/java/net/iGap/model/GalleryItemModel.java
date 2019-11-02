package net.iGap.model;

import androidx.annotation.Nullable;

public class GalleryItemModel {

    private int id ;
    private String address ;
    private String caption = "";
    private boolean isSelect ;
    private boolean isFolder ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof GalleryItemModel){
            return ((GalleryItemModel) obj).id == this.id ;
        }
        return super.equals(obj);
    }
}
