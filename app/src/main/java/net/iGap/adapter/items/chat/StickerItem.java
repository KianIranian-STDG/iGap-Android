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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.eventbus.EventManager;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.emoji.HelperDownloadSticker;
import net.iGap.helper.downloadFile.IGDownloadFile;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.util.List;

import static net.iGap.module.AndroidUtils.suitablePath;

public class StickerItem extends AbstractMessage<StickerItem, StickerItem.ViewHolder> {

    public StickerItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.sticker_layout;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.itemView.setTag(structMessage.getAttachment().getToken());
        super.bindView(holder, payloads);

        holder.getChatBloke().setBackgroundResource(0);

        holder.image.setOnClickListener(v -> {

            if (FragmentChat.isInSelectionMode) {
                holder.itemView.performLongClick();
            } else {
                if (mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                    return;
                }
                if (mMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                    messageClickListener.onFailedMessageClick(v, structMessage, holder.getAdapterPosition());
                } else {
                    messageClickListener.onOpenClick(v, structMessage, holder.getAdapterPosition());
                }
            }
        });

        String path = HelperDownloadSticker.downloadStickerPath(structMessage.getAttachment().getToken(), structMessage.getAttachment().getName());
        if (new File(path).exists()) {
            Glide.with(holder.image.getContext()).load(suitablePath(path))
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .into(holder.image);
            /*G.imageLoader.displayImage(suitablePath(path), holder.image);*/
        } else {
            EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, (id, message) -> {
                if (id == EventManager.STICKER_DOWNLOAD) {

                    String filePath = (String) message[0];
                    String fileToken = (String) message[1];

                    if (holder.itemView.getTag().equals(fileToken)) {
                        G.handler.post(() -> {
                            Glide.with(holder.image.getContext()).load(suitablePath(filePath))
                                    .apply(new RequestOptions()
                                            .fitCenter()
                                            .format(DecodeFormat.PREFER_ARGB_8888))
                                    .into(holder.image);
                            /*G.imageLoader.displayImage(suitablePath(filePath), holder.image);*/
                        });
                    }
                }
            });

            IGDownloadFile.getInstance().startDownload(new IGDownloadFileStruct(structMessage.getAttachment().getCacheId(),
                    structMessage.getAttachment().getToken(), structMessage.getAttachment().getSize(), path));
        }

        holder.image.setOnLongClickListener(getLongClickPerform(holder));
        holder.progress.setVisibility(View.GONE);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends NewChatItemHolder implements IProgress, IThumbNailItem {
        protected ReserveSpaceRoundedImageView image;
        protected MessageProgress progress;

        public ViewHolder(View view) {
            super(view);

            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

            image = new ReserveSpaceRoundedImageView(getContext());
            image.setId(R.id.thumbnail);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setCornerRadius((int) G.context.getResources().getDimension(R.dimen.messageBox_cornerRadius));
            LinearLayout.LayoutParams layout_758 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layout_758);
            getContentBloke().addView(frameLayout);
            frameLayout.addView(image);
            image.reserveSpace(180, 180, ProtoGlobal.Room.Type.CHAT);

            progress = getProgressBar(view.getContext(), 0);
            frameLayout.addView(progress, new FrameLayout.LayoutParams(i_Dp(R.dimen.dp60), i_Dp(R.dimen.dp60), Gravity.CENTER));

        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }

        @Override
        public ImageView getThumbNailImageView() {
            return image;
        }
    }
}
