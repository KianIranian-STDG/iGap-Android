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

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.chrisbanes.photoview.PhotoView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperSaveFile;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.HttpRequest;
import net.iGap.module.imageLoaderService.ImageLoadingServiceInjector;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;

public class FragmentShowImage extends BaseFragment {

    private static ArrayList<String> downloadedList = new ArrayList<>();
    public final String ROOM_ID = "roomId";
    public final String TYPE = "type";
    public View appBarLayout;
    private MediaController videoController;
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
    private ArrayList<RealmRoomMessage> mFList = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private boolean isLockScreen = false;
    private boolean isFocusable = false;
    private PhotoView zoomableImageViewTmp = null;
    private int lastOrientation = 0;
    public static FocusAudioListener focusAudioListener;
    private ProtoGlobal.RoomMessageType messageType;
    private ArrayList<TextureView> mTextureViewTmp = new ArrayList<>();

    public static FragmentShowImage newInstance() {
        return new FragmentShowImage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.activity_show_image, container, false);
        //exitFragmentTransition = FragmentTransition.with(this).duration(200).interpolator(new LinearOutSlowInInterpolator()).to(view.findViewById(R.id.asi_view_pager)).start(savedInstanceState);
        //
        //exitFragmentTransition.exitListener(new AnimatorListenerAdapter() {
        //    @Override
        //    public void onAnimationStart(Animator animation) {
        //
        //    }
        //
        //    @Override
        //    public void onAnimationEnd(Animator animation) {
        //
        //    }
        //}).interpolator(new FastOutSlowInInterpolator());
        //exitFragmentTransition.startExitListening(view.findViewById(R.id.rooShowImage));

