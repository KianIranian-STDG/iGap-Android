package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.SpannableStringBuilder;
import android.view.WindowManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.BindingAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnGroupDelete;
import net.iGap.interfaces.OnGroupEdit;
import net.iGap.interfaces.OnGroupLeft;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.SUID;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmChatRoom;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmNotificationSetting;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestGroupAvatarAdd;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupEdit;
import net.iGap.request.RequestGroupLeft;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class EditGroupViewModel extends ViewModel {

    //update ui data
    public MutableLiveData<BindingAdapter.AvatarImage> avatarImage = new MutableLiveData<>();
    public ObservableInt chatHistoryForNewMemberStatus = new ObservableInt();
    public ObservableField<String> permissionCount = new ObservableField<>("");
    public ObservableField<String> administratorsCount = new ObservableField<>("");
    public ObservableField<String> moderatorsCount = new ObservableField<>("");
    public ObservableField<String> membersCount = new ObservableField<>("");
    public ObservableField<String> groupName = new ObservableField<>("");
    public ObservableField<String> groupDescription = new ObservableField<>("");
    public MutableLiveData<Boolean> showImageProgress = new MutableLiveData<>();
    //observed in view for go to another page
    public MutableLiveData<Boolean> goToMembersPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToAdministratorPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToModeratorPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToPermissionPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public MutableLiveData<Boolean> showSelectImageDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogChatHistory = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLeaveGroup = new MutableLiveData<>();
    public MutableLiveData<Boolean> initEmoji = new MutableLiveData<>();
    public MutableLiveData<Boolean> showLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToRoomListPage = new MutableLiveData<>();

    public GroupChatRole role;
    public long roomId;
    //TODO: add To repository. this code same in fragment group profile
    private Realm realmGroupProfile;
    private RealmGroupRoom realmGroupRoom;
    private RealmNotificationSetting realmNotificationSetting;
    private int realmNotification = 0;
    private String initials;
    RealmResults<RealmMember> adminMembers;
    RealmResults<RealmMember> moderatorMembers;


    public EditGroupViewModel(Long roomId) {

        realmGroupProfile = Realm.getDefaultInstance();
        this.roomId = roomId;
        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getGroupRoom() == null) {
            goBack.setValue(true);
            return;
        } else if (realmRoom.getGroupRoom() != null) {
            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
            if (realmGroupRoom != null) {
                if (realmGroupRoom.getRealmNotificationSetting() == null) {

                    setRealm(getRealm(), realmGroupRoom, null, null);
                } else {
                    realmNotificationSetting = realmGroupRoom.getRealmNotificationSetting();
                }
                getRealm();
                realmNotification = realmNotificationSetting.getNotification();
            }
        }


        realmGroupRoom = realmRoom.getGroupRoom();
        groupName.set(realmRoom.getTitle());
        initials = realmRoom.getInitials();
        role = realmGroupRoom.getRole();
        //group link
        /*realmGroupRoom.getInvite_link();*/
        //group linkUsername
        /*realmGroupRoom.getUsername();*/
        //group is private
        /*realmGroupRoom.isPrivate();*/
        //group participantsCountLabel
        String c = realmGroupRoom.getParticipantsCountLabel();
        if (HelperCalander.isPersianUnicode) {
            membersCount.set(HelperCalander.convertToUnicodeFarsiNumber(c));
        } else {
            membersCount.set(c);
        }
        groupDescription.set(realmGroupRoom.getDescription());
        /*if (role == GroupChatRole.OWNER) {
            callBackDeleteLeaveGroup.set(G.fragmentActivity.getResources().getString(R.string.delete_group));
        } else {
            callBackDeleteLeaveGroup.set(G.fragmentActivity.getResources().getString(R.string.left_group));
        }*/

        //ToDo: add this code to repository
        adminMembers = RealmMember.filterMember(getRealm(), roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
        moderatorMembers = RealmMember.filterMember(getRealm(), roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
        administratorsCount.set(String.valueOf(adminMembers.size()));
        moderatorsCount.set(String.valueOf(moderatorMembers.size()));

        adminMembers.addChangeListener((realmMembers, changeSet) -> administratorsCount.set(String.valueOf(realmMembers.size())));
        moderatorMembers.addChangeListener((realmMembers, changeSet) -> moderatorsCount.set(String.valueOf(realmMembers.size())));

        int t;
        switch (realmGroupRoom.getStartFrom()) {
            case 0:
                t = R.string.from_beginning;
                break;
            case -1:
                t = R.string.from_Now;
                break;
            case 50:
                t = R.string.last_50_Messages;
                break;
            default:
                t = R.string.customs;
                break;

        }
        chatHistoryForNewMemberStatus.set(t);
    }

    //TODO: move this code to repository
    private Realm getRealm() {
        if (realmGroupProfile == null || realmGroupProfile.isClosed()) {
            realmGroupProfile = Realm.getDefaultInstance();
        }
        return realmGroupProfile;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        realmGroupProfile.close();
    }

    //TODO: move this code to repository
    private void setRealm(Realm realm, final RealmGroupRoom realmGroupRoom, final RealmChannelRoom realmChannelRoom, final RealmChatRoom realmChatRoom) {
        realm.executeTransaction(realm1 -> realmNotificationSetting = RealmNotificationSetting.put(realm1, realmChatRoom, realmGroupRoom, realmChannelRoom));
    }

    public void chooseImage() {
        showSelectImageDialog.setValue(true);
    }

    public void onEmojiClickListener() {
        if (initEmoji.getValue() != null) {
            initEmoji.setValue(!initEmoji.getValue());
        } else {
            initEmoji.setValue(false);
        }
    }

    public void leaveGroupOnClick() {
        showDialogLeaveGroup.setValue(true);
    }

    public void onPermissionClick() {
        goToPermissionPage.setValue(true);
    }

    public void onAdministratorClick() {
        goToAdministratorPage.setValue(true);
    }

    public void onModeratorClick() {
        goToModeratorPage.setValue(true);
    }

    public void onChatHistoryStatusClick() {
        showDialogChatHistory.setValue(true);
    }

    public void leaveGroup() {
        //ToDo:move this code to repository
        showLoading.setValue(true);
        G.onGroupLeft = new OnGroupLeft() {
            @Override
            public void onGroupLeft(final long roomId, long memberId) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.setValue(false);
                    goToRoomListPage.setValue(true);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.setValue(false);
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.setValue(false);
                });
            }
        };

        G.onGroupDelete = new OnGroupDelete() {
            @Override
            public void onGroupDelete(final long roomId) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.setValue(false);
                    goToRoomListPage.setValue(true);
                });
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.setValue(false);
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.setValue(false);
                });
            }
        };

        if (role == GroupChatRole.OWNER) {
            new RequestGroupDelete().groupDelete(roomId);
        } else {
            new RequestGroupLeft().groupLeft(roomId);
        }
    }

    public void onMemberClick() {
        goToMembersPage.setValue(true);
    }

    public void setData(String newGroupName, String newGroupDescription) {
        //ToDo:Add this code to repository
        new RequestGroupEdit().groupEdit(roomId, newGroupName, newGroupDescription,new OnGroupEdit() {
            @Override
            public void onGroupEdit(long roomId, String name, String description) {
                G.handler.post(() -> {
                    showLoading.setValue(false);
                    groupName.set(name);
                    groupDescription.set(description);
                    /*SpannableStringBuilder ds = HelperUrl.setUrlLink(description, true, false, null, true);
                    if (ds != null) {
                        groupDescription.set(ds.toString());
                    } else {
                        groupDescription.set("");
                    }*/
                    goBack.setValue(true);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(()-> showLoading.setValue(false));
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> {
                    //ToDo:move this code to view
                    showLoading.setValue(false);
                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);
                });
            }
        });
    }

    public void setEditedImage(String path) {
        long avatarId = SUID.id().get();
        long lastUploadedAvatarId = avatarId + 1L;
        showImageProgress.setValue(true);
        //ToDo: add this code to repository
        HelperUploadFile.startUploadTaskAvatar(path, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
            @Override
            public void OnProgress(int progress, FileUploadStructure struct) {
                if (progress < 100) {
                    /*binding.loading.setProgress(progress);*/
                } else {
                    new RequestGroupAvatarAdd().groupAvatarAdd(roomId, struct.token);
                }
            }

            @Override
            public void OnError() {
                showImageProgress.setValue(false);
            }
        });
    }

    public void setChatHistoryStatus(int status) {
        //ToDo: move this code to repository
        new Thread(() -> {
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(realm1 -> {
                    //ToDo: improve this code
                    realm1.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst().getGroupRoom().setStartFrom(status);
                });
            }
        }).start();

        int t;
        switch (status) {
            case 0:
                t = R.string.from_beginning;
                break;
            case -1:
                t = R.string.from_Now;
                break;
            case 50:
                t = R.string.last_50_Messages;
                break;
            default:
                t = R.string.customs;
                break;

        }
        chatHistoryForNewMemberStatus.set(t);
    }

}
