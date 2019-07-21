package net.iGap.adapter.beepTunes;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.fragments.beepTunes.album.AlbumFragment;
import net.iGap.interfaces.TrackOnclick;
import net.iGap.module.api.beepTunes.Track;

import java.util.ArrayList;
import java.util.List;

public class AlbumTrackAdapter extends RecyclerView.Adapter<AlbumTrackAdapter.TrackViewHolder> {
    private List<Track> tracks = new ArrayList<>();
    private boolean isPlaying = false;
    private MediaPlayer mp = new MediaPlayer();
    private TrackOnclick onclick;

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    public void setOnclick(TrackOnclick onclick) {
        this.onclick = onclick;
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

    class TrackViewHolder extends RecyclerView.ViewHolder implements AlbumFragment.OnProgress {
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
        }

        void bindTracks(Track track) {
            songActionTv.setOnClickListener(v -> {
                track.setName(track.getId() + ".mp3");
                onclick.onClick(track);
            });
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
