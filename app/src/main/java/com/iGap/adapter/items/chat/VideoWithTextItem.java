package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MyType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VideoWithTextItem extends AbstractChatItem<VideoWithTextItem, VideoWithTextItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VideoWithTextItem(ProtoGlobal.Room.Type type) {
        super(true, type);
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
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        holder.cslr_txt_tic.setVisibility(View.GONE);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        holder.cslr_txt_tic.setVisibility(View.VISIBLE);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.cslr_txt_time.setText(formatTime());
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

        if (!mMessage.messageText.isEmpty()) {
            holder.cslr_txt_message.setText(mMessage.messageText);
            holder.cslr_txt_message.setVisibility(View.VISIBLE);
        } else {
            holder.cslr_txt_message.setVisibility(View.GONE);
        }

        if (mMessage.sendType == MyType.SendType.send) {
            updateMessageStatus(holder.cslr_txt_tic);
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
        protected LinearLayout cslr_ll_frame;
        protected LinearLayout cslr_ll_content_main;
        protected LinearLayout cslr_ll_forward;
        protected TextView cslr_txt_forward_from;
        protected LinearLayout cslr_ll_time;
        protected TextView cslr_txt_time;
        protected MaterialDesignTextView cslr_txt_tic;
        protected ImageView cslv_imv_vido_image;
        protected ImageButton cslv_btn_play_video;
        protected TextView cslv_txt_video_name;
        protected TextView cslv_txt_video_mime_type;
        protected TextView cslv_txt_vido_info;
        protected TextView cslr_txt_message;

        public ViewHolder(View view) {
            super(view);

            cslr_txt_message = (TextView) view.findViewById(R.id.cslr_txt_message);
            cslr_ll_frame = (LinearLayout) view.findViewById(R.id.mainContainer);
            cslr_ll_content_main = (LinearLayout) view.findViewById(R.id.cslr_ll_content_main);
            cslr_ll_forward = (LinearLayout) view.findViewById(R.id.cslr_ll_forward);
            cslr_txt_forward_from = (TextView) view.findViewById(R.id.cslr_txt_forward_from);
            cslr_ll_time = (LinearLayout) view.findViewById(R.id.cslr_ll_time);
            cslr_txt_time = (TextView) view.findViewById(R.id.cslr_txt_time);
            cslr_txt_tic = (MaterialDesignTextView) view.findViewById(R.id.cslr_txt_tic);
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
