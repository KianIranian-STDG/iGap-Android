package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.helper.HelperMimeType;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.module.Utils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VideoItem extends AbstractMessage<VideoItem, VideoItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VideoItem(ProtoGlobal.Room.Type type, OnMessageViewClick messageClickListener) {
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
    public void onLoadFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadFromLocal(holder, localPath, fileType);
        new HelperMimeType().loadVideoThumbnail(holder.image, localPath);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.attachment != null) {
            int[] dimens = Utils.scaleDimenWithSavedRatio(holder.itemView.getContext(), mMessage.attachment.width, mMessage.attachment.height);
            ((ViewGroup) holder.image.getParent()).setLayoutParams(new LinearLayout.LayoutParams(dimens[0], dimens[1]));
            holder.image.getParent().requestLayout();
        }

        holder.cslv_txt_video_name.setText(mMessage.attachment.name);
        holder.cslv_txt_video_mime_type.setText(mMessage.fileMime);
        holder.cslv_txt_vido_size.setText(Utils.humanReadableByteCount(mMessage.attachment.size, true));
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView cslv_txt_video_name;
        protected TextView cslv_txt_video_mime_type;
        protected TextView cslv_txt_vido_size;

        public ViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.thumbnail);
            cslv_txt_video_name = (TextView) view.findViewById(R.id.cslv_txt_video_name);
            cslv_txt_video_mime_type = (TextView) view.findViewById(R.id.cslv_txt_video_mime_type);
            cslv_txt_vido_size = (TextView) view.findViewById(R.id.cslv_txt_video_size);
        }
    }
}
