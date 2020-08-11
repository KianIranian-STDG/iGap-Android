package net.iGap.observers.interfaces;

public interface GalleryItemListener {

    void onItemClicked(String name, String id);

    void onMultiSelect(int size);
}
