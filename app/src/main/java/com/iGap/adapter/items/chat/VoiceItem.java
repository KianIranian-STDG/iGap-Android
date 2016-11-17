package com.iGap.adapter.items.chat;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.IOException;
import java.util.List;

import io.github.meness.audioplayerview.AudioPlayerView;
import io.github.meness.audioplayerview.listeners.OnAudioPlayerViewControllerClick;
import io.github.meness.emoji.EmojiTextView;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VoiceItem extends AbstractMessage<VoiceItem, VoiceItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VoiceItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVoice;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_voice;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadThumbnailFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        //ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.thumbnail);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.playerView.setClickListener(new OnAudioPlayerViewControllerClick() {
                    @Override
                    public void onPlayClick(AudioPlayerView playerView) {
                        messageClickListener.onOpenClick(null, mMessage, holder.getAdapterPosition());
                    }

                    @Override
                    public void onPauseClick(AudioPlayerView playerView) {

                    }

                    @Override
                    public void onStopClick(AudioPlayerView playerView) {

                    }
                });
                holder.playerView.setEnabled(mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal());
                if (mMessage.attachment.isFileExistsOnLocal()) {
                    holder.playerView.setMediaPlayer(makeMediaPlayer(AndroidUtils.suitablePath(mMessage.forwardedFrom.getAttachment().getLocalFilePath())));
                }
            }
            setTextIfNeeded(holder.messageText, mMessage.forwardedFrom.getMessage());
        } else {
            if (mMessage.attachment != null) {
                holder.playerView.setClickListener(new OnAudioPlayerViewControllerClick() {
                    @Override
                    public void onPlayClick(AudioPlayerView playerView) {
                        messageClickListener.onOpenClick(null, mMessage, holder.getAdapterPosition());
                    }

                    @Override
                    public void onPauseClick(AudioPlayerView playerView) {

                    }

                    @Override
                    public void onStopClick(AudioPlayerView playerView) {

                    }
                });
                holder.playerView.setEnabled(mMessage.attachment.isFileExistsOnLocal());
                if (mMessage.attachment.isFileExistsOnLocal()) {
                    holder.playerView.setMediaPlayer(makeMediaPlayer(AndroidUtils.suitablePath(mMessage.attachment.getLocalFilePath())));
                }
            }
            setTextIfNeeded(holder.messageText, mMessage.messageText);
        }

        AppUtils.rightFileThumbnailIcon(holder.thumbnail, mMessage.messageType, null);
    }

    private MediaPlayer makeMediaPlayer(String filePath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mediaPlayer;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView thumbnail;
        protected EmojiTextView messageText;
        protected AudioPlayerView playerView;

        public ViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);

            playerView = new AudioPlayerView(view.getContext());
            playerView.setAnchorView((ViewGroup) view.findViewById(R.id.audioPlayerViewContainer));
            playerView.show();
        }
    }
}
