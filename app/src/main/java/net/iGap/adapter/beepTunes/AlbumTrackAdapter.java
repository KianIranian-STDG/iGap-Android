package net.iGap.adapter.beepTunes;

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
import net.iGap.interfaces.OnTrackClick;
import net.iGap.module.api.beepTunes.DownloadSong;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.realm.RealmDownloadSong;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AlbumTrackAdapter extends RecyclerView.Adapter<AlbumTrackAdapter.TrackViewHolder> {
    private List<Track> tracks = new ArrayList<>();
    private OnTrackClick onTrackClick;
    private Realm realm;


    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    public void setOnTrackClick(OnTrackClick onTrackClick) {
        this.onTrackClick = onTrackClick;
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
                tracks.get(i).setInStorage(true);
                tracks.get(i).setDownloadStatus(DownloadSong.STATUS_STOP);
                notifyItemChanged(i);
            }
        }
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
        trackViewHolder.bindTracks(tracks.get(i));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void onDestroy() {
        realm.close();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
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
            if (realm.where(RealmDownloadSong.class).equalTo("id", track.getId()).findFirst() != null) {
                track.setInStorage(true);
            }

            if (track.isInStorage()) {
                songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_play));
            } else {
                songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_sync));
            }

            songActionTv.setOnClickListener(v -> {
                if (track.isInStorage()) {

                    onTrackClick.onPlayClick();
                } else {
                    track.setName(track.getId() + ".mp3");
                    onTrackClick.onDownloadClick(track);
                }
            });

            songActionTv.setOnLongClickListener(v -> {
                realm.executeTransactionAsync(realm -> {
                    RealmDownloadSong downloadSong = new RealmDownloadSong();
                    downloadSong.setId(track.getId());
                    realm.copyToRealmOrUpdate(downloadSong);
                });
                return false;
            });

            if (track.getDownloadStatus() == DownloadSong.STATUS_START) {
                startDownload();
            } else {
                stopDownload();
            }
        }

        private void stopDownload() {
            rotate.cancel();
        }

        private void startDownload() {
            rotate.setDuration(1000);
            rotate.setRepeatCount(Animation.INFINITE);
            songActionTv.startAnimation(rotate);
        }
    }
}
