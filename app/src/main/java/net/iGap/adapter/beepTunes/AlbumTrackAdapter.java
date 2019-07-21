package net.iGap.adapter.beepTunes;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.fragments.beepTunes.album.AlbumFragment;
import net.iGap.interfaces.TrackOnclick;
import net.iGap.module.api.beepTunes.DownloadSong;
import net.iGap.module.api.beepTunes.Track;

import java.util.ArrayList;
import java.util.List;

public class AlbumTrackAdapter extends RecyclerView.Adapter<AlbumTrackAdapter.TrackViewHolder> {
    private List<Track> tracks = new ArrayList<>();
    private boolean isPlaying = false;
    private MediaPlayer mp = new MediaPlayer();
    private TrackOnclick onclick;
    private TrackViewHolder viewHolder;

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    public void setOnclick(TrackOnclick onclick) {
        this.onclick = onclick;
    }

    public void startDownload(Long id) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getId().equals(id)) {
                tracks.get(i).setDownloadStatus(DownloadSong.STATUS_START);
                notifyItemChanged(i);
            }
        }
    }

    public void stopDownload(Long id) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getId().equals(id)) {
                tracks.get(i).setDownloadStatus(DownloadSong.STATUS_STOP);
                notifyItemChanged(i);
            }
        }
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_song, viewGroup, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder trackViewHolder, int i) {
        this.viewHolder = trackViewHolder;
        trackViewHolder.bindTracks(tracks.get(i));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder implements AlbumFragment.OnProgress {
        private TextView songNameTv;
        private TextView songPriceTv;
        private TextView songActionTv;
        private TextView songPrwTv;
        private ProgressBar progressBar;
        private RotateAnimation rotate;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTv = itemView.findViewById(R.id.tv_itemSong_title);
            songPriceTv = itemView.findViewById(R.id.tv_itemSong_price);
            songActionTv = itemView.findViewById(R.id.tv_itemSong_action);
            songPrwTv = itemView.findViewById(R.id.tv_itemSong_prw);
            progressBar = itemView.findViewById(R.id.pb_itemSong);

            rotate = new RotateAnimation(
                    0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
        }

        void bindTracks(Track track) {
            songActionTv.setOnClickListener(v -> {
                track.setName(track.getId() + ".mp3");
                onclick.onClick(track);
            });

            if (track.getDownloadStatus() == DownloadSong.STATUS_START) {
                startDownload();
            } else {
                stopDownload();
            }
        }

        private void stopDownload() {
            rotate.cancel();
            songActionTv.setText("ò");
        }

        private void startDownload() {
            rotate.setDuration(1000);
            rotate.setRepeatCount(Animation.INFINITE);
            songActionTv.startAnimation(rotate);
        }

        @Override
        public void progress(boolean visibility) {
            if (visibility)
                progressBar.setVisibility(View.VISIBLE);
            else
                progressBar.setVisibility(View.GONE);
        }
    }
}
