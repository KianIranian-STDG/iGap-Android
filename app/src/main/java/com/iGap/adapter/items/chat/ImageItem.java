package com.iGap.adapter.items.chat;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.iGap.R;
import com.iGap.helper.ImageHelper;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.image.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, (int) holder.itemView.getResources().getDimension(R.dimen.chatMessageImageCorner)));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.attachment != null) {
            ((FrameLayout) holder.image.getParent()).setMinimumWidth(mMessage.attachment.width);
            ((FrameLayout) holder.image.getParent()).setMinimumHeight(mMessage.attachment.height);
        }

        setOnClick(holder, holder.image, ProtoGlobal.RoomMessageType.IMAGE);
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;

        public ViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
