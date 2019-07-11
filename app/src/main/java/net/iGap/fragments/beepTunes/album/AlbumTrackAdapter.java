package net.iGap.fragments.beepTunes.album;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.api.beepTunes.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlbumTrackAdapter extends RecyclerView.Adapter<AlbumTrackAdapter.TrackViewHolder> {
    private List<Track> tracks = new ArrayList<>();
    private boolean isPlaying = false;
    private MediaPlayer mp = new MediaPlayer();

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
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

    class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView songNameTv;
        private TextView songPriceTv;
        private TextView songActionTv;
        private TextView songPrwTv;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTv = itemView.findViewById(R.id.tv_itemSong_title);
            songPriceTv = itemView.findViewById(R.id.tv_itemSong_price);
            songActionTv = itemView.findViewById(R.id.tv_itemSong_action);
            songPrwTv = itemView.findViewById(R.id.tv_itemSong_prw);
        }

        void bindTracks(Track track) {
            if (G.isAppRtl) {
                songNameTv.setText(track.getName());

                if (track.getPrice() != 0)
                    songPriceTv.setText(track.getPrice().toString() + itemView.getContext().getResources().getString(R.string.toman));

                songActionTv.setText("ï");
                songPrwTv.setText(itemView.getContext().getResources().getString(R.string.play_icon));
            }


            itemView.setOnClickListener(v -> {
                if (!mp.isPlaying()) {
                    songPrwTv.setText("Ä");
                    try {
                        mp.setDataSource(track.getPreviewUrl());
                        mp.prepare();
                        mp.start();
                    } catch (IOException e) {
                        Log.e("beepTunes song player", e.getMessage());
                    }
                }else {
                    stopPlaying();
                    isPlaying = false;
                    songPrwTv.setText(itemView.getContext().getResources().getString(R.string.play_icon));
                }

            });

        }

        private void stopPlaying() {
            mp.stop();
            mp.reset();
        }
    }
}
