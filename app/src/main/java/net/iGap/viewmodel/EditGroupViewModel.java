package net.iGap.viewmodel;

import android.view.View;
import android.view.WindowManager;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.BindingAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.module.SUID;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.upload.Uploader;
import net.iGap.observers.interfaces.OnGroupAvatarResponse;
import net.iGap.observers.interfaces.OnGroupDelete;
import net.iGap.observers.interfaces.OnGroupEdit;
import net.iGap.observers.interfaces.OnGroupLeft;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestGroupAvatarAdd;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupEdit;
import net.iGap.request.RequestGroupLeft;

import java.io.File;
import java.util.ArrayList;

import io.realm.RealmResults;

public class EditGroupViewModel extends BaseViewModel implements OnGroupAvatarResponse {

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
    public SingleLiveEvent<Boolean> showSelectImageDialog = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> showDialogChatHistory = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLeaveGroup = new MutableLiveData<>();
    public MutableLiveData<Boolean> initEmoji = new MutableLiveData<>();
    public MutableLiveData<Boolean> showLoading = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToRoomListPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> closePageImediatly = new MutableLiveData<>();

    public MutableLiveData<Long> onGroupAvatarUpdated = new MutableLiveData<>();
    private MutableLiveData<Integer> showUploadProgressLiveData = new MutableLiveData<>();

    public GroupChatRole role;
    public long roomId;
    //TODO: add To repository. this code same in fragment group profile
    private RealmGroupRoom realmGroupRoom;
    private String initials;
    RealmResults<RealmMember> adminMembers;
    RealmResults<RealmMember> moderatorMembers;


    public EditGroupViewModel(Long roomId) {

        this.roomId = roomId;
        RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        });

        if (realmRoom == null || realmRoom.getGroupRoom() == null) {
            goBack.setValue(true);
            return;
        }

        realmGroupRoom = realmRoom.getGroupRoom();
        realmGroupRoom.addChangeListener(realmModel -> checkMemberCount());
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
        checkMemberCount();
        groupDescription.set(realmGroupRoom.getDescription());
        /*if (role == GroupChatRole.OWNER) {
            callBackDeleteLeaveGroup.set(G.fragmentActivity.getResources().getString(R.string.delete_group));
        } else {
            callBackDeleteLeaveGroup.set(G.fragmentActivity.getResources().getString(R.string.left_group));
        }*/

        //ToDo: add this code to repository
        DbManager.getInstance().doRealmTask(realm -> {
            adminMembers = RealmMember.filterMember(realm, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString());
            moderatorMembers = RealmMember.filterMember(realm, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString());

        });
        administratorsCount.set(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(adminMembers.size())));
        moderatorsCount.set(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(moderatorMembers.size())));

        adminMembers.addChangeListener((realmMembers, changeSet) -> administratorsCount.set(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(realmMembers.size()))));
        moderatorMembers.addChangeListener((realmMembers, changeSet) -> moderatorsCount.set(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(realmMembers.size()))));

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

    private void checkMemberCount() {
        String c = realmGroupRoom.getParticipantsCountLabel();
        if (HelperCalander.isPersianUnicode) {
            membersCount.set(HelperCalander.convertToUnicodeFarsiNumber(c));
        } else {
            membersCount.set(c);
        }
    }

    @Override
    public void onCreateFragment(BaseFragment fragment) {
        showUploadProgressLiveData.postValue(View.GONE);
        G.onGroupAvatarResponse = this;
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

    public void updateGroupRole() {
        if (realmGroupRoom == null) return;
        role = realmGroupRoom.getRole();
        if (role.toString().equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString()) ||
                role.toString().equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
            closePageImediatly.setValue(true);
        }
    }

    public void onMemberClick() {
        goToMembersPage.setValue(true);
    }

    public void setData(String newGroupName, String newGroupDescription) {
        //ToDo:Add this code to repository
        new RequestGroupEdit().groupEdit(roomId, newGroupName, newGroupDescription, new OnGroupEdit() {
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
                G.handler.post(() -> showLoading.setValue(false));
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

    public void setChatHistoryStatus(int status) {
        //ToDo: move this code to repository
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm1 -> {
                realm1.where(RealmRoom.class).equalTo("id", roomId).findFirst().getGroupRoom().setStartFrom(status);
            });
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

    public void uploadAvatar(String path) {
        long avatarId = SUID.id().get();
        long lastUploadedAvatarId = avatarId + 1L;
        Uploader.getInstance().upload(lastUploadedAvatarId + "", new File(path), ProtoGlobal.RoomMessageType.IMAGE, new OnUploadListener() {
            @Override
            public void onProgress(String id, int progress) {
                showUploadProgressLiveData.postValue(View.VISIBLE);
            }

            @Override
            public void onFinish(String id, String token) {
                new RequestGroupAvatarAdd().groupAvatarAdd(roomId, token);
            }

            @Override
            public void onError(String id) {
                showUploadProgressLiveData.postValue(View.GONE);
            }
        });
    }

    @Override
    public void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar) {
        onGroupAvatarUpdated.postValue(roomId);
        showUploadProgressLiveData.postValue(View.GONE);
    }

    @Override
    public void onAvatarAddError() {
        showUploadProgressLiveData.postValue(View.GONE);
    }

    public MutableLiveData<Integer> getShowUploadProgressLiveData() {
        return showUploadProgressLiveData;
    }

    public MutableLiveData<Long> getOnGroupAvatarUpdated() {
        return onGroupAvatarUpdated;
    }
}
