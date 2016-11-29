package com.iGap.adapter.items.chat;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.IOException;
import java.util.List;

import io.github.meness.audioplayerview.AudioPlayerView;
import io.github.meness.audioplayerview.listeners.OnAudioPlayerViewControllerClick;
import io.github.meness.emoji.EmojiTextView;

import static android.view.View.GONE;

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
    public void onLoadThumbnailFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        //ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.thumbnail);
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

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.isSenderMe()) {
            holder.thumbnail.setImageResource(R.drawable.white_music_note);
        } else {
            holder.thumbnail.setImageResource(R.drawable.green_music_note);
        }

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                if (mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal()) {
                    holder.fileSize.setVisibility(GONE);
                } else {
                    holder.fileSize.setVisibility(View.VISIBLE);
                    holder.fileSize.setText(
                            AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true));
                }
                holder.fileName.setText(mMessage.forwardedFrom.getAttachment().getName());
                holder.playerView.setClickListener(new OnAudioPlayerViewControllerClick() {
                    @Override
                    public void onPlayClick(AudioPlayerView playerView) {
                        messageClickListener.onOpenClick(null, mMessage, holder.getAdapterPosition());
                    }

                    @Override
                    public void onPauseClick(AudioPlayerView playerView) {

                    }

                });
                holder.playerView.setEnabled(mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal());
                if (mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal()) {
                    holder.playerView.setMediaPlayer(makeMediaPlayer(AndroidUtils.suitablePath(mMessage.forwardedFrom.getAttachment().getLocalFilePath())));
                    String artistName = AndroidUtils.getAudioArtistName(mMessage.forwardedFrom.getAttachment().getLocalFilePath());
                    if (!TextUtils.isEmpty(artistName)) {
                        holder.songArtist.setText(artistName);
                    } else {
                        holder.songArtist.setText(holder.itemView.getResources().getString(R.string.unknown_artist));
                    }
                }
            }

            setTextIfNeeded(holder.messageText, mMessage.forwardedFrom.getMessage());
        } else {
            if (mMessage.attachment != null) {
                if (mMessage.attachment.isFileExistsOnLocal()) {
                    holder.fileSize.setVisibility(GONE);
                } else {
                    holder.fileSize.setVisibility(View.VISIBLE);
                    holder.fileSize.setText(
                            AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true));
                }
                holder.fileName.setText(mMessage.attachment.name);
                holder.playerView.setClickListener(new OnAudioPlayerViewControllerClick() {
                    @Override
                    public void onPlayClick(AudioPlayerView playerView) {
                        messageClickListener.onOpenClick(null, mMessage, holder.getAdapterPosition());
                    }

                    @Override
                    public void onPauseClick(AudioPlayerView playerView) {

                    }

                });
                holder.playerView.setEnabled(mMessage.attachment.isFileExistsOnLocal());
                if (mMessage.attachment.isFileExistsOnLocal()) {
                    holder.playerView.setMediaPlayer(makeMediaPlayer(AndroidUtils.suitablePath(mMessage.attachment.getLocalFilePath())));
                }
            }
            if (!TextUtils.isEmpty(mMessage.songArtist)) {
                holder.songArtist.setText(mMessage.songArtist);
            } else {
                holder.songArtist.setText(holder.itemView.getResources().getString(R.string.unknown_artist));
            }

            setTextIfNeeded(holder.messageText, mMessage.messageText);
        }

        View audioBoxView = holder.itemView.findViewById(R.id.audioBox);
        if ((mMessage.forwardedFrom != null && !TextUtils.isEmpty(mMessage.forwardedFrom.getForwardMessage().getMessage())) || !TextUtils.isEmpty(mMessage.messageText)) {
            audioBoxView.setBackgroundResource(R.drawable.green_bg_rounded_corner);
        } else {
            audioBoxView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        holder.playerView.setProgressColor(R.color.white);
        holder.playerView.setProgressThumb(R.color.gray10);
        holder.playerView.setDrawablesColor(R.color.white);
        holder.playerView.setRecordedByColor(R.color.white);
        holder.playerView.setTimesColor(R.color.black90, android.R.color.black);
        holder.fileName.setTextColor(Color.WHITE);
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        holder.playerView.setProgressColor(R.color.iGapColor);
        holder.playerView.setProgressThumb(R.color.iGapColorDarker);
        holder.playerView.setDrawablesColor(R.color.iGapColor);
        holder.playerView.setRecordedByColor(R.color.colorOldBlack);
        holder.playerView.setTimesColor(R.color.grayNew, R.color.grayNewDarker);
        holder.fileName.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected RoundedImageView thumbnail;
        protected TextView fileSize;
        protected TextView fileName;
        protected TextView songArtist;
        protected EmojiTextView messageText;
        protected AudioPlayerView playerView;

        public ViewHolder(View view) {
            super(view);
            thumbnail = (RoundedImageView) view.findViewById(R.id.thumbnail);
            fileSize = (TextView) view.findViewById(R.id.fileSize);
            fileName = (TextView) view.findViewById(R.id.fileName);
            songArtist = (TextView) view.findViewById(R.id.songArtist);
            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);

            playerView = new AudioPlayerView(view.getContext());
            playerView.setAnchorView((ViewGroup) view.findViewById(R.id.audioPlayerViewContainer));
            playerView.show();
            playerView.hideRecordedBy();
        }
    }
}