        return inflater.inflate(R.layout.activity_show_image, container, false);
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
            if (zoomableImageViewTmp != null && videoController != null && mMediaPlayer != null) {
                zoomableImageViewTmp.setVisibility(View.GONE);
                zoomableImageViewTmp.setVisibility(View.VISIBLE);
                if (mMediaPlayer.isPlaying()) {
                    videoController.show();
                }

                if (mTextureViewTmp.size() > 0) {
                    getRealSize(mMediaPlayer, mTextureViewTmp.get(0));
                }
            }
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
                popBackStackFragment();
                return false;
            }

            RealmResults<RealmRoomMessage> mRealmList = DbManager.getInstance().doRealmTask(realm -> {
                return RealmRoomMessage.findSorted(realm, mRoomId, "updateTime", Sort.ASCENDING);
            });
            if (mRealmList.size() < 1) {
                popBackStackFragment();
                return false;
            }

            //todo : remove for and handle it with query
            for (RealmRoomMessage roomMessage : mRealmList) {
                if (RealmRoomMessage.isImageOrVideo(roomMessage, messageType)) {
                    if ((roomMessage.getForwardMessage() != null ? roomMessage.getForwardMessage().getAttachment() : roomMessage.getAttachment()) != null)
                        mFList.add(roomMessage);
                }
            }

            if (selectedFileToken != null) {
                for (int i = mFList.size() - 1; i >= 0; i--) {
                    if (selectedFileToken == mFList.get(i).getMessageId()) {
                        selectedFile = i;
                        break;
                    }
                }
            }

            return true;
        } else {
            if (G.fragmentActivity != null) {
                popBackStackFragment();
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
                popBackStackFragment();
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

        initViewPager();


        focusAudioListener = isPlay -> {

            if (isPlay) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    isLockScreen = true;
                    isFocusable = true;
                }
            } else {
                if (mMediaPlayer != null && isFocusable) {
                    int length = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.seekTo(length);
                    mMediaPlayer.start();
                    isLockScreen = true;
                    isFocusable = false;

                }
            }

        };
    }

    //***************************************************************************************

    private void initViewPager() {

        AdapterViewPager mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);

        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + mFList.size());
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
    private void showImageInfo(RealmRoomMessage realmRoomMessage) {
        if (realmRoomMessage == null || RealmUserInfo.getCurrentUserAuthorHash().equals("")) {
            return;
        }
        RealmRoomMessage realmRoomMessageFinal = RealmRoomMessage.getFinalMessage(realmRoomMessage);

        if (realmRoomMessageFinal != null && realmRoomMessageFinal.isValid() && realmRoomMessageFinal.getMessage() != null && !realmRoomMessageFinal.getMessage().isEmpty()) {
            txtImageDesc.setText(EmojiManager.getInstance().replaceEmoji(realmRoomMessageFinal.getMessage(), txtImageDesc.getPaint().getFontMetricsInt()));
            txtImageDesc.setVisibility(View.VISIBLE);
        } else {
            txtImageDesc.setVisibility(View.GONE);
        }

        RealmRegisteredInfo realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRegisteredInfo.getRegistrationInfo(realm, realmRoomMessageFinal.getUserId());
        });

        if (realmRegisteredInfo != null) {
            txtImageName.setText(realmRegisteredInfo.getDisplayName());
        } else {
            txtImageName.setText("");
        }

        if (realmRoomMessageFinal.getAuthorHash() != null && RealmUserInfo.getCurrentUserAuthorHash().equals(realmRoomMessageFinal.getAuthorHash())) {

            txtImageName.setText(R.string.you);
        }

        if (realmRoomMessageFinal.getUpdateTime() != 0) {
            txtImageTime.setText(HelperCalander.getClocktime(realmRoomMessageFinal.getUpdateTime(), true));
            txtImageDate.setText(HelperCalander.checkHijriAndReturnTime(realmRoomMessageFinal.getUpdateTime() / 1000));
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

        new TopSheetDialog(getContext()).setListData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.save_to_gallery))) {
                saveToGallery();
            } else {
                shareImage();
            }
        }).show();
    }

    /**
     * share Image and video
     */
    private void shareImage() {
        RealmRoomMessage roomMessage = null;

        if (mFList.size() > viewPager.getCurrentItem())
            roomMessage = mFList.get(viewPager.getCurrentItem());

        if (roomMessage != null) {
            roomMessage = RealmRoomMessage.getFinalMessage(roomMessage);
            String path = getFilePath(viewPager.getCurrentItem());
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                AppUtils.shareItem(intent, new StructMessageInfo(roomMessage));
                if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
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


    /**
     * share Image and video
     */
    private void saveToGallery() {
        RealmRoomMessage rm = null;
        if (mFList.size() > viewPager.getCurrentItem()) rm = mFList.get(viewPager.getCurrentItem());
        if (rm != null) {
            String path = getFilePath(viewPager.getCurrentItem());
            ProtoGlobal.RoomMessageType messageType;
            if (rm.getForwardMessage() != null) {
                messageType = rm.getForwardMessage().getMessageType();
            } else {
                messageType = rm.getMessageType();
            }
            File file = new File(path);
            if (file.exists()) {
                if (messageType == ProtoGlobal.RoomMessageType.VIDEO || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                    HelperSaveFile.saveFileToDownLoadFolder(path, "VIDEO_" + System.currentTimeMillis() + ".mp4", HelperSaveFile.FolderType.video, R.string.file_save_to_video_folder);
                } else if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                    HelperSaveFile.savePicToGallery(path, true);
                }
            }
        }
    }

    public String getFilePath(int position) {

        String result = "";

        RealmAttachment at = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage().getAttachment() : mFList.get(position).getAttachment();

        if (at != null) {
            if (at.getLocalFilePath() != null) result = at.getLocalFilePath();
        }

        ProtoGlobal.RoomMessageType messageType = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage().getMessageType() : mFList.get(position).getMessageType();

        if (result.length() < 1) {
            result = AndroidUtils.getFilePathWithCashId(at.getCacheId(), at.getName(), messageType);
        }

        return result;
    }

    public String getThumbnailPath(RealmRoomMessage roomMessage) {

        String result = "";

        if (roomMessage == null) {
            return "";
        }

        if (roomMessage.getAttachment() != null) {
            if (roomMessage.getAttachment().getLocalThumbnailPath() != null) {
                result = roomMessage.getAttachment().getLocalThumbnailPath();
            }

            if (result.length() < 1) {
                result = AndroidUtils.getFilePathWithCashId(roomMessage.getAttachment().getCacheId(), roomMessage.getAttachment().getName(), G.DIR_TEMP, true);
            }
        }

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (videoController != null) {
            videoController.hide();
            videoController = null;
        }
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (videoController != null) {
            videoController.hide();
            videoController = null;
        }

        focusAudioListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isLockScreen = true;
        }
        if (videoController != null && videoController.isShowing()) {
            videoController.hide();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isLockScreen) {
            if (videoController != null && getActivity() != null) {
                videoController.show();
            }
        }
    }

    /**
     * adapter for view pager
     */
    private class AdapterViewPager extends PagerAdapter implements MediaController.MediaPlayerControl {

        private String videoPath;

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
            zoomableImageView.setZoomable(false);

            FrameLayout frameLayout = layout.findViewById(R.id.Layout_showImage);
            frameLayout.setOnClickListener(v -> setPageViewState(imgPlay));

            final MessageProgress progress = layout.findViewById(R.id.progress);
            AppUtils.setProgresColor(progress.progressBar);

            final RealmRoomMessage rm = RealmRoomMessage.getFinalMessage(mFList.get(position));

            if (rm != null && rm.isValid()) {
                if (HelperDownloadFile.getInstance().isDownLoading(rm.getAttachment().getCacheId())) {
                    progress.withDrawable(R.drawable.ic_cancel, true);
                    startDownload(position, progress, zoomableImageView, imgPlay, mTextureView);
                } else {
                    progress.withDrawable(R.drawable.ic_download, true);
                }

                String path = getFilePath(position);
                File file = new File(path);
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                    if (videoController != null) videoController.hide();
                }
                if (file.exists()) {
                    progress.setVisibility(View.GONE);
//                    G.imageLoader.displayImage(suitablePath(path), zoomableImageView);
                    ImageLoadingServiceInjector.inject().loadImage(zoomableImageView, path, true);
                    zoomableImageView.setZoomable(true);
                    if (rm.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || rm.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        zoomableImageView.setVisibility(View.VISIBLE);
                        imgPlay.setVisibility(View.GONE);
                        isFirstPlay = false;
                    } else {
                        if (isFirstPlay) {
                            mTextureView.setVisibility(View.VISIBLE);

                            G.handler.postDelayed(() -> playVideo(position, mTextureView, imgPlay, zoomableImageView), 100);
                            isFirstPlay = false;
                        }
                        imgPlay.setVisibility(View.VISIBLE);
                        mTextureView.setVisibility(View.INVISIBLE);
                        videoPath = path;
                    }
                } else {
                    imgPlay.setVisibility(View.GONE);
                    path = getThumbnailPath(rm);
                    zoomableImageView.setVisibility(View.VISIBLE);
                    file = new File(path);
                    if (file.exists()) {
//                        G.imageLoader.displayImage(suitablePath(path), zoomableImageView);
                        ImageLoadingServiceInjector.inject().loadImage(zoomableImageView, path);
                    } else if (rm.getAttachment() != null) {
                        // if thumpnail not exist download it
                        ProtoFileDownload.FileDownload.Selector selector = null;
                        long fileSize = 0;

                        if (rm.getAttachment().getSmallThumbnail() != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                            fileSize = rm.getAttachment().getSmallThumbnail().getSize();
                        } else if (rm.getAttachment().getLargeThumbnail() != null) {
                            selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                            fileSize = rm.getAttachment().getLargeThumbnail().getSize();
                        }

                        final String filePathTumpnail = AndroidUtils.getFilePathWithCashId(rm.getAttachment().getCacheId(), rm.getAttachment().getName(), G.DIR_TEMP, true);

                        if (selector != null && fileSize > 0) {
                            HelperDownloadFile.getInstance().startDownload(rm.getMessageType(), System.currentTimeMillis() + "", rm.getAttachment().getToken(), rm.getAttachment().getUrl(), rm.getAttachment().getCacheId(), rm.getAttachment().getName(), fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override
                                public void OnProgress(final String path, int progress) {

                                    if (progress == 100) {

                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
//                                                G.imageLoader.displayImage(AndroidUtils.suitablePath(path), zoomableImageView);
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

                String _cashID = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage().getAttachment().getCacheId() : mFList.get(position).getAttachment().getCacheId();

                if (HelperDownloadFile.getInstance().isDownLoading(_cashID)) {
                    HelperDownloadFile.getInstance().stopDownLoad(_cashID);
                } else {
                    progress.withDrawable(R.drawable.ic_cancel, true);
                    startDownload(position, progress, zoomableImageView, imgPlay, mTextureView);
                }
            });

            zoomableImageView.setOnClickListener(view -> {
                setPageViewState(imgPlay);
            });

            imgPlay.setOnClickListener(v -> {
                mTextureView.setVisibility(View.VISIBLE);
                playVideo(position, mTextureView, imgPlay, zoomableImageView);
            });

            mTextureView.setOnClickListener(v -> setPageViewState(imgPlay));

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

                    if (isFirstPlay) {
                        if (mFList.get(position).getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || mFList.get(position).getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                            isFirstPlay = false;
                        }
                    }
                }

                @Override
                public void onPageSelected(final int position) {

                    zoomableImageViewTmp = null;

                    txtImageNumber.setText(position + 1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + mFList.size());
                    if (HelperCalander.isPersianUnicode) {
                        txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
                    }
                    showImageInfo(mFList.get(position));

                    if (mFList.get(position).getForwardMessage() != null) {
                        messageType = mFList.get(position).getForwardMessage().getMessageType();
                    } else {
                        messageType = mFList.get(position).getMessageType();
                    }

                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) mMediaPlayer.stop();
                    if (messageType == ProtoGlobal.RoomMessageType.VIDEO || messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                        File f = new File(getFilePath(position));
                        if (f.exists()) {
                            imgPlay.setVisibility(View.VISIBLE);
                            zoomableImageView.setVisibility(View.VISIBLE);
                        } else {
                            imgPlay.setVisibility(View.GONE);
                        }
                    } else if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        imgPlay.setVisibility(View.GONE);
                    }
                    if (videoController != null) {
                        videoController.hide();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            container.addView(layout);
            return layout;
        }

        private void setPageViewState(ImageView imgPlay) {
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

            if (getActivity() != null && zoomableImageViewTmp != null && imgPlay.getVisibility() != View.VISIBLE && mMediaPlayer != null && videoController != null) {
                if (videoController.isShowing()) {
                    videoController.hide();
                } else {
                    videoController.show();
                }
            }
        }

        private void startDownload(final int position, final MessageProgress progress, final PhotoView ZoomableImageView, final ImageView imgPlay, final TextureView mTextureView) {
            final RealmRoomMessage rm = RealmRoomMessage.getFinalMessage(mFList.get(position));
            if (downloadedList.indexOf(rm.getAttachment().getToken()) == -1) {
                downloadedList.add(rm.getAttachment().getCacheId());
            }

            progress.withOnProgress(() -> G.currentActivity.runOnUiThread(() -> {
                progress.withProgress(0);
                progress.setVisibility(View.GONE);
                ZoomableImageView.setZoomable(true);
                if (rm.isValid() && rm.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO) {
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
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * video player
         */
        private void playVideo(final int position, final TextureView mTextureView, final ImageView imgPlay, final PhotoView zoomableImageView) {

            zoomableImageViewTmp = zoomableImageView;
            if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
            if (videoController == null) videoController = new MediaController(G.fragmentActivity);
            mTextureView.setVisibility(View.VISIBLE);

            videoPath = getFilePath(position);
            ViewCompat.setLayoutDirection(videoController, View.LAYOUT_DIRECTION_LTR);
            videoController.setAnchorView(zoomableImageView);
            videoController.setMediaPlayer(this);
            imgPlay.setVisibility(View.GONE);
            mMediaPlayer.reset();

            mTextureViewTmp.clear();
            mTextureViewTmp.add(mTextureView);

            try {
                mMediaPlayer.setDataSource(G.fragmentActivity, Uri.parse(videoPath));
                mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        setMediaPlayer(mMediaPlayer, mTextureView, imgPlay, zoomableImageView);
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                        /*Log.e("ddd", "width  :" + width + "    height  : " + height);*/
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

                        return false;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                    }
                });
                if (mTextureView.getSurfaceTexture() != null) {
                    setMediaPlayer(mMediaPlayer, mTextureView, imgPlay, zoomableImageView);
                }
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            mMediaPlayer.setOnCompletionListener(mp -> {
                mp.seekTo(0);
                imgPlay.setVisibility(View.VISIBLE);
            });

            //mMediaPlayer.seekTo(100);
        }

        private void setMediaPlayer(MediaPlayer mMediaPlayer, final TextureView mTextureView, final ImageView imgPlay, final PhotoView zoomableImageView) {

            if (mTextureView == null) {
                return;
            }
            Surface surfaceTexture = new Surface(mTextureView.getSurfaceTexture());

            if (surfaceTexture == null) {
                return;
            }

            try {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setSurface(surfaceTexture);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.prepareAsync();
                }
            } catch (IllegalStateException e) {
                e.getMessage();
            }


            mMediaPlayer.setOnPreparedListener(mp -> {

                getRealSize(mp, mTextureView);
                imgPlay.setVisibility(View.GONE);
                mp.start();
                MusicPlayer.pauseSound();
                mTextureView.setVisibility(View.VISIBLE);
                zoomableImageView.animate().setDuration(700).alpha(0F).start();

                G.handler.postDelayed(() -> {
                    zoomableImageView.setVisibility(View.GONE);
                    zoomableImageView.clearAnimation();
                }, 700);

                videoController.setEnabled(true);
                videoController.show();
            });

        }


        @Override
        public void start() {
            if (mMediaPlayer != null) mMediaPlayer.start();
            MusicPlayer.pauseSound();
        }

        @Override
        public void pause() {
            if (mMediaPlayer != null) mMediaPlayer.pause();
        }

        @Override
        public int getDuration() {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getDuration();
            }
            return 0;
        }

        @Override
        public int getCurrentPosition() {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getCurrentPosition();
            }
            return 0;
        }

        @Override
        public void seekTo(int pos) {
            if (mMediaPlayer != null) mMediaPlayer.seekTo(pos);
        }

        @Override
        public boolean isPlaying() {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return true;
        }

        @Override
        public boolean canSeekBackward() {
            return true;
        }

        @Override
        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    }


    /**
     * get real width and height video
     */
    private void getRealSize(MediaPlayer mp, TextureView mTextureView) {

        if (mp == null || mTextureView == null) {
            return;
        }

        //Get the dimensions of the video
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        Display display = G.fragmentActivity.getWindowManager().getDefaultDisplay();

        int finalWith, finalHeight;

        finalWith = display.getWidth();
        finalHeight = (int) (((float) videoHeight / (float) videoWidth) * (float) display.getWidth());

        if (finalHeight > display.getHeight()) {
            finalWith = (int) (((float) finalWith / (float) finalHeight) * (float) display.getHeight());
            finalHeight = display.getHeight();
        }

        ViewGroup.LayoutParams lp = mTextureView.getLayoutParams();
        lp.width = finalWith;
        lp.height = finalHeight;
        mTextureView.setLayoutParams(lp);
    }

    public interface FocusAudioListener {

        void audioPlay(boolean isPlay);

    }

}