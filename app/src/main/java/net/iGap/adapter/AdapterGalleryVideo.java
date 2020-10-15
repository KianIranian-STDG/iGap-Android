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

import net.iGap.R;
import net.iGap.model.GalleryVideoModel;
import net.iGap.observers.interfaces.GalleryItemListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdapterGalleryVideo extends RecyclerView.Adapter<AdapterGalleryVideo.ViewHolderGallery> {

    private boolean isVideoMode;
    private boolean isMultiSelect;
    private List<GalleryVideoModel> videosItem = new ArrayList<>();
    private List<GalleryVideoModel> mSelectedVideos = new ArrayList<>();
    private GalleryItemListener listener;

    public AdapterGalleryVideo(boolean isVideoMode) {
        this.isVideoMode = isVideoMode;
    }

    public void setVideosItem(List<GalleryVideoModel> videosItem) {
        this.videosItem = videosItem;
        notifyDataSetChanged();
    }

    public void setListener(GalleryItemListener listener) {
        this.listener = listener;
    }

    public void setMultiSelectState(boolean enable) {
        this.isMultiSelect = enable;
        if (!enable) mSelectedVideos.clear();
        notifyDataSetChanged();
    }

    public List<GalleryVideoModel> getVideosItem() {
        return videosItem;
    }

    public boolean getMultiSelectState() {
        return isMultiSelect;
    }

    public List<GalleryVideoModel> getSelectedVideos() {
        return mSelectedVideos;
    }

    @NonNull
    @Override
    public ViewHolderGallery onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_video, parent, false);
        return new ViewHolderGallery(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGallery holder, int position) {

        if (!isVideoMode) {

            holder.play.setVisibility(View.GONE);
            holder.caption.setText(videosItem.get(position).getCaption());
            holder.caption.setVisibility(View.VISIBLE);

        } else {

            if (isMultiSelect) {
                holder.check.setVisibility(View.VISIBLE);
                holder.check.setChecked(mSelectedVideos.contains(videosItem.get(position)));
            } else {
                holder.check.setChecked(false);
                holder.check.setVisibility(View.GONE);
            }

            holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!mSelectedVideos.contains(videosItem.get(holder.getAdapterPosition())))
                        mSelectedVideos.add(videosItem.get(holder.getAdapterPosition()));
                } else {
                    mSelectedVideos.remove(videosItem.get(holder.getAdapterPosition()));
                }
            });

        }
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isMultiSelect) {
                    holder.check.setChecked(!holder.check.isChecked());
                    listener.onMultiSelect(mSelectedVideos.size());
                    setMultiSelectState(!getMultiSelectState());
                }
                return true;
            }
        });
        //handle item click
        holder.image.setOnClickListener(v -> {

            if (!isMultiSelect) {
                listener.onItemClicked(
                        isVideoMode ? videosItem.get(holder.getAdapterPosition()).getPath() : videosItem.get(holder.getAdapterPosition()).getCaption(),
                        isVideoMode ? null : videosItem.get(holder.getAdapterPosition()).getId()
                );
            } else {
                holder.check.setChecked(!holder.check.isChecked());
                listener.onMultiSelect(mSelectedVideos.size());
            }

        });

        //load image
        Glide.with(holder.image.getContext())
                .load(Uri.fromFile(new File(videosItem.get(position).getPath())))
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return videosItem.size();
    }

    class ViewHolderGallery extends RecyclerView.ViewHolder {

        TextView caption;
        ImageView image;
        ImageView play;
        CheckBox check;

        ViewHolderGallery(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            caption = itemView.findViewById(R.id.caption);
            check = itemView.findViewById(R.id.check);
            play = itemView.findViewById(R.id.play);
        }

    }

}
