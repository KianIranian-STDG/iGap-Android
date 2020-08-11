package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.model.GalleryMusicModel;
import net.iGap.module.CircleImageView;
import net.iGap.observers.interfaces.GalleryItemListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterGalleryMusic extends RecyclerView.Adapter<AdapterGalleryMusic.MusicGalleryViewHolder> {

    private boolean isMultiSelect;
    private List<GalleryMusicModel> musicsItem = new ArrayList<>();
    private List<GalleryMusicModel> mSelectedMusics = new ArrayList<>();
    private GalleryItemListener listener;

    public AdapterGalleryMusic() {

    }

    @NonNull
    @Override
    public MusicGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicGalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_music, parent, false));
    }

    public boolean getMultiSelectState() {
        return isMultiSelect;
    }

    public void setMultiSelectState(boolean multiSelect) {
        isMultiSelect = multiSelect;
        notifyDataSetChanged();
    }

    public void setMusicsItem(List<GalleryMusicModel> musicsItem) {
        this.musicsItem = musicsItem;
        notifyDataSetChanged();
    }

    public List<GalleryMusicModel> getMusicsItem() {
        return musicsItem;
    }

    public List<GalleryMusicModel> getSelectedMusics() {
        return mSelectedMusics;
    }

    public void setListener(GalleryItemListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicGalleryViewHolder holder, int position) {

        holder.subtitle.setText(musicsItem.get(position).getArtist());
        holder.title.setText(musicsItem.get(position).getTitle());
        holder.itemView.setOnClickListener(v ->
                listener.onItemClicked(musicsItem.get(holder.getAdapterPosition()).getPath(), musicsItem.get(holder.getAdapterPosition()).getId() + "")
        );
    }

    @Override
    public int getItemCount() {
        return musicsItem.size();
    }

    class MusicGalleryViewHolder extends RecyclerView.ViewHolder {

        TextView title, subtitle;
        CircleImageView cover;

        public MusicGalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            cover = itemView.findViewById(R.id.play_bg);
        }
    }
}