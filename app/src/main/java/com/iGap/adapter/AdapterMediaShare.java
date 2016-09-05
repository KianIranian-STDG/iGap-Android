package com.iGap.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.StructMediaShare;

import java.util.List;

/**
 * Created by Rahmani on 9/4/2016.
 */
public class AdapterMediaShare extends RecyclerView.Adapter<AdapterMediaShare.ViewHolder> {

    private List<StructMediaShare> items;

    public AdapterMediaShare(List<StructMediaShare> item) {
        this.items = item;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_media_share, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        StructMediaShare item = items.get(position);

        Bitmap b = BitmapFactory.decodeFile(G.imageFile.toString());
//        holder.imgMedia.setImageResource(R.mipmap.b);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgMedia;

        public ViewHolder(View itemView) {
            super(itemView);

            imgMedia = (ImageView) itemView.findViewById(R.id.mh_img_menuPopup);
        }
    }
}
