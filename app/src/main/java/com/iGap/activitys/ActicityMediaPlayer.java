package com.iGap.activitys;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.MusicPlayer;
import com.iGap.module.OnComplete;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by android3 on 10/2/2016.
 */
public class ActicityMediaPlayer extends ActivityEnhanced {

    private TextView txt_MusicName;
    private TextView txt_MusicPlace;
    private TextView txt_MusicTime;
    private TextView txt_Timer;
    private TextView txt_musicInfo;
    private SeekBar musikSeekbar;
    private ImageView img_MusicImage;
    private Button btnPlay;

    private Timer mTimer, mTimeSecend;
    private long time = 0;
    private double amoungToupdate;

    private String str_info = "";

    OnComplete onComplete;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        if (MusicPlayer.mp == null) {
            finish();
            NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancelAll();
            return;
        }

        initComponent();

        getMusicInfo();

        onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                if (messageOne.equals("play")) {
                    btnPlay.setText(R.string.md_play_rounded_button);
                    stopTimer();
                } else if (messageOne.equals("pause")) {
                    btnPlay.setText(R.string.md_round_pause_button);
                    updateProgress();
                } else if (messageOne.equals("update")) {
                    updateUi();
                }

            }
        };

    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicPlayer.onComplete = null;
        MusicPlayer.updateNotification();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.onComplete = onComplete;
        updateUi();

        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        if (MusicPlayer.mp != null)
            if (MusicPlayer.mp.isPlaying())
                updateProgress();
    }

    //*****************************************************************************************

    private void getMusicInfo() {

        str_info = "";

        MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
        Uri uri = (Uri) Uri.fromFile(new File(MusicPlayer.musicPath));
        mediaMetadataRetriever.setDataSource(ActicityMediaPlayer.this, uri);

        String title = (String) mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

        if (title != null) {
            str_info += title + "\n";
        }

        String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        if (albumName != null) {
            str_info += albumName + "\n";
        }

        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (artist != null) {
            str_info += artist + "\n";
        }

        txt_musicInfo.setText(str_info);

//        byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
//        if (data != null) {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            img_MusicImage.setImageBitmap(bitmap);
//        }

    }

    private void initComponent() {

        txt_MusicName = (TextView) findViewById(R.id.ml_txt_music_name);
        txt_MusicPlace = (TextView) findViewById(R.id.ml_txt_music_place);
        txt_MusicTime = (TextView) findViewById(R.id.ml_txt_music_time);
        txt_Timer = (TextView) findViewById(R.id.ml_txt_timer);

        if (MusicPlayer.mp != null)
            txt_Timer.setText(MusicPlayer.milliSecondsToTimer(MusicPlayer.mp.getCurrentPosition()));


        txt_musicInfo = (TextView) findViewById(R.id.ml_txt_music_info);
        img_MusicImage = (ImageView) findViewById(R.id.ml_img_music_picture);
        if (MusicPlayer.mediaThumpnail != null)
            img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
        else img_MusicImage.setImageResource(R.mipmap.igap_music_large);


        musikSeekbar = (SeekBar) findViewById(R.id.ml_seekBar1);

        if (MusicPlayer.mp != null) {
            musikSeekbar.setProgress((int) ((MusicPlayer.mp.getCurrentPosition() * 100) / MusicPlayer.mp.getDuration()));
        }

        musikSeekbar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                try {
                    if (MusicPlayer.mp != null) {
                        MusicPlayer.mp.seekTo((int) (musikSeekbar.getProgress() * amoungToupdate));
                        time = MusicPlayer.mp.getCurrentPosition();
                        txt_Timer.setText(MusicPlayer.milliSecondsToTimer(time));
                    }
                } catch (IllegalStateException e) {
                    Log.e("wer", e.toString());
                }

                return false;
            }
        });

        Button btnBack = (Button) findViewById(R.id.ml_btn_back);
        btnBack.setTypeface(G.flaticon);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button btnPrevious = (Button) findViewById(R.id.ml_btn_Previous_music);
        btnPrevious.setTypeface(G.flaticon);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.previousMusic();
            }
        });

        btnPlay = (Button) findViewById(R.id.ml_btn_play_music);
        btnPlay.setTypeface(G.flaticon);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.playAndPause();
            }
        });

        Button btnNextMusic = (Button) findViewById(R.id.ml_btn_forward_music);
        btnNextMusic.setTypeface(G.flaticon);
        btnNextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.nextMusic();
            }
        });


    }

    private void updateUi() {
        txt_MusicTime.setText(MusicPlayer.txt_music_time.getText());
        txt_MusicPlace.setText(MusicPlayer.roomName);
        txt_MusicName.setText(MusicPlayer.musicName);

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                btnPlay.setText(getString(R.string.md_round_pause_button));
            } else {
                btnPlay.setText(getString(R.string.md_play_rounded_button));
            }

            if (MusicPlayer.mediaThumpnail != null) {
                img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);

            } else {
                img_MusicImage.setImageResource(R.mipmap.igap_music_large);
            }

            getMusicInfo();

        }


    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeSecend != null) {
            mTimeSecend.cancel();
            mTimeSecend = null;
        }
    }

    private void updateProgress() {

        stopTimer();

        double duration = MusicPlayer.mp.getDuration();
        amoungToupdate = duration / 100;
        musikSeekbar.setProgress((int) (MusicPlayer.mp.getCurrentPosition() / amoungToupdate));
        time = MusicPlayer.mp.getCurrentPosition();
        mTimeSecend = new Timer();
        mTimeSecend.schedule(new TimerTask() {

            @Override
            public void run() {

                runOnUiThread(new Runnable() {

                    public void run() {
                        txt_Timer.setText(MusicPlayer.milliSecondsToTimer(time));
                    }
                });
                time += 1000;
            }
        }, 0, 1000);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (musikSeekbar.getProgress() < 100) {
                            int p = musikSeekbar.getProgress();
                            p += 1;
                            musikSeekbar.setProgress(p);
                        } else {
                            stopTimer();

                        }
                    }
                });
            }

            ;
        }, 0, (int) amoungToupdate);

    }


}
