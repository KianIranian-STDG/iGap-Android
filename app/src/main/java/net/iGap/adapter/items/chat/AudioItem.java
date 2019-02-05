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
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import static android.support.design.R.id.center;
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
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnComplete;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.util.List;

import io.realm.Realm;

import static android.R.attr.left;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.RIGHT;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static java.lang.Boolean.TRUE;
import static net.iGap.G.context;
import static net.iGap.R.dimen.messageContainerPadding;

public class AudioItem extends AbstractMessage<AudioItem, AudioItem.ViewHolder> {

    public AudioItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
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
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);

        if (holder.musicSeekbar.getTag().equals(holder.mMessageID)) {
            if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {

                holder.mFilePath = localPath;
                holder.musicSeekbar.setEnabled(true);
                holder.btnPlayMusic.setEnabled(true);

                //if (!mMessage.isSenderMe() && Build.VERSION.SDK_INT >= JELLY_BEAN) {
                //    holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
                //}

                holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
            } else {
                holder.musicSeekbar.setEnabled(false);
                holder.btnPlayMusic.setEnabled(false);

                holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.gray_6c));
            }
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        if (mMessage.forwardedFrom != null) {
            holder.mMessageID = mMessage.forwardedFrom.getMessageId() + "";
        } else {
            holder.mMessageID = mMessage.messageID;
        }

        holder.musicSeekbar.setTag(holder.mMessageID);

        holder.complete = new OnComplete() {
            @Override
            public void complete(final boolean result, String messageOne, final String MessageTow) {

                if (holder.musicSeekbar.getTag().equals(holder.mMessageID) && holder.mMessageID.equals(MusicPlayer.messageId)) {
                    if (messageOne.equals("play")) {
                        holder.btnPlayMusic.setText(R.string.md_play_arrow);
                    } else if (messageOne.equals("pause")) {
                        holder.btnPlayMusic.setText(R.string.md_pause_button);
                    } else if (messageOne.equals("updateTime")) {

                        if (result) {

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (holder.mMessageID.equals(MusicPlayer.messageId)) {

                                        holder.txt_Timer.setText(MessageTow + "/" + holder.mTimeMusic);

                                        if (result) {
                                            holder.musicSeekbar.setProgress(MusicPlayer.musicProgress);
                                        } else {
                                            holder.musicSeekbar.setProgress(0);
                                        }

                                        if (HelperCalander.isPersianUnicode) {
                                            holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber((holder.txt_Timer.getText().toString())));
                                        }
                                    }
                                }
                            });
                        } else {
                            holder.btnPlayMusic.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.txt_Timer.setText(MessageTow + "/" + holder.mTimeMusic);
                                    holder.musicSeekbar.setProgress(0);
                                    if (HelperCalander.isPersianUnicode) {
                                        holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber((holder.txt_Timer.getText().toString())));
                                    }
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

                String name = "";

                if (mMessage != null && mMessage.getAttachment() != null) {
                    name = mMessage.getAttachment().name;
                }

                if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                    MusicPlayer.onCompleteChat = holder.complete;

                    if (MusicPlayer.mp != null) {
                        MusicPlayer.playAndPause();
                    } else {
                        MusicPlayer.startPlayer(name, holder.mFilePath, FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, true, holder.mMessageID);
                        messageClickListener.onPlayMusic(holder.mMessageID);
                    }
                } else {

                    MusicPlayer.stopSound();
                    MusicPlayer.onCompleteChat = holder.complete;

                    MusicPlayer.startPlayer(name, holder.mFilePath, FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, true, holder.mMessageID);
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

        if (mMessage.isSenderMe()) {
            AppUtils.setImageDrawable(holder.thumbnail, R.drawable.white_music_note);
        } else {
            AppUtils.setImageDrawable(holder.thumbnail, R.drawable.green_music_note);
        }

        String text = "";

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                if (mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal()) {
                    holder.fileSize.setVisibility(View.INVISIBLE);
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

            text = mMessage.forwardedFrom.getMessage();
        } else {
            if (mMessage.attachment != null) {
                if (mMessage.attachment.isFileExistsOnLocal()) {
                    holder.fileSize.setVisibility(View.INVISIBLE);
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

            text = mMessage.messageText;
        }

        setTextIfNeeded((TextView) holder.itemView.findViewById(R.id.messageSenderTextMessage), text);

        View audioBoxView = holder.itemView.findViewById(R.id.audioBox);
        if (G.isDarkTheme) {
            audioBoxView.setBackgroundResource(R.drawable.green_bg_rounded_corner_dark);
        } else {
            audioBoxView.setBackgroundResource(R.drawable.green_bg_rounded_corner);
        }

        GradientDrawable circleDarkColor = (GradientDrawable) audioBoxView.getBackground();
        circleDarkColor.setColor(Color.parseColor(G.bubbleChatMusic));

        //if ((mMessage.forwardedFrom != null && mMessage.forwardedFrom.getForwardMessage() != null && mMessage.forwardedFrom.getForwardMessage().getMessage() != null && !TextUtils.isEmpty(mMessage.forwardedFrom.getForwardMessage().getMessage())) || !TextUtils.isEmpty(mMessage.messageText)) {
        //    audioBoxView.setBackgroundResource(R.drawable.green_bg_rounded_corner);
        //} else {
        //    audioBoxView.setBackgroundColor(Color.TRANSPARENT);
        //}

        final long _st = (int) ((mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getDuration() : mMessage.attachment.duration) * 1000);

        holder.txt_Timer.setText("00/" + MusicPlayer.milliSecondsToTimer(_st));

        if (holder.musicSeekbar.getTag().equals(holder.mMessageID) && holder.mMessageID.equals(MusicPlayer.messageId)) {
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

        if (HelperCalander.isPersianUnicode) {
            (holder.txt_Timer).setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
        }
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
        }
        holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.txt_Timer.setTextColor(Color.parseColor(G.textTitleTheme));
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        if (type == ProtoGlobal.Room.Type.CHANNEL) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
            }
            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.txt_Timer.setTextColor(Color.parseColor(G.textTitleTheme));
        } else {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
            }
            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.grayNewDarker));
            holder.fileName.setTextColor(Color.parseColor(G.textSubTheme));
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemWithTextHolder implements IThumbNailItem, IProgress {
        protected MessageProgress progress;
        protected ImageView thumbnail;
        protected TextView fileSize;
        protected TextView fileName;
        protected TextView songArtist;
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";

        protected MaterialDesignTextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected TextView txt_Timer;

        public ViewHolder(final View view) {
            super(view);
            LinearLayout audioBox = new LinearLayout(G.context);
            audioBox.setId(R.id.audioBox);
            setLayoutDirection(audioBox, View.LAYOUT_DIRECTION_LTR);
            audioBox.setMinimumHeight((int) context.getResources().getDimension(R.dimen.dp130));
            audioBox.setMinimumWidth(i_Dp(R.dimen.dp220));
            audioBox.setOrientation(HORIZONTAL);
            audioBox.setPadding(0, (int) G.context.getResources().getDimension(messageContainerPadding), 0, (int) G.context.getResources().getDimension(R.dimen.messageContainerPaddingBottom));
            LinearLayout.LayoutParams layout_262 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            audioBox.setLayoutParams(layout_262);

            LinearLayout linearLayout_39 = new LinearLayout(G.context);
            linearLayout_39.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layout_803 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layout_803.leftMargin = (int) G.context.getResources().getDimension(R.dimen.dp8);
            linearLayout_39.setLayoutParams(layout_803);

            LinearLayout linearLayout_632 = new LinearLayout(G.context);
            LinearLayout.LayoutParams layout_842 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLayout_632.setLayoutParams(layout_842);

            LinearLayout linearLayout_916 = new LinearLayout(G.context);
            linearLayout_916.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout_916.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layout_6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLayout_916.setLayoutParams(layout_6);

            FrameLayout frameLayout = new FrameLayout(G.context);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

            thumbnail = new ImageView(G.context);
            thumbnail.setId(R.id.thumbnail);
            LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp48), (int) G.context.getResources().getDimension(R.dimen.dp48));
            thumbnail.setAdjustViewBounds(true);
            thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
            AppUtils.setImageDrawable(thumbnail, R.drawable.green_music_note);
            thumbnail.setLayoutParams(thumbnailParams);

            fileSize = new TextView(G.context);
            fileSize.setId(R.id.fileSize);
            fileSize.setTextAppearance(context, android.R.style.TextAppearance_Small);
            fileSize.setGravity(BOTTOM | CENTER_HORIZONTAL);
            fileSize.setSingleLine(true);
            fileSize.setText("3.2 mb");
            fileSize.setAllCaps(TRUE);
            fileSize.setTextColor(Color.parseColor(G.textChatMusic));
            setTextSize(fileSize, R.dimen.dp12);
            setTypeFace(fileSize);
            LinearLayout.LayoutParams layout_996 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            fileSize.setLayoutParams(layout_996);
            linearLayout_632.addView(linearLayout_916);

            LinearLayout linearLayout_222 = new LinearLayout(G.context);
            linearLayout_222.setOrientation(VERTICAL);
            linearLayout_222.setPadding((int) G.context.getResources().getDimension(R.dimen.dp8), 0, 0, 0);
            LinearLayout.LayoutParams layout_114 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayout_222.setLayoutParams(layout_114);

            fileName = new TextView(G.context);
            fileName.setId(R.id.fileName);
            fileName.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            fileName.setGravity(LEFT);
            fileName.setSingleLine(true);
            fileName.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            fileName.setMaxWidth((int) G.context.getResources().getDimension(R.dimen.dp160));
            fileName.setText("file_name.ext");
            fileName.setTextColor(Color.parseColor(G.textChatMusic));
            setTextSize(fileName, R.dimen.dp14);
            fileName.setTypeface(G.typeface_IRANSansMobile_Bold);
            LinearLayout.LayoutParams layout_298 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            fileName.setLayoutParams(layout_298);
            linearLayout_222.addView(fileName);

            songArtist = new TextView(G.context);
            songArtist.setId(R.id.songArtist);
            songArtist.setTextAppearance(context, android.R.style.TextAppearance_Small);
            songArtist.setSingleLine(true);
            songArtist.setText("Artist");
            setTypeFace(songArtist);
            songArtist.setTextColor(Color.parseColor(G.textChatMusic));
            LinearLayout.LayoutParams layout_757 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            songArtist.setLayoutParams(layout_757);
            linearLayout_222.addView(songArtist);
            linearLayout_632.addView(linearLayout_222);
            linearLayout_39.addView(linearLayout_632);

            LinearLayout audioPlayerViewContainer = new LinearLayout(G.context);
            audioPlayerViewContainer.setId(R.id.audioPlayerViewContainer);
            audioPlayerViewContainer.setOrientation(VERTICAL);
            LinearLayout.LayoutParams layout_435 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            audioPlayerViewContainer.setLayoutParams(layout_435);

            LinearLayout linearLayout_511 = new LinearLayout(G.context);
            linearLayout_511.setGravity(left | center);
            LinearLayout.LayoutParams layout_353 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) G.context.getResources().getDimension(R.dimen.dp36));
            linearLayout_511.setLayoutParams(layout_353);

            btnPlayMusic = new MaterialDesignTextView(G.context);
            btnPlayMusic.setId(R.id.txt_play_music);
            btnPlayMusic.setBackgroundResource(0); //txt_play_music.setBackgroundResource(@null);
            btnPlayMusic.setTypeface(G.typeface_Fontico);
            btnPlayMusic.setGravity(CENTER);
            btnPlayMusic.setText(G.fragmentActivity.getResources().getString(R.string.md_play_arrow));
            btnPlayMusic.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
            setTextSize(btnPlayMusic, R.dimen.dp20);
            LinearLayout.LayoutParams layout_326 = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp32), LinearLayout.LayoutParams.MATCH_PARENT);
            btnPlayMusic.setLayoutParams(layout_326);
            linearLayout_511.addView(btnPlayMusic);

            musicSeekbar = new SeekBar(G.context);
            musicSeekbar.setId(R.id.csla_seekBar1);
            LinearLayout.LayoutParams layout_990 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_990.weight = 1;
            layout_990.gravity = CENTER;
            musicSeekbar.setEnabled(false);
            musicSeekbar.setLayoutParams(layout_990);
            musicSeekbar.setProgress(0);
            linearLayout_511.addView(musicSeekbar);
            audioPlayerViewContainer.addView(linearLayout_511);

            txt_Timer = new TextView(G.context);
            txt_Timer.setId(R.id.csla_txt_timer);
            txt_Timer.setPadding(0, 0, (int) G.context.getResources().getDimension(R.dimen.dp8), 0);
            txt_Timer.setText("00:00");
            txt_Timer.setTextColor(G.context.getResources().getColor(R.color.toolbar_background));
            setTextSize(txt_Timer, R.dimen.dp10);
            setTypeFace(txt_Timer);
            LinearLayout.LayoutParams layout_637 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_637.gravity = RIGHT;
            layout_637.leftMargin = (int) G.context.getResources().getDimension(R.dimen.dp52);
            txt_Timer.setLayoutParams(layout_637);
            audioPlayerViewContainer.addView(txt_Timer);
            linearLayout_39.addView(audioPlayerViewContainer);
            audioBox.addView(linearLayout_39);
            m_container.addView(audioBox);

            LinearLayout.LayoutParams layout_992 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp220), LinearLayout.LayoutParams.WRAP_CONTENT); // before width was -> LinearLayout.LayoutParams.MATCH_PARENT, for fix text scroll changed it
            setLayoutMessageContainer(layout_992);

            linearLayout_916.addView(frameLayout);
            linearLayout_916.addView(fileSize);
            frameLayout.addView(thumbnail);
            progress = getProgressBar(R.dimen.dp48);
            frameLayout.addView(progress);
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
