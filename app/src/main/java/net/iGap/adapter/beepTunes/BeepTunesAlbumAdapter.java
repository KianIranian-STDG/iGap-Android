package net.iGap.adapter.beepTunes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.beepTunes.main.BeepTunesMainFragment;
import net.iGap.module.api.beepTunes.Album;

import java.util.ArrayList;
import java.util.List;

public class BeepTunesAlbumAdapter extends RecyclerView.Adapter<BeepTunesAlbumAdapter.ItemViewHolder> {
    private List<Album> albums = new ArrayList<>();
    private BeepTunesMainFragment.OnItemClick onItemClick;

    public void setOnItemClick(BeepTunesMainFragment.OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_track, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        Glide.with(G.context)
                .load(albums.get(i).getImage())
                .fitCenter().centerInside()
                .error(R.drawable.ic_error)
                .into(itemViewHolder.itemIv);
        itemViewHolder.bindItem(albums.get(i));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemIv;
        private TextView itemTitle;
        private TextView itemDetail;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIv = itemView.findViewById(R.id.iv_trackItem);
            itemTitle = itemView.findViewById(R.id.tv_trackItem_name);
            itemDetail = itemView.findViewById(R.id.tv_trackItem_artistName);
        }

        void bindItem(Album album) {
            itemTitle.setText(album.getName());
            itemDetail.setText(album.getArtists().get(0).getName());

            itemView.setOnClickListener(v -> onItemClick.onClick(album));
        }
    }
}
