package net.iGap.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterGallery extends RecyclerView.Adapter<AdapterGallery.ViewHolderGallery> {

    @NonNull
    @Override
    public ViewHolderGallery onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGallery holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolderGallery extends RecyclerView.ViewHolder{

        public ViewHolderGallery(@NonNull View itemView) {
            super(itemView);
        }
    }
}
