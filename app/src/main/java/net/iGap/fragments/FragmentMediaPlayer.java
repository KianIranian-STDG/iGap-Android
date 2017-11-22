/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperSaveFile;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.libs.ripplesoundplayer.RippleVisualizerView;
import net.iGap.libs.ripplesoundplayer.renderer.LineRenderer;
import net.iGap.libs.ripplesoundplayer.util.PaintUtil;
import net.iGap.module.DialogAnimation;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.realm.RealmRoomMessage;

public class FragmentMediaPlayer extends BaseFragment {

    TextView btnReplay;
    TextView btnShuffle;
    OnComplete onComplete;
    private TextView txt_MusicName;
    private TextView txt_MusicPlace;
    private TextView txt_MusicTime;
    private TextView txt_Timer;
    private TextView txt_musicInfo;
    private SeekBar musicSeekbar;
    private ImageView img_MusicImage;
    private ImageView img_RepeatOne;
    private ImageView img_MusicImage_default_icon;
    private TextView btnPlay;
    private RecyclerView rcvListMusicPlayer;
    public static AdapterListMusicPlayer adapterListMusicPlayer;


    private RippleVisualizerView rippleVisualizerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;

        if (G.twoPaneMode) {
            return inflater.inflate(R.layout.activity_media_player, container, false);
        } else {
            if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return inflater.inflate(R.layout.activity_media_player_land, container, false);
            } else {
                return inflater.inflate(R.layout.activity_media_player, container, false);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MusicPlayer.isShowMediaPlayer = true;

        if (MusicPlayer.mp == null) {
            removeFromBaseFragment();
            return;
        }

        onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, final String MessageTow) {

                if (messageOne.equals("play")) {
                    btnPlay.setText(R.string.md_play_rounded_button);

                    if (rippleVisualizerView != null) {
                        rippleVisualizerView.setEnabled(false);
                        rippleVisualizerView.pauseVisualizer();
                    }
                } else if (messageOne.equals("pause")) {
                    btnPlay.setText(R.string.md_round_pause_button);

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
                    txt_Timer.post(new Runnable() {
                        @Override
                        public void run() {
                            txt_Timer.setText(MessageTow);
                            musicSeekbar.setProgress(MusicPlayer.musicProgress);

                            if (HelperCalander.isPersianUnicode) txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_Timer.getText().toString()));
                        }
                    });
                } else if (messageOne.equals("RepeatMode")) {
                    setReplayButton();
                } else if (messageOne.equals("Shuffel")) {
                    setShuffleButton();
                } else if (messageOne.equals("finish")) {
                    removeFromBaseFragment();
                }
            }
        };

        initComponent(view);
        setMusicInfo();

        MusicPlayer.onComplete = onComplete;
    }

    private void setShuffleButton() {

        if (MusicPlayer.isShuffelOn) {
            btnShuffle.setTextColor(Color.BLACK);
        } else {
            btnShuffle.setTextColor(Color.GRAY);
        }
    }

    private void setReplayButton() {
        if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.noRepeat.toString())) {
            btnReplay.setText(R.string.md_synchronization_arrows);
            btnReplay.setTextColor(Color.GRAY);
            img_RepeatOne.setVisibility(View.GONE);
        } else if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.repeatAll.toString())) {
            btnReplay.setText(R.string.md_synchronization_arrows);
            btnReplay.setTextColor(Color.BLACK);
            img_RepeatOne.setVisibility(View.GONE);
        } else if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.oneRpeat.toString())) {
            btnReplay.setText(R.string.md_synchronization_arrows);
            btnReplay.setTextColor(Color.BLACK);
            img_RepeatOne.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MusicPlayer.isShowMediaPlayer = false;
        MusicPlayer.onComplete = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MusicPlayer.mp == null) {
            removeFromBaseFragment();
        } else {
            MusicPlayer.isShowMediaPlayer = true;
            updateUi();
            MusicPlayer.onComplete = onComplete;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (rippleVisualizerView != null) {
            rippleVisualizerView.pauseVisualizer();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        try {
            if (!G.twoPaneMode) {
                if (isAdded()) {
                    G.fragmentManager.beginTransaction().detach(this).attach(this).commit();
                }
            }
        } catch (Exception e) {
            Log.e("ddddd", "FragmentMediaPlayer  onConfigurationChanged  " + e.toString());
        }



        super.onConfigurationChanged(newConfig);
    }

    //*****************************************************************************************

    private void initComponent(View view) {

        initVisualizer(view);

        txt_MusicName = (TextView) view.findViewById(R.id.ml_txt_music_name);
        txt_MusicPlace = (TextView) view.findViewById(R.id.ml_txt_music_place);
        txt_MusicTime = (TextView) view.findViewById(R.id.ml_txt_music_time);
        txt_Timer = (TextView) view.findViewById(R.id.ml_txt_timer);

        txt_musicInfo = (TextView) view.findViewById(R.id.ml_txt_music_info);
        img_MusicImage = (ImageView) view.findViewById(R.id.ml_img_music_picture);
        img_MusicImage_default_icon = (ImageView) view.findViewById(R.id.ml_img_music_icon_default);
        img_RepeatOne = (ImageView) view.findViewById(R.id.ml_img_repead_one);
        if (MusicPlayer.mediaThumpnail != null) {
            img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
            img_MusicImage.setVisibility(View.VISIBLE);
            img_MusicImage_default_icon.setVisibility(View.GONE);
        } else {
            img_MusicImage.setVisibility(View.GONE);
            img_MusicImage_default_icon.setVisibility(View.VISIBLE);
        }

        musicSeekbar = (SeekBar) view.findViewById(R.id.ml_seekBar1);
        musicSeekbar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    MusicPlayer.setMusicProgress(musicSeekbar.getProgress());
                }
                return false;
            }
        });

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.ml_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.ml_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                removeFromBaseFragment();
            }
        });

        MaterialDesignTextView btnMusicMenu = (MaterialDesignTextView) view.findViewById(R.id.ml_btn_music_menu);
        RippleView rippleMusicMenu = (RippleView) view.findViewById(R.id.amp_ripple_menu);
        rippleMusicMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popUpMusicMenu();
            }
        });

        TextView btnPrevious = (TextView) view.findViewById(R.id.ml_btn_Previous_music);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.previousMusic();
            }
        });

        btnShuffle = (TextView) view.findViewById(R.id.ml_btn_shuffel_music);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.shuffleClick();
            }
        });

        setShuffleButton();

        btnReplay = (TextView) view.findViewById(R.id.ml_btn_replay_music);
        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.repeatClick();
            }
        });
        setReplayButton();

        btnPlay = (TextView) view.findViewById(R.id.ml_btn_play_music);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.playAndPause();
            }
        });

        TextView btnNextMusic = (TextView) view.findViewById(R.id.ml_btn_forward_music);
        btnNextMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.nextMusic();
            }
        });

        if (HelperCalander.isPersianUnicode) {
            txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_Timer.getText().toString()));
            txt_MusicTime.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_MusicTime.getText().toString()));
        }


        rcvListMusicPlayer = (RecyclerView) view.findViewById(R.id.rcvListMusicPlayer);
        adapterListMusicPlayer = new AdapterListMusicPlayer(MusicPlayer.mediaList, new AdapterListMusicPlayer.OnClickAdapterListMusic() {
            @Override
            public void onClick(String name) {
            }
        });

        rcvListMusicPlayer.setAdapter(adapterListMusicPlayer);
        rcvListMusicPlayer.setLayoutManager(new LinearLayoutManager(_mActivity));
        //adapterListMusicPlayer.updateAdapter(MusicPlayer.mediaList);

    }

    //private void setRenderer() {
    //    switch (currentRenderMode) {
    //        case LINE:
    //            renderDemoView.setCurrentRenderer(new LineRenderer(PaintUtil.getLinePaint(Color.YELLOW)));
    //            break;
    //        case BAR_GRAPH:
    //            renderDemoView.setCurrentRenderer(new BarRenderer(16, PaintUtil.getBarGraphPaint(Color.WHITE)));
    //            break;
    //        case COLORFUL_BAR_GRAPH:
    //            renderDemoView.setCurrentRenderer(new ColorfulBarRenderer(8, PaintUtil.getBarGraphPaint(Color.BLUE)
    //                , Color.parseColor("#FF0033")
    //                , Color.parseColor("#ffebef"))
    //            );
    //            break;
    //    }
    //}

    private void initVisualizer(final View view) {

        if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            try {
                HelperPermision.getMicroPhonePermission(G.currentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {

                        rippleVisualizerView = (RippleVisualizerView) view.findViewById(R.id.line_renderer_demo);

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

    private void popUpMusicMenu() {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);

        final TextView txtShare = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtSaveToGallery = (TextView) v.findViewById(R.id.dialog_text_item2_notification);

        TextView iconSaveToGallery = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconSaveToGallery.setText(G.fragmentActivity.getResources().getString(R.string.md_save));

        txtShare.setText(G.fragmentActivity.getResources().getString(R.string.save_to_Music));
        txtSaveToGallery.setText(G.fragmentActivity.getResources().getString(R.string.share_item_dialog));

        root1.setVisibility(View.VISIBLE);
        root2.setVisibility(View.VISIBLE);

        TextView iconShare = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconShare.setText(G.fragmentActivity.getResources().getString(R.string.md_share_button));

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                saveToMusic();
            }
        });

        root2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareMusuic();
            }
        });
    }

    private void saveToMusic() {

        HelperSaveFile.saveToMusicFolder(MusicPlayer.musicPath, MusicPlayer.musicName);
    }

    private void shareMusuic() {

        String sharePath = MusicPlayer.musicPath;

        Uri uri = Uri.fromFile(new File(sharePath));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, G.fragmentActivity.getResources().getString(R.string.shate_audio_file)));
    }

    private void updateUi() {
        txt_MusicTime.setText(MusicPlayer.musicTime);
        txt_MusicPlace.setText(MusicPlayer.musicInfoTitle);
        txt_MusicName.setText(MusicPlayer.musicName);
        txt_Timer.setText(MusicPlayer.strTimer);

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                btnPlay.setText(G.fragmentActivity.getResources().getString(R.string.md_round_pause_button));
            } else {
                btnPlay.setText(G.fragmentActivity.getResources().getString(R.string.md_play_rounded_button));
            }

            if (MusicPlayer.mediaThumpnail != null) {
                img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
                img_MusicImage.setVisibility(View.VISIBLE);
                img_MusicImage_default_icon.setVisibility(View.GONE);
            } else {
                img_MusicImage.setVisibility(View.GONE);
                img_MusicImage_default_icon.setVisibility(View.VISIBLE);
            }

            setMusicInfo();
        }

        if (HelperCalander.isPersianUnicode) {
            txt_MusicTime.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_MusicTime.getText().toString()));
        }

        if (rippleVisualizerView != null) {
            rippleVisualizerView.setMediaPlayer(MusicPlayer.mp);
        }
    }

    private void setMusicInfo() {

        if (MusicPlayer.musicInfo.trim().length() > 0) {
            txt_musicInfo.setVisibility(View.VISIBLE);
            txt_musicInfo.setText(MusicPlayer.musicInfo);

            txt_musicInfo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            txt_musicInfo.setSelected(true);
            txt_musicInfo.setSingleLine(true);
        } else {
            txt_musicInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicPlayer.onComplete = null;
    }

    public static class AdapterListMusicPlayer extends RecyclerView.Adapter<AdapterListMusicPlayer.ViewHolder> {

        private List<RealmRoomMessage> realmRoomMessagesList;
        private OnClickAdapterListMusic adapterOnclick;
        private DisplayImageOptions options;

        public AdapterListMusicPlayer(List<RealmRoomMessage> realmRoomMessages, OnClickAdapterListMusic adapterOnclick) {
            realmRoomMessagesList = realmRoomMessages;
            this.adapterOnclick = adapterOnclick;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_music_player, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            RealmRoomMessage realmRoomMessage = getItem(position);



            holder.txtNameMusic.setText(realmRoomMessage.getAttachment().getName());
            MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();

            Uri uri = null;

            if (realmRoomMessage.getAttachment().getLocalFilePath() != null) {
                uri = (Uri) Uri.fromFile(new File(realmRoomMessage.getAttachment().getLocalFilePath()));
            }

            if (uri != null) {
                mediaMetadataRetriever.setDataSource(G.context, uri);
                byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                if (data != null) {
                    Bitmap mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                    holder.imgMusicItem.setImageBitmap(mediaThumpnail);
                } else {

                    holder.imgMusicItem.setImageResource(R.mipmap.music_icon_green);
                }
            }

            String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (artist != null) {
                holder.txtMusicplace.setText(artist);
            } else {
                holder.txtMusicplace.setText(G.context.getString(R.string.unknown_artist));
            }
        }

        @Override
        public int getItemCount() {
            return realmRoomMessagesList.size();
        }


        private RealmRoomMessage getItem(int po) {
            return RealmRoomMessage.getFinalMessage(realmRoomMessagesList.get(po));
        }

        //public void updateAdapter(ArrayList<RealmRoomMessage> realmRoomMessages) {
        //    realmRoomMessagesList = realmRoomMessages;
        //    notifyDataSetChanged();
        //}


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView txtNameMusic, txtMusicplace;
            private ImageView imgMusicItem;

            public ViewHolder(View itemView) {
                super(itemView);

                txtNameMusic = itemView.findViewById(R.id.txtListMusicPlayer);
                txtMusicplace = itemView.findViewById(R.id.ml_txt_music_place);
                imgMusicItem = itemView.findViewById(R.id.imgListMusicPlayer);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

                RealmRoomMessage realmRoomMessage = realmRoomMessagesList.get(getAdapterPosition());
                adapterOnclick.onClick(realmRoomMessage.getAttachment().getName());
                MusicPlayer.startPlayer(realmRoomMessage.getAttachment().getName(), realmRoomMessage.getAttachment().getLocalFilePath(), FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, false, realmRoomMessage.getMessageId() + "");
            }

        }

        public interface OnClickAdapterListMusic {
            void onClick(String name);

        }
    }

}
