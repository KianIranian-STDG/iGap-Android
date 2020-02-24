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
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.module.structs.StructExplorerItem;

import java.util.ArrayList;

import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class AdapterExplorer extends RecyclerView.Adapter<AdapterExplorer.ViewHolder> {

    private ArrayList<StructExplorerItem> items;
    private OnItemClickListenerExplorer onItemClickListener;

    public AdapterExplorer(ArrayList<StructExplorerItem> list, OnItemClickListenerExplorer onItemClickListener) {
        items = list;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        StructExplorerItem rowItem = items.get(position);
        holder.txtTitle.setText(rowItem.name);
        holder.imageView.setImageResource(rowItem.image);

        if (rowItem.image == R.drawable.ic_fm_image || rowItem.image == R.drawable.ic_fm_video) {
            holder.imageView.setBackgroundResource(0);
            holder.imageView.setPadding(0, 0, 0, 0);
            ImageLoadingServiceInjector.inject().loadImage(holder.imageView, rowItem.path, rowItem.image);
        } else {
            int padding = i_Dp(R.dimen.dp14);
            holder.imageView.setPadding(padding, padding, padding, padding);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_layout_explorer, parent, false));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.sle_sub_textView1);
            imageView = itemView.findViewById(R.id.sle_sub_imageView1);

            itemView.setOnClickListener(view -> onItemClickListener.onItemClick(view, getAdapterPosition()));
        }
    }

    public interface OnItemClickListenerExplorer {

        void onItemClick(View view, int position);
    }
}
