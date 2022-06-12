package net.iGap.viewmodel;

import android.view.Gravity;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentShearedMedia;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.model.GoToSharedMediaModel;
import net.iGap.module.AppUtils;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnUserContactDelete;
import net.iGap.observers.interfaces.OnUserContactEdit;
import net.iGap.observers.interfaces.OnUserInfoResponse;
import net.iGap.observers.interfaces.OnUserUpdateStatus;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObjectChangeListener;

public class FragmentContactsProfileViewModel extends ViewModel implements OnUserContactEdit, OnUserUpdateStatus, OnUserInfoResponse {

    public ObservableInt bioVisibility = new ObservableInt(View.VISIBLE);
    public ObservableBoolean showNumber = new ObservableBoolean(true);
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> phone = new ObservableField<>("0");
    public ObservableField<String> bio = new ObservableField<>();
    public ObservableInt verifyTextVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt textsGravity = new ObservableInt(Gravity.LEFT);
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
    public ObservableInt userBlockState = new ObservableInt(R.string.block);
    public ObservableInt sharedEmptyVisibility = new ObservableInt(View.VISIBLE);
    public MutableLiveData<Integer> callVisibility = new MutableLiveData<>();
    public MutableLiveData<Integer> videoCallVisibility = new MutableLiveData<>();
    public MutableLiveData<Integer> menuVisibility = new MutableLiveData<>();
    public MutableLiveData<Boolean> cloudVisibility = new MutableLiveData<>();
    public ObservableBoolean isMuteNotification = new ObservableBoolean(false);
    public MutableLiveData<Boolean> isMuteNotificationChangeListener = new MutableLiveData<>();
    public ObservableBoolean isShowReportView = new ObservableBoolean(false);
    public MutableLiveData<Boolean> showMenu = new MutableLiveData<>();
    public MutableLiveData<Boolean> showClearChatDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToCustomNotificationPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToShowAvatarPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> setAvatar = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDeleteContactDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogReportContact = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogStartSecretChat = new MutableLiveData<>();
    public MutableLiveData<Boolean> showPhoneNumberDialog = new MutableLiveData<>();
    public MutableLiveData<String> contactName = new MutableLiveData<>();
    public MutableLiveData<String> lastSeen = new MutableLiveData<>();
    public MutableLiveData<Long> goToChatPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public MutableLiveData<GoToSharedMediaModel> goToShearedMediaPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> blockDialogListener = new MutableLiveData<>();
    public MutableLiveData<String> copyUserNameToClipBoard = new MutableLiveData<>();
    public MutableLiveData<Boolean> editContactListener = new MutableLiveData<>();
    public List<String> items;
    private RealmRoom mRoom;
    private RealmRegisteredInfo registeredInfo;
    private RealmList<RealmAvatar> avatarList;
    private RealmChangeListener<RealmModel> changeListener;
    public long shearedId = -2;
    public String firstName;
    public String lastName;
    public String phoneNumber = "+0";
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
    private boolean isCloud;

    public FragmentContactsProfileViewModel() {

    }

    public void init(long roomId, long userId, String enterFrom, AvatarHandler avatarHandler) {
        this.roomId = roomId;
        this.userId = userId;
        this.enterFrom = enterFrom;
        this.avatarHandler = avatarHandler;

        getUserInfo(); // client should send request for get user info because need to update user online timing
        mainStart();
        startInitCallbacks();
    }

    private void getUserInfo() {
        new RequestUserInfo().userInfo(userId);
    }

    public void onNotificationCheckChange(boolean isChecked) {
        isMuteNotification.set(isChecked);

    }

    public void onNotificationClick() {
        isMuteNotification.set(!isMuteNotification.get());
        isMuteNotificationChangeListener.setValue(isMuteNotification.get());
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
        if (AccountManager.getInstance().getCurrentUser().getId() != userId && !disableDeleteContact) {
            items.add(G.fragmentActivity.getString(R.string.delete_contact));
            items.add(G.fragmentActivity.getString(R.string.edit_contact));
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
        } else if (items.get(position).equals(G.fragmentActivity.getString(R.string.edit_contact))) {
            editContactListener.setValue(true);
        }
    }

