package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import static com.iGap.module.AndroidUtils.suitablePath;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VideoItem extends AbstractMessage<VideoItem, VideoItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VideoItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVideo;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_video;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(),
                        mMessage.forwardedFrom.getAttachment().getWidth(), mMessage.forwardedFrom.getAttachment().getHeight());
                ((ViewGroup) holder.image.getParent()).setLayoutParams(
                        new LinearLayout.LayoutParams(dimens[0], dimens[1]));
                holder.image.getParent().requestLayout();
                holder.fileName.setText(mMessage.forwardedFrom.getAttachment().getName());
                holder.duration.setText(
                        String.format(holder.itemView.getResources().getString(R.string.video_duration),
                                AppUtils.humanReadableDuration(mMessage.forwardedFrom.getAttachment().getDuration()).replace(".", ":"),
                                AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true)));
            }
        } else {
            if (mMessage.attachment != null) {
                int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(),
                        mMessage.attachment.width, mMessage.attachment.height);
                ((ViewGroup) holder.image.getParent()).setLayoutParams(
                        new LinearLayout.LayoutParams(dimens[0], dimens[1]));
                holder.image.getParent().requestLayout();
                holder.fileName.setText(mMessage.attachment.name);
                holder.duration.setText(
                        String.format(holder.itemView.getResources().getString(R.string.video_duration),
                                AppUtils.humanReadableDuration(mMessage.attachment.duration).replace(".", ":"),
                                AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true)));
            }
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected RoundedImageView image;
        protected TextView fileName;
        protected TextView duration;

        public ViewHolder(View view) {
            super(view);

            image = (RoundedImageView) view.findViewById(R.id.thumbnail);
            fileName = (TextView) view.findViewById(R.id.fileName);
            duration = (TextView) view.findViewById(R.id.duration);
        }
    }
}
