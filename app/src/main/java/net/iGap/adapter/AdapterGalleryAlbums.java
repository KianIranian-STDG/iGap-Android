package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnRotateImage;
import net.iGap.model.GalleryAlbumModel;
import net.iGap.model.GalleryPhotoModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterGalleryAlbums extends RecyclerView.Adapter<AdapterGalleryAlbums.ViewHolderGallery> {

    private List<GalleryAlbumModel> albumsItem = new ArrayList<>();
    private List<GalleryPhotoModel> photosItem = new ArrayList<>();
    private GalleryItemListener listener ;

    public AdapterGalleryAlbums() {

    }

    public void setAlbumsItem(List<GalleryAlbumModel> albumsItem) {
        this.albumsItem = albumsItem;
        notifyDataSetChanged();
    }

    public void setPhotosItem(List<GalleryPhotoModel> photosItem) {
        this.photosItem = photosItem;
        notifyDataSetChanged();
    }

    public void setListener(GalleryItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderGallery onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_image, parent, false);
        return new ViewHolderGallery(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGallery holder, int position) {

        if (albumsItem.size() > 0){
            holder.bindAlbums(albumsItem.get(holder.getAdapterPosition()));
        }else if (photosItem.size() > 0){
            holder.bindPhotos(photosItem.get(holder.getAdapterPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return albumsItem.size();
    }

    class ViewHolderGallery extends RecyclerView.ViewHolder {

        private TextView caption;
        private ImageView image;
        private CheckBox check;

        public ViewHolderGallery(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            caption = itemView.findViewById(R.id.caption);
            check = itemView.findViewById(R.id.check);
        }

        void bindAlbums(GalleryAlbumModel item) {

            caption.setText(item.getCaption());
            caption.setVisibility(View.VISIBLE);
            image.setOnClickListener(v -> listener.onItemClicked(item.getCaption()));

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

        void bindPhotos(GalleryPhotoModel item) {

            check.setVisibility(View.VISIBLE);
            check.setChecked(item.isSelect());
            image.setOnClickListener(v -> listener.onItemClicked(item.getAddress()));
            check.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onItemSelected(item , isChecked));

            //rotate and load image
            ImageHelper.correctRotateImage(item.getAddress(), true, new OnRotateImage() {
                @Override
                public void startProcess() {
                    //nothing
                }

                @Override
                public void success(String newPath) {
                    G.handler.post(() -> G.imageLoader.displayImage("file://" + item.getAddress(), image));
                }
            });
        }
    }

    public interface GalleryItemListener{

        void onItemClicked(String name);
        void onItemSelected(GalleryPhotoModel item ,boolean isCheck);

    }
}
