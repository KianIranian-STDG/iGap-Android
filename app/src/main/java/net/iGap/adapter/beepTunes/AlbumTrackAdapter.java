package net.iGap.adapter.beepTunes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_CANCEL;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_COMPLETE;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_DOWNLOADING;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_ERROR;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_PAUSE;
import static net.iGap.module.api.beepTunes.DownloadSong.STATUS_START;

public class AlbumTrackAdapter extends RecyclerView.Adapter<AlbumTrackAdapter.TrackViewHolder> {
    private static final String TAG = "aabolfazlAdapter";
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

    @FunctionalInterface
    public interface OnSongProgress {
        void progress(DownloadSong downloadSong);
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        //        private RotateAnimation rotate;
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

//            rotate = new RotateAnimation(
//                    0, 360,
//                    Animation.RELATIVE_TO_SELF, 0.5f,
//                    Animation.RELATIVE_TO_SELF, 0.5f
//            );
        }

        void bindTracks(Track track) {
            realmDownloadSong = realm.where(RealmDownloadSong.class).equalTo("id", track.getId()).findFirst();
            if (realmDownloadSong != null) {
                track.setInStorage(true);
            }


            if (track.isInStorage()) {
                songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_play));
            } else {
                songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_sync));
            }

            songNameTv.setText(track.getEnglishName());

            songActionTv.setOnClickListener(v -> {
                if (track.isInStorage()) {
                    onTrackClick.onPlayClick(realmDownloadSong);
                } else {
                    track.setSavedName(track.getId() + ".mp3");

                    onTrackClick.onDownloadClick(track, downloadSong -> {
                        if (downloadSong.getId().equals(track.getId())) {
                            switch (downloadSong.getDownloadStatus()) {
                                case STATUS_START:
                                    break;
                                case STATUS_CANCEL:
                                    break;
                                case STATUS_PAUSE:
                                    break;
                                case STATUS_COMPLETE:
                                    progressBar.setVisibility(View.GONE);
                                    track.setInStorage(true);
                                    songActionTv.setText(itemView.getContext().getResources().getString(R.string.icon_play));
                                    break;
                                case STATUS_ERROR:
                                    break;
                                case STATUS_DOWNLOADING:
                                    progressBar.setVisibility(View.VISIBLE);
                                    progressBar.setProgress(downloadSong.getDownloadProgress());
                                    break;
                            }
                        }
                    });

                }
            });
            realm.addChangeListener(realm -> realmDownloadSong = realm.where(RealmDownloadSong.class).equalTo("id", track.getId()).findFirst());
        }

//        private void stopDownload() {
//            rotate.cancel();
//        }
//
//        private void startDownload() {
//            rotate.setDuration(1000);
//            rotate.setRepeatCount(Animation.INFINITE);
//            songActionTv.startAnimation(rotate);
//        }
    }
}
