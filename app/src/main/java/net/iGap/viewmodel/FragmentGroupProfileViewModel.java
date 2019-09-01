package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentGroupProfile;
import net.iGap.fragments.FragmentShearedMedia;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnGroupAddMember;
import net.iGap.interfaces.OnGroupDelete;
import net.iGap.interfaces.OnGroupKickMember;
import net.iGap.interfaces.OnGroupLeft;
import net.iGap.interfaces.OnGroupRemoveUsername;
import net.iGap.interfaces.OnGroupRevokeLink;
import net.iGap.interfaces.OnMenuClick;
import net.iGap.model.GoToSharedMediaModel;
import net.iGap.module.Contacts;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmChatRoom;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmNotificationSetting;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestGroupAddAdmin;
import net.iGap.request.RequestGroupAddModerator;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupLeft;
import net.iGap.request.RequestGroupRemoveUsername;
import net.iGap.request.RequestGroupRevokeLink;
import net.iGap.request.RequestUserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class FragmentGroupProfileViewModel extends ViewModel {

    public ObservableInt haveDescription = new ObservableInt(View.VISIBLE);
    public ObservableBoolean isUnMuteNotification = new ObservableBoolean(true);
    public ObservableInt noMediaSharedVisibility = new ObservableInt(View.GONE);
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
    public ObservableInt addMemberVisibility = new ObservableInt(View.GONE);
    public ObservableField<String> inviteLink = new ObservableField<>("");
    public ObservableInt inviteLinkTitle = new ObservableInt(R.string.group_link);
    public ObservableInt showLoading = new ObservableInt(View.GONE);
    public ObservableInt showLink = new ObservableInt(View.GONE);
    public ObservableInt showLeaveGroup = new ObservableInt(View.GONE);

    //ui Observable
    public MutableLiveData<String> groupName = new MutableLiveData<>();
    public MutableLiveData<String> groupNumber = new MutableLiveData<>();
    public MutableLiveData<Long> goToShowAvatarPage = new MutableLiveData<>();
    public MutableLiveData<List<Integer>> showMenu = new MutableLiveData<>();
    public MutableLiveData<GoToSharedMediaModel> goToShearedMediaPage = new MutableLiveData<>();
    public MutableLiveData<String> goToShowMemberPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogConvertToPublic = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogConvertToPrivate = new MutableLiveData<>();
    public MutableLiveData<Integer> showRequestError = new MutableLiveData<>();
    public MutableLiveData<List<StructContactInfo>> goToShowCustomListPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public MutableLiveData<String> groupDescription = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToRoomListPage = new MutableLiveData<>();
    public MutableLiveData<Long> goToCustomNotificationPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> showMoreMenu = new MutableLiveData<>();
    public MutableLiveData<Boolean> showEditButton = new MutableLiveData<>();
    public MutableLiveData<String> showDialogCopyLink = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLeaveGroup = new MutableLiveData<>();

    private RealmRoom realmRoom;
    private boolean isNotJoin;
    public GroupChatRole role;
    public boolean isPrivate;

    public static OnMenuClick onMenuClick;
    public long roomId;
    private String initials;
    public String linkUsername;
    private String color;
    private long noLastMessage;
    private String pathSaveImage;
    public boolean isPopup = false;
    private long startMessageId = 0;
    public boolean isNeedgetContactlist = true;
    private Realm realmGroupProfile;
    private FragmentGroupProfile fragment;
    private String memberCount;

    public FragmentGroupProfileViewModel() {
        realmGroupProfile = Realm.getDefaultInstance();
    }

    public void init(FragmentGroupProfile fragmentGroupProfile, long roomId, boolean isNotJoin) {

        this.fragment = fragmentGroupProfile;
        this.roomId = roomId;
        this.isNotJoin = isNotJoin;

        //group info
        realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getGroupRoom() == null) {
            goBack.setValue(true);
            return;
        }

        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();

        groupName.setValue(realmRoom.getTitle());
        groupNumber.setValue(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(realmGroupRoom.getParticipantsCountLabel()) : realmGroupRoom.getParticipantsCountLabel());
        role = realmGroupRoom.getRole();
        isPrivate = realmGroupRoom.isPrivate();

        initials = realmRoom.getInitials();
        color = realmRoom.getColor();

        isUnMuteNotification.set(realmRoom.getMute());

        inviteLink.set(realmGroupRoom.getInvite_link());
        linkUsername = realmGroupRoom.getUsername();

        if (realmGroupRoom.getDescription() != null && !realmGroupRoom.getDescription().isEmpty()) {
            haveDescription.set(View.VISIBLE);
            groupDescription.setValue(realmGroupRoom.getDescription());
        } else {
            haveDescription.set(View.GONE);
        }

        setTextGroupLik();

        //OWNER,ADMIN,MODERATOR,MEMBER can add member to group
        addMemberVisibility.set(View.VISIBLE);
        showEditButton.setValue(role == GroupChatRole.ADMIN || role == GroupChatRole.OWNER);
        showLeaveGroup.set(role != GroupChatRole.ADMIN && role != GroupChatRole.OWNER ? View.VISIBLE : View.GONE);

        showMoreMenu.setValue(!isNotJoin);

        FragmentShearedMedia.getCountOfSharedMedia(roomId);

        initRecycleView();
        onGroupAddMemberCallback();
        onGroupKickMemberCallback();

        if (realmRoom != null) {
            realmRoom.addChangeListener((realmModel, changeSet) -> {
                if (changeSet != null) {
                    if (changeSet.isDeleted()) {
                        goToRoomListPage.setValue(true);
                    } else if (((RealmRoom) realmModel).isValid()) {
                        isUnMuteNotification.set(realmRoom.getMute());
                        String countText = ((RealmRoom) realmModel).getSharedMediaCount();

                        groupName.postValue(realmRoom.getTitle());
                        groupDescription.postValue(realmRoom.getGroupRoom().getDescription());

                        if (HelperCalander.isPersianUnicode) {
                            countText = HelperCalander.convertToUnicodeFarsiNumber(countText);
                        }
                        if (countText == null || countText.length() == 0) {
                            noMediaSharedVisibility.set(View.GONE);
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
                                noMediaSharedVisibility.set(View.VISIBLE);
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
                                noMediaSharedVisibility.set(View.GONE);
                            }
                        }
                    }
                }
            });
        } else {
            noMediaSharedVisibility.set(View.GONE);
        }
    }


    public void onClickRippleMenu() {
        List<Integer> items = new ArrayList<>();
        items.add(R.string.clear_history);
        if (role == GroupChatRole.OWNER) {
            if (isPrivate) {
                items.add(R.string.group_title_convert_to_public);
            } else {
                items.add(R.string.group_title_convert_to_private);
            }
        }
        showMenu.setValue(items);
    }

    public void convertMenuClick() {
        isPopup = true;
        if (isPrivate) {
            showDialogConvertToPublic.setValue(true);
        } else {
            showDialogConvertToPrivate.setValue(true);
        }
    }

    public void onClickRippleGroupAvatar() {
        if (getRealm().where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
            goToShowAvatarPage.setValue(roomId);
        }
    }

    public void onNotificationCheckChange() {
        new RequestClientMuteRoom().muteRoom(roomId, !isUnMuteNotification.get());
    }

    public void onInviteLinkClick() {
        isPopup = false;
        showDialogCopyLink.setValue(inviteLink.get());
    }

    public void onShowMemberClick() {
        goToShowMemberPage.setValue(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString());
        isNeedgetContactlist = false;
    }

    public void onCustomNotificationClick() {
        goToCustomNotificationPage.setValue(roomId);
    }

    public void sendRequestRemoveGroupUsername() {
        if (G.userLogin) {
            showProgressBar();
            new RequestGroupRemoveUsername().groupRemoveUsername(roomId, new OnGroupRemoveUsername() {
                @Override
                public void onGroupRemoveUsername(final long roomId) {
                    isPrivate = true;
                    if (inviteLink.get() == null || inviteLink.get().isEmpty() || inviteLink.equals("https://")) {
                        new RequestGroupRevokeLink().groupRevokeLink(roomId, new OnGroupRevokeLink() {
                            @Override
                            public void onGroupRevokeLink(long roomId, String inviteLink, String inviteToken) {
                                hideProgressBar();
                                G.handler.post(() -> FragmentGroupProfileViewModel.this.inviteLink.set(inviteLink));
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                hideProgressBar();
                            }

                            @Override
                            public void onTimeOut() {
                                hideProgressBar();
                                G.handler.post(() -> showRequestError.setValue(R.string.time_out));
                            }
                        });
                    } else {
                        hideProgressBar();
                        G.handler.post(() -> setTextGroupLik());
                    }
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    hideProgressBar();
                    G.handler.post(() -> {
                        if (majorCode == 5) {
                            showRequestError.setValue(R.string.wallet_error_server);
                        } else {
                            showRequestError.setValue(R.string.server_error);
                        }
                    });

                }
            });
        } else {
            showRequestError.setValue(R.string.wallet_error_server);
        }
    }

    //type: 1=image 2=video 3=audio 4=voice 5=gif 6=file 7=link
    public void onClickGroupShearedMedia(int type) {
        goToShearedMediaPage.setValue(new GoToSharedMediaModel(roomId, type));
    }

    public void addNewMember() {
        List<StructContactInfo> userList = Contacts.retrieve(null);
        RealmList<RealmMember> memberList = RealmMember.getMembers(getRealm(), roomId);

        //ToDo: change algorithm order is n2
        for (int i = 0; i < memberList.size(); i++) {
            for (int j = 0; j < userList.size(); j++) {
                if (userList.get(j).peerId == memberList.get(i).getPeerId()) {
                    userList.remove(j);
                    break;
                }
            }
        }

        goToShowCustomListPage.setValue(userList);
    }

    public void onLeaveGroupClick() {
        showDialogLeaveGroup.setValue(true);
    }

    private Realm getRealm() {
        if (realmGroupProfile == null || realmGroupProfile.isClosed()) {
            realmGroupProfile = Realm.getDefaultInstance();
        }
        return realmGroupProfile;
    }

    public void setTextGroupLik() {
        if (isPrivate) {
            inviteLinkTitle.set(R.string.group_link);
            if (role == GroupChatRole.OWNER) {
                showLink.set(View.VISIBLE);
            } else {
                showLink.set(View.GONE);
            }
        } else {
            showLink.set(View.VISIBLE);
            inviteLink.set(Config.IGAP_LINK_PREFIX + linkUsername);
            inviteLinkTitle.set(R.string.st_username);
        }
    }

    @Override
    protected void onCleared() {
        if (realmRoom != null) {
            realmRoom.removeAllChangeListeners();
        }

        realmGroupProfile.close();
        super.onCleared();
    }

    public void leaveGroup() {
        //ToDo:move this code to repository
        showLoading.set(View.VISIBLE);
        G.onGroupLeft = new OnGroupLeft() {
            @Override
            public void onGroupLeft(final long roomId, long memberId) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.set(View.GONE);
                    goToRoomListPage.setValue(true);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.set(View.GONE);
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.set(View.GONE);
                });
            }
        };

        new RequestGroupLeft().groupLeft(roomId);
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                showLoading.set(View.VISIBLE);
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                showLoading.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void onGroupAddMemberCallback() {
        G.onGroupAddMember = new OnGroupAddMember() {
            @Override
            public void onGroupAddMember(final Long roomIdUser, final Long userId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setMemberCount(roomIdUser);
                        RealmRegisteredInfo realmRegistered = RealmRegisteredInfo.getRegistrationInfo(getRealm(), userId);

                        if (realmRegistered == null) {
                            if (roomIdUser == roomId) {
                                new RequestUserInfo().userInfo(userId, roomId + "");
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };
    }

    private void onGroupKickMemberCallback() {
        G.onGroupKickMember = new OnGroupKickMember() {
            @Override
            public void onGroupKickMember(final long roomId, final long memberId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setMemberCount(roomId);
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
    }

    private void setMemberCount(final long roomId) {
        memberCount = RealmRoom.getMemberCount(getRealm(), roomId);
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (HelperCalander.isPersianUnicode) {
                    groupNumber.setValue(HelperCalander.convertToUnicodeFarsiNumber(memberCount));
                } else {
                    groupNumber.setValue(memberCount);
                }
            }
        });
    }

    private void initRecycleView() {

        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };
    }

    class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(G.fragmentActivity, view, Gravity.TOP);
            popup.getMenuInflater().inflate(R.menu.menu_item_group_profile, popup.getMenu());

            if (role == GroupChatRole.OWNER) {

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
            } else if (role == GroupChatRole.ADMIN) {

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
            } else if (role == GroupChatRole.MODERATOR) {

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
                            ((FragmentGroupProfile) fragment).kickAdmin(info.peerId);
                            return true;
                        case R.id.menu_remove_moderator:
                            ((FragmentGroupProfile) fragment).kickModerator(info.peerId);
                            return true;
                        case R.id.menu_kick:
                            ((FragmentGroupProfile) fragment).kickMember(info.peerId);
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

        private void setToAdmin(Long peerId) {
            new RequestGroupAddAdmin().groupAddAdmin(roomId, peerId);
        }

        private void setToModerator(Long peerId) {
            new RequestGroupAddModerator().groupAddModerator(roomId, peerId);
        }
    }

}