/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.HelperRadius;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.FontIconTextView;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;
import net.iGap.structs.MessageObject;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static net.iGap.module.AndroidUtils.suitablePath;

public class VideoWithTextItem extends AbstractMessage<VideoWithTextItem, VideoWithTextItem.ViewHolder> {

    public VideoWithTextItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
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
        super.bindView(holder, payloads);

        if (messageObject.status != MessageObject.STATUS_SENDING) {
            if (messageObject.forwardedMessage != null) {
                if (messageObject.forwardedMessage.attachment != null) {
                    holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (messageObject.forwardedMessage.attachment.duration * 1000L)), AndroidUtils.humanReadableByteCount(messageObject.forwardedMessage.attachment.size, true)));
                }
            } else {
                if (attachment != null) {// TODO: 12/29/20 MESSAGE_REFACTOR_NEED_TEST
                    if (messageObject.status == MessageObject.STATUS_SENDING) {
                        holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.humanReadableByteCount(messageObject.getAttachment().size, true) + " " + G.context.getResources().getString(R.string.Uploading), AndroidUtils.formatDuration((int) (messageObject.getAttachment().duration * 1000L))));
                    } else {
                        holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.humanReadableByteCount(messageObject.getAttachment().size, true) + "", AndroidUtils.formatDuration((int) (messageObject.getAttachment().duration * 1000L))));
                    }
                }
            }
        }

        holder.tempText = holder.duration.getText().toString();
        setTextIfNeeded(holder.messageView);

    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);
        if (fileType == LocalFileType.THUMBNAIL) {
            G.imageLoader.displayImage(suitablePath(localPath), holder.image);
//            Glide.with(holder.getContext()).asDrawable().load(suitablePath(localPath)).into(holder.image);
 //           ImageLoadingServiceInjector.inject().loadImage(holder.image, suitablePath(localPath));
        } else {
            AppUtils.setProgresColor(holder.progress.progressBar);
            holder.progress.setVisibility(View.VISIBLE);
            holder.progress.withDrawable(R.drawable.ic_play, true);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemWithTextHolder implements IThumbNailItem, IProgress {
        protected FontIconTextView more;
        protected MessageProgress progress;
        protected ReserveSpaceRoundedImageView image;
        public TextView duration;
        public String tempText;

        public ViewHolder(View view) {
            super(view);
            FrameLayout frameLayout_642 = new FrameLayout(view.getContext());
            LinearLayout.LayoutParams layout_535 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLayout_642.setLayoutParams(layout_535);

            image = new ReserveSpaceRoundedImageView(view.getContext());
            image.setId(R.id.thumbnail);
            FrameLayout.LayoutParams layout_679 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layout_679);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setCornerRadius(HelperRadius.computeRadius());
            frameLayout_642.addView(image);

            duration = new AppCompatTextView(view.getContext());
            duration.setId(R.id.duration);
            duration.setBackgroundResource(R.drawable.bg_message_image_time);
            duration.setGravity(Gravity.CENTER_VERTICAL);
            duration.setSingleLine(true);
            duration.setPadding(i_Dp(R.dimen.dp4), dpToPx(1), i_Dp(R.dimen.dp4), dpToPx(1));
            duration.setAllCaps(TRUE);
            duration.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray10));
            setTextSize(duration, R.dimen.verySmallTextSize);
            setTypeFace(duration);
            FrameLayout.LayoutParams layout_49 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_49.gravity = Gravity.LEFT | Gravity.TOP;
            layout_49.bottomMargin = -dpToPx(2);
            layout_49.leftMargin = dpToPx(5);
            layout_49.topMargin = dpToPx(7);
            duration.setLayoutParams(layout_49);
            frameLayout_642.addView(duration);

            more = new FontIconTextView(view.getContext());
            more.setId(R.id.more);
            //more.setBackgroundResource(R.drawable.bg_message_image_time);
            more.setGravity(Gravity.CENTER);
            more.setText(R.string.horizontal_more_icon);
            setTextSize(more, R.dimen.largeTextSize);
            more.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
            more.setPadding(i_Dp(R.dimen.dp8), i_Dp(R.dimen.dp8), i_Dp(R.dimen.dp12), i_Dp(R.dimen.dp8));
            FrameLayout.LayoutParams layout_50 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_50.gravity = Gravity.RIGHT | Gravity.TOP;
            more.setLayoutParams(layout_50);
            frameLayout_642.addView(more);

            progress = getProgressBar(view.getContext(), 0);
            frameLayout_642.addView(progress, new FrameLayout.LayoutParams(i_Dp(R.dimen.dp48), i_Dp(R.dimen.dp48), Gravity.CENTER));
            getContentBloke().addView(frameLayout_642);
            setLayoutMessageContainer();
        }

        public FontIconTextView getMoreButton() {
            return more;
        }

        @Override
        public ImageView getThumbNailImageView() {
            return image;
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }

        @Override
        public TextView getProgressTextView() {
            return duration;
        }

        @Override
        public String getTempTextView() {
            return tempText;
        }
    }
}
