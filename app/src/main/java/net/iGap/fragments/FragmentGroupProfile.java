package net.iGap.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.ActivityGroupProfileBinding;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGroupAvatarDelete;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.OnGroupCheckUsername;
import net.iGap.interfaces.OnGroupUpdateUsername;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.MEditText;
import net.iGap.module.SUID;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupCheckUsername;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestGroupAddMember;
import net.iGap.request.RequestGroupAvatarAdd;
import net.iGap.request.RequestGroupCheckUsername;
import net.iGap.request.RequestGroupKickAdmin;
import net.iGap.request.RequestGroupKickMember;
import net.iGap.request.RequestGroupKickModerator;
import net.iGap.request.RequestGroupUpdateUsername;
import net.iGap.viewmodel.FragmentGroupProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static android.content.Context.CLIPBOARD_SERVICE;


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

    private FragmentGroupProfileViewModel viewModel;
    private ActivityGroupProfileBinding binding;


    AttachFile attachFile;
    private CircleImageView imvGroupAvatar;
    private String pathSaveImage;

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
        long roomId = 0;
        boolean isNotJoin = true;
        if (getArguments() != null) {
            roomId = getArguments().getLong(ROOM_ID);
            isNotJoin = getArguments().getBoolean(IS_NOT_JOIN);
        }
        viewModel = new FragmentGroupProfileViewModel(this ,roomId, isNotJoin);
        binding.setViewModel(viewModel);
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
                        viewModel.onClickRippleMenu();
                    }

                    @Override
                    public void onSecondRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), EditGroupFragment.newInstance(viewModel.roomId)).setReplace(false).load();
                        }
                    }
                });
        // because actionbar not in this view do that and not correct in viewModel
        binding.toolbar.addView(t.getView());
        imvGroupAvatar = t.getGroupAvatar();
        imvGroupAvatar.setOnClickListener(v -> viewModel.onClickRippleGroupAvatar());

        viewModel.groupName.observe(this, s -> t.getGroupName().setText(s));

        viewModel.groupNumber.observe(this, s -> t.getGroupMemberCount().setText(String.format("%s %s", s, getString(R.string.member))));

        viewModel.showMoreMenu.observe(this, isShow -> {
            if (isShow != null) {
                t.getRightButton().setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.showEditButton.observe(this, isShow -> {
            if (isShow != null) {
                t.getSecondRightButton().setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.showNotificationDialog.observe(this, isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                new MaterialDialog.Builder(getActivity()).title(R.string.st_popupNotification).items(R.array.notifications_notification).negativeText(R.string.B_cancel).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        viewModel.setNotificationState(which);
                    }
                }).show();
            }
        });

        viewModel.goToShearedMediaPage.observe(this, model -> {
            if (getActivity() != null && model != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentShearedMedia.newInstance(model)).setReplace(false).load();
            }
        });

        viewModel.goToShowAvatarPage.observe(this, roomId -> {
            if (getActivity() != null && roomId != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.group)).setReplace(false).load();
            }
        });

        viewModel.showMenu.observe(this, menuList -> {
            if (getActivity() != null && menuList != null) {
                new TopSheetDialog(getActivity()).setListDataWithResourceId(menuList, -1, position -> {
                    if (menuList.get(position).equals(getString(R.string.clear_history))) {
                        new MaterialDialog.Builder(getActivity()).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (FragmentChat.onComplete != null) {
                                    FragmentChat.onComplete.complete(false, viewModel.roomId + "", "");
                                }
                            }
                        }).negativeText(R.string.no).show();
                    } else if (menuList.get(position).equals(getString(R.string.group_title_convert_to_public)) || menuList.get(position).equals(getString(R.string.group_title_convert_to_private))) {
                        viewModel.convertMenuClick();
                    }
                }).show();
            }
        });

        viewModel.goToShowMemberPage.observe(this, type -> {
            if (getActivity() != null && type != null) {
                FragmentShowMember fragment = FragmentShowMember.newInstance2(this, viewModel.roomId, viewModel.role.toString(), G.userId, type, viewModel.isNeedgetContactlist , true);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        viewModel.showDialogConvertToPublic.observe(this, isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                new MaterialDialog.Builder(getActivity()).title(getString(R.string.group_title_convert_to_public)).content(getString(R.string.group_text_convert_to_public)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        setUsername();
                    }
                }).negativeText(R.string.no).show();
            }
        });

        viewModel.showDialogConvertToPrivate.observe(this, isShow -> {
            if (getActivity() != null && isShow != null && isShow) {
                new MaterialDialog.Builder(getActivity()).title(R.string.group_title_convert_to_private).content(R.string.group_text_convert_to_private).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        viewModel.sendRequestRemoveGroupUsername();
                    }
                }).negativeText(R.string.no).show();
            }
        });

        viewModel.showRequestError.observe(this, errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(getString(errorMessage), false);
            }
        });

        viewModel.goToShowCustomListPage.observe(this, listItem -> {
            if (getActivity() != null && listItem != null) {
                Fragment fragment = ShowCustomList.newInstance(listItem, (result, message, countForShowLastMessage, list) -> {
                    for (int i = 0; i < list.size(); i++) {
                        new RequestGroupAddMember().groupAddMember(viewModel.roomId, list.get(i).peerId, RealmRoomMessage.findCustomMessageId(viewModel.roomId, countForShowLastMessage));
                    }
                });

                Bundle bundle = new Bundle();
                bundle.putBoolean("DIALOG_SHOWING", true);
                bundle.putLong("COUNT_MESSAGE", 0);
                fragment.setArguments(bundle);

                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        viewModel.goBack.observe(this, isGoBack -> {
            if (isGoBack != null && isGoBack) {
                popBackStackFragment();
            }
        });

        viewModel.groupDescription.observe(this, groupDescription -> {
            if (getActivity() != null && groupDescription != null) {
                binding.description.setText(HelperUrl.setUrlLink(getActivity(), groupDescription, true, false, null, true));
            }
        });

        viewModel.goToRoomListPage.observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() instanceof ActivityMain && isGo != null) {
                Log.wtf(this.getClass().getName(),"goToRoomListPage observe");
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                /*new HelperFragment(getActivity().getSupportFragmentManager()).popBackStack(2);*/
            }
        });

        viewModel.showDialogCopyLink.observe(this, link -> {
            if (getActivity() != null && link != null) {

                LinearLayout layoutGroupLink = new LinearLayout(getActivity());
                layoutGroupLink.setOrientation(LinearLayout.VERTICAL);
                View viewRevoke = new View(getActivity());
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                TextInputLayout inputGroupLink = new TextInputLayout(getActivity());
                MEditText edtLink = new MEditText(getActivity());
                edtLink.setHint(getString(R.string.group_link_hint_revoke));
                edtLink.setTypeface(G.typeface_IRANSansMobile);
                edtLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dp14));
                edtLink.setText(link);
                edtLink.setTextColor(getResources().getColor(R.color.text_edit_text));
                edtLink.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtLink.setPadding(0, 8, 0, 8);
                edtLink.setEnabled(false);
                edtLink.setSingleLine(true);
                inputGroupLink.addView(edtLink);
                inputGroupLink.addView(viewRevoke, viewParams);

                TextView txtLink = new AppCompatTextView(getActivity());
                txtLink.setText(Config.IGAP_LINK_PREFIX);
                txtLink.setTextColor(getResources().getColor(R.color.gray_6c));

                viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtLink.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutGroupLink.addView(inputGroupLink, layoutParams);
                layoutGroupLink.addView(txtLink, layoutParams);

                MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title(R.string.group_link)
                        .positiveText(R.string.array_Copy)
                        .customView(layoutGroupLink, true)
                        .widgetColor(Color.parseColor(G.appBarColor))
                        .negativeText(R.string.no)
                        .onPositive((dialog1, which) -> {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("LINK_GROUP", link);
                            clipboard.setPrimaryClip(clip);
                        })
                        .build();

                dialog.show();
            }
        });

        initComponent();

        attachFile = new AttachFile(getActivity());
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


    private void initComponent() {

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
    /*private void startDialogSelectPicture(int r) {
        new MaterialDialog.Builder(getActivity()).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
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
    }*/

    /*private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(getActivity()).dispatchTakePictureIntent(FragmentGroupProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(getActivity()).requestTakePicture(FragmentGroupProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void showAvatar() {
        avatarHandler.getAvatar(new ParamWithAvatarType(imvGroupAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (binding.loading != null) {
                    binding.loading.setVisibility(View.VISIBLE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                binding.loading.setVisibility(View.GONE);
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    //TODO: change this and create custom dialog and handle request in it and ...
    //TODO: because no time be this and this code is not correct and should be change it
    public void setUsername() {
        final LinearLayout layoutUserName = new LinearLayout(getContext());
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(getContext());
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(getContext());
        final MEditText edtUserName = new MEditText(getContext());
        edtUserName.setHint(getString(R.string.group_title_set_username));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            edtUserName.setTextDirection(View.TEXT_DIRECTION_LTR);
        }
        edtUserName.setTypeface(G.typeface_IRANSansMobile);
        edtUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.dp14));
        //TODO: fixed this and this will set viewModel
        if (viewModel.isPopup) {
            edtUserName.setText(Config.IGAP_LINK_PREFIX);
        } else {
            edtUserName.setText(Config.IGAP_LINK_PREFIX + viewModel.linkUsername);
        }

        edtUserName.setTextColor(getContext().getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(getContext().getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(getContext().getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(getContext().getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);
        ProgressBar progressBar = new ProgressBar(getContext());
        LinearLayout.LayoutParams progParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(progParams);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        layoutUserName.addView(progressBar);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(getContext()).title(R.string.st_username).positiveText(R.string.save).customView(layoutUserName, true).widgetColor(Color.parseColor(G.appBarColor)).negativeText(R.string.B_cancel).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        edtUserName.setSelection((edtUserName.getText().toString().length()));
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtUserName.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(edtUserName, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().startsWith(Config.IGAP_LINK_PREFIX)) {
                    edtUserName.setText(Config.IGAP_LINK_PREFIX);
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                } else {
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                }


                if (HelperString.regexCheckUsername(editable.toString().replace(Config.IGAP_LINK_PREFIX, ""))) {
                    if (G.userLogin) {
                        String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
                        new RequestGroupCheckUsername().GroupCheckUsername(viewModel.roomId, userName, new OnGroupCheckUsername() {
                            @Override
                            public void onGroupCheckUsername(final ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status status) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.AVAILABLE) {

                                            positive.setEnabled(true);
                                            inputUserName.setErrorEnabled(true);
                                            inputUserName.setError("");
                                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.INVALID) {
                                            positive.setEnabled(false);
                                            inputUserName.setErrorEnabled(true);
                                            inputUserName.setError(getString(R.string.INVALID));
                                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.TAKEN) {
                                            positive.setEnabled(false);
                                            inputUserName.setErrorEnabled(true);
                                            inputUserName.setError(getString(R.string.TAKEN));
                                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                                            positive.setEnabled(false);
                                            inputUserName.setErrorEnabled(true);
                                            inputUserName.setError("" + getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                if (majorCode == 5) {
                                    positive.setEnabled(false);
                                    inputUserName.setErrorEnabled(true);
                                    inputUserName.setError(getString(R.string.network_error));
                                } else {
                                    positive.setEnabled(false);
                                    inputUserName.setErrorEnabled(true);
                                    inputUserName.setError(getString(R.string.server_error));
                                }
                            }
                        });
                    } else {
                        positive.setEnabled(false);
                        inputUserName.setErrorEnabled(true);
                        inputUserName.setError(getString(R.string.network_error));
                    }
                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError(getString(R.string.INVALID));
                }
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
                if (G.userLogin) {
                    progressBar.setVisibility(View.VISIBLE);
                    positive.setEnabled(false);
                    new RequestGroupUpdateUsername().groupUpdateUsername(viewModel.roomId, userName, new OnGroupUpdateUsername() {
                        @Override
                        public void onGroupUpdateUsername(final long roomId, final String username) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    positive.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    viewModel.isPrivate = false;
                                    dialog.dismiss();
                                    viewModel.linkUsername = username;
                                    viewModel.setTextGroupLik();
                                }
                            });
                        }

                        @Override
                        public void onError(final int majorCode, int minorCode, final int time) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    positive.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    switch (majorCode) {
                                        case 5:
                                            HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                                        case 368:
                                            if (dialog.isShowing()) dialog.dismiss();
                                            dialogWaitTime(R.string.GROUP_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                                            break;
                                    }
                                }
                            });

                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    viewUserName.setBackgroundColor(getContext().getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        // check each word with server
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideKeyboard();
            }
        });

        dialog.show();
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

        View v = dialog.getCustomView();

        final TextView remindTime = v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
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
                HelperError.showSnackMessage(getString(R.string.time_out), false);
            }
        });
    }


    /**
     * if user was admin set  role to member
     */
    public void kickAdmin(final long memberID) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity()).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    new RequestGroupKickAdmin().groupKickAdmin(viewModel.roomId, memberID);
                }
            }).show();
        }
    }

    /**
     * delete this member from list of member group
     */
    public void kickMember(final long memberID) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity()).content(R.string.do_you_want_to_kick_this_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestGroupKickMember().groupKickMember(viewModel.roomId, memberID);
                }
            }).show();
        }
    }

    public void kickModerator(final long memberID) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity()).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestGroupKickModerator().groupKickModerator(viewModel.roomId, memberID);
                }
            }).show();
        }
    }
}

