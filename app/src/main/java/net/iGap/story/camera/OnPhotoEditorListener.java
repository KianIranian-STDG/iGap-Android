package net.iGap.story.camera;

public interface OnPhotoEditorListener {
    void onAddViewListener(int numberOfAddedViews);

    void onRemoveViewListener(int numberOfAddedViews);

    void onStartViewChangeListener();

    void onStopViewChangeListener();
}
