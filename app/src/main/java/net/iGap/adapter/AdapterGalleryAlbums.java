package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnRotateImage;
import net.iGap.model.GalleryAlbumModel;
import net.iGap.module.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AdapterGalleryAlbums extends RecyclerView.Adapter<AdapterGalleryAlbums.ViewHolderGallery> {

    private List<GalleryAlbumModel> items = new ArrayList<>();
    private SingleLiveEvent<String> listener = new SingleLiveEvent<>();

    public AdapterGalleryAlbums() {

    }

    public void setItems(List<GalleryAlbumModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public SingleLiveEvent<String> getClickListener() {
        return listener;
    }

    @NonNull
    @Override
    public ViewHolderGallery onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_image, parent, false);
        return new ViewHolderGallery(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGallery holder, int position) {
        holder.bind(items.get(holder.getAdapterPosition()), holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolderGallery extends RecyclerView.ViewHolder {

        private TextView caption;
        private ImageView image;

        public ViewHolderGallery(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            caption = itemView.findViewById(R.id.caption);
        }

        public void bind(GalleryAlbumModel item, int pos) {

            caption.setText(item.getCaption());
            caption.setVisibility(View.VISIBLE);
            image.setOnClickListener(v -> listener.setValue("/" + item.getCaption()));

            //rotate and load image
            ImageHelper.correctRotateImage(item.getCover(), true, new OnRotateImage() {
                @Override
                public void startProcess() {
                    //nothing
                }

                @Override
                public void success(String newPath) {
                    G.handler.post(() -> G.imageLoader.displayImage("file://" + item.getCover(), image));
                }
            });
        }
    }
}
