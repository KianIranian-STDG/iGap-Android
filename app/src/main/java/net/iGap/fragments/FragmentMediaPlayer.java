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

import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.iGap.module.accountManager.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityMediaPlayerBinding;
import net.iGap.databinding.ActivityMediaPlayerLandBinding;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.observers.interfaces.OnClientSearchRoomHistory;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.proto.ProtoClientSearchRoomHistory;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestClientSearchRoomHistory;
import net.iGap.viewmodel.FragmentMediaPlayerViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class FragmentMediaPlayer extends BaseFragment {


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
    private boolean isThereAnyMoreItemToLoad = false;
    private int offset;
    private RealmResults<RealmRoomMessage> mRealmList;
    private ArrayList<RealmRoomMessage> mediaList;
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

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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


    private void initComponent(final View view) {

        final ImageView img_MusicImage = view.findViewById(R.id.ml_img_music_picture);
        onSetImage = new OnSetImage() {
            @Override
            public void setImage() {
                img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
            }
        };

        musicSeekbar = view.findViewById(R.id.ml_seekBar1);

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
        mediaList = new ArrayList<>();

        for (RealmRoomMessage r : MusicPlayer.mediaList) {
            if (r.isValid()) {
                fastItemAdapter.add(new AdapterListMusicPlayer().setItem(r).withIdentifier(r.getMessageId()));
            }
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

        private RealmRoomMessage realmRoomMessagesList;

        public AdapterListMusicPlayer() {

        }

        public AdapterListMusicPlayer setItem(RealmRoomMessage realmRoomMessages) {
            realmRoomMessagesList = realmRoomMessages;
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

            holder.txtNameMusic.setText(realmRoomMessagesList.getAttachment().getName());
            if (realmRoomMessagesList.getAttachment().fileExistsOnLocal()) {

                holder.iconPlay.setVisibility(View.VISIBLE);
                holder.messageProgress.setVisibility(View.GONE);

                if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying() && Long.parseLong(MusicPlayer.messageId) == (realmRoomMessagesList.getMessageId())) {
                    holder.iconPlay.setText(R.string.pause_icon);
                } else {
                    holder.iconPlay.setText(R.string.play_icon);
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

                if (realmRoomMessagesList.getAttachment() != null) {
                    holder.messageProgress.setTag(realmRoomMessagesList.getMessageId());
                    holder.messageProgress.withDrawable(R.drawable.ic_download, true);
                    holder.iconPlay.setVisibility(View.GONE);

                    if (HelperDownloadFile.getInstance().isDownLoading(MusicPlayer.mediaList.get(holder.getAdapterPosition()).getAttachment().getCacheId())) {
                        startDownload(holder.getAdapterPosition(), holder.messageProgress);
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
                    if (!realmRoomMessagesList.getAttachment().fileExistsOnLocal()) {
                        downloadFile(holder.getAdapterPosition(), holder.messageProgress);
                    } else {
                        if (MusicPlayer.musicName.equals(MusicPlayer.mediaList.get(holder.getAdapterPosition()).getAttachment().getName())) {
                            MusicPlayer.playAndPause();
                        } else {
                            MusicPlayer.startPlayer(MusicPlayer.mediaList.get(holder.getAdapterPosition()).getAttachment().getName(), MusicPlayer.mediaList.get(holder.getAdapterPosition()).getAttachment().getLocalFilePath(), FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, false, MusicPlayer.mediaList.get(holder.getAdapterPosition()).getMessageId() + "");
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
                txtMusicplace = itemView.findViewById(R.id.ml_txt_music_place);
                iconPlay = itemView.findViewById(R.id.ml_btn_play_music);
                root = itemView.findViewById(R.id.rootViewMuciPlayer);

                messageProgress = (MessageProgress) itemView.findViewById(R.id.progress);
                AppUtils.setProgresColor(messageProgress.progressBar);
            }
        }


    }

    private void getDataFromServer(final ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter filter) {

        offset = 0;
        nextMessageId = 0;

        G.onClientSearchRoomHistory = new OnClientSearchRoomHistory() {
            @Override
            public void onClientSearchRoomHistory(int totalCount, final int notDeletedCount, final List<ProtoGlobal.RoomMessage> resultList, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter identity) {

                if (resultList.size() > 0) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            saveDataToLocal(resultList, MusicPlayer.roomId);

                            nextMessageId = resultList.get(0).getMessageId();

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

        if (HelperDownloadFile.getInstance().isDownLoading(MusicPlayer.mediaList.get(position).getAttachment().getCacheId())) {
            stopDownload(position, messageProgress);
        } else {
            startDownload(position, messageProgress);
        }
    }

    private void stopDownload(int position, final MessageProgress messageProgress) {

        HelperDownloadFile.getInstance().stopDownLoad(MusicPlayer.mediaList.get(position).getAttachment().getCacheId());
    }

    private void startDownload(final int position, final MessageProgress messageProgress) {


        messageProgress.withDrawable(R.drawable.ic_cancel, true);

        final RealmAttachment at = MusicPlayer.mediaList.get(position).getForwardMessage() != null ? MusicPlayer.mediaList.get(position).getForwardMessage().getAttachment() : MusicPlayer.mediaList.get(position).getAttachment();
        ProtoGlobal.RoomMessageType messageType = MusicPlayer.mediaList.get(position).getForwardMessage() != null ? MusicPlayer.mediaList.get(position).getForwardMessage().getMessageType() : MusicPlayer.mediaList.get(position).getMessageType();

        String dirPath = AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), messageType);


        messageProgress.withOnProgress(new OnProgress() {
            @Override
            public void onProgressFinished() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (messageProgress.getTag() != null && messageProgress.getTag().equals(MusicPlayer.mediaList.get(position).getMessageId())) {
                            messageProgress.withProgress(0);
                            messageProgress.setVisibility(View.GONE);
                            updateViewAfterDownload(at.getCacheId());
                        }
                    }
                });

            }
        });


        HelperDownloadFile.getInstance().startDownload(messageType, MusicPlayer.mediaList.get(position).getMessageId() + "", at.getToken(), at.getUrl(), at.getCacheId(), at.getName(), at.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 2, new HelperDownloadFile.UpdateListener() {
            @Override
            public void OnProgress(String path, final int progress) {

                if (canUpdateAfterDownload) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (messageProgress.getTag() != null && messageProgress.getTag().equals(MusicPlayer.mediaList.get(position).getMessageId())) {

                                G.currentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageProgress.withProgress(progress);
                                    }
                                });
                            }
                        }
                    });

                }
            }

            @Override
            public void OnError(String token) {
                if (canUpdateAfterDownload) {

                    if (messageProgress.getTag() != null && messageProgress.getTag().equals(MusicPlayer.mediaList.get(position).getMessageId())) {
                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageProgress.withProgress(0);
                                messageProgress.withDrawable(R.drawable.ic_download, true);
                            }
                        });
                    }
                }
            }
        });
    }

    private void updateViewAfterDownload(String cashId) {
        for (int j = MusicPlayer.mediaList.size() - 1; j >= 0; j--) {
            try {
                if (MusicPlayer.mediaList.get(j) != null && MusicPlayer.mediaList.get(j).isValid() && !MusicPlayer.mediaList.get(j).isDeleted()) {
                    String mCashId = MusicPlayer.mediaList.get(j).getForwardMessage() != null ? MusicPlayer.mediaList.get(j).getForwardMessage().getAttachment().getCacheId() : MusicPlayer.mediaList.get(j).getAttachment().getCacheId();
                    if (mCashId.equals(cashId)) {
                        needDownloadList.remove(MusicPlayer.mediaList.get(j).getMessageId());

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

        changeListener = null;
        List<RealmRoomMessage> realmRoomMessages = null;

        try {
            realmRoomMessages = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoomMessage.class)
                        .equalTo(RealmRoomMessageFields.ROOM_ID, MusicPlayer.roomId)
                        .notEqualTo(RealmRoomMessageFields.DELETED, true)
                        .contains(RealmRoomMessageFields.MESSAGE_TYPE, ProtoGlobal.RoomMessageType.AUDIO.toString())
                        .lessThan(RealmRoomMessageFields.MESSAGE_ID, MusicPlayer.mediaList.get(MusicPlayer.mediaList.size() - 1).getMessageId())
                        .findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
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
                MusicPlayer.mediaList.add(r);
                fastItemAdapter.add(new AdapterListMusicPlayer().setItem(r).withIdentifier(r.getMessageId()));
            }

        } else {
            if (isThereAnyMoreItemToLoad)
                new RequestClientSearchRoomHistory().clientSearchRoomHistory(MusicPlayer.roomId, nextMessageId, ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter.AUDIO);

        }

    }

}
