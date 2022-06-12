/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityMediaPlayerBinding;
import net.iGap.databinding.ActivityMediaPlayerLandBinding;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnProgress;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.HttpRequest;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.observers.interfaces.OnClientSearchRoomHistory;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.proto.ProtoClientSearchRoomHistory;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestClientSearchRoomHistory;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;
import net.iGap.viewmodel.FragmentMediaPlayerViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class FragmentMusicPlayer extends BaseFragment {


    public static OnComplete onComplete;
    public static OnSetImage onSetImage;
    public static FastItemAdapter fastItemAdapter;
    public static OnBackFragment onBackFragment;
    private SeekBar musicSeekbar;
    private RecyclerView rcvListMusicPlayer;
    private FragmentMediaPlayerViewModel fragmentMediaPlayerViewModel;
    private ActivityMediaPlayerBinding fragmentMediaPlayerBinding;
    private ActivityMediaPlayerLandBinding activityMediaPlayerLandBinding;
    private ItemAdapter<ProgressItem> footerAdapter;


    private long nextMessageId = 0;
    private long nextDocumentId = 0;
    private boolean isThereAnyMoreItemToLoad = false;
    private int offset;
    private RealmResults<RealmRoomMessage> mRealmList;
    private RecyclerView.OnScrollListener onScrollListener;
    private boolean canUpdateAfterDownload = false;
    protected ArrayMap<Long, Boolean> needDownloadList = new ArrayMap<>();
    private RealmChangeListener<RealmResults<RealmRoomMessage>> changeListener;
    private int changeSize = 0;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;
        if (G.twoPaneMode) {
            fragmentMediaPlayerBinding = DataBindingUtil.inflate(inflater, R.layout.activity_media_player, container, false);
            return fragmentMediaPlayerBinding.getRoot();
        } else {
            if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activityMediaPlayerLandBinding = DataBindingUtil.inflate(inflater, R.layout.activity_media_player_land, container, false);
                return activityMediaPlayerLandBinding.getRoot();
            } else {
                fragmentMediaPlayerBinding = DataBindingUtil.inflate(inflater, R.layout.activity_media_player, container, false);
                return fragmentMediaPlayerBinding.getRoot();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getRootView().setBackgroundColor(Theme.getColor(Theme.key_window_background));
        LinearLayout dragView = view.findViewById(R.id.dragView);
        TextView ml_txt_music_info = view.findViewById(R.id.ml_txt_music_info);
        ml_txt_music_info.setTextColor(Theme.getColor(Theme.key_dark_gray));
        dragView.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(context, R.drawable.bottom_sheet_background), context, Theme.getColor(Theme.key_theme_color)));
        initDataBinding();

        MusicPlayer.isShowMediaPlayer = true;

        if (MusicPlayer.mp == null) {
            removeFromBaseFragment();
            return;
        }


        initComponent(view);
        MusicPlayer.onComplete = onComplete;
    }

    private void initDataBinding() {
        if (G.twoPaneMode) {

            fragmentMediaPlayerViewModel = new FragmentMediaPlayerViewModel(fragmentMediaPlayerBinding.getRoot());
            fragmentMediaPlayerBinding.setFragmentMediaPlayerViewModel(fragmentMediaPlayerViewModel);

        } else {
            if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                fragmentMediaPlayerViewModel = new FragmentMediaPlayerViewModel(activityMediaPlayerLandBinding.getRoot());
                activityMediaPlayerLandBinding.setFragmentMediaPlayerViewModel(fragmentMediaPlayerViewModel);

            } else {
                fragmentMediaPlayerViewModel = new FragmentMediaPlayerViewModel(fragmentMediaPlayerBinding.getRoot());
                fragmentMediaPlayerBinding.setFragmentMediaPlayerViewModel(fragmentMediaPlayerViewModel);
            }
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

        canUpdateAfterDownload = true;
        fragmentMediaPlayerViewModel.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        canUpdateAfterDownload = false;
        fragmentMediaPlayerViewModel.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        try {
            if (!G.twoPaneMode) {
                if (getActivity() != null && isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
                }
            }
        } catch (Exception e) {
            Log.e("ddddd", "FragmentMediaPlayer  onConfigurationChanged  " + e.toString());
        }


        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicPlayer.onComplete = null;
    }

    //*****************************************************************************************


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initComponent(final View view) {

        AppCompatTextView ml_txt_timer = view.findViewById(R.id.ml_txt_timer);
        ml_txt_timer.setTextColor(Theme.getColor(Theme.key_light_theme_color));

        View ml_splitter_timers = view.findViewById(R.id.ml_splitter_timers);
        ml_splitter_timers.setBackgroundColor(Theme.getColor(Theme.key_light_theme_color));


        AppCompatTextView ml_txt_music_time = view.findViewById(R.id.ml_txt_timer);
        ml_txt_music_time.setTextColor(Theme.getColor(Theme.key_light_theme_color));

        MaterialDesignTextView ml_btn_play_music = view.findViewById(R.id.ml_btn_play_music);
        ml_btn_play_music.setTextColor(Theme.getColor(Theme.key_light_theme_color));

        final ImageView img_MusicImage = view.findViewById(R.id.ml_img_music_picture);
        onSetImage = new OnSetImage() {
            @Override
            public void setImage() {
                img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
            }
        };

        musicSeekbar = view.findViewById(R.id.ml_seekBar1);
        musicSeekbar.setThumbTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_light_theme_color)));
        musicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) MusicPlayer.setMusicProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicPlayer.pauseSound();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicPlayer.playAndPause();
            }
        });

        onBackFragment = new OnBackFragment() {
            @Override
            public void onBack() {
                removeFromBaseFragment();
            }
        };

        rcvListMusicPlayer = view.findViewById(R.id.rcvListMusicPlayer);
        final SlidingUpPanelLayout slidingUpPanelLayout = view.findViewById(R.id.sliding_layout);

        footerAdapter = new ItemAdapter<>();
        fastItemAdapter = new FastItemAdapter();
        fastItemAdapter.addAdapter(1, footerAdapter);

        rcvListMusicPlayer.setAdapter(fastItemAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_mActivity);
        rcvListMusicPlayer.setLayoutManager(linearLayoutManager);
        rcvListMusicPlayer.setHasFixedSize(true);

        rcvListMusicPlayer.addOnScrollListener(new EndlessRecyclerOnScrollListener(footerAdapter) {
            @Override
            public void onLoadMore(int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                // Load your items here and add it to FastAdapter

                if (isThereAnyMoreItemToLoad) {
                    getInfoRealm();
                }
            }
        });


