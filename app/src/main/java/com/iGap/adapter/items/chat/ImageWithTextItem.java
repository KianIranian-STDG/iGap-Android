package com.iGap.adapter.items.chat;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.ImageHelper;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class ImageWithTextItem extends AbstractChatItem<ImageWithTextItem, ImageWithTextItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public ImageWithTextItem(ProtoGlobal.Room.Type type) {
        super(true, type);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutImageWithText;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_image_with_text;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.messageText.setText(mMessage.messageText);
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadFromLocal(final ViewHolder holder, String localPath) {
        super.onLoadFromLocal(holder, localPath);
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.imvPicture, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.imvPicture.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, (int) holder.itemView.getResources().getDimension(R.dimen.chatMessageImageCorner)));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imvPicture;
        protected TextView messageText;

        public ViewHolder(View view) {
            super(view);

            imvPicture = (ImageView) view.findViewById(R.id.shli_imv_image);
            messageText = (TextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
        }
    }
}
