package net.iGap.dialog.topsheet;
/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import net.iGap.R;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.BottomSheetListAdapter;

import java.util.List;

/**
 * Created by andrea on 23/08/16.
 */
public class TopSheetDialog extends AppCompatDialog {

    private List<String> itemList;
    private int range;
    private BottomSheetItemClickCallback bottomSheetItemClickCallback;

    public TopSheetDialog(@NonNull Context context) {
        super(context, getThemeResId(context, 0));
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public TopSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, getThemeResId(context, theme));
        // We hide the title bar for any style configuration. Otherwise, there will be a gap
        // above the bottom sheet when it is expanded.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    protected TopSheetDialog(@NonNull Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(wrapInTopSheet());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(wrapInTopSheet());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(wrapInTopSheet());
    }

    public TopSheetDialog setListData(List<String> listItem, int range, BottomSheetItemClickCallback bottomSheetItemClickCallback) {
        this.itemList = listItem;
        this.range = range;
        this.bottomSheetItemClickCallback = bottomSheetItemClickCallback;
        super.setContentView(wrapInTopSheet());
        return this;
    }

    private View wrapInTopSheet() {
        final CoordinatorLayout coordinator = (CoordinatorLayout) View.inflate(getContext(), R.layout.top_sheet_dialog, null);
        FrameLayout topSheet = coordinator.findViewById(R.id.design_top_sheet);
        TopSheetBehavior<FrameLayout> topSheetBehavior = TopSheetBehavior.from(topSheet);
        topSheetBehavior.setTopSheetCallback(mTopSheetCallback);
        RecyclerView recyclerView = topSheet.findViewById(R.id.bottomSheetList);
        recyclerView.setAdapter(new BottomSheetListAdapter(itemList, range, position -> {
            dismiss();
            bottomSheetItemClickCallback.onClick(position);
        }));
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        coordinator.findViewById(R.id.top_sheet_touch_outside).setOnClickListener(
                view -> {
                    if (isShowing()) {
                        cancel();
                    }
                });
        return coordinator;
    }

    @Override
    public void show() {
        super.show();
        //topSheetBehavior.setState(TopSheetBehavior.STATE_EXPANDED);
    }

    private static int getThemeResId(Context context, int themeId) {
        if (themeId == 0) {
            // If the provided theme is 0, then retrieve the dialogTheme from our theme
            TypedValue outValue = new TypedValue();
            if (context.getTheme().resolveAttribute(android.support.design.R.attr.bottomSheetDialogTheme, outValue, true)) {
                themeId = outValue.resourceId;
            } else {
                // bottomSheetDialogTheme is not provided; we default to our light theme
                themeId = R.style.Theme_Design_TopSheetDialog;
            }
        }
        return themeId;
    }

    private TopSheetBehavior.TopSheetCallback mTopSheetCallback
            = new TopSheetBehavior.TopSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View topSheet, @BottomSheetBehavior.State int newState) {
            if (newState == TopSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View topSheet, float slideOffset, @Nullable Boolean isOpening) {
        }
    };


}
