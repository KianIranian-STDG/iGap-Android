package com.iGap.adapter.items.chat;

import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AppUtils;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

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
    public void onLoadThumbnailFromLocal(ViewHolder holder, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {
            holder.mFilePath = localPath;
            holder.btnPlayMusic.setEnabled(true);
        } else {
            holder.btnPlayMusic.setEnabled(false);
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        AppUtils.rightFileThumbnailIcon(holder.thumbnail, mMessage.messageType, null);

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getUserId() : Long.parseLong(mMessage.senderID)).findFirst();

        if (registeredInfo != null) {
            holder.auther.setText("Recorded By " + registeredInfo.getDisplayName());
        } else {
            holder.auther.setText("");
        }

        realm.close();

        int st = 1000;
        if (mMessage.isSenderMe()) st = 1;
        final long _st = (int) ((mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getDuration() : mMessage.attachment.duration) * st);

        holder.txt_Timer.post(new Runnable() {
            @Override public void run() {
                holder.txt_Timer.setText("00/" + MusicPlayer.milliSecondsToTimer(_st));
            }
        });

        if (mMessage.messageID.equals(MusicPlayer.messageId)) {
            MusicPlayer.onCompleteChat = holder.complete;

            holder.musicSeekbar.setProgress(MusicPlayer.musicProgress);
            holder.txt_Timer.setText(MusicPlayer.strTimer + "/" + MusicPlayer.musicTime);

            holder.mTimeMusic = MusicPlayer.musicTime;

            if (MusicPlayer.mp != null) {
                if (MusicPlayer.mp.isPlaying()) {
                    holder.btnPlayMusic.setText(R.string.md_pause_button);
                } else {
                    holder.btnPlayMusic.setText(R.string.md_play_arrow);
                }
            }


        } else {
            holder.musicSeekbar.setProgress(0);
            holder.btnPlayMusic.setText(R.string.md_play_arrow);
        }

        holder.mMessageID = mMessage.messageID;


    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
        }

        holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
        holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        holder.auther.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
        }

        holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);

        holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.green));
        holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.grayNewDarker));
        holder.auther.setTextColor(holder.itemView.getResources().getColor(R.color.black90));

    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
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
        protected TextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected TextView txt_Timer;
        protected TextView auther;
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";

        public ViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            auther = (TextView) view.findViewById(R.id.cslv_txt_author);
            btnPlayMusic = (TextView) view.findViewById(R.id.csla_btn_play_music);
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
                            MusicPlayer.startPlayer(mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, mMessageID);
                        }
                    } else {

                        MusicPlayer.stopSound();
                        MusicPlayer.onCompleteChat = complete;
                        MusicPlayer.startPlayer(mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, mMessageID);

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
