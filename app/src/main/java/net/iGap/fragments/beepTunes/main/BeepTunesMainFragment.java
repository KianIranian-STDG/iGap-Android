package net.iGap.fragments.beepTunes.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import net.iGap.R;
import net.iGap.adapter.beepTunes.BeepTunesAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.beepTunes.album.AlbumFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.module.api.beepTunes.Album;

public class BeepTunesMainFragment extends BaseFragment {
    private View rootView;
    private BeepTunesMainViewModel viewModel;
    private BeepTunesAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_beeptunes_main, container, false);
        viewModel = new BeepTunesMainViewModel();
        adapter = new BeepTunesAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ProgressBar loadingProgress = rootView.findViewById(R.id.pb_beepTunes_loading);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_beepTunes_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        viewModel.getFirstPageMutableLiveData().observe(this, firstPage -> {
            if (firstPage != null)
                adapter.setData(firstPage.getData());
        });

        adapter.setOnItemClick(album -> {
            new HelperFragment(getFragmentManager(), new AlbumFragment().getInstance(album)).setReplace(false).load();
        });

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), loadingProgress::setVisibility);
    }

    public interface OnItemClick {
        void onClick(Album album);
    }
}
