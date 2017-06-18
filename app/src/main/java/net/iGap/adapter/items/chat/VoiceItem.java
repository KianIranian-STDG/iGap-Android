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

import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import io.realm.Realm;
import io.realm.RealmList;
import java.io.File;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityChat;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnComplete;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SUID;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmOfflineListen;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

public class VoiceItem extends AbstractMessage<VoiceItem, VoiceItem.ViewHolder> {

    private long roomId;

    public VoiceItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override public int getType() {
        return R.id.chatSubLayoutVoice;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_voice;
    }

    @Override public void onLoadThumbnailFromLocal(ViewHolder holder, final String localPath, LocalFileType fileType) {
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

        ProtoGlobal.RoomMessageType _type = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType;
        holder.mType = type;
        AppUtils.rightFileThumbnailIcon(holder.thumbnail, _type, null);

        Realm realm = Realm.getDefaultInstance();

        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(mMessage.messageID)).findFirst();

        holder.mRoomId = mMessage.roomId;

        //if (realmRoomMessage !=null){
        //    if (realmRoomMessage.getStatus().contains(ProtoGlobal.RoomMessageStatus.LISTENED.toString())){
        //        holder.iconHearing.setVisibility(View.VISIBLE);
        //        holder.tic.setVisibility(View.VISIBLE);
        //        //try {
        //        //    holder.tic.setColorFilter(ContextCompat.getColor(G.context, R.color.iGapColor));
        //        //} catch (NullPointerException e) {
        //        //    // imageView.setColorFilter(color,android.graphics.PorterDuff.Mode.MULTIPLY);
        //        //    try {
        //        //        holder.tic.setColorFilter(G.context.getResources().getColor(R.color.iGapColor));
        //        //    } catch (Exception e1) {
        //        //    }
        //        //}
        //    }
        //}

        RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getUserId() : Long.parseLong(mMessage.senderID))
            .findFirst();

        if (registeredInfo != null) {
            holder.author.setText("Recorded By " + registeredInfo.getDisplayName());
        } else {
            holder.author.setText("");
        }

        realm.close();

        final long _st = (int) ((mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getDuration() : mMessage.attachment.duration) * 1000);

        holder.txt_Timer.setText("00/" + MusicPlayer.milliSecondsToTimer(_st));

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

        if (HelperCalander.isLanguagePersian) holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
    }

    @Override protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
        }

        holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
        holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        holder.author.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
    }

    @Override protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        if (type == ProtoGlobal.Room.Type.CHANNEL) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }

            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
            holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
            holder.author.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        } else {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
            }

            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.green));
            holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.grayNewDarker));
            holder.author.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView thumbnail;
        //protected ImageView tic;
        protected TextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected TextView txt_Timer;
        protected TextView author;
        protected TextView iconHearing;
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";
        protected long mRoomId;
        protected ProtoGlobal.Room.Type mType;

        public ViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            author = (TextView) view.findViewById(R.id.cslv_txt_author);
            btnPlayMusic = (TextView) view.findViewById(R.id.csla_btn_play_music);
            txt_Timer = (TextView) view.findViewById(R.id.csla_txt_timer);
            iconHearing = (TextView) view.findViewById(R.id.cslr_txt_hearing);
            musicSeekbar = (SeekBar) view.findViewById(R.id.csla_seekBar1);
            //tic = (ImageView) view.findViewById(R.id.cslr_txt_tic);

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

                                if (HelperCalander.isLanguagePersian) txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_Timer.getText().toString()));

                                musicSeekbar.setProgress(MusicPlayer.musicProgress);
                            }
                        });
                    }
                }
            };

            btnPlayMusic.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                    if (mFilePath.length() < 1) return;

                    G.chatUpdateStatusUtil.sendUpdateStatus(mType, mRoomId, Long.parseLong(mMessageID), ProtoGlobal.RoomMessageStatus.LISTENED);

                    final Realm realm = Realm.getDefaultInstance();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, mRoomId).findFirst();

                            if (realmClientCondition != null) {

                                RealmOfflineListen realmOfflineListen = realm.createObject(RealmOfflineListen.class, SUID.id().get());
                                realmOfflineListen.setOfflineListen(Long.parseLong(mMessageID));
                                if (realmClientCondition.getOfflineListen() != null) {
                                    realmClientCondition.getOfflineListen().add(realmOfflineListen);
                                } else {
                                    RealmList<RealmOfflineListen> offlineSeenListen = new RealmList<>();
                                    offlineSeenListen.add(realmOfflineListen);
                                    realmClientCondition.setOfflineListen(offlineSeenListen);
                                }

                                //realmClientCondition.getOfflineListen().add(realmOfflineListen);
                                Log.i("OOOOOOOBB", "mMessageID: " + mMessageID);
                            }
                        }
                    });

                    realm.close();

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

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
