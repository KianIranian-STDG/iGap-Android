package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.iGap.R;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.module.Utils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class ImageItem extends AbstractMessage<ImageItem, ImageItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public ImageItem(ProtoGlobal.Room.Type type, OnMessageViewClick messageClickListener) {
        super(true, type, messageClickListener);
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
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadFromLocal(holder, localPath, fileType);
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.attachment != null) {
            int[] dimens = Utils.scaleDimenWithSavedRatio(holder.itemView.getContext(), mMessage.attachment.width, mMessage.attachment.height);
            ((ViewGroup) holder.image.getParent()).setLayoutParams(new LinearLayout.LayoutParams(dimens[0], dimens[1]));
            holder.image.getParent().requestLayout();
        }


        setOnClick(holder, holder.image, ProtoGlobal.RoomMessageType.IMAGE);
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected RoundedImageView image;

        public ViewHolder(View view) {
            super(view);

            image = (RoundedImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
