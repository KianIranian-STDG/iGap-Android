package com.iGap.adapter.items.chat;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.File;
import java.util.List;

import io.github.meness.emoji.EmojiTextView;
import io.meness.github.messageprogress.MessageProgress;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class GifWithTextItem extends AbstractMessage<GifWithTextItem, GifWithTextItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public GifWithTextItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public void onPlayPauseGIF(ViewHolder holder, String localPath) {
        super.onPlayPauseGIF(holder, localPath);

        ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play, true);

        GifDrawable gifDrawable = (GifDrawable) holder.image.getDrawable();
        if (gifDrawable != null) {
            if (gifDrawable.isPlaying()) {
                gifDrawable.pause();
                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            } else {
                gifDrawable.start();
                holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutGifWithText;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_gif_with_text;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadThumbnailFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        holder.image.setImageURI(Uri.fromFile(new File(localPath)));

        if (fileType == LocalFileType.FILE) {
            SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            if (sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0) == 1) {
                holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
            } else {
                if (holder.image.getDrawable() instanceof GifDrawable) {
                    GifDrawable gifDrawable = (GifDrawable) holder.image.getDrawable();
                    // to get first frame
                    gifDrawable.stop();
                    holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        int[] dimens = new int[2];
        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(),
                        mMessage.forwardedFrom.getAttachment().getWidth(), mMessage.forwardedFrom.getAttachment().getHeight());
            }

            setTextIfNeeded(holder.messageText, mMessage.forwardedFrom.getMessage());
        } else {
            if (mMessage.attachment != null) {
                dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(),
                        mMessage.attachment.width, mMessage.attachment.height);
            }

            setTextIfNeeded(holder.messageText, mMessage.messageText);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimens[0], ViewGroup.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams layoutParamsForParentParent = new FrameLayout.LayoutParams(dimens[0], ViewGroup.LayoutParams.WRAP_CONTENT);
        ((ViewGroup) holder.image.getParent()).setLayoutParams(layoutParams);
        ((ViewGroup) holder.image.getParent().getParent()).setLayoutParams(layoutParamsForParentParent);
        holder.image.getParent().requestLayout();
        holder.image.getParent().getParent().requestLayout();

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelected()) {
                    if (mMessage.status.equalsIgnoreCase(
                            ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                        return;
                    }
                    if (mMessage.status.equalsIgnoreCase(
                            ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                        messageClickListener.onFailedMessageClick(v, mMessage,
                                holder.getAdapterPosition());
                    } else {
                        if (mMessage.forwardedFrom != null && mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal()) {
                            onPlayPauseGIF(holder, mMessage.forwardedFrom.getAttachment().getLocalFilePath());
                        } else {
                            if (mMessage.attachment.isFileExistsOnLocal()) {
                                onPlayPauseGIF(holder, mMessage.attachment.getLocalFilePath());
                            }
                        }
                    }
                }
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return false;
            }
        });

        holder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return false;
            }
        });

        holder.messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelected()) {
                    if (mMessage.status.equalsIgnoreCase(
                            ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                        return;
                    }
                    if (mMessage.status.equalsIgnoreCase(
                            ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                        messageClickListener.onFailedMessageClick(v, mMessage,
                                holder.getAdapterPosition());
                    } else {
                        messageClickListener.onContainerClick(v, mMessage, holder.getAdapterPosition());
                    }
                }
            }
        });
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected GifImageView image;
        protected EmojiTextView messageText;

        public ViewHolder(View view) {
            super(view);

            image = (GifImageView) view.findViewById(R.id.thumbnail);
            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
        }
    }
}
