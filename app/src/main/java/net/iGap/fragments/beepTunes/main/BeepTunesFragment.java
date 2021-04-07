package net.iGap.fragments.beepTunes.main;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.module.api.beepTunes.PlayingSong;

public class BeepTunesFragment extends BaseFragment {

    private static String TAG = "aabolfazlBeepTunes";

    private View rootView;
    private ConstraintLayout bottomPlayerCl;
    private ConstraintLayout playerToolBarCl;
    private BottomSheetBehavior behavior;
    private ProgressBar progressBar;

    private BeepTunesPlayer beepTunesPlayer;
    private BeepTunesViewModel viewModel;

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

        new HelperFragment(getFragmentManager(), new BeepTunesMainFragment().getInstance(fromAlbumAdapter, toAlbumAdapter))
                .setResourceContainer(R.id.fl_beepTunes_Container).setAddToBackStack(false).setReplace(false).load();

        TextView artistNameTv = rootView.findViewById(R.id.tv_btBehavior_artistName);
        TextView songNameTv = rootView.findViewById(R.id.tv_btBehavior_songName);
        TextView playIconTv = rootView.findViewById(R.id.tv_btBehavior_playIcon);
        ImageView songImageIv = rootView.findViewById(R.id.iv_btBehavior_image);
        ImageView hidePlayerIv = rootView.findViewById(R.id.iv_btPlayer_hide);
        TextView playerToolBarPlayerTv = rootView.findViewById(R.id.tv_btPlayer_toolBarTitle);
        TextView behaviorPlayerTime = rootView.findViewById(R.id.tv_btBehavior_timeDuration);
        LinearLayout playerLayout = rootView.findViewById(R.id.cl_beepTunesPlayer);
        progressBar = rootView.findViewById(R.id.pb_btBehavior_behavior);
        bottomPlayerCl = rootView.findViewById(R.id.cl_btPlayer_behavior);
        playerToolBarCl = rootView.findViewById(R.id.cl_btPlayer_toolBar);

        beepTunesPlayer = BeepTunesPlayer.getInstance(toAlbumAdapter, viewModel.getProgressDurationLiveData(), viewModel.getMediaPlayerStatusLiveData());
        behavior = BottomSheetBehavior.from(playerLayout);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        viewModel.getPlayingSongViewLiveData().observe(getViewLifecycleOwner(), playingSong -> {
            if (playingSong != null) {
                toAlbumAdapter.postValue(playingSong);
                artistNameTv.setText(playingSong.getArtistName());
                songNameTv.setText(playingSong.getTitle());
                songImageIv.setImageBitmap(playingSong.getBitmap());

                if (playingSong.isPlay()) {
                    playIconTv.setText(getContext().getResources().getString(R.string.pause_icon));
                } else {
                    playIconTv.setText(getContext().getResources().getString(R.string.icon_beeptunes_play));
                }

                behavior.setState(playingSong.getBehaviorStatus());

                new HelperFragment(getFragmentManager(), beepTunesPlayer)
                        .setResourceContainer(R.id.fl_btPlayer_container).setAddToBackStack(false).setReplace(true).load();
                playerToolBarPlayerTv.setText(playingSong.getTitle());
            }
        });

        playerLayout.setOnClickListener(v -> behavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        playIconTv.setOnClickListener(v -> {
            if (viewModel.getPlayingSongViewLiveData().getValue() != null) {
                viewModel.getPlayingSongViewLiveData().getValue().setBehaviorStatus(BottomSheetBehavior.STATE_COLLAPSED);
                viewModel.onPlaySongClicked(viewModel.getPlayingSongViewLiveData().getValue(), getContext());
            }
        });

        fromAlbumAdapter.observe(getViewLifecycleOwner(), playingSong -> viewModel.onPlaySongClicked(playingSong, getContext()));

        viewModel.getProgressDurationLiveData().observe(getViewLifecycleOwner(), progressDuration -> {
            if (progressDuration != null && viewModel.getPlayingSongViewLiveData().getValue() != null) {
                if (viewModel.getPlayingSongViewLiveData().getValue().getSongId() == progressDuration.getId()) {
                    progressBar.setProgress(progressDuration.getCurrent());
                    progressBar.setMax(progressDuration.getTotal());
                    behaviorPlayerTime.setText(progressDuration.getCurrentTime() + " | " + progressDuration.getTotalTime());
                }
            }
        });

        beepTunesPlayer.getSongFromPlayerLiveData().observe(getViewLifecycleOwner(),
                playingSong -> viewModel.onPlaySongClicked(playingSong, getContext()));

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

        progressBar.getProgressDrawable().setColorFilter(new Theme().getAccentColor(progressBar.getContext()), PorterDuff.Mode.SRC_IN);

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

