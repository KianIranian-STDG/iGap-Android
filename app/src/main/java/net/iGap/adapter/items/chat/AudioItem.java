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

import android.graphics.PorterDuff;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.LocalFileType;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.proto.ProtoGlobal;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.LEFT;
import static java.lang.Boolean.TRUE;
import static net.iGap.G.context;

public class AudioItem extends AbstractMessage<AudioItem, AudioItem.ViewHolder> {

    public AudioItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
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
        if (holder.seekBar.getTag().equals(holder.mMessageID)) {
            if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {
                holder.mFilePath = localPath;
                holder.seekBar.setEnabled(true);
                holder.playBtn.setEnabled(true);
                holder.seekBar.setVisibility(View.VISIBLE);
                holder.songArtist.setVisibility(View.INVISIBLE);
                holder.playBtn.setVisibility(View.VISIBLE);
                holder.songSize.setVisibility(View.INVISIBLE);

            } else {
                holder.seekBar.setEnabled(false);
                holder.playBtn.setEnabled(false);
                holder.seekBar.setVisibility(View.INVISIBLE);
                holder.songArtist.setVisibility(View.VISIBLE);
                holder.playBtn.setVisibility(View.INVISIBLE);
                holder.songSize.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        if (messageObject.forwardedMessage != null) {
            holder.mMessageID = messageObject.forwardedMessage.id + "";
        } else {
            holder.mMessageID = messageObject.id + "";
        }

        holder.seekBar.setTag(holder.mMessageID);

        holder.complete = (result, messageOne, MessageTow) -> {

            if (holder.seekBar.getTag().equals(holder.mMessageID) && holder.mMessageID.equals(MusicPlayer.messageId)) {
                if (messageOne.equals("play")) {
                    holder.playBtn.setText(R.string.icon_play);
                } else if (messageOne.equals("pause")) {
                    holder.playBtn.setText(R.string.icon_pause);
                } else if (messageOne.equals("updateTime")) {

                    if (result) {

                        G.handler.post(() -> {
                            if (holder.mMessageID.equals(MusicPlayer.messageId)) {

                                holder.songTimeTv.setText(MessageTow + "/" + holder.mTimeMusic);

                                if (result) {
                                    holder.seekBar.setProgress(MusicPlayer.musicProgress);
                                } else {
                                    holder.seekBar.setProgress(0);
                                }

                                if (HelperCalander.isPersianUnicode) {
                                    holder.songTimeTv.setText(HelperCalander.convertToUnicodeFarsiNumber((holder.songTimeTv.getText().toString())));
                                }
                            }
                        });
                    } else {
                        holder.playBtn.post(() -> {
                            holder.songTimeTv.setText(MessageTow + "/" + holder.mTimeMusic);
                            holder.seekBar.setProgress(0);
                            if (HelperCalander.isPersianUnicode) {
                                holder.songTimeTv.setText(HelperCalander.convertToUnicodeFarsiNumber((holder.songTimeTv.getText().toString())));
                            }
                        });
                    }
                }
            }
        };

        holder.playBtn.setOnLongClickListener(getLongClickPerform(holder));

        holder.playBtn.setOnClickListener(v -> {
            String name = "";

            if (FragmentChat.isInSelectionMode) {
                holder.itemView.performLongClick();
                return;
            }

            if (holder.mFilePath.length() < 1)
                return;

            if (messageObject != null && attachment != null) {
                name = attachment.name;
            }
            int currentVoiceGoTO = (int) (AndroidUtils.getAudioDuration(G.context, holder.mFilePath) * holder.seekBar.getProgress() / 100);
            MusicPlayer.currentDuration = currentVoiceGoTO;
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
        });

        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int currentVoiceGoTO = (int) (AndroidUtils.getAudioDuration(G.context, holder.mFilePath) * holder.seekBar.getProgress() / 100);
                String finalElapsedTime = exractTimingInString(currentVoiceGoTO);
                String totalSizeInString = exractTimingInString((int) AndroidUtils.getAudioDuration(G.context, holder.mFilePath));
                holder.songTimeTv.setText(finalElapsedTime + holder.getContext().getString(R.string.forward_slash) + totalSizeInString);
                if (HelperCalander.isPersianUnicode) {
                    holder.songTimeTv.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.songTimeTv.getText().toString()));
                }

