package net.iGap.fragments.emoji.stickerDetail;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.add.StickerAdapter;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.sticker.StickerDialogViewModel;

public class FragmentStickersDetail extends BaseFragment {

    private StructIGStickerGroup stickerGroup;

    private StickerAdapter adapter;
    private StickerDialogViewModel viewModel;

    public static FragmentStickersDetail newInstance(StructIGStickerGroup stickerGroup) {
        FragmentStickersDetail fragmentStickersDetail = new FragmentStickersDetail();
        fragmentStickersDetail.stickerGroup = stickerGroup;
        return fragmentStickersDetail;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new StickerAdapter();
        viewModel = new StickerDialogViewModel(stickerGroup);
        return inflater.inflate(R.layout.fragment_detail_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setShowConnectionState(false)
                .setDefaultTitle(stickerGroup.getName())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        getActivity().onBackPressed();
                    }
                });

        ViewGroup toolbarLayout = view.findViewById(R.id.ll_stickerDetail_toolBar);
        toolbarLayout.addView(toolbar.getView());

        RecyclerView recyclerView = view.findViewById(R.id.rv_stickerDetail);

        recyclerView.setAdapter(adapter);

        viewModel.getStickersMutableLiveData().observe(getViewLifecycleOwner(), stickers -> adapter.setIgStickers(stickers.getStickers()));

        viewModel.getSticker();
    }
}
