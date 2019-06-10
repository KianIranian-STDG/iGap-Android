package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChannelProfile;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnMenuClick;
import net.iGap.module.AttachFile;
import net.iGap.module.MEditText;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelAddAdmin;
import net.iGap.request.RequestChannelAddModerator;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;

public class FragmentChannelProfileViewModel
        /*implements OnChannelAddMember, OnChannelAddModerator, OnChannelUpdateReactionStatus, OnChannelKickModerator, OnChannelAddAdmin, OnChannelKickAdmin, OnChannelDelete,
        OnChannelLeft, OnChannelEdit, OnChannelRevokeLink*/ {

    public static final String FRAGMENT_TAG = "FragmentChannelProfile";

    public MutableLiveData<String> channelName = new MutableLiveData<>();
    public MutableLiveData<Integer> menuPopupVisibility = new MutableLiveData<>();
    public MutableLiveData<Boolean> isVerifiedChannel = new MutableLiveData<>();
    public MutableLiveData<String> channelLinkTitle = new MutableLiveData<>();
    public MutableLiveData<Boolean> isShowLink = new MutableLiveData<>();
    public MutableLiveData<String> channelLink = new MutableLiveData<>();
    public MutableLiveData<Boolean> showLoading = new MutableLiveData<>();
    public MutableLiveData<String> subscribersCount = new MutableLiveData<>();
    public MutableLiveData<String> administratorsCount = new MutableLiveData<>();
    public MutableLiveData<String> moderatorsCount = new MutableLiveData<>();
    public MutableLiveData<Boolean> isMuteNotification = new MutableLiveData<>();
    public MutableLiveData<Boolean> isMuteNotificationChangeListener = new MutableLiveData<>();
    public ObservableInt haveDescription = new ObservableInt(View.VISIBLE);

    public static OnMenuClick onMenuClick;
    public ChannelChatRole role;
    public long roomId;
    public boolean isPrivate;
    public ObservableBoolean isCheckedSignature = new ObservableBoolean(false);
    public ObservableBoolean isReactionStatus = new ObservableBoolean(true);
    //todo: move to edit channel fragment
    /*public ObservableField<Integer> showLayoutReactStatus = new ObservableField<>(View.GONE);*/
    public ObservableBoolean channelNameEnable = new ObservableBoolean(true);
    public ObservableBoolean channelDescriptionEnable = new ObservableBoolean(true);
    public ObservableField<SpannableStringBuilder> callbackChannelDescription = new ObservableField<>();
    public ObservableField<String> callbackChannelSharedMedia = new ObservableField<>("");
    //todo: move to edit channel fragment
    /*public ObservableField<String> callBackDeleteLeaveChannel = new ObservableField<>(G.context.getResources().getString(R.string.delete_and_leave_channel));*/
    public ObservableField<Integer> addMemberVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> prgWaitingVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> settingVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> descriptionVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> signatureVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> fabVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> listAdminVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> moderatorVisibility = new ObservableField<>(View.VISIBLE);
    private String initials;
    /*private String color;*/
    private String participantsCountLabel;
    private String description;
    /*private String inviteLink;*/
    private long noLastMessage;
    private MEditText edtRevoke;
    private RealmList<RealmMember> members;
    private AttachFile attachFile;
    /*private boolean isSignature;*/
    private boolean isPopup = false;
    private boolean isNeedGetMemberList = true;
    private FragmentChannelProfile fragment;
    private Realm realmChannelProfile;
    private RealmChangeListener<RealmModel> changeListener;
    private RealmRoom mRoom;
    public boolean isNotJoin = false;
    private String dialogDesc;
    private String dialogName;

    public FragmentChannelProfileViewModel(long roomId, boolean isNotJoin) {
        this.roomId = roomId;
        this.isNotJoin = isNotJoin;

        /*G.onChannelAddMember = this;*/
        /*G.onChannelAddAdmin = this;*/
        /*G.onChannelKickAdmin = this;*/
        /*G.onChannelAddModerator = this;*/
        /*G.onChannelKickModerator = this;*/
        /*G.onChannelDelete = this;*/
        /*G.onChannelLeft = this;*/
        /*G.onChannelEdit = this;*/
        /*G.onChannelRevokeLink = this;*/

        realmChannelProfile = Realm.getDefaultInstance();

        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getChannelRoom() == null) {
            if (FragmentChannelProfile.onBackFragment != null)
                FragmentChannelProfile.onBackFragment.onBack();
            return;
        }
        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
        initials = realmRoom.getInitials();
        /*color = realmRoom.getColor();*/
        role = realmChannelRoom.getRole();
        isPrivate = realmChannelRoom.isPrivate();
        /*isSignature = realmChannelRoom.isSignature();*/
        description = realmChannelRoom.getDescription();

        channelName.setValue(realmRoom.getTitle());
        isVerifiedChannel.setValue(realmChannelRoom.isVerified());
        if (isPrivate) {
            channelLink.setValue(realmChannelRoom.getInviteLink());
            channelLinkTitle.setValue(G.fragmentActivity.getResources().getString(R.string.channel_link));
        } else {
            channelLink.setValue(realmChannelRoom.getUsername());
            channelLinkTitle.setValue(G.fragmentActivity.getResources().getString(R.string.st_username));
        }
        isShowLink.setValue(!(isPrivate && ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR))));
        Log.wtf("view model", "value234: " + realmRoom.getMute());
        isMuteNotification.setValue(!realmRoom.getMute());

        //todo: move to edit channel fragment
        /*if (realmChannelRoom.isReactionStatus()) {
            isReactionStatus.set(true);
        } else {
            isReactionStatus.set(false);
        }*/

        try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }
        participantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
        members = realmChannelRoom.getMembers();
        subscribersCount.setValue(String.valueOf(participantsCountLabel));
        administratorsCount.setValue(String.valueOf(RealmMember.filterRole(roomId, CHANNEL, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString()).size()));
        moderatorsCount.setValue(String.valueOf(RealmMember.filterRole(roomId, CHANNEL, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString()).size()));

        //todo:move to edit channel fragment
        /*if (isNotJoin) {
            settingVisibility.set(View.GONE);
        }*/
        //todo:move to edit channel fragment
        /*if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            addMemberVisibility.set(View.GONE);
        }*/
        //todo:move to edit channel fragment
        /*if (role == ChannelChatRole.OWNER) {

            signatureVisibility.set(View.VISIBLE);
        } else {
            channelNameEnable.set(false);
            channelDescriptionEnable.set(false);
        }*/
        //todo:move to edit channel fragment
        /*if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {

            fabVisibility.set(View.VISIBLE);

            menuPopupVisibility.setValue(View.VISIBLE);
        } else {
            fabVisibility.set(View.GONE);
            menuPopupVisibility.setValue(View.GONE);
        }*/
        //todo:move to edit channel fragment
        /*if (role != ChannelChatRole.OWNER) {
            if (description == null || description.isEmpty() || description.length() == 0) {
                descriptionVisibility.set(View.GONE);
            }
        }*/
        //todo:move to edit channel fragment
        /*if (role == ChannelChatRole.OWNER) {
            callBackDeleteLeaveChannel.set(G.fragmentActivity.getResources().getString(R.string.channel_delete));
        } else {
            callBackDeleteLeaveChannel.set(G.fragmentActivity.getResources().getString(R.string.channel_left));
        }*/

        callbackChannelDescription.set(new SpannableStringBuilder(""));

        if (description != null && !description.isEmpty()) {
            haveDescription.set(View.VISIBLE);
            SpannableStringBuilder spannableStringBuilder = HelperUrl.setUrlLink(description, true, false, null, true);
            if (spannableStringBuilder != null) {
                callbackChannelDescription.set(spannableStringBuilder);
            }
        } else {
            haveDescription.set(View.GONE);
        }

        /*if (isSignature) {
            isCheckedSignature.set(true);
        } else {
            isCheckedSignature.set(false);
        }*/

        attachFile = new AttachFile(G.fragmentActivity);
        /*initRecycleView();*/
        /*showAdminOrModeratorList();*/

        /*FragmentShearedMedia.getCountOfSharedMedia(roomId);*/

    }

    public void onNotificationCheckChange(boolean isChecked) {
        Log.wtf("view model", "value: " + isMuteNotification.getValue() + "+" + isChecked);
        isMuteNotification.setValue(isChecked);
    }

    public void onNotificationClick(){
        isMuteNotification.setValue(!isMuteNotification.getValue());
        isMuteNotificationChangeListener.setValue(isMuteNotification.getValue());

    }
    //Todo: move to edit channel fragment
    /*public void onClickChannelListAdmin(View v) {
        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
    }*/

    // todo: move to edit channel fragment
    /*public void onClickChannelListModerator(View v) {
        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
    }*/

    // todo: move to edit channel fragment
    /*public void onClickChannelNotification(View v) {
        notificationAndSound();
    }*/

    public void onClickCircleImage() {
        if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
            FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.channel);
            //fragment.appBarLayout = fab;
            //new HelperFragment(fragment).setResourceContainer(R.id.fragmentContainer_channel_profile).load();
            new HelperFragment(fragment).setReplace(false).load();
        }

    }

    //share link
    public void onClickChannelLink() {

    }

    //todo: move to edit channel fragment
    /*public void onClickChannelSharedMedia(View v) {
        new HelperFragment(FragmentShearedMedia.newInstance(roomId)).setReplace(false).load();
    }*/

    //todo: move to edit channel fragment
    /*public void onClickChannelName(View v) {
        ChangeGroupName(v);
    }*/

    // todo: move to edit channel fragment
    /*public void onClickChannelDescription(View v) {
        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
            ChangeGroupDescription(v);
        }
    }*/

    //todo:move to edit channel fragment
    /*public void onClickChannelShowMember(View v) {
        showListForCustomRole(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());
    }*/

    //todo:move to edit channel fragment
    /*public void onClickChannelAddMember(View v) {
        addMemberToChannel();
    }*/


    //todo : move to edit channel fragment
    /*public void onClickChannelLink() {
        isPopup = false;
        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
            if (isPrivate) {
                dialogRevoke();
            } else {
                setUsername(v);
            }
        } else {
            dialogCopyLink();
        }

    }*/

    public void onResume() {
        /*mRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (mRoom != null) {

            if (changeListener == null) {
                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(final RealmModel element) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (((RealmRoom) element).isValid()) {
                                    String countText = ((RealmRoom) element).getSharedMediaCount();
                                    if (HelperCalander.isPersianUnicode) {
                                        callbackChannelSharedMedia.set(HelperCalander.convertToUnicodeFarsiNumber(countText));
                                    } else {
                                        callbackChannelSharedMedia.set(countText);
                                    }
                                }
                            }
                        });
                    }
                };
            }

            mRoom.addChangeListener(changeListener);
            changeListener.onChange(mRoom);
        } else {
            if (callbackChannelSharedMedia.get() != null) {
                callbackChannelSharedMedia.set(context.getString(R.string.there_is_no_sheared_media));
            }
        }*/
    }

    public void onStop() {
        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
        showLoading.setValue(false);
    }

    public void onDestroy() {
        if (realmChannelProfile != null && !realmChannelProfile.isClosed()) {
            realmChannelProfile.close();
        }
    }

    private Realm getRealm() {
        if (realmChannelProfile == null || realmChannelProfile.isClosed()) {
            realmChannelProfile = Realm.getDefaultInstance();
        }
        return realmChannelProfile;
    }

    /*private void dialogRevoke() {

        String link = callbackChannelLink.get();

        final LinearLayout layoutRevoke = new LinearLayout(G.fragmentActivity);
        layoutRevoke.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputRevoke = new TextInputLayout(G.fragmentActivity);
        edtRevoke = new MEditText(G.fragmentActivity);
        edtRevoke.setHint(G.fragmentActivity.getResources().getString(R.string.channel_link_hint_revoke));
        edtRevoke.setTypeface(G.typeface_IRANSansMobile);
        edtRevoke.setText(link);
        edtRevoke.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtRevoke.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtRevoke.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtRevoke.setPadding(0, 8, 0, 8);
        edtRevoke.setEnabled(false);
        edtRevoke.setSingleLine(true);
        inputRevoke.addView(edtRevoke);
        inputRevoke.addView(viewRevoke, viewParams);

        viewRevoke.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtRevoke.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutRevoke.addView(inputRevoke, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_link_title_revoke))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.revoke))
                .customView(layoutRevoke, true)
                .widgetColor(Color.parseColor(G.appBarColor))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .neutralText(R.string.array_Copy)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = callbackChannelLink.get();
                        ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestChannelRevokeLink().channelRevokeLink(roomId);
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/

    /*private void dialogCopyLink() {

        String link = callbackChannelLink.get();

        final LinearLayout layoutChannelLink = new LinearLayout(G.fragmentActivity);
        layoutChannelLink.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputChannelLink = new TextInputLayout(G.fragmentActivity);
        MEditText edtLink = new MEditText(G.fragmentActivity);
        edtLink.setHint(G.fragmentActivity.getResources().getString(R.string.channel_public_hint_revoke));
        edtLink.setTypeface(G.typeface_IRANSansMobile);
        edtLink.setText(link);
        edtLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtLink.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtLink.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtLink.setPadding(0, 8, 0, 8);
        edtLink.setEnabled(false);
        edtLink.setSingleLine(true);
        inputChannelLink.addView(edtLink);
        inputChannelLink.addView(viewRevoke, viewParams);

        TextView txtLink = new AppCompatTextView(G.fragmentActivity);
        txtLink.setText(Config.IGAP_LINK_PREFIX + link);
        txtLink.setTextColor(G.context.getResources().getColor(R.color.gray_6c));

        viewRevoke.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLink.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutChannelLink.addView(inputChannelLink, layoutParams);
        layoutChannelLink.addView(txtLink, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_link))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.array_Copy))
                .customView(layoutChannelLink, true)
                .widgetColor(Color.parseColor(G.appBarColor))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = Config.IGAP_LINK_PREFIX + callbackChannelLink.get();
                        ClipboardManager clipboard = (ClipboardManager) G.fragmentActivity.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .build();

        dialog.show();
    }*/

    //****** show admin or moderator list

    /*private void initRecycleView() {
        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };
    }*/


    //todo move to edit channel fragment
    /*private void showListForCustomRole(String SelectedRole) {
        if (role != null) {
            FragmentShowMember fragment = FragmentShowMember.newInstance1(this.fragment, roomId, role.toString(), G.userId, SelectedRole, isNeedGetMemberList);
            new HelperFragment(fragment).setReplace(false).load();
            isNeedGetMemberList = false;
        }
    }*/

    private void showAdminOrModeratorList() {
        if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            listAdminVisibility.set(View.GONE);
            moderatorVisibility.set(View.GONE);
        } else if (role == ChannelChatRole.ADMIN) {
            listAdminVisibility.set(View.GONE);
        }
    }

    //todo move to edit channel fragment
    /*private void addMemberToChannel() {
        List<StructContactInfo> userList = Contacts.retrieve(null);
        RealmList<RealmMember> memberList = RealmMember.getMembers(getRealm(), roomId);

        for (int i = 0; i < memberList.size(); i++) {
            for (int j = 0; j < userList.size(); j++) {
                if (userList.get(j).peerId == memberList.get(i).getPeerId()) {
                    userList.remove(j);
                    break;
                }
            }
        }

        Fragment fragment = ShowCustomList.newInstance(userList, new OnSelectedList() {
            @Override
            public void getSelectedList(boolean result, String message, int countForShowLastMessage, final ArrayList<StructContactInfo> list) {

                for (int i = 0; i < list.size(); i++) {
                    new RequestChannelAddMember().channelAddMember(roomId, list.get(i).peerId);
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", false);
        bundle.putLong("COUNT_MESSAGE", noLastMessage);
        fragment.setArguments(bundle);

        new HelperFragment(fragment).setReplace(false).load();
    }*/

    //****** create popup

    //todo move to edit channel fragment
    /*@Override
    public void OnChannelUpdateReactionStatusError() {
        hideProgressBar();
    }*/

    //********** channel Add Member

    //todo move to edit channel fragment
    /*private void channelAddMemberResponse(long roomIdResponse, final long userId, final ProtoGlobal.ChannelRoom.Role role) {
        if (roomIdResponse == roomId) {
            RealmRegisteredInfo realmRegistered = RealmRegisteredInfo.getRegistrationInfo(getRealm(), userId);
            if (realmRegistered == null) {
                new RequestUserInfo().userInfo(userId, roomId + "");
            }
        }
    }*/

    //todo: change to edit channel fragment
    /*private void ChangeGroupDescription(final View v) {
        MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.channel_description).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).alwaysCallInputCallback().widgetColor(Color.parseColor(G.appBarColor)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                editChannelRequest(callbackChannelName.get(), dialogDesc);
                showProgressBar();
            }
        }).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT).input(G.fragmentActivity.getResources().getString(R.string.please_enter_group_description), callbackChannelDescription.get().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something
                View positive = dialog.getActionButton(DialogAction.POSITIVE);
                dialogDesc = input.toString();
                if (!input.toString().equals(callbackChannelDescription.get().toString())) {

                    positive.setClickable(true);
                    positive.setAlpha(1.0f);
                } else {
                    positive.setClickable(false);
                    positive.setAlpha(0.5f);
                }
            }
        }).build();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AndroidUtils.closeKeyboard(v);
            }
        });

        dialog.show();
    }*/

    //todo move to edit channel fragment
    /*private void ChangeGroupName(final View v) {
        final LinearLayout layoutUserName = new LinearLayout(G.fragmentActivity);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(G.fragmentActivity);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(G.fragmentActivity);
        final EmojiEditTextE edtNameChannel = new EmojiEditTextE(G.fragmentActivity);
        edtNameChannel.setHint(G.fragmentActivity.getResources().getString(R.string.st_username));
        edtNameChannel.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edtNameChannel.setTypeface(G.typeface_IRANSansMobile);
        edtNameChannel.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtNameChannel.setText(callbackChannelName.get());
        edtNameChannel.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtNameChannel.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtNameChannel.setPadding(0, 8, 0, 8);
        edtNameChannel.setSingleLine(true);
        inputUserName.addView(edtNameChannel);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtNameChannel.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.channel_name)).positiveText(G.fragmentActivity.getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(Color.parseColor(G.appBarColor)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);

        final String finalChannelName = title;
        positive.setEnabled(false);
        edtNameChannel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtNameChannel.getText().toString().equals(finalChannelName)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }
        });

        G.onChannelEdit = new OnChannelEdit() {
            @Override
            public void onChannelEdit(final long roomId, final String name, final String description) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        callbackChannelName.set(name);
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });
            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RequestChannelEdit().channelEdit(roomId, edtNameChannel.getText().toString(), callbackChannelDescription.get().toString());
                dialog.dismiss();
                showProgressBar();
            }
        });

        edtNameChannel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    viewUserName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AndroidUtils.closeKeyboard(v);
            }
        });

        dialog.show();

    }*/

    //********** channel edit name and description

    /*private void editChannelRequest(String name, String description) {
        new RequestChannelEdit().channelEdit(roomId, name, description);
    }*/

    private void setToAdmin(Long peerId) {
        new RequestChannelAddAdmin().channelAddAdmin(roomId, peerId);
    }

    //********** set roles

    private void setToModerator(Long peerId) {
        new RequestChannelAddModerator().channelAddModerator(roomId, peerId);
    }

    /*@Override
    public void onChannelEdit(long roomId, String name, String description) {
        editChannelResponse(roomId, name, description);
    }*/


    //************************************************** interfaces

    //***On Add Avatar Response From Server

    //***Edit Channel

    private void closeActivity() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                showLoading.setValue(false);
                if (FragmentChat.finishActivity != null) {
                    FragmentChat.finishActivity.finishActivity();
                }
            }
        });
    }

    //***

    //***Member
    /*@Override
    public void onChannelAddMember(final Long roomId, final Long userId, final ProtoGlobal.ChannelRoom.Role role) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                channelAddMemberResponse(roomId, userId, role);
            }
        });
    }*/

    //***Moderator
    /*@Override
    public void onChannelAddModerator(long roomId, long memberId) {

    }*/

    /*@Override
    public void onChannelKickModerator(long roomId, long memberId) {

    }*/

    //***Admin
    /*@Override
    public void onChannelAddAdmin(long roomId, long memberId) {

    }*/

    /*@Override
    public void onChannelKickAdmin(long roomId, long memberId) {

    }*/

    /*@Override
    public void onChannelRevokeLink(final long roomId, final String inviteLink, final String inviteToken) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                showLoading.setValue(false);
                callbackChannelLink.set("" + inviteLink);
                RealmChannelRoom.revokeLink(roomId, inviteLink, inviteToken);
            }
        });
    }*/

    //***time out and errors for either of this interfaces

    /*@Override
    public void onError(int majorCode, int minorCode) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                showLoading.setValue(false);
                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.normal_error), false);
            }
        });
    }*/

    /*@Override
    public void onTimeOut() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                showLoading.setValue(false);
                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);
            }
        });
    }*/

    // todo:move to edit channel fragment
    /*private void notificationAndSound() {
        FragmentNotification fragmentNotification = new FragmentNotification();
        Bundle bundle = new Bundle();
        bundle.putString("PAGE", "CHANNEL");
        bundle.putLong("ID", roomId);
        fragmentNotification.setArguments(bundle);

        new HelperFragment(fragmentNotification).setReplace(false).load();
    }*/

    //todo : move to edit channel fragment
    /*private class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(G.fragmentActivity, view, Gravity.TOP);
            popup.getMenuInflater().inflate(R.menu.menu_item_group_profile, popup.getMenu());

            if (role == ChannelChatRole.OWNER) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
            } else if (role == ChannelChatRole.ADMIN) {

                *//**
     *  ----------- Admin ---------------
     *  1- admin dose'nt access set another admin
     *  2- admin can set moderator
     *  3- can remove moderator
     *  4- can kick moderator and Member
     *//*

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
            } else if (role == ChannelChatRole.MODERATOR) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                }
            } else {

                return;
            }

            // Setup menu item selection
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_setAdmin:
                            setToAdmin(info.peerId);
                            return true;
                        case R.id.menu_set_moderator:
                            setToModerator(info.peerId);
                            return true;
                        case R.id.menu_remove_admin:
                            ((FragmentChannelProfile) fragment).kickAdmin(info.peerId);
                            return true;
                        case R.id.menu_remove_moderator:
                            ((FragmentChannelProfile) fragment).kickModerator(info.peerId);
                            return true;
                        case R.id.menu_kick:
                            ((FragmentChannelProfile) fragment).kickMember(info.peerId);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            // Handle dismissal with: popup.setOnDismissListener(...);
            // Show the menu
            popup.show();
        }
    }*/


}
