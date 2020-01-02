package net.iGap.fragments.emoji.remove;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.add.StickerDialogFragment;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.sticker.RemoveStickerViewModel;

public class StickerSettingFragment extends BaseFragment {

    private RemoveStickerAdapter adapter;
    private RemoveStickerViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new RemoveStickerAdapter();
        viewModel = new RemoveStickerViewModel();
        return inflater.inflate(R.layout.fragment_remove_sticker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_removeSticker);
        LinearLayout linearLayout = view.findViewById(R.id.ll_stickerSetting_toolBar);

        HelperToolbar helperToolbar = HelperToolbar.create()
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getResources().getString(R.string.sticker_setting))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null)
                            getActivity().onBackPressed();
                    }
                })
                .setContext(getContext());

        linearLayout.addView(helperToolbar.getView());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.updateAdapter(viewModel.getFavoriteStickers());

        adapter.setListener(new RemoveStickerAdapter.RemoveStickerDialogListener() {
            @Override
            public void onStickerClick(StructIGStickerGroup stickerGroup) {
                openFragmentAddStickerToFavorite(stickerGroup);
            }

            @Override
            public void onRemoveStickerClick(StructIGStickerGroup stickerGroup, int pos, RemoveStickerAdapter.ProgressStatus progressStatus) {
                if (pos != -1 && getContext() != null) {
                    new MaterialDialog.Builder(getContext())
                            .title(getResources().getString(R.string.remove_sticker))
                            .content(getResources().getString(R.string.remove_sticker_text))
                            .positiveText(getString(R.string.yes))
                            .negativeText(getString(R.string.no))
                            .onPositive((dialog, which) -> {
                                progressStatus.setVisibility(true);
                                viewModel.removeStickerFromFavorite(adapter.getStickerGroup(pos).getGroupId(), pos);
                                dialog.dismiss();
                            }).show();
                }
            }
        });

        viewModel.getRemoveStickerLiveData().observe(getViewLifecycleOwner(), removedItemPosition -> adapter.removeItem(removedItemPosition));

        TextView removeRecentTv = view.findViewById(R.id.tv_stickerSetting_clearRecent);
        removeRecentTv.setOnClickListener(v -> {
            new MaterialDialog.Builder(getContext())
                    .title(getResources().getString(R.string.remove_sticker))
                    .content(getResources().getString(R.string.remove_sticker_text))
                    .positiveText(getString(R.string.yes))
                    .negativeText(getString(R.string.no))
                    .onPositive((dialog, which) -> {
                        viewModel.clearRecentSticker();
                        dialog.dismiss();
                    }).show();
        });

        ProgressBar progressBar = view.findViewById(R.id.pb_stickerSetting_clearRecent);
        viewModel.getClearRecentStickerLiveData().observe(getViewLifecycleOwner(), progressBar::setVisibility);

        TextView clearInternalStorage = view.findViewById(R.id.tv_stickerSetting_clearStorage);
        clearInternalStorage.setOnClickListener(v -> viewModel.clearStickerFromInternalStorage());

        TextView storageSize = view.findViewById(R.id.tv_stickerSetting_clearStorageSize);
        viewModel.getStickerFileSizeLiveData().observe(getViewLifecycleOwner(), storageSize::setText);

    }

    private void openFragmentAddStickerToFavorite(StructIGStickerGroup stickerGroup) {

        StickerDialogFragment dialogFragment = StickerDialogFragment.getInstance(stickerGroup, true);

        if (getFragmentManager() != null)
            dialogFragment.show(getFragmentManager(), "dialogFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.unsubscribe();
    }
}
