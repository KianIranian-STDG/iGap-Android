package com.iGap.module;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActicityMediaPlayer;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoomMessage;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by android3 on 10/2/2016.
 */
public class MusicPlayer {


    private static LinearLayout layoutTripMusic;
    private static Button btnStopMusic;
    private static Button btnPlayMusic;
    private static Button btnCloseMusic;
    public static TextView txt_music_time;
    private static TextView txt_music_name;
    public static String roomName;
    public static String musicPath;
    public static String musicName;
    public static long roomId = 0;
    public static Bitmap mediaThumpnail = null;
    public static MediaPlayer mp;
    private static RemoteViews remoteViews;
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static boolean isPause = false;
    public static OnComplete onComplete = null;
    private static RealmList<RealmRoomMessage> mediaList;
    private static int selectedMedia = 0;


    public MusicPlayer(LinearLayout layoutTripMusic) {
        this.layoutTripMusic = layoutTripMusic;

        remoteViews = new RemoteViews(G.context.getPackageName(), R.layout.music_layout_notification);
        notificationManager = (NotificationManager) G.context.getSystemService(G.context.NOTIFICATION_SERVICE);

        initLayoutTripMusic();
    }

    private void initLayoutTripMusic() {

        layoutTripMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(G.context, ActicityMediaPlayer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);
            }
        });

        txt_music_time = (TextView) layoutTripMusic.findViewById(R.id.mls_txt_music_time);
        txt_music_name = (TextView) layoutTripMusic.findViewById(R.id.mls_txt_music_name);

        btnStopMusic = (Button) layoutTripMusic.findViewById(R.id.mls_btn_stop);
        btnStopMusic.setTypeface(G.fontawesome);
        btnStopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSound();
            }
        });

        btnPlayMusic = (Button) layoutTripMusic.findViewById(R.id.mls_btn_play_music);
        btnPlayMusic.setTypeface(G.flaticon);
        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAndPause();
            }
        });

        btnCloseMusic = (Button) layoutTripMusic.findViewById(R.id.mls_btn_close);
        btnCloseMusic.setTypeface(G.flaticon);
        btnCloseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLayoutMediaPlayer();
            }
        });

    }

    //**************************************************************************

    public static void playAndPause() {

        if (mp != null) {
            if (mp.isPlaying())
                pauseSound();
            else
                playSound();
        } else {
            closeLayoutMediaPlayer();
        }


    }

    private static void pauseSound() {
        try {
            btnPlayMusic.setText(G.context.getString(R.string.md_play_rounded_button));
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play);
            if (onComplete == null) {
                notificationManager.notify(0, notification);
            } else {
                onComplete.complete(true, "play", "");
            }
        } catch (Exception e) {
        }

        isPause = true;
        mp.pause();

    }

    private static void playSound() {
        try {
            btnPlayMusic.setText(G.context.getString(R.string.md_round_pause_button));
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause);
            if (onComplete == null) {
                notificationManager.notify(0, notification);
            } else {
                onComplete.complete(true, "pause", "");
            }
        } catch (Exception e) {
        }

        if (isPause) {
            if (mp != null) {
                mp.start();
                isPause = false;
            }
        } else {
            startPlayer(musicPath, roomName, roomId, false);
        }

    }

    public static void stopSound() {

        try {
            btnPlayMusic.setText(G.context.getString(R.string.md_play_rounded_button));
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play);
            if (onComplete == null) {
                notificationManager.notify(0, notification);
            } else {
                onComplete.complete(true, "play", "");
            }
        } catch (Exception e) {
        }

        if (mp != null) {
            mp.stop();
        }

    }

    public static void nextMusic() {
        Log.e("ddd", selectedMedia + "   selected media");

        if (selectedMedia < mediaList.size()) {
            startPlayer(mediaList.get(selectedMedia).getAttachment().getLocalFilePath(), roomName, roomId, false);

            selectedMedia++;

            if (onComplete != null)
                onComplete.complete(true, "update", "");
        }
    }

    public static void previousMusic() {

        Log.e("ddd", selectedMedia + "   selected media");

        if (selectedMedia > 1) {
            selectedMedia--;
            startPlayer(mediaList.get(selectedMedia - 1).getAttachment().getLocalFilePath(), roomName, roomId, false);

            if (onComplete != null)
                onComplete.complete(true, "update", "");
        }
    }

    private static void closeLayoutMediaPlayer() {
        if (layoutTripMusic != null)
            layoutTripMusic.setVisibility(View.GONE);

        stopSound();

        if (mp != null) {
            mp.release();
            mp = null;
        }

        NotificationManager notifManager = (NotificationManager) G.context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

    }

    public static void startPlayer(String musicPath, String roomName, long roomId, boolean updateList) {

        MusicPlayer.musicPath = musicPath;
        MusicPlayer.roomName = roomName;
        mediaThumpnail = null;
        MusicPlayer.roomId = roomId;

        if (layoutTripMusic.getVisibility() == View.GONE)
            layoutTripMusic.setVisibility(View.VISIBLE);

        if (mp != null) {
            mp.stop();
            mp.reset();

            try {
                mp.setDataSource(musicPath);
                mp.prepare();
                mp.start();

                try {
                    btnPlayMusic.setText(G.context.getString(R.string.md_round_pause_button));

                    remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause);
                    if (onComplete == null) {
                        notificationManager.notify(0, notification);
                    } else {
                        onComplete.complete(true, "pause", "");
                    }
                } catch (Exception e) {

                    Log.e("ddd", "aaaaaaaaa      " + e.toString());
                }

                txt_music_time.setText(milliSecondsToTimer((long) mp.getDuration()));

                musicName = musicPath.substring(musicPath.lastIndexOf("/") + 1);
                txt_music_name.setText(musicName);

                updateNotification();

            } catch (Exception e) {
            }

        } else {

            mp = new MediaPlayer();
            try {
                mp.setDataSource(musicPath);
                mp.prepare();
                mp.start();
                txt_music_time.setText(milliSecondsToTimer((long) mp.getDuration()));
                try {
                    btnPlayMusic.setText(G.context.getString(R.string.md_round_pause_button));
                    remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause);
                    if (onComplete == null) {
                        notificationManager.notify(0, notification);
                    } else {
                        onComplete.complete(true, "pause", "");
                    }
                } catch (Exception e) {
                    Log.e("ddd", "aaaaaaaaa      " + e.toString());
                }

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopSound();
                    }
                });

                musicName = musicPath.substring(musicPath.lastIndexOf("/") + 1);
                txt_music_name.setText(musicName);
                updateNotification();

            } catch (Exception e) {

            }
        }

        if (updateList)
            fillMediaList();

    }

    public static String milliSecondsToTimer(long milliseconds) {

        if (milliseconds == -1)
            return " ";

        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static void updateNotification() {

        PendingIntent pi = PendingIntent.getActivity(G.context, 10, new Intent(G.context, ActicityMediaPlayer.class), PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setTextViewText(R.id.mln_txt_music_name, MusicPlayer.musicName);
        remoteViews.setTextViewText(R.id.mln_txt_music_place, MusicPlayer.roomName);

        if (mp != null)
            if (mp.isPlaying())
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause);
            else {
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play);
            }

        try {
            MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();
            Uri uri = (Uri) Uri.fromFile(new File(musicPath));

            mediaMetadataRetriever.setDataSource(G.context, uri);
            byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
            if (data != null) {
                mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                remoteViews.setImageViewBitmap(R.id.mln_img_picture_music, mediaThumpnail);
            } else {
                remoteViews.setImageViewResource(R.id.mln_img_picture_music, R.mipmap.igap_music);
            }
        } catch (Exception e) {

        }

        Intent intentPrevious = new Intent(G.context, customButtonListener.class);
        intentPrevious.putExtra("mode", "previous");
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(G.context, 1, intentPrevious, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_Previous_music, pendingIntentPrevious);

        Intent intentPlayPause = new Intent(G.context, customButtonListener.class);
        intentPlayPause.putExtra("mode", "play");
        PendingIntent pendingIntentPlayPause = PendingIntent.getBroadcast(G.context, 2, intentPlayPause, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_play_music, pendingIntentPlayPause);

        Intent intentforward = new Intent(G.context, customButtonListener.class);
        intentforward.putExtra("mode", "forward");
        PendingIntent pendingIntentforward = PendingIntent.getBroadcast(G.context, 3, intentforward, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_forward_music, pendingIntentforward);

        Intent intentClose = new Intent(G.context, customButtonListener.class);
        intentClose.putExtra("mode", "close");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(G.context, 4, intentClose, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_close, pendingIntentClose);

        notification = new NotificationCompat.Builder(G.context.getApplicationContext())
                .setTicker("music")
                .setSmallIcon(R.mipmap.j_audio)
                .setContentTitle(musicName)
                //  .setContentText(place)
                .setContent(remoteViews)
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();

        try {
            notificationManager.notify(0, notification);
        } catch (RuntimeException e) {
        }

    }

    public static class customButtonListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String str = intent.getExtras().getString("mode");

            if (str.equals("previous")) {
                previousMusic();
            } else if (str.equals("play")) {
                playAndPause();
            } else if (str.equals("forward")) {
                nextMusic();
            } else if (str.equals("close")) {
                closeLayoutMediaPlayer();
            }

        }
    }


    public static void fillMediaList() {

        mediaList = new RealmList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", roomId).findAll();

        if (chatHistories != null) {
            for (RealmChatHistory chatHistory : chatHistories) {
                if (chatHistory.getRoomMessage().getMessageType().equals("VOICE") || chatHistory.getRoomMessage().getMessageType().equals("AUDIO")
                        || chatHistory.getRoomMessage().getMessageType().equals("AUDIO_TEXT")) {
                    mediaList.add(chatHistory.getRoomMessage());

                    String tmpPath = chatHistory.getRoomMessage().getAttachment().getLocalFilePath();
                    if (tmpPath != null)
                        if (tmpPath.equals(musicPath))
                            selectedMedia = mediaList.size();
                }
            }

        }

        realm.close();

    }

}
