package net.iGap.fragments.emoji.add;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.iGap.R;
import net.iGap.helper.HelperCalander;

public class StickerDialogFragment extends BottomSheetDialogFragment {
    private View progressBar;
    private StickerAdapter adapter;
    private StickerDialogViewModel viewModel;
    private StructIGStickerGroup stickerGroup;

    private TextView addOrRemoveTv;
    private TextView groupNameTv;
    private ImageView previewIv;

    private OnStickerDialogListener listener;

    private String TAG = "abbasiStickerDialog";

    public static StickerDialogFragment newInstance(StructIGStickerGroup stickerGroup) {
        StickerDialogFragment dialogAddSticker = new StickerDialogFragment();
        dialogAddSticker.stickerGroup = stickerGroup;
        return dialogAddSticker;
    }

    public void setListener(OnStickerDialogListener listener) {
        this.listener = listener;
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
        progressBar = view.findViewById(R.id.fl_stickerDialog_progressContainer);
        groupNameTv = view.findViewById(R.id.tv_stickerDialog_groupName);
        addOrRemoveTv = view.findViewById(R.id.tv_stickerDialog_add);
        previewIv = view.findViewById(R.id.iv_stickerDialog_preview);

        stickerRecyclerView.setAdapter(adapter);

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

        adapter.setListener(structIGSticker -> viewModel.onStickerInListModeClick(structIGSticker));

        viewModel.getOpenPreviewViewLiveData().observe(getViewLifecycleOwner(), structIGSticker -> {
            if (structIGSticker != null && previewIv.getVisibility() == View.GONE) {

                if (getContext() != null)
                    Glide.with(getContext()).load(structIGSticker.getPath()).into(previewIv);

                previewIv.animate().alpha(1.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        previewIv.setVisibility(View.VISIBLE);
                    }
                });

                addOrRemoveTv.setText(getResources().getString(R.string.send));
            } else if (previewIv.getVisibility() == View.VISIBLE) {

                previewIv.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        previewIv.setVisibility(View.GONE);
                    }
                });
            }
        });

        viewModel.getSendMessageLiveData().observe(getViewLifecycleOwner(), structIGSticker -> {
            if (listener != null) {
                listener.sendMessage(structIGSticker);
                dismiss();
            }
        });

        previewIv.setOnClickListener(v -> viewModel.onPreviewImageClicked());

        viewModel.getCloseDialogMutableLiveData().observe(getViewLifecycleOwner(), close -> dismiss());

        addOrRemoveTv.setOnClickListener(v -> viewModel.onAddOrRemoveStickerClicked());

        viewModel.getSticker();
    }

    public interface OnStickerDialogListener {
        void sendMessage(StructIGSticker structIGSticker);
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }
}