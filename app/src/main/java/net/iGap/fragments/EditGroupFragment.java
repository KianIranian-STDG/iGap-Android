package net.iGap.fragments;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vanniktech.emoji.EmojiPopup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentEditGroupBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AttachFile;
import net.iGap.module.SUID;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.viewmodel.EditGroupViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class EditGroupFragment extends BaseFragment implements FragmentEditImage.CompleteEditImage {

    private static final String ROOM_ID = "RoomId";

    private FragmentEditGroupBinding binding;
    private EditGroupViewModel viewModel;
    private AttachFile attachFile;
    private EmojiPopup emojiPopup;
    private boolean isInitEmoji = false;
    private boolean isEmojiShow = false;

    public static EditGroupFragment newInstance(long roomId) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        EditGroupFragment fragment = new EditGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_group, container, false);
        viewModel = new EditGroupViewModel(getArguments() != null ? getArguments().getLong(ROOM_ID) : -1);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarHandler.getAvatar(new ParamWithAvatarType(binding.groupAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.check_icon)
                .setDefaultTitle(G.context.getResources().getString(R.string.tab_edit))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        viewModel.setData(binding.groupNameEditText.getEditableText().toString(), binding.groupDescriptionEditText.getEditableText().toString());
                        hideKeyboard();
                    }
                });
        binding.toolbar.addView(mHelperToolbar.getView());
        //mHelperToolbar.getTextViewLogo().setText(R.string.tab_edit);

        attachFile = new AttachFile(G.fragmentActivity);

        viewModel.goToMembersPage.observe(this, b -> {
            if (b != null && b) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());
            }
        });

        viewModel.goToAdministratorPage.observe(this, b -> {
            if (b != null && b) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
            }
        });

        viewModel.goToPermissionPage.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean b) {

            }
        });

        viewModel.goBack.observe(this, aBoolean -> popBackStackFragment());

        viewModel.showSelectImageDialog.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialogSelectPicture();
            }
        });

        viewModel.showDialogChatHistory.observe(this, aBoolean -> {
            if (getActivity() != null && aBoolean != null && aBoolean) {
                showDialog();
            }
        });

        viewModel.goToModeratorPage.observe(this, aBoolean -> showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString()));

        viewModel.initEmoji.observe(this, aBoolean -> {
            if (aBoolean != null) {
                if (!isInitEmoji) {
                    setUpEmojiPopup();
                    isInitEmoji = true;
                }
                emojiPopup.toggle();
            }
        });

        viewModel.showDialogLeaveGroup.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                groupLeft();
            }
        });

        viewModel.goToRoomListPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() instanceof ActivityMain && go != null && go) {
                Log.wtf(this.getClass().getName(), "goToRoomListPage observe");
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                /*new HelperFragment(getActivity().getSupportFragmentManager()).popBackStack(3);*/
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (G.isPassCode) ActivityMain.isUseCamera = true;

        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = SUID.id().get();

            if (FragmentEditImage.textImageList != null) FragmentEditImage.textImageList.clear();
            if (FragmentEditImage.itemGalleryList != null)
                FragmentEditImage.itemGalleryList.clear();

            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:
                    if (getActivity() != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                            FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false, this);
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();

                        } else {
                            ImageHelper.correctRotateImage(AttachFile.imagePath, true);

                            FragmentEditImage.insertItemList(AttachFile.imagePath, false, this);
                            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(AttachFile.imagePath, false, false, 0)).setReplace(false).load();
                        }
                    }
                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data.getData() == null) {
                        return;
                    }
                    if (getActivity() != null) {
                        ImageHelper.correctRotateImage(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), true);
                        FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
                    }
                    break;
            }
        }
    }

    private void startDialogSelectPicture() {
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                if (which == 0) {
                    try {
                        attachFile.requestOpenGalleryForImageSingleSelect(EditGroupFragment.this);
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
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showListForCustomRole(String SelectedRole) {
        if (getActivity() != null) {
            FragmentShowMember fragment = FragmentShowMember.newInstance2(this, viewModel.roomId, viewModel.role.toString(), G.userId, SelectedRole, false, true);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    private void showDialog() {
        new MaterialDialog.Builder(getActivity()).title(R.string.show_message_count).items(R.array.numberCountGroup).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                switch (which) {
                    case 0:
                        viewModel.setChatHistoryStatus(0);
                        break;
                    case 1:
                        viewModel.setChatHistoryStatus(-1);
                        break;
                    case 2:
                        viewModel.setChatHistoryStatus(50);
                        break;
                    case 3:
                        dialog.dismiss();
                        new MaterialDialog.Builder(getActivity()).title(R.string.customs).positiveText(getString(R.string.B_ok)).alwaysCallInputCallback().widgetColor(getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (dialog.getInputEditText() != null && dialog.getInputEditText().getEditableText() != null) {
                                    if (dialog.getInputEditText().getEditableText().length() > 0 && dialog.getInputEditText().getEditableText().length() < 5) {
                                        viewModel.setChatHistoryStatus(Integer.parseInt(dialog.getInputEditText().getEditableText().toString()));
                                    } else {
                                        viewModel.setChatHistoryStatus(0);
                                    }
                                } else {
                                    viewModel.setChatHistoryStatus(0);
                                }
                            }
                        }).inputType(InputType.TYPE_CLASS_NUMBER).input(G.fragmentActivity.getResources().getString(R.string.count_of_show_message), null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NotNull MaterialDialog dialog, CharSequence input) {

                            }
                        }).show();
                        break;
                }
            }
        }).show();
    }

    private void setUpEmojiPopup() {
        switch (G.themeColor) {
            case Theme.BLUE_GREY_COMPLETE:
            case Theme.INDIGO_COMPLETE:
            case Theme.BROWN_COMPLETE:
            case Theme.GREY_COMPLETE:
            case Theme.TEAL_COMPLETE:
            case Theme.DARK:
                setEmojiColor(G.getTheme2BackgroundColor(), G.textTitleTheme, G.textTitleTheme);
                break;
            default:
                setEmojiColor(Color.parseColor("#eceff1"), "#61000000", "#61000000");
        }
    }

    private void setEmojiColor(int BackgroundColor, String iconColor, String dividerColor) {
        emojiPopup = EmojiPopup.Builder.fromRootView(binding.root)
                .setOnEmojiBackspaceClickListener(v -> {

                }).setOnEmojiPopupShownListener(() -> isEmojiShow = true)
                .setOnSoftKeyboardOpenListener(keyBoardHeight -> {
                }).setOnEmojiPopupDismissListener(() -> isEmojiShow = false)
                .setOnSoftKeyboardCloseListener(() -> emojiPopup.dismiss())
                .setBackgroundColor(BackgroundColor)
                .setIconColor(Color.parseColor(iconColor))
                .setDividerColor(Color.parseColor(dividerColor))
                .build(binding.groupNameEditText);

    }

    private void groupLeft() {
        if (getActivity() != null) {
            int text;
            int title;
            if (viewModel.role == GroupChatRole.OWNER) {
                text = R.string.do_you_want_to_delete_this_group;
                title = R.string.delete_group;
            } else {
                text = R.string.do_you_want_to_leave_this_group;
                title = R.string.left_group;
            }

            new MaterialDialog.Builder(getActivity()).title(title).content(text).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                    viewModel.leaveGroup();
                    if (getActivity() != null) {
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }
            }).show();
        }
    }

    @Override
    public void result(String path, String message, HashMap<String, StructBottomSheet> textImageList) {
        viewModel.setEditedImage(path);
    }
}
