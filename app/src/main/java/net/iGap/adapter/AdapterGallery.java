package net.iGap.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnRotateImage;
import net.iGap.model.GalleryAlbumModel;
import net.iGap.model.GalleryPhotoModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterGallery extends RecyclerView.Adapter<AdapterGallery.ViewHolderGallery> {

    private boolean isPhotoMode;
    private boolean isMultiSelect;
    private List<GalleryAlbumModel> albumsItem = new ArrayList<>();
    private List<GalleryPhotoModel> photosItem = new ArrayList<>();
    private List<GalleryPhotoModel> mSelectedPhotos = new ArrayList<>();
    private GalleryItemListener listener;

    public AdapterGallery(boolean isPhotoMode) {
        this.isPhotoMode = isPhotoMode;
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

    public void setMultiSelectState(boolean enable) {
        this.isMultiSelect = enable;
        if (!enable) mSelectedPhotos.clear();
        notifyDataSetChanged();
    }

    public boolean getMultiSelectState() {
        return isMultiSelect;
    }

    public List<GalleryPhotoModel> getSelectedPhotos() {
        return mSelectedPhotos;
    }

    @NonNull
    @Override
    public ViewHolderGallery onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_image, parent, false);
        return new ViewHolderGallery(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGallery holder, int position) {

        //use 2 state for gallery to decrease codes => ALBUM and PHOTO
        if (!isPhotoMode) {

            holder.caption.setText(albumsItem.get(position).getCaption());
            holder.caption.setVisibility(View.VISIBLE);

        } else {

            if (isMultiSelect) {
                holder.check.setVisibility(View.VISIBLE);
                holder.check.setChecked(mSelectedPhotos.contains(photosItem.get(position)));
            } else {
                holder.check.setChecked(false);
                holder.check.setVisibility(View.GONE);
            }

            holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!mSelectedPhotos.contains(photosItem.get(holder.getAdapterPosition())))
                        mSelectedPhotos.add(photosItem.get(holder.getAdapterPosition()));
                } else {
                    mSelectedPhotos.remove(photosItem.get(holder.getAdapterPosition()));
                }
            });

        }

        //handle item click
        holder.image.setOnClickListener(v -> {

            if (!isMultiSelect){
                listener.onItemClicked(
                        isPhotoMode ? photosItem.get(holder.getAdapterPosition()).getAddress() : albumsItem.get(holder.getAdapterPosition()).getCaption()
                );
            }else{
                holder.check.setChecked(!holder.check.isChecked());
            }

        });

        //rotate and load image
        ImageHelper.correctRotateImage(isPhotoMode ? photosItem.get(position).getAddress() : albumsItem.get(position).getCover(), true, new OnRotateImage() {
            @Override
            public void startProcess() {
                //nothing
            }

            @Override
            public void success(String newPath) {
                G.handler.post(() -> Glide.with(holder.image.getContext()).load(Uri.parse("file://" + newPath)).into(holder.image));
            }
        });
    }

    @Override
    public int getItemCount() {
        return isPhotoMode ? photosItem.size() : albumsItem.size();
    }

    class ViewHolderGallery extends RecyclerView.ViewHolder {

        TextView caption;
        ImageView image;
        CheckBox check;

        ViewHolderGallery(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            caption = itemView.findViewById(R.id.caption);
            check = itemView.findViewById(R.id.check);
        }

    }

    public interface GalleryItemListener {

        void onItemClicked(String name);

    }
}
