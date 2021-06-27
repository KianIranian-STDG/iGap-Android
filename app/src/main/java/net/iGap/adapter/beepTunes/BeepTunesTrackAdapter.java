package net.iGap.adapter.beepTunes;

import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.module.accountManager.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.observers.interfaces.OnTrackAdapter;
import net.iGap.module.BeepTunesPlayerService;
import net.iGap.module.api.beepTunes.DownloadSong;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.realm.RealmDownloadSong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_COMPLETE;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_DOWNLOADING;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_ERROR;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_START;

public class BeepTunesTrackAdapter extends RecyclerView.Adapter<BeepTunesTrackAdapter.TrackViewHolder> {
    private static final String TAG = "aabolfazlAdapter";
    private List<Track> tracks = new ArrayList<>();
    private OnTrackAdapter onTrackAdapter;
    private MediaPlayer mediaPlayer;

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    public void setOnTrackAdapter(OnTrackAdapter onTrackAdapter) {
        this.onTrackAdapter = onTrackAdapter;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_song, viewGroup, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder trackViewHolder, int i) {
        trackViewHolder.bindTracks(tracks.get(i));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @FunctionalInterface
    public interface OnSongProgress {
        void progress(DownloadSong downloadSong);
    }

    @FunctionalInterface
    public interface OnSongPlay {
        void songStatus(PlayingSong playingSong);
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        private RealmDownloadSong realmDownloadSong;
        private TextView songNameTv;
        private TextView songPriceTv;
        private TextView songActionTv;
        private TextView songPrwTv;
        private ProgressBar progressBar;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTv = itemView.findViewById(R.id.tv_itemSong_title);
            songPriceTv = itemView.findViewById(R.id.tv_itemSong_price);
            songActionTv = itemView.findViewById(R.id.tv_itemSong_action);
            songPrwTv = itemView.findViewById(R.id.tv_itemSong_prw);
            progressBar = itemView.findViewById(R.id.pb_itemSong);
            progressBar.getIndeterminateDrawable().setColorFilter(itemView.getContext().getResources().getColor(R.color.beeptunes_primary), PorterDuff.Mode.SRC_IN);
        }

        void bindTracks(Track track) {
            realmDownloadSong = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmDownloadSong.class).equalTo("id", track.getId()).findFirst();
            });

            if (realmDownloadSong != null) {
                track.setInStorage(true);
                songPrwTv.setText(itemView.getContext().getResources().getString(R.string.icon_music));
            } else {
                track.setInStorage(false);
                songPrwTv.setText(itemView.getContext().getResources().getString(R.string.icon_play));
            }

            RotateAnimation rotate = new RotateAnimation(
                    0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );

            if (track.isInStorage()) {
                if (track.getId() == BeepTunesPlayerService.playingSongId) {
                    songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_pause));
                } else {
                    songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_beeptunes_play));
                }
            } else {
                songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_beeptunes_sync));
            }

            if (G.isAppRtl)
                songNameTv.setText(track.getName());
            else
                songNameTv.setText(track.getEnglishName());

            songPrwTv.setOnClickListener(v -> {
                if (!track.isInStorage()) {
//                    onTrackAdapter.onPreviewClick(track);
                    playPrw(track);
                }
            });

            songActionTv.setOnClickListener(v -> {
                if (track.isInStorage()) {
                    onTrackAdapter.onPlayClick(realmDownloadSong, playingSong -> {
                        if (playingSong.getSongId() == realmDownloadSong.getId())
                            if (playingSong.isPlay()) {
                                songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_pause));
                            } else {
                                songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_beeptunes_play));
                            }
                        else
                            songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_beeptunes_play));

                    });

                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }

                } else {
                    track.setSavedName(track.getId() + ".mp3");

                    onTrackAdapter.onDownloadClick(track, downloadSong -> {
                        if (downloadSong.getId().equals(track.getId())) {
                            switch (downloadSong.getDownloadStatus()) {
                                case STATUS_START:
                                    if (downloadSong.getId().equals(track.getId())) {
                                        rotate.setDuration(1000);
                                        rotate.setRepeatCount(Animation.INFINITE);
                                        songActionTv.startAnimation(rotate);
                                    }
                                    break;
                                case STATUS_COMPLETE:

                                    if (downloadSong.getId().equals(track.getId())) {
                                        rotate.cancel();
                                        songPrwTv.setText(itemView.getContext().getResources().getString(R.string.icon_music));
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    track.setInStorage(true);
                                    songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_beeptunes_play));
                                    break;
                                case STATUS_ERROR:
                                    break;
                                case STATUS_DOWNLOADING:
                                    if (downloadSong.getId().equals(track.getId())) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        progressBar.setProgress(downloadSong.getDownloadProgress());
                                    }
                                    break;
                            }
                        }
                    });
                }
            });

            realmDownloadSong = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmDownloadSong.class).equalTo("id", track.getId()).findFirst();
            });

//            realmDownloadSong.addChangeListener(realm -> );
            progressBar.getIndeterminateDrawable().setColorFilter(itemView.getContext().getResources().getColor(R.color.beeptunes_primary), PorterDuff.Mode.SRC_IN);
        }

        private void playPrw(Track track) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(track.getPreviewUrl());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(mp -> {
                        mediaPlayer.start();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource(track.getPreviewUrl());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(mp -> {
                        mediaPlayer.start();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
