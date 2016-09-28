package com.iGap.adapter.items.chat;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

        if (mMessage.attachment.existsOnLocal()) {
            ImageLoader.getInstance().displayImage(suitablePath(mMessage.attachment.localPath), holder.imvPicture, new ImageLoadingListener() {
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
        } else {
            // TODO: 9/28/2016 [Alireza Eskandarpour Shoferi] request to download file and set progress, then update localPath field
            // behtare dokme download dashte bashe ke click kard, download kone, niaz be UI darim
        }
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
        protected ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);

            imvPicture = (ImageView) view.findViewById(R.id.shli_imv_image);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);

            progressBar.setIndeterminate(false);
            progressBar.setProgress(0);
        }
    }
}
