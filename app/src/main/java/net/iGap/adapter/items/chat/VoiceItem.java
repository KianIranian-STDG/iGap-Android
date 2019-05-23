/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnComplete;
import net.iGap.libs.audio.AudioWave;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRegisteredInfo;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static net.iGap.fragments.FragmentChat.getRealmChat;

public class VoiceItem extends AbstractMessage<VoiceItem, VoiceItem.ViewHolder> {

    private static final String PLAY = "play";
    private static final String PAUSE = "pause";

    private String playMode = PAUSE;

    public VoiceItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }


    @Override
    public int getType() {
        return R.id.chatSubLayoutVoice;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);

        if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {
            holder.mFilePath = localPath;
            holder.waveView.setEnabled(true);
            holder.btnPlayMusic.setVisibility(View.VISIBLE);

        } else {
//            holder.musicSeekbar.setEnabled(false);
            holder.waveView.setEnabled(false);
            holder.btnPlayMusic.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.waveView.setTag(mMessage.messageID);

        holder.messageTime = HelperCalander.getClocktime(mMessage.time, false);


        ValueAnimator anim = ValueAnimator.ofInt(0, 100);
        anim.setDuration((long) ((mMessage.attachment.duration) / 0.001));
        Log.i("aabolfazl", "bindView: " + (mMessage.attachment.duration) / 0.001);
        anim.addUpdateListener(animation -> {
            int animProgress = (Integer) animation.getAnimatedValue();
            holder.waveView.setProgress(animProgress);
        });

        holder.complete = (result, messageOne, MessageTow) -> {

            if (holder.waveView.getTag().equals(mMessage.messageID) && mMessage.messageID.equals(MusicPlayer.messageId)) {
                if (messageOne.equals("play")) {
                    holder.btnPlayMusic.setText(R.string.md_play_arrow);
                } else if (messageOne.equals("pause")) {
                    holder.btnPlayMusic.setText(R.string.md_pause_button);
                } else if (messageOne.equals("updateTime")) {

                    if (result) {

                        G.handler.post(() -> {

                            if (mMessage.messageID.equals(MusicPlayer.messageId)) {
                                holder.txt_Timer.setText(MessageTow + "/" + holder.mTimeMusic);
                                if (HelperCalander.isPersianUnicode) {
                                    holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                                }
//                                        holder.waveView.setProgress(MusicPlayer.musicProgress);
                            }
                        });
                    } else {
                        holder.btnPlayMusic.post(() -> {

                            holder.txt_Timer.setText(MessageTow + "/" + holder.mTimeMusic);
                            if (HelperCalander.isPersianUnicode) {
                                holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                            }
                            holder.waveView.setProgress(0);
                        });
                    }
                }
            }
        };

        holder.btnPlayMusic.setOnLongClickListener(getLongClickPerform(holder));

        holder.btnPlayMusic.setOnClickListener(v -> {


            if (FragmentChat.isInSelectionMode) {
                holder.itemView.performLongClick();
                return;
            }

            if (holder.mFilePath.length() < 1) return;

            G.chatUpdateStatusUtil.sendUpdateStatus(holder.mType, holder.mRoomId, Long.parseLong(holder.mMessageID), ProtoGlobal.RoomMessageStatus.LISTENED);

            RealmClientCondition.addOfflineListen(holder.mRoomId, Long.parseLong(holder.mMessageID));

            if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                MusicPlayer.onCompleteChat = holder.complete;

                if (MusicPlayer.mp != null) {
                    MusicPlayer.playAndPause();
                } else {
                    MusicPlayer.startPlayer("", holder.mFilePath, FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, true, holder.mMessageID);
                    messageClickListener.onPlayMusic(holder.mMessageID);
                }
            } else {
                MusicPlayer.stopSound();
                MusicPlayer.onCompleteChat = holder.complete;
                MusicPlayer.startPlayer("", holder.mFilePath, FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, true, holder.mMessageID);
                messageClickListener.onPlayMusic(holder.mMessageID);
                holder.mTimeMusic = MusicPlayer.musicTime;
            }

            if (payloads.equals(PLAY)) {
                playMode = PAUSE;
                anim.cancel();
            } else if (playMode.equals(PAUSE)) {
                playMode = PLAY;
                anim.start();
            }


        });

        holder.waveView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (FragmentChat.isInSelectionMode) {
                    holder.itemView.performLongClick();
                    return true;
                }
                if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                    MusicPlayer.setMusicProgress((int) holder.waveView.getProgress());
                }
            }
            return false;
        });

        super.bindView(holder, payloads);

        ProtoGlobal.RoomMessageType _type = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType;
        holder.mType = type;
        AppUtils.rightFileThumbnailIcon(holder.thumbnail, _type, null);

        holder.mRoomId = mMessage.roomId;

        RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getUserId() : Long.parseLong(mMessage.senderID));

        if (registeredInfo != null) {
            holder.author.setText(G.context.getString(R.string.recorded_by) + " " + registeredInfo.getDisplayName());
        } else {
            holder.author.setText("");
        }

        final long _st = (int) ((mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getDuration() : mMessage.attachment.duration) * 1000);

        holder.txt_Timer.setText("00/" + MusicPlayer.milliSecondsToTimer(_st));

        if (holder.waveView.getTag().equals(mMessage.messageID) && mMessage.messageID.equals(MusicPlayer.messageId)) {
            MusicPlayer.onCompleteChat = holder.complete;

//            holder.waveView.setProgress(MusicPlayer.musicProgress);

            if (MusicPlayer.musicProgress > 0) {
                holder.txt_Timer.setText(MusicPlayer.strTimer + "/" + MusicPlayer.musicTime);
            }


            holder.mTimeMusic = MusicPlayer.musicTime;

            if (MusicPlayer.mp != null) {
                if (MusicPlayer.mp.isPlaying()) {
                    holder.btnPlayMusic.setText(R.string.md_pause_button);
                } else {
                    holder.btnPlayMusic.setText(R.string.md_play_arrow);
                }
            }
        } else {
            holder.waveView.setProgress(0);
            holder.btnPlayMusic.setText(R.string.md_play_arrow);
        }

        holder.mMessageID = mMessage.messageID;

        if (HelperCalander.isPersianUnicode)
            holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));

    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        if (mMessage.isSenderMe() && ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status) == ProtoGlobal.RoomMessageStatus.LISTENED) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColor), PorterDuff.Mode.SRC_IN);
            }
            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.iGapColor), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }
            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        holder.txt_Timer.setTextColor(Color.parseColor(G.textTitleTheme));
        holder.author.setTextColor(Color.parseColor(G.textTitleTheme));
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        if (type == ProtoGlobal.Room.Type.CHANNEL) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }

            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.txt_Timer.setTextColor(Color.parseColor(G.textTitleTheme));
            holder.author.setTextColor(Color.parseColor(G.textTitleTheme));
        } else {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }

            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.grayNewDarker));
            holder.author.setTextColor(Color.parseColor(G.textTitleTheme));
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends NewChatItemHolder implements IThumbNailItem, IProgress {

        protected MessageProgress progress;
        protected AppCompatImageView thumbnail;
        //protected ImageView tic;
        protected AppCompatTextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected AppCompatTextView txt_Timer;
        protected AppCompatTextView author;
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";
        protected long mRoomId;
        protected ProtoGlobal.Room.Type mType;

        private ConstraintLayout rootView;
        private ConstraintSet set;
        private AudioWave waveView;
        private String messageTime;

        public ViewHolder(View view) {
            super(view);

            thumbnail = new AppCompatImageView(G.context);
            thumbnail.setId(R.id.thumbnail);
            AppUtils.setImageDrawable(thumbnail, R.drawable.microphone_icon);
            progress = getProgressBar(0);

            author = new AppCompatTextView(G.context);
            author.setId(R.id.cslv_txt_author);
            author.setText("recorded voice");
            author.setTextColor(Color.parseColor(G.textBubble));
            author.setSingleLine(true);
            setTextSize(author, R.dimen.dp14);
            author.setMaxLines(2);
            setTypeFace(author);


            btnPlayMusic = new AppCompatTextView(G.context);
            btnPlayMusic.setId(R.id.csla_btn_play_music);
            btnPlayMusic.setBackgroundResource(0);
            btnPlayMusic.setGravity(Gravity.CENTER);
            btnPlayMusic.setText(G.fragmentActivity.getResources().getString(R.string.md_play_arrow));
            btnPlayMusic.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
            setTextSize(btnPlayMusic, R.dimen.dp20);
            btnPlayMusic.setTypeface(G.typeface_Fontico);

            musicSeekbar = new SeekBar(G.context);
            musicSeekbar.setId(R.id.csla_seekBar1);
            LinearLayout.LayoutParams layout_652 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            musicSeekbar.setLayoutParams(layout_652);

            txt_Timer = new AppCompatTextView(G.context);
            txt_Timer.setId(R.id.csla_txt_timer);
            txt_Timer.setTextColor(getColor(R.color.gray));
            setTextSize(txt_Timer, R.dimen.dp8);
            setTypeFace(txt_Timer);


            rootView = new ConstraintLayout(getContext());
            set = new ConstraintSet();

            waveView = new AudioWave(getContext());
            waveView.setId(R.id.wv_voiceItem_progress);

            Date currentTime = Calendar.getInstance().getTime();
            String value = currentTime.toString();

            byte[] soundBytes = hexStringToByteArray(value + value + value);

            waveView.setWaveColor(Color.parseColor("#007fff"));
            waveView.setScaledData(soundBytes);
            waveView.setChunkHeight(dpToPx(16));
            waveView.setChunkRadius(dpToPx(8));
            waveView.setChunkSpacing(dpToPx(2));
            waveView.setChunkWidth(dpToPx(3));


            set.constrainHeight(btnPlayMusic.getId(), dpToPx(40));
            set.constrainWidth(btnPlayMusic.getId(), dpToPx(40));

            set.connect(btnPlayMusic.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(4));
            set.connect(btnPlayMusic.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(4));
            set.connect(btnPlayMusic.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, dpToPx(4));
            rootView.addView(btnPlayMusic);


            set.constrainWidth(waveView.getId(), dpToPx(190));
            set.constrainHeight(waveView.getId(), dpToPx(30));

            set.connect(waveView.getId(), ConstraintSet.LEFT, btnPlayMusic.getId(), ConstraintSet.RIGHT, dpToPx(4));
            set.centerVertically(waveView.getId(), ConstraintSet.PARENT_ID);
            rootView.addView(waveView);

            set.constrainHeight(progress.getId(), dpToPx(40));
            set.constrainWidth(progress.getId(), dpToPx(40));

            set.connect(progress.getId(), ConstraintSet.TOP, btnPlayMusic.getId(), ConstraintSet.TOP);
            set.connect(progress.getId(), ConstraintSet.BOTTOM, btnPlayMusic.getId(), ConstraintSet.BOTTOM);
            set.connect(progress.getId(), ConstraintSet.RIGHT, btnPlayMusic.getId(), ConstraintSet.RIGHT);
            set.connect(progress.getId(), ConstraintSet.LEFT, btnPlayMusic.getId(), ConstraintSet.LEFT);
            rootView.addView(progress);

            set.constrainWidth(txt_Timer.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(txt_Timer.getId(), ConstraintSet.WRAP_CONTENT);

            set.connect(txt_Timer.getId(), ConstraintSet.LEFT, waveView.getId(), ConstraintSet.LEFT);
            set.connect(txt_Timer.getId(), ConstraintSet.TOP, waveView.getId(), ConstraintSet.BOTTOM, dpToPx(2));
            rootView.addView(txt_Timer);


            set.applyTo(rootView);
            rootView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
            getContentBloke().addView(rootView, 0);
        }

        public static byte[] hexStringToByteArray(String s) {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
            return data;
        }

        @Override
        public ImageView getThumbNailImageView() {
            return thumbnail;
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }
    }
}
