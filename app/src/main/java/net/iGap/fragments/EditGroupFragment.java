package net.iGap.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentEditGroupBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.model.PassCode;
import net.iGap.module.AttachFile;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.viewmodel.EditGroupViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class EditGroupFragment extends BaseFragment implements FragmentEditImage.OnImageEdited {

    private static final String ROOM_ID = "RoomId";

    private FragmentEditGroupBinding binding;
    private EditGroupViewModel viewModel;
    private AttachFile attachFile;
    //    private EmojiPopup emojiPopup;
    private boolean isEmojiShow = false;

    public static EditGroupFragment newInstance(long roomId) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        EditGroupFragment fragment = new EditGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNeedResume = true;
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new EditGroupViewModel(getArguments() != null ? getArguments().getLong(ROOM_ID) : -1);
            }
        }).get(EditGroupViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_group, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.onCreateFragment(this);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_sent)
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

        viewModel.goToMembersPage.observe(getViewLifecycleOwner(), b -> {
            if (b != null && b) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());
            }
        });

        viewModel.goToAdministratorPage.observe(getViewLifecycleOwner(), b -> {
            if (b != null && b) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
            }
        });

        viewModel.goToPermissionPage.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean b) {

            }
        });

        viewModel.goBack.observe(getViewLifecycleOwner(), aBoolean -> popBackStackFragment());

        viewModel.showSelectImageDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialogSelectPicture();
            }
        });

        viewModel.closePageImediatly.observe(getViewLifecycleOwner(), isClose -> {
            if (isClose == null || !isClose) return;
            popBackStackFragment();
        });

        viewModel.showDialogChatHistory.observe(getViewLifecycleOwner(), aBoolean -> {
            if (getActivity() != null && aBoolean != null && aBoolean) {
                showDialog();
            }
        });

        viewModel.goToModeratorPage.observe(getViewLifecycleOwner(), aBoolean -> showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString()));

        viewModel.initEmoji.observe(this, aBoolean -> {
//            if (aBoolean != null) {
//                emojiPopup.toggle();
//            }
        });

        viewModel.showDialogLeaveGroup.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                groupLeft();
            }
        });

        viewModel.goToRoomListPage.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() instanceof ActivityMain && go != null && go) {
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
            }
        });

        viewModel.getOnGroupAvatarUpdated().observe(getViewLifecycleOwner(), roomId -> {
            if (roomId != null && roomId == viewModel.roomId) {
                setAvatar();
            }
        });

//        setUpEmojiPopup();
        setAvatar();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.updateGroupRole();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PassCode.getInstance().isPassCode()) ActivityMain.isUseCamera = true;

        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = SUID.id().get();

            if (FragmentEditImage.textImageList != null) FragmentEditImage.textImageList.clear();
            if (FragmentEditImage.itemGalleryList != null)
                FragmentEditImage.itemGalleryList.clear();

            if (requestCode == AttachFile.request_code_TAKE_PICTURE) {
                if (getActivity() != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                        FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false, null);
                        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                        fragmentEditImage.setOnProfileImageEdited(this);
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();

                    } else {
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                        FragmentEditImage.insertItemList(AttachFile.imagePath, false, null);
                        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(AttachFile.imagePath, false, false, 0);
                        fragmentEditImage.setOnProfileImageEdited(this);
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                    }
                }
            }
        }
    }

    private void setAvatar() {
        avatarHandler.getAvatar(new ParamWithAvatarType(binding.groupAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
    }

    private void startDialogSelectPicture() {
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                if (which == 0) {
                    try {
                        HelperPermission.getStoragePermision(getActivity(), new OnGetPermission() {
                            @Override
                            public void Allow() {
                                if (getActivity() == null) return;
                                Fragment fragment = FragmentGallery.newInstance(false, FragmentGallery.GalleryMode.PHOTO, true, getString(R.string.gallery), "-1", new FragmentGallery.GalleryFragmentListener() {
                                    @Override
                                    public void openOsGallery() {

                                    }

                                    @Override
                                    public void onGalleryResult(String path) {
                                        handleGalleryImageResult(path);
                                    }
                                });
                                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                            }

                            @Override
                            public void deny() {

                            }
                        });
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

    private void handleGalleryImageResult(String path) {
        if (getActivity() != null) {
            ImageHelper.correctRotateImage(path, true);
            FragmentEditImage.insertItemList(path, false);
            FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
            fragmentEditImage.setOnProfileImageEdited(this);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
        }
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(this);
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
            FragmentShowMember fragment = FragmentShowMember.newInstance2(this, viewModel.roomId, viewModel.role.toString(), AccountManager.getInstance().getCurrentUser().getId(), SelectedRole, false, true);
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

//    private void setUpEmojiPopup() {
//        setEmojiColor(new Theme().getRootColor(getContext()), new Theme().getTitleTextColor(getContext()), new Theme().getTitleTextColor(getContext()));
//    }
//
//    private void setEmojiColor(int BackgroundColor, int iconColor, int dividerColor) {
//        emojiPopup = EmojiPopup.Builder.fromRootView(binding.root)
//                .setOnEmojiBackspaceClickListener(v -> {
//
//                }).setOnEmojiPopupShownListener(() -> isEmojiShow = true)
//                .setOnSoftKeyboardOpenListener(keyBoardHeight -> {
//                }).setOnEmojiPopupDismissListener(() -> isEmojiShow = false)
//                .setOnSoftKeyboardCloseListener(() -> emojiPopup.dismiss())
//                .setBackgroundColor(BackgroundColor)
//                .setIconColor(iconColor)
//                .setDividerColor(dividerColor)
//                .build(binding.groupNameEditText);
//
//    }

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
    public void profileImageAdd(String path) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack(EditGroupFragment.class.getName(), 0);
        }
        viewModel.uploadAvatar(path);
    }
}
