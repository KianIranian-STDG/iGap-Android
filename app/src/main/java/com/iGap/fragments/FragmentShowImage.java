package com.iGap.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperDownloadFile;
import com.iGap.helper.HelperSaveFile;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.TouchImageView;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.github.meness.emoji.EmojiTextView;
import io.meness.github.messageprogress.MessageProgress;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.iGap.module.AndroidUtils.suitablePath;

public class FragmentShowImage extends Fragment {

    private TextView txtImageNumber;
    private TextView txtImageName;
    private TextView txtImageDate;
    private EmojiTextView txtImageDesc;
    private LinearLayout toolbarShowImage;
    private boolean isShowToolbar = true;
    private ViewGroup ltImageName;
    private ViewPager viewPager;
    private boolean isFirstPlay = true;
    private boolean isReadyToPause = true;
    private int selectedFile = 0;
    private AdapterViewPager mAdapter;
    private RealmResults<RealmRoomMessage> mRealmList;

    private ArrayList<RealmRoomMessage> mFList = new ArrayList<>();

    private Long mRoomid;
    private Long selectedFileToken;
    private Realm mRealm;
    private MediaPlayer mMediaPlayer;
    public static ArrayList<String> downloadedList = new ArrayList<>();

    public static View appBarLayout;
    public MediaController videoController;
    public int po;
    private String path;
    private String type = null;

    public static FragmentShowImage newInstance() {
        return new FragmentShowImage();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_show_image, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getIntentData(this.getArguments())) initComponent(view);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();

        if (appBarLayout != null) appBarLayout.setVisibility(View.VISIBLE);

