package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.WindowManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.BindingAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnAvatarGet;
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

import io.realm.Realm;
import io.realm.RealmResults;

import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class EditGroupViewModel extends ViewModel {

    //update ui data
    public MutableLiveData<BindingAdapter.AvatarImage> avatarImage = new MutableLiveData<>();
    public MutableLiveData<Integer> chatHistoryForNewMemberStatus = new MutableLiveData<>();
    public MutableLiveData<String> permissionCount = new MutableLiveData<>();
    public MutableLiveData<String> administratorsCount = new MutableLiveData<>();
    public MutableLiveData<String> moderatorsCount = new MutableLiveData<>();
    public MutableLiveData<String> membersCount = new MutableLiveData<>();
    public MutableLiveData<String> groupName = new MutableLiveData<>();
    public MutableLiveData<String> groupDescription = new MutableLiveData<>();
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

    public GroupChatRole role;
    public long roomId;
    //TODO: add To repository. this code same in fragment group profile
    private Realm realmGroupProfile;
    private RealmGroupRoom realmGroupRoom;
    private RealmNotificationSetting realmNotificationSetting;
    private int realmNotification = 0;
    private String initials;


    public EditGroupViewModel(Long roomId) {
        this.roomId = roomId;
        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getGroupRoom() == null) {
            goBack.setValue(true);
            return;
        } else if (realmRoom.getGroupRoom() != null) {
            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
            if (realmGroupRoom != null) {
                if (realmGroupRoom.getRealmNotificationSetting() == null) {
                    setRealm(Realm.getDefaultInstance(), realmGroupRoom, null, null);
                } else {
                    realmNotificationSetting = realmGroupRoom.getRealmNotificationSetting();
                }
                getRealm();
                realmNotification = realmNotificationSetting.getNotification();
            }
        }


        realmGroupRoom = realmRoom.getGroupRoom();
        groupName.setValue(realmRoom.getTitle());
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
            membersCount.setValue(HelperCalander.convertToUnicodeFarsiNumber(c));
        } else {
            membersCount.setValue(c);
        }
        groupDescription.setValue(realmGroupRoom.getDescription());
        /*if (role == GroupChatRole.OWNER) {
            callBackDeleteLeaveGroup.set(G.fragmentActivity.getResources().getString(R.string.delete_group));
        } else {
            callBackDeleteLeaveGroup.set(G.fragmentActivity.getResources().getString(R.string.left_group));
        }*/
        //ToDo: move To repository
        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                G.handler.post(() -> {
                    if (roomId != ownerId)
                        return;
                    showImageProgress.setValue(false);
                    avatarImage.setValue(new BindingAdapter.AvatarImage(avatarPath, false, null));
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color, final long ownerId) {
                G.handler.post(() -> {
                    if (roomId != ownerId)
                        return;
                    showImageProgress.setValue(false);
                    avatarImage.setValue(new BindingAdapter.AvatarImage(initials, true, color));
                });
            }
        });

        //ToDo: add this code to repository
        RealmResults<RealmMember> realmMembers = RealmMember.filterRole(roomId, GROUP, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
        RealmResults<RealmMember> moderatorMembers = RealmMember.filterRole(roomId, GROUP, ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());
        administratorsCount.setValue(String.valueOf(realmMembers.size()));
        moderatorsCount.setValue(String.valueOf(moderatorMembers.size()));
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
        chatHistoryForNewMemberStatus.setValue(t);
        initEmoji.setValue(false);
    }

    //TODO: move this code to repository
    private Realm getRealm() {
        if (realmGroupProfile == null || realmGroupProfile.isClosed()) {
            realmGroupProfile = Realm.getDefaultInstance();
        }
        return realmGroupProfile;
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

    public void leaveGroup(){
        //ToDo:move this code to repository
        G.onGroupLeft = new OnGroupLeft() {
            @Override
            public void onGroupLeft(final long roomId, long memberId) {
                G.handler.post(() -> {
                    if (FragmentChat.finishActivity != null) {
                        FragmentChat.finishActivity.finishActivity();
                    }
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.setValue(false);
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
                    //G.fragmentActivity.finish();
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    showLoading.setValue(false);
                    if (FragmentChat.finishActivity != null) {
                        FragmentChat.finishActivity.finishActivity();
                    }
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
        G.onGroupEdit = new OnGroupEdit() {
            @Override
            public void onGroupEdit(long roomId, String name, String description) {
                /*hideProgressBar();*/
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        groupName.setValue(name);
                        SpannableStringBuilder ds = HelperUrl.setUrlLink(description, true, false, null, true);
                        if (ds != null) {
                            groupDescription.setValue(ds.toString());
                        } else {
                            groupDescription.setValue("");
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //ToDo:move this code to view
                        HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.time_out), false);
                    }
                });
            }
        };
        new RequestGroupEdit().groupEdit(roomId, newGroupName, newGroupDescription);
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
        realmGroupProfile.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NotNull Realm realm) {
                //ToDo: improve this code
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst().getGroupRoom().setStartFrom(status);
            }
        });
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
        chatHistoryForNewMemberStatus.setValue(t);
    }

}
