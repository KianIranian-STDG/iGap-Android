/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items;

import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.R;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.interfaces.OnPathAdapterBottomSheet;

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
//
//        Glide.with(holder.imgSrc.getContext())
//                .load("file://" + mList.getPath())
//                .into(holder.imgSrc);

        ImageLoadingServiceInjector.inject().loadImage(holder.imgSrc, mList.getPath(), true);

        if (mList.isSelected) {
            holder.checkBoxSelect.setChecked(false);
        } else {
            holder.checkBoxSelect.setChecked(true);
        }

        holder.checkBoxSelect.setOnClickListener(v -> {
            holder.checkBoxSelect.setChecked(holder.checkBoxSelect.isChecked());
            onPathAdapterBottomSheet.path(mList.getPath(), holder.checkBoxSelect.isChecked(), false, mList, mList.getId());
            mList.setSelected(!holder.checkBoxSelect.isChecked());
        });

        holder.cr.setOnClickListener(v -> {
            onPathAdapterBottomSheet.path(mList.getPath(), holder.checkBoxSelect.isChecked(), true, mList, mList.getId());
        });
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this mList. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected AppCompatCheckBox checkBoxSelect;
        private CardView cr;
        private ImageView imgSrc;

        public ViewHolder(View view) {
            super(view);

            cr = view.findViewById(R.id.card_view);
            imgSrc = view.findViewById(R.id.img_gallery);
            checkBoxSelect = view.findViewById(R.id.cig_checkBox_select_user);
        }
    }

}