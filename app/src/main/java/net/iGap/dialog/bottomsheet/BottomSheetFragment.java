package net.iGap.dialog.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.BottomSheetListAdapter;

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
        if (G.isDarkTheme) {
            return R.style.BaseBottomSheetDialog;
        } else {
            return R.style.BaseBottomSheetDialogLight;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }
}
