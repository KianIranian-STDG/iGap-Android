package net.iGap.camera;

public interface OnPhotoEditorListener {
    void onAddViewListener(int numberOfAddedViews);

    void onRemoveViewListener(int numberOfAddedViews);

    void onStartViewChangeListener();

    void onStopViewChangeListener();
}
