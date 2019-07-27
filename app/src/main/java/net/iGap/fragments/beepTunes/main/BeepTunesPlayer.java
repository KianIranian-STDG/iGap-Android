package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.module.api.beepTunes.PlayingSong;

public class BeepTunesPlayer extends BaseFragment {

    private View rootView;
    private MutableLiveData<PlayingSong> songMutableLiveData;


    public static BeepTunesPlayer getInstance(MutableLiveData<PlayingSong> songMutableLiveData) {
        BeepTunesPlayer beepTunesPlayer = new BeepTunesPlayer();
        beepTunesPlayer.songMutableLiveData = songMutableLiveData;
        return beepTunesPlayer;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beeptunes_player, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        songMutableLiveData.observe(getViewLifecycleOwner(), playingSong -> {

        });

    }
}

