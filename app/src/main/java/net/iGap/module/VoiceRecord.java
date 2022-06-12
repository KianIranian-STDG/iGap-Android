/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperString;
import net.iGap.observers.interfaces.OnVoiceRecord;
import net.iGap.proto.ProtoGlobal;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import net.iGap.messenger.theme.Theme;

public class VoiceRecord {

    private MediaRecorder mediaRecorder;
    private String outputFile;
    private boolean canStop = false;
    private boolean state = false;
    private ImageView imgPicRecord;
    private TimerTask timertask;
    private Timer timer;
    private Timer secondTimer;
    private Timer millisecondTimer;
    private int second = 0;
    private int minute = 0;
    private TextView txtTimeRecord;
    private int firstX;
    private boolean cancel = false;
    private int distanceToCancel = 130;
    private TextView txt_slide_to_cancel;
    private String itemTag = "";
    private View layoutAttach;
    private View layoutMic;
    private OnVoiceRecord onVoiceRecordListener;
    private TextView txtMillisecond;
    private int milliSecond = 0;
    private MaterialDesignTextView btnMicLayout;
    private MaterialDesignTextView btnLock;
    private boolean continuePlay;
    private boolean isHandFree = false;
    private int firstY;
    private final Context context;
    private Animation animation1;

    public VoiceRecord(Context context, View layoutMic, View layoutAttach, OnVoiceRecord listener) {
        imgPicRecord = layoutMic.findViewById(R.id.img_pic_record);
        txtTimeRecord = layoutMic.findViewById(R.id.txt_time_record);
        txtTimeRecord.setTextColor(Theme.getColor(Theme.key_default_text));
        txtMillisecond = layoutMic.findViewById(R.id.txt_time_mili_secend);
        txtMillisecond.setTextColor(Theme.getColor(Theme.key_default_text));
        txt_slide_to_cancel = layoutMic.findViewById(R.id.txt_slideto_cancel);
        txt_slide_to_cancel.setTextColor(Theme.getColor(Theme.key_default_text));
        btnMicLayout = layoutMic.findViewById(R.id.lmr_btn_mic_layout);
        btnMicLayout.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(context, R.drawable.shape_floating_button), context, Theme.getColor(Theme.key_theme_color)));
        btnLock = layoutMic.findViewById(R.id.lmr_txt_Lock);
        this.layoutAttach = layoutAttach;
        this.layoutMic = layoutMic;
        this.onVoiceRecordListener = listener;
        this.context = context;
        distanceToCancel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, context.getResources().getDisplayMetrics());
        animation1 = AnimationUtils.loadAnimation(context, R.anim.reverse_bottom_to_top_slow);
        btnLock.startAnimation(animation1);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.reverse_left_to_right);
        txt_slide_to_cancel.startAnimation(animation);
        txt_slide_to_cancel.setOnClickListener(v -> {
            cancel = true;
            reset();
        });

        btnMicLayout.setOnClickListener(v -> {
            if (isHandFree)
                reset();
        });
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            btnLock.setBackgroundResource(R.drawable.circle_white);
        }
    }

    public String getItemTag() {
        return itemTag;
    }

    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }

    private void stopVoiceRecord() {
        if (null != mediaRecorder) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                if (continuePlay) {
                    continuePlay = false;
                    MusicPlayer.playSound();
                    MusicPlayer.playerStatusObservable.setValue(MusicPlayer.PLAY);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    private void startRecording() {

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                MusicPlayer.pauseSound();
                MusicPlayer.playerStatusObservable.setValue(MusicPlayer.PAUSE);
                MusicPlayer.pauseSoundFromIGapCall = true;
                continuePlay = true;
            }
        }

        if (G.onHelperSetAction != null) {
            G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.RECORDING_VOICE);
        }

        outputFile = G.context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/" + "record_" + HelperString.getRandomFileName(3) + ".mp3";

        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(128000);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setOutputFile(outputFile);

            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void startVoiceRecord() {

        canStop = false;
        startRecording();
        timertask = new TimerTask() {
            @Override
            public void run() {
                if (state) {
                    imgPicRecord.post(() -> {
                        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
                            imgPicRecord.setImageResource(R.drawable.circle_white);
                        }
                        state = false;
                    });
                } else {
                    imgPicRecord.post(() -> {
                        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP) {
                            imgPicRecord.setImageResource(R.drawable.circle_red);
                        }
                        state = true;
                    });
                }
            }
        };

        if (timer == null) {
            timer = new Timer();
            timer.schedule(timertask, 100, 300);
        }

        if (secondTimer == null) {

            secondTimer = new Timer();
            secondTimer.schedule(new TimerTask() {

                @Override
                public void run() {

                    second++;
                    if (second >= 60) {
                        minute++;
                        second %= 60;
                    }
                    if (minute >= 60) {
                        minute %= 60;
                    }
                    if (second >= 1) {
                        canStop = true;
                    }

                    txtTimeRecord.post(new Runnable() {

                        @Override
                        public void run() {
                            String s = "";
                            if (minute < 10) {
                                s += "0" + minute;
                            } else {
                                s += minute;
                            }
                            s += ":";
                            if (second < 10) {
                                s += "0" + second;
                            } else {
                                s += second;
                            }

                            txtTimeRecord.setText(s);
                        }
                    });
                }
            }, 1000, 1000);
        }

        if (millisecondTimer == null) {
            millisecondTimer = new Timer();
            millisecondTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    milliSecond++;
                    if (milliSecond >= 99) milliSecond = 1;
                    txtMillisecond.post(new Runnable() {
                        @Override
                        public void run() {
                            if (milliSecond < 10) {
                                txtMillisecond.setText(milliSecond + ":0");
                            } else {
                                txtMillisecond.setText(milliSecond + ":");
                            }
                        }
                    });
                }
            }, 10, 10);
        }
    }

    public void dispatchTouchEvent(MotionEvent event) {

        if (isHandFree) {
            return;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startMoving((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (itemTag.equals("ivVoice")) moving((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (itemTag.equals("ivVoice")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                        }
                    }, 100);
                }
                break;
        }
    }

    private void startMoving(int x, int y) {
        isHandFree = false;
        firstY = y;
        firstX = x;
        cancel = false;
    }

    private void moving(int x, int y) {
        int MoveY = Math.abs(firstY - y);
        int moveX = Math.abs(firstX - x);

        if (MoveY > 100) {
            lockVoice();
            return;
        }

        if (moveX > distanceToCancel) {
            cancel = true;
            reset();
        } else {
            txt_slide_to_cancel.setAlpha(((float) (distanceToCancel - moveX) / distanceToCancel));
            txt_slide_to_cancel.setPadding(0, 0, moveX, 0);
        }
    }

    private void lockVoice() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.reverse_bottom_to_top);
        btnLock.setAnimation(animation);
        btnLock.setText(R.string.icon_lock);
        txt_slide_to_cancel.setPadding(0, 0, 50, 0);
        txt_slide_to_cancel.setAlpha(1);
        txt_slide_to_cancel.setText(R.string.cancel);
        txt_slide_to_cancel.clearAnimation();
        txt_slide_to_cancel.setTextColor(Theme.getColor(Theme.key_red));
        btnMicLayout.setText(R.string.icon_send);
        isHandFree = true;
    }

    private void reset() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.reverse_left_to_right);
        btnLock.setAnimation(animation1);
        btnLock.setText(R.string.icon_unlock);
        txt_slide_to_cancel.setPadding(0, 0, 0, 0);
        txt_slide_to_cancel.setText(R.string.slide_to_cancel_en);
        txt_slide_to_cancel.startAnimation(animation);
        txt_slide_to_cancel.setAlpha(1);
        txt_slide_to_cancel.setTextColor(Theme.getColor(Theme.key_icon));
        btnMicLayout.setText(R.string.icon_microphone);
        itemTag = "";
        layoutAttach.setVisibility(View.VISIBLE);
        layoutMic.setVisibility(View.GONE);
        isHandFree = false;

        if (timertask != null) {
            timertask.cancel();
            timertask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (secondTimer != null) {
            secondTimer.cancel();
            secondTimer.purge();
            secondTimer = null;
        }
        if (millisecondTimer != null) {
            millisecondTimer.cancel();
            millisecondTimer.purge();
            millisecondTimer = null;
        }

        second = 0;
        minute = 0;
        txtTimeRecord.setText("00:00");

        if (canStop) {
            stopVoiceRecord();
        }
        if (cancel) {
            if (onVoiceRecordListener != null) {
                onVoiceRecordListener.onVoiceRecordCancel();
            }
        } else {
            if (canStop) {
                try {
                    if (onVoiceRecordListener != null) {
                        onVoiceRecordListener.onVoiceRecordDone(outputFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (onVoiceRecordListener != null) {
                    onVoiceRecordListener.onVoiceRecordCancel();
                }
            }
        }
    }
}
