package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.module.api.beepTunes.PlayingSong;

public class BeepTunesFragment extends BaseFragment {

    private static String TAG = "aabolfazlBeepTunes";
    private View rootView;
    private BeepTunesViewModel viewModel;
    private BottomSheetBehavior behavior;
    private LinearLayout playerLayout;
    private MutableLiveData<PlayingSong> toAlbumAdapter = new MutableLiveData<>();
    private MutableLiveData<PlayingSong> fromAlbumeAdpater = new MutableLiveData<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beep_tunes, container, false);
        viewModel = new BeepTunesViewModel();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerLayout = rootView.findViewById(R.id.cl_beepTunesPlayer);
        behavior = BottomSheetBehavior.from(playerLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        new HelperFragment(getFragmentManager(), new BeepTunesMainFragment().getInstance(fromAlbumeAdpater, toAlbumAdapter))
                .setResourceContainer(R.id.fl_beepTunes_Container).setAddToBackStack(false).setReplace(false).load();

        TextView artistNameTv = rootView.findViewById(R.id.tv_btPlayer_artistName);
        TextView songNameTv = rootView.findViewById(R.id.tv_btPlayer_songName);
        TextView playIconTv = rootView.findViewById(R.id.tv_btPlayer_playIcon);
        ImageView songImageIv = rootView.findViewById(R.id.iv_btPlayer_image);


        viewModel.getPlayerStatusLiveData().observe(getViewLifecycleOwner(), playingSong -> {
            if (playingSong != null) {
                switch (playingSong.getStatus()) {
                    case PlayingSong.PLAY:
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case PlayingSong.PAUSE:
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case PlayingSong.STOP:
                        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;

                }
                artistNameTv.setText(playingSong.getArtistName());
                songNameTv.setText(playingSong.getTitle());
                songImageIv.setImageBitmap(playingSong.getBitmap());

            }
        });

        fromAlbumeAdpater.observe(getViewLifecycleOwner(), playingSong -> viewModel.onPlaySongClicked(playingSong));

    }
}

