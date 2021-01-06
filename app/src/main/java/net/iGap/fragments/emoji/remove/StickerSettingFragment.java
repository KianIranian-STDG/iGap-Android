package net.iGap.fragments.emoji.remove;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.fragments.emoji.add.StickerDialogFragment;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.observers.rx.ObserverFragment;
import net.iGap.viewmodel.sticker.RemoveStickerViewModel;

public class StickerSettingFragment extends ObserverFragment<RemoveStickerViewModel> {

    private RemoveStickerAdapter adapter;

    @Override
    public RemoveStickerViewModel getObserverViewModel() {
        return ViewModelProviders.of(this).get(RemoveStickerViewModel.class);
    }

    @Override
    public void setupViews() {
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_removeSticker);
        LinearLayout linearLayout = rootView.findViewById(R.id.ll_stickerSetting_toolBar);

        HelperToolbar helperToolbar = HelperToolbar.create()
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getResources().getString(R.string.sticker_setting))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        finish();
                    }
                })
                .setContext(getContext());

        linearLayout.addView(helperToolbar.getView());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        viewModel.getStickersLiveData().observe(getViewLifecycleOwner(), structIGStickerGroups -> adapter.updateAdapter(structIGStickerGroups));

        adapter.setListener(new RemoveStickerAdapter.RemoveStickerDialogListener() {
            @Override
            public void onStickerClick(StructIGStickerGroup stickerGroup) {
                openFragmentAddStickerToFavorite(stickerGroup);
            }

            @Override
            public void onRemoveStickerClick(StructIGStickerGroup stickerGroup, int pos, RemoveStickerAdapter.ProgressStatus progressStatus) {
                if (getContext() != null) {
                    new MaterialDialog.Builder(getContext())
                            .title(getResources().getString(R.string.remove_sticker))
                            .content(getResources().getString(R.string.remove_sticker_text))
                            .positiveText(getString(R.string.yes))
                            .negativeText(getString(R.string.no))
                            .onPositive((dialog, which) -> {
                                progressStatus.setVisibility(true);
                                viewModel.removeStickerFromMySticker(stickerGroup, (response, error) -> {
                                    if (error == null) {
                                        adapter.removeItem(pos);
                                    } else {
                                        progressStatus.setVisibility(false);
                                    }
                                });
                                dialog.dismiss();
                            }).show();
                }
            }
        });

        viewModel.getRemoveStickerLiveData().observe(getViewLifecycleOwner(), removedItemPosition -> adapter.removeItem(removedItemPosition));

        TextView removeRecentTv = rootView.findViewById(R.id.tv_stickerSetting_clearRecent);
        removeRecentTv.setOnClickListener(v -> onClearRecentStickerClicked());

        ProgressBar progressBar = rootView.findViewById(R.id.pb_stickerSetting_clearRecent);
        viewModel.getClearRecentStickerLiveData().observe(getViewLifecycleOwner(), progressBar::setVisibility);

        TextView clearInternalStorage = rootView.findViewById(R.id.tv_stickerSetting_clearStorage);
        clearInternalStorage.setOnClickListener(v -> viewModel.clearStickerFromInternalStorage());

        TextView storageSize = rootView.findViewById(R.id.tv_stickerSetting_clearStorageSize);
        viewModel.getStickerFileSizeLiveData().observe(getViewLifecycleOwner(), storageSize::setText);

        TextView clearFavoriteStickerTv = rootView.findViewById(R.id.tv_stickerSetting_clearFavorite);
        clearFavoriteStickerTv.setOnClickListener(v -> onClearFavoriteStickerClicked());

        ProgressBar clearFavoriteStickerPb = rootView.findViewById(R.id.pb_stickerSetting_clearFavorite);

        TextView clearRecentEmojiTv = rootView.findViewById(R.id.tv_stickerSetting_clearEmoji);
        clearRecentEmojiTv.setOnClickListener(v -> onClearRecentEmojiClicked());


        ImageView emptyIv = rootView.findViewById(R.id.iv_stickerSetting_empty);
        TextView emptyTv = rootView.findViewById(R.id.tv_stickerSetting_empty);
        TextView headerTv = rootView.findViewById(R.id.tv_stickerSetting_header);

        viewModel.getEmptyRecentStickerLiveData().observe(getViewLifecycleOwner(), visibility -> {
            emptyIv.setVisibility(visibility);
            emptyTv.setVisibility(visibility);
        });

        viewModel.getRecyclerVisibilityRecentStickerLiveData().observe(getViewLifecycleOwner(), visibility -> {
            recyclerView.setVisibility(visibility);
            headerTv.setVisibility(visibility);
        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_remove_sticker;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RemoveStickerAdapter();
    }

    private void onClearFavoriteStickerClicked() {

    }

    private void onClearRecentStickerClicked() {
        if (getContext() != null)
            new MaterialDialog.Builder(getContext())
                    .title(getResources().getString(R.string.remove_sticker))
                    .content(getResources().getString(R.string.remove_sticker_text))
                    .positiveText(getString(R.string.yes))
                    .negativeText(getString(R.string.no))
                    .onPositive((dialog, which) -> {
                        viewModel.clearRecentSticker();
                        dialog.dismiss();
                    }).show();
    }

    private void onClearRecentEmojiClicked() {
        viewModel.clearRecentEmoji();
    }

    private void openFragmentAddStickerToFavorite(StructIGStickerGroup stickerGroup) {

        StickerDialogFragment dialogFragment = StickerDialogFragment.getInstance(stickerGroup, true);

        if (getActivity()!= null)
            dialogFragment.show(getActivity().getSupportFragmentManager(), "dialogFragment");
    }
}
