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

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentMediaPlayer;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperLog;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Downloader;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnAudioFocusChangeListener;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.context;

public class MusicPlayer extends Service implements AudioManager.OnAudioFocusChangeListener, OnAudioFocusChangeListener {

    public static final int PLAY = 0;
    public static final int PAUSE = 1;
    public static final int RESUME = 2;
    public static final int STOP = 3;

    public static final int notificationId = 19;
    public static final int limitMediaList = 50;
    public static final String musicChannelId = "music_channel";
    public static boolean canDoAction = true;
    public static String repeatMode = RepeatMode.noRepeat.toString();
    public static boolean isShuffelOn = false;
    public static TextView txt_music_time;
    public static TextView txt_music_time_counter;
    //    private static Bitmap orginalWallPaper = null;
    //    private static boolean isGetOrginalWallpaper=false;
    public static String musicTime = "";
    public static String roomName;
    public static String musicPath;
    public static String musicName;
    public static String musicInfo = "";
    public static String musicInfoTitle = "";
    public static long roomId = 0;
    public static Bitmap mediaThumpnail = null;
    public static MediaPlayer mp;
    public static OnComplete onComplete = null;
    public static OnComplete onCompleteChat = null;
    public static MutableLiveData<Boolean> playerStateChangeListener = new MutableLiveData<>();
    public static MutableLiveData<Integer> playerStatusObservable = new MutableLiveData<>(STOP);
    public static boolean isShowMediaPlayer = false;
    public static int musicProgress = 0;
    public static boolean isPause = false;
    public static ArrayList<MessageObject> mediaList;
    public static String strTimer = "";
    public static String messageId = "";
    public static boolean isNearDistance = false;
    public static int currentDuration = 0;
    public static boolean isVoice = false;
    public static boolean pauseSoundFromIGapCall = false;
    public static boolean inChangeStreamType = false;
    public static boolean pauseSoundFromCall = false;
    public static boolean isMusicPlyerEnable = false;
    public static boolean playNextMusic = false;
    public static String STARTFOREGROUND_ACTION = "STARTFOREGROUND_ACTION";
    public static String STOPFOREGROUND_ACTION = "STOPFOREGROUND_ACTION";
    public static boolean downloadNewItem = false;
    public static LinearLayout mainLayout;
    public static LinearLayout chatLayout;
    public static LinearLayout shearedMediaLayout;
    public static UpdateName updateName;
    private static SensorManager mSensorManager;
    private static Sensor mProximity;
    private static SensorEventListener sensorEventListener;
    private static MediaSessionCompat mSession;
    private static int latestAudioFocusState;
    private static LinearLayout layoutTripMusic;
    private static TextView btnPlayMusic;
    private static TextView btnCloseMusic;
    private static TextView txt_music_name;
    private static TextView txt_music_info;
//    private static RemoteViews remoteViews;
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static int selectedMedia = 0;
    private static Timer mTimer, mTimeSecend;
    private static long time = 0;
    private static double amoungToupdate;
    private static int stateHedset = 0;
    private static boolean pauseOnAudioFocusChange;
    private static HeadsetPluginReciver headsetPluginReciver;
    private static BluetoothCallbacks bluetoothCallbacks;
    private static RemoteControlClient remoteControlClient;
    private static ComponentName remoteComponentName;
    private static boolean isRegisterSensor = false;

    public static void setMusicPlayer(LinearLayout layoutTripMusic) {
        if (layoutTripMusic != null) {
            layoutTripMusic.setVisibility(View.GONE);
        }

        initLayoutTripMusic(layoutTripMusic);

        getAttribute();
    }

    private static NotificationManager getNotificationManager() {

        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    public static void repeatClick() {

        String str = "";
        if (repeatMode.equals(RepeatMode.noRepeat.toString())) {
            str = RepeatMode.repeatAll.toString();
        } else if (repeatMode.equals(RepeatMode.repeatAll.toString())) {
            str = RepeatMode.oneRpeat.toString();
        } else if (repeatMode.equals(RepeatMode.oneRpeat.toString())) {
            str = RepeatMode.noRepeat.toString();
        }

        repeatMode = str;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicSetting", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("RepeatMode", str);
        editor.apply();

        if (onComplete != null) {
            onComplete.complete(true, "RepeatMode", "");
        }
    }

    public static void shuffleClick() {

        isShuffelOn = !isShuffelOn;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicSetting", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Shuffel", isShuffelOn);
        editor.apply();
        if (onComplete != null) {
            onComplete.complete(true, "Shuffel", "");
        }
    }

