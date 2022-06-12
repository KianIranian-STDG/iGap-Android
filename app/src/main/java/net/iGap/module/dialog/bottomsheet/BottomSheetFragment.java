package net.iGap.module.dialog.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.iGap.R;
import net.iGap.databinding.FragmentBottomSheetDialogBinding;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.module.dialog.BottomSheetItemClickCallback;
import net.iGap.module.dialog.BottomSheetListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends BaseBottomSheet {

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.bottomSheetList.setLayoutManager(linearLayoutManager);
        binding.bottomSheetList.setAdapter(bottomSheetListAdapter);
        if (title != null) {
            binding.title.setText(title);
            binding.title.setTextColor(Theme.getColor(Theme.key_icon));
            binding.title.setVisibility(View.VISIBLE);
        }
        //binding.getRoot().setBackgroundColor(Theme.getColor(Theme.key_popup_background));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout mainContainer = view.findViewById(R.id.mainContainer);
        mainContainer.setBackgroundColor(Theme.getColor(Theme.key_popup_background));
        View lineViewTop = view.findViewById(R.id.lineViewTop);
        lineViewTop.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bottom_sheet_dialog_line), getContext(), Theme.getColor(Theme.key_line)));
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

}
