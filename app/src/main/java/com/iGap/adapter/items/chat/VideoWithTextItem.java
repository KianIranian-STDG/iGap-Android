package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperMimeType;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.module.EmojiTextView;
import com.iGap.module.MyType;
import com.iGap.module.Utils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VideoWithTextItem extends AbstractChatItem<VideoWithTextItem, VideoWithTextItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VideoWithTextItem(ProtoGlobal.Room.Type type, OnMessageViewClick messageClickListener) {
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

        holder.cslv_txt_video_name.setText(mMessage.attachment.name);
        holder.cslv_txt_video_mime_type.setText(mMessage.fileMime);
        holder.cslv_txt_vido_size.setText(Utils.humanReadableByteCount(mMessage.attachment.size, true));

        if (mMessage.fileState == MyType.FileState.notDownload || mMessage.fileState == MyType.FileState.downloading)// enable or disable btn play video
        {
            holder.cslv_btn_play_video.setVisibility(View.GONE);
        } else {
            holder.cslv_btn_play_video.setVisibility(View.VISIBLE);
        }

        setTextIfNeeded(holder.messageText);
    }

    @Override
    public void onLoadFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadFromLocal(holder, localPath, fileType);
        new HelperMimeType().LoadVideoTumpnail(holder.cslv_imv_vido_image, localPath);
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
        protected TextView cslv_txt_vido_size;
        protected EmojiTextView messageText;

        public ViewHolder(View view) {
            super(view);

            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
            cslv_imv_vido_image = (ImageView) view.findViewById(R.id.thumbnail);
            cslv_btn_play_video = (ImageButton) view.findViewById(R.id.cslv_btn_play_video);
            cslv_txt_video_name = (TextView) view.findViewById(R.id.cslv_txt_video_name);
            cslv_txt_video_mime_type = (TextView) view.findViewById(R.id.cslv_txt_video_mime_type);
            cslv_txt_vido_size = (TextView) view.findViewById(R.id.cslv_txt_video_size);

            cslv_btn_play_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", " play video clicked");
                }
            });
        }
    }
}
