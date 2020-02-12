package net.iGap.observers.interfaces;

import android.view.View;

public interface ToolbarListener {

    default void onLeftIconClickListener(View view) {
    }

    default void onSecondLeftIconClickListener(View view) {
    }

    default void onSearchClickListener(View view) {
    }

    default void onBtnClearSearchClickListener(View view) {
    }

    default void onSmallAvatarClickListener(View view) {
    }

    default void onBigAvatarClickListener(View view) {
    }

    default void onChatAvatarClickListener(View view) {
    }

    default void onRightIconClickListener(View view) {
    }

    default void onSecondRightIconClickListener(View view) {
    }

    default void onThirdRightIconClickListener(View view) {
    }

    default void onFourthRightIconClickListener(View view) {
    }

    default void onSearchTextChangeListener(View view, String text) {
    }

    default void onSearchBoxClosed() {
    }

    default void onToolbarTitleClickListener(View view) {
    }
}
