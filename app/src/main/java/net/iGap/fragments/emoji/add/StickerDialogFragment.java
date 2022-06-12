package net.iGap.fragments.emoji.add;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.R;
import net.iGap.adapter.items.cells.AnimatedStickerCell;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperCalander;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.viewmodel.sticker.StickerDialogViewModel;

public class StickerDialogFragment extends BaseBottomSheet {
    private StickerAdapter adapter;
    private StickerDialogViewModel viewModel;
    private StructIGStickerGroup stickerGroup;
    private boolean readOnlyChat;

    private TextView addOrRemoveTv;
    private TextView groupNameTv;
    private ImageView previewIv;
    private ProgressBar addOrRemoveProgressBar;
    private ProgressBar progressBar;
    private TextView retryView;
    private AnimatedStickerCell stickerCell;
    private View lineViewTop;

    private OnStickerDialogListener listener;

    private String TAG = "abbasiStickerDialog";

    public static StickerDialogFragment getInstance(StructIGStickerGroup stickerGroup, boolean readOnlyChat) {
        StickerDialogFragment dialogAddSticker = new StickerDialogFragment();
        dialogAddSticker.stickerGroup = stickerGroup;
        dialogAddSticker.readOnlyChat = readOnlyChat;
        return dialogAddSticker;
    }

    public static StickerDialogFragment getInstance(String groupId) {
        StickerDialogFragment dialogAddSticker = new StickerDialogFragment();
        dialogAddSticker.stickerGroup = new StructIGStickerGroup(groupId);
        return dialogAddSticker;
    }

    public void setListener(OnStickerDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new StickerDialogViewModel(stickerGroup);
        adapter = new StickerAdapter(true);
        return inflater.inflate(R.layout.dialog_add_sticker, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineViewTop = view.findViewById(R.id.lineViewTop);
        RecyclerView stickerRecyclerView = view.findViewById(R.id.rv_stickerDialog);
        progressBar = view.findViewById(R.id.fl_stickerDialog_progressContainer);
        groupNameTv = view.findViewById(R.id.tv_stickerDialog_groupName);
        groupNameTv.setTextColor(Theme.getColor(Theme.key_title_text));
        addOrRemoveTv = view.findViewById(R.id.tv_stickerDialog_add);
        addOrRemoveTv.setBackgroundColor(Theme.getColor(Theme.key_button_background));
        previewIv = view.findViewById(R.id.iv_stickerDialog_preview);
        stickerCell = view.findViewById(R.id.iv_stickerDialog_lottiePreview);
        retryView = view.findViewById(R.id.retryView);
        retryView.setTextColor(Theme.getColor(Theme.key_title_text));
        addOrRemoveProgressBar = view.findViewById(R.id.pb_stickerDialog_addOrRemove);

        lineViewTop.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bottom_sheet_dialog_line), getContext(), Theme.getColor(Theme.key_theme_color)));
        addOrRemoveProgressBar.getIndeterminateDrawable().setColorFilter(Theme.getColor(Theme.key_white), PorterDuff.Mode.SRC_IN);
        progressBar.getIndeterminateDrawable().setColorFilter(Theme.getColor(Theme.key_dark_theme_color), PorterDuff.Mode.SRC_IN);
        stickerRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5, GridLayoutManager.VERTICAL, false));
        stickerRecyclerView.setAdapter(adapter);

        stickerCell.setFailureListener(result -> Log.e(TAG, "setFailureListener: ", result));

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), visibility -> progressBar.setVisibility(visibility));

        viewModel.getStickersMutableLiveData().observe(getViewLifecycleOwner(), structIGStickerGroup -> {
            if (!stickerGroup.hasData())
                stickerGroup = structIGStickerGroup;

            adapter.setIgStickers(structIGStickerGroup.getStickers());
            groupNameTv.setText(structIGStickerGroup.getName());
        });

        viewModel.getAddOrRemoveStickerLiveData().observe(getViewLifecycleOwner(), resourceId -> {
            addOrRemoveTv.setText(getResources().getString(resourceId, HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stickerGroup.getStickersSize())) : String.valueOf(stickerGroup.getStickersSize())));
            addOrRemoveTv.setVisibility(View.VISIBLE);
        });

        adapter.setListener(new StickerAdapter.AddStickerDialogListener() {
            @Override
            public void onStickerClick(StructIGSticker structIGSticker) {
                viewModel.onStickerInListModeClick(structIGSticker);
            }

            @Override
            public void onStickerLongClick(StructIGSticker structIGSticker) {

            }
        });

        viewModel.getOpenPreviewViewLiveData().observe(getViewLifecycleOwner(), structIGSticker -> {
            // TODO: 12/8/19  must create custom dialog for add just once view for handle stickers view type
            if (structIGSticker != null && previewIv.getVisibility() == View.GONE) {
                if (structIGSticker.getType() == StructIGSticker.ANIMATED_STICKER) {
                    stickerCell.playAnimation(structIGSticker.getPath());

                    stickerCell.animate().alpha(1.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            stickerCell.setVisibility(View.VISIBLE);
                        }
                    });

                } else if (structIGSticker.getType() == StructIGSticker.NORMAL_STICKER) {
                    if (getContext() != null)
                        Glide.with(getContext()).load(structIGSticker.getPath()).into(previewIv);

                    previewIv.animate().alpha(1.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            previewIv.setVisibility(View.VISIBLE);
                        }
                    });
                }

                if (readOnlyChat) {
                    addOrRemoveTv.setVisibility(View.GONE);
                } else {
                    addOrRemoveTv.setText(getResources().getString(R.string.send));
                    addOrRemoveTv.setVisibility(View.VISIBLE);
                }
            } else if (previewIv.getVisibility() == View.VISIBLE) {

                previewIv.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        previewIv.setVisibility(View.GONE);
                    }
                });
            } else if (stickerCell.getVisibility() == View.VISIBLE) {

                stickerCell.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        stickerCell.setVisibility(View.GONE);
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

        viewModel.getAddOrRemoveProgressLiveData().observe(getViewLifecycleOwner(), visibility -> addOrRemoveProgressBar.setVisibility(visibility));
        viewModel.getRetryViewLiveData().observe(getViewLifecycleOwner(), visibility -> retryView.setVisibility(visibility));

        previewIv.setOnClickListener(v -> viewModel.onPreviewImageClicked());
        stickerCell.setOnClickListener(v -> viewModel.onPreviewImageClicked());

        viewModel.getCloseDialogMutableLiveData().observe(getViewLifecycleOwner(), close -> dismiss());

        addOrRemoveTv.setOnClickListener(v -> viewModel.onAddOrRemoveStickerClicked());
        retryView.setOnClickListener(v -> viewModel.onRetryViewClicked());

        viewModel.getSticker();
    }

    public interface OnStickerDialogListener {
        void sendMessage(StructIGSticker structIGSticker);
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}