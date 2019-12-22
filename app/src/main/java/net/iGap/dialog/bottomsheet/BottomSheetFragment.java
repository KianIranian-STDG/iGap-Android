package net.iGap.dialog.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.BottomSheetListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private List<String> itemList;
    private int range;
    private String title = null;
    private BottomSheetItemClickCallback bottomSheetItemClickCallback;

    public BottomSheetFragment setData(List<String> itemListId, int range, BottomSheetItemClickCallback bottomSheetItemClickCallback) {
        this.itemList = itemListId;
        this.range = range;
        this.bottomSheetItemClickCallback = bottomSheetItemClickCallback;
        return this;
    }

    public BottomSheetFragment setListDataWithResourceId(Context context, List<Integer> itemListId, int range, BottomSheetItemClickCallback bottomSheetItemClickCallback) {
        this.itemList = new ArrayList<>();
        for (int i = 0; i < itemListId.size(); i++) {
            this.itemList.add(context.getString(itemListId.get(i)));
        }
        this.range = range;
        this.bottomSheetItemClickCallback = bottomSheetItemClickCallback;
        return this;
    }

    public BottomSheetFragment setTitle(String title) {
        this.title = title;
        return this;
    }

    @SuppressLint("ResourceType")
    public BottomSheetFragment setTitle(@IdRes int title) {
        this.title = getString(title);
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);

        BottomSheetListAdapter bottomSheetListAdapter = new BottomSheetListAdapter(itemList, range, position -> {
            dismiss();
            bottomSheetItemClickCallback.onClick(position);
        });
        binding.bottomSheetList.setAdapter(bottomSheetListAdapter);
        if (title != null) {
            binding.title.setText(title);
            binding.title.setVisibility(View.VISIBLE);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), getTheme());
        dialog.setOnShowListener(dialog1 -> {
            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null)
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

        });
        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //No call for super(). Bug on API Level > 11.
//        super.onSaveInstanceState(outState);
    }
}
