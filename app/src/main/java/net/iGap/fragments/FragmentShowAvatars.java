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
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.chrisbanes.photoview.PhotoView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.observers.interfaces.OnChannelAvatarDelete;
import net.iGap.observers.interfaces.OnComplete;
import net.iGap.observers.interfaces.OnGroupAvatarDelete;
import net.iGap.observers.interfaces.OnUserAvatarDelete;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelAvatarDelete;
import net.iGap.request.RequestChannelAvatarGetList;
import net.iGap.request.RequestGroupAvatarDelete;
import net.iGap.request.RequestGroupAvatarGetList;
import net.iGap.request.RequestUserAvatarDelete;
import net.iGap.request.RequestUserAvatarGetList;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.R.string.array_Delete_photo;

public class FragmentShowAvatars extends BaseFragment {

    public static final int mChatNumber = 1;
    public static final int mGroupNumber = 2;
    public static final int mChannelNumber = 3;
    public static final int mSettingNumber = 4;
    private static final String ARG_PEER_ID = "arg_peer_id";
    private static final String ARG_Type = "arg_type";
    public static OnComplete onComplete;
    public View appBarLayout;
    From from = From.chat;
    private TextView txtImageNumber;
    private TextView txtImageName;
    private LinearLayout toolbarShowImage;
    private boolean isShowToolbar = true;
    private ViewGroup ltImageName;
    private ViewPager viewPager;
    private long mPeerId = -1;
    private GroupChatRole roleGroup;
    private ChannelChatRole roleChannel;
    private int avatarListSize = 0;
    private FragmentShowAvatars.AdapterViewPager mAdapter;
    private RealmResults<RealmAvatar> avatarList;

