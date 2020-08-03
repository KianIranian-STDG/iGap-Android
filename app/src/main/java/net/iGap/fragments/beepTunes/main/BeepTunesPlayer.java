package net.iGap.fragments.beepTunes.main;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.module.CircleImageView;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.ProgressDuration;
import net.iGap.realm.RealmDownloadSong;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.fragments.beepTunes.main.BeepTunesViewModel.MEDIA_PLAYER_STATUS_COMPLETE;

public class BeepTunesPlayer extends BaseFragment {
    private static final String TAG = "aabolfazlPlayer";


    private View rootView;
    private TextView playTv;
    private TextView nextTv;
    private TextView previousTv;
    private TextView artistNameTv;
    private TextView songNameTv;
    private TextView totalTimeTv;
    private TextView favoriteTv;
    private TextView currentTimeTv;
    private SeekBar seekBar;
    private CircleImageView songArtIv;
    private ImageView backgroundIv;

    private RealmDownloadSong realmDownloadSong;
    private List<RealmDownloadSong> realmDownloadSongs;

    private MutableLiveData<PlayingSong> songMutableLiveData;
    private MutableLiveData<PlayingSong> songFromPlayerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> seekBarLiveData = new MutableLiveData<>();
    private MutableLiveData<ProgressDuration> progressDurationLiveData;
    private MutableLiveData<Integer> mediaPlayerStatusLiveData;

