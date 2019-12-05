package net.iGap.fragments.emoji.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

import net.iGap.R;
import net.iGap.fragments.emoji.api.APIEmojiService;
import net.iGap.fragments.emoji.api.ApiEmojiUtils;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

import java.util.ArrayList;
import java.util.List;

public class StickerDialogFragment extends DialogFragment {
    private ProgressBar progressBar;
    private StickerAdapter adapter;
    private APIEmojiService mAPIService;
    private String groupId = "";
    private String token = "";
    private List<StructGroupSticker> data = new ArrayList<>();
    private StickerDialogViewModel viewModel;
    private String TAG = "abbasiSticker";

    public StickerDialogFragment newInstance(String groupId, String token) {
        StickerDialogFragment dialogAddSticker = new StickerDialogFragment();
        Bundle args = new Bundle();
        args.putString("GROUP_ID", groupId);
        args.putString("TOKEN", token);
        dialogAddSticker.setArguments(args);
        return dialogAddSticker;
    }

    private void getSticker(String groupID) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new StickerDialogViewModel();
        return inflater.inflate(R.layout.dialog_add_sticker, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            groupId = getArguments().getString("GROUP_ID");
            token = getArguments().getString("TOKEN");
        }

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), visibility -> progressBar.setVisibility(visibility));
        viewModel.getCloseDialogMutableLiveData().observe(getViewLifecycleOwner(), close -> {
            if (close)
                dismiss();
        });

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.add_icon_without_circle_font)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.add_sticker))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        getDialog().dismiss();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        viewModel.addSticker(groupId);
                    }
                });

        ViewGroup layoutToolbar = view.findViewById(R.id.add_sticker_toolbar);
        layoutToolbar.addView(toolbar.getView());

        mAPIService = ApiEmojiUtils.getAPIService();
        progressBar = view.findViewById(R.id.progress_stricker);
        RecyclerView stickerRecyclerView = view.findViewById(R.id.rcvAddStickerDialog);
        adapter = new StickerAdapter(new StickerAdapter.AddStickerDialogListener() {
            @Override
            public void onStickerClick() {

            }

            @Override
            public void onStickerLongClick() {

            }

            @Override
            public void onStickerDownLoaded() {

            }
        });
        stickerRecyclerView.setAdapter(adapter);
        stickerRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        stickerRecyclerView.setHasFixedSize(true);
        getSticker(groupId);

        viewModel.getSticker(groupId, structIGStickers -> adapter.setIgStickers(structIGStickers));
    }
}