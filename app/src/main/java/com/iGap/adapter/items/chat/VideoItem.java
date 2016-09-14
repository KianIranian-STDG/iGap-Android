package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.module.MyType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VideoItem extends AbstractChatItem<VideoItem, VideoItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VideoItem(ProtoGlobal.Room.Type type) {
        super(true, type);
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
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.cslv_txt_video_name.setText(mMessage.fileName);
        holder.cslv_txt_video_mime_type.setText(mMessage.fileMime);
        holder.cslv_txt_vido_info.setText(mMessage.fileInfo);

        holder.cslv_imv_vido_image.setImageResource(Integer.parseInt(mMessage.filePic));

        if (mMessage.fileState == MyType.FileState.notDownload || mMessage.fileState == MyType.FileState.downloading)// enable or disable btn play video
        {
            holder.cslv_btn_play_video.setVisibility(View.GONE);
        } else {
            holder.cslv_btn_play_video.setVisibility(View.VISIBLE);
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
        protected ImageView cslv_imv_vido_image;
        protected ImageButton cslv_btn_play_video;
        protected TextView cslv_txt_video_name;
        protected TextView cslv_txt_video_mime_type;
        protected TextView cslv_txt_vido_info;

        public ViewHolder(View view) {
            super(view);

            cslv_imv_vido_image = (ImageView) view.findViewById(R.id.cslv_imv_vido_image);
            cslv_btn_play_video = (ImageButton) view.findViewById(R.id.cslv_btn_play_video);
            cslv_txt_video_name = (TextView) view.findViewById(R.id.cslv_txt_video_name);
            cslv_txt_video_mime_type = (TextView) view.findViewById(R.id.cslv_txt_video_mime_type);
            cslv_txt_vido_info = (TextView) view.findViewById(R.id.cslv_txt_vido_info);

            cslv_btn_play_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", " play video clicked");
                }
            });
        }
    }
}