    public static BeepTunesPlayer getInstance(MutableLiveData<PlayingSong> songMutableLiveData, MutableLiveData<ProgressDuration> progressDurationLiveData, MutableLiveData<Integer> mediaPlayerStatusLiveData) {
        BeepTunesPlayer beepTunesPlayer = new BeepTunesPlayer();
        beepTunesPlayer.songMutableLiveData = songMutableLiveData;
        beepTunesPlayer.progressDurationLiveData = progressDurationLiveData;
        beepTunesPlayer.realmDownloadSongs = new ArrayList<>();
        beepTunesPlayer.mediaPlayerStatusLiveData = mediaPlayerStatusLiveData;
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
        setUpViews();

        songMutableLiveData.observe(getViewLifecycleOwner(), playingSong -> {
            if (playingSong != null) {
                artistNameTv.setText(playingSong.getArtistName());
                songNameTv.setText(playingSong.getTitle());
                songArtIv.setImageBitmap(playingSong.getBitmap());
                backgroundIv.setImageBitmap(playingSong.getBitmap());
                if (playingSong.isPlay()) {
                    playTv.setText(getContext().getResources().getString(R.string.pause_icon));
                } else {
                    playTv.setText(getContext().getResources().getString(R.string.play_icon));
                }

//                realmDownloadSongs = DbManager.getInstance().getUiRealm().copyFromRealm(DbManager.getInstance().getUiRealm().where(RealmDownloadSong.class)
//                        .equalTo("artistId", playingSong.getArtistId()).findAll());

                realmDownloadSong = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.copyFromRealm(realm
                            .where(RealmDownloadSong.class)
                            .equalTo("id", playingSong.getSongId())
                            .findFirst());
                });

                checkFavorite(realmDownloadSong);
            }
        });

        progressDurationLiveData.observe(getViewLifecycleOwner(), progressDuration -> {
            if (progressDuration != null && songMutableLiveData.getValue() != null) {
                if (songMutableLiveData.getValue().getSongId() == progressDuration.getId()) {
                    seekBar.setProgress(progressDuration.getCurrent());
                    seekBar.setMax(progressDuration.getTotal());
                    currentTimeTv.setText(progressDuration.getCurrentTime());
                    totalTimeTv.setText(progressDuration.getTotalTime());

                }
            }
        });

        playTv.setOnClickListener(v -> {
            if (songMutableLiveData.getValue() != null && songMutableLiveData.getValue().isPlay()) {
                songMutableLiveData.getValue().setStatus(PlayingSong.PAUSE);
                songMutableLiveData.getValue().setBehaviorStatus(BottomSheetBehavior.STATE_EXPANDED);
                songFromPlayerLiveData.postValue(songMutableLiveData.getValue());
            } else {
                songMutableLiveData.getValue().setStatus(PlayingSong.PLAY);
                songMutableLiveData.getValue().setBehaviorStatus(BottomSheetBehavior.STATE_EXPANDED);
                songFromPlayerLiveData.postValue(songMutableLiveData.getValue());
            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBarLiveData.postValue(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        nextTv.setOnClickListener(v -> playNextSong(songMutableLiveData.getValue()));
        previousTv.setOnClickListener(v -> playPreviousSong(songMutableLiveData.getValue()));

        mediaPlayerStatusLiveData.observe(getViewLifecycleOwner(), status -> {
            if (status != null)
                if (status == MEDIA_PLAYER_STATUS_COMPLETE) {
                    playNextSong(songMutableLiveData.getValue());
                }
        });

        favoriteTv.setOnClickListener(v -> {
            if (!realmDownloadSong.isFavorite()) {
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransactionAsync(realm1 -> {
                        realmDownloadSong.setFavorite(true);
                        realm1.copyToRealmOrUpdate(realmDownloadSong);
                    }, () -> checkFavorite(realmDownloadSong));
                });
            } else {
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransactionAsync(realm1 -> {
                        realmDownloadSong.setFavorite(false);
                        realm1.copyToRealmOrUpdate(realmDownloadSong);
                    }, () -> checkFavorite(realmDownloadSong));
                });
            }
        });

    }

    private void checkFavorite(RealmDownloadSong realmDownloadSong) {
        if (realmDownloadSong != null && realmDownloadSong.getId() == songMutableLiveData.getValue().getSongId()) {
            if (realmDownloadSong.isFavorite()) {
                favoriteTv.setTextColor(getContext().getResources().getColor(R.color.beeptunes_primary));
            } else {
                favoriteTv.setTextColor(getContext().getResources().getColor(R.color.gray));
            }
        }
    }

    private void playNextSong(PlayingSong playingSong) {
        if (realmDownloadSongs.size() > 0) {
            for (int i = 0; i < realmDownloadSongs.size(); i++) {
                if (realmDownloadSongs.get(i).getId() == playingSong.getSongId()) {
                    int index = i + 1;
                    if (index != realmDownloadSongs.size()) {
                        PlayingSong nextSong = new PlayingSong();
                        RealmDownloadSong realmDownloadSong = realmDownloadSongs.get(index);
                        nextSong.setSongId(realmDownloadSong.getId());
                        nextSong.setSongPath(realmDownloadSong.getPath());
                        nextSong.setArtistId(realmDownloadSong.getArtistId());
                        nextSong.setAlbumId(realmDownloadSong.getAlbumId());
                        nextSong.setBehaviorStatus(BottomSheetBehavior.STATE_EXPANDED);
                        songFromPlayerLiveData.postValue(nextSong);
                    } else {

                    }
                }
            }
        }
    }

    private void playPreviousSong(PlayingSong playingSong) {
        if (realmDownloadSongs.size() > 0) {
            for (int i = 0; i < realmDownloadSongs.size(); i++) {
                if (realmDownloadSongs.get(i).getId() == playingSong.getSongId()) {
                    int index = i - 1;
                    if (!(index < 0)) {
                        PlayingSong nextSong = new PlayingSong();
                        RealmDownloadSong realmDownloadSong = realmDownloadSongs.get(index);
                        nextSong.setSongId(realmDownloadSong.getId());
                        nextSong.setSongPath(realmDownloadSong.getPath());
                        nextSong.setArtistId(realmDownloadSong.getArtistId());
                        nextSong.setAlbumId(realmDownloadSong.getAlbumId());
                        nextSong.setBehaviorStatus(BottomSheetBehavior.STATE_EXPANDED);
                        songFromPlayerLiveData.postValue(nextSong);
                    }
                }
            }
        }

    }

    private void setUpViews() {
        playTv = rootView.findViewById(R.id.tv_btPlayer_play);
        nextTv = rootView.findViewById(R.id.tv_btPlayer_nextSong);
        previousTv = rootView.findViewById(R.id.tv_ptPlayer_previous);
        artistNameTv = rootView.findViewById(R.id.tv_btPlayer_artistName);
        songNameTv = rootView.findViewById(R.id.tv_btPlayer_songName);
        totalTimeTv = rootView.findViewById(R.id.tv_btPlayer_totalDuration);
        currentTimeTv = rootView.findViewById(R.id.tv_btPlayer_currentDuration);
        seekBar = rootView.findViewById(R.id.sb_ptPlayer);
        songArtIv = rootView.findViewById(R.id.iv_btPlayer_songArt);
        backgroundIv = rootView.findViewById(R.id.iv_btPlayer_cover);
        favoriteTv = rootView.findViewById(R.id.tv_btPlayer_isFavorite);
        seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#00D20E"), PorterDuff.Mode.SRC_IN);
    }

    public MutableLiveData<PlayingSong> getSongFromPlayerLiveData() {
        return songFromPlayerLiveData;
    }

    public MutableLiveData<Integer> getPlayingSongSeekBarLiveData() {
        return seekBarLiveData;
    }
}

