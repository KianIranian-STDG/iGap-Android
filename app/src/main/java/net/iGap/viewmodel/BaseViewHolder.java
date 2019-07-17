package net.iGap.viewmodel;

public interface BaseViewHolder {
    default void onCreateViewModel() {
    }

    default void onStart() {
    }

    default void onDestroy() {
    }

    default void onPause() {
    }

    default void onResume() {
    }
}
