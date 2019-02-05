/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items.chat;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperRadius;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

import static java.lang.Boolean.TRUE;
import static net.iGap.module.AndroidUtils.suitablePath;

public class VideoWithTextItem extends AbstractMessage<VideoWithTextItem, VideoWithTextItem.ViewHolder> {

    public VideoWithTextItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVideoWithText;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.image.setTag(getCacheId(mMessage));

        super.bindView(holder, payloads);

        String text = "";

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.forwardedFrom.getAttachment().getDuration() * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true)));
            }

            text = mMessage.forwardedFrom.getMessage();
        } else {
            if (mMessage.attachment != null) {

                if (ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status) == ProtoGlobal.RoomMessageStatus.SENDING) {
                    holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true) + " " + G.context.getResources().getString(R.string.Uploading)));
                    AbstractMessage.processVideo(holder.duration, holder.itemView, mMessage);
                } else {
                    holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true) + ""));
                }
            }
            text = mMessage.messageText;
        }

        setTextIfNeeded(holder.itemView.findViewById(R.id.messageSenderTextMessage), text);

    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);

        if (holder.image != null && holder.image.getTag() != null && (holder.image.getTag()).equals(tag)) {
            if (fileType == LocalFileType.THUMBNAIL) {

                G.imageLoader.displayImage(suitablePath(localPath), holder.image);

                holder.image.setCornerRadius(HelperRadius.computeRadius(localPath));
            } else {

                MessageProgress progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
                AppUtils.setProgresColor(progress.progressBar);

                progress.setVisibility(View.VISIBLE);
                progress.withDrawable(R.drawable.ic_play, true);
            }
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemWithTextHolder implements IThumbNailItem, IProgress {
        protected MessageProgress progress;
        protected ReserveSpaceRoundedImageView image;
        protected TextView duration;

        public ViewHolder(View view) {
            super(view);
            boolean withText = true;
            FrameLayout frameLayout_642 = new FrameLayout(G.context);
            LinearLayout.LayoutParams layout_535 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLayout_642.setLayoutParams(layout_535);

            image = new ReserveSpaceRoundedImageView(G.context);
            image.setId(R.id.thumbnail);
            FrameLayout.LayoutParams layout_679 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layout_679);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setCornerRadius((int) G.context.getResources().getDimension(R.dimen.messageBox_cornerRadius));
            frameLayout_642.addView(image);

            duration = new TextView(G.context);
            duration.setId(R.id.duration);
            duration.setBackgroundResource(R.drawable.bg_message_image_time);
            duration.setGravity(Gravity.CENTER_VERTICAL);
            duration.setSingleLine(true);
            duration.setPadding(i_Dp(R.dimen.dp4), dpToPixel(1), i_Dp(R.dimen.dp4), dpToPixel(1));
            duration.setText("3:48 (4.5 MB)");
            duration.setAllCaps(TRUE);
            duration.setTextColor(G.context.getResources().getColor(R.color.gray10));
            setTextSize(duration, R.dimen.dp10);
            setTypeFace(duration);
            FrameLayout.LayoutParams layout_49 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_49.gravity = Gravity.LEFT | Gravity.TOP;
            layout_49.bottomMargin = -dpToPixel(2);
            layout_49.leftMargin = dpToPixel(5);
            layout_49.topMargin = dpToPixel(7);
            duration.setLayoutParams(layout_49);
            frameLayout_642.addView(duration);
            progress = getProgressBar(0);
            frameLayout_642.addView(progress, new FrameLayout.LayoutParams(i_Dp(R.dimen.dp48), i_Dp(R.dimen.dp48), Gravity.CENTER));
            m_container.addView(frameLayout_642);
            if (withText) {
                setLayoutMessageContainer();
            }
        }

        @Override
        public ImageView getThumbNailImageView() {
            return image;
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }
    }
}
