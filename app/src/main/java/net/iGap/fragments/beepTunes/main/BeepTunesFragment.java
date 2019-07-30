package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.api.beepTunes.PlayingSong;

public class BeepTunesFragment extends BaseFragment {

    private static String TAG = "aabolfazlBeepTunes";
    private View rootView;
    private BeepTunesViewModel viewModel;
    private BottomSheetBehavior behavior;
    private ProgressBar progressBar;
    private MutableLiveData<PlayingSong> toAlbumAdapter = new MutableLiveData<>();
    private MutableLiveData<PlayingSong> fromAlbumAdapter = new MutableLiveData<>();
    private BeepTunesPlayer beepTunesPlayer;
    private ConstraintLayout bottomPlayerCl;
    private ConstraintLayout playerToolBarCl;

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
        beepTunesPlayer = BeepTunesPlayer.getInstance(toAlbumAdapter, viewModel.getProgressDurationLiveData(), viewModel.getMediaPlayerStatusLiveData());
        LinearLayout playerLayout = rootView.findViewById(R.id.cl_beepTunesPlayer);
        behavior = BottomSheetBehavior.from(playerLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        progressBar = rootView.findViewById(R.id.pb_btBehavior_behavior);
        bottomPlayerCl = rootView.findViewById(R.id.cl_btPlayer_behavior);
        playerToolBarCl = rootView.findViewById(R.id.cl_btPlayer_toolBar);
        TextView playerToolBarPlayerTv = rootView.findViewById(R.id.tv_btPlayer_toolBarTitle);
        TextView behaviorPlayerTime = rootView.findViewById(R.id.tv_btBehavior_timeDuration);

        progressBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        new HelperFragment(getFragmentManager(), new BeepTunesMainFragment().getInstance(fromAlbumAdapter, toAlbumAdapter))
                .setResourceContainer(R.id.fl_beepTunes_Container).setAddToBackStack(false).setReplace(false).load();

        TextView artistNameTv = rootView.findViewById(R.id.tv_btBehavior_artistName);
        TextView songNameTv = rootView.findViewById(R.id.tv_btBehavior_songName);
        TextView playIconTv = rootView.findViewById(R.id.tv_btBehavior_playIcon);
        ImageView songImageIv = rootView.findViewById(R.id.iv_btBehavior_image);
        ImageView hidePlayerIv = rootView.findViewById(R.id.iv_btPlayer_hide);


        Utils.setShapeBackground(bottomPlayerCl,R.color.gray_300,R.color.navigation_dark_mode_bg);
        Utils.setShapeBackground(playerToolBarCl,R.color.gray_300,R.color.navigation_dark_mode_bg);

        viewModel.getPlayingSongViewLiveData().observe(getViewLifecycleOwner(), playingSong -> {
            if (playingSong != null) {
                toAlbumAdapter.postValue(playingSong);
                artistNameTv.setText(playingSong.getArtistName());
                songNameTv.setText(playingSong.getTitle());
                songImageIv.setImageBitmap(playingSong.getBitmap());

                if (playingSong.isPlay()) {
                    playIconTv.setText(getContext().getResources().getString(R.string.pause_icon));
                } else {
                    playIconTv.setText(getContext().getResources().getString(R.string.icon_play));
                }
                behavior.setState(playingSong.getBehaviorStatus());

                new HelperFragment(getFragmentManager(), beepTunesPlayer)
                        .setResourceContainer(R.id.fl_btPlayer_container).setAddToBackStack(false).setReplace(true).load();
                playerToolBarPlayerTv.setText(playingSong.getTitle());
            }
        });

        playIconTv.setOnClickListener(v -> {
            if (viewModel.getPlayingSongViewLiveData().getValue() != null) {
                viewModel.getPlayingSongViewLiveData().getValue().setFromPlayer(false);
                viewModel.onPlaySongClicked(viewModel.getPlayingSongViewLiveData().getValue(), getContext());
            }
        });

        fromAlbumAdapter.observe(getViewLifecycleOwner(), playingSong -> viewModel.onPlaySongClicked(playingSong, getContext()));

        playerLayout.setOnClickListener(v -> behavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        viewModel.getProgressDurationLiveData().observe(getViewLifecycleOwner(), progressDuration -> {
            if (progressDuration != null && viewModel.getPlayingSongViewLiveData().getValue() != null) {
                if (viewModel.getPlayingSongViewLiveData().getValue().getSongId() == progressDuration.getId()) {
                    progressBar.setProgress(progressDuration.getCurrent());
                    progressBar.setMax(progressDuration.getTotal());
                    behaviorPlayerTime.setText(progressDuration.getCurrentTime() + " | " + progressDuration.getTotalTime());
                }
            }
        });

        beepTunesPlayer.getSongFromPlayerLiveData().observe(getViewLifecycleOwner(), playingSong -> {
            viewModel.onPlaySongClicked(playingSong, getContext());
        });

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int status) {
                switch (status) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        playerToolBarCl.setVisibility(View.GONE);
                        bottomPlayerCl.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        playerToolBarCl.setVisibility(View.VISIBLE);
                        bottomPlayerCl.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        playerToolBarCl.setVisibility(View.GONE);
                        bottomPlayerCl.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        playerToolBarCl.setVisibility(View.VISIBLE);
                        bottomPlayerCl.setVisibility(View.GONE);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        hidePlayerIv.setOnClickListener(v -> {
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        beepTunesPlayer.getPlayingSongSeekBarLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null)
                viewModel.seekBarProgressChanged(progress);
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

