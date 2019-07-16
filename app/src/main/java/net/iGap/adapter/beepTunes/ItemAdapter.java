package net.iGap.adapter.beepTunes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.module.api.beepTunes.Album;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Album> albums = new ArrayList<>();

    void setAlbums(List<Album> albums) {
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
        ImageLoadingService.load(albums.get(i).getImage(), itemViewHolder.itemIv);
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
        }
    }
}
