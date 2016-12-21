package com.iGap.adapter.items.chat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.io.File;
import java.util.List;

import io.github.meness.emoji.EmojiTextView;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.view.View.GONE;

public class AudioItem extends AbstractMessage<AudioItem, AudioItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public AudioItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override public int getType() {
        return R.id.chatSubLayoutAudio;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_audio;
    }

    @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override public void onLoadThumbnailFromLocal(final ViewHolder holder, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {

            holder.mFilePath = localPath;
            holder.btnPlayMusic.setEnabled(true);
        } else {
            holder.btnPlayMusic.setEnabled(false);
        }
    }

    @Override public void bindView(final ViewHolder holder, List payloads) {
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
                    holder.fileSize.setText(AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true));
                }
                holder.fileName.setText(mMessage.forwardedFrom.getAttachment().getName());
                if (mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal()) {
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
                    holder.fileSize.setText(AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true));
                }
                holder.fileName.setText(mMessage.attachment.name);
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

        int st = 1000;
        if (mMessage.isSenderMe()) st = 1;
        final long _st = (int) ((mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getDuration() : mMessage.attachment.duration) * st);

        holder.txt_Timer.post(new Runnable() {
            @Override public void run() {
                holder.txt_Timer.setText("00/" + MusicPlayer.milliSecondsToTimer(_st));
                Log.e("ddd", _st + "");
            }
        });

        if (mMessage.messageID.equals(MusicPlayer.messageId)) {
            MusicPlayer.onCompleteChat = holder.complete;

            holder.musicSeekbar.setProgress(MusicPlayer.musicProgress);
            holder.txt_Timer.setText(MusicPlayer.strTimer + "/" + MusicPlayer.musicTime);

            holder.mTimeMusic = MusicPlayer.musicTime;
        }
        {
            holder.musicSeekbar.setProgress(0);
            holder.btnPlayMusic.setText(R.string.md_play_arrow);
        }

        holder.mMessageID = mMessage.messageID;
    }

    @Override protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray10), PorterDuff.Mode.SRC_IN);
        }
        holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.white));
        holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        holder.fileName.setTextColor(Color.WHITE);
    }

    @Override protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
        }
        holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.green));
        holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.grayNewDarker));
        holder.fileName.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
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
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";

        protected TextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected TextView txt_Timer;

        public ViewHolder(View view) {
            super(view);
            thumbnail = (RoundedImageView) view.findViewById(R.id.thumbnail);
            fileSize = (TextView) view.findViewById(R.id.fileSize);
            fileName = (TextView) view.findViewById(R.id.fileName);
            songArtist = (TextView) view.findViewById(R.id.songArtist);
            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);

            btnPlayMusic = (TextView) view.findViewById(R.id.csla_btn_play_music);
            btnPlayMusic.setTypeface(G.flaticon);
            txt_Timer = (TextView) view.findViewById(R.id.csla_txt_timer);
            musicSeekbar = (SeekBar) view.findViewById(R.id.csla_seekBar1);

            complete = new OnComplete() {
                @Override public void complete(boolean result, String messageOne, final String MessageTow) {

                    if (messageOne.equals("play")) {
                        btnPlayMusic.setText(R.string.md_play_arrow);
                    } else if (messageOne.equals("pause")) {
                        btnPlayMusic.setText(R.string.md_pause_button);
                    } else if (messageOne.equals("updateTime")) {
                        txt_Timer.post(new Runnable() {
                            @Override public void run() {
                                txt_Timer.setText(MessageTow + "/" + mTimeMusic);
                                musicSeekbar.setProgress(MusicPlayer.musicProgress);
                            }
                        });
                    }
                }
            };

            btnPlayMusic.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                    if (mFilePath.length() < 1) return;

                    if (mMessageID.equals(MusicPlayer.messageId)) {
                        MusicPlayer.onCompleteChat = complete;

                        if (MusicPlayer.mp != null) {
                            MusicPlayer.playAndPause();
                        } else {
                            MusicPlayer.startPlayer(mFilePath, ActivityChat.title, ActivityChat.mRoomId, true, mMessageID);
                        }
                    } else {

                        MusicPlayer.stopSound();
                        MusicPlayer.onCompleteChat = complete;

                        MusicPlayer.startPlayer(mFilePath, ActivityChat.title, ActivityChat.mRoomId, true, mMessageID);

                        mTimeMusic = MusicPlayer.musicTime;
                    }
                }
            });

            musicSeekbar.setOnTouchListener(new View.OnTouchListener() {

                @Override public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (mMessageID.equals(MusicPlayer.messageId)) {
                            MusicPlayer.setMusicProgress(musicSeekbar.getProgress());
                        }
                    }
                    return false;
                }
            });
        }
    }
}
