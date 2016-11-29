package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import io.github.meness.emoji.EmojiTextView;

import static com.iGap.module.AndroidUtils.suitablePath;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VideoWithTextItem
        extends AbstractMessage<VideoWithTextItem, VideoWithTextItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VideoWithTextItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVideoWithText;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_video_with_text;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        int[] dimens = new int[2];
        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(),
                        mMessage.forwardedFrom.getAttachment().getWidth(), mMessage.forwardedFrom.getAttachment().getHeight());
                holder.duration.setText(
                        String.format(holder.itemView.getResources().getString(R.string.video_duration),
                                AppUtils.humanReadableDuration(mMessage.forwardedFrom.getAttachment().getDuration()).replace(".", ":"),
                                AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true)));
            }

            setTextIfNeeded(holder.messageText, mMessage.forwardedFrom.getMessage());
        } else {
            if (mMessage.attachment != null) {
                dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(),
                        mMessage.attachment.width, mMessage.attachment.height);
                holder.duration.setText(
                        String.format(holder.itemView.getResources().getString(R.string.video_duration),
                                AppUtils.humanReadableDuration(mMessage.attachment.duration).replace(".", ":"),
                                AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true)));
            }

            setTextIfNeeded(holder.messageText, mMessage.messageText);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimens[0], ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams layoutParamsForParentParent = new FrameLayout.LayoutParams(dimens[0], ViewGroup.LayoutParams.WRAP_CONTENT);
        ((ViewGroup) holder.image.getParent()).setLayoutParams(layoutParams);
        ((ViewGroup) holder.image.getParent().getParent()).setLayoutParams(layoutParamsForParentParent);
        holder.image.getParent().requestLayout();
        holder.image.getParent().getParent().requestLayout();
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);
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
        protected ImageView image;
        protected TextView duration;
        protected EmojiTextView messageText;

        public ViewHolder(View view) {
            super(view);

            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
            image = (ImageView) view.findViewById(R.id.thumbnail);
            duration = (TextView) view.findViewById(R.id.duration);
        }
    }
}
