package net.iGap.fragments.emoji.remove;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vanniktech.emoji.sticker.struct.StructItemSticker;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// TODO: 12/15/19 must fix this class for better performance
public class RemoveRecentStickerFragment extends BaseFragment {

    public RemoveRecentStickerAdapter removeRecentStickerAdapter;
    public List<StructItemSticker> stickerList = new ArrayList<>();
    public static ArrayList<String> removeStickerList = new ArrayList<>();

    public static RemoveRecentStickerFragment newInstance(List<StructItemSticker> stickerList) {
        RemoveRecentStickerFragment fragmentDetailStickers = new RemoveRecentStickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("RECENT", (Serializable) stickerList);
        fragmentDetailStickers.setArguments(bundle);
        return fragmentDetailStickers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remove_recent_sticker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stickerList = (List<StructItemSticker>) getArguments().getSerializable("RECENT");

        if (stickerList == null) stickerList = new ArrayList<>();

        RecyclerView rcvSettingPage = view.findViewById(R.id.rcvSettingPage);
        removeRecentStickerAdapter = new RemoveRecentStickerAdapter(stickerList);
        rcvSettingPage.setAdapter(removeRecentStickerAdapter);
    }
}


