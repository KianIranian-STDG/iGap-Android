package net.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentEditChannelBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.model.PassCode;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.MEditText;
import net.iGap.module.SUID;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnChannelCheckUsername;
import net.iGap.observers.interfaces.OnChannelRemoveUsername;
import net.iGap.observers.interfaces.OnChannelUpdateUsername;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoChannelCheckUsername;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelCheckUsername;
import net.iGap.request.RequestChannelRemoveUsername;
import net.iGap.request.RequestChannelRevokeLink;
import net.iGap.request.RequestChannelUpdateUsername;
import net.iGap.viewmodel.EditChannelViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditChannelFragment extends BaseFragment implements FragmentEditImage.OnImageEdited, EventManager.EventDelegate {

    private static final String ROOM_ID = "RoomId";

    private long roomId;
    private String uploadAvatarId;

    private FragmentEditChannelBinding binding;
    private EditChannelViewModel viewModel;
    private boolean isEmojiShow = false;
//    private EmojiPopup emojiPopup;

    public static EditChannelFragment newInstance(long channelId) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, channelId);
        EditChannelFragment fragment = new EditChannelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNeedResume = true;
        roomId = getArguments() != null ? getArguments().getLong(ROOM_ID) : -1;

        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new EditChannelViewModel(roomId);
            }
        }).get(EditChannelViewModel.class);

        getEventManager().addObserver(EventManager.AVATAR_UPDATE, this);
        getEventManager().addObserver(EventManager.FILE_UPLOAD_FAILED, this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_channel, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarHandler.getAvatar(new ParamWithAvatarType(binding.channelAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());

        viewModel.onCreateFragment(this);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getContext().getResources().getString(R.string.tab_edit))
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_sent)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        viewModel.setData(binding.channelNameEditText.getEditableText().toString(), binding.groupDescriptionEditText.getEditableText().toString());
                        hideKeyboard();
                    }
                });
        binding.toolbar.addView(mHelperToolbar.getView());


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

        viewModel.goToModeratorPage.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
            }
        });

        viewModel.closePageImediatly.observe(getViewLifecycleOwner(), isClose -> {
            if (isClose == null || !isClose) return;
            popBackStackFragment();
        });

        viewModel.initEmoji.observe(getViewLifecycleOwner(), aBoolean -> {
//            if (aBoolean != null) {
//                emojiPopup.toggle();
//            }
        });

        viewModel.showDialogLeaveGroup.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                /*groupLeft();*/
            }
        });

        viewModel.showSelectImageDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialogSelectPicture();
            }
        });
        viewModel.showConvertChannelDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null) {
                showPopUp(aBoolean);
            }
        });
        viewModel.showDeleteChannelDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null) {
                deleteChannel(aBoolean);
            }
        });

        viewModel.goBack.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                popBackStackFragment();
            }
        });

        viewModel.goToChatRoom.observe(getViewLifecycleOwner(), go -> {
            if (getActivity() instanceof ActivityMain && go != null && go) {
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                /*new HelperFragment(getActivity().getSupportFragmentManager()).popBackStack(3);*/
            }
        });

        viewModel.onSignClickListener.observe(getViewLifecycleOwner(), isClicked -> {
            binding.signedMessage.setChecked(!binding.signedMessage.isChecked());
        });

        viewModel.onReactionMessageClickListener.observe(getViewLifecycleOwner(), isClicked -> {
            binding.rateMessage.setChecked(!binding.rateMessage.isChecked());
        });

        viewModel.getChannelAvatarUpdatedLiveData().observe(getViewLifecycleOwner(), roomId -> {
            if (roomId != null && roomId == viewModel.roomId)
                avatarHandler.getAvatar(new ParamWithAvatarType(binding.channelAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
        });

        viewModel.showChangeUsername.observe(getViewLifecycleOwner(), username -> {
            if (username != null) {
                setUsername(username);
            }
        });

//        setEmojiColor();

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
                        ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true); //rotate image

                        FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
                        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                        fragmentEditImage.setOnProfileImageEdited(this);
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                    } else {
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true); //rotate image

                        FragmentEditImage.insertItemList(AttachFile.imagePath, false);
                        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(AttachFile.imagePath, false, false, 0);
                        fragmentEditImage.setOnProfileImageEdited(this);
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                    }
                }
            }
        }
    }

    private void startDialogSelectPicture() {
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.choose_picture).negativeText(R.string.cansel).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
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
                }
            }
        }).show();
    }

    private void handleGalleryImageResult(String path) {
        if (getActivity() != null) {
            ImageHelper.correctRotateImage(path, true); //rotate image
            FragmentEditImage.insertItemList(path, false);
            FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
            fragmentEditImage.setOnProfileImageEdited(this);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
        }
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(EditChannelFragment.this);
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(EditChannelFragment.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showListForCustomRole(String SelectedRole) {
        if (getActivity() != null) {
            FragmentShowMember fragment = FragmentShowMember.newInstance2(this, viewModel.roomId, viewModel.role.toString(), AccountManager.getInstance().getCurrentUser().getId(), SelectedRole, false, false);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    private void showPopUp(boolean isPrivate) {
        List<Integer> items = new ArrayList<>();
        if (isPrivate) {
            items.add(R.string.channel_title_convert_to_public);
        } else {
            items.add(R.string.channel_title_convert_to_private);
        }
        new BottomSheetFragment().setListDataWithResourceId(getContext(),items, -1, position -> {
            if (isPrivate) {
                convertToPublic();
            } else {
                convertToPrivate();
            }
        }).show(getFragmentManager(), "bottom sheet");
    }

    private void convertToPublic() {
        if (getContext() != null) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.channel_title_convert_to_public)
                    .content(R.string.channel_text_convert_to_public)
                    .positiveText(R.string.yes)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            setUsername("");
                        }
                    }).negativeText(R.string.no).show();
        }
    }

    private void setUsername(String username) {
        final LinearLayout layoutUserName = new LinearLayout(getContext());
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(getContext());
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
        final MEditText edtUserName = new MEditText(getContext());
        edtUserName.setHint(G.fragmentActivity.getResources().getString(R.string.channel_title_channel_set_username));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            edtUserName.setTextDirection(View.TEXT_DIRECTION_LTR);
        }
        edtUserName.setTypeface(ResourcesCompat.getFont(edtUserName.getContext(), R.font.main_font));
        edtUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));

        /*if (isPopup) {*/
        edtUserName.setText(Config.IGAP_LINK_PREFIX + username);
        /*} else {
            edtUserName.setText(Config.IGAP_LINK_PREFIX + linkUsername);
        }*/

        edtUserName.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title(R.string.st_username)
                .positiveText(R.string.save)
                .customView(layoutUserName, true)
                .widgetColor(new Theme().getPrimaryColor(getContext()))
                .negativeText(R.string.B_cancel)
                .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        G.onChannelCheckUsername = new OnChannelCheckUsername() {
            @Override
            public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {
                            positive.setEnabled(true);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.TAKEN));
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.OCCUPYING_LIMIT_EXCEEDED));
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };

        edtUserName.setSelection((edtUserName.getText().toString().length()));
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtUserName.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
                    new RequestChannelCheckUsername().channelCheckUsername(viewModel.roomId, userName);
                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("" + G.fragmentActivity.getResources().getString(R.string.INVALID));
                }
            }
        });


        G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
            @Override
            public void onChannelUpdateUsername(final long roomId, final String username) {
                G.handler.post(() -> {
                    viewModel.setPrivate(false, username);
                    dialog.dismiss();
                    viewModel.linkUsername = username;
                    /*setTextChannelLik();*/
                });
            }

            @Override
            public void onError(final int majorCode, int minorCode, final int time) {
                switch (majorCode) {
                    case 457:
                        G.handler.post(() -> {
                            if (dialog.isShowing()) dialog.dismiss();
                            dialogWaitTime(R.string.limit_for_set_username, time, majorCode);
                        });
                        break;
                }
            }

            @Override
            public void onTimeOut() {

            }
        };

        positive.setOnClickListener(view -> {
            String userName = edtUserName.getText().toString().replace(Config.IGAP_LINK_PREFIX, "");
            new RequestChannelUpdateUsername().channelUpdateUsername(viewModel.roomId, userName);
        });
        edtUserName.setOnFocusChangeListener((view, b) -> {
            if (b) {
                viewUserName.setBackgroundColor(new Theme().getAccentColor(getContext()));
            } else {
                viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
            }
        });
        // check each word with server
        dialog.setOnDismissListener(dialog1 -> AndroidUtils.closeKeyboard(binding.getRoot()));
        dialog.show();
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
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

    private void convertToPrivate() {
        G.onChannelRemoveUsername = new OnChannelRemoveUsername() {
            @Override
            public void onChannelRemoveUsername(final long roomId) {
                G.handler.post(() -> {
                    RealmRoom.setPrivate(roomId, () -> {
                        viewModel.setPrivate(true, "");
                        if (viewModel.inviteLink == null || viewModel.inviteLink.isEmpty() || viewModel.inviteLink.equals("https://")) {
                            new RequestChannelRevokeLink().channelRevokeLink(roomId);
                        }
                    });
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_title_convert_to_private)).content(G.fragmentActivity.getResources().getString(R.string.channel_text_convert_to_private)).positiveText(R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestChannelRemoveUsername().channelRemoveUsername(viewModel.roomId);
            }
        }).negativeText(R.string.no).show();
    }

    //Todo: remove code of leave channel
    private void deleteChannel(boolean isOwner) {
        String deleteText = "";
        int title;
        if (isOwner) {
            deleteText = getString(R.string.do_you_want_delete_this_channel);
            title = R.string.channel_delete;
        } else {
            deleteText = getString(R.string.do_you_want_leave_this_channel);
            title = R.string.channel_left;
        }

        new MaterialDialog.Builder(G.fragmentActivity)
                .title(title).content(deleteText)
                .positiveText(R.string.yes)
                .onPositive((dialog, which) -> {
                    if (isOwner) {
                        getMessageController().deleteChannel(roomId);

                        if (getActivity() instanceof ActivityMain) {
                            ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                        }

                    } else {
                        getRoomController().channelLeft(viewModel.roomId);
                    }
                    binding.loading.setVisibility(View.VISIBLE);
                }).negativeText(R.string.no).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getEventManager().removeObserver(EventManager.AVATAR_UPDATE, this);
        getEventManager().removeObserver(EventManager.FILE_UPLOAD_FAILED, this);
    }

    @Override
    public void profileImageAdd(String path) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack(EditChannelFragment.class.getName(), 0);
        }

        viewModel.getShowUploadProgressLiveData().postValue(View.VISIBLE);
        uploadAvatarId = getMessageController().saveChannelAvatar(path, roomId);
    }

    @Override
    public void receivedEvent(int id, int account, Object... args) {

        if (id == EventManager.AVATAR_UPDATE) {
            G.runOnUiThread(() -> {
                viewModel.getShowUploadProgressLiveData().postValue(View.GONE);

                long roomId = (long) args[0];

                if (roomId == this.roomId) {
                    avatarHandler.getAvatar(new ParamWithAvatarType(binding.channelAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
                }

            }, 500);
        } else if (id == EventManager.FILE_UPLOAD_FAILED) {
            String fileId = (String) args[0];

            if (uploadAvatarId != null && uploadAvatarId.equals(fileId)) {
                G.runOnUiThread(() -> viewModel.getShowUploadProgressLiveData().postValue(View.GONE));
            }
        }
    }
}
