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

import net.iGap.R;
import net.iGap.module.customView.CheckBox;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.module.structs.StructFileManager;

import java.io.File;
import java.util.List;

import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class AdapterFileManager extends RecyclerView.Adapter<AdapterFileManager.ViewHolder> {

    private List<StructFileManager> items;
    private OnItemClickListenerExplorer onItemClickListener;

    public AdapterFileManager(List<StructFileManager> list, OnItemClickListenerExplorer onItemClickListener) {
        items = list;
        this.onItemClickListener = onItemClickListener;
    }

    public void removeAll() {
        items.clear();
        notifyDataSetChanged();
    }

    public void update(List<StructFileManager> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        StructFileManager rowItem = items.get(position);
        holder.checkBox.setChecked(rowItem.isSelected);
        holder.imageView.setImageResource(rowItem.image);
        if (rowItem.backColor != 0) holder.imageView.setBackgroundResource(rowItem.backColor);

        if (rowItem.isFolderOrFile && (rowItem.image == R.drawable.ic_fm_image_small || rowItem.image == R.drawable.ic_fm_video_small)) {
            if (rowItem.path.endsWith(".png")) holder.imageView.setImageResource(0);
            holder.imageView.setPadding(0, 0, 0, 0);
            ImageLoadingServiceInjector.inject().loadImage(holder.imageView, rowItem.path, rowItem.path.endsWith(".png") ? 0 : rowItem.image);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            int padding = i_Dp(R.dimen.dp10);
            holder.imageView.setPadding(padding, padding, padding, padding);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        if (rowItem.nameStr == null) {
            try {
                holder.txtTitle.setText(rowItem.name);
            } catch (Exception e) {
                holder.txtTitle.setText(holder.imageView.getContext().getResources().getString(R.string.unknown));
            }
        } else {
            holder.txtTitle.setText(rowItem.nameStr);
        }

        if (rowItem.descriptionStr != null) {
            holder.txtSubtitle.setText(rowItem.descriptionStr);
        } else {
            try {
                holder.txtSubtitle.setText(rowItem.description);
            } catch (Exception e) {
                holder.txtSubtitle.setText(holder.imageView.getContext().getResources().getString(R.string.unknown));
            }
        }

        holder.itemView.setOnClickListener(view -> {
            StructFileManager item = items.get(holder.getAdapterPosition());
            if (item.isFolderOrFile) {
                if (new File(item.path).isDirectory()) {
                    onItemClickListener.onFolderClicked(item.path, holder.getAdapterPosition());
                } else {
                    if (items.get(holder.getAdapterPosition()).isSelected) {
                        items.get(holder.getAdapterPosition()).isSelected = false;
                    } else {
                        items.get(holder.getAdapterPosition()).isSelected = true;
                    }
                    onItemClickListener.onFileClicked(item.path, holder.getAdapterPosition(), items.get(holder.getAdapterPosition()).isSelected);
                }
            } else {
                onItemClickListener.onGalleryClicked(item.name, item.path, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_layout_file_manager, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle, txtSubtitle;
        private ImageView imageView;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.sle_sub_textView1);
            txtSubtitle = itemView.findViewById(R.id.sle_sub_textView2);
            imageView = itemView.findViewById(R.id.sle_sub_imageView1);
            checkBox = itemView.findViewById(R.id.checkSelect);
            checkBox.setVisibility(View.VISIBLE);

        }
    }

    public interface OnItemClickListenerExplorer {

        void onFileClicked(String path, int position, boolean isSelected);

        void onFolderClicked(String path, int position);

        void onGalleryClicked(int type, String path, int position);
    }
}
