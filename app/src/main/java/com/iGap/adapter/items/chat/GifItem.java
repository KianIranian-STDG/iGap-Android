package com.iGap.adapter.items.chat;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class GifItem extends AbstractMessage<GifItem, GifItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public GifItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public void onPlayPauseGIF(ViewHolder holder, String localPath) {
        super.onPlayPauseGIF(holder, localPath);
        holder.image.setImageURI(Uri.fromFile(new File(localPath)));
        holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutGif;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_gif;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadThumbnailFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, 0) != 0) {
            holder.image.setImageURI(Uri.fromFile(new File(localPath)));
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
        } else {
            if (mMessage.attachment != null) {
                dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(),
                        mMessage.attachment.width, mMessage.attachment.height);
            }
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimens[0], dimens[1]);
        FrameLayout.LayoutParams layoutParamsForParentParent = new FrameLayout.LayoutParams(dimens[0], dimens[1]);
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
                        messageClickListener.onOpenClick(v, mMessage, holder.getAdapterPosition());
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
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected GifImageView image;

        public ViewHolder(View view) {
            super(view);

            image = (GifImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
