/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hanks.library.AnimateCheckBox;

import net.iGap.R;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.module.structs.StructExplorerItem;

import java.io.File;
import java.util.List;

import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class AdapterExplorer extends RecyclerView.Adapter<AdapterExplorer.ViewHolder> {

    private List<StructExplorerItem> items;
    private OnItemClickListenerExplorer onItemClickListener;

    public AdapterExplorer(List<StructExplorerItem> list, OnItemClickListenerExplorer onItemClickListener) {
        items = list;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        StructExplorerItem rowItem = items.get(position);
        holder.checkBox.setChecked(rowItem.isSelected);
        holder.txtTitle.setText(rowItem.name);
        holder.imageView.setImageResource(rowItem.image);
        if (rowItem.backColor != 0) holder.imageView.setBackgroundResource(rowItem.backColor);

        if (rowItem.isFolderOrFile && (rowItem.image == R.drawable.ic_fm_image || rowItem.image == R.drawable.ic_fm_video)) {
            holder.imageView.setPadding(0, 0, 0, 0);
            ImageLoadingServiceInjector.inject().loadImage(holder.imageView, rowItem.path, rowItem.image);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            int padding = i_Dp(R.dimen.dp10);
            holder.imageView.setPadding(padding, padding, padding, padding);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        if (rowItem.description != null) {
            holder.txtSubtitle.setVisibility(View.VISIBLE);
            holder.txtSubtitle.setText(rowItem.description);
        } else {
            holder.txtSubtitle.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            StructExplorerItem item = items.get(holder.getAdapterPosition());
            if (item.isFolderOrFile) {
                if (new File(item.path).isDirectory()) {
                    onItemClickListener.onFolderClicked(item.path, holder.getAdapterPosition());
                } else {
                    if(items.get(holder.getAdapterPosition()).isSelected){
                        items.get(holder.getAdapterPosition()).isSelected = false;
                    }else {
                        items.get(holder.getAdapterPosition()).isSelected = true;
                    }
                    onItemClickListener.onFileClicked(item.path, holder.getAdapterPosition() , items.get(holder.getAdapterPosition()).isSelected);
                }
            } else {
                onItemClickListener.onGalleryClicked(item.name, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_layout_explorer, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtSubtitle;
        private ImageView imageView;
        private AnimateCheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.sle_sub_textView1);
            txtSubtitle = itemView.findViewById(R.id.sle_sub_textView2);
            imageView = itemView.findViewById(R.id.sle_sub_imageView1);
            checkBox = itemView.findViewById(R.id.checkSelect);

        }
    }

    public interface OnItemClickListenerExplorer {

        void onFileClicked(String path, int position , boolean isSelected);

        void onFolderClicked(String path, int position);

        void onGalleryClicked(String type, int position);
    }
}
