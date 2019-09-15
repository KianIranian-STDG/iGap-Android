package net.iGap.adapter.beepTunes;

import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.realm.RealmDownloadSong;

import java.io.File;
import java.util.List;

import static net.iGap.G.context;

public class BeepTunesLocalSongAdapter extends RecyclerView.Adapter<BeepTunesLocalSongAdapter.LocalSongViewHolder> {
    private List<RealmDownloadSong> downloadSongs;
    private OnLocalSongAdapterCallBack callBack;

    public BeepTunesLocalSongAdapter(List<RealmDownloadSong> downloadSongs, OnLocalSongAdapterCallBack callBack) {
        this.downloadSongs = downloadSongs;
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public LocalSongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_track, viewGroup, false);
        return new LocalSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalSongViewHolder localSongViewHolder, int i) {
        localSongViewHolder.bindSong(downloadSongs.get(i));
    }

    @Override
    public int getItemCount() {
        return downloadSongs.size();
    }

    @FunctionalInterface
    public interface OnLocalSongAdapterCallBack {
        void OnLocalSongClick(RealmDownloadSong realmDownloadSong);
    }

    class LocalSongViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemIv;
        private TextView itemTitle;
        private TextView itemDetail;

        LocalSongViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIv = itemView.findViewById(R.id.iv_trackItem);
            itemTitle = itemView.findViewById(R.id.tv_trackItem_name);
            itemDetail = itemView.findViewById(R.id.tv_trackItem_artistName);
        }

        void bindSong(RealmDownloadSong realmDownloadSong) {
            getSongInfo(realmDownloadSong.getPath());
            itemView.setOnClickListener(v -> callBack.OnLocalSongClick(realmDownloadSong));
        }

        private void getSongInfo(String path) {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            Uri uri = Uri.fromFile(new File(path));
            if (uri != null) {
                mediaMetadataRetriever.setDataSource(context, uri);
                itemTitle.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                itemDetail.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                if (data != null)
                    itemIv.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
            }
        }
    }
}
