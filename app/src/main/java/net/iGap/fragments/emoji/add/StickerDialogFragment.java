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

public class StickerDialogFragment extends BottomSheetDialogFragment {
    private ProgressBar progressBar;
    private StickerAdapter adapter;
    private String groupId = "";
    private String token = "";
    private StickerDialogViewModel viewModel;
    private TextView addOrRemoveTv;
    private boolean hasGroupInUserFavorite;
    private String TAG = "abbasiSticker";

    public static StickerDialogFragment newInstance(String groupId, String token, boolean hasGroupInUserFavorite) {
        StickerDialogFragment dialogAddSticker = new StickerDialogFragment();
        Bundle args = new Bundle();
        args.putString("GROUP_ID", groupId);
        args.putString("TOKEN", token);
        args.putBoolean("hasGroupOnFavorite", hasGroupInUserFavorite);
        dialogAddSticker.setArguments(args);
        return dialogAddSticker;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new StickerDialogViewModel();
        adapter = new StickerAdapter();
        return inflater.inflate(R.layout.dialog_add_sticker, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            groupId = getArguments().getString("GROUP_ID");
            token = getArguments().getString("TOKEN");
            hasGroupInUserFavorite = getArguments().getBoolean("hasGroupOnFavorite");
        }

        RecyclerView stickerRecyclerView = view.findViewById(R.id.rv_stickerDialog);
        progressBar = view.findViewById(R.id.progress_stricker);
        addOrRemoveTv = view.findViewById(R.id.tv_stickerDialog_add);
        stickerRecyclerView.setAdapter(adapter);
//        stickerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        stickerRecyclerView.setHasFixedSize(true);

        addOrRemoveTv.setText(hasGroupInUserFavorite ? "REMOVE" : "ADD STICKERS");

        addOrRemoveTv.setTextColor(hasGroupInUserFavorite ? getResources().getColor(R.color.red) : getResources().getColor(R.color.green));

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), visibility -> progressBar.setVisibility(visibility));
        viewModel.getCloseDialogMutableLiveData().observe(getViewLifecycleOwner(), close -> {
            if (close)
                dismiss();
        });

        viewModel.getSticker(groupId);

        viewModel.getStickersMutableLiveData().observe(getViewLifecycleOwner(), structIGStickers -> adapter.setIgStickers(structIGStickers));
        viewModel.getAddOrRemoveStickerLiveData().observe(getViewLifecycleOwner(), s -> addOrRemoveTv.setText(s));

        addOrRemoveTv.setOnClickListener(v -> viewModel.onAddOrRemoveStickerClicked(groupId, hasGroupInUserFavorite));
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }
}