                if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                    MusicPlayer.setMusicProgress(holder.seekBar.getProgress());
                }
            }
        });

        super.bindView(holder, payloads);

        if (messageObject.isSenderMe()) {
            AppUtils.setImageDrawable(holder.thumbnail, R.drawable.white_music_note);
        } else {
            AppUtils.setImageDrawable(holder.thumbnail, R.drawable.green_music_note);
        }


        if (messageObject.forwardedMessage != null) {
            if (messageObject.forwardedMessage.attachment != null) {
                if (!messageObject.forwardedMessage.attachment.isFileExistsOnLocal()) {
                    holder.songSize.setText(AndroidUtils.humanReadableByteCount(messageObject.forwardedMessage.attachment.size, true));
                }
                holder.songFileName.setText(messageObject.forwardedMessage.attachment.name);
                if (messageObject.forwardedMessage.attachment.isFileExistsOnLocal()) {
                    String artistName = AndroidUtils.getAudioArtistName(messageObject.forwardedMessage.attachment.filePath);
                    if (!TextUtils.isEmpty(artistName)) {
                        holder.songArtist.setText(artistName);
                    } else {
                        holder.songArtist.setText(holder.itemView.getResources().getString(R.string.unknown_artist));
                    }
                }
            }

        } else {
            if (attachment != null) {
                if (!attachment.isFileExistsOnLocal()) {
                    holder.songSize.setText(AndroidUtils.humanReadableByteCount(attachment.size, true));
                }
                holder.songFileName.setText(attachment.name);
            }
//            if (!TextUtils.isEmpty(structMessage.songArtist)) {
//                holder.songArtist.setText(structMessage.songArtist);
//            } else {
//                holder.songArtist.setText(holder.itemView.getResources().getString(R.string.unknown_artist));
//            }
        }

        setTextIfNeeded(holder.messageView);

        final long _st = (long) (attachment != null ? attachment.duration * 1000 : 0);

        holder.songTimeTv.setText("00:00/" + MusicPlayer.milliSecondsToTimer(_st));

        if (holder.seekBar.getTag().equals(holder.mMessageID) && holder.mMessageID.equals(MusicPlayer.messageId)) {
            MusicPlayer.onCompleteChat = holder.complete;

            holder.seekBar.setProgress(MusicPlayer.musicProgress);
            if (MusicPlayer.musicProgress > 0) {
                holder.songTimeTv.setText(MusicPlayer.strTimer + "/" + MusicPlayer.musicTime);
            }

            holder.mTimeMusic = MusicPlayer.musicTime;

            if (MusicPlayer.mp != null) {
                if (MusicPlayer.mp.isPlaying()) {
                    holder.playBtn.setText(R.string.icon_pause);
                } else {
                    holder.playBtn.setText(R.string.icon_play);
                }
            }
        } else {
            holder.seekBar.setProgress(0);
            holder.playBtn.setText(R.string.icon_play);
        }

        if (HelperCalander.isPersianUnicode) {
            (holder.songTimeTv).setText(HelperCalander.convertToUnicodeFarsiNumber(holder.songTimeTv.getText().toString()));
        }
        holder.tempText = holder.songSize.getText().toString();
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        holder.songFileName.setTextColor(theme.getSendMessageTextColor(holder.getContext()));
        holder.songSize.setTextColor(theme.getSendMessageOtherTextColor(holder.getContext()));
        holder.songArtist.setTextColor(theme.getSendMessageOtherTextColor(holder.getContext()));
        holder.songTimeTv.setTextColor(theme.getSendMessageOtherTextColor(holder.songTimeTv.getContext()));

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            holder.seekBar.getThumb().mutate().setColorFilter(holder.getColor(R.color.black),
                    PorterDuff.Mode.SRC_IN);
        }

        holder.seekBar.getProgressDrawable().setColorFilter(holder.getColor(R.color.text_line1_igap_dark),
                android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        holder.songFileName.setTextColor(theme.getReceivedMessageColor(holder.getContext()));
        holder.songSize.setTextColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));
        holder.songArtist.setTextColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));
        holder.songTimeTv.setTextColor(theme.getReceivedMessageOtherTextColor(holder.songTimeTv.getContext()));

        if (type == ProtoGlobal.Room.Type.CHANNEL) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.seekBar.getThumb().mutate().setColorFilter(holder.getColor(R.color.black),
                        PorterDuff.Mode.SRC_IN);
            }
            holder.seekBar.getProgressDrawable().setColorFilter(holder.getColor(R.color.text_line1_igap_dark),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            holder.seekBar.setVisibility(View.INVISIBLE);
        } else {

            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.seekBar.getThumb().mutate().setColorFilter(holder.getColor(R.color.black),
                        PorterDuff.Mode.SRC_IN);
            }
            holder.seekBar.getProgressDrawable().setColorFilter(holder.getColor(R.color.black),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View v) {
        return new ViewHolder(v);
    }

    public class ViewHolder extends ChatItemWithTextHolder implements IThumbNailItem, IProgress {
        private MessageProgress progress;
        private AppCompatImageView thumbnail;
        private AppCompatTextView songSize;
        private AppCompatTextView songFileName;
        private AppCompatTextView songArtist;
        private String mFilePath = "";
        private String mMessageID = "";
        private String mTimeMusic = "";
        private MaterialDesignTextView playBtn;
        private SeekBar seekBar;
        private OnComplete complete;
        private AppCompatTextView songTimeTv;
        private ConstraintLayout rootView;
        private ConstraintSet set;
        private CircleImageView coverIv;
        private String tempText;

        public ViewHolder(View view) {
            super(view);

            thumbnail = new AppCompatImageView(view.getContext());
            thumbnail.setId(R.id.thumbnail);
            LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams((int) G.context.getResources().getDimension(R.dimen.dp48), (int) G.context.getResources().getDimension(R.dimen.dp48));
            thumbnail.setAdjustViewBounds(true);
            thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
            thumbnail.setBackgroundResource(R.drawable.green_music_note);
            thumbnail.setLayoutParams(thumbnailParams);

            songSize = new AppCompatTextView(view.getContext());
            songSize.setId(R.id.fileSize);
            songSize.setTextAppearance(context, android.R.style.TextAppearance_Small);
            songSize.setGravity(BOTTOM | CENTER_HORIZONTAL);
            songSize.setSingleLine(true);
            songSize.setAllCaps(TRUE);
            setTextSize(songSize, R.dimen.verySmallTextSize);
            setTypeFace(songSize);

            songFileName = new AppCompatTextView(view.getContext());
            songFileName.setId(R.id.fileName);
            songFileName.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            songFileName.setGravity(LEFT);
            songFileName.setSingleLine(true);
            songFileName.setTextAppearance(view.getContext(), android.R.style.TextAppearance_Medium);
            songFileName.setMaxWidth((int) G.context.getResources().getDimension(R.dimen.dp160));
            setTextSize(songFileName, R.dimen.smallTextSize);
            songFileName.setTypeface(ResourcesCompat.getFont(songFileName.getContext(), R.font.main_font));

            songArtist = new AppCompatTextView(view.getContext());
            songArtist.setId(R.id.songArtist);
            songArtist.setTextAppearance(view.getContext(), android.R.style.TextAppearance_Small);
            songArtist.setSingleLine(true);
            songArtist.setText("Artist");
            setTextSize(songArtist, R.dimen.verySmallTextSize);
            setTypeFace(songArtist);

            playBtn = new MaterialDesignTextView(view.getContext());
            playBtn.setId(R.id.txt_play_music);
            playBtn.setBackgroundResource(0); //txt_play_music.setBackgroundResource(@null);
            playBtn.setTypeface(ResourcesCompat.getFont(playBtn.getContext(), R.font.font_icons));
            playBtn.setGravity(CENTER);
            playBtn.setTextColor(getColor(R.color.white));
            playBtn.setText(R.string.icon_play);
            setTextSize(playBtn, R.dimen.largeTextSize);
            playBtn.setBackgroundResource(R.drawable.background_audioitem_cover);

            seekBar = new SeekBar(view.getContext());
            seekBar.setId(R.id.csla_seekBar1);
            seekBar.setEnabled(false);
            seekBar.setProgress(0);

            songTimeTv = new AppCompatTextView(view.getContext());
            songTimeTv.setId(R.id.csla_txt_timer);
            songTimeTv.setPadding(0, 0, (int) G.context.getResources().getDimension(R.dimen.dp8), 0);
            songTimeTv.setText("00:00");
            setTextSize(songTimeTv, R.dimen.verySmallTextSize);
            setTypeFace(songTimeTv);

            LinearLayout.LayoutParams layout_992 = new LinearLayout.LayoutParams(i_Dp(R.dimen.dp220), LinearLayout.LayoutParams.WRAP_CONTENT); // before width was -> LinearLayout.LayoutParams.MATCH_PARENT, for fix text scroll changed it
            setLayoutMessageContainer(layout_992);

            progress = getProgressBar(view.getContext(), R.dimen.dp48);
            rootView = new ConstraintLayout(getContext());
            set = new ConstraintSet();

            coverIv = new CircleImageView(getContext());
            coverIv.setId(R.id.iv_musicItem_cover);
            coverIv.setBorderColor(0);
            coverIv.setImageResource(R.drawable.ic_music_cover_blue);

            set.constrainWidth(coverIv.getId(), dpToPx(45));
            set.constrainHeight(coverIv.getId(), dpToPx(45));

            set.constrainWidth(playBtn.getId(), dpToPx(45));
            set.constrainHeight(playBtn.getId(), dpToPx(45));

            set.constrainHeight(songFileName.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(songFileName.getId(), ConstraintSet.WRAP_CONTENT);

            set.constrainWidth(songArtist.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(songArtist.getId(), ConstraintSet.WRAP_CONTENT);

            set.constrainHeight(seekBar.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(seekBar.getId(), ConstraintSet.MATCH_CONSTRAINT);

            set.constrainWidth(progress.getId(), dpToPx(45));
            set.constrainHeight(progress.getId(), dpToPx(45));

            set.constrainHeight(songTimeTv.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(songTimeTv.getId(), ConstraintSet.WRAP_CONTENT);

            set.constrainWidth(songSize.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(songSize.getId(), ConstraintSet.WRAP_CONTENT);


            set.connect(coverIv.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, dpToPx(4));
            set.connect(coverIv.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, dpToPx(4));
            set.connect(coverIv.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, dpToPx(4));
            rootView.addView(coverIv);

            set.connect(playBtn.getId(), ConstraintSet.BOTTOM, coverIv.getId(), ConstraintSet.BOTTOM);
            set.connect(playBtn.getId(), ConstraintSet.TOP, coverIv.getId(), ConstraintSet.TOP);
            set.connect(playBtn.getId(), ConstraintSet.RIGHT, coverIv.getId(), ConstraintSet.RIGHT);
            set.connect(playBtn.getId(), ConstraintSet.LEFT, coverIv.getId(), ConstraintSet.LEFT);
            rootView.addView(playBtn);

            set.connect(songFileName.getId(), ConstraintSet.TOP, coverIv.getId(), ConstraintSet.TOP);
            set.connect(songFileName.getId(), ConstraintSet.LEFT, coverIv.getId(), ConstraintSet.RIGHT, dpToPx(8));
            rootView.addView(songFileName);

            set.connect(songArtist.getId(), ConstraintSet.TOP, songFileName.getId(), ConstraintSet.BOTTOM);
            set.connect(songArtist.getId(), ConstraintSet.LEFT, songFileName.getId(), ConstraintSet.LEFT);
            rootView.addView(songArtist);

            set.connect(seekBar.getId(), ConstraintSet.LEFT, coverIv.getId(), ConstraintSet.RIGHT);
            set.connect(seekBar.getId(), ConstraintSet.TOP, songFileName.getId(), ConstraintSet.BOTTOM);
            set.connect(seekBar.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            rootView.addView(seekBar);

            set.connect(progress.getId(), ConstraintSet.BOTTOM, coverIv.getId(), ConstraintSet.BOTTOM);
            set.connect(progress.getId(), ConstraintSet.TOP, coverIv.getId(), ConstraintSet.TOP);
            set.connect(progress.getId(), ConstraintSet.RIGHT, coverIv.getId(), ConstraintSet.RIGHT);
            set.connect(progress.getId(), ConstraintSet.LEFT, coverIv.getId(), ConstraintSet.LEFT);
            rootView.addView(progress);

            set.connect(songTimeTv.getId(), ConstraintSet.LEFT, songFileName.getId(), ConstraintSet.LEFT);
            set.connect(songTimeTv.getId(), ConstraintSet.TOP, songArtist.getId(), ConstraintSet.BOTTOM);
            rootView.addView(songTimeTv);

            set.connect(songSize.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, dpToPx(4));
            set.connect(songSize.getId(), ConstraintSet.TOP, songTimeTv.getId(), ConstraintSet.TOP);
            set.connect(songSize.getId(), ConstraintSet.BOTTOM, songTimeTv.getId(), ConstraintSet.BOTTOM);
            rootView.addView(songSize);


            set.applyTo(rootView);
            rootView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
            getContentBloke().addView(rootView, 0);
        }

        public AppCompatTextView getSongTimeTv() {
            return songTimeTv;
        }

        @Override
        public ImageView getThumbNailImageView() {
            return thumbnail;
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }

        @Override
        public TextView getProgressTextView() {
            return songSize;
        }

        @Override
        public String getTempTextView() {
            return tempText;
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
