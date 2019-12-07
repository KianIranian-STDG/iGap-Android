package net.iGap.fragments.emoji.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.R;
import net.iGap.helper.HelperCalander;

public class StickerDialogFragment extends BottomSheetDialogFragment {
    private ProgressBar progressBar;
    private StickerAdapter adapter;
    private StickerDialogViewModel viewModel;
    private StructIGStickerGroup stickerGroup;

    private TextView addOrRemoveTv;
    private TextView groupNameTv;

    private String TAG = "abbasiSticker";

    public static StickerDialogFragment newInstance(StructIGStickerGroup stickerGroup) {
        StickerDialogFragment dialogAddSticker = new StickerDialogFragment();
        dialogAddSticker.stickerGroup = stickerGroup;
        return dialogAddSticker;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new StickerDialogViewModel(stickerGroup);
        adapter = new StickerAdapter();
        return inflater.inflate(R.layout.dialog_add_sticker, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView stickerRecyclerView = view.findViewById(R.id.rv_stickerDialog);
        progressBar = view.findViewById(R.id.progress_sticker);
        groupNameTv = view.findViewById(R.id.tv_stickerDialog_groupName);
        addOrRemoveTv = view.findViewById(R.id.tv_stickerDialog_add);

        stickerRecyclerView.setAdapter(adapter);
        stickerRecyclerView.setHasFixedSize(true);

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), visibility -> progressBar.setVisibility(visibility));

        viewModel.getStickersMutableLiveData().observe(getViewLifecycleOwner(), structIGStickerGroup -> {
            if (!stickerGroup.hasData())
                stickerGroup = structIGStickerGroup;

            adapter.setIgStickers(structIGStickerGroup.getStickers());
            groupNameTv.setText(structIGStickerGroup.getName());
        });

        viewModel.getAddOrRemoveStickerLiveData().observe(getViewLifecycleOwner(), resourceId -> {
            addOrRemoveTv.setText(getResources().getString(resourceId, HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stickerGroup.getStickersSize()))));
            addOrRemoveTv.setVisibility(View.VISIBLE);
        });

        viewModel.getCloseDialogMutableLiveData().observe(getViewLifecycleOwner(), close -> dismiss());

        addOrRemoveTv.setOnClickListener(v -> viewModel.onAddOrRemoveStickerClicked());

        viewModel.getSticker();
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }
}