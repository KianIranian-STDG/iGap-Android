package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentCall;
import net.iGap.fragments.FragmentShearedMedia;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.interfaces.OnUserContactDelete;
import net.iGap.interfaces.OnUserContactEdit;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserUpdateStatus;
import net.iGap.module.AppUtils;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserContactsBlock;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsUnblock;
import net.iGap.request.RequestUserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;

public class FragmentContactsProfileViewModel implements OnUserContactEdit, OnUserUpdateStatus, OnUserInfoResponse {

    public ObservableInt videoCallVisibility = new ObservableInt(View.GONE);
    public ObservableInt callVisibility = new ObservableInt(View.GONE);
    public ObservableInt menuVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt bioVisibility = new ObservableInt(View.VISIBLE);
    public ObservableBoolean showNumber = new ObservableBoolean(true);
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> contactName = new ObservableField<>();
    public ObservableField<String> lastSeen = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>("0");
    public ObservableField<String> bio = new ObservableField<>();
    public ObservableField<Integer> verifyTextVisibility = new ObservableField<>(View.VISIBLE);

    public ObservableInt sharedPhotoVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedPhotoCount = new ObservableInt(0);
    public ObservableInt sharedVideoVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedVideoCount = new ObservableInt(0);
    public ObservableInt sharedAudioVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedAudioCount = new ObservableInt(0);
    public ObservableInt sharedVoiceVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedVoiceCount = new ObservableInt(0);
    public ObservableInt sharedGifVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedGifCount = new ObservableInt(0);
    public ObservableInt sharedFileVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedFileCount = new ObservableInt(0);
    public ObservableInt sharedLinkVisibility = new ObservableInt(View.GONE);
    public ObservableInt sharedLinkCount = new ObservableInt(0);
    public ObservableInt sharedEmptyVisibility = new ObservableInt(View.VISIBLE);

    public MutableLiveData<Boolean> isMuteNotification = new MutableLiveData<>();
    public MutableLiveData<Boolean> isMuteNotificationChangeListener = new MutableLiveData<>();
    public MutableLiveData<Boolean> isShowReportView = new MutableLiveData<>();

    //ui event and observed
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public MutableLiveData<Boolean> showMenu = new MutableLiveData<>();
    public MutableLiveData<Boolean> showClearChatDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToCustomNotificationPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> setAvatar = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDeleteContactDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogReportContact = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogStartSecretChat = new MutableLiveData<>();
    public MutableLiveData<Boolean> showPhoneNumberDialog = new MutableLiveData<>();

    public List<String> items;
    private Realm realm;
    private RealmRoom mRoom;
    private RealmRegisteredInfo registeredInfo;
    private RealmList<RealmAvatar> avatarList;
    private RealmChangeListener<RealmModel> changeListener;
    public long shearedId = -2;
    public String firstName;
    public String lastName;
    public boolean isBlockUser = false;
    public boolean disableDeleteContact = false;
    public boolean isVerified = false;
    public long userId;
    public long roomId;
    private long lastSeenValue;
    private String enterFrom;
    private String initials;
    private String color;
    private String userStatus;
    private String avatarPath;
    private AvatarHandler avatarHandler;
    private boolean isBot = false;

    public FragmentContactsProfileViewModel(long roomId, long userId, String enterFrom, AvatarHandler avatarHandler) {
        this.roomId = roomId;
        this.userId = userId;
        this.enterFrom = enterFrom;
        this.avatarHandler = avatarHandler;

        mainStart();
        startInitCallbacks();
        getUserInfo(); // client should send request for get user info because need to update user online timing
    }

    private void getUserInfo() {
        new RequestUserInfo().userInfo(userId);
    }

    public void onBackButtonOnClick() {
        goBack.setValue(true);
    }

