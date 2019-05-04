package net.iGap.dialog.bottomsheet;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.BottomSheetListAdapter;

import java.util.List;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private List<String> itemList;
    private int range;
    private BottomSheetItemClickCallback bottomSheetItemClickCallback;

    public BottomSheetFragment setData(List<String> itemListId,int range, BottomSheetItemClickCallback bottomSheetItemClickCallback) {
        this.itemList = itemListId;
        this.range = range;
        this.bottomSheetItemClickCallback = bottomSheetItemClickCallback;
        return this;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBottomSheetDialogBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_dialog, container, false);

        BottomSheetListAdapter bottomSheetListAdapter = new BottomSheetListAdapter(itemList, range, new BottomSheetItemClickCallback() {
            @Override
            public void onClick(int position) {
                dismiss();
                bottomSheetItemClickCallback.onClick(position);
            }
        });
        binding.bottomSheetList.setAdapter(bottomSheetListAdapter);
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
        return new BottomSheetDialog(requireContext(), getTheme());
    }
}
