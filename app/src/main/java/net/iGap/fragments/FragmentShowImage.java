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

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
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
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;

public class FragmentShowImage extends Fragment {
    private static final String TAG = "FragmentShowImage";
    private static final ArrayList<String> downloadedList = new ArrayList<>();
    public static FocusAudioListener focusAudioListener;
    public final String ROOM_ID = "roomId";
    public final String TYPE = "type";
    public View appBarLayout;
    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;
    private TextView txtImageTime;
    private TextView txtImageDesc;
    private LinearLayout toolbarShowImage;
    private boolean isShowToolbar = true;
    private ViewGroup ltImageName;
    private ViewPager viewPager;
    private boolean isFirstPlay = true;
    private int selectedFile = 0;
    private ArrayList<MessageObject> mFList = new ArrayList<>();
    private int lastOrientation = 0;
    private ProtoGlobal.RoomMessageType messageType;
    private RealmRoom room;
    private SimpleExoPlayer player;
    private VerticalSwipeBackLayout mVerticalSwipeBackLayout;

    public static FragmentShowImage newInstance() {
        return new FragmentShowImage();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVerticalSwipeBackLayout = new VerticalSwipeBackLayout(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_show_image, container, false);
        View fragmentView = mVerticalSwipeBackLayout.setFragment(this, view);
        startingAnimation();
        return fragmentView;
    }

