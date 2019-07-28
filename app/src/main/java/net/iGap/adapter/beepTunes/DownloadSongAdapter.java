package net.iGap.adapter.beepTunes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.realm.RealmDownloadSong;

import java.util.ArrayList;
import java.util.List;

public class DownloadSongAdapter extends RecyclerView.Adapter<DownloadSongAdapter.TrackViewHolder> {
    private List<RealmDownloadSong> downloadSongs = new ArrayList<>();
    private OnDownloadSongCallBack callBack;

    public void setDownloadSongs(List<RealmDownloadSong> downloadSongs) {
        this.downloadSongs = downloadSongs;
        notifyDataSetChanged();
    }

    public void setCallBack(OnDownloadSongCallBack callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_sync_song, viewGroup, false);
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

    @FunctionalInterface
    public interface OnDownloadSongCallBack {
        void onClick(RealmDownloadSong realmDownloadSong);
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView songNameTv;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTv = itemView.findViewById(R.id.tv_syncSong_title);
        }

        void bindTracks(RealmDownloadSong downloadSong) {

            songNameTv.setText(downloadSong.getDisplayName());

            itemView.setOnClickListener(v -> callBack.onClick(downloadSong));
        }
    }
}
