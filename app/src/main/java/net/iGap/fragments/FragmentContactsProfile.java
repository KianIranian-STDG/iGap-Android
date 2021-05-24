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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.controllers.RoomController;
import net.iGap.databinding.FragmentContactsProfileBinding;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.module.CircleImageView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.proto.ProtoUserReport;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserReport;
import net.iGap.viewmodel.FragmentContactsProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;
import static net.iGap.module.Contacts.showLimitDialog;

//todo : fixed view mode and view and remove logic code from view
public class FragmentContactsProfile extends BaseFragment {

    private static final String ROOM_ID = "RoomId";
    private static final String PEER_ID = "peerId";
    private static final String ENTER_FROM = "enterFrom";

    private final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.7f;
    private final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private String report;
    private FragmentContactsProfileBinding binding;
    private FragmentContactsProfileViewModel viewModel;
    private CircleImageView userAvatarImageView;
    private boolean isCollapsed;
    private long userId;

    public static FragmentContactsProfile newInstance(long roomId, long peerId, String enterFrom) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putLong(PEER_ID, peerId);
        args.putString(ENTER_FROM, enterFrom);
        FragmentContactsProfile fragment = new FragmentContactsProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentContactsProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts_profile, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        userId = 0;
        long roomId = 0;
        String enterFrom = "";
        if (getArguments() != null) {
            userId = getArguments().getLong(PEER_ID);
            roomId = getArguments().getLong(ROOM_ID);
            enterFrom = getArguments().getString(ENTER_FROM);
        }
        viewModel.init(roomId, userId, enterFrom, avatarHandler);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialToolbar();

        userAvatarImageView = binding.toolbarAvatar;
        userAvatarImageView.setOnClickListener(v -> viewModel.onImageClick());

        binding.toolbarBack.setOnClickListener(v -> popBackStackFragment());
        binding.toolbarMore.setOnClickListener(v -> viewModel.onMoreButtonClick());
        binding.toolbarCall.setOnClickListener(v -> onCallButtonClick());