    private void startingAnimation() {
        View decorView = getActivity().getWindow().getDecorView();
        ObjectAnimator scaleUpAnimation = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
        scaleUpAnimation.setDuration(150);
        scaleUpAnimation.start();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getIntentData(this.getArguments())) {
            initComponent(view);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != lastOrientation) {
            lastOrientation = newConfig.orientation;
        }
    }

    @Override
    public void onAttach(Context context) {
        if (appBarLayout != null) {
            appBarLayout.setVisibility(View.GONE);
        }
        super.onAttach(context);
    }

    private boolean getIntentData(Bundle bundle) {
        if (getActivity() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (bundle != null) { // get a list of image
            Long mRoomId = bundle.getLong("RoomId");
            Long selectedFileToken = bundle.getLong("SelectedImage");
            if (bundle.getString("TYPE") != null) {
                messageType = convertType(bundle.getString("TYPE"));
            }
            if (mRoomId == null) {
                getFragmentManager().popBackStackImmediate();
                return false;
            }
            Log.i(TAG, "getIntentData: before get list from database");
            RealmResults<RealmRoomMessage> mRealmList = DbManager.getInstance().doRealmTask(realm -> {
                return RealmRoomMessage.findSorted(realm, mRoomId, "updateTime", Sort.ASCENDING);
            });
            Log.i(TAG, "getIntentData: after get list from database size -> " + mRealmList.size());
            if (mRealmList.size() < 1) {
                getFragmentManager().popBackStackImmediate();
                return false;
            }
            List<RealmRoomMessage> realmRoomMessages = DbManager.getInstance().doRealmTask(realm -> {
                return realm.copyFromRealm(mRealmList);
            });
            //todo : remove for and handle it with query
            for (RealmRoomMessage roomMessage : realmRoomMessages) {
                Log.i(TAG, "--------------------------------------------------------");
                if (RealmRoomMessage.isImageOrVideo(roomMessage, messageType)) {
                    if ((roomMessage.getForwardMessage() != null ? roomMessage.getForwardMessage().getAttachment() : roomMessage.getAttachment()) != null) {
                        Log.i(TAG, "start create");
                        MessageObject messageObject = MessageObject.create(roomMessage);
                        Log.i(TAG, "end create");
                        mFList.add(messageObject);
                    }
                }
            }
            if (selectedFileToken != null) {
                for (int i = mFList.size() - 1; i >= 0; i--) {
                    if (selectedFileToken == mFList.get(i).id) {
                        selectedFile = i;
                        break;
                    }
                }
            }
            return true;
        } else {
            if (G.fragmentActivity != null) {
                getFragmentManager().popBackStackImmediate();
            }
            return false;
        }
    }

    private ProtoGlobal.RoomMessageType convertType(String type) {
        if (type != null) {
            if (type.contains(ProtoGlobal.RoomMessageType.VIDEO.toString())) {
                return ProtoGlobal.RoomMessageType.VIDEO;
            } else if (type.contains(ProtoGlobal.RoomMessageType.IMAGE.toString())) {
                return ProtoGlobal.RoomMessageType.IMAGE;
            }
        }
        return null;
    }

    private void initComponent(View view) {
        downloadedList.clear();
        RippleView rippleBack = view.findViewById(R.id.asi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Log.wtf(this.getClass().getName(), "on back");
                getFragmentManager().popBackStackImmediate();
            }
        });
        RippleView rippleMenu = view.findViewById(R.id.asi_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(rippleView -> popUpMenuShowImage());
        TextView btnShare = view.findViewById(R.id.asi_btn_share);
        btnShare.setOnClickListener(v -> shareImage());
        viewPager = view.findViewById(R.id.asi_view_pager);
        txtImageNumber = view.findViewById(R.id.asi_txt_image_number);
        ltImageName = view.findViewById(R.id.asi_layout_image_name);
        txtImageName = view.findViewById(R.id.asi_txt_image_name);
        txtImageDate = view.findViewById(R.id.asi_txt_image_date);
        txtImageTime = view.findViewById(R.id.asi_txt_image_time);
        txtImageDesc = view.findViewById(R.id.asi_txt_image_desc);
        toolbarShowImage = view.findViewById(R.id.toolbarShowImage);
        room = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", mFList.get(selectedFile).roomId).findFirst();
        });
        initViewPager();
    }

    private void initViewPager() {
        AdapterViewPager mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(selectedFile);
        txtImageNumber.setText(MessageFormat.format("{0} {1} {2}", selectedFile + 1, G.fragmentActivity.getResources().getString(R.string.of), mFList.size()));
        if (HelperCalander.isPersianUnicode) {
            txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
        }
        if (selectedFile >= mFList.size()) {
            return;
        }
        showImageInfo(mFList.get(selectedFile));
        viewPager.setOnClickListener(view -> {
        });
        viewPager.setPageTransformer(false, (view, position) -> {

            final float normalizedPosition = Math.abs(Math.abs(position) - 1);
            view.setScaleX(normalizedPosition / 2 + 0.5f);
            view.setScaleY(normalizedPosition / 2 + 0.5f);
        });
    }

    /**
     * show image info, time , name , description
     */
    private void showImageInfo(MessageObject messageObject) {
        if (messageObject == null || RealmUserInfo.getCurrentUserAuthorHash().equals("")) {
            return;
        }
        MessageObject realmRoomMessageFinal = RealmRoomMessage.getFinalMessage(messageObject);
        if (realmRoomMessageFinal != null && realmRoomMessageFinal.message != null && !realmRoomMessageFinal.message.isEmpty()) {
            txtImageDesc.setText(EmojiManager.getInstance().replaceEmoji(realmRoomMessageFinal.message, txtImageDesc.getPaint().getFontMetricsInt()));
            txtImageDesc.setVisibility(View.VISIBLE);
        } else {
            txtImageDesc.setVisibility(View.GONE);
        }
        RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRegisteredInfo.getRegistrationInfo(realm, realmRoomMessageFinal.userId);
        });
        if (realmRegisteredInfo != null) {
            txtImageName.setText(realmRegisteredInfo.getDisplayName());
        } else {
            txtImageName.setText("");
        }
        if (realmRoomMessageFinal.authorHash != null && RealmUserInfo.getCurrentUserAuthorHash().equals(realmRoomMessageFinal.authorHash)) {
            txtImageName.setText(R.string.you);
        }
        if (realmRoomMessageFinal.updateTime != 0) {
            txtImageTime.setText(HelperCalander.getClocktime(realmRoomMessageFinal.updateTime, true));
            txtImageDate.setText(HelperCalander.checkHijriAndReturnTime(realmRoomMessageFinal.updateTime / 1000));
        }
        if (HelperCalander.isPersianUnicode) {
            txtImageName.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageName.getText().toString()));
            txtImageTime.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageTime.getText().toString()));
            txtImageDate.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageDate.getText().toString()));
        }
    }

    public void popUpMenuShowImage() {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.save_to_gallery));
        if (messageType == ProtoGlobal.RoomMessageType.VIDEO || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
            items.add(getString(R.string.share_video_file_2));
        } else {
            items.add(getString(R.string.share_image_2));
        }
        if (RoomObject.isRoomPublic(room)) {
            if (MessageObject.canSharePublic(mFList.get(selectedFile))) {
                items.add(getString(R.string.share_file_link));
            }
        }
        new TopSheetDialog(getContext()).setListData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.save_to_gallery))) {
                saveToGallery();
            } else if (items.get(position).equals(getString(R.string.share_file_link))) {
                shareMediaLink();
            } else {
                shareImage();
            }
        }).show();
    }

    /**
     * share Image and video
     */
    private void shareImage() {
        MessageObject messageObject = null;
        if (mFList.size() > viewPager.getCurrentItem())
            messageObject = mFList.get(viewPager.getCurrentItem());
        if (messageObject != null) {
            messageObject = RealmRoomMessage.getFinalMessage(messageObject);
            String path = getFilePath(viewPager.getCurrentItem());
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

    private void shareMediaLink() {
        MessageObject messageObject = null;
        if (mFList.size() > viewPager.getCurrentItem())
            messageObject = mFList.get(viewPager.getCurrentItem());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (messageObject != null) {
            intent.putExtra(Intent.EXTRA_TEXT, messageObject.attachment.publicUrl);
        }
        startActivity(Intent.createChooser(intent, G.context.getString(R.string.share_link_item_dialog)));
    }

    /**
     * share Image and video
     */
    private void saveToGallery() {
        MessageObject messageObject = null;
        if (mFList.size() > viewPager.getCurrentItem())
            messageObject = mFList.get(viewPager.getCurrentItem());
        if (messageObject != null) {
            String path = getFilePath(viewPager.getCurrentItem());
            int messageType;
            if (messageObject.forwardedMessage != null) {
                messageType = messageObject.forwardedMessage.messageType;
            } else {
                messageType = messageObject.messageType;
            }
            File file = new File(path);
            if (file.exists()) {
                if (messageType == ProtoGlobal.RoomMessageType.VIDEO_VALUE || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT_VALUE) {
                    HelperSaveFile.saveFileToDownLoadFolder(path, "VIDEO_" + System.currentTimeMillis() + ".mp4", HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                } else if (messageType == ProtoGlobal.RoomMessageType.IMAGE_VALUE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT_VALUE) {
                    HelperSaveFile.savePicToGallery(path, true);
                }
            }
        }
    }

    public String getFilePath(int position) {
        String result = "";
        AttachmentObject at = mFList.get(position).forwardedMessage != null ? mFList.get(position).forwardedMessage.attachment : mFList.get(position).attachment;
        if (at != null) {
            if (at.filePath != null) result = at.filePath;
        }
        int messageType = mFList.get(position).forwardedMessage != null ? mFList.get(position).forwardedMessage.messageType : mFList.get(position).messageType;
        if (result.length() < 1) {
            result = AndroidUtils.getFilePathWithCashId(at.cacheId, at.name, messageType);
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
        if (player != null)
            player.release();
        player = null;
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        focusAudioListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null && player.isPlaying()) {
            player.setPlayWhenReady(false);
            boolean isLockScreen = true;
        }
    }

    public interface FocusAudioListener {
        void audioPlay(boolean isPlay);
    }

    /**
     * adapter for view pager
     */
    private class AdapterViewPager extends PagerAdapter {
        private int index = 0;
        private List<Integer> videoPositions;

        public AdapterViewPager() {
            player = new SimpleExoPlayer.Builder(context).build();
            videoPositions = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mFList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = LayoutInflater.from(G.fragmentActivity);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, container, false);
            final TextureView mTextureView = layout.findViewById(R.id.textureView);
            final ImageView imgPlay = layout.findViewById(R.id.imgPlay);
            final PhotoView zoomableImageView = layout.findViewById(R.id.sisl_touch_image_view);
            final PlayerView playerView = layout.findViewById(R.id.player_view);
            zoomableImageView.setZoomable(false);
            FrameLayout frameLayout = layout.findViewById(R.id.Layout_showImage);
            frameLayout.setOnClickListener(v -> setPageViewState());
            final MessageProgress progress = layout.findViewById(R.id.progress);
            AppUtils.setProgresColor(progress.progressBar);
            final MessageObject messageObject = RealmRoomMessage.getFinalMessage(mFList.get(position));
            if (messageObject != null) {
                if (HelperDownloadFile.getInstance().isDownLoading(messageObject.getAttachment().cacheId)) {
                    progress.withDrawable(R.drawable.ic_cancel, true);
                    startDownload(position, progress, zoomableImageView, imgPlay, mTextureView);
                } else {
                    progress.withDrawable(R.drawable.ic_download, true);
                }
                String path = getFilePath(position);
                File file = new File(path);
                if (player != null && player.isPlaying()) {
                    player.setPlayWhenReady(false);
                    player.seekTo(0);
                }
                if (file.exists()) {
                    progress.setVisibility(View.GONE);
                    ImageLoadingServiceInjector.inject().loadImage(zoomableImageView, path, true);
                    zoomableImageView.setZoomable(true);
                    zoomableImageView.setVisibility(View.VISIBLE);
                    playerView.setVisibility(View.INVISIBLE);
                    if (messageObject.messageType == ProtoGlobal.RoomMessageType.IMAGE_VALUE || messageObject.messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT_VALUE) {
                        imgPlay.setVisibility(View.GONE);
                    } else {
                        imgPlay.setVisibility(View.VISIBLE);
                    }
                } else {
                    imgPlay.setVisibility(View.GONE);
                    path = getThumbnailPath(messageObject);
                    zoomableImageView.setVisibility(View.VISIBLE);
                    file = new File(path);
                    if (file.exists()) {
                        ImageLoadingServiceInjector.inject().loadImage(zoomableImageView, path);
                    } else if (messageObject.getAttachment() != null) {
                        // if thumpnail not exist download it
                        ProtoFileDownload.FileDownload.Selector selector = null;
                        long fileSize = 0;
                        if (messageObject.attachment.smallThumbnail != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                            fileSize = messageObject.attachment.smallThumbnail.size;
                        } else if (messageObject.attachment.largeThumbnail != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                            fileSize = messageObject.attachment.largeThumbnail.size;
                        }
                        final String filePathTumpnail = AndroidUtils.getFilePathWithCashId(messageObject.attachment.cacheId, messageObject.attachment.name, G.DIR_TEMP, true);
                        if (selector != null && fileSize > 0) {
                            HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.forNumber(messageObject.messageType), System.currentTimeMillis() + "", messageObject.attachment.token, messageObject.attachment.publicUrl, messageObject.attachment.cacheId, messageObject.attachment.name, fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override
                                public void OnProgress(final String path, int progress) {
                                    if (progress == 100) {
                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ImageLoadingServiceInjector.inject().loadImage(zoomableImageView, path);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void OnError(String token) {
                                }
                            });
                        }
                    }
                }
            }
            progress.setOnClickListener(view -> {
                String _cashID = mFList.get(position).forwardedMessage != null ? mFList.get(position).forwardedMessage.attachment.cacheId : mFList.get(position).attachment.cacheId;
                if (HelperDownloadFile.getInstance().isDownLoading(_cashID)) {
                    HelperDownloadFile.getInstance().stopDownLoad(_cashID);
                } else {
                    progress.withDrawable(R.drawable.ic_cancel, true);
                    startDownload(position, progress, zoomableImageView, imgPlay, mTextureView);
                }
            });

            zoomableImageView.setOnClickListener(view -> {
                setPageViewState();
            });

            imgPlay.setOnClickListener(v -> {
                int currentIndex = index;
                String path = getFilePath(position);
                boolean isExistsVideoPosition = false;
                for (int i = 0; i < videoPositions.size(); i++) {
                    if (videoPositions.get(i).equals(position)) {
                        isExistsVideoPosition = true;
                        currentIndex = i;
                        break;
                    }
                }
                if (!isExistsVideoPosition) {
                    videoPositions.add(position);
                    player.addMediaItem(index++, MediaItem.fromUri(path));
                }
                playerView.setPlayer(player);
                playerView.hideController();
                player.seekTo(currentIndex, 0);
                zoomableImageView.setVisibility(View.INVISIBLE);
                playerView.setVisibility(View.VISIBLE);
                mTextureView.setVisibility(View.GONE);
                imgPlay.setVisibility(View.INVISIBLE);
                player.prepare();
                player.play();
            });

            mTextureView.setOnClickListener(v -> setPageViewState());

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                    if (isFirstPlay) {
                        if (mFList.get(position).messageType == ProtoGlobal.RoomMessageType.IMAGE_VALUE || mFList.get(position).messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT_VALUE) {
                            isFirstPlay = false;
                        }
                    }
                    player.setPlayWhenReady(false);
                    for (int i = 0; i < videoPositions.size(); i++) {
                        if (videoPositions.get(i).equals(position)) {
                            player.seekTo(i, 0);
                            break;
                        }
                    }
                }

                @Override
                public void onPageSelected(final int position) {
                    txtImageNumber.setText(MessageFormat.format("{0} {1} {2}", position + 1, G.fragmentActivity.getResources().getString(R.string.of), mFList.size()));
                    if (HelperCalander.isPersianUnicode) {
                        txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
                    }
                    showImageInfo(mFList.get(position));
                    if (mFList.get(position).forwardedMessage != null) {
                        messageType = ProtoGlobal.RoomMessageType.forNumber(mFList.get(position).forwardedMessage.messageType);
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.forNumber(mFList.get(position).messageType);
                    }
                    if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        imgPlay.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            container.addView(layout);
            return layout;
        }

        private void setPageViewState() {
            if (isShowToolbar) {
                toolbarShowImage.animate().setDuration(150).alpha(0F).start();
                ltImageName.setVisibility(View.GONE);
                ltImageName.animate().setDuration(150).alpha(0F).start();
                toolbarShowImage.setVisibility(View.GONE);
                isShowToolbar = false;
            } else {
                toolbarShowImage.animate().setDuration(150).alpha(1F).start();
                toolbarShowImage.setVisibility(View.VISIBLE);
                ltImageName.animate().setDuration(150).alpha(1F).start();
                ltImageName.setVisibility(View.VISIBLE);
                isShowToolbar = true;
            }
        }

        private void startDownload(final int position, final MessageProgress progress, final PhotoView ZoomableImageView, final ImageView imgPlay, final TextureView mTextureView) {
            final MessageObject rm = RealmRoomMessage.getFinalMessage(mFList.get(position));
            if (!downloadedList.contains(rm.attachment.token)) {
                downloadedList.add(rm.attachment.cacheId);
            }
            progress.withOnProgress(() -> G.currentActivity.runOnUiThread(() -> {
                progress.withProgress(0);
                progress.setVisibility(View.GONE);
                ZoomableImageView.setZoomable(true);
                if (rm.messageType == ProtoGlobal.RoomMessageType.VIDEO_VALUE || rm.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT_VALUE) {
                    imgPlay.setVisibility(View.VISIBLE);
                }
            }));
            DownloadObject struct = DownloadObject.createForRoomMessage(rm);
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
                        rm.attachment.filePath = arg.data.getFilePath();
                        rm.attachment.token = arg.data.getToken();
                        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_FILE_DOWNLOAD_COMPLETED, rm));
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

        @Override
        public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
            container.removeView((View) object);
        }
    }

    public IDownloader getDownloader() {
        return Downloader.getInstance(AccountManager.selectedAccount);
    }

}