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

import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.protobuf.AbstractMessage;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.RoomObject;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.libs.swipeback.VerticalSwipeBackLayout;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.downloader.HttpRequest;
import net.iGap.module.downloader.IDownloader;
import net.iGap.module.downloader.Status;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmConstants;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Sort;

public class FragmentShowContent extends Fragment implements ShowMediaListener, EventManager.EventDelegate {
    private TextView contentNumberTv;
    private ViewPager2 viewPager;
    private ShowContentAdapter mAdapter;
    private int selectedFile = 0;
    private int lastOrientation = 0;
    private RealmRoom room;
    private VerticalSwipeBackLayout mVerticalSwipeBackLayout;
    private SimpleExoPlayer exoPlayer;
    private DefaultDataSourceFactory dataSourceFactory;
    private SparseArrayCompat<WeakReference<PlayerView>> videos;
    private ViewPager2.OnPageChangeCallback viewPagerListener;
    private boolean initialViewPager = true;
    private ImageButton imgPlay;
    private LinearLayout toolbarLl;
    private int itemPosition = 0;

    public static FragmentShowContent newInstance() {
        return new FragmentShowContent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVerticalSwipeBackLayout = new VerticalSwipeBackLayout(getActivity());
        exoPlayer = new SimpleExoPlayer.Builder(getActivity()).build();
        dataSourceFactory = new DefaultDataSourceFactory(getActivity());
        videos = new SparseArrayCompat<>();
        setPlayerListener();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_content, container, false);
        View fragmentView = mVerticalSwipeBackLayout.setFragment(this, view);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<RealmRoomMessage> roomMessages = getRoomMediaMessages(this.getArguments());
        if (roomMessages != null && roomMessages.size() > 0) {
            initComponent(view, roomMessages);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.ON_NEW_MEDIA_MESSAGE_RECEIVED, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.ON_NEW_MEDIA_MESSAGE_RECEIVED, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != lastOrientation) {
            lastOrientation = newConfig.orientation;
        }
    }

    private void setPlayerListener() {
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                switch (state) {
                    case Player.STATE_ENDED:
                        exoPlayer.seekTo(0);
                        exoPlayer.setPlayWhenReady(false);
                        break;
                    default:
                        // nothing
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                imgPlay.setActivated(isPlaying);
                if (isPlaying) {
                    if (MusicPlayer.isMusicPlyerEnable) {
                        if (MusicPlayer.mp.isPlaying()) {
                            MusicPlayer.playAndPause();
                        }
                    }
                }
            }
        });
    }

    private List<RealmRoomMessage> getRoomMediaMessages(Bundle bundle) {

        if (bundle != null) {
            long mRoomId = bundle.getLong(RealmConstants.REALM_ROOM_ID, 0L);
            long selectedFileToken = bundle.getLong(RealmConstants.REALM_SELECTED_IMAGE);

            String[] messageTypeImageVideo = new String[]{ProtoGlobal.RoomMessageType.VIDEO.toString(), ProtoGlobal.RoomMessageType.VIDEO_TEXT.toString()
                    , ProtoGlobal.RoomMessageType.IMAGE.toString(), ProtoGlobal.RoomMessageType.IMAGE_TEXT.toString()};
            List<RealmRoomMessage> realmRoomMessages = DbManager.getInstance().doRealmTask(realm -> {
                return realm.copyFromRealm(realm.where(RealmRoomMessage.class)
                        .equalTo(RealmConstants.REALM_ROOM_ID, mRoomId)
                        .in(RealmConstants.REALM_MESSAGE_TYPE, messageTypeImageVideo)
                        .or()
                        //get forwarded messages
                        .equalTo(RealmConstants.REALM_ROOM_ID, mRoomId)
                        .isNotNull(RealmConstants.REALM_FORWARD_MESSAGE)
                        .findAll()
                        .sort(RealmConstants.REALM_UPDATE_TIME, Sort.ASCENDING));
            });

            List messageTypesList = Arrays.asList(messageTypeImageVideo);
            ArrayList<RealmRoomMessage> roomMessagesImageVideo = new ArrayList<>(realmRoomMessages);
            for (RealmRoomMessage roomMessageObj : realmRoomMessages) {
                if (roomMessageObj.forwardMessage != null && !messageTypesList.contains(roomMessageObj.forwardMessage.messageType)) {
                    roomMessagesImageVideo.remove(roomMessageObj);
                }
            }

            for (int i = roomMessagesImageVideo.size() - 1; i >= 0; i--) {
                if (selectedFileToken == roomMessagesImageVideo.get(i).messageId) {
                    selectedFile = i;
                    break;
                }
            }

            return roomMessagesImageVideo;
        } else {
            return null;
        }
    }

    private void initComponent(View view, List<RealmRoomMessage> roomMessages) {
        RippleView rippleBackBtn = view.findViewById(R.id.asi_ripple_back);
        rippleBackBtn.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.getSupportFragmentManager().popBackStackImmediate();
            }
        });
        RippleView rippleMenu = view.findViewById(R.id.asi_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(rippleView -> popUpMenuTopSheet(roomMessages.get(selectedFile)));

        imgPlay = view.findViewById(R.id.imgPlay);
        viewPager = view.findViewById(R.id.asi_view_pager);
        contentNumberTv = view.findViewById(R.id.asi_txt_image_number);
        toolbarLl = view.findViewById(R.id.toolbarShowContent);


        room = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", roomMessages.get(selectedFile).roomId).findFirst();
        });

        imgPlay.setOnClickListener(v -> {
            if (exoPlayer.isPlaying()) {
                exoPlayer.pause();
            } else {
                exoPlayer.setPlayWhenReady(true);
            }
        });

        initViewPager(roomMessages);
    }

    private void initViewPager(List<RealmRoomMessage> roomMessages) {
        mAdapter = new ShowContentAdapter(this);
        mAdapter.setRoomMessages(roomMessages);
        setViewPagerListener(roomMessages);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(selectedFile, false);
        viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);

        if (selectedFile >= roomMessages.size()) {
            return;
        }
    }

    public void setViewPagerListener(List<RealmRoomMessage> roomMessages) {

        this.viewPagerListener = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                selectedFile = position;
                contentNumberTv.setText(MessageFormat.format("{0} {1} {2}", position + 1, G.fragmentActivity.getResources().getString(R.string.of), roomMessages.size()));

                if (HelperCalander.isPersianUnicode) {
                    contentNumberTv.setText(HelperCalander.convertToUnicodeFarsiNumber(contentNumberTv.getText().toString()));
                }

                if (exoPlayer != null) {
                    exoPlayer.seekTo(0);
                    exoPlayer.setPlayWhenReady(false);
                }
                imgPlay.setVisibility(View.GONE);
                toolbarLl.setVisibility(View.GONE);
                itemPosition = position;

                try {
                    WeakReference<PlayerView> weakPlayerView = videos.get(position);
                    if (weakPlayerView != null) {
                        MessageObject messageObject = MessageObject.create(roomMessages.get(position));
                        MessageObject finalMessageObject = RealmRoomMessage.getFinalMessage(messageObject);
                        String path = getFilePath(finalMessageObject);
                        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(path));
                        exoPlayer.prepare(mediaSource);
                        weakPlayerView.get().setPlayer(null);
                        weakPlayerView.get().setPlayer(exoPlayer);
                        weakPlayerView.get().hideController();
                        exoPlayer.setPlayWhenReady(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        viewPager.registerOnPageChangeCallback(viewPagerListener);
    }

    public void popUpMenuTopSheet(RealmRoomMessage roomMessage) {
        List<Integer> items = new ArrayList<>();
        items.add(R.string.save_to_gallery);
        MessageObject messageObject = MessageObject.create(roomMessage);
        ProtoGlobal.RoomMessageType messageType = ProtoGlobal.RoomMessageType.forNumber(messageObject.messageType);
        if (messageType == ProtoGlobal.RoomMessageType.VIDEO || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
            items.add(R.string.share_video_file);
        } else {
            items.add(R.string.share_image);
        }
        if (RoomObject.isRoomPublic(room)) {
            if (MessageObject.canSharePublic(messageObject)) {
                items.add(R.string.share_file_link);
            }
        }
        new TopSheetDialog(getContext()).setListDataWithResourceId(items, -1, position -> {
            if (items.get(position) == R.string.save_to_gallery) {
                saveToGallery(messageObject);
            } else if (items.get(position) == R.string.share_file_link) {
                shareMediaLink(messageObject);
            } else {
                shareContent(messageObject);
            }
        }).show();
    }

    private void shareMediaLink(MessageObject messageObject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (messageObject != null) {
            intent.putExtra(Intent.EXTRA_TEXT, messageObject.attachment.publicUrl);
        }
        startActivity(Intent.createChooser(intent, G.context.getString(R.string.share_link_item_dialog)));
    }

    private void saveToGallery(MessageObject messageObject) {
        if (messageObject != null) {
            String path = getFilePath(messageObject);
            String extension = "";
            if (path.contains(".")) {
                extension = path.substring(path.lastIndexOf("."));
            }
            int messageType;
            if (messageObject.forwardedMessage != null) {
                messageType = messageObject.forwardedMessage.messageType;
            } else {
                messageType = messageObject.messageType;
            }
            File file = new File(path);
            if (file.exists()) {
                if (messageType == ProtoGlobal.RoomMessageType.VIDEO_VALUE || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT_VALUE) {
                    HelperSaveFile.saveFileToDownLoadFolder(path, "VIDEO_" + System.currentTimeMillis() + extension, HelperSaveFile.FolderType.video);
                } else if (messageType == ProtoGlobal.RoomMessageType.IMAGE_VALUE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT_VALUE) {
                    HelperSaveFile.savePicToGallery(path, true);
                }
            }
        }
    }

    public String getFilePath(MessageObject messageObject) {
        String result = "";
        AttachmentObject at = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.attachment : messageObject.attachment;
        if (at != null) {
            if (at.filePath != null) result = at.filePath;
        }
        int messageType = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.messageType : messageObject.messageType;
        if (result.length() < 1) {
            String mimeType = DownloadObject.extractMime(at.name);
            String path = AndroidUtils.suitableAppFilePath(ProtoGlobal.RoomMessageType.forNumber(messageType));
            result = new File(path + "/" + at.cacheId + "_" + mimeType).getAbsolutePath();
            return result;
        }
        return result;
    }

    public String getThumbnailPath(MessageObject roomMessage) {
        String result = "";
        if (roomMessage == null) {
            return "";
        }
        if (roomMessage.getAttachment() != null) {
            if (roomMessage.attachment.thumbnailPath != null) {
                result = roomMessage.attachment.thumbnailPath;
            }
            if (result.length() < 1) {
                result = AndroidUtils.getFilePathWithCashId(roomMessage.attachment.cacheId, roomMessage.attachment.name, G.DIR_TEMP, true);
            }
        }
        return result;
    }

    @Override
    public void onDestroy() {
        if (exoPlayer != null)
            exoPlayer.release();
        exoPlayer = null;
        super.onDestroy();

        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void videoAttached(WeakReference<PlayerView> playerView, int position, boolean callPageSelected) {
        videos.put(position, playerView);
        if (initialViewPager || callPageSelected) {
            viewPagerListener.onPageSelected(position);
            initialViewPager = false;
        }
    }

    @Override
    public void setToolbarVisibility(int state) {
        toolbarLl.setVisibility(state);
    }

    private class ShowContentAdapter extends RecyclerView.Adapter<ShowContentAdapter.ViewHolder> {
        private List<RealmRoomMessage> roomMessages = new ArrayList<>();
        private ShowMediaListener mShowContentListener;

        ShowContentAdapter(ShowMediaListener showContentListener) {
            mShowContentListener = showContentListener;
        }

        public void setRoomMessages(List<RealmRoomMessage> roomMessages) {
            this.roomMessages = roomMessages;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_content_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(roomMessages, position);
        }

        @Override
        public int getItemCount() {
            return roomMessages.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final PhotoView zoomableImageView;
            private final PlayerView playerView;
            private final MessageProgress progress;
            private final TextView nameTv;
            private final TextView dateTv;
            private final TextView timeTv;
            private final TextView descriptionTv;
            private final TextView readMore;
            private final LinearLayout readMoreRoot;
            private final ConstraintLayout mediaInfoCl;
            private ProtoGlobal.RoomMessageType messageType;

            private final static int MAX_LINES_COLLAPSED = 8;
            private final boolean INITIAL_IS_COLLAPSED = true;

            private static final int IDLE_ANIMATION_STATE = 1;
            private static final int EXPANDING_ANIMATION_STATE = 2;
            private static final int COLLAPSING_ANIMATION_STATE = 8;
            private boolean readMoreFlag = false;

            private int mCurrentAnimationState = IDLE_ANIMATION_STATE;

            private boolean isCollapsed = INITIAL_IS_COLLAPSED;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                zoomableImageView = itemView.findViewById(R.id.showContentImageView);

                progress = itemView.findViewById(R.id.progress);
                AppUtils.setProgresColor(progress.progressBar);

                nameTv = itemView.findViewById(R.id.asi_txt_image_name);
                dateTv = itemView.findViewById(R.id.asi_txt_image_date);
                timeTv = itemView.findViewById(R.id.asi_txt_image_time);
                descriptionTv = itemView.findViewById(R.id.asi_txt_image_desc);
                readMore = itemView.findViewById(R.id.asi_txt_image_readMore);
                mediaInfoCl = itemView.findViewById(R.id.asi_layout_image_name);
                playerView = itemView.findViewById(R.id.showContentPlayerView);
                readMoreRoot = itemView.findViewById(R.id.readMore_root);
            }

            void bind(List<RealmRoomMessage> roomMessages, int position) {
                MessageObject message = MessageObject.create(roomMessages.get(position));
                final MessageObject messageObject = RealmRoomMessage.getFinalMessage(message);
                if (messageObject != null) {
                    if (messageObject.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT_VALUE || messageObject.messageType == ProtoGlobal.RoomMessageType.VIDEO_VALUE) {
                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    } else {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                    if (HelperDownloadFile.getInstance().isDownLoading(messageObject.getAttachment().cacheId)) {
                        progress.withDrawable(R.drawable.ic_cancel, true);
                        startDownload(position, progress, zoomableImageView, roomMessages);
                    } else {
                        progress.withDrawable(R.drawable.ic_download, true);
                    }

                    String path = getFilePath(messageObject);
                    File file = new File(path);
                    if (file.exists()) {
                        progress.setVisibility(View.GONE);
                        ImageLoadingServiceInjector.inject().loadImage(zoomableImageView, path, true);

                        if (messageObject.messageType == ProtoGlobal.RoomMessageType.IMAGE_VALUE || messageObject.messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT_VALUE) {
                            if (itemPosition == position) {
                                imgPlay.setVisibility(View.GONE);
                            }
                            showImageView();
                        } else {
                            if (itemPosition == position) {
                                imgPlay.setVisibility(View.VISIBLE);
                            }
                            showPlayerView();
                            mShowContentListener.videoAttached(new WeakReference(playerView), position, false);
                        }
                    } else {
                        showImageView();
                        path = getThumbnailPath(messageObject);
                        file = new File(path);
                        if (file.exists()) {
                            ImageLoadingServiceInjector.inject().loadImage(zoomableImageView, path);
                        } else if (messageObject.getAttachment() != null) {
                            // if thumbnail does not exist then download it
                            ProtoFileDownload.FileDownload.Selector selector = null;
                            long fileSize = 0;
                            boolean big = false;

                            if (messageObject.attachment.smallThumbnail != null) {
                                selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                                fileSize = messageObject.attachment.smallThumbnail.size;
                                big = false;
                            } else if (messageObject.attachment.largeThumbnail != null) {
                                selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                                fileSize = messageObject.attachment.largeThumbnail.size;
                                big = true;
                            }
                            final String filePathTumpnail = AndroidUtils.getFilePathWithCashId(messageObject.attachment.cacheId, messageObject.attachment.name, G.DIR_TEMP, true);

                            if (selector != null && fileSize > 0) {
                                DownloadObject downloadObject = DownloadObject.createForThumb(messageObject.attachment, ProtoGlobal.RoomMessageType.IMAGE.getNumber(), big);
                                Downloader.getInstance(AccountManager.selectedAccount).download(downloadObject, arg -> {
                                    if (arg.status == Status.SUCCESS && arg.data != null) {
                                        String filepath = arg.data.getFilePath();
                                        String fileToken = arg.data.getToken();

                                        if (!(new File(filepath).exists())) {
                                            HelperLog.getInstance().setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                                        }


                                        DbManager.getInstance().doRealmTransaction(realm -> {
                                            for (RealmAvatar realmAvatar1 : realm.where(RealmAvatar.class).equalTo("file.token", fileToken).findAll()) {
                                                realmAvatar1.getFile().setLocalThumbnailPath(filepath);
                                            }
                                        });
                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ImageLoadingServiceInjector.inject().loadImage(zoomableImageView, filepath);
                                            }
                                        });
                                    }

                                });
                            }
                        }
                    }
                } else {
                    return;
                }
                // show content info
                if (messageObject.message != null && !messageObject.message.isEmpty()) {
                    descriptionTv.setText(EmojiManager.getInstance().replaceEmoji(messageObject.message, descriptionTv.getPaint().getFontMetricsInt()));
                    descriptionTv.setVisibility(View.VISIBLE);
                    if (descriptionTv.getText().length() > 250) {
                        readMoreRoot.setVisibility(View.VISIBLE);
                    }

                    String s = String.valueOf(EmojiManager.getInstance().replaceEmoji(messageObject.message, descriptionTv.getPaint().getFontMetricsInt()));
                    updateWithNewText(s);
                    applyLayoutTransition();
                    readMore.setMovementMethod(new ScrollingMovementMethod());
                } else {
                    descriptionTv.setVisibility(View.GONE);
                }
                RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
                    return RealmRegisteredInfo.getRegistrationInfo(realm, messageObject.userId);
                });
                if (realmRegisteredInfo != null) {
                    nameTv.setText(realmRegisteredInfo.getDisplayName());
                } else {
                    nameTv.setText("");
                }
                if (messageObject.authorHash != null && RealmUserInfo.getCurrentUserAuthorHash().equals(messageObject.authorHash)) {
                    nameTv.setText(R.string.you);
                }
                if (messageObject.updateTime != 0) {
                    timeTv.setText(HelperCalander.getClocktime(messageObject.updateTime, true));
                    dateTv.setText(HelperCalander.checkHijriAndReturnTime(messageObject.updateTime / 1000));
                }
                if (HelperCalander.isPersianUnicode) {
                    nameTv.setText(HelperCalander.convertToUnicodeFarsiNumber(nameTv.getText().toString()));
                    timeTv.setText(HelperCalander.convertToUnicodeFarsiNumber(timeTv.getText().toString()));
                    dateTv.setText(HelperCalander.convertToUnicodeFarsiNumber(dateTv.getText().toString()));
                }
                progress.setOnClickListener(view -> {
                    String _cashID = messageObject.forwardedMessage != null ? messageObject.forwardedMessage.attachment.cacheId : messageObject.attachment.cacheId;
                    int currentAccount = AccountManager.selectedAccount;
                    if (Downloader.getInstance(currentAccount).isDownloading(_cashID)) {
                        progress.withDrawable(R.drawable.ic_download, true);
                        Downloader.getInstance(currentAccount).cancelDownload(_cashID);
                    } else {
                        progress.withDrawable(R.drawable.ic_cancel, true);
                        startDownload(position, progress, zoomableImageView, roomMessages);
                    }
                });

                playerView.hideController();
                playerView.setControllerVisibilityListener(visibility -> {
                    if (itemPosition == position) {
                        imgPlay.setVisibility(visibility);
                    }
                    mShowContentListener.setToolbarVisibility(visibility);
                    mediaInfoCl.setVisibility(visibility);
                });
                readMore.setTypeface(ResourcesCompat.getFont(readMore.getContext(), R.font.main_font));
                zoomableImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaInfoCl.getVisibility() == View.VISIBLE) {
                            mediaInfoCl.setVisibility(View.GONE);
                            mShowContentListener.setToolbarVisibility(View.GONE);
                        } else {
                            mediaInfoCl.setVisibility(View.VISIBLE);
                            mShowContentListener.setToolbarVisibility(View.VISIBLE);
                        }
                    }
                });
                descriptionTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        descriptionTv.setVerticalScrollBarEnabled(false);
                        descriptionTv.setMovementMethod(null);
                        if (readMoreFlag) {
                            if (isRunning()) {
                                mediaInfoCl.setLayoutTransition(mediaInfoCl.getLayoutTransition());
                            }
                            if (isCollapsed) {
                                mCurrentAnimationState = EXPANDING_ANIMATION_STATE;
                                descriptionTv.setMaxLines(Integer.MAX_VALUE);
                                readMoreRoot.setVisibility(View.GONE);
                            } else {
                                readMoreRoot.setVisibility(View.VISIBLE);
                                mCurrentAnimationState = COLLAPSING_ANIMATION_STATE;
                                descriptionTv.setMaxLines(MAX_LINES_COLLAPSED);
                                descriptionTv.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        descriptionTv.setMaxLines(Integer.MAX_VALUE);
                                    }
                                });
                            }
                            isCollapsed = !isCollapsed;
                            readMoreFlag = false;
                        }

                    }
                });
                readMoreRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!readMoreFlag) {
                            descriptionTv.setVerticalScrollBarEnabled(true);
                            descriptionTv.setMovementMethod(new ScrollingMovementMethod());
                            if (isRunning()) {
                                mediaInfoCl.setLayoutTransition(mediaInfoCl.getLayoutTransition());
                            }
                            if (isCollapsed) {
                                mCurrentAnimationState = EXPANDING_ANIMATION_STATE;
                                descriptionTv.setMaxLines(Integer.MAX_VALUE);
                                readMoreRoot.setVisibility(View.GONE);
                            } else {
                                readMoreRoot.setVisibility(View.VISIBLE);
                                mCurrentAnimationState = COLLAPSING_ANIMATION_STATE;
                                descriptionTv.setMaxLines(MAX_LINES_COLLAPSED);
                                descriptionTv.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        descriptionTv.setMaxLines(Integer.MAX_VALUE);
                                    }
                                });
                            }
                            isCollapsed = !isCollapsed;
                            readMoreFlag = true;
                        }

                    }
                });

                if (isCollapsed) {
                    descriptionTv.setMaxLines(MAX_LINES_COLLAPSED);
                } else {
                    descriptionTv.setMaxLines(Integer.MAX_VALUE);
                }


            }

            private void startDownload(int position, final MessageProgress progress, final PhotoView ZoomableImageView, final List<RealmRoomMessage> roomMessages) {
                final MessageObject messageObject = RealmRoomMessage.getFinalMessage(MessageObject.create(roomMessages.get(position)));
                boolean isVideo = messageObject.messageType == ProtoGlobal.RoomMessageType.VIDEO_VALUE || messageObject.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT_VALUE;
                progress.withOnProgress(() -> G.currentActivity.runOnUiThread(() -> {
                    progress.withProgress(0);
                    progress.setVisibility(View.GONE);
                    ZoomableImageView.setZoomable(true);
                }));
                DownloadObject struct = DownloadObject.createForRoomMessage(messageObject);
                if (struct == null) {
                    return;
                }
                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;
                getDownloader().download(struct, selector, HttpRequest.PRIORITY.PRIORITY_HIGH, arg -> G.runOnUiThread(() -> {
                    if (arg.data == null) {
                        return;
                    }
                    switch (arg.status) {
                        case SUCCESS:
                            ImageLoadingServiceInjector.inject().loadImage(ZoomableImageView, arg.data.getFilePath());
                            ZoomableImageView.setZoomable(true);

                            RealmRoomMessage realmRoomMessage = roomMessages.get(position);
                            if (roomMessages.get(position).getForwardMessage() != null) {
                                realmRoomMessage = roomMessages.get(position).getForwardMessage();
                            }
                            realmRoomMessage.attachment.localFilePath = arg.data.getFilePath();
                            messageObject.attachment.filePath = arg.data.getFilePath();
                            realmRoomMessage.attachment.token = arg.data.getToken();
                            messageObject.attachment.token = arg.data.getToken();

                            G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_FILE_DOWNLOAD_COMPLETED, messageObject));
                            if (isVideo) {
                                mShowContentListener.videoAttached(new WeakReference(playerView), position, true);
                                playerView.setVisibility(View.VISIBLE);
                            }
                            break;
                        case LOADING:
                            progress.withProgress(arg.data.getProgress());
                            break;
                        case ERROR:
                            progress.withProgress(0);
                            progress.withDrawable(R.drawable.ic_download, true);
                            break;
                    }
                }));
            }

            private void showImageView() {
                playerView.setVisibility(View.GONE);
                zoomableImageView.setVisibility(View.VISIBLE);
            }

            private void showPlayerView() {
                playerView.setVisibility(View.VISIBLE);
                zoomableImageView.setVisibility(View.GONE);
            }

            private void updateWithNewText(String text) {
                descriptionTv.setText(text);
                descriptionTv.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver
                                .OnGlobalLayoutListener() {

                            @Override
                            public void onGlobalLayout() {
                                if (isTextUnlimited()) {
                                    if (canBeCollapsed()) {
                                        descriptionTv.setClickable(false);
                                        descriptionTv.setEllipsize(null);
                                    } else {
                                        descriptionTv.setClickable(true);
                                        descriptionTv.setEllipsize(TextUtils.TruncateAt.END);
                                    }
                                } else {
                                    if (isTrimmedWithLimitLines()) {
                                        descriptionTv.setClickable(false);
                                        descriptionTv.setEllipsize(null);
                                    } else {
                                        descriptionTv.setClickable(true);
                                        descriptionTv.setEllipsize(TextUtils.TruncateAt.END);
                                    }
                                }
                                descriptionTv.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            }
                        });
            }

            private boolean isTextUnlimited() {
                return descriptionTv.getMaxLines() == Integer.MAX_VALUE;
            }

            private boolean canBeCollapsed() {
                return descriptionTv.getLineCount() <= MAX_LINES_COLLAPSED;
            }

            private boolean isTrimmedWithLimitLines() {
                return descriptionTv.getLineCount() <= descriptionTv.getMaxLines();
            }

            private void applyLayoutTransition() {
                LayoutTransition transition = new LayoutTransition();
                transition.setDuration(300);
                transition.enableTransitionType(LayoutTransition.CHANGING);
                mediaInfoCl.setLayoutTransition(transition);

                transition.addTransitionListener(new LayoutTransition.TransitionListener() {
                    @Override
                    public void startTransition(LayoutTransition transition,
                                                ViewGroup container, View view, int transitionType) {
                    }

                    @Override
                    public void endTransition(LayoutTransition transition,
                                              ViewGroup container, View view, int transitionType) {
                        if (COLLAPSING_ANIMATION_STATE == mCurrentAnimationState) {
                            descriptionTv.setMaxLines(MAX_LINES_COLLAPSED);
                        }
                        mCurrentAnimationState = IDLE_ANIMATION_STATE;
                    }
                });
            }

            private boolean isIdle() {
                return mCurrentAnimationState == IDLE_ANIMATION_STATE;
            }

            private boolean isRunning() {
                return !isIdle();
            }
        }
    }

    public IDownloader getDownloader() {
        return Downloader.getInstance(AccountManager.selectedAccount);
    }

    private void shareContent(MessageObject messageObject) {
        if (messageObject != null) {
            messageObject = RealmRoomMessage.getFinalMessage(messageObject);
            String path = getFilePath(messageObject);
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                AppUtils.shareItem(intent, messageObject);
                if (messageObject.messageType == ProtoGlobal.RoomMessageType.VIDEO_VALUE || messageObject.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT_VALUE) {
                    intent.setType("video/*");
                    startActivity(Intent.createChooser(intent, G.fragmentActivity.getResources().getString(R.string.share_video_from_igap)));
                } else {
                    intent.setType("image/*");
                    startActivity(Intent.createChooser(intent, G.fragmentActivity.getResources().getString(R.string.share_image_from_igap)));
                }
            } else {
                HelperError.showSnackMessage(getString(R.string.file_not_download_yet), false);
            }
        }
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == EventManager.ON_NEW_MEDIA_MESSAGE_RECEIVED && (long) args[0] == room.id) {
            G.runOnUiThread(() -> {
                List<RealmRoomMessage> roomMessages = getRoomMediaMessages(FragmentShowContent.this.getArguments());
                mAdapter.setRoomMessages(roomMessages);
                contentNumberTv.setText(MessageFormat.format("{0} {1} {2}", viewPager.getCurrentItem() + 1, G.fragmentActivity.getResources().getString(R.string.of), roomMessages.size()));
                setViewPagerListener(roomMessages);
            });
        }
    }
}

interface ShowMediaListener {
    void videoAttached(WeakReference<PlayerView> playerView, int position, boolean callPageSelected);


    void setToolbarVisibility(int state);
}