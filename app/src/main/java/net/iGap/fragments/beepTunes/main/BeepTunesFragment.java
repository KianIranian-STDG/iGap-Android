package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;
    private LinearLayout playerLayout;
    private MutableLiveData<PlayingSong> toAlbumAdapter = new MutableLiveData<>();
    private MutableLiveData<PlayingSong> fromAlbumAdapter = new MutableLiveData<>();


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
        viewModel.onCreateFragment(this);
        playerLayout = rootView.findViewById(R.id.cl_beepTunesPlayer);
        behavior = BottomSheetBehavior.from(playerLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        progressBar = rootView.findViewById(R.id.pb_btPlayer_behavier);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);


        new HelperFragment(getFragmentManager(), new BeepTunesMainFragment().getInstance(fromAlbumAdapter, toAlbumAdapter))
                .setResourceContainer(R.id.fl_beepTunes_Container).setAddToBackStack(false).setReplace(false).load();

        TextView artistNameTv = rootView.findViewById(R.id.tv_btPlayer_artistName);
        TextView songNameTv = rootView.findViewById(R.id.tv_btPlayer_songName);
        TextView playIconTv = rootView.findViewById(R.id.tv_btPlayer_playIcon);
        ImageView songImageIv = rootView.findViewById(R.id.iv_btPlayer_image);


        viewModel.getPlayingSongViewLiveData().observe(getViewLifecycleOwner(), playingSong -> {
            if (playingSong != null) {
//                toAlbumAdapter.postValue(playingSong);
                artistNameTv.setText(playingSong.getArtistName());
                songNameTv.setText(playingSong.getTitle());
                songImageIv.setImageBitmap(playingSong.getBitmap());

                if (playingSong.isPlay()) {
                    playIconTv.setText(getContext().getResources().getString(R.string.pause_icon));
                } else {
                    playIconTv.setText(getContext().getResources().getString(R.string.icon_play));
                }
            }
        });

//        playIconTv.setOnClickListener(v -> {
//            viewModel.onPlaySongClicked(viewModel.getPlayerStatusLiveData().getValue(), getContext());
//        });

        fromAlbumAdapter.observe(getViewLifecycleOwner(), playingSong -> viewModel.onPlaySongClicked(playingSong, getContext()));

        viewModel.getBehaviorStatusLiveData().observe(getViewLifecycleOwner(), status -> {
            if (status != null)
                if (status) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
        });

        playerLayout.setOnClickListener(v -> behavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        viewModel.getProgressDurationLiveData().observe(getViewLifecycleOwner(), progressDuration -> {
            if (progressDuration != null) {
                progressBar.setProgress(progressDuration.getCurrent());
                progressBar.setMax(progressDuration.getTotal());

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStartFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroyFragment(this);
    }
}