    public static void initLayoutTripMusic(LinearLayout layout) {

        MusicPlayer.layoutTripMusic = layout;

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVoice) {
                    Intent intent = new Intent(context, ActivityMain.class);
                    intent.putExtra(ActivityMain.openMediaPlyer, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            }
        });

        txt_music_time = layout.findViewById(R.id.mls_txt_music_time);

        txt_music_time_counter = layout.findViewById(R.id.mls_txt_music_time_counter);
        txt_music_name = layout.findViewById(R.id.mls_txt_music_name);
        txt_music_info = layout.findViewById(R.id.mls_txt_music_info);

        btnPlayMusic = layout.findViewById(R.id.mls_btn_play_music);
        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAndPause();
            }
        });

        btnCloseMusic = layout.findViewById(R.id.mls_btn_close);
        btnCloseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLayoutMediaPlayer();
            }
        });

        if (MusicPlayer.mp != null) {

            getMusicArtist();

            layout.setVisibility(View.VISIBLE);
            txt_music_name.setText(MusicPlayer.musicName);
            txt_music_info.setText(MusicPlayer.musicInfoTitle);
            txt_music_time.setText(musicTime);

            if (MusicPlayer.mp.isPlaying()) {
                btnPlayMusic.setText(context.getString(R.string.icon_pause));
            } else {
                btnPlayMusic.setText(context.getString(R.string.icon_play));
            }
        }

        if (HelperCalander.isPersianUnicode) {
            txt_music_time.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_music_time.getText().toString()));
        }

        if (MusicPlayer.mp != null) {
            time = MusicPlayer.mp.getCurrentPosition() - 1;
            if (time >= 0) {
                updatePlayerTime();
            }
        }
    }

    public static void playAndPause() {

        if (mp != null) {
            if (mp.isPlaying()) {
                pauseSound();
                MusicPlayer.playerStatusObservable.setValue(PAUSE);
            } else {
                playSound();
                MusicPlayer.playerStatusObservable.setValue(PLAY);
            }
        } else {
            playSound();
        }
        EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.MEDIA_PLAYER_STATE_CHANGED, true);
    }

    public static void pauseSound() {

        if (!isVoice) {
            try {
                getNotificationManager().notify(notificationId, notification);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        try {
            stopTimer();

            if (btnPlayMusic != null) {
                btnPlayMusic.setText(context.getString(R.string.icon_play));
            }

            if (!isShowMediaPlayer) {
                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "play", "");
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "play", "");
            }
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }

        try {
            if (mp != null && mp.isPlaying()) {
                mp.pause();
                isPause = true;
            }
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }
        updateFastAdapter(MusicPlayer.messageId);
    }

    private static void updateFastAdapter(String messageId) {

        if (FragmentMediaPlayer.fastItemAdapter != null)
            FragmentMediaPlayer.fastItemAdapter.notifyAdapterItemChanged(FragmentMediaPlayer.fastItemAdapter.getPosition(Long.parseLong(messageId)));

    }

    public static void playSound() {

        if (mp == null) {
            return;
        }

        if (mp.isPlaying()) {
            return;
        }


        if (G.onAudioFocusChangeListener != null) {
            G.onAudioFocusChangeListener.onAudioFocusChangeListener(AudioManager.AUDIOFOCUS_GAIN);
        }

        if (!isVoice) {
            try {
                getNotificationManager().notify(notificationId, notification);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        try {

            if (btnPlayMusic != null) {
                btnPlayMusic.setText(context.getString(R.string.icon_pause));
            }

            if (!isShowMediaPlayer) {

                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "pause", "");
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "pause", "");
            }
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }

        try {
            if (mp != null && isPause) {
                mp.start();
                isPause = false;
                updateProgress();
            } else {
                startPlayer(musicName, musicPath, roomName, roomId, false, MusicPlayer.messageId);
            }
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }
        updateFastAdapter(MusicPlayer.messageId);
    }

    public static void stopSound() {

        String zeroTime = "0:00";

        if (HelperCalander.isPersianUnicode) {
            zeroTime = HelperCalander.convertToUnicodeFarsiNumber(zeroTime);
        }

        if (txt_music_time_counter != null) {
            txt_music_time_counter.setText(zeroTime);
        }

        try {

            if (btnPlayMusic != null) {
                btnPlayMusic.setText(context.getString(R.string.icon_play));
            }

            musicProgress = 0;

            if (!isShowMediaPlayer) {

                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "play", "");
                    onCompleteChat.complete(false, "updateTime", zeroTime);
                } else {
                    if (FragmentChat.onMusicListener != null) {
                        FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, "");
                    }
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "play", "");
                onComplete.complete(true, "updateTime", zeroTime);
            }
            stopTimer();
            MusicPlayer.playerStatusObservable.setValue(STOP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mp != null) {
            mp.stop();
            updateFastAdapter(MusicPlayer.messageId);
        }
    }

    public static void nextMusic() {
        try {
            String beforeMessageId = MusicPlayer.messageId;
            selectedMedia--;
            if (selectedMedia < 0) {

                if (isVoice) { // avoid from return to first voice
                    if (btnPlayMusic != null) {
                        btnPlayMusic.setText(context.getString(R.string.icon_play));
                    }
                    stopSound();
                    closeLayoutMediaPlayer();
                    return;
                }

                selectedMedia = mediaList.size() - 1;
            }
            MessageObject messageObject = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));
            boolean _continue = true;
            while (_continue) {
                if (!messageObject.attachment.isFileExistsOnLocal(messageObject)) {
                    selectedMedia--;
                    if (selectedMedia < 0) {
                        selectedMedia = mediaList.size() - 1;
                    }
                    messageObject = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));
                } else {
                    _continue = false;
                }
            }
            startPlayer(messageObject.attachment.name, messageObject.attachment.filePath, roomName, roomId, false, String.valueOf(mediaList.get(selectedMedia).id));
            if (FragmentChat.onMusicListener != null) {
                FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, beforeMessageId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //**************************************************************************

    private static void nextRandomMusic() {
        try {
            Random r = new Random();
            selectedMedia = r.nextInt(mediaList.size() - 1);
            nextMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void previousMusic() {

        try {
            if (MusicPlayer.mp != null) {

                if (MusicPlayer.mp.getCurrentPosition() > 10000) {

                    musicProgress = 0;

                    MusicPlayer.mp.seekTo(0);
                    time = MusicPlayer.mp.getCurrentPosition();
                    updatePlayerTime();

                    return;
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        try {
            String beforeMessageId = MusicPlayer.messageId;
            selectedMedia++;

            if (selectedMedia >= mediaList.size()) {
                selectedMedia = 0;
            }
            MessageObject messageObject = null;
            boolean _continue = true;
            while (_continue) {
                messageObject = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));
                if (!messageObject.getAttachment().isFileExistsOnLocal(messageObject)) {
                    selectedMedia++;
                    if (selectedMedia >= mediaList.size()) {
                        if (isVoice) { // avoid from return to first voice
                            if (btnPlayMusic != null) {
                                btnPlayMusic.setText(context.getString(R.string.icon_play));
                            }
                            stopSound();
                            closeLayoutMediaPlayer();
                            return;
                        }
                        selectedMedia = 0;
                    }
                } else {
                    _continue = false;
                }
            }
            startPlayer(messageObject.attachment.name, messageObject.attachment.filePath, roomName, roomId, false, mediaList.get(selectedMedia).id + "");
            if (FragmentChat.onMusicListener != null) {
                FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, beforeMessageId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void closeLayoutMediaPlayer() {

        try {

            isMusicPlyerEnable = false;

            if (layoutTripMusic != null) {
                layoutTripMusic.setVisibility(View.GONE);
                //playerStateChangeListener.setValue(false);
            }

            if (onComplete != null) {
                onComplete.complete(true, "finish", "");
            }

            if (onCompleteChat != null) {
                onCompleteChat.complete(true, "pause", "");
            }

            stopSound();

            if (mp != null) {
                mp.release();
                mp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // clearWallpaperLockScrean();

        setMedaiInfoOnLockScreen(true);

        try {

            Intent intent = new Intent(context, MusicPlayer.class);
            intent.putExtra("ACTION", STOPFOREGROUND_ACTION);
            context.startService(intent);
        } catch (RuntimeException e) {

            try {
                getNotificationManager().cancel(notificationId);
            } catch (NullPointerException e1) {

            }
        }

        if (MusicPlayer.chatLayout != null) {
            MusicPlayer.chatLayout.setVisibility(View.GONE);
        }

        if (MusicPlayer.mainLayout != null) {
            MusicPlayer.mainLayout.setVisibility(View.GONE);
        }

        if (MusicPlayer.shearedMediaLayout != null) {
            MusicPlayer.shearedMediaLayout.setVisibility(View.GONE);
        }

        if (MusicPlayer.layoutTripMusic != null) {
            MusicPlayer.layoutTripMusic.setVisibility(View.GONE);
        }
        MusicPlayer.playerStateChangeListener.setValue(false);
        EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.MEDIA_PLAYER_STATE_CHANGED, false);
    }

    private static String getMusicName(long messageId, String name) {
        try {
            if (isVoice) {
                String voiceName = "";
                RealmRoomMessage realmRoomMessage = DbManager.getInstance().doRealmTask(realm -> {
                    return RealmRoomMessage.getFinalMessage(realm.where(RealmRoomMessage.class).equalTo("messageId", messageId).findFirst());
                });
                if (realmRoomMessage != null) {
                    if (realmRoomMessage.getUserId() != 0) {
                        if (realmRoomMessage.getUserId() == AccountManager.getInstance().getCurrentUser().getId()) {
                            voiceName = G.context.getResources().getString(R.string.you);
                        } else {
                            voiceName = RealmRegisteredInfo.getNameWithId(realmRoomMessage.getUserId());
                        }

                    } else if (realmRoomMessage.getAuthorRoomId() != 0) {
                        voiceName = RealmRoom.detectTitle(realmRoomMessage.getAuthorRoomId());
                    }
                    return G.fragmentActivity.getResources().getString(R.string.recorded_by) + " " + voiceName;
                }
            }

            if (name == null) {
                name = "";
            }

            if (name.length() > 0) {
                return musicName = name;
            } else if (musicPath != null && musicPath.length() > 0) {
                return musicPath.substring(musicPath.lastIndexOf("/") + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void startPlayer(final String name, String musicPath, String roomName, long roomId, final boolean updateList, final String messageID) {

        if (!inChangeStreamType) {
            isVoice = false;
            isPause = false;


            if (messageID != null && messageID.length() > 0) {

                try {
                    RealmRoomMessage realmRoomMessage = DbManager.getInstance().doRealmTask(realm -> {
                        return RealmRoomMessage.getFinalMessage(realm.where(RealmRoomMessage.class).equalTo("messageId", Long.parseLong(messageID)).findFirst());
                    });
                    if (realmRoomMessage != null) {
                        String type = realmRoomMessage.getMessageType().toString();

                        if (type.equals("VOICE")) {
                            isVoice = true;
                        }
                    }
                } catch (Exception e) {
                    HelperLog.getInstance().setErrorLog(e);
                }
            }

            if (isVoice) {
//                closeLayoutMediaPlayer();
            }

            updateFastAdapter(MusicPlayer.messageId);
            MusicPlayer.messageId = messageID;
            MusicPlayer.musicPath = musicPath;
            MusicPlayer.roomName = roomName;
            mediaThumpnail = null;
            MusicPlayer.roomId = roomId;

        }

        if (MusicPlayer.downloadNextMusic(messageId)) {
            if (FragmentMediaPlayer.fastItemAdapter != null) {
                FragmentMediaPlayer.fastItemAdapter.notifyAdapterDataSetChanged();
            }
        }

        try {
            if (mp != null) {
                mp.setOnCompletionListener(null);
                mp.stop();
                mp.reset();
                mp.release();
            }
            musicName = getMusicName(Long.parseLong(messageID), name);
            mp = new MediaPlayer();

            if (layoutTripMusic != null) {
                layoutTripMusic.setVisibility(View.VISIBLE);
                playerStateChangeListener.setValue(true);
                MusicPlayer.playerStatusObservable.setValue(PLAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mp.setDataSource(musicPath);

            if (isNearDistance) {
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mp.prepare();
            mp.start();

            if (currentDuration > 0) {
                mp.seekTo(currentDuration);
                currentDuration = 0;
            }

            getMusicArtist();

            updateFastAdapter(MusicPlayer.messageId);
            musicTime = milliSecondsToTimer((long) mp.getDuration());
            txt_music_time.setText(musicTime);
            btnPlayMusic.setText(context.getString(R.string.icon_pause));
            txt_music_name.setText(musicName);

            if (isVoice) {
                txt_music_info.setVisibility(View.GONE);
            } else {
                txt_music_info.setVisibility(View.VISIBLE);
                txt_music_info.setText(musicInfoTitle);
            }
            updateName = new UpdateName() {
                @Override
                public void rename() {
                    musicName = getMusicName(Long.parseLong(messageID), name);
                }
            };

            updateProgress();

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    OnCompleteMusic();
                }
            });

            if (onComplete != null) {
                onComplete.complete(true, "update", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!inChangeStreamType) {
            updateNotification();
        }

        if (!isShowMediaPlayer) {

            if (onCompleteChat != null) {
                onCompleteChat.complete(true, "pause", "");
            }
        } else if (onComplete != null) {
            onComplete.complete(true, "pause", "");
        }

        if (updateList || downloadNewItem) {
            fillMediaList(true);
            downloadNewItem = false;
        }


        if (HelperCalander.isPersianUnicode) {
//            txt_music_time.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_music_time.getText().toString()));
        }

        isMusicPlyerEnable = true;


        inChangeStreamType = false;


        if (MusicPlayer.chatLayout != null) {
            MusicPlayer.chatLayout.setVisibility(View.VISIBLE);
        }

        if (MusicPlayer.mainLayout != null) {
            MusicPlayer.mainLayout.setVisibility(View.VISIBLE);
        }

        if (MusicPlayer.shearedMediaLayout != null) {
            MusicPlayer.shearedMediaLayout.setVisibility(View.VISIBLE);
        }

        EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.MEDIA_PLAYER_STATE_CHANGED, true);
    }

    private static void OnCompleteMusic() {
        try {
            if (isVoice) {

                fillMediaList(true);
                nextMusic();
                if (FragmentChat.onMusicListener != null) {
                    FragmentChat.onMusicListener.complete(false, MusicPlayer.messageId, "");
                } else {
                    downloadNextMusic(MusicPlayer.messageId);
                }

                String zeroTime = "0:00";
                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "play", "");
                    onCompleteChat.complete(false, "updateTime", zeroTime);
                } else {
                    if (FragmentChat.onMusicListener != null) {
                        FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, "");
                    }
                }
            } else {
                if (repeatMode.equals(RepeatMode.noRepeat.toString())) {
                    stopSound();
                    closeLayoutMediaPlayer();
                } else if (repeatMode.equals(RepeatMode.repeatAll.toString())) {

                    if (playNextMusic) {

                        fillMediaList(true);

                        nextMusic();
                        if (FragmentChat.onMusicListener != null) {
                            FragmentChat.onMusicListener.complete(false, MusicPlayer.messageId, "");
                        } else {
                            downloadNextMusic(MusicPlayer.messageId);
                        }
                    } else {

                        if (isShuffelOn) {
                            nextRandomMusic();
                        } else {

                            nextMusic();
                        }
                    }
                } else if (repeatMode.equals(RepeatMode.oneRpeat.toString())) {
                    stopSound();
                    playAndPause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String milliSecondsToTimer(long milliseconds) {

        if (milliseconds == -1) return " ";

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

    private static void updateNotification() {
        if (!isVoice) {
            getMusicInfo();

            Intent intentFragmentMusic = new Intent(context, ActivityMain.class);
            intentFragmentMusic.putExtra(ActivityMain.openMediaPlyer, true);

            PendingIntent pi = PendingIntent.getActivity(context, 555, intentFragmentMusic, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentPrevious = new Intent(context, CustomButtonListener.class);
            intentPrevious.putExtra("mode", "previous");
            PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 1, intentPrevious, 0);

            Intent intentPlayPause = new Intent(context, CustomButtonListener.class);
            intentPlayPause.putExtra("mode", "play");
            PendingIntent pendingIntentPlayPause = PendingIntent.getBroadcast(context, 2, intentPlayPause, 0);

            Intent intentforward = new Intent(context, CustomButtonListener.class);
            intentforward.putExtra("mode", "forward");
            PendingIntent pendingIntentforward = PendingIntent.getBroadcast(context, 3, intentforward, 0);

            Intent intentClose = new Intent(context, CustomButtonListener.class);
            intentClose.putExtra("mode", "close");
            PendingIntent pendingIntentClose = PendingIntent.getBroadcast(context, 4, intentClose, 0);
        }

        Intent intent = new Intent(context, MusicPlayer.class);
        intent.putExtra("ACTION", STARTFOREGROUND_ACTION);
        context.startService(intent);
    }

    public static ArrayList<MessageObject> fillMediaList(boolean setSelectedItem) {

        boolean isOnListMusic = false;
        mediaList = new ArrayList<>();

        List<RealmRoomMessage> roomMessages = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).notEqualTo("createTime", 0).equalTo("deleted", false).equalTo("showMessage", true).findAll().sort("messageId", Sort.DESCENDING);
        });

        List<MessageObject> messageObjects = new ArrayList<>();
        for (RealmRoomMessage message : roomMessages) {
            messageObjects.add(MessageObject.create(message));
        }
        if (!messageObjects.isEmpty()) {
            for (MessageObject messageObject : messageObjects) { //TODO Saeed Mozaffari; write better code for detect voice and audio instead get all roomMessages

                MessageObject roomMessage = RealmRoomMessage.getFinalMessage(messageObject);

                if (isVoice) {
                    if (ProtoGlobal.RoomMessageType.forNumber(roomMessage.messageType).toString().equals(ProtoGlobal.RoomMessageType.VOICE.toString())) {
                        try {
                            if (roomMessage.attachment.filePath != null) {
                                if (new File(roomMessage.attachment.filePath).exists()) {
                                    mediaList.add(roomMessage);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    if ((ProtoGlobal.RoomMessageType.forNumber(roomMessage.messageType).toString().equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) || ProtoGlobal.RoomMessageType.forNumber(roomMessage.messageType).toString().equals(ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString()))) {

                        if (mediaList.size() <= limitMediaList || !isOnListMusic) {
                            try {
                                if (roomMessage.id == Long.parseLong(messageId)) {
                                    isOnListMusic = true;
                                }
                                mediaList.add(roomMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        if (setSelectedItem) {
            for (int i = mediaList.size() - 1; i >= 0; i--) {
                try {
                    MessageObject _rm = RealmRoomMessage.getFinalMessage(mediaList.get(i));
                    if (_rm.attachment.filePath != null && _rm.attachment.filePath.equals(musicPath)) {
                        selectedMedia = i;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        return mediaList;
    }

    private static void updateProgress() {

        stopTimer();

        double duration = MusicPlayer.mp.getDuration();
        amoungToupdate = duration / 100;
        time = MusicPlayer.mp.getCurrentPosition();
        musicProgress = ((int) (time / amoungToupdate));

        mTimeSecend = new Timer();

        mTimeSecend.schedule(new TimerTask() {
            @Override
            public void run() {

                updatePlayerTime();
                time += 1000;
            }
        }, 0, 1000);

        if (amoungToupdate >= 1) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {

                    if (musicProgress < 100) {
                        musicProgress++;
                    } else {
                        stopTimer();
                    }
                }
            }, 0, (int) amoungToupdate);
        }
    }

    private static void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeSecend != null) {
            mTimeSecend.cancel();
            mTimeSecend = null;
        }
    }

    private static void updatePlayerTime() {

        strTimer = MusicPlayer.milliSecondsToTimer(time);

        if (HelperCalander.isPersianUnicode) {
            strTimer = HelperCalander.convertToUnicodeFarsiNumber(strTimer);
        }

        if (txt_music_time_counter != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    txt_music_time_counter.setText(strTimer);
                }
            });
        }

        if (isShowMediaPlayer) {
            if (onComplete != null) {
                onComplete.complete(true, "updateTime", strTimer);
            }
        } else {
            if (onCompleteChat != null) {
                onCompleteChat.complete(true, "updateTime", strTimer);
            }
        }
    }

    public static void setMusicProgress(int percent) {
        try {
            musicProgress = percent;
            if (MusicPlayer.mp != null) {
                MusicPlayer.mp.seekTo((int) (musicProgress * amoungToupdate));
                time = MusicPlayer.mp.getCurrentPosition();
                updatePlayerTime();
            }
        } catch (IllegalStateException e) {
        }
    }

    private static void getMusicInfo() {

        musicInfo = "";
        musicInfoTitle = context.getString(R.string.unknown_artist);

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        Uri uri = null;

        if (MusicPlayer.musicPath != null) {
            uri = Uri.fromFile(new File(MusicPlayer.musicPath));
        }

        if (uri != null) {

            try {

                mediaMetadataRetriever.setDataSource(context, uri);

                String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                if (title != null) {
                    musicInfo += title + "       ";
                    musicInfoTitle = title;
                }

                String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                if (albumName != null) {
                    musicInfo += albumName + "       ";
                    musicInfoTitle = albumName;
                }

                String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                if (artist != null) {
                    musicInfo += artist + "       ";
                    musicInfoTitle = artist;
                }

                byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                if (data != null) {
                    mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                    //  setWallpaperLockScreen(mediaThumpnail);
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setMedaiInfoOnLockScreen(false);
                        }
                    }, 100);
                } else {
                    setMedaiInfoOnLockScreen(true);
                }
            } catch (Exception e) {

                Log.e("debug", " music plyer   getMusicInfo    " + uri + "       " + e.toString());
            }
        }
    }

    private static void getMusicArtist() {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

        Uri uri = null;

        if (MusicPlayer.musicPath != null) {
            uri = Uri.fromFile(new File(MusicPlayer.musicPath));
        }

        if (uri != null) {

            try {

                mediaMetadataRetriever.setDataSource(context, uri);

                String title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                if (title != null) {
                    musicInfo += title + "       ";
                    musicInfoTitle = title;
                }

                String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                if (albumName != null) {
                    musicInfo += albumName + "       ";
                    musicInfoTitle = albumName;
                }

                String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                if (artist != null) {
                    musicInfo += artist + "       ";
                    musicInfoTitle = artist;
                }

            } catch (Exception e) {

                if (musicInfoTitle != null && musicInfoTitle.trim().equals("")) {
                    txt_music_info.setVisibility(View.GONE);
                }
            }
        }

    }

    private static void getAttribute() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicSetting", MODE_PRIVATE);
        repeatMode = sharedPreferences.getString("RepeatMode", RepeatMode.noRepeat.toString());
        isShuffelOn = sharedPreferences.getBoolean("Shuffel", false);
    }

    public static boolean downloadNextMusic(String messageId) {

        boolean result = false;
        RealmResults<RealmRoomMessage> roomMessages = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).equalTo("deleted", false).greaterThan("messageId", Long.parseLong(messageId)).findAll().sort("createTime");

        });

        if (!roomMessages.isEmpty()) {
            for (RealmRoomMessage rm : roomMessages) {
                if (isVoice) {
                    if (rm.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.VOICE.toString())) {
                        result = startDownload(MessageObject.create(rm));
                        break;
                    }
                } else {
                    if (rm.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) || rm.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString())) {
                        result = startDownload(MessageObject.create(rm));
                        break;
                    }
                }
            }
        }

        return result;
    }

    private static boolean startDownload(MessageObject rm) {
        boolean result = false;
        try {
            if (rm.attachment.filePath == null || !new File(rm.attachment.filePath).exists()) {
                int _messageType = rm.forwardedMessage != null ? rm.forwardedMessage.messageType : rm.messageType;
                String _cacheId = rm.forwardedMessage != null ? rm.forwardedMessage.attachment.cacheId : rm.getAttachment().cacheId;
                String _name = rm.forwardedMessage != null ? rm.forwardedMessage.attachment.name : rm.getAttachment().name;
                String _token = rm.forwardedMessage != null ? rm.forwardedMessage.attachment.token : rm.getAttachment().token;
                String _url = rm.forwardedMessage != null ? rm.forwardedMessage.attachment.publicUrl : rm.getAttachment().publicUrl;
                Long _size = rm.forwardedMessage != null ? rm.forwardedMessage.attachment.size : rm.getAttachment().size;

                if (_cacheId == null) {
                    return false;
                }

                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

                final String _path = AndroidUtils.getFilePathWithCashId(_cacheId, _name, _messageType);

                if (_token != null && _token.length() > 0 && _size > 0) {

                    if (!new File(_path).exists()) {

                        result = true;
                        DownloadObject fileObject = DownloadObject.createForRoomMessage(rm);

                        if (fileObject == null) {
                            return false;
                        }

                        Downloader.getInstance(AccountManager.selectedAccount).download(fileObject, selector, arg -> {
                            switch (arg.status) {
                                case SUCCESS:
                                case LOADING:
                                    if (arg.data != null && arg.data.getProgress() == 100)
                                        downloadNewItem = true;
                                    break;
                            }
                        });

                        MusicPlayer.playNextMusic = true;
                    } else {
                        MusicPlayer.playNextMusic = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void initSensor() {

        if (!isRegisterSensor) {

            registerMediaBottom();

            headsetPluginReciver = new HeadsetPluginReciver();
            //bluetoothCallbacks = new BluetoothCallbacks();

            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

                        AudioManager am = (AudioManager) G.context.getSystemService(AUDIO_SERVICE);
                        if (am.isWiredHeadsetOn() || am.isSpeakerphoneOn()) {
                            return;
                        }

                        boolean newIsNear = Math.abs(event.values[0]) < Math.min(event.sensor.getMaximumRange(), 3);
                        if (newIsNear != isNearDistance) {
                            isNearDistance = newIsNear;
                            if (isVoice) {
                                changeStreamType();
                            }
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            context.registerReceiver(headsetPluginReciver, filter);
            registerDistanceSensor();

            isRegisterSensor = true;
        }
    }

    private static void changeStreamType() {

        try {
            if (mp != null && mp.isPlaying()) {
                inChangeStreamType = true;
                currentDuration = mp.getCurrentPosition();
                startPlayer(musicName, musicPath, roomName, roomId, false, MusicPlayer.messageId);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void removeSensor() {

        if (isRegisterSensor) {

            isRegisterSensor = false;

            AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

            try {
                if (remoteComponentName != null && audioManager != null) {
                    audioManager.unregisterMediaButtonEventReceiver(remoteComponentName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (remoteControlClient != null && audioManager != null) {
                    audioManager.unregisterRemoteControlClient(remoteControlClient);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                context.unregisterReceiver(headsetPluginReciver);
            } catch (Exception e) {
                e.printStackTrace();
            }

            unRegisterDistanceSensor();

            remoteComponentName = null;
            remoteControlClient = null;

        }
    }

    private static void registerDistanceSensor() {

        try {

            mSensorManager.registerListener(sensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            Log.e("dddd", "music player registerDistanceSensor   " + e.toString());
        }
    }

    private static void unRegisterDistanceSensor() {

        try {
            mSensorManager.unregisterListener(sensorEventListener);
        } catch (Exception e) {
            Log.e("dddd", "music player unRegisterDistanceSensor   " + e.toString());
        }
    }

    private static void registerMediaBottom() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                try {

                    mSession = new MediaSessionCompat(context, context.getPackageName());
                    Intent intent = new Intent(context, MediaBottomReciver.class);
                    PendingIntent pintent = PendingIntent.getBroadcast(context, 50, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mSession.setMediaButtonReceiver(pintent);
                    mSession.setActive(true);

                    PlaybackStateCompat state = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

                            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime()).build();
                    mSession.setPlaybackState(state);
                } catch (Exception e) {

                    HelperLog.getInstance().setErrorLog(e);
                }
            } else {

                try {
                    remoteComponentName = new ComponentName(context, MediaBottomReciver.class.getName());
                    AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                    audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                } catch (Exception e) {
                    HelperLog.getInstance().setErrorLog(e);
                }
            }

            if (remoteComponentName != null) {
                remoteComponentName = new ComponentName(context, MediaBottomReciver.class.getName());
            }

            try {

                if (remoteControlClient == null) {

                    Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    mediaButtonIntent.setComponent(remoteComponentName);
                    PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context, 55, mediaButtonIntent, 0);
                    remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                    AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                    audioManager.registerRemoteControlClient(remoteControlClient);

                    remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
                }
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
            }
        }
    }

    private static void setMedaiInfoOnLockScreen(boolean clear) {

        try {

            if (remoteControlClient != null) {

                RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);

                if (clear) {

                    metadataEditor.clear();
                } else {
                    metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, musicName + "");
                    metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, musicInfoTitle + "");
                    try {
                        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mediaThumpnail);

                    } catch (Throwable e) {
                    }
                }

                metadataEditor.apply();
            }
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    //***************************************************************************** sensor *********************************

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        G.onAudioFocusChangeListener = this;

        if (intent == null || intent.getExtras() == null) {
            stopForeground(false);
            stopSelf();
        } else {

            String action = intent.getExtras().getString("ACTION");
            if (action != null) {

                if (action.equals(STARTFOREGROUND_ACTION)) {

                    if (notification != null) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            CharSequence name = G.context.getString(R.string.channel_name_notification);// The user-visible name of the channel.
                            @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(musicChannelId, name, NotificationManager.IMPORTANCE_HIGH);
                            getNotificationManager().createNotificationChannel(mChannel);
                        }

                        try {
                            startForeground(notificationId, notification);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if (latestAudioFocusState != AudioManager.AUDIOFOCUS_GAIN) { // if do double "AUDIOFOCUS_GAIN", "AUDIOFOCUS_LOSS" will be called
                            latestAudioFocusState = AudioManager.AUDIOFOCUS_GAIN;
                            registerAudioFocus(AudioManager.AUDIOFOCUS_GAIN);
                        }
                    }
                    initSensor();
                } else if (action.equals(STOPFOREGROUND_ACTION)) {

                    removeSensor();

                    stopForeground(false);
                    stopSelf();
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getNotificationManager().cancel(notificationId);
        } catch (NullPointerException e) {
            notificationManager.cancel(notificationId);
        }
    }

    private void registerAudioFocus(int audioState) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, audioState);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        latestAudioFocusState = focusChange;
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            pauseOnAudioFocusChange = true;
            if (mp != null && mp.isPlaying()) pauseSound();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (pauseOnAudioFocusChange) {
                pauseOnAudioFocusChange = false;
                //playSound(); // commented this line because after receive incoming call and end call, this listener will be called and sound will be played!!!
            }
        }
    }

    //*************************************************************************************** getPhoneState

    @Override
    public void onAudioFocusChangeListener(int audioState) {
        if (latestAudioFocusState != audioState) {
            latestAudioFocusState = audioState;
            registerAudioFocus(audioState);
        }
    }


    public enum RepeatMode {
        noRepeat, oneRpeat, repeatAll
    }

    public interface UpdateName {
        void rename();
    }

    static class HeadsetPluginReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                int state = intent.getIntExtra("state", -1);

                if (state != stateHedset) {

                    stateHedset = state;

                    switch (state) {
                        case 0:
                            if (mp != null && mp.isPlaying()) {
                                pauseSound();
                            }
                            break;
                        case 1:
                            break;
                    }
                }
            }
        }
    }

    static class BluetoothCallbacks extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                pauseSound();
                            }
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    }
}


