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

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.viewmodel.ChatBackgroundViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class AdapterSolidChatBackground extends RecyclerView.Adapter<AdapterSolidChatBackground.ViewHolder> {


    private ArrayList<String> mList;
    /*private ChatBackgroundViewModel.OnImageClick onImageClick;*/

    public AdapterSolidChatBackground(ArrayList<String> List/*, ChatBackgroundViewModel.OnImageClick onImageClick*/) {
        this.mList = List;
        /*this.onImageClick = onImageClick;*/
        this.mList.addAll(new ArrayList<>(Arrays.asList("#2962ff", "#00b8d4",
                "#b71c1c", "#e53935", "#e57373",
                "#880e4f", "#d81b60", "#f06292",
                "#4a148c", "#8e24aa", "#ba68c8",
                "#311b92", "#5e35b1", "#9575cd",
                "#1a237e", "#3949ab", "#7986cb",
                "#0d47a1", "#1e88e5", "#64b5f6",
                "#01579b", "#039be5", "#4fc3f7",
                "#006064", "#00acc1", "#4dd0e1",
                "#004d40", "#00897b", "#4db6ac",
                "#1b5e20", "#43a047", "#81c784",
                "#33691e", "#7cb342", "#aed581",
                "#827717", "#c0ca33", "#dce775",
                "#f57f17", "#fdd835", "#fff176",
                "#ff6f00", "#ffb300", "#ffd54f",
                "#e65100", "#fb8c00", "#fb8c00",
                "#bf360c", "#f4511e", "#ff8a65",
                "#3e2723", "#6d4c41", "#a1887f",
                "#212121", "#757575", "#e0e0e0",
                "#263238", "#546e7a", "#90a4ae")));
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_background_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {
        /*holder.imageView.setOnClickListener(v -> onImageClick.onClick(holder.getAdapterPosition()));*/
        holder.imageView.setBackgroundColor(Color.parseColor(mList.get(position)));
    }

    @Override
    public int getItemViewType(int position) {
        return mList.size();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            /*imageView = itemView.findViewById(R.id.imgBackgroundImage);*/
        }
    }
}
