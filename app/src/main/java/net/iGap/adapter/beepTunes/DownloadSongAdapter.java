package net.iGap.adapter.beepTunes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.interfaces.OnTrackAdapter;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.realm.RealmDownloadSong;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class DownloadSongAdapter extends RecyclerView.Adapter<DownloadSongAdapter.TrackViewHolder> {
    private static final String TAG = "aabolfazlAdapter";
    private List<RealmDownloadSong> downloadSongs = new ArrayList<>();
    private OnTrackAdapter onTrackAdapter;
    private Realm realm;

    public void setDownloadSongs(List<RealmDownloadSong> downloadSongs) {
        this.downloadSongs = downloadSongs;
        notifyDataSetChanged();
    }

    public void setOnTrackAdapter(OnTrackAdapter onTrackAdapter) {
        this.onTrackAdapter = onTrackAdapter;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_song, viewGroup, false);
        realm = Realm.getDefaultInstance();
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder trackViewHolder, int i) {
        trackViewHolder.bindTracks(downloadSongs.get(i));
    }

    @Override
    public int getItemCount() {
        return downloadSongs.size();
    }

    public void onDestroy() {
        if (realm != null)
            realm.close();
    }

    @FunctionalInterface
    public interface OnSongPlay {
        void songStatus(PlayingSong playingSong);
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
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

        void bindTracks(RealmDownloadSong downloadSong) {

            songNameTv.setText(downloadSong.getSavedName());

            songActionTv.setOnClickListener(v -> {

            });
        }
    }
}
