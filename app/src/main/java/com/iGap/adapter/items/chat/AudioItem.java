package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
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
public class AudioItem extends AbstractMessage<AudioItem, AudioItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public AudioItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutAudio;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_audio;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadFromLocal(holder, localPath, fileType);
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.thumbnail);
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.fileSize.setText(
                AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true));
        holder.fileName.setText(mMessage.attachment.name);
        if (mMessage.songArtist != null && !mMessage.songArtist.isEmpty()) {
            holder.songArtist.setText(mMessage.songArtist);
        } else {
            holder.songArtist.setText(holder.itemView.getResources().getString(R.string.unknown_artist));
        }
        holder.elapsedTime.setText("0");
        holder.duration.setText(AppUtils.humanReadableDuration(mMessage.attachment.duration));
        setTextIfNeeded(holder.messageText);
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView thumbnail;
        protected TextView fileSize;
        protected TextView fileName;
        protected TextView songArtist;
        protected TextView elapsedTime;
        protected TextView duration;
        protected MediaController mediaController;
        protected EmojiTextView messageText;

        public ViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            fileSize = (TextView) view.findViewById(R.id.fileSize);
            fileName = (TextView) view.findViewById(R.id.fileName);
            songArtist = (TextView) view.findViewById(R.id.songArtist);
            elapsedTime = (TextView) view.findViewById(R.id.elapsedTime);
            duration = (TextView) view.findViewById(R.id.duration);
            mediaController = (MediaController) view.findViewById(R.id.mediaController);
            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
        }
    }
}