    public static FragmentShowAvatars newInstance(long peerId, FragmentShowAvatars.From from) {
        Bundle args = new Bundle();
        args.putLong(ARG_PEER_ID, peerId);
        args.putSerializable(ARG_Type, from);

        FragmentShowAvatars fragment = new FragmentShowAvatars();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getIntentData(this.getArguments())) {
            initComponent(view);
        } else {
            popBackStackFragment();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (avatarList != null) {
            avatarList.removeAllChangeListeners();
        }

        if (appBarLayout != null) {
            appBarLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        if (appBarLayout != null) appBarLayout.setVisibility(View.GONE);

        super.onAttach(context);
    }

    private boolean getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image

            // init passed data through bundle
            mPeerId = getArguments().getLong(ARG_PEER_ID, -1);

            From result = (From) getArguments().getSerializable(ARG_Type);

            if (result != null) from = result;

            fillListAvatar(from);

            return avatarListSize > 0;
        } else {
            return false;
        }
    }

    private void initComponent(View view) {

        //ViewGroup rooShowImage = (ViewGroup) view.findViewById(R.id.rooShowImage);
        //rooShowImage.setBackgroundColor(G.fragmentActivity.getResources().getColor(R.color.black));

        RippleView rippleBack = view.findViewById(R.id.asi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
            }
        });

        RippleView rippleMenu = view.findViewById(R.id.asi_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(rippleView -> {

            if (getContext() == null) return;

            List<Integer> items = new ArrayList<>();
            items.add(R.string.save_to_gallery);

            switch (from) {
                case setting:
                    items.add(R.string.array_Delete_photo);
                    break;
                case group:
                    if (roleGroup == GroupChatRole.OWNER || roleGroup == GroupChatRole.ADMIN) {
                        items.add(R.string.array_Delete_photo);
                    }
                    break;
                case channel:
                    if (roleChannel == ChannelChatRole.OWNER || roleChannel == ChannelChatRole.ADMIN) {
                        items.add(R.string.array_Delete_photo);
                    }
                    break;
            }
            new TopSheetDialog(getContext()).setListDataWithResourceId(items, -1, position -> {
                if (items.get(position) == R.string.save_to_gallery) {
                    saveToGallery();
                } else if (items.get(position) == array_Delete_photo) {
                    switch (from) {
                        case setting:
                            deletePhotoSetting();
                            break;
                        case group:
                            deletePhotoGroup();
                            break;
                        case channel:
                            deletePhotoChannel();
                            break;
                        case chat:
                            deletePhotoChat();
                            break;
                    }
                }
            }).show();
        });
        viewPager = view.findViewById(R.id.asi_view_pager);

        txtImageNumber = view.findViewById(R.id.asi_txt_image_number);
        txtImageName = view.findViewById(R.id.asi_txt_image_name);
        ltImageName = view.findViewById(R.id.asi_layout_image_name);
        ltImageName.setVisibility(View.GONE);

        toolbarShowImage = view.findViewById(R.id.toolbarShowImage);

        initViewPager();
    }

    private void fillListAvatar(From from) {


        boolean isRoomExist = false;

        switch (from) {
            case chat:
            case setting:
                RealmRegisteredInfo user = DbManager.getInstance().doRealmTask(realm -> {
                    return RealmRegisteredInfo.getRegistrationInfo(realm, mPeerId);
                });
                if (user != null) {
                    new RequestUserAvatarGetList().userAvatarGetList(mPeerId);
                    isRoomExist = true;
                }
                break;
            case group:
                RealmRoom roomGroup = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.where(RealmRoom.class).equalTo("id", mPeerId).findFirst();
                });
                if (roomGroup != null) {
                    new RequestGroupAvatarGetList().groupAvatarGetList(mPeerId);
                    isRoomExist = true;
                    roleGroup = roomGroup.getGroupRoom().getRole();
                }
                break;
            case channel:
                RealmRoom roomChannel = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.where(RealmRoom.class).equalTo("id", mPeerId).findFirst();
                });
                if (roomChannel != null) {
                    new RequestChannelAvatarGetList().channelAvatarGetList(mPeerId);
                    isRoomExist = true;
                    roleChannel = roomChannel.getChannelRoom().getRole();
                }
                break;
        }

        if (isRoomExist) {

            avatarList = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmAvatar.class).equalTo("ownerId", mPeerId).findAll().sort("id", Sort.DESCENDING);
            });
            avatarListSize = avatarList.size();
        }
    }

    private void initViewPager() {

        mAdapter = new FragmentShowAvatars.AdapterViewPager();
        viewPager.setAdapter(mAdapter);

        avatarList.addChangeListener(new RealmChangeListener<RealmResults<RealmAvatar>>() {
            @Override
            public void onChange(RealmResults<RealmAvatar> element) {

                if (avatarListSize != element.size()) {

                    avatarListSize = element.size();

                    if (avatarListSize > 0) {
                        mAdapter = new AdapterViewPager();
                        viewPager.setAdapter(mAdapter);
                        txtImageNumber.setText(viewPager.getCurrentItem() + 1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + avatarListSize);
                        if (HelperCalander.isPersianUnicode) {
                            txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
                        }
                    } else {
                        //  G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentShowAvatars.this).commit();

                        popBackStackFragment();
                    }
                }
            }
        });

        txtImageNumber.setText(1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + avatarList.size());
        if (HelperCalander.isPersianUnicode) {
            txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
        }

        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                txtImageNumber.setText(position + 1 + " " + G.fragmentActivity.getResources().getString(R.string.of) + " " + avatarList.size());
                if (HelperCalander.isPersianUnicode) {
                    txtImageNumber.setText(HelperCalander.convertToUnicodeFarsiNumber(txtImageNumber.getText().toString()));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {

                final float normalizedPosition = Math.abs(Math.abs(position) - 1);
                view.setScaleX(normalizedPosition / 2 + 0.5f);
                view.setScaleY(normalizedPosition / 2 + 0.5f);
            }
        });
    }

    //***************************************************************************************

    private void showPopupMenu(int r) {
        MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).items(r).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                if (text.equals(G.fragmentActivity.getResources().getString(R.string.save_to_gallery))) {
                    saveToGallery();
                } else if (text.equals(G.fragmentActivity.getResources().getString(array_Delete_photo))) {
                    switch (from) {
                        case setting:
                            deletePhotoSetting();
                            break;
                        case group:
                            deletePhotoGroup();
                            break;
                        case channel:
                            deletePhotoChannel();
                            break;
                        case chat:
                            deletePhotoChat();
                            break;
                    }
                }

            }
        }).build();

        DialogAnimation.animationUp(dialog);

        dialog.show();
        //WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //layoutParams.copyFrom(dialog.getWindow().getAttributes());
        //layoutParams.width = (int) G.context.getResources().getDimension(R.dimen.dp200);
        //layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        //dialog.getWindow().setAttributes(layoutParams);
    }

    private void saveToGallery() {

        if (avatarList.get(viewPager.getCurrentItem()).getFile() != null) {
            String media = avatarList.get(viewPager.getCurrentItem()).getFile().getLocalFilePath();
            if (media != null) {
                File file = new File(media);
                if (file.exists()) {
                    HelperSaveFile.savePicToGallery(media, true);
                }
            } else {
                if (getContext() != null)
                    Toast.makeText(getContext(), R.string.file_not_download_yet, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //private void shareImage() {
    //
    //    RealmRoomMessage rm = mFList.get(viewPager.getCurrentItem());
    //
    //    if (rm != null) {
    //
    //        if (rm.getForwardMessage() != null) rm = rm.getForwardMessage();
    //
    //
    //        String path = getFilePath(rm.getAttachment().getToken(), rm.getAttachment().getName(), rm.getMessageType());
    //        File file = new File(path);
    //        if (file.exists()) {
    //
    //            Intent intent = new Intent(Intent.ACTION_SEND);
    //            intent.putExtra(Intent.EXTRA_TEXT, "iGap/download this image");
    //            Uri screenshotUri = Uri.parse(path);
    //
    //            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
    //            intent.setType("image/*");
    //            startActivity(Intent.createChooser(intent, G.fragmentActivity.getResources().getString(R.string.share_image_from_igap)));
    //        }
    //    }
    //}

    private void deletePhotoChannel() {

        G.onChannelAvatarDelete = new OnChannelAvatarDelete() {
            @Override
            public void onChannelAvatarDelete(long roomId, long avatarId) {
                G.handler.post(() -> {
                    if (onComplete != null)
                        onComplete.complete(true, "" + avatarId, "");
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };

        if (viewPager.getCurrentItem() >= avatarList.size()) {
            return;
        }

        new RequestChannelAvatarDelete().channelAvatarDelete(mPeerId, avatarList.get(viewPager.getCurrentItem()).getId());
    }

    private void deletePhotoGroup() {

        G.onGroupAvatarDelete = new OnGroupAvatarDelete() {
            @Override
            public void onDeleteAvatar(long roomId, final long avatarId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (onComplete != null) {
                            onComplete.complete(true, "" + avatarId, "");
                        }
                    }
                });
            }

            @Override
            public void onDeleteAvatarError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };

        if (viewPager.getCurrentItem() >= avatarList.size()) {
            return;
        }

        new RequestGroupAvatarDelete().groupAvatarDelete(mPeerId, avatarList.get(viewPager.getCurrentItem()).getId());
    }

    //******************************************************************************************************

    private void deletePhotoSetting() {

        G.onUserAvatarDelete = new OnUserAvatarDelete() {
            @Override
            public void onUserAvatarDelete(long avatarId, String token) {
                AvatarHandler.clearCacheForOwnerId(avatarId);
                if (onComplete != null) onComplete.complete(true, "" + avatarId, "");
            }

            @Override
            public void onUserAvatarDeleteError() {

            }
        };

        if (viewPager.getCurrentItem() >= avatarList.size()) {
            return;
        }

        new RequestUserAvatarDelete().userAvatarDelete(avatarList.get(viewPager.getCurrentItem()).getId());
    }

    private void deletePhotoChat() {

    }

    public enum From {
        chat(mChatNumber), group(mGroupNumber), channel(mChannelNumber), setting(mSettingNumber);

        public int value;

        From(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private class AdapterViewPager extends PagerAdapter {

        public AdapterViewPager() {

        }

        @Override
        public int getCount() {
            return avatarList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.show_image_or_video_sub_layout, container, false);

            final PhotoView zoomableImageView = layout.findViewById(R.id.sisl_touch_image_view);
            zoomableImageView.setZoomable(false);
            final ImageView imgPlay = layout.findViewById(R.id.imgPlay);
            imgPlay.setVisibility(View.GONE);

            final MessageProgress progress = layout.findViewById(R.id.progress);
            AppUtils.setProgresColor(progress.progressBar);

            final RealmAttachment ra = avatarList.get(position).getFile();


            if (getDownloader().isDownloading(ra.getCacheId())) {
                progress.withDrawable(R.drawable.ic_cancel, true);
                startDownload(position, progress, zoomableImageView);
            } else {
                progress.withDrawable(R.drawable.ic_download, true);
            }

            String path = ra.getLocalFilePath() != null ? ra.getLocalFilePath() : "";

            File file = new File(path);
            if (file.exists()) {
                loadImage(zoomableImageView, path);
                progress.setVisibility(View.GONE);
                zoomableImageView.setZoomable(true);
            } else {
                path = ra.getLocalThumbnailPath() != null ? ra.getLocalThumbnailPath() : "";
                file = new File(path);
                if (file.exists()) {
                    loadImage(zoomableImageView, path);
                } else {
                    // if thumpnail not exist download it
                    ProtoFileDownload.FileDownload.Selector selector = null;
                    long fileSize = 0;

                    if (ra.getSmallThumbnail() != null) {
                        selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                        fileSize = ra.getSmallThumbnail().getSize();
                    } else if (ra.getLargeThumbnail() != null) {
                        selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                        fileSize = ra.getLargeThumbnail().getSize();
                    }

                    if (selector != null && fileSize > 0) {
                        HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", ra.getToken(), ra.getUrl(), ra.getCacheId(), ra.getName(), fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(final String path, int progress) {

                                if (progress == 100) {
                                    G.currentActivity.runOnUiThread(() -> loadImage(zoomableImageView, path));
                                }
                            }

                            @Override
                            public void OnError(String token) {

                            }
                        });
                    }
                }
            }

            progress.setOnClickListener(view -> {

                String _cashId = avatarList.get(position).getFile().getCacheId();

                if (getDownloader().isDownloading(_cashId)) {
                    getDownloader().cancelDownload(_cashId);
                } else {
                    progress.withDrawable(R.drawable.ic_cancel, true);
                    startDownload(position, progress, zoomableImageView);
                }
            });

            zoomableImageView.setOnClickListener(view -> {
                if (isShowToolbar) {
                    toolbarShowImage.animate().setDuration(150).alpha(0F).start();
                    toolbarShowImage.setVisibility(View.GONE);
                    isShowToolbar = false;
                } else {
                    toolbarShowImage.animate().setDuration(150).alpha(1F).start();
                    toolbarShowImage.setVisibility(View.VISIBLE);
                    isShowToolbar = true;
                }
            });

            container.addView(layout);
            return layout;
        }

        private void loadImage(PhotoView img, String path) {
            G.imageLoader.displayImage(AndroidUtils.suitablePath(path), img);
            img.setVisibility(View.VISIBLE);
        }

        private void startDownload(int position, final MessageProgress progress, final PhotoView zoomableImageView) {
            final RealmAttachment ra = avatarList.get(position).getFile();
            final String dirPath = AndroidUtils.getFilePathWithCashId(ra.getCacheId(), ra.getName(), G.DIR_IMAGE_USER, false);

            progress.withOnProgress(() -> G.currentActivity.runOnUiThread(() -> {
                progress.withProgress(0);
                progress.setVisibility(View.GONE);
                zoomableImageView.setZoomable(true);
            }));


            HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE, System.currentTimeMillis() + "", ra.getToken(), ra.getUrl(), ra.getCacheId(), ra.getName(), ra.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, final int progres) {
                    G.currentActivity.runOnUiThread(() -> {
                        progress.withProgress(progres);
                        if (progres == 100) {
                            loadImage(zoomableImageView, path);
                        }
                    });

                }

                @Override
                public void OnError(String token) {
                    G.currentActivity.runOnUiThread(() -> {
                        progress.withProgress(0);
                        progress.withDrawable(R.drawable.ic_download, true);
                    });
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}