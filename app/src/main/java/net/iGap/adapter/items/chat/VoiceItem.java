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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnComplete;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRegisteredInfo;

import java.io.File;
import java.util.List;

import io.realm.Realm;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static net.iGap.fragments.FragmentChat.getRealmChat;

public class VoiceItem extends AbstractMessage<VoiceItem, VoiceItem.ViewHolder> {

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
            holder.musicSeekbar.setEnabled(true);
            holder.btnPlayMusic.setEnabled(true);

            if (!mMessage.isSenderMe() && Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
            }
            holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.toolbar_background));
        } else {
            holder.musicSeekbar.setEnabled(false);
            holder.btnPlayMusic.setEnabled(false);
            holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.gray_6c));
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.musicSeekbar.setTag(mMessage.messageID);

        holder.complete = new OnComplete() {
            @Override
            public void complete(final boolean result, String messageOne, final String MessageTow) {

                if (holder.musicSeekbar.getTag().equals(mMessage.messageID) && mMessage.messageID.equals(MusicPlayer.messageId)) {
                    if (messageOne.equals("play")) {
                        holder.btnPlayMusic.setText(R.string.md_play_arrow);
                    } else if (messageOne.equals("pause")) {
                        holder.btnPlayMusic.setText(R.string.md_pause_button);
                    } else if (messageOne.equals("updateTime")) {

                        if (result) {

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (mMessage.messageID.equals(MusicPlayer.messageId)) {
                                        holder.txt_Timer.setText(MessageTow + "/" + holder.mTimeMusic);
                                        if (HelperCalander.isPersianUnicode) {
                                            holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                                        }
                                        holder.musicSeekbar.setProgress(MusicPlayer.musicProgress);
                                    }
                                }
                            });
                        } else {
                            holder.btnPlayMusic.post(new Runnable() {
                                @Override
                                public void run() {

                                    holder.txt_Timer.setText(MessageTow + "/" + holder.mTimeMusic);
                                    if (HelperCalander.isPersianUnicode) {
                                        holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                                    }
                                    holder.musicSeekbar.setProgress(0);
                                }
                            });
                        }
                    }
                }
            }
        };

        holder.btnPlayMusic.setOnLongClickListener(getLongClickPerform(holder));

        holder.btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        holder.musicSeekbar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (FragmentChat.isInSelectionMode) {
                        holder.itemView.performLongClick();
                        return true;
                    }
                    if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                        MusicPlayer.setMusicProgress(holder.musicSeekbar.getProgress());
                    }
                }
                return false;
            }
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

        if (holder.musicSeekbar.getTag().equals(mMessage.messageID) && mMessage.messageID.equals(MusicPlayer.messageId)) {
            MusicPlayer.onCompleteChat = holder.complete;

            holder.musicSeekbar.setProgress(MusicPlayer.musicProgress);

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
            holder.musicSeekbar.setProgress(0);
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

    protected static class ViewHolder extends ChatItemHolder implements IThumbNailItem, IProgress {

        protected MessageProgress progress;
        protected ImageView thumbnail;
        //protected ImageView tic;
        protected TextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected TextView txt_Timer;
        protected TextView author;
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";
        protected long mRoomId;
        protected ProtoGlobal.Room.Type mType;

        public ViewHolder(View view) {
            super(view);
            LinearLayout linearLayout_197 = new LinearLayout(G.context);
            linearLayout_197.setGravity(Gravity.CENTER_VERTICAL);
            setLayoutDirection(linearLayout_197, View.LAYOUT_DIRECTION_LTR);
            linearLayout_197.setMinimumHeight(i_Dp(R.dimen.dp95));
            linearLayout_197.setMinimumWidth(i_Dp(R.dimen.dp220));
            linearLayout_197.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layout_80 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout_197.setLayoutParams(layout_80);

            LinearLayout audioPlayerViewContainer = new LinearLayout(G.context);
            audioPlayerViewContainer.setId(R.id.audioPlayerViewContainer);
            audioPlayerViewContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layout_868 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            audioPlayerViewContainer.setLayoutParams(layout_868);

            LinearLayout linearLayout_153 = new LinearLayout(G.context);
            LinearLayout.LayoutParams layout_928 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout_153.setLayoutParams(layout_928);

            //****************************
            FrameLayout frameLayout_161 = new FrameLayout(G.context);

            int pading = i_Dp(R.dimen.dp4);
            frameLayout_161.setPadding(pading, pading, pading, pading);

            LinearLayout.LayoutParams layout_1488 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp40), i_Dp(R.dimen.dp40));
            layout_1488.gravity = Gravity.CENTER;
            frameLayout_161.setLayoutParams(layout_1488);

            thumbnail = new ImageView(G.context);
            thumbnail.setId(R.id.thumbnail);
            FrameLayout.LayoutParams layout_152 = new FrameLayout.LayoutParams(i_Dp(R.dimen.dp20), i_Dp(R.dimen.dp20));
            layout_152.gravity = Gravity.CENTER;
            AppUtils.setImageDrawable(thumbnail, R.drawable.microphone_icon);
            thumbnail.setLayoutParams(layout_152);
            frameLayout_161.addView(thumbnail);
            progress = getProgressBar(0);
            frameLayout_161.addView(progress);
            linearLayout_153.addView(frameLayout_161);

            author = new TextView(G.context);
            author.setId(R.id.cslv_txt_author);
            author.setText("recorded voice");
            author.setTextColor(Color.parseColor(G.textBubble));
            author.setSingleLine(true);
            setTextSize(author, R.dimen.dp14);
            author.setMaxLines(2);
            setTypeFace(author);
            // cslv_txt_author.setEllipsize(TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams layout_799 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_799.topMargin = i_Dp(R.dimen.dp12);
            author.setLayoutParams(layout_799);
            linearLayout_153.addView(author);
            audioPlayerViewContainer.addView(linearLayout_153);

            LinearLayout linearLayout_503 = new LinearLayout(G.context);
            linearLayout_503.setGravity(Gravity.LEFT | Gravity.CENTER);
            linearLayout_503.setMinimumHeight(i_Dp(R.dimen.dp32));
            LinearLayout.LayoutParams layout_669 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
            linearLayout_503.setLayoutParams(layout_669);

            btnPlayMusic = new TextView(G.context);
            btnPlayMusic.setId(R.id.csla_btn_play_music);
            btnPlayMusic.setBackgroundResource(0);
            btnPlayMusic.setGravity(Gravity.CENTER);
            btnPlayMusic.setEnabled(false);
            btnPlayMusic.setText(G.fragmentActivity.getResources().getString(R.string.md_play_arrow));
            btnPlayMusic.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
            setTextSize(btnPlayMusic, R.dimen.dp20);
            btnPlayMusic.setTypeface(G.typeface_Fontico);
            LinearLayout.LayoutParams layout_978 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp40), ViewGroup.LayoutParams.MATCH_PARENT);
            btnPlayMusic.setLayoutParams(layout_978);
            linearLayout_503.addView(btnPlayMusic);

            musicSeekbar = new SeekBar(G.context);
            musicSeekbar.setId(R.id.csla_seekBar1);
            LinearLayout.LayoutParams layout_652 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            musicSeekbar.setLayoutParams(layout_652);
            linearLayout_503.addView(musicSeekbar);
            audioPlayerViewContainer.addView(linearLayout_503);

            txt_Timer = new TextView(G.context);
            txt_Timer.setId(R.id.csla_txt_timer);
            txt_Timer.setPadding(0, 0, i_Dp(R.dimen.dp8), 0);
            txt_Timer.setText("00:00");
            txt_Timer.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
            setTextSize(txt_Timer, R.dimen.dp10);
            setTypeFace(txt_Timer);
            LinearLayout.LayoutParams layout_758 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_758.gravity = Gravity.RIGHT;
            layout_758.leftMargin = i_Dp(R.dimen.dp52);
            txt_Timer.setLayoutParams(layout_758);

            audioPlayerViewContainer.addView(txt_Timer);
            linearLayout_197.addView(audioPlayerViewContainer);

            m_container.addView(linearLayout_197);
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