        if (mRealm != null) mRealm.close();
    }

    @Override public void onAttach(Context context) {
        if (appBarLayout != null) appBarLayout.setVisibility(View.GONE);

        super.onAttach(context);
    }

    private boolean getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image

            mRoomid = bundle.getLong("RoomId");
            selectedFileToken = bundle.getLong("SelectedImage");
            if (bundle.getString("TYPE") != null) type = bundle.getString("TYPE");
            if (mRoomid == null) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return false;
            }

            mRealm = Realm.getDefaultInstance();

            mRealmList = mRealm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomid).findAllSorted(RealmRoomMessageFields.UPDATE_TIME, Sort.ASCENDING);

            if (mRealmList.size() < 1) {
                getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
                return false;
            }

            for (RealmRoomMessage item : mRealmList) {

                boolean isImage = false;

                if (type == null) {
                    if (item.getMessageType().toString().contains(ProtoGlobal.RoomMessageType.IMAGE.toString()) || item.getMessageType()
                        .toString()
                        .contains(ProtoGlobal.RoomMessageType.VIDEO.toString())) {
                        isImage = true;
                    } else if (item.getForwardMessage() != null) {
                        if (item.getForwardMessage().getMessageType().toString().contains(ProtoGlobal.RoomMessageType.IMAGE.toString()) || item.getForwardMessage().getMessageType().toString().contains(ProtoGlobal.RoomMessageType.VIDEO.toString())) {
                            isImage = true;
                        }
                    }
                } else if (type.contains(ProtoGlobal.RoomMessageType.VIDEO.toString())) {
                    if (item.getMessageType().toString().contains(ProtoGlobal.RoomMessageType.VIDEO.toString())) {
                        isImage = true;
                    }
                } else if (type.contains(ProtoGlobal.RoomMessageType.IMAGE.toString())) {
                    if (item.getMessageType().toString().contains(ProtoGlobal.RoomMessageType.IMAGE.toString())) {
                        isImage = true;
                    }
                }

                if (isImage) mFList.add(item);
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
            getActivity().getFragmentManager().beginTransaction().remove(FragmentShowImage.this).commit();
            return false;
        }
    }

    private void initComponent(View view) {

        downloadedList.clear();

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.asi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {
                getActivity().onBackPressed();
            }
        });

        MaterialDesignTextView btnMenu = (MaterialDesignTextView) view.findViewById(R.id.asi_btn_menu);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.asi_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override public void onComplete(RippleView rippleView) {
                popUpMenuShowImage();
            }
        });
        viewPager = (ViewPager) view.findViewById(R.id.asi_view_pager);

        txtImageNumber = (TextView) view.findViewById(R.id.asi_txt_image_number);
        txtImageName = (TextView) view.findViewById(R.id.asi_txt_image_name);
        ltImageName = (ViewGroup) view.findViewById(R.id.asi_layout_image_name);
        txtImageDate = (TextView) view.findViewById(R.id.asi_txt_image_date);
        txtImageDesc = (EmojiTextView) view.findViewById(R.id.asi_txt_image_desc);
        toolbarShowImage = (LinearLayout) view.findViewById(R.id.toolbarShowImage);

        initViewPager();
    }

    //***************************************************************************************

    private void initViewPager() {

        mAdapter = new AdapterViewPager();
        viewPager.setAdapter(mAdapter);

        viewPager.setCurrentItem(selectedFile);

        txtImageNumber.setText(selectedFile + 1 + " " + getString(R.string.of) + " " + mFList.size());

        showImageInfo(mFList.get(selectedFile));

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override public void transformPage(View view, float position) {

                final float normalizedPosition = Math.abs(Math.abs(position) - 1);
                view.setScaleX(normalizedPosition / 2 + 0.5f);
                view.setScaleY(normalizedPosition / 2 + 0.5f);
            }
        });
    }

    /**
     * show image info, time , name , description
     */
    private void showImageInfo(RealmRoomMessage realmRoomMessage) {
        RealmRoomMessage realmRoomMessageFinal;
        if (realmRoomMessage.getForwardMessage() != null) {
            realmRoomMessageFinal = realmRoomMessage.getForwardMessage();
        } else {
            realmRoomMessageFinal = realmRoomMessage;
        }

        if (realmRoomMessageFinal.getMessage() != null && !realmRoomMessageFinal.getMessage().isEmpty()) {
            txtImageDesc.setText(realmRoomMessageFinal.getMessage());
            txtImageDesc.setVisibility(View.VISIBLE);
        } else {
            txtImageDesc.setVisibility(View.GONE);
        }
        if (realmRoomMessageFinal.getAttachment() != null) {
            txtImageName.setText(realmRoomMessageFinal.getAttachment().getName());
        }
        if (realmRoomMessageFinal.getUpdateTime() != 0) {
            txtImageDate.setText(TimeUtils.toLocal(realmRoomMessageFinal.getUpdateTime(), G.CHAT_MESSAGE_TIME));
        }
    }

    public void popUpMenuShowImage() {

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).items(R.array.pop_up_menu_show_image).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
            @Override public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                if (which == 0) {
                    shareImage();
                } else if (which == 1) {
                    saveToGalary();
                }
            }
        }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp200);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void shareImage() {

        RealmRoomMessage rm = mFList.get(viewPager.getCurrentItem());

        if (rm != null) {
            if (rm.getForwardMessage() != null) rm = rm.getForwardMessage();
            String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                putExtra(intent, StructMessageInfo.convert(rm));
                startActivity(Intent.createChooser(intent, getString(R.string.share_image_from_igap)));
            }
        }
    }

    private void putExtra(Intent intent, StructMessageInfo messageInfo) {
        try {
            String filePath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();
            if (filePath != null) {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToGalary() {

        RealmRoomMessage rm = mFList.get(viewPager.getCurrentItem());
        if (rm != null) {

            if (rm.getForwardMessage() != null) rm = rm.getForwardMessage();

            String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
            File file = new File(path);
            if (file.exists()) {
                HelperSaveFile.savePicToGallary(path, true);
            }
        }
    }

    private class AdapterViewPager extends PagerAdapter implements MediaController.MediaPlayerControl, TextureView.SurfaceTextureListener {

        private String videoPath;

        @Override public int getCount() {
            return mFList.size();
        }

        @Override public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override public Object instantiateItem(View container, final int position) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_sub_layout, (ViewGroup) container, false);
            final TextureView mTextureView = (TextureView) layout.findViewById(R.id.textureView);
            final ImageView imgPlay = (ImageView) layout.findViewById(R.id.imgPlay);
            final TouchImageView touchImageView = (TouchImageView) layout.findViewById(R.id.sisl_touch_image_view);
            final MessageProgress progress = (MessageProgress) layout.findViewById(R.id.progress);

            final ContentLoadingProgressBar contentLoading = (ContentLoadingProgressBar) layout.findViewById(R.id.ch_progress_loadingContent);
            contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
            final RealmRoomMessage rm = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage() : mFList.get(position);

            if (HelperDownloadFile.isDownLoading(rm.getAttachment().getToken())) {
                progress.withDrawable(R.drawable.ic_cancel, true);
                startDownload(position, progress, touchImageView, contentLoading, imgPlay, mTextureView);
            } else {
                progress.withDrawable(R.drawable.ic_download, true);
                contentLoading.setVisibility(View.GONE);
            }

            //
            if (rm != null) {

                path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
                File file = new File(path);
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mMediaPlayer.seekTo(0);
                    if (videoController != null) videoController.hide();
                }
                if (file.exists()) {
                    progress.setVisibility(View.GONE);
                    ImageLoader.getInstance().displayImage(suitablePath(path), touchImageView);
                    if (rm.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE) {
                        touchImageView.setVisibility(View.VISIBLE);
                        imgPlay.setVisibility(View.GONE);
                        isFirstPlay = false;
                    } else {
                        if (isFirstPlay) {
                            mTextureView.setVisibility(View.VISIBLE);

                            G.handler.postDelayed(new Runnable() {
                                @Override public void run() {
                                    playVideo(position, mTextureView, imgPlay, touchImageView);
                                }
                            }, 100);
                            isFirstPlay = false;
                        }
                        imgPlay.setVisibility(View.VISIBLE);
                        mTextureView.setVisibility(View.VISIBLE);
                        videoPath = path;
                    }
                } else {
                    imgPlay.setVisibility(View.GONE);
                    path = getThumpnailPath(rm.getAttachment().getToken(), rm.getAttachment().getName());
                    touchImageView.setVisibility(View.VISIBLE);
                    file = new File(path);
                    if (file.exists()) {
                        ImageLoader.getInstance().displayImage(suitablePath(path), touchImageView);
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

                        final String filePathTumpnail = G.DIR_TEMP + "/" + "thumb_" + rm.getAttachment().getToken() + "_" + rm.getAttachment().getName();

                        if (selector != null && fileSize > 0) {
                            HelperDownloadFile.startDownload(rm.getAttachment().getToken(), rm.getAttachment().getName(), fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                                @Override public void OnProgress(String token, int progress) {

                                    if (progress == 100) {

                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                ImageLoader.getInstance().displayImage(suitablePath(filePathTumpnail), touchImageView);
                                            }
                                        });
                                    }
                                }

                                @Override public void OnError(String token) {

                                }
                            });
                        }
                    }
                }
            }

            progress.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {

                    String _tpken =
                        mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage().getAttachment().getToken() : mFList.get(position).getAttachment().getToken();

                    if (HelperDownloadFile.isDownLoading(_tpken)) {
                        HelperDownloadFile.stopDownLoad(_tpken);
                    } else {
                        progress.withDrawable(R.drawable.ic_cancel, true);
                        startDownload(position, progress, touchImageView, contentLoading, imgPlay, mTextureView);
                    }
                }
            });

            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
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
            });

            imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                    mTextureView.setVisibility(View.VISIBLE);
                    playVideo(position, mTextureView, imgPlay, touchImageView);
                }
            });

            mTextureView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (mMediaPlayer != null) {
                        if (videoController.isShowing()) {
                            videoController.setVisibility(View.GONE);
                        } else if (videoController != null) {
                            videoController.show();
                            videoController.setVisibility(View.VISIBLE);
                        }
                    }
                    touchImageView.performClick();
                }
            });
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

                    if (mFList.get(position).getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || mFList.get(position).getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                        isFirstPlay = false;
                    }
                }

                @Override public void onPageSelected(final int position) {
                    txtImageNumber.setText(position + 1 + " " + getString(R.string.of) + " " + mFList.size());
                    showImageInfo(mFList.get(position));

                    if (mFList.get(position).getMessageType() == ProtoGlobal.RoomMessageType.VIDEO || mFList.get(position).getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                        if (touchImageView.getVisibility() == View.GONE) {
                            touchImageView.setVisibility(View.VISIBLE);
                            imgPlay.setVisibility(View.VISIBLE);
                        }
                    }

                    if (videoController != null) {
                        videoController.hide();
                    }
                }

                @Override public void onPageScrollStateChanged(int state) {
                }
            });

            ((ViewGroup) container).addView(layout);
            return layout;
        }

        private void startDownload(final int position, final MessageProgress progress, final TouchImageView touchImageView, final ContentLoadingProgressBar contentLoading, final ImageView imgPlay,
            final TextureView mTextureView) {

            contentLoading.setVisibility(View.VISIBLE);

            final RealmRoomMessage rm = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage() : mFList.get(position);

            String dirPath = AndroidUtils.suitableAppFilePath(rm.getMessageType()) + "/" + rm.getAttachment().getToken() + "_" + rm.getAttachment().getName();

            if (downloadedList.indexOf(rm.getAttachment().getToken()) == -1) downloadedList.add(rm.getAttachment().getToken());

            HelperDownloadFile.startDownload(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getAttachment().getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 4,
                new HelperDownloadFile.UpdateListener() {
                    @Override public void OnProgress(String token, final int progres) {

                        if (progress != null) {

                            G.currentActivity.runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    if (progres < 100) {
                                        progress.withProgress(progres);
                                    } else {
                                        progress.withProgress(0);
                                        progress.setVisibility(View.GONE);
                                        contentLoading.setVisibility(View.GONE);
                                        if (rm.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO) {
                                            imgPlay.setVisibility(View.VISIBLE);
                                            //if (position == viewPager.getCurrentItem()) playVideo(position, mTextureView, imgPlay, touchImageView);
                                        }
                                        String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
                                        ImageLoader.getInstance().displayImage(suitablePath(path), touchImageView);
                                    }
                                }
                            });
                        }
                    }

                    @Override public void OnError(String token) {

                        G.currentActivity.runOnUiThread(new Runnable() {
                            @Override public void run() {
                                progress.withProgress(0);
                                progress.withDrawable(R.drawable.ic_download, true);
                                contentLoading.setVisibility(View.GONE);
                            }
                        });
                    }
                });
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void playVideo(final int position, final TextureView mTextureView, final ImageView imgPlay, final TouchImageView touchImageView) {

            if (mMediaPlayer == null) mMediaPlayer = new MediaPlayer();
            if (videoController == null) videoController = new MediaController(getActivity());
            mTextureView.setVisibility(View.VISIBLE);
            final RealmRoomMessage rm = mFList.get(position).getForwardMessage() != null ? mFList.get(position).getForwardMessage() : mFList.get(position);
            videoPath = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
            videoController.setAnchorView(touchImageView);
            videoController.setMediaPlayer(this);
            imgPlay.setVisibility(View.GONE);
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(getActivity(), Uri.parse(videoPath));
                if (mTextureView.getSurfaceTexture() == null) {
                    G.handler.postDelayed(new Runnable() {
                        @Override public void run() {
                            setMediaPlayer(mMediaPlayer, mTextureView, imgPlay, touchImageView);
                        }
                    }, 500);
                } else {
                    setMediaPlayer(mMediaPlayer, mTextureView, imgPlay, touchImageView);
                }
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            //mMediaPlayer.seekTo(100);
        }

        private void setMediaPlayer(MediaPlayer mMediaPlayer, final TextureView mTextureView, final ImageView imgPlay, final TouchImageView touchImageView) {
            Surface surfaceTexture = new Surface(mTextureView.getSurfaceTexture());
            mMediaPlayer.setSurface(surfaceTexture);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepareAsync();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override public void onPrepared(MediaPlayer mp) {

                    getRealSize(mp, mTextureView);
                    imgPlay.setVisibility(View.GONE);
                    mp.start();
                    mp.seekTo(10);
                    mTextureView.setVisibility(View.VISIBLE);
                    touchImageView.setVisibility(View.GONE);
                    videoController.setEnabled(true);
                    videoController.show();
                }
            });
        }

        private void getRealSize(MediaPlayer mp, TextureView mTextureView) {
            //Get the dimensions of the video
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            //Get the width of the screen
            int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();

            //Get the SurfaceView layout parameters
            ViewGroup.LayoutParams lp = mTextureView.getLayoutParams();

            //Set the width of the SurfaceView to the width of the screen
            lp.width = screenWidth;

            //Set the height of the SurfaceView to match the aspect ratio of the video
            //be sure to cast these as floats otherwise the calculation will likely be 0
            lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);

            //Commit the layout parameters
            mTextureView.setLayoutParams(lp);
        }

        @Override public void start() {
            if (mMediaPlayer != null) mMediaPlayer.start();
        }

        @Override public void pause() {
            if (mMediaPlayer != null) mMediaPlayer.pause();
        }

        @Override public int getDuration() {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getDuration();
            }
            return 0;
        }

        @Override public int getCurrentPosition() {
            if (mMediaPlayer != null) {
                return mMediaPlayer.getCurrentPosition();
            }
            return 0;
        }

        @Override public void seekTo(int pos) {
            if (mMediaPlayer != null) mMediaPlayer.seekTo(pos);
        }

        @Override public boolean isPlaying() {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        }

        @Override public int getBufferPercentage() {
            return 0;
        }

        @Override public boolean canPause() {
            return true;
        }

        @Override public boolean canSeekBackward() {
            return true;
        }

        @Override public boolean canSeekForward() {
            return true;
        }

        @Override public int getAudioSessionId() {
            return 0;
        }

        @Override public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        }

        @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }

    public String getFilePath(String token, String fileName, ProtoGlobal.RoomMessageType messageType) {

        String result = "";

        Realm realm = Realm.getDefaultInstance();
        RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();

        if (attachment != null) {
            if (attachment.getLocalFilePath() != null) result = attachment.getLocalFilePath();
        }

        if (result.length() < 1) result = AndroidUtils.suitableAppFilePath(messageType) + "/" + token + "_" + fileName;

        realm.close();

        return result;
    }

    public String getThumpnailPath(String token, String fileName) {

        String result = "";

        Realm realm = Realm.getDefaultInstance();
        RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();

        if (attachment != null) {
            if (attachment.getLocalThumbnailPath() != null) result = attachment.getLocalThumbnailPath();
        }

        if (result.length() < 1) result = G.DIR_TEMP + "/" + "thumb_" + token + "_" + AppUtils.suitableThumbFileName(fileName);

        realm.close();

        return result;
    }

    @Override public void onDestroy() {
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
    }

    @Override public void onDetach() {
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
    }
}