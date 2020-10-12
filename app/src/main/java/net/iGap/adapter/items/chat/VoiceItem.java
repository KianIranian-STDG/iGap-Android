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

import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.audio.AudioWave;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.FontIconTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.LocalFileType;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRegisteredInfo;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;

public class VoiceItem extends AbstractMessage<VoiceItem, VoiceItem.ViewHolder> {

    private static final String PLAY = "play";
    private static final String PAUSE = "pause";
    private static final String UPDATE = "updateTime";

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
            G.handler.postDelayed(() -> holder.btnPlayMusic.setVisibility(View.VISIBLE), 200);
        } else {
            holder.waveView.setEnabled(false);
            holder.btnPlayMusic.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.waveView.setTag(mMessage.getMessageId());

        holder.mMessageID = mMessage.getMessageId() + "";

        holder.complete = (result, messageOne, MessageTow) -> {
            if (holder.waveView.getTag().equals(mMessage.getMessageId()) && (mMessage.getMessageId() + "").equals(MusicPlayer.messageId)) {

                switch (messageOne) {
                    case PLAY:
                        holder.btnPlayMusic.setText(holder.getResources().getString(R.string.play_icon));
                        break;
                    case PAUSE:
                        holder.btnPlayMusic.setText(holder.getResources().getString(R.string.pause_icon));
                        break;
                    case UPDATE:
                        if (result) {

                            G.handler.post(() -> {
                                holder.waveView.setProgress(MusicPlayer.musicProgress);
                                if ((mMessage.getMessageId() + "").equals(MusicPlayer.messageId)) {
                                    holder.txt_Timer.setText(MessageTow + holder.getContext().getString(R.string.forward_slash) + holder.mTimeMusic);
                                    if (HelperCalander.isPersianUnicode) {
                                        holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                                    }
                                }
                            });
                        } else {
                            holder.btnPlayMusic.post(() -> {

                                holder.txt_Timer.setText(MessageTow + holder.getContext().getString(R.string.forward_slash) + holder.mTimeMusic);
                                if (HelperCalander.isPersianUnicode) {
                                    holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                                }
                                holder.waveView.setProgress(0);
                            });
                        }

                }
            }
        };

        holder.rootView.setOnLongClickListener(getLongClickPerform(holder));

        holder.rootView.setOnClickListener(v -> {
            if (FragmentChat.isInSelectionMode) {
                holder.itemView.performLongClick();
            }
        });

        holder.btnPlayMusic.setOnClickListener(v -> {

            if (FragmentChat.isInSelectionMode) {
                holder.itemView.performLongClick();
                return;
            }
            if (holder.mFilePath.length() < 1)
                return;

            if (!structMessage.isSenderMe() && structMessage.realmRoomMessage.getStatus() != null &&
                    !structMessage.realmRoomMessage.getStatus().equals(ProtoGlobal.RoomMessageStatus.LISTENED.toString())
            ) {
                G.chatUpdateStatusUtil.sendUpdateStatus(holder.mType, holder.mRoomId, parseLong(holder.mMessageID), ProtoGlobal.RoomMessageStatus.LISTENED);
                RealmClientCondition.addOfflineListen(holder.mRoomId, parseLong(holder.mMessageID));
            }

            AndroidUtils.getAudioDuration(G.context, holder.mFilePath);
            int currentVoiceGoTO = (int) (AndroidUtils.getAudioDuration(G.context, holder.mFilePath) * holder.waveView.getProgress() / 100);
            MusicPlayer.currentDuration = currentVoiceGoTO;
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
            MusicPlayer.messageId = mMessage.getMessageId() + "";
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
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                int currentVoiceGoTO = (int) (AndroidUtils.getAudioDuration(G.context, holder.mFilePath) * holder.waveView.getProgress() / 100);
                String finalElapsedTime = exractTimingInString(currentVoiceGoTO);
                String totalSizeInString = exractTimingInString((int) AndroidUtils.getAudioDuration(G.context, holder.mFilePath));
                holder.txt_Timer.setText(finalElapsedTime + holder.getContext().getString(R.string.forward_slash) + totalSizeInString);
                if (HelperCalander.isPersianUnicode) {
                    holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                }
            }
            return false;
        });

        super.bindView(holder, payloads);

        ProtoGlobal.RoomMessageType _type = mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getMessageType() : mMessage.getMessageType();
        holder.mType = type;
        AppUtils.rightFileThumbnailIcon(holder.thumbnail, _type, null);

        holder.mRoomId = mMessage.getRoomId();

        RealmRegisteredInfo registeredInfo = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRegisteredInfo.getRegistrationInfo(realm, mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getUserId() : mMessage.getUserId());
        });

        if (registeredInfo != null) {
            holder.author.setText(G.context.getString(R.string.recorded_by) + " " + registeredInfo.getDisplayName());
        } else {
            holder.author.setText("");
        }

        final long _st = (int) ((mMessage.getForwardMessage() != null ? mMessage.getForwardMessage().getAttachment().getDuration() : structMessage.getAttachment().getDuration()) * 1000);

        holder.txt_Timer.setText("00:00/" + MusicPlayer.milliSecondsToTimer(_st));

        if (holder.waveView.getTag().equals(mMessage.getMessageId()) && MusicPlayer.messageId.equals(mMessage.getMessageId() + "")) {
            MusicPlayer.onCompleteChat = holder.complete;

            holder.waveView.setProgress(MusicPlayer.musicProgress);
            if (MusicPlayer.musicProgress > 0) {
                holder.txt_Timer.setText(MusicPlayer.strTimer + holder.getContext().getString(R.string.forward_slash) + MusicPlayer.musicTime);
            }


            holder.mTimeMusic = MusicPlayer.musicTime;

            if (MusicPlayer.mp != null) {
                if (MusicPlayer.mp.isPlaying()) {
                    holder.btnPlayMusic.setText(holder.getResources().getString(R.string.pause_icon));
                } else {
                    holder.btnPlayMusic.setText(holder.getResources().getString(R.string.play_icon));
                }
            }
        } else {
            holder.waveView.setProgress(0);
            holder.btnPlayMusic.setText(holder.getResources().getString(R.string.play_icon));
        }


        if (HelperCalander.isPersianUnicode)
            holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));

    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        holder.txt_Timer.setTextColor(theme.getSendMessageOtherTextColor(holder.getContext()));
        holder.author.setTextColor(theme.getSendMessageOtherTextColor(holder.getContext()));

        ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
        if (mMessage.getStatus() != null) {
            try {
                status = ProtoGlobal.RoomMessageStatus.valueOf(mMessage.getStatus());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        if (status == ProtoGlobal.RoomMessageStatus.LISTENED) {
            holder.listenView.setVisibility(View.GONE);
        } else {
            holder.listenView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        holder.txt_Timer.setTextColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));
        holder.author.setTextColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));
        holder.listenView.setVisibility(View.GONE);
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public class ViewHolder extends NewChatItemHolder implements IThumbNailItem, IProgress {

        private MessageProgress progress;
        private AppCompatImageView thumbnail;
        private FontIconTextView btnPlayMusic;
        private OnComplete complete;
        private AppCompatTextView txt_Timer;
        private AppCompatTextView author;
        private String mFilePath = "";
        private String mMessageID = "";
        private String mTimeMusic = "";
        private long mRoomId;
        private ProtoGlobal.Room.Type mType;
        private ConstraintLayout rootView;
        private ConstraintSet set;
        private AudioWave waveView;
        private View listenView;

        public ViewHolder(View view) {
            super(view);

            thumbnail = new AppCompatImageView(view.getContext());
            thumbnail.setId(R.id.thumbnail);
            AppUtils.setImageDrawable(thumbnail, R.drawable.microphone_icon);
            progress = getProgressBar(view.getContext(), 0);

            author = new AppCompatTextView(view.getContext());
            author.setId(R.id.cslv_txt_author);
            author.setSingleLine(true);
            setTextSize(author, R.dimen.standardTextSize);
            author.setMaxLines(2);
            setTypeFace(author);


            btnPlayMusic = new FontIconTextView(view.getContext());
            btnPlayMusic.setId(R.id.csla_btn_play_music);
            btnPlayMusic.setBackgroundResource(0);
            btnPlayMusic.setGravity(Gravity.CENTER);
            btnPlayMusic.setText(R.string.play_icon);
            setTextSize(btnPlayMusic, R.dimen.dp36);

            txt_Timer = new AppCompatTextView(view.getContext());
            txt_Timer.setId(R.id.csla_txt_timer);
            setTextSize(txt_Timer, R.dimen.verySmallTextSize);
            setTypeFace(txt_Timer);

            listenView = new View(view.getContext());
            listenView.setBackground(getResources().getDrawable(R.drawable.shape_voice_item_listen));
            listenView.setId(R.id.view_listen);

            rootView = new ConstraintLayout(getContext());
            set = new ConstraintSet();

            waveView = new AudioWave(getContext());
            waveView.setId(R.id.wv_voiceItem_progress);


            Date currentTime = Calendar.getInstance().getTime();
            String value = currentTime.toString() + currentTime.toString();

            byte[] soundBytes = hexStringToByteArray(value + value);

            waveView.setWaveColor(getColor(R.color.voice_item_dark));

            waveView.setScaledData(soundBytes);
            waveView.setChunkHeight(dpToPx(16));
            waveView.setMinChunkHeight(dpToPx(2));
            waveView.setChunkRadius(dpToPx(8));
            waveView.setExpansionAnimated(true);
            waveView.setChunkSpacing(dpToPx(1));
            waveView.setChunkWidth(dpToPx(3));


            set.constrainHeight(btnPlayMusic.getId(), dpToPx(40));
            set.constrainWidth(btnPlayMusic.getId(), dpToPx(40));

            set.connect(btnPlayMusic.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(4));
            set.connect(btnPlayMusic.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(4));
            set.connect(btnPlayMusic.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, dpToPx(4));
            rootView.addView(btnPlayMusic);


            set.constrainWidth(waveView.getId(), dpToPx(190));
            set.constrainHeight(waveView.getId(), dpToPx(30));

            set.constrainWidth(listenView.getId(), LayoutCreator.dp(4));
            set.constrainHeight(listenView.getId(), LayoutCreator.dp(4));

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

            set.connect(listenView.getId(), ConstraintSet.LEFT, txt_Timer.getId(), ConstraintSet.RIGHT, LayoutCreator.dp(8));
            set.connect(listenView.getId(), ConstraintSet.TOP, txt_Timer.getId(), ConstraintSet.TOP);
            set.connect(listenView.getId(), ConstraintSet.BOTTOM, txt_Timer.getId(), ConstraintSet.BOTTOM);
            rootView.addView(listenView);

            set.constrainWidth(txt_Timer.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(txt_Timer.getId(), ConstraintSet.WRAP_CONTENT);

            set.connect(txt_Timer.getId(), ConstraintSet.LEFT, waveView.getId(), ConstraintSet.LEFT);
            set.connect(txt_Timer.getId(), ConstraintSet.TOP, waveView.getId(), ConstraintSet.BOTTOM, dpToPx(2));
            rootView.addView(txt_Timer);


            set.applyTo(rootView);
            rootView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
            getContentBloke().addView(rootView, 0);
        }

        private byte[] hexStringToByteArray(String s) {
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

    private String exractTimingInString(int currentVoiceGoTO) {
        int timeToSec = currentVoiceGoTO / 1000;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentVoiceGoTO);
        long sec = timeToSec % 60;

        String minTo = minutes + "";
        String secTo = sec + "";
        if (sec < 10) {
            secTo = "0" + secTo;
        }
        if (minutes < 10) {
            minTo = "0" + minTo;
        }
        String finalElapsedTime = minTo + ":" + secTo;
        return finalElapsedTime;
    }
}
