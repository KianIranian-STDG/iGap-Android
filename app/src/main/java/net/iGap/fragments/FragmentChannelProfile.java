package net.iGap.fragments;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityProfileChannelBinding;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.OnAvatarChange;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnChannelAvatarDelete;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.request.RequestChannelKickAdmin;
import net.iGap.request.RequestChannelKickMember;
import net.iGap.request.RequestChannelKickModerator;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.viewmodel.FragmentChannelProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FragmentChannelProfile extends BaseFragment /*implements OnChannelAvatarAdd, OnChannelAvatarDelete*/ {


    public static final String FRAGMENT_TAG = "FragmentChannelProfile";
    private static final String ROOM_ID = "RoomId";
    private static final String IS_NOT_JOIN = "is_not_join";
    public static OnBackFragment onBackFragment;
    private String pathSaveImage;
    private AttachFile attachFile;
    private FragmentChannelProfileViewModel viewModel;
    private ActivityProfileChannelBinding binding;
    private long roomId;

    private CircleImageView imvChannelAvatar;


    public static FragmentChannelProfile newInstance(long roomId, Boolean isNotJoin) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putBoolean(IS_NOT_JOIN, isNotJoin);
        FragmentChannelProfile fragment = new FragmentChannelProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_profile_channel, container, false);
        roomId = -1;
        boolean v = false;
        if (getArguments() != null) {
            roomId = getArguments().getLong(ROOM_ID);
            v = getArguments().getBoolean(IS_NOT_JOIN);
        }
        viewModel = new FragmentChannelProfileViewModel(roomId, v);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ToDo:Move to edit channel fragment
        /*G.onChannelAvatarAdd = this;
        G.onChannelAvatarDelete = this;*/

        HelperToolbar t = HelperToolbar.create().setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.more_icon, R.string.edit_icon)
                .setGroupProfile(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        showPopUp();
                    }

                    @Override
                    public void onSecondRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), EditChannelFragment.newInstance(viewModel.roomId)).setReplace(false).load();
                        }
                    }
                });
        // because actionbar not in this view do that and not correct in viewModel
        /*if (isNotJoin) {
            settingVisibility.set(View.GONE);
        }*/
        if (viewModel.isNotJoin) {
            t.getSecondRightButton().setVisibility(View.GONE);
        }
        binding.toolbar.addView(t.getView());
        imvChannelAvatar = t.getGroupAvatar();
        imvChannelAvatar.setOnClickListener(v -> viewModel.onClickCircleImage());
        viewModel.channelName.observe(this, s -> t.getGroupName().setText(s));
        viewModel.subscribersCount.observe(this, s -> t.getGroupMemberCount().setText(String.format("%s %s", s, getString(R.string.subscribers_title))));
        viewModel.menuPopupVisibility.observe(this, integer -> {
            if (integer != null) {
                t.getRightButton().setVisibility(integer);
            }
        });

        onBackFragment = this::popBackStackFragment;

        attachFile = new AttachFile(G.fragmentActivity);

        AppUtils.setProgresColler(binding.loading);
        setAvatar();


        viewModel.isMuteNotificationChangeListener.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isChecked) {
                binding.enableNotification.setChecked(isChecked);

                new RequestClientMuteRoom().muteRoom(roomId, !isChecked);
            }
        });

        viewModel.goToShowAvatarPage.observe(this, roomId -> {
            if (getActivity() != null && roomId != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.channel)).setReplace(false).load();
            }
        });

        /*FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                final long finalMAvatarId = mAvatarId;
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        avatarHandler.avatarDelete(new ParamWithAvatarType(imgCircleImageView, fragmentChannelProfileViewModel.roomId)
                                .avatarType(AvatarHandler.AvatarType.ROOM).turnOffCache().onAvatarChange(new OnAvatarChange() {
                                    @Override
                                    public void onChange(boolean fromCache) {
                                        imgCircleImageView.setPadding(0, 0, 0, 0);
                                    }
                                }), finalMAvatarId);
                    }
                });
            }
        };*/
        /*FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message, HashMap<String, StructBottomSheet> textImageList) {
                pathSaveImage = null;
                pathSaveImage = path;
                long avatarId = SUID.id().get();
                long lastUploadedAvatarId = avatarId + 1L;

                showProgressBar();
                HelperUploadFile.startUploadTaskAvatar(pathSaveImage, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (progress < 100) {
                            prgWait.setProgress(progress);
                        } else {
                            new RequestChannelAvatarAdd().channelAvatarAdd(fragmentChannelProfileViewModel.roomId, struct.token);
                        }
                    }

                    @Override
                    public void OnError() {
                        hideProgressBar();
                    }
                });
            }
        };*/
    }

    @Override
    public void onResume() {
        super.onResume();
        /*viewModel.onResume();*/
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FragmentChat fragment = (FragmentChat) getFragmentManager().findFragmentByTag(FragmentChat.class.getName());
        if (fragment != null && fragment.isVisible()) {
            fragment.onResume();
        }
    }

    private void setAvatar() {
        avatarHandler.getAvatar(new ParamWithAvatarType(imvChannelAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
    }

    private void showPopUp() {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.add_to_home_screen));
        /*if (viewModel.isPrivate) {
            items.add(G.fragmentActivity.getString(R.string.channel_title_convert_to_public));
        } else {
            items.add(G.fragmentActivity.getString(R.string.channel_title_convert_to_private));
        }*/
        new TopSheetDialog(G.fragmentActivity).setListData(items, -1, position -> {
            /*isPopup = true;
            if (viewModel.isPrivate) {
                convertToPublic(view);
            } else {
                convertToPrivate();
            }*/
        }).show();
    }

    //Todo: Move to edit channel fragment
    //********** select picture
    /*private void startDialogSelectPicture(int r) {

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(G.fragmentActivity.getResources().getString(R.string.from_camera))) {

                    if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                        try {
                            HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                                @Override
                                public void Allow() {
                                    // this dialog show 2 way for choose image : gallery and camera
                                    dialog.dismiss();
                                    useCamera();
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(G.fragmentActivity, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        new AttachFile(G.fragmentActivity).requestOpenGalleryForImageSingleSelect(FragmentChannelProfile.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).show();
    }*/

    //todo: move to edit channel fragment
    /*private void useCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentChannelProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(FragmentChannelProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/


    ////************************************************** interfaces
    //
    ////***On Add Avatar Response From Server

    /*@Override
    public void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar) {
        */

    /**
     * if another account do this action we haven't avatar source and have
     * to download avatars . for do this action call HelperAvatar.getAvatar
     *//*

        hideProgressBar();
        if (pathSaveImage == null) {
            setAvatar();
        } else {
            avatarHandler.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                            setImage(avatarPath);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }
    }*/

    /*@Override
    public void onAvatarAddError() {
        hideProgressBar();
    }*/

    //***On Avatar Delete
    /*@Override
    public void onChannelAvatarDelete(final long roomId, final long avatarId) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                avatarHandler.avatarDelete(new ParamWithAvatarType(imvChannelAvatar, roomId)
                        .avatarType(AvatarHandler.AvatarType.ROOM).turnOffCache().onAvatarChange(new OnAvatarChange() {
                            @Override
                            public void onChange(boolean fromCache) {
                                imvChannelAvatar.setPadding(0, 0, 0, 0);
                            }
                        }), avatarId);
            }
        });
    }*/

    /*@Override
    public void onError(int majorCode, int minorCode) {
        viewModel.showLoading.setValue(false);
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.normal_error), false);
            }
        });
    }*/

    /*@Override
    public void onTimeOut() {
        viewModel.showLoading.setValue(false);
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);
            }
        });
    }*/


    //*** set avatar image

    private void setImage(final String imagePath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (new File(imagePath).exists()) {
                    imvChannelAvatar.setPadding(0, 0, 0, 0);
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imvChannelAvatar);
                }
            }
        });
    }

    //*** notification and sounds


    //*** onActivityResult

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);

        /*
         * If it's in the app and the screen lock is activated after receiving the result of the camera and .... The page code is displayed.
         * The wizard will  be set ActivityMain.isUseCamera = true to prevent the page from being opened....
         *//*
        if (G.isPassCode) ActivityMain.isUseCamera = true;

        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = SUID.id().get();

            if (FragmentEditImage.textImageList != null) FragmentEditImage.textImageList.clear();
            if (FragmentEditImage.itemGalleryList != null)
                FragmentEditImage.itemGalleryList.clear();

            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true); //rotate image

                        FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
                        new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
                    } else {
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true); //rotate image

                        FragmentEditImage.insertItemList(AttachFile.imagePath, false);
                        new HelperFragment(FragmentEditImage.newInstance(AttachFile.imagePath, false, false, 0)).setReplace(false).load();
                    }
                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data.getData() == null) {
                        return;
                    }
                    ImageHelper.correctRotateImage(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), true); //rotate image
                    FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                    new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
                    break;
            }
        }
    }*/


    //********* kick user from roles
    public void kickMember(final Long peerId) {
        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_kick_this_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestChannelKickMember().channelKickMember(viewModel.roomId, peerId);
            }
        }).show();
    }

    public void kickModerator(final Long peerId) {
        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestChannelKickModerator().channelKickModerator(viewModel.roomId, peerId);
            }
        }).show();
    }

    public void kickAdmin(final Long peerId) {
        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestChannelKickAdmin().channelKickAdmin(viewModel.roomId, peerId);
            }
        }).show();
    }

    /*private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWait != null) {
                    prgWait.setVisibility(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }*/

    /*private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWait != null) {
                    prgWait.setVisibility(View.GONE);
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }*/

    public interface OnBackFragment {
        void onBack();
    }

}
