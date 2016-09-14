package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.iGap.R;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class ImageItem extends AbstractChatItem<ImageItem, ImageItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public ImageItem(ProtoGlobal.Room.Type type) {
        super(true, type);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutImage;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_image;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.imvPicture.setImageResource(Integer.parseInt(mMessage.filePath));
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imvPicture;

        public ViewHolder(View view) {
            super(view);

            imvPicture = (ImageView) view.findViewById(R.id.shli_imv_image);
        }
    }
}
