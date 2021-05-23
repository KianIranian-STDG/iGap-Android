package net.iGap.story;

public interface OnPhotoEditorListener {
    void onAddViewListener(int numberOfAddedViews);

    void onRemoveViewListener(int numberOfAddedViews);

    void onStartViewChangeListener();

    void onStopViewChangeListener();
}
