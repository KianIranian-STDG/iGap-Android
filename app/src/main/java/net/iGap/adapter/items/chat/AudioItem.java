/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items.chat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityChat;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnComplete;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static net.iGap.R.id.fileName;
import static net.iGap.R.id.fileSize;
import static net.iGap.R.id.songArtist;

public class AudioItem extends AbstractMessage<AudioItem, AudioItem.ViewHolder> {

    public AudioItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutAudio;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {

            holder.mFilePath = localPath;
            holder.itemView.findViewById(R.id.txt_play_music).setEnabled(true);
        } else {
            holder.itemView.findViewById(R.id.txt_play_music).setEnabled(false);
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.mainContainer) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getAudioItem());
        }

        final MaterialDesignTextView materialDesignTextView = (MaterialDesignTextView) holder.itemView.findViewById(R.id.txt_play_music);

        holder.complete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, final String MessageTow) {

                if (messageOne.equals("play")) {
                    (materialDesignTextView).setText(R.string.md_play_arrow);
                } else if (messageOne.equals("pause")) {
                    (materialDesignTextView).setText(R.string.md_pause_button);
                } else if (messageOne.equals("updateTime")) {
                    ((materialDesignTextView).findViewById(R.id.txt_play_music)).post(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).setText(MessageTow + "/" + holder.mTimeMusic);
                            ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).setProgress(MusicPlayer.musicProgress);

                            if (HelperCalander.isLanguagePersian) ((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).setText(HelperCalander.convertToUnicodeFarsiNumber(((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).getText().toString()));
                        }
                    });
                }
            }
        };


        (materialDesignTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.mFilePath.length() < 1) return;

                if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                    MusicPlayer.onCompleteChat = holder.complete;

                    if (MusicPlayer.mp != null) {
                        MusicPlayer.playAndPause();
                    } else {
                        MusicPlayer.startPlayer(holder.mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, holder.mMessageID);
                    }
                } else {

                    MusicPlayer.stopSound();
                    MusicPlayer.onCompleteChat = holder.complete;

                    MusicPlayer.startPlayer(holder.mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, holder.mMessageID);
                    holder.mTimeMusic = MusicPlayer.musicTime;
                }
            }
        });

        ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                        MusicPlayer.setMusicProgress(((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).getProgress());
                    }
                }
                return false;
            }
        });

        super.bindView(holder, payloads);

        if (mMessage.isSenderMe()) {
            AppUtils.setImageDrawable(((ImageView) holder.itemView.findViewById(R.id.thumbnail)), R.drawable.white_music_note);
        } else {
            AppUtils.setImageDrawable(((ImageView) holder.itemView.findViewById(R.id.thumbnail)), R.drawable.green_music_note);
        }

        String text = "";

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                if (mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal()) {
                    holder.itemView.findViewById(fileSize).setVisibility(View.INVISIBLE);
                } else {
                    holder.itemView.findViewById(fileSize).setVisibility(View.VISIBLE);
                    ((TextView) holder.itemView.findViewById(fileSize)).setText(AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true));
                }
                ((TextView) holder.itemView.findViewById(fileName)).setText(mMessage.forwardedFrom.getAttachment().getName());
                if (mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal()) {
                    String artistName = AndroidUtils.getAudioArtistName(mMessage.forwardedFrom.getAttachment().getLocalFilePath());
                    if (!TextUtils.isEmpty(artistName)) {
                        ((TextView) holder.itemView.findViewById(songArtist)).setText(artistName);
                    } else {
                        ((TextView) holder.itemView.findViewById(songArtist)).setText(holder.itemView.getResources().getString(R.string.unknown_artist));
                    }
                }
            }

            text = mMessage.forwardedFrom.getMessage();
        } else {
            if (mMessage.attachment != null) {
                if (mMessage.attachment.isFileExistsOnLocal()) {
                    holder.itemView.findViewById(fileSize).setVisibility(View.INVISIBLE);
                } else {
                    holder.itemView.findViewById(fileSize).setVisibility(View.VISIBLE);
                    ((TextView) holder.itemView.findViewById(fileSize)).setText(AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true));
                }
                ((TextView) holder.itemView.findViewById(fileName)).setText(mMessage.attachment.name);
            }
            if (!TextUtils.isEmpty(mMessage.songArtist)) {
                ((TextView) holder.itemView.findViewById(songArtist)).setText(mMessage.songArtist);
            } else {
                ((TextView) holder.itemView.findViewById(songArtist)).setText(holder.itemView.getResources().getString(R.string.unknown_artist));
            }

            text = mMessage.messageText;
        }

        if (mMessage.hasEmojiInText) {
            setTextIfNeeded((EmojiTextViewE) holder.itemView.findViewById(R.id.messageSenderTextMessage), text);
        } else {
            setTextIfNeeded((TextView) holder.itemView.findViewById(R.id.messageSenderTextMessage), text);
        }

        View audioBoxView = holder.itemView.findViewById(R.id.audioBox);
        if ((mMessage.forwardedFrom != null && mMessage.forwardedFrom.getForwardMessage() != null && mMessage.forwardedFrom.getForwardMessage().getMessage() != null && !TextUtils.isEmpty(mMessage.forwardedFrom.getForwardMessage().getMessage())) || !TextUtils.isEmpty(mMessage.messageText)) {
            audioBoxView.setBackgroundResource(R.drawable.green_bg_rounded_corner);
        } else {
            audioBoxView.setBackgroundColor(Color.TRANSPARENT);
        }

        final long _st = (int) ((mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getDuration() : mMessage.attachment.duration) * 1000);

        ((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).setText("00/" + MusicPlayer.milliSecondsToTimer(_st));

        if (mMessage.messageID.equals(MusicPlayer.messageId)) {
            MusicPlayer.onCompleteChat = holder.complete;

            ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).setProgress(MusicPlayer.musicProgress);
            ((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).setText(MusicPlayer.strTimer + "/" + MusicPlayer.musicTime);

            holder.mTimeMusic = MusicPlayer.musicTime;

            if (MusicPlayer.mp != null) {
                if (MusicPlayer.mp.isPlaying()) {
                    ((TextView) holder.itemView.findViewById(R.id.txt_play_music)).setText(R.string.md_pause_button);
                } else {
                    ((TextView) holder.itemView.findViewById(R.id.txt_play_music)).setText(R.string.md_play_arrow);
                }
            }
        } else {
            ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).setProgress(0);
            ((TextView) holder.itemView.findViewById(R.id.txt_play_music)).setText(R.string.md_play_arrow);
        }

        holder.mMessageID = mMessage.messageID;

        if (HelperCalander.isLanguagePersian) ((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).setText(HelperCalander.convertToUnicodeFarsiNumber(((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).getText().toString()));
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
        }
        ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        ((TextView) holder.itemView.findViewById(R.id.txt_play_music)).setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
        ((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).setTextColor(holder.itemView.getResources().getColor(R.color.black90));
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        if (type == ProtoGlobal.Room.Type.CHANNEL) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }
            ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            ((TextView) holder.itemView.findViewById(R.id.txt_play_music)).setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
            ((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        } else {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
            }
            ((SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1)).getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
            ((TextView) holder.itemView.findViewById(R.id.txt_play_music)).setTextColor(holder.itemView.getResources().getColor(R.color.green));
            ((TextView) holder.itemView.findViewById(R.id.csla_txt_timer)).setTextColor(holder.itemView.getResources().getColor(R.color.grayNewDarker));
            ((TextView) holder.itemView.findViewById(fileName)).setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView thumbnail;
        protected TextView fileSize;
        protected TextView fileName;
        protected TextView songArtist;
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";

        protected TextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected TextView txt_Timer;

        public ViewHolder(final View view) {
            super(view);
            /**
             *  this commented code used with xml layout
             */

            //thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //fileSize = (TextView) view.findViewById(R.id.fileSize);
            //fileName = (TextView) view.findViewById(R.id.fileName);
            //songArtist = (TextView) view.findViewById(R.id.songArtist);
            //
            //btnPlayMusic = (TextView) view.findViewById(R.id.txt_play_music);
            //
            //txt_Timer = (TextView) view.findViewById(R.id.csla_txt_timer);
            //musicSeekbar = (SeekBar) view.findViewById(R.id.csla_seekBar1);
            //
            //complete = new OnComplete() {
            //    @Override
            //    public void complete(boolean result, String messageOne, final String MessageTow) {
            //
            //        if (messageOne.equals("play")) {
            //            btnPlayMusic.setText(R.string.md_play_arrow);
            //        } else if (messageOne.equals("pause")) {
            //            btnPlayMusic.setText(R.string.md_pause_button);
            //        } else if (messageOne.equals("updateTime")) {
            //            txt_Timer.post(new Runnable() {
            //                @Override
            //                public void run() {
            //                    txt_Timer.setText(MessageTow + "/" + mTimeMusic);
            //                    musicSeekbar.setProgress(MusicPlayer.musicProgress);
            //
            //                    if (HelperCalander.isLanguagePersian) txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_Timer.getText().toString()));
            //                }
            //            });
            //        }
            //    }
            //};
            //
            //btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //
            //        if (mFilePath.length() < 1) return;
            //
            //        if (mMessageID.equals(MusicPlayer.messageId)) {
            //            MusicPlayer.onCompleteChat = complete;
            //
            //            if (MusicPlayer.mp != null) {
            //                MusicPlayer.playAndPause();
            //            } else {
            //                MusicPlayer.startPlayer(mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, mMessageID);
            //            }
            //        } else {
            //
            //            MusicPlayer.stopSound();
            //            MusicPlayer.onCompleteChat = complete;
            //
            //            MusicPlayer.startPlayer(mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, mMessageID);
            //            mTimeMusic = MusicPlayer.musicTime;
            //        }
            //    }
            //});
            //
            //musicSeekbar.setOnTouchListener(new View.OnTouchListener() {
            //
            //    @Override
            //    public boolean onTouch(View v, MotionEvent event) {
            //
            //        if (event.getAction() == MotionEvent.ACTION_UP) {
            //            if (mMessageID.equals(MusicPlayer.messageId)) {
            //                MusicPlayer.setMusicProgress(musicSeekbar.getProgress());
            //            }
            //        }
            //        return false;
            //    }
            //});
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
