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

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hanks.library.AnimateCheckBox;
import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnPathAdapterBottomSheet;
import net.iGap.interfaces.OnRotateImage;
import net.iGap.module.structs.StructBottomSheet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BottomSheetItem extends AbstractItem<BottomSheetItem, BottomSheetItem.ViewHolder> {

    public StructBottomSheet mList;
    private OnPathAdapterBottomSheet onPathAdapterBottomSheet;
    public boolean isChecked = false;

    public BottomSheetItem(StructBottomSheet item, OnPathAdapterBottomSheet OnPathAdapterBottomSheet) {
        this.mList = item;
        this.onPathAdapterBottomSheet = OnPathAdapterBottomSheet;
    }

    public StructBottomSheet getItem() {
        return mList;
    }

    public void setItem(StructBottomSheet item) {
        this.mList = item;
    }

    //The unique ID for this type of mList
    @Override
    public int getType() {
        return R.id.rcv_root_bottom_sheet;
    }

    //The layout to be used for this type of mList
    @Override
    public int getLayoutRes() {
        return R.layout.adapter_bottom_sheet;
    }

    //The logic to bind your data to the view

    @Override
    public void bindView(@NotNull final ViewHolder holder, @NotNull List payloads) {
        super.bindView(holder, payloads);

        ImageHelper.correctRotateImage(mList.getPath(), true, new OnRotateImage() {
            @Override
            public void startProcess() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.prgBottomSheet.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void success(String newPath) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.prgBottomSheet.setVisibility(View.GONE);
                        G.imageLoader.displayImage("file://" + newPath, holder.imgSrc);
                    }
                });

            }
        }); //rotate image


        if (mList.isSelected) {
            holder.checkBoxSelect.setChecked(false);
        } else {
            holder.checkBoxSelect.setChecked(true);
        }
        holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));

        holder.checkBoxSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.checkBoxSelect.isChecked()) {
                    holder.checkBoxSelect.setChecked(false);
                    holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                    onPathAdapterBottomSheet.path(mList.getPath(), false, false, mList, mList.getId());
                    mList.setSelected(true);
                } else {
                    holder.checkBoxSelect.setChecked(true);
                    holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.green));
                    onPathAdapterBottomSheet.path(mList.getPath(), true, false, mList, mList.getId());
                    mList.setSelected(false);
                }
            }
        });

        holder.cr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBoxSelect.isChecked()) {
                    holder.checkBoxSelect.setChecked(false);
                    holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.transparent));
                    onPathAdapterBottomSheet.path(mList.getPath(), false, true, mList, mList.getId());
                    mList.setSelected(false);
                } else {
                    holder.checkBoxSelect.setChecked(true);
                    holder.checkBoxSelect.setUnCheckColor(G.context.getResources().getColor(R.color.green));
                    onPathAdapterBottomSheet.path(mList.getPath(), true, true, mList, mList.getId());
                    mList.setSelected(true);
                }

            }
        });
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this mList. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected AnimateCheckBox checkBoxSelect;
        private CardView cr;
        private ImageView imgSrc;
        private ProgressBar prgBottomSheet;

        public ViewHolder(View view) {
            super(view);

            cr = view.findViewById(R.id.card_view);
            imgSrc = view.findViewById(R.id.img_gallery);
            checkBoxSelect = view.findViewById(R.id.cig_checkBox_select_user);
            prgBottomSheet = view.findViewById(R.id.prgBottomSheet);
        }
    }

}