    public void onClickGoToChat() {
        if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString()) || enterFrom.equals("Others")) { // Others is from FragmentMapUsers adapter
            RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", userId).findFirst();
            });

            if (realmRoom != null) {
                goToChatPage.setValue(realmRoom.getId());
            } else {
                new RequestChatGetRoom().chatGetRoom(userId, new RequestChatGetRoom.OnChatRoomReady() {
                    @Override
                    public void onReady(ProtoGlobal.Room room) {
                        DbManager.getInstance().doRealmTransaction(realm -> {
                            RealmRoom realmRoom1 = RealmRoom.putOrUpdate(room, realm);
                            realmRoom1.setDeleted(true);
                        });
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.refreshRealmUi();
                                goToChatPage.setValue(room.getId());
                            }
                        });
                    }

                    @Override
                    public void onError(int major, int minor) {

                    }
                });
            }
        } else {
            goBack.setValue(true);
        }
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
        blockDialogListener.postValue(isBlockUser);

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

    public void onUserNameClicked() {

        if (registeredInfo != null) {
            copyUserNameToClipBoard.postValue(registeredInfo.getUsername());
        }

    }

    /**
     * type: 1=image 2=video 3=audio 4=voice 5=gif 6=file 7=link
     */
    public void onSharedMediaItemClick(int type) {
        goToShearedMediaPage.setValue(new GoToSharedMediaModel(roomId, type));
    }

    private void mainStart() {
        if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString()) || roomId == 0) {
            RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", userId).findFirst();
            });
            if (realmRoom != null) {
                shearedId = realmRoom.getId();
            }
        } else {
            shearedId = roomId;
        }

        if (!RealmRoom.isNotificationServices(roomId)) {
            isShowReportView.set(true);
        } else {
            isShowReportView.set(false);
        }

        if (userId == AccountManager.getInstance().getCurrentUser().getId()) {
            cloudVisibility.postValue(true);
        } else
            cloudVisibility.postValue(false);

        registeredInfo = DbManager.getInstance().doRealmTask(realm -> {
            return RealmRegisteredInfo.getRegistrationInfo(realm, userId);
        });
        if (registeredInfo != null) {
            registeredInfo.addChangeListener((RealmObjectChangeListener<RealmRegisteredInfo>) (realmModel, changeSet) -> {
                if (changeSet != null) {
                    for (int i = 0; i < changeSet.getChangedFields().length; i++) {
                        if (changeSet.getChangedFields()[i].equals("blockUser")) {
                            userBlockState.set(realmModel.isBlockUser() ? R.string.un_block_user : R.string.block);
                        }
                    }
                }
            });
            isBot = registeredInfo.isBot();
            if (isBot || userId == AccountManager.getInstance().getCurrentUser().getId()) {
                callVisibility.setValue(View.GONE);
                menuVisibility.setValue(View.GONE);
                videoCallVisibility.setValue(View.GONE);
            } else {
                callVisibility.setValue(View.VISIBLE);
                menuVisibility.setValue(View.VISIBLE);
                videoCallVisibility.setValue(View.VISIBLE);
            }

            isBlockUser = registeredInfo.isBlockUser();
            userBlockState.set(isBlockUser ? R.string.un_block_user : R.string.block);
            registeredInfo.addChangeListener(element -> isBlockUser = registeredInfo.isBlockUser());
            DbManager.getInstance().doRealmTask(realm -> {
                if (registeredInfo.getLastAvatar(realm) != null) {
                    String mainFilePath = registeredInfo.getLastAvatar(realm).getFile().getLocalFilePath();
                    if (mainFilePath != null && new File(mainFilePath).exists()) { // if main image is exist showing that
                        avatarPath = mainFilePath;
                    } else {
                        avatarPath = registeredInfo.getLastAvatar(realm).getFile().getLocalThumbnailPath();
                    }
                    avatarList = registeredInfo.getAvatars(realm);
                }
            });
        }

        RealmContacts realmUser = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmContacts.class).equalTo("id", userId).findFirst();
        });

        if (registeredInfo != null) {
            if (registeredInfo.getDisplayName() != null && !registeredInfo.getDisplayName().equals("")) {
                contactName.setValue(registeredInfo.getDisplayName());
            } else {
                contactName.setValue(G.fragmentActivity.getResources().getString(R.string.nick_name_not_exist));
            }

            if (registeredInfo.getBio() == null || registeredInfo.getBio().length() == 0) {
                bioVisibility.set(View.GONE);
            } else {
                bioVisibility.set(View.VISIBLE);
                bio.set(registeredInfo.getBio());
            }
            username.set(registeredInfo.getUsername());
            phone.set(registeredInfo.getPhoneNumber());
            phoneNumber = "+" + registeredInfo.getPhoneNumber();

            firstName = registeredInfo.getFirstName();
            lastName = registeredInfo.getLastName();
            lastSeenValue = registeredInfo.getLastSeen();
            color = registeredInfo.getColor();
            initials = registeredInfo.getInitials();
            userStatus = registeredInfo.getStatus();
            isVerified = registeredInfo.isVerified();
        } else if (realmUser != null) {
            if (realmUser.getDisplay_name() != null && !realmUser.getDisplay_name().equals("")) {
                contactName.setValue(realmUser.getDisplay_name());
            } else {
                contactName.setValue(G.fragmentActivity.getResources().getString(R.string.nick_name_not_exist));
            }
            username.set(realmUser.getUsername());
            phone.set(Long.toString(realmUser.getPhone()));
            phoneNumber = "+" + realmUser.getPhone();

            firstName = realmUser.getFirst_name();
            lastName = realmUser.getLast_name();
            lastSeenValue = realmUser.getLast_seen();
            color = realmUser.getColor();
            initials = realmUser.getInitials();
            isVerified = realmUser.isVerified();

            if (realmUser.getBio() == null || realmUser.getBio().length() == 0) {
                bioVisibility.set(View.GONE);
            } else {
                bioVisibility.set(View.VISIBLE);
                bio.set(realmUser.getBio());
            }
        }

        if (isVerified) {
            verifyTextVisibility.set(View.VISIBLE);
        } else {
            verifyTextVisibility.set(View.INVISIBLE);
        }

        //todo: fixed it two times check it and first and her
        if (userId != 134 && AccountManager.getInstance().getCurrentUser().getId() != userId) {
            RealmCallConfig callConfig = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmCallConfig.class).findFirst();
            });
            if (callConfig != null) {

                if (isBot) {
                    callVisibility.setValue(View.GONE);
                    videoCallVisibility.setValue(View.GONE);
                } else {

                    if (callConfig.isVoice_calling()) {
                        callVisibility.setValue(View.VISIBLE);
                    }

                    if (callConfig.isVideo_calling()) {
                        videoCallVisibility.setValue(View.VISIBLE);
                    }
                }


            } else {
                new RequestSignalingGetConfiguration().signalingGetConfiguration();
            }
        } else {
            callVisibility.setValue(View.GONE);
            videoCallVisibility.setValue(View.GONE);
        }
        RealmContacts realmContacts = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmContacts.class).equalTo("phone", Long.parseLong(phone.get())).findFirst();
        });

        /**
         * if this user isn't in my contacts don't show phone number
         */
        if (realmContacts == null && enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            showNumber.set(false);
            disableDeleteContact = true;
            menuVisibility.setValue(View.GONE);
        }

        if (!G.isAppRtl) {
            textsGravity.set(Gravity.LEFT);
        } else {
            textsGravity.set(Gravity.RIGHT);
        }

        setUserStatus(userStatus, lastSeenValue);
        setAvatar.setValue(userId != AccountManager.getInstance().getCurrentUser().getId());
        //todo: change it
        FragmentShearedMedia.getCountOfSharedMedia(shearedId);

        if (registeredInfo == null) {
            callVisibility.setValue(View.GONE);
            menuVisibility.setValue(View.GONE);
            videoCallVisibility.setValue(View.GONE);
        }
    }

    private void startInitCallbacks() {
        G.onUserUpdateStatus = this;
        G.onUserContactEdit = this;
        G.onUserInfoResponse = this;
    }

    public void onImageClick() {
        if (userId == AccountManager.getInstance().getCurrentUser().getId())
            return; //dont work when profile was cloud
        if (DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmAvatar.class).equalTo("ownerId", userId).findFirst();
        }) != null) {
            goToShowAvatarPage.setValue(userId == AccountManager.getInstance().getCurrentUser().getId());
        }
    }

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
        if (userId == user.getId()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (user.getDisplayName() != null && !user.getDisplayName().equals("")) {
                        contactName.setValue(user.getDisplayName());
                    }
                    setAvatar.setValue(userId != AccountManager.getInstance().getCurrentUser().getId());
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
                setAvatar.setValue(userId != AccountManager.getInstance().getCurrentUser().getId());
                contactName.setValue(firstName + " " + lastName);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if (registeredInfo != null) {
            registeredInfo.removeAllChangeListeners();
        }
    }

    private void setUserStatus(String userStatus, long time) {
        this.userStatus = userStatus;
        this.lastSeenValue = time;

        if (isBot) {
            lastSeen.setValue(G.context.getResources().getString(R.string.bot));
            return;
        }

        if (userStatus != null) {
            if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                String status = LastSeenTimeUtil.computeTime(G.context, userId, time, false);

                lastSeen.setValue(status);
            } else {
                lastSeen.setValue(userStatus);
            }
        }
    }

    public void onResume() {
        mRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", shearedId).findFirst();
        });

        if (mRoom != null) {
            isMuteNotification.set(mRoom.getMute());
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

    public MutableLiveData<Boolean> getCloudVisibility() {
        return cloudVisibility;
    }
}