    public void onVideoCallClick() {
        FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);
    }

    public void onVoiceCallButtonClick() {
        FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
    }

    public void onNotificationCheckChange(boolean isChecked) {
        Log.wtf("view model", "value: " + isMuteNotification.getValue() + "+" + isChecked);
        isMuteNotification.setValue(isChecked);

    }

    public void onNotificationClick(){
        isMuteNotification.setValue(!isMuteNotification.getValue());
        isMuteNotificationChangeListener.setValue(isMuteNotification.getValue());

    }

    public void onMoreButtonClick() {
        //todo: fixed string list
        items = new ArrayList<>();
        /*if (G.userId != userId) {
            if (isBlockUser) {
                items.add(G.fragmentActivity.getString(R.string.un_block_user));
            } else {
                items.add(G.fragmentActivity.getString(R.string.block_user));
            }
        }*/
        /*items.add(G.fragmentActivity.getString(R.string.clear_history));*/
        if (G.userId != userId && !disableDeleteContact) {
            items.add(G.fragmentActivity.getString(R.string.delete_contact));
        }
        showMenu.setValue(true);
    }

    public void onMenuItemClick(int position) {
        /*if (items.get(position).equals(G.fragmentActivity.getString(R.string.un_block_user)) || items.get(position).equals(G.fragmentActivity.getString(R.string.block_user))) {
            blockOrUnblockUser();
        } else if (items.get(position).equals(G.fragmentActivity.getString(R.string.clear_history))) {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    if (FragmentChat.onComplete != null) {
                        FragmentChat.onComplete.complete(false, roomId + "", "");
                    }
                }
            }).negativeText(R.string.B_cancel).show();
        } else*/
        if (items.get(position).equals(G.fragmentActivity.getString(R.string.delete_contact))) {
            showDeleteContactDialog.setValue(true);
        } /*else if (items.get(position).equals(G.fragmentActivity.getString(R.string.report))) {
            showDialogReportContact.setValue(true);
        }*/
    }

    public void onClearChatClick() {
        showClearChatDialog.setValue(true);
    }

    public void onPhoneNumberClick() {
        showPhoneNumberDialog.setValue(true);
    }

    public void onCustomNotificationClick() {
        goToCustomNotificationPage.setValue(true);
    }

    public void onReportButtonClick() {
        showDialogReportContact.setValue(true);
    }

    public void onBlockButtonClick() {
        //todo: move view code to fragment
        if (isBlockUser) {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.unblock_the_user).content(R.string.unblock_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestUserContactsUnblock().userContactsUnblock(userId);
                }
            }).negativeText(R.string.cancel).show();
        } else {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.block_the_user).content(R.string.block_the_user_text).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    new RequestUserContactsBlock().userContactsBlock(userId);
                }
            }).negativeText(R.string.cancel).show();
        }
    }

    public void onSecretChatClick() {
        showDialogStartSecretChat.setValue(true);
    }

    public void deleteContact() {
        G.onUserContactdelete = new OnUserContactDelete() {
            @Override
            public void onContactDelete() {
                getUserInfo();
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };
        new RequestUserContactsDelete().contactsDelete(phone.get());
    }

    //===============================================================================
    //=====================================Starts====================================
    //===============================================================================

    private void mainStart() {
        if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString()) || roomId == 0) {
            RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();
            if (realmRoom != null) {
                shearedId = realmRoom.getId();
            }
        } else {
            shearedId = roomId;
        }

        if (!RealmRoom.isNotificationServices(roomId)) {
            isShowReportView.setValue(true);
        } else {
            isShowReportView.setValue(false);
        }

        registeredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealm(), userId);

        if (registeredInfo != null) {
            isBot = registeredInfo.isBot();
            if (isBot) {
                callVisibility.set(View.GONE);
                menuVisibility.set(View.GONE);
                videoCallVisibility.set(View.GONE);
            }

            isBlockUser = registeredInfo.isBlockUser();
            registeredInfo.addChangeListener(element -> isBlockUser = registeredInfo.isBlockUser());

            if (registeredInfo.getLastAvatar() != null) {
                String mainFilePath = registeredInfo.getLastAvatar().getFile().getLocalFilePath();
                if (mainFilePath != null && new File(mainFilePath).exists()) { // if main image is exist showing that
                    avatarPath = mainFilePath;
                } else {
                    avatarPath = registeredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
                }
                avatarList = registeredInfo.getAvatars();
            }
        }

        RealmContacts realmUser = getRealm().where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();

        if (registeredInfo != null) {
            if (registeredInfo.getDisplayName() != null && !registeredInfo.getDisplayName().equals("")) {
                contactName.set(registeredInfo.getDisplayName());
            } else {
                contactName.set(G.fragmentActivity.getResources().getString(R.string.nick_name_not_exist));
            }

            if (registeredInfo.getBio() == null || registeredInfo.getBio().length() == 0) {
                bioVisibility.set(View.GONE);
            } else {
                bio.set(registeredInfo.getBio());
            }
            username.set(registeredInfo.getUsername());
            phone.set(registeredInfo.getPhoneNumber());

            firstName = registeredInfo.getFirstName();
            lastName = registeredInfo.getLastName();
            lastSeenValue = registeredInfo.getLastSeen();
            color = registeredInfo.getColor();
            initials = registeredInfo.getInitials();
            userStatus = registeredInfo.getStatus();
            isVerified = registeredInfo.isVerified();
        } else if (realmUser != null) {
            if (realmUser.getDisplay_name() != null && !realmUser.getDisplay_name().equals("")) {
                contactName.set(realmUser.getDisplay_name());
            } else {
                contactName.set(G.fragmentActivity.getResources().getString(R.string.nick_name_not_exist));
            }
            username.set(realmUser.getUsername());
            phone.set(Long.toString(realmUser.getPhone()));

            firstName = realmUser.getFirst_name();
            lastName = realmUser.getLast_name();
            lastSeenValue = realmUser.getLast_seen();
            color = realmUser.getColor();
            initials = realmUser.getInitials();
            isVerified = realmUser.isVerified();

            if (realmUser.getBio() == null || realmUser.getBio().length() == 0) {
                bioVisibility.set(View.GONE);
            } else {
                bio.set(realmUser.getBio());
            }
        }

        if (isVerified) {
            verifyTextVisibility.set(View.VISIBLE);
        } else {
            verifyTextVisibility.set(View.INVISIBLE);
        }

        if (userId != 134 && G.userId != userId) {
            RealmCallConfig callConfig = getRealm().where(RealmCallConfig.class).findFirst();
            if (callConfig != null) {

                if (isBot) {
                    callVisibility.set(View.GONE);
                    videoCallVisibility.set(View.GONE);
                } else {

                    if (callConfig.isVoice_calling()) {
                        callVisibility.set(View.VISIBLE);
                    }

                    if (callConfig.isVideo_calling()) {
                        videoCallVisibility.set(View.VISIBLE);
                    }
                }


            } else {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        }
        RealmContacts realmContacts = getRealm().where(RealmContacts.class).equalTo(RealmContactsFields.PHONE, Long.parseLong(phone.get())).findFirst();

        /**
         * if this user isn't in my contacts don't show phone number
         */
        if (realmContacts == null && enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            showNumber.set(false);
            disableDeleteContact = true;
        }

        setUserStatus(userStatus, lastSeenValue);
        setAvatar.setValue(true);
        setAvatar.setValue(true);
        //todo: change it
        FragmentShearedMedia.getCountOfSharedMedia(shearedId);
    }

    private void startInitCallbacks() {
        G.onUserUpdateStatus = this;
        G.onUserContactEdit = this;
        G.onUserInfoResponse = this;
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================


    public void onImageClick() {
        if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst() != null) {
            FragmentShowAvatars fragment;
            if (userId == G.userId) {
                fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
            } else {
                fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.chat);
            }

            //fragment.appBarLayout = fab; //TODO- check
            new HelperFragment(fragment).setReplace(false).load();
        }
    }

    //===============================================================================
    //===================================Callbacks===================================
    //===============================================================================

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
        if (userId == user.getId()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (user.getDisplayName() != null && !user.getDisplayName().equals("")) {
                        contactName.set(user.getDisplayName());
                    }
                    setAvatar.setValue(true);
                }
            });
        }
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }

    @Override
    public void onContactEdit(final String firstName, final String lastName, String initials) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                setAvatar.setValue(true);
                contactName.set(firstName + " " + lastName);
            }
        });
    }

    @Override
    public void onContactEditTimeOut() {

    }

    @Override
    public void onContactEditError(int majorCode, int minorCode) {

    }

    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {
        if (this.userId == userId) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    setUserStatus(AppUtils.getStatsForUser(status), time);
                }
            });
        }
    }

    //===============================================================================
    //====================================Methods====================================
    //===============================================================================

    private void setUserStatus(String userStatus, long time) {
        this.userStatus = userStatus;
        this.lastSeenValue = time;

        if (isBot) {
            lastSeen.set(G.context.getResources().getString(R.string.bot));
            return;
        }

        if (userStatus != null) {
            if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String status = LastSeenTimeUtil.computeTime(userId, time, false);

                lastSeen.set(status);
            } else {
                lastSeen.set(userStatus);
            }
        }
    }

    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    public void onResume() {
        mRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, shearedId).findFirst();
        if (mRoom != null) {
            isMuteNotification.setValue(mRoom.getMute());
            if (changeListener == null) {
                changeListener = element -> G.handler.post(() -> {
                    if (((RealmRoom) element).isValid()) {
                        String countText = ((RealmRoom) element).getSharedMediaCount();
                        if (HelperCalander.isPersianUnicode) {
                            countText = HelperCalander.convertToUnicodeFarsiNumber(countText);
                        }
                        if (countText == null || countText.length() == 0) {
                            sharedEmptyVisibility.set(View.VISIBLE);
                        } else {
                            String[] countList = countText.split("\n");
                            int countOFImage = Integer.parseInt(countList[0]);
                            int countOFVIDEO = Integer.parseInt(countList[1]);
                            int countOFAUDIO = Integer.parseInt(countList[2]);
                            int countOFVOICE = Integer.parseInt(countList[3]);
                            int countOFGIF = Integer.parseInt(countList[4]);
                            int countOFFILE = Integer.parseInt(countList[5]);
                            int countOFLink = Integer.parseInt(countList[6]);
                            if (countOFImage > 0 || countOFVIDEO > 0 || countOFAUDIO > 0 || countOFVOICE > 0 || countOFGIF > 0 || countOFFILE > 0 || countOFLink > 0) {
                                sharedEmptyVisibility.set(View.GONE);
                                if (countOFImage > 0) {
                                    sharedPhotoVisibility.set(View.VISIBLE);
                                    sharedPhotoCount.set(countOFImage);
                                } else {
                                    sharedPhotoVisibility.set(View.GONE);
                                }
                                if (countOFVIDEO > 0) {
                                    sharedVideoVisibility.set(View.VISIBLE);
                                    sharedVideoCount.set(countOFVIDEO);
                                } else {
                                    sharedVideoVisibility.set(View.GONE);
                                }
                                if (countOFAUDIO > 0) {
                                    sharedAudioVisibility.set(View.VISIBLE);
                                    sharedAudioCount.set(countOFAUDIO);
                                } else {
                                    sharedAudioVisibility.set(View.GONE);
                                }
                                if (countOFVOICE > 0) {
                                    sharedVoiceVisibility.set(View.VISIBLE);
                                    sharedVoiceCount.set(countOFVOICE);
                                } else {
                                    sharedVoiceVisibility.set(View.GONE);
                                }
                                if (countOFGIF > 0) {
                                    sharedGifVisibility.set(View.VISIBLE);
                                    sharedGifCount.set(countOFGIF);
                                } else {
                                    sharedGifVisibility.set(View.GONE);
                                }
                                if (countOFFILE > 0) {
                                    sharedFileVisibility.set(View.VISIBLE);
                                    sharedFileCount.set(countOFFILE);
                                } else {
                                    sharedFileVisibility.set(View.GONE);
                                }
                                if (countOFLink > 0) {
                                    sharedLinkVisibility.set(View.VISIBLE);
                                    sharedLinkCount.set(countOFLink);
                                } else {
                                    sharedLinkVisibility.set(View.GONE);
                                }
                            } else {
                                sharedEmptyVisibility.set(View.VISIBLE);
                            }
                        }
                    }
                });
            }
            mRoom.addChangeListener(changeListener);
            changeListener.onChange(mRoom);
        } else {
            /*sharedMedia.set(context.getString(R.string.there_is_no_sheared_media));*/
            sharedEmptyVisibility.set(View.VISIBLE);
        }
    }

    public void onPause() {
        if (G.onUpdateUserStatusInChangePage != null) {
            G.onUpdateUserStatusInChangePage.updateStatus(userId, userStatus, lastSeenValue);
        }
    }

    public void onStop() {
        if (registeredInfo != null) {
            registeredInfo.removeAllChangeListeners();
        }

        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
    }

    public void onDestroy() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

}
