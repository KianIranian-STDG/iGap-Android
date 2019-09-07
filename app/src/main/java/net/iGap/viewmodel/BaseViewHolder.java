package net.iGap.viewmodel;

import net.iGap.fragments.BaseFragment;

public interface BaseViewHolder {
    default void onCreateViewModel() {
    }

    default void onStartFragment(BaseFragment fragment) {
    }

    default void onDestroyFragment(BaseFragment fragment) {
    }

    default void onPauseFragment(BaseFragment fragment) {
    }

    default void onResumeFragment(BaseFragment fragment) {
    }

    default void onCreateFragment(BaseFragment fragment) {
    }

    default void onStartFragment() {
    }

    default void onDestroyFragment() {
    }

    default void onPauseFragment() {
    }

    default void onResumeFragment() {
    }

    default void onCreateFragment() {
    }


    default void onDestroyViewModel() {
    }
}
