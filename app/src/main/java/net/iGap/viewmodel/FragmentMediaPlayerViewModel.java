package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.fragments.FragmentMediaPlayer;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperSaveFile;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.libs.ripplesoundplayer.RippleVisualizerView;
import net.iGap.libs.ripplesoundplayer.renderer.LineRenderer;
import net.iGap.libs.ripplesoundplayer.util.PaintUtil;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentMediaPlayerViewModel {


    public ObservableField<String> callBackMusicName = new ObservableField<>(G.context.getResources().getString(R.string.music_name));
    public ObservableField<String> callBackMusicPlace = new ObservableField<>(G.context.getResources().getString(R.string.place));
    public ObservableField<String> callBackBtnPlayMusic = new ObservableField<>();
    public ObservableField<String> callBackTxtTimer = new ObservableField<>("00:00}");
    public ObservableField<String> callBackTxtMusicInfo = new ObservableField<>("");
    public ObservableField<String> callBackTxtMusicTime = new ObservableField<>(G.context.getResources().getString(R.string.music_time));
    public ObservableField<String> callBackBtnReplayMusic = new ObservableField<>(G.context.getResources().getString(R.string.md_synchronization_arrows));
    public ObservableInt txtMusicInfoVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt imgRepeadOneVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt imgMusicPicture = new ObservableInt(View.VISIBLE);
    public ObservableInt imgMusIciconDefault = new ObservableInt(View.VISIBLE);
    public ObservableInt btnShuffelMusicColor = new ObservableInt(R.attr.iGapTitleTextColor);
    public ObservableInt btnReplayMusicColor = new ObservableInt(R.attr.iGapTitleTextColor);
    public ObservableInt seekBar1 = new ObservableInt();
    public ObservableBoolean txtMusicInfoSingleLine = new ObservableBoolean(true);
    private RippleVisualizerView rippleVisualizerView;
    private View v;

    public FragmentMediaPlayerViewModel(View v) {

        this.v = v;

        getInfo();
    }

    public void onClickRippleBack(View v) {

        if (FragmentMediaPlayer.onBackFragment != null) {
            FragmentMediaPlayer.onBackFragment.onBack();
        }

    }

    public void onClickRippleMenu(View v) {
        popUpMusicMenu();
    }

    public void onClickBtnShuffelMusic(View v) {
        MusicPlayer.shuffleClick();
    }

    public void onClickBtnReplayMusic(View v) {
        MusicPlayer.repeatClick();
    }

    public void onClickBtnPlayMusic(View v) {
        MusicPlayer.playAndPause();
    }

    public void onClickBtnForwardMusic(View v) {
        MusicPlayer.nextMusic();
    }

    public void onClickBtnPreviousMusic(View v) {
        MusicPlayer.previousMusic();
    }

    private void getInfo() {

        FragmentMediaPlayer.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, final String MessageTow) {

                if (messageOne.equals("play")) {
                    callBackBtnPlayMusic.set(G.fragmentActivity.getResources().getString(R.string.play_icon));

                    if (rippleVisualizerView != null) {
                        rippleVisualizerView.setEnabled(false);
                        rippleVisualizerView.pauseVisualizer();
                    }
                } else if (messageOne.equals("pause")) {
                    callBackBtnPlayMusic.set(G.fragmentActivity.getResources().getString(R.string.pause_icon));

                    if (rippleVisualizerView != null) {
                        rippleVisualizerView.setEnabled(true);
                        rippleVisualizerView.startVisualizer();
                    }
                } else if (messageOne.equals("update")) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUi();
                        }
                    });
                } else if (messageOne.equals("updateTime")) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBackTxtTimer.set(MessageTow);
                            seekBar1.set(MusicPlayer.musicProgress);
                        }
                    });

                } else if (messageOne.equals("RepeatMode")) {
                    setReplayButton();
                } else if (messageOne.equals("Shuffel")) {
                    setShuffleButton();
                } else if (messageOne.equals("finish")) {
                    if (FragmentMediaPlayer.onBackFragment != null) {
                        FragmentMediaPlayer.onBackFragment.onBack();
                    }
                }
            }
        };

        setMusicInfo();
        initVisualizer(v);
        setShuffleButton();
        setReplayButton();

        if (HelperCalander.isPersianUnicode) {
            callBackTxtTimer.set(HelperCalander.convertToUnicodeFarsiNumber(callBackTxtTimer.get()));
            callBackTxtMusicTime.set(HelperCalander.convertToUnicodeFarsiNumber(callBackTxtMusicTime.get()));
        }
    }


    private void popUpMusicMenu() {

        List<String> items = new ArrayList<>();
        items.add(G.fragmentActivity.getString(R.string.save_to_Music));
        items.add(G.fragmentActivity.getString(R.string.share_item_dialog));

        new TopSheetDialog(G.fragmentActivity).setListData(items, -1, position -> {
            if (items.get(position).equals(G.fragmentActivity.getString(R.string.save_to_Music))) {
                saveToMusic();
            } else if (items.get(position).equals(G.fragmentActivity.getString(R.string.share_item_dialog))) {
                shareMusic();
            }
        }).show();
    }

    private void saveToMusic() {

        HelperSaveFile.saveToMusicFolder(MusicPlayer.musicPath, MusicPlayer.musicName);
    }

    private void shareMusic() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        AppUtils.shareItem(intent, MusicPlayer.musicPath);
        String chooserDialogText = G.fragmentActivity.getResources().getString(R.string.share_audio_file);
        G.fragmentActivity.startActivity(Intent.createChooser(intent, chooserDialogText));
    }

    private void initVisualizer(final View view) {

        if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            try {
                HelperPermission.getMicroPhonePermission(G.currentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {

                        rippleVisualizerView = view.findViewById(R.id.line_renderer_demo);

                        if (rippleVisualizerView != null) {

                            rippleVisualizerView.setCurrentRenderer(new LineRenderer(PaintUtil.getLinePaint(Color.parseColor("#15E4EE"))));


                            if (MusicPlayer.mp.isPlaying()) {
                                rippleVisualizerView.setEnabled(true);
                            } else {
                                rippleVisualizerView.setEnabled(false);
                            }

                            rippleVisualizerView.setAmplitudePercentage(3);
                        }
                    }

                    @Override
                    public void deny() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUi() {
        callBackTxtMusicTime.set(MusicPlayer.musicTime);
        callBackMusicPlace.set(MusicPlayer.musicInfoTitle);
        callBackMusicName.set(MusicPlayer.musicName);
        callBackTxtTimer.set(MusicPlayer.strTimer);

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                callBackBtnPlayMusic.set(G.fragmentActivity.getResources().getString(R.string.pause_icon));
            } else {
                callBackBtnPlayMusic.set(G.fragmentActivity.getResources().getString(R.string.play_icon));
            }

            if (MusicPlayer.mediaThumpnail != null) {

                if (FragmentMediaPlayer.onSetImage != null)
                    FragmentMediaPlayer.onSetImage.setImage();

                imgMusicPicture.set(View.VISIBLE);
                imgMusIciconDefault.set(View.GONE);
            } else {
                imgMusicPicture.set(View.GONE);
                imgMusIciconDefault.set(View.VISIBLE);
            }

            setMusicInfo();
        }

        if (HelperCalander.isPersianUnicode) {
            callBackTxtMusicTime.set(HelperCalander.convertToUnicodeFarsiNumber(callBackTxtMusicTime.get().toString()));
        }


        if (rippleVisualizerView != null) {
            rippleVisualizerView.setMediaPlayer(MusicPlayer.mp);
        }
    }

    private void setReplayButton() {
        if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.noRepeat.toString())) {
            callBackBtnReplayMusic.set(G.context.getResources().getString(R.string.retry_icon));
            btnReplayMusicColor.set(Color.GRAY);
            imgRepeadOneVisibility.set(View.GONE);
        } else if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.repeatAll.toString())) {
            callBackBtnReplayMusic.set(G.context.getResources().getString(R.string.retry_icon));
            btnReplayMusicColor.set(R.attr.iGapTitleTextColor);
            imgRepeadOneVisibility.set(View.GONE);
        } else if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.oneRpeat.toString())) {
            callBackBtnReplayMusic.set(G.context.getResources().getString(R.string.retry_icon));
            btnReplayMusicColor.set(R.attr.iGapTitleTextColor);
            imgRepeadOneVisibility.set(View.VISIBLE);
        }
    }

    private void setShuffleButton() {

        if (MusicPlayer.isShuffelOn) {
            btnShuffelMusicColor.set(R.attr.iGapTitleTextColor);
        } else {
            btnShuffelMusicColor.set(Color.GRAY);
        }
    }

    private void setMusicInfo() {

        if (MusicPlayer.musicInfo.trim().length() > 0) {
            txtMusicInfoVisibility.set(View.GONE);//before was visible
            callBackTxtMusicInfo.set(MusicPlayer.musicInfo);
            //txt_musicInfo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            //txt_musicInfo.setSelected(true);
            txtMusicInfoSingleLine.set(true);
        } else {
            txtMusicInfoVisibility.set(View.GONE);
        }
    }

    public void onResume() {
        if (MusicPlayer.mp == null) {
            if (FragmentMediaPlayer.onBackFragment != null) {
                FragmentMediaPlayer.onBackFragment.onBack();
            }
        } else {
            MusicPlayer.isShowMediaPlayer = true;
            updateUi();
            MusicPlayer.onComplete = FragmentMediaPlayer.onComplete;
        }
    }

    public void onStop() {
        if (rippleVisualizerView != null) {
            rippleVisualizerView.pauseVisualizer();
        }
    }
}
