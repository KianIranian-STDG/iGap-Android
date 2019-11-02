package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnRotateImage;
import net.iGap.model.GalleryItemModel;
import net.iGap.module.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class AdapterGallery extends RecyclerView.Adapter<AdapterGallery.ViewHolderGallery> {

    private List<GalleryItemModel> items = new ArrayList<>();
    private SingleLiveEvent<Integer> listener = new SingleLiveEvent<>();

    public AdapterGallery() {

    }

    public void setItems(List<GalleryItemModel> items) {
        this.items = items;
    }

    public SingleLiveEvent<Integer> getClickListener() {
       return listener ;
    }

    @NonNull
    @Override
    public ViewHolderGallery onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_image , parent , false);
        return new ViewHolderGallery(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGallery holder, int position) {
        holder.bind(items.get(holder.getAdapterPosition()) , holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolderGallery extends RecyclerView.ViewHolder{

        private TextView caption ;
        private ImageView image ;
        private AppCompatCheckBox check ;

        public ViewHolderGallery(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            caption = itemView.findViewById(R.id.caption);
            check = itemView.findViewById(R.id.check);
        }

        public void bind(GalleryItemModel item , int pos){

            caption.setVisibility(item.isFolder() ? View.VISIBLE : View.GONE);
            caption.setText(item.getCaption());
            check.setVisibility(item.isSelect() ? View.VISIBLE : View.GONE);
            check.setChecked(item.isSelect());
            image.setOnClickListener(v -> listener.setValue(pos));

            //rotate and load image
            ImageHelper.correctRotateImage(item.getAddress(), true, new OnRotateImage() {
                @Override
                public void startProcess() {
                    //nothing
                }

                @Override
                public void success(String newPath) {
                    G.handler.post(() -> G.imageLoader.displayImage("file://" + item.getAddress() , image));
                }
            });
        }
    }
}