//        getDataFromServer(ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO);
        loadLocalData(ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO, ProtoGlobal.RoomMessageType.AUDIO);

        for (MessageObject r : MusicPlayer.mediaList) {

            fastItemAdapter.add(new AdapterListMusicPlayer().setItem(r).withIdentifier(r.id));

        }
        rcvListMusicPlayer.scrollToPosition(fastItemAdapter.getPosition(Long.parseLong(MusicPlayer.messageId)));
    }

    public interface OnBackFragment {
        void onBack();
    }


    public interface OnSetImage {
        void setImage();
    }

    public class AdapterListMusicPlayer extends AbstractItem<AdapterListMusicPlayer, AdapterListMusicPlayer.ViewHolder> {

        private MessageObject messageRoomMessagesList;

        public AdapterListMusicPlayer() {

        }

        public AdapterListMusicPlayer setItem(MessageObject realmRoomMessages) {
            messageRoomMessagesList = realmRoomMessages;
            return this;
        }

        //The unique ID for this type of item
        @Override
        public int getType() {
            return R.id.rootListMusicPlayer;
        }

        //The layout to be used for this type of item
        @Override
        public int getLayoutRes() {
            return R.layout.adapter_list_music_player;
        }

        //The logic to bind your data to the view

        @Override
        public void bindView(ViewHolder holder, List payloads) {
            super.bindView(holder, payloads);

            holder.txtNameMusic.setText(messageRoomMessagesList.attachment.name);
            if (messageRoomMessagesList.attachment.isFileExistsOnLocal(messageRoomMessagesList)) {

                holder.iconPlay.setVisibility(View.VISIBLE);
                holder.messageProgress.setVisibility(View.GONE);

                if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying() && Long.parseLong(MusicPlayer.messageId) == (messageRoomMessagesList.id)) {
                    holder.iconPlay.setText(R.string.icon_pause);
                } else {
                    holder.iconPlay.setText(R.string.icon_play);
                }
                //holder.txtNameMusic.setText(realmRoomMessagesList.getAttachment().getName());
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                if (artist != null) {
                    holder.txtMusicplace.setText(artist);
                } else {
                    holder.txtMusicplace.setText(G.context.getString(R.string.unknown_artist));
                }
            } else {

                if (messageRoomMessagesList.getAttachment() != null) {
                    holder.messageProgress.setTag(messageRoomMessagesList.id);
                    holder.messageProgress.withDrawable(R.drawable.ic_download, true);
                    holder.iconPlay.setVisibility(View.GONE);

                    if (getDownloader().isDownloading(MusicPlayer.mediaList.get(MusicPlayer.selectedMedia).attachment.cacheId)) {
                        startDownload(MusicPlayer.selectedMedia, holder.messageProgress);
                    }
                }
            }

            holder.messageProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemView.performClick();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!messageRoomMessagesList.attachment.isFileExistsOnLocal(messageRoomMessagesList)) {
                        downloadFile(holder.getAdapterPosition(), holder.messageProgress);
                    } else {
                        if (MusicPlayer.musicName.equals(MusicPlayer.mediaList.get(holder.getAdapterPosition()).attachment.name)) {
                            MusicPlayer.playAndPause();
                        } else {
                            MusicPlayer.startPlayer(MusicPlayer.mediaList.get(holder.getAdapterPosition()).attachment.name, MusicPlayer.mediaList.get(holder.getAdapterPosition()).attachment.filePath, FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, false, MusicPlayer.mediaList.get(holder.getAdapterPosition()).id + "");
                        }
                    }

                }
            });
        }

        @Override
        public ViewHolder getViewHolder(View v) {
            return new ViewHolder(v);
        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtNameMusic, txtMusicplace, iconPlay;
            public MessageProgress messageProgress;
            private ViewGroup root;

            public ViewHolder(View view) {
                super(view);
                txtNameMusic = itemView.findViewById(R.id.txtListMusicPlayer);
                txtNameMusic.setTextColor(Theme.getColor(Theme.key_title_text));
                txtMusicplace = itemView.findViewById(R.id.ml_txt_music_place);
                txtMusicplace.setTextColor(Theme.getColor(Theme.key_subtitle_text));
                iconPlay = itemView.findViewById(R.id.ml_btn_play_music);
                iconPlay.setTextColor(Theme.getColor(Theme.key_light_theme_color));
                root = itemView.findViewById(R.id.rootViewMuciPlayer);

                messageProgress = (MessageProgress) itemView.findViewById(R.id.progress);
                AppUtils.setProgresColor(messageProgress.progressBar);
            }
        }


    }

    private void getDataFromServer(final ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        offset = 0;
        nextMessageId = 0;
        nextDocumentId = 0;

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, final int notDeletedCount, final List<ProtoGlobal.RoomMessage> resultList, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter identity) {

                if (resultList.size() > 0) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            saveDataToLocal(resultList, MusicPlayer.roomId);

                            nextMessageId = resultList.get(0).getMessageId();
                            nextDocumentId = resultList.get(0).getDocumentId();

                            isThereAnyMoreItemToLoad = true;

                            int deletedCount = 0;
                            for (int i = 0; i < resultList.size(); i++) {
                                if (resultList.get(i).getDeleted()) {
                                    deletedCount++;
                                }
                            }

                            offset += resultList.size() - deletedCount;
                        }
                    }).start();
                }
            }

            @Override
            public void onTimeOut() {
                footerAdapter.clear();
            }

            @Override
            public void onError(final int majorCode, int minorCode, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter identity) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (majorCode == 620) {

                            isThereAnyMoreItemToLoad = false;
                        }
                        footerAdapter.clear();
                    }
                });
            }
        };

