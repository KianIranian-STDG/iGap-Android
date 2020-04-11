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

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChannelProfile;
import net.iGap.fragments.FragmentShearedMedia;
import net.iGap.helper.HelperCalander;
import net.iGap.model.GoToSharedMediaModel;
import net.iGap.model.GoToShowMemberModel;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.observers.interfaces.OnChannelLeft;
import net.iGap.observers.interfaces.OnMenuClick;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelAddAdmin;
import net.iGap.request.RequestChannelAddModerator;
import net.iGap.request.RequestChannelLeft;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;

public class FragmentChannelProfileViewModel extends ViewModel
        /*implements OnChannelAddMember, OnChannelAddModerator, OnChannelUpdateReactionStatus, OnChannelKickModerator, OnChannelAddAdmin, OnChannelKickAdmin, OnChannelDelete,
        OnChannelLeft, OnChannelEdit, OnChannelRevokeLink*/ {

    public static final String FRAGMENT_TAG = "FragmentChannelProfile";

    public ObservableInt haveDescription = new ObservableInt(View.VISIBLE);
    public ObservableInt isVerifiedChannel = new ObservableInt(View.GONE);
    public ObservableField<String> channelLink = new ObservableField<>("Link");
    public ObservableInt isShowLink = new ObservableInt(View.GONE);
    public ObservableInt channelLinkTitle = new ObservableInt(R.string.invite_link_title);
    public ObservableBoolean isMuteNotification = new ObservableBoolean(false);
    public MutableLiveData<Boolean> muteNotifListener = new MutableLiveData<>();
    public ObservableField<String> subscribersCount = new ObservableField<>("0");
    public ObservableField<String> administratorsCount = new ObservableField<>("0");
    public ObservableField<String> moderatorsCount = new ObservableField<>("0");
    public ObservableInt noMediaVisibility = new ObservableInt(View.GONE);
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
    public ObservableInt showLoading = new ObservableInt(View.GONE);
    public ObservableInt textGravity = new ObservableInt(Gravity.LEFT);
    public ObservableInt showLeaveChannel = new ObservableInt();
    //Ui event
    public MutableLiveData<String> channelName = new MutableLiveData<>();
    public MutableLiveData<String> channelSecondsTitle = new MutableLiveData<>();
    public MutableLiveData<String> channelDescription = new MutableLiveData<>();
    public MutableLiveData<Integer> menuPopupVisibility = new MutableLiveData<>();
    public MutableLiveData<Long> goToShowAvatarPage = new MutableLiveData<>();
    public MutableLiveData<GoToShowMemberModel> goToShowMemberList = new MutableLiveData<>();
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToRoomListPage = new MutableLiveData<>();
    public MutableLiveData<String> showDialogCopyLink = new MutableLiveData<>();
    public MutableLiveData<GoToSharedMediaModel> goToSharedMediaPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLeaveChannel = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToChatRoom = new MutableLiveData<>();
    public MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();

    private ChannelChatRole role;
    public long roomId;
    private boolean isPrivate;
    private boolean isNotJoin;
    private RealmRoom mRoom;
    private FragmentChannelProfile fragment;

    private io.realm.RealmResults<RealmMember> admins;
    private io.realm.RealmResults<RealmMember> moderators;
    public static OnMenuClick onMenuClick;
    private boolean isNeedGetMemberList = true;
    private RealmChangeListener<RealmModel> changeListener;

    public FragmentChannelProfileViewModel(FragmentChannelProfile fragmentChannelProfile, long roomId, boolean isNotJoin) {
        this.fragment = fragmentChannelProfile;

        this.roomId = roomId;
        this.isNotJoin = isNotJoin;

        G.onChannelLeft = new OnChannelLeft() {
            @Override
            public void onChannelLeft(long roomId, long memberId) {
                G.handler.post(() -> showLoading.set(View.GONE));
                goToChatRoom.postValue(true);
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                showLoading.set(View.GONE);
                showErrorMessage.postValue(R.string.error);
            }

            @Override
            public void onTimeOut() {
                showLoading.set(View.GONE);
                showErrorMessage.postValue(R.string.error);
            }
        };

        mRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        });

        if (mRoom == null || mRoom.getChannelRoom() == null) {
            goBack.setValue(true);
            return;
        }
        /*initials = mRoom.getInitials();*/
        role = mRoom.getChannelRoom().getRole();
        isPrivate = mRoom.getChannelRoom().isPrivate();
        channelName.setValue(mRoom.getTitle());

        channelDescription.setValue(mRoom.getChannelRoom().getDescription());
        if (mRoom.getChannelRoom().getDescription() != null && !mRoom.getChannelRoom().getDescription().isEmpty()) {
            haveDescription.set(View.VISIBLE);
        } else {
            haveDescription.set(View.GONE);
        }

        isVerifiedChannel.set(mRoom.getChannelRoom().isVerified() ? View.VISIBLE : View.GONE);

        // show link for logic
        if (isPrivate) {
            channelLink.set(mRoom.getChannelRoom().getInviteLink());
            channelLinkTitle.set(R.string.channel_link);
            if (role == ChannelChatRole.OWNER) {
                isShowLink.set(View.VISIBLE);
            } else {
                isShowLink.set(View.GONE);
            }
        } else {
            channelLink.set(mRoom.getChannelRoom().getUsername());
            channelLinkTitle.set(R.string.st_username);
            isShowLink.set(View.VISIBLE);
        }

        isMuteNotification.set(mRoom.getMute());

        subscribersCount.set(mRoom.getChannelRoom().getParticipantsCountLabel());
        DbManager.getInstance().doRealmTask(realm -> {
            administratorsCount.set(String.valueOf(RealmMember.filterMember(realm, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString()).size()));
            moderatorsCount.set(String.valueOf(RealmMember.filterMember(realm, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString()).size()));

            admins = RealmMember.filterMember(realm, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
            moderators = RealmMember.filterMember(realm, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());

        });

        admins.addChangeListener((realmMembers, changeSet) -> administratorsCount.set(realmMembers.size() + ""));
        moderators.addChangeListener((realmMembers, changeSet) -> moderatorsCount.set(realmMembers.size() + ""));

        if (role == ChannelChatRole.ADMIN || role == ChannelChatRole.OWNER) {
            //Todo : fixed it
            channelSecondsTitle.setValue(mRoom.getChannelRoom().isPrivate() ? G.currentActivity.getString(R.string.private_channel) : G.currentActivity.getString(R.string.public_channel));
            showLeaveChannel.set(View.GONE);
        } else {
            channelSecondsTitle.setValue(String.format("%s %s", mRoom.getChannelRoom().getParticipantsCountLabel(), G.currentActivity.getString(R.string.subscribers_title)));
            showLeaveChannel.set(View.VISIBLE);
        }

        if (!G.isAppRtl) {
            textGravity.set(Gravity.LEFT);
        } else {
            textGravity.set(Gravity.RIGHT);
        }

        initRecycleView();

        FragmentShearedMedia.getCountOfSharedMedia(roomId);
    }

    public void onNotificationCheckChange() {
        isMuteNotification.set(!isMuteNotification.get());
        muteNotifListener.setValue(isMuteNotification.get());
    }

    public void onClickCircleImage() {
        if (DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst();
        }) != null) {
            goToShowAvatarPage.setValue(roomId);
        }
    }

    public void onClickChannelLink() {
        if (isPrivate) {
            showDialogCopyLink.setValue(mRoom.getChannelRoom().getInviteLink());
        } else {
            showDialogCopyLink.setValue(mRoom.getChannelRoom().getUsername());
        }
    }

    public void onSubscribersClick() {
        goToShowMemberList.setValue(new GoToShowMemberModel(roomId, role.toString(), AccountManager.getInstance().getCurrentUser().getId(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString(), isNeedGetMemberList));
        isNeedGetMemberList = false;
    }

    public void onAdministratorsClick() {
        goToShowMemberList.setValue(new GoToShowMemberModel(roomId, role.toString(), AccountManager.getInstance().getCurrentUser().getId(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString(), isNeedGetMemberList));
        isNeedGetMemberList = false;
    }

    public void onModeratorClick() {
        goToShowMemberList.setValue(new GoToShowMemberModel(roomId, role.toString(), AccountManager.getInstance().getCurrentUser().getId(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString(), isNeedGetMemberList));
        isNeedGetMemberList = false;
    }

    public void onClickGroupShearedMedia(int type) {
        goToSharedMediaPage.setValue(new GoToSharedMediaModel(roomId, type));
    }

    public void onLeaveChannelClick() {
        showDialogLeaveChannel.setValue(true);
    }

    public void onResume() {
        mRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        });
        if (mRoom != null) {
            if (changeListener == null) {
                changeListener = new RealmChangeListener<RealmModel>() {
                    @Override
                    public void onChange(@NotNull RealmModel element) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (((RealmRoom) element).isValid()) {
                                    String countText = ((RealmRoom) element).getSharedMediaCount();
                                    Log.wtf("group profile view model", "value: " + countText);

                                    channelName.postValue(mRoom.getTitle());
                                    channelDescription.postValue(mRoom.getChannelRoom().getDescription());

                                    role = mRoom.getChannelRoom().getRole();
                                    isPrivate = mRoom.getChannelRoom().isPrivate();

                                    // show link for logic
                                    if (isPrivate) {
                                        channelLink.set(mRoom.getChannelRoom().getInviteLink());
                                        channelLinkTitle.set(R.string.channel_link);
                                        if (role == ChannelChatRole.OWNER) {
                                            isShowLink.set(View.VISIBLE);
                                        } else {
                                            isShowLink.set(View.GONE);
                                        }
                                    } else {
                                        channelLink.set(mRoom.getChannelRoom().getUsername());
                                        channelLinkTitle.set(R.string.st_username);
                                        isShowLink.set(View.VISIBLE);
                                    }


                                    if (HelperCalander.isPersianUnicode) {
                                        countText = HelperCalander.convertToUnicodeFarsiNumber(countText);
                                    }
                                    if (countText == null || countText.length() == 0) {
                                        noMediaVisibility.set(View.GONE);
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
                                            noMediaVisibility.set(View.VISIBLE);
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
                                            noMediaVisibility.set(View.GONE);
                                        }
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
            noMediaVisibility.set(View.GONE);
            /*callbackGroupShearedMedia.set(context.getString(R.string.there_is_no_sheared_media));*/
        }
    }

    public void onStop() {
        if (mRoom != null) {
            mRoom.removeAllChangeListeners();
        }
        showLoading.set(View.GONE);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (admins != null)
            admins.removeAllChangeListeners();
        if (moderators != null)
            moderators.removeAllChangeListeners();
    }


    public void leaveChannel() {
        new RequestChannelLeft().channelLeft(roomId);
        showLoading.set(View.VISIBLE);
    }

    public void checkChannelIsEditable() {
        if (mRoom == null) return;
        role = mRoom.getChannelRoom().getRole();
    }

    private boolean isEditable() {
        return role == ChannelChatRole.ADMIN || role == ChannelChatRole.OWNER;
    }

    public boolean checkIsEditableAndReturnState() {
        checkChannelIsEditable();
        return isEditable();
    }

    //****** show admin or moderator list

    private void initRecycleView() {

        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };
    }


    private void setToAdmin(Long peerId) {
        new RequestChannelAddAdmin().channelAddAdmin(roomId, peerId);
    }

    //********** set roles

    private void setToModerator(Long peerId) {
        new RequestChannelAddModerator().channelAddModerator(roomId, peerId);
    }

    private class CreatePopUpMessage {

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

                /**
                 *  ----------- Admin ---------------
                 *  1- admin dose'nt access set another admin
                 *  2- admin can set moderator
                 *  3- can remove moderator
                 *  4- can kick moderator and Member
                 */

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
                            fragment.kickAdmin(info.peerId);
                            return true;
                        case R.id.menu_remove_moderator:
                            fragment.kickModerator(info.peerId);
                            return true;
                        case R.id.menu_kick:
                            fragment.kickMember(info.peerId);
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
    }

}