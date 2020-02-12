package net.iGap.module.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public abstract class BaseBottomSheet extends BottomSheetDialogFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //expand bottom sheet and fix problem in landscape or when dialog has edit text
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            if (dialog == null) return;
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet == null) return;
            BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setPeekHeight(0);
        });
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), getTheme());
        dialog.setOnShowListener(dialog1 -> {

            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet == null) return;
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            if (behavior == null) return;
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int state) {
                    //dismiss dialog when collapsed
                    if (state == BottomSheetBehavior.STATE_COLLAPSED) dialog.dismiss();
                }

                @Override
                public void onSlide(@NonNull View view, float v) {

                }
            });

        });
        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        //when need to saveInstance add some bundle : Link below
        //https://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit
        //super.onSaveInstanceState(outState);
    }
}
