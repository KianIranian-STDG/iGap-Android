package net.iGap.fragments.emoji.remove;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.stickerDetail.FragmentStickersDetail;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperFragment;
import net.iGap.viewmodel.sticker.RemoveStickerViewModel;

public class FragmentRemoveSticker extends BaseFragment {

    private ProgressBar progressBar;

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

        progressBar = view.findViewById(R.id.pb_removeSticker);
        RecyclerView recyclerView = view.findViewById(R.id.rv_removeSticker);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.updateAdapter(viewModel.getFavoriteStickers());

        adapter.setListener(new RemoveStickerAdapter.RemoveStickerDialogListener() {
            @Override
            public void onStickerClick(StructIGStickerGroup stickerGroup) {
                if (getFragmentManager() != null)
                    new HelperFragment(getFragmentManager(), FragmentStickersDetail.newInstance(stickerGroup)).setReplace(false).load();
            }

            @Override
            public void onRemoveStickerClick(StructIGStickerGroup stickerGroup, int pos) {
                viewModel.onRemoveStickerClicked(pos);
            }
        });

        viewModel.getRemoveProgressLiveData().observe(getViewLifecycleOwner(), visibility -> progressBar.setVisibility(visibility));

        viewModel.getRemoveStickerLiveData().observe(getViewLifecycleOwner(), removedItemPosition -> adapter.removeItem(removedItemPosition));

        viewModel.getRemoveDialogLiveData().observe(getViewLifecycleOwner(), position -> {
            if (position != -1 && getContext() != null) {
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.remove_sticker))
                        .content(getResources().getString(R.string.remove_sticker_text))
                        .positiveText(getString(R.string.yes))
                        .negativeText(getString(R.string.no))
                        .onPositive((dialog, which) -> {
                            viewModel.removeStickerFromFavorite(adapter.getStickerGroup(position).getGroupId(), position);
                            dialog.dismiss();
                        })
                        .show();
            }
        });

    }
}
