package com.iGap.module;

import android.content.Context;
import android.media.MediaRecorder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interface_package.OnVoiceRecord;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by android3 on 9/26/2016.
 */
public class VoiceRecord {

    private MediaRecorder mediaRecorder;
    private String outputFile;
    private boolean canStop = false;
    private boolean state = false;
    private ImageView imgPicRecord;
    private TimerTask timertask;
    private Timer timer;
    private Timer secendTimer;
    private int secend = 0;
    private int minute = 0;
    private TextView txtTimeRecord;
    private int leftPading;
    private int Allmoving = 0;
    private LinearLayout layout3;
    private int lastX;
    private boolean cansel = false;
    private int DistanceToCancel = 130;
    private TextView txt_slide_to_cancel;
    private String itemTag = "";
    private View layoutAttach;
    private View layoutMic;
    private OnVoiceRecord onVoiceRecordListener;

    private Context context;

    public VoiceRecord(Context context, View layoutMic, View layoutAttach, OnVoiceRecord listener) {

        this.context = context;

        imgPicRecord = (ImageView) layoutMic.findViewById(R.id.img_pic_record);
        txtTimeRecord = (TextView) layoutMic.findViewById(R.id.txt_time_record);
        layout3 = (LinearLayout) layoutMic.findViewById(R.id.layout3);
        txt_slide_to_cancel = (TextView) layoutMic.findViewById(R.id.txt_slideto_cancel);

        this.layoutAttach = layoutAttach;
        this.layoutMic = layoutMic;
        this.onVoiceRecordListener = listener;
    }


    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }


    public void stopVoiceRecord() {
        if (null != mediaRecorder) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (IllegalStateException e) {
            }
        }
    }


    private void startRecording() {
        final long currentTime = System.currentTimeMillis();
        outputFile = G.DIR_AUDIOS + "/" + currentTime + ".mp3";

        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

                    imgPicRecord.post(new Runnable() {

                        @Override
                        public void run() {
                            imgPicRecord.setImageResource(R.mipmap.circle_white);
                            state = false;
                        }
                    });

                } else {
                    imgPicRecord.post(new Runnable() {

                        @Override
                        public void run() {
                            imgPicRecord.setImageResource(R.mipmap.circle_red);
                            state = true;
                        }
                    });
                }
            }
        };

        if (timer == null) {
            timer = new Timer();
            timer.schedule(timertask, 100, 300);
        }

        if (secendTimer == null) {

            secendTimer = new Timer();
            secendTimer.schedule(new TimerTask() {

                @Override
                public void run() {

                    secend++;
                    if (secend >= 60) {
                        minute++;
                        secend %= 60;
                    }
                    if (minute >= 60) {
                        minute %= 60;
                    }
                    if (secend >= 1) {
                        canStop = true;
                    }

                    txtTimeRecord.post(new Runnable() {

                        @Override
                        public void run() {
                            String s = "";
                            if (minute < 10)
                                s += "0" + minute;
                            else
                                s += minute;
                            s += ":";
                            if (secend < 10)
                                s += "0" + secend;
                            else
                                s += secend;

                            txtTimeRecord.setText(s);
                        }
                    });
                }
            }, 1000, 1000);
        }
    }


    public void dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startMoving((int) event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                if (itemTag.equals("ivVoice"))
                    moving((int) event.getX());
                break;
            case MotionEvent.ACTION_UP:
                if (itemTag.equals("ivVoice"))
                    reset();
                break;
        }

    }


    private void startMoving(int x) {
        leftPading = layout3.getPaddingRight();
        lastX = x;
        cansel = false;
    }


    private void moving(int x) {
        int i = lastX - x;

        if (i > 0 || Allmoving > 0) {
            Allmoving += i;
            txt_slide_to_cancel.setAlpha(((float) (DistanceToCancel - Allmoving) / DistanceToCancel));
            layout3.setPadding(0, 0, layout3.getPaddingRight() + i, 0);
            lastX = x;

            if (Allmoving >= DistanceToCancel) {
                cansel = true;
                reset();
            }
        }
    }


    private void reset() {
        layout3.setPadding(0, 0, leftPading, 0);
        txt_slide_to_cancel.setAlpha(1);
        Allmoving = 0;
        itemTag = "";
        layoutAttach.setVisibility(View.VISIBLE);
        layoutMic.setVisibility(View.GONE);

        if (timertask != null) {
            timertask.cancel();
            timertask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (secendTimer != null) {
            secendTimer.cancel();
            secendTimer.purge();
            secendTimer = null;
        }

        secend = 0;
        minute = 0;
        txtTimeRecord.setText("00:00");

        if (canStop) {
            stopVoiceRecord();
        }

        if (cansel) {
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
            }

        }
    }


}