        viewModel.copyUserNameToClipBoard.observe(getViewLifecycleOwner(), userName -> {

            if (userName == null) return;

            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("LINK_USER", Config.IGAP_LINK_PREFIX + userName);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), getString(R.string.username_copied), Toast.LENGTH_SHORT).show();

        });

        viewModel.getCloudVisibility().observe(getViewLifecycleOwner(), thisMyCloud -> {
            if (thisMyCloud != null) {
                if (thisMyCloud) {
                    binding.report.setVisibility(View.GONE);
                    binding.block.setVisibility(View.GONE);
                    binding.enableNotificationLyt.setVisibility(View.GONE);
                    binding.line1.setVisibility(View.GONE);
                    binding.customNotification.setVisibility(View.GONE);
                } else {
                    binding.report.setVisibility(View.VISIBLE);
                    binding.block.setVisibility(View.VISIBLE);
                    binding.line1.setVisibility(View.VISIBLE);
                    binding.enableNotificationLyt.setVisibility(View.VISIBLE);
                    binding.customNotification.setVisibility(View.VISIBLE);
                }
            }
        });


        viewModel.menuVisibility.observe(getViewLifecycleOwner(), visible -> {
            if (visible != null) binding.toolbarMore.setVisibility(visible);
        });

        viewModel.videoCallVisibility.observe(getViewLifecycleOwner(), visible -> {
            if (visible != null) binding.toolbarCall.setVisibility(visible);
        });

        //todo: fixed it and move to viewModel
        viewModel.isMuteNotificationChangeListener.observe(getViewLifecycleOwner(), isChecked -> {
            binding.enableNotification.setChecked(isChecked);
            getRoomController().clientMuteRoom(viewModel.roomId,isChecked);
        });

        viewModel.contactName.observe(getViewLifecycleOwner(), name -> {
            if (name != null) {
                binding.toolbarTxtNameCollapsed.setText(EmojiManager.getInstance().replaceEmoji(name, binding.toolbarTxtNameCollapsed.getPaint().getFontMetricsInt()));
                binding.toolbarTxtNameExpanded.setText(EmojiManager.getInstance().replaceEmoji(name, binding.toolbarTxtNameExpanded.getPaint().getFontMetricsInt()));
                binding.toolbarTxtNameExpanded.setSelected(true);
            }
        });

        viewModel.lastSeen.observe(getViewLifecycleOwner(), lastSeen -> {
            if (lastSeen != null) {
                binding.toolbarTxtStatusExpanded.setText(HelperCalander.unicodeManage(lastSeen));
            }
        });

        viewModel.goToChatPage.observe(getViewLifecycleOwner(), userRoomId -> {
            if (getActivity() != null && userRoomId != null) {
                if (G.twoPaneMode) {
                    ((ActivityMain) getActivity()).removeAllFragment();
                } else {
                    ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                }
                new GoToChatActivity(userRoomId).startActivity(getActivity());
            }
        });

        viewModel.goBack.observe(getViewLifecycleOwner(), isBack -> {
            if (isBack != null && isBack) {
                popBackStackFragment();
            }
        });

        viewModel.goToShearedMediaPage.observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentShearedMedia.newInstance(data)).setReplace(false).load();
            }
        });

        if (viewModel.phone != null && (!viewModel.phone.get().equals("0") || viewModel.showNumber.get())) {

            if (viewModel.phone.get().equals("0")) {
                binding.toolbarTxtTelExpanded.setVisibility(View.GONE);
                viewModel.menuVisibility.setValue(View.GONE);
            } else {
                binding.toolbarTxtTelExpanded.setText(viewModel.phone.get());
                binding.toolbarTxtTelExpanded.setOnClickListener(v -> viewModel.onPhoneNumberClick());
            }

        } else {
            binding.toolbarTxtTelExpanded.setVisibility(View.GONE);
        }

        /*binding.toolbarTxtUsernameExpanded.setText(viewModel.username.get());*/

        binding.toolbarFabChat.setOnClickListener(v -> {
            viewModel.onClickGoToChat();
        });

        /*binding.chiFabSetPic.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.fabBottom)));
        binding.chiFabSetPic.setColorFilter(Color.WHITE);
        binding.chiFabSetPic.setOnClickListener(new View.OnClickListener() { //fab button
            @Override
            public void onClick(View view) {

                if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString()) || enterFrom.equals("Others")) { // Others is from FragmentMapUsers adapter

final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", userId).findFirst();

                    if (realmRoom != null) {
                        new HelperFragment().removeAll(true);
                        new GoToChatActivity(realmRoom.getId()).startActivity();
                    } else {
                        G.onChatGetRoom = new OnChatGetRoom() {
                            @Override
                            public void onChatGetRoom(final ProtoGlobal.Room room) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new HelperFragment().removeAll(true);
                                        new GoToChatActivity(room.getId()).setPeerID(userId).startActivity();
                                        G.onChatGetRoom = null;
                                    }
                                });
                            }

                            @Override
                            public void onChatGetRoomTimeOut() {

                            }

                            @Override
                            public void onChatGetRoomError(int majorCode, int minorCode) {

                            }
                        };

                        new RequestChatGetRoom().chatGetRoom(userId);
                    }


                } else {
                    popBackStackFragment();
                }
            }
        });*/

        /*if (viewModel.showNumber.get()) {
            binding.chiLayoutNickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (viewModel.contactName.get() == null) {
                        return;
                    }

                    final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
                    layoutNickname.setOrientation(LinearLayout.VERTICAL);

                    String splitNickname[] = viewModel.contactName.get().split(" ");
                    String firsName = "";
                    String lastName = "";
                    StringBuilder stringBuilder = null;
                    if (splitNickname.length > 1) {

                        lastName = splitNickname[splitNickname.length - 1];
                        stringBuilder = new StringBuilder();
                        for (int i = 0; i < splitNickname.length - 1; i++) {

                            stringBuilder.append(splitNickname[i]).append(" ");
                        }
                        firsName = stringBuilder.toString();
                    } else {
                        firsName = splitNickname[0];
                    }
                    final View viewFirstName = new View(G.fragmentActivity);
                    viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));

                    LinearLayout.LayoutParams viewParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

                    TextInputLayout inputFirstName = new TextInputLayout(G.fragmentActivity);
                    final EmojiEditTextE edtFirstName = new EmojiEditTextE(G.fragmentActivity);
                    edtFirstName.setHint(R.string.first_name);
                    edtFirstName.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                    edtFirstName.setTypeface(G.typeface_IRANSansMobile);
                    edtFirstName.setText(firsName);
                    edtFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
                    edtFirstName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
                    edtFirstName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
                    edtFirstName.setPadding(0, 8, 0, 8);
                    edtFirstName.setSingleLine(true);
                    inputFirstName.addView(edtFirstName);
                    inputFirstName.addView(viewFirstName, viewParams);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        edtFirstName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
                    }

                    final View viewLastName = new View(G.fragmentActivity);
                    viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));

                    TextInputLayout inputLastName = new TextInputLayout(G.fragmentActivity);
                    final MEditText edtLastName = new MEditText(G.fragmentActivity);
                    edtLastName.setHint(R.string.last_name);
                    edtLastName.setTypeface(G.typeface_IRANSansMobile);
                    edtLastName.setText(lastName);
                    edtLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
                    edtLastName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
                    edtLastName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
                    edtLastName.setPadding(0, 8, 0, 8);
                    edtLastName.setSingleLine(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        edtLastName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
                    }
                    inputLastName.addView(edtLastName);
                    inputLastName.addView(viewLastName, viewParams);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, 15);
                    LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lastNameLayoutParams.setMargins(0, 15, 0, 10);

                    layoutNickname.addView(inputFirstName, layoutParams);
                    layoutNickname.addView(inputLastName, lastNameLayoutParams);

                    final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.pu_nikname_profileUser))
                            .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                            .customView(layoutNickname, true)
                            .widgetColor(Color.parseColor(G.appBarColor))
                            .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                            .build();

                    final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                    positive.setEnabled(false);

                    edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                viewFirstName.setBackgroundColor(Color.parseColor(G.appBarColor));
                            } else {
                                viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                            }
                        }
                    });

                    edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                viewLastName.setBackgroundColor(Color.parseColor(G.appBarColor));
                            } else {
                                viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                            }
                        }
                    });

                    final String finalFirsName = firsName;
                    edtFirstName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            if (!edtFirstName.getText().toString().equals(finalFirsName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                        }
                    });

                    final String finalLastName = lastName;
                    edtLastName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (!edtLastName.getText().toString().equals(finalLastName)) {
                                positive.setEnabled(true);
                            } else {
                                positive.setEnabled(false);
                            }
                        }
                    });

                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long po = Long.parseLong(viewModel.phone.get());
                            String firstName = edtFirstName.getText().toString().trim();
                            String lastName = edtLastName.getText().toString().trim();
                            new RequestUserContactsEdit().contactsEdit(userId, po, firstName, lastName);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }*/

        /*binding.chiAppbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewGroup viewGroup = binding.chiRootCircleImage;
                if (verticalOffset < -5) {
                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    binding.chiTxtTitleToolbarDisplayName.setVisibility(View.VISIBLE);
                    binding.chiTxtTitleToolbarDisplayName.animate().alpha(1).setDuration(300);
                    binding.chiTxtTitleToolbarLastSeen.setVisibility(View.VISIBLE);
                    binding.chiTxtTitleToolbarLastSeen.animate().alpha(1).setDuration(300);
                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(1).setDuration(700);
                    binding.chiTxtTitleToolbarDisplayName.setVisibility(View.GONE);
                    binding.chiTxtTitleToolbarDisplayName.animate().alpha(0).setDuration(500);
                    binding.chiTxtTitleToolbarLastSeen.setVisibility(View.GONE);
                    binding.chiTxtTitleToolbarLastSeen.animate().alpha(0).setDuration(500);
                }
            }
        });*/

        viewModel.showMenu.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showPopUp();
            }
        });

        viewModel.showPhoneNumberDialog.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    try {
                        HelperPermission.getContactPermision(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                showPopupPhoneNumber(/*t.getProfileTell()*/null, viewModel.phone.get());
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
        });

        /*binding.chiLayoutSharedMedia.setOnClickListener(new View.OnClickListener() {// go to the ActivityMediaChanel
            @Override
            public void onClick(View view) {
                new HelperFragment(FragmentShearedMedia.newInstance(viewModel.shearedId)).setReplace(false).load();
            }
        });*/

        viewModel.showClearChatDialog.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showAlertDialog(getString(R.string.clear_this_chat), getString(R.string.clear), getString(R.string.cancel));
            }
        });

        viewModel.goToCustomNotificationPage.observe(this, aBoolean -> {
            if (getActivity() != null && aBoolean != null && aBoolean) {
                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "CONTACT");
                bundle.putLong("ID", viewModel.roomId);
                fragmentNotification.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentNotification).setReplace(false).load();
            }
        });

        viewModel.setAvatar.observe(this, aBoolean -> {
            if (aBoolean != null) {
                if (aBoolean) {
                    avatarHandler.getAvatar(new ParamWithAvatarType(userAvatarImageView, viewModel.userId).avatarSize(R.dimen.dp100).avatarType(AvatarHandler.AvatarType.USER).showMain());
                } else {
                    userAvatarImageView.setImageResource(R.drawable.ic_cloud_space_blue);
                }
            }
        });

        viewModel.showDeleteContactDialog.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        viewModel.deleteContact();
                    }
                }).negativeText(R.string.B_cancel).show();
            }
        });

        viewModel.showDialogReportContact.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                openDialogReport();
            }
        });

        viewModel.showDialogStartSecretChat.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                Toast.makeText(getContext(), "secret chat", Toast.LENGTH_LONG).show();
            }
        });

        viewModel.goToShowAvatarPage.observe(this, isCurrentUser -> {
            if (getActivity() != null && isCurrentUser != null) {
                Log.wtf(this.getClass().getName(), "goToShowAvatarPage observe");
                FragmentShowAvatars fragment;
                if (isCurrentUser) {
                    fragment = FragmentShowAvatars.newInstance(viewModel.userId, FragmentShowAvatars.From.setting);
                } else {
                    fragment = FragmentShowAvatars.newInstance(viewModel.userId, FragmentShowAvatars.From.chat);
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        viewModel.blockDialogListener.observe(getViewLifecycleOwner(), isBlockUser -> {
            if (isBlockUser == null) return;

            if (isBlockUser) {
                new MaterialDialog.Builder(getContext()).title(R.string.unblock_the_user).content(R.string.unblock_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new RequestUserContactsUnblock().userContactsUnblock(viewModel.userId);
                    }
                }).negativeText(R.string.cancel)
                        .dismissListener(dialog -> checkViewsState())
                        .showListener(dialog -> checkViewsState()).show();
            } else {
                new MaterialDialog.Builder(getContext()).title(R.string.block_the_user).content(R.string.block_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new RequestUserContactsBlock().userContactsBlock(viewModel.userId);
                    }
                }).negativeText(R.string.cancel)
                        .dismissListener(dialog -> checkViewsState())
                        .showListener(dialog -> checkViewsState()).show();
            }
        });

        viewModel.editContactListener.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean == null) return;
            FragmentAddContact fragment = FragmentAddContact.newInstance(
                    viewModel.userId, viewModel.phoneNumber, viewModel.firstName, viewModel.lastName, FragmentAddContact.ContactMode.EDIT, (name, family) -> {
                        viewModel.contactName.setValue(name + " " + family);
                        viewModel.firstName = name;
                        viewModel.lastName = family;
                        if (getActivity() != null) {
                            ((ActivityMain) getActivity()).onUpdateContacts();
                        }
                    }
            );
            if (getActivity() != null)
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();

        });

    }

    private void onCallButtonClick() {
        CallSelectFragment selectFragment = CallSelectFragment.getInstance(userId, false, null);
        if (getFragmentManager() != null)
            selectFragment.show(getFragmentManager(), null);
    }

    private void checkViewsState() {
        if (isCollapsed) startAlphaAnimation(binding.toolbarFabChat, 0, View.INVISIBLE);
    }

    private void initialToolbar() {
        startAlphaAnimation(binding.toolbarTxtNameCollapsed, 0, View.INVISIBLE);

        binding.toolbarAppbar.addOnOffsetChangedListener((appBarLayout, offset) -> {

            isCollapsed = offset != 0;

            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(offset) / (float) maxScroll;

            handleAlphaOnTitle(percentage);
            handleToolbarTitleVisibility(percentage);

        });

    }


    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(binding.toolbarTxtNameCollapsed, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(binding.toolbarTxtNameCollapsed, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.toolbarLayoutExpTitles, 100, View.INVISIBLE);
                startAlphaAnimation(binding.toolbarFabChat, 100, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.toolbarLayoutExpTitles, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                startAlphaAnimation(binding.toolbarFabChat, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {

        if (visibility == View.VISIBLE) v.setVisibility(visibility);

        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onResume() {
        if (binding != null && !mIsTheTitleContainerVisible) {
            startAlphaAnimation(binding.toolbarFabChat, 0, View.INVISIBLE);
        }
        super.onResume();
        viewModel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * ************************************ methods ************************************
     */
    private void showPopupPhoneNumber(View v, String number) {
        try {
            boolean isExist = false;
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur != null) {
                    isExist = cur.moveToFirst();
                }
            } finally {
                if (cur != null) cur.close();
            }

            if (isExist) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).items(R.array.phone_number2).itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            String call = "+" + Long.parseLong(viewModel.phone.get());
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + Uri.encode(call.trim())));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } catch (Exception ex) {
                                ex.getStackTrace();
                            }
                            break;
                        case 1:
                            String copy;
                            copy = viewModel.phone.get();
                            ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                            clipboard.setPrimaryClip(clip);
                            break;
                    }
                }).show();
            } else {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).items(R.array.phone_number).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:

                                String name = viewModel.contactName.getValue();
                                String phone = "+" + viewModel.phone.get();

                                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

                                //------------------------------------------------------ Names

                                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());

                                //------------------------------------------------------ Mobile Number

                                ops.add(ContentProviderOperation.
                                        newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                        .build());

                                try {
                                    G.context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                    addContactToServer();
                                    Toast.makeText(G.context, R.string.save_ok, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(G.context, G.fragmentActivity.getResources().getString(R.string.exception) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case 1:

                                String call = "+" + Long.parseLong(viewModel.phone.get());
                                try {
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + Uri.encode(call.trim())));
                                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(callIntent);
                                } catch (Exception ex) {

                                    ex.getStackTrace();
                                }
                                break;
                            case 2:

                                ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("PHONE_NUMBER", viewModel.phone.get());
                                clipboard.setPrimaryClip(clip);

                                break;
                        }
                    }
                }).show();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * import contact to server with True force
     */
    private void addContactToServer() {

        if (HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.EXCEED_CONTACTS_DIALOG)) {
            showLimitDialog();
            return;
        }

        List<StructListOfContact> contacts = new ArrayList<>();
        StructListOfContact contact = new StructListOfContact();
        contact.firstName = viewModel.firstName;
        contact.lastName = viewModel.lastName;
        contact.phone = viewModel.phone.get() + "";

        contacts.add(contact);

        new RequestUserContactImport().contactImport(contacts, true);
    }

    private void showPopUp() {
        new TopSheetDialog(getContext()).setListData(viewModel.items, -1, position -> viewModel.onMenuItemClick(position)).show();
    }

    private void openDialogReport() {
        //todo: fixed on click and handle in viewModel get list menu from viewModel
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.st_Spam));
        items.add(getString(R.string.st_Abuse));
        items.add(getString(R.string.st_FakeAccount));
        items.add(getString(R.string.st_Other));
        new BottomSheetFragment().setData(items, -1, position -> {
            if (items.get(position).equals(getString(R.string.st_Spam))) {
                new RequestUserReport().userReport(viewModel.userId, ProtoUserReport.UserReport.Reason.SPAM, "");
            } else if (items.get(position).equals(getString(R.string.st_Abuse))) {
                new RequestUserReport().userReport(viewModel.userId, ProtoUserReport.UserReport.Reason.ABUSE, "");
            } else if (items.get(position).equals(getString(R.string.st_FakeAccount))) {
                new RequestUserReport().userReport(viewModel.userId, ProtoUserReport.UserReport.Reason.FAKE_ACCOUNT, "");
            } else if (items.get(position).equals(getString(R.string.st_Other))) {
                final MaterialDialog dialogReport = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.report).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE).alwaysCallInputCallback().input(G.context.getString(R.string.description), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        report = input.toString();
                        if (input.length() > 0) {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(true);
                        } else {
                            View positive = dialog.getActionButton(DialogAction.POSITIVE);
                            positive.setEnabled(false);
                        }
                    }
                }).positiveText(R.string.ok).onPositive((dialog, which) -> new RequestUserReport().userReport(viewModel.roomId, ProtoUserReport.UserReport.Reason.OTHER, report)).negativeText(R.string.cancel).build();

                final View positive = dialogReport.getActionButton(DialogAction.POSITIVE);
                positive.setEnabled(false);

                DialogAnimation.animationDown(dialogReport);

                dialogReport.show();

                dialogReport.setOnShowListener(dialog -> checkViewsState());
                dialogReport.setOnDismissListener(dialog -> checkViewsState());
            }
        }).show(getFragmentManager(), "bottom sheet");

        G.onReport = () -> error(G.fragmentActivity.getResources().getString(R.string.st_send_report));

    }

    private void showAlertDialog(String message, String positive, String negative) { // alert dialog for block or clear user
        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(message).positiveText(positive).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                getMessageController().clearHistoryMessage(viewModel.shearedId);
                dialog.dismiss();
            }
        }).negativeText(negative)
                .dismissListener(dialog -> checkViewsState())
                .showListener(dialog -> checkViewsState()).show();
    }

    private void error(String error) {
        if (isAdded()) {
            try {
                final Snackbar snack = Snackbar.make(G.fragmentActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                snack.setAction(G.fragmentActivity.getResources().getString(R.string.cancel), view -> snack.dismiss());
                snack.show();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }
}