//        new RequestClientSearchRoomHistory().clientSearchRoomHistory(MusicPlayer.roomId, nextMessageId, filter);
    }

    public void saveDataToLocal(final List<ProtoGlobal.RoomMessage> RoomMessages, final long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            for (final ProtoGlobal.RoomMessage roomMessage : RoomMessages) {
                RealmRoomMessage.putOrUpdate(realm, roomId, roomMessage, new StructMessageOption().setFromShareMedia());
            }
        });
    }

    private RealmResults<RealmRoomMessage> loadLocalData(ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter, ProtoGlobal.RoomMessageType type) {

        if (mRealmList != null) {
            mRealmList.removeAllChangeListeners();
        }

        mRealmList = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRoomMessage.filterMessage(realm, MusicPlayer.roomId, type);
        });

        changeSize = mRealmList.size();

        setListener();
        isThereAnyMoreItemToLoad = true;
        getDataFromServer(filter);
        return mRealmList;
    }

    private void downloadFile(int position, MessageProgress messageProgress) {

        if (getDownloader().isDownloading(MusicPlayer.mediaList.get(position).attachment.cacheId)) {
            stopDownload(position, messageProgress);
        } else {
            startDownload(position, messageProgress);
        }
    }

    private void stopDownload(int position, final MessageProgress messageProgress) {

        getDownloader().cancelDownload(MusicPlayer.mediaList.get(position).attachment.cacheId);
    }

    private void startDownload(final int position, final MessageProgress messageProgress) {


        messageProgress.withDrawable(R.drawable.ic_cancel, true);

        final AttachmentObject at = MusicPlayer.mediaList.get(position).forwardedMessage != null ? MusicPlayer.mediaList.get(position).forwardedMessage.attachment : MusicPlayer.mediaList.get(position).attachment;
        int messageType = MusicPlayer.mediaList.get(position).forwardedMessage != null ? MusicPlayer.mediaList.get(position).forwardedMessage.messageType : MusicPlayer.mediaList.get(position).messageType;

        String dirPath = AndroidUtils.getFilePathWithCashId(at.cacheId, at.name, messageType);


        messageProgress.withOnProgress(new OnProgress() {
            @Override
            public void onProgressFinished() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (messageProgress.getTag() != null && messageProgress.getTag().equals(MusicPlayer.mediaList.get(position).id)) {
                            messageProgress.withProgress(0);
                            messageProgress.setVisibility(View.GONE);
                            updateViewAfterDownload(at.cacheId);
                        }
                    }
                });

            }
        });

        DownloadObject fileObject = DownloadObject.createForRoomMessage(MusicPlayer.mediaList.get(position));

        if (fileObject == null) {
            return;
        }

        getDownloader().download(fileObject, ProtoFileDownload.FileDownload.Selector.FILE, HttpRequest.PRIORITY.PRIORITY_HIGH, arg -> {
            if (canUpdateAfterDownload) {
                G.handler.post(() -> {
                    switch (arg.status) {
                        case SUCCESS:
                        case LOADING:
                            if (arg.data == null)
                                return;

                            if (messageProgress.getTag() != null && messageProgress.getTag().equals(MusicPlayer.mediaList.get(position).id)) {
                                messageProgress.withProgress(arg.data.getProgress());
                            }
                            break;
                        case ERROR:
                            if (messageProgress.getTag() != null && messageProgress.getTag().equals(MusicPlayer.mediaList.get(position).id)) {
                                messageProgress.withProgress(0);
                                messageProgress.withDrawable(R.drawable.ic_download, true);
                            }
                    }
                });
            }
        });
    }

    private void updateViewAfterDownload(String cashId) {
        for (int j = MusicPlayer.mediaList.size() - 1; j >= 0; j--) {
            try {
                if (MusicPlayer.mediaList.get(j) != null && !MusicPlayer.mediaList.get(j).deleted) {
                    String mCashId = MusicPlayer.mediaList.get(j).forwardedMessage != null ? MusicPlayer.mediaList.get(j).forwardedMessage.attachment.cacheId : MusicPlayer.mediaList.get(j).attachment.cacheId;
                    if (mCashId.equals(cashId)) {
                        needDownloadList.remove(MusicPlayer.mediaList.get(j).id);

                        final int finalJ = j;
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                rcvListMusicPlayer.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rcvListMusicPlayer.getAdapter().notifyItemChanged(finalJ);
                                    }
                                });
                            }
                        });
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void setListener() {
        changeListener = new RealmChangeListener<RealmResults<RealmRoomMessage>>() {
            @Override
            public void onChange(RealmResults<RealmRoomMessage> element) {
                getInfoRealm();
            }
        };

        if (changeListener != null) {
            mRealmList.addChangeListener(changeListener);
        }
    }

    public void getInfoRealm() {
        if (MusicPlayer.mediaList.size() != 0) {
            changeListener = null;
            List<RealmRoomMessage> realmRoomMessages = null;

            try {
                realmRoomMessages = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.where(RealmRoomMessage.class)
                            .equalTo("roomId", MusicPlayer.roomId)
                            .notEqualTo("deleted", true)
                            .contains("messageType", ProtoGlobal.RoomMessageType.AUDIO.toString())
                            .lessThan("messageId", MusicPlayer.mediaList.get(MusicPlayer.mediaList.size() - 1).id)
                            .findAll().sort("messageId", Sort.DESCENDING);
                });
            } catch (IllegalStateException e) {
            }

            if (realmRoomMessages != null && realmRoomMessages.size() > 0) {

//                                mRealmList = RealmRoomMessage.filterMessage(getUiRealm(), MusicPlayer.roomId, ProtoGlobal.RoomMessageType.AUDIO);
                if (realmRoomMessages.size() > MusicPlayer.limitMediaList) {
                    realmRoomMessages = realmRoomMessages.subList(0, MusicPlayer.limitMediaList);
                } else {
                    realmRoomMessages = realmRoomMessages.subList(0, realmRoomMessages.size());
                }

                footerAdapter.clear();
                for (RealmRoomMessage r : realmRoomMessages) {
                    MessageObject messageObject = MessageObject.create(r);
                    if (messageObject.attachment.isFileExistsOnLocal(messageObject)) {
                        MusicPlayer.mediaList.add(messageObject);
                    }
                    fastItemAdapter.add(new AdapterListMusicPlayer().setItem(messageObject).withIdentifier(r.getMessageId()));
                }

            } else {
                if (isThereAnyMoreItemToLoad)
                    new RequestClientSearchRoomHistory().clientSearchRoomHistory(MusicPlayer.roomId, nextMessageId,nextDocumentId, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO);

            }
        }
    }

}
