package net.iGap.module.dialog.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.module.dialog.BottomSheetItemClickCallback;
import net.iGap.module.dialog.BottomSheetListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends BaseBottomSheet {

    private List<String> itemList;
    private List<Integer> itemListInt;
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
        this.itemListInt = new ArrayList<>();
        this.itemListInt.addAll(itemListId);
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

        BottomSheetListAdapter bottomSheetListAdapter = new BottomSheetListAdapter(range, itemListInt, position -> {
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
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

}
