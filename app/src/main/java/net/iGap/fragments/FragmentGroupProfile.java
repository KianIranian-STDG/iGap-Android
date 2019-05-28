package net.iGap.fragments;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityGroupProfileBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAvatarDelete;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.SUID;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestGroupAvatarAdd;
import net.iGap.request.RequestGroupKickAdmin;
import net.iGap.request.RequestGroupKickMember;
import net.iGap.request.RequestGroupKickModerator;
import net.iGap.viewmodel.FragmentGroupProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;


/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */
public class FragmentGroupProfile extends BaseFragment implements OnGroupAvatarResponse, OnGroupAvatarDelete {

    private static final String ROOM_ID = "RoomId";
    private static final String IS_NOT_JOIN = "is_not_join";
    public static OnBackFragment onBackFragment;
    /*NestedScrollView nestedScrollView;*/
    AttachFile attachFile;
    private CircleImageView imvGroupAvatar;
    /*private AppBarLayout appBarLayout;*/
    private String pathSaveImage;
    /*private ProgressBar prgWait;*/
    private Fragment fragment;
    private FragmentGroupProfileViewModel viewModel;
    private ActivityGroupProfileBinding binding;


    public static FragmentGroupProfile newInstance(long roomId, Boolean isNotJoin) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putBoolean(IS_NOT_JOIN, isNotJoin);
        FragmentGroupProfile fragment = new FragmentGroupProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_group_profile, container, false);
        viewModel = new FragmentGroupProfileViewModel(this, getArguments());
        binding.setFragmentGroupProfileViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                        viewModel.onClickRippleMenu(view);
                    }

                    @Override
                    public void onSecondRightIconClickListener(View view) {
                        new HelperFragment(EditGroupFragment.newInstance(viewModel.roomId)).setReplace(false).load();
                    }
                });
        // because actionbar not in this view do that and not correct in viewModel
        if (viewModel.isNotJoin){
            t.getSecondRightButton().setVisibility(View.GONE);
        }
        binding.toolbar.addView(t.getView());
        imvGroupAvatar = t.getGroupAvatar();
        imvGroupAvatar.setOnClickListener(v -> viewModel.onClickRippleGroupAvatar());

        viewModel.callbackGroupName.observe(this, s -> t.getGroupName().setText(s));

        viewModel.callbackMemberNumber.observe(this, s -> t.getGroupMemberCount().setText(String.format("%s %s", s, getString(R.string.member))));

        fragment = this;
        onBackFragment = new OnBackFragment() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };


        initComponent(view);

        attachFile = new AttachFile(G.fragmentActivity);
        G.onGroupAvatarResponse = this;
        G.onGroupAvatarDelete = this;

        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
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
                            binding.loading.setProgress(progress);
                        } else {
                            new RequestGroupAvatarAdd().groupAvatarAdd(viewModel.roomId, struct.token);
                        }
                    }

                    @Override
                    public void OnError() {
                        hideProgressBar();
                    }
                });
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
        //ToDo: change code. this code is so bad

    }

    /*Change group avatar result*/
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
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
                        ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                        FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
                        new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();

                    } else {
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true);

                        FragmentEditImage.insertItemList(AttachFile.imagePath, false);
                        new HelperFragment(FragmentEditImage.newInstance(AttachFile.imagePath, false, false, 0)).setReplace(false).load();
                    }

                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data.getData() == null) {
                        return;
                    }
                    ImageHelper.correctRotateImage(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), true);

                    FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                    new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();

                    break;
            }
        }
    }*/

    /**
     * ************************************** methods **************************************
     */


    private void initComponent(final View view) {

        AppUtils.setProgresColler(binding.loading);

        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                avatarHandler.avatarDelete(new ParamWithAvatarType(imvGroupAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM), mAvatarId);
            }
        };

        showAvatar();
    }


    //dialog for choose pic from gallery or camera
    private void startDialogSelectPicture(int r) {
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                if (which == 0) {
                    try {
                        //attachFile.requestOpenGalleryForImageSingleSelect();
                        attachFile.requestOpenGalleryForImageSingleSelect(fragment);
                        //HelperPermision.getStoragePermision(context, new OnGetPermission() {
                        //    @Override
                        //    public void Allow() {
                        //        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //        intent.setType("image/*");
                        //        fragment.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                        //    }
                        //
                        //    @Override
                        //    public void deny() {
                        //
                        //    }
                        //});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (which == 1) {
                    if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { // camera

                        try {
                            HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                                @Override
                                public void Allow() {
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
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentGroupProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(FragmentGroupProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAvatar() {
        avatarHandler.getAvatar(new ParamWithAvatarType(imvGroupAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (binding.loading != null) {
                    binding.loading.setVisibility(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                binding.loading.setVisibility(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }


    /**
     * ************************************** Callbacks **************************************
     */
    //
    @Override
    public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {
        hideProgressBar();
        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            showAvatar();
        } else {
            avatarHandler.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }
    }

    @Override
    public void onAvatarAddError() {
        hideProgressBar();
    }

    @Override
    public void onDeleteAvatar(long roomId, long avatarId) {
        hideProgressBar();
        avatarHandler.avatarDelete(new ParamWithAvatarType(imvGroupAvatar, roomId).avatarType(AvatarHandler.AvatarType.ROOM), avatarId);
    }

    @Override
    public void onDeleteAvatarError(int majorCode, int minorCode) {

    }


    @Override
    public void onTimeOut() {

        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);

            }
        });
    }


    /**
     * if user was admin set  role to member
     */
    public void kickAdmin(final long memberID) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestGroupKickAdmin().groupKickAdmin(viewModel.roomId, memberID);
            }
        }).show();
    }

    /**
     * delete this member from list of member group
     */
    public void kickMember(final long memberID) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_kick_this_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestGroupKickMember().groupKickMember(viewModel.roomId, memberID);
            }
        }).show();
    }

    public void kickModerator(final long memberID) {

        new MaterialDialog.Builder(G.fragmentActivity).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestGroupKickModerator().groupKickModerator(viewModel.roomId, memberID);
            }
        }).show();
    }

    public interface OnBackFragment {
        void onBack();
    }

}

