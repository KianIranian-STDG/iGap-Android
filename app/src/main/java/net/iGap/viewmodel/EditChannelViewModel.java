package net.iGap.viewmodel;

import android.view.View;
import android.view.WindowManager;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperCalander;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.observers.interfaces.OnChannelAvatarDelete;
import net.iGap.observers.interfaces.OnChannelEdit;
import net.iGap.observers.interfaces.OnChannelUpdateReactionStatus;
import net.iGap.observers.interfaces.OnChannelUpdateSignature;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelEdit;
import net.iGap.request.RequestChannelUpdateReactionStatus;
import net.iGap.request.RequestChannelUpdateSignature;

import java.util.ArrayList;

public class EditChannelViewModel extends BaseViewModel implements OnChannelAvatarDelete, OnChannelUpdateReactionStatus {

    public ObservableField<String> channelName = new ObservableField<>("");
    public ObservableField<String> channelDescription = new ObservableField<>("");
    public ObservableInt channelType = new ObservableInt(R.string.private_channel);
    public ObservableBoolean isSignedMessage = new ObservableBoolean(false);
    public ObservableBoolean isReactionMessage = new ObservableBoolean(false);
    public ObservableField<String> administratorsCount = new ObservableField<>("");
    public ObservableField<String> moderatorsCount = new ObservableField<>("");
    public ObservableField<String> subscribersCount = new ObservableField<>("");
    public ObservableInt showLayoutReactStatus = new ObservableInt(View.GONE);
    public ObservableInt isShowLoading = new ObservableInt(View.GONE);
    public ObservableInt leaveChannelText = new ObservableInt();
    public ObservableInt showUsername = new ObservableInt(View.GONE);
    public SingleLiveEvent<String> showChangeUsername = new SingleLiveEvent<>();
    public ObservableField<String> channelUsername = new ObservableField<>();
    //ui
    public MutableLiveData<Boolean> goToMembersPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToAdministratorPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToModeratorPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> initEmoji = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLeaveGroup = new MutableLiveData<>();
    public SingleLiveEvent<Boolean> showSelectImageDialog = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> showConvertChannelDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDeleteChannelDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToChatRoom = new MutableLiveData<>();
    public MutableLiveData<Boolean> onSignClickListener = new MutableLiveData<>();
    public MutableLiveData<Boolean> onReactionMessageClickListener = new MutableLiveData<>();
    public MutableLiveData<Boolean> closePageImediatly = new MutableLiveData<>();

    public MutableLiveData<Long> channelAvatarUpdatedLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> showUploadProgressLiveData = new MutableLiveData<>();

    private RealmChannelRoom realmChannelRoom;

    public long roomId;
    public ChannelChatRole role;
    public String inviteLink;
    public String linkUsername;
    private boolean isPrivate;
    /*private AttachFile attachFile;*/
    private String pathSaveImage;

    public EditChannelViewModel(long roomId) {
        this.roomId = roomId;

        G.onChannelAvatarDelete = this;
        /*G.onChannelAddMember = this;*/
        /*G.onChannelAddAdmin = this;*/
        /*G.onChannelKickAdmin = this;*/
        /*G.onChannelAddModerator = this;*/
        /*G.onChannelKickModerator = this;*/
        /*G.onChannelRevokeLink = this;*/

        FragmentShowAvatars.onComplete = (result, messageOne, MessageTow) -> {
            long mAvatarId = 0;
            if (messageOne != null && !messageOne.equals("")) {
                mAvatarId = Long.parseLong(messageOne);
            }

            final long finalMAvatarId = mAvatarId;
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    //todo: fixed it
                    /*avatarHandler.avatarDelete(new ParamWithAvatarType(imgCircleImageView, fragmentChannelProfileViewModel.roomId)
                            .avatarType(AvatarHandler.AvatarType.ROOM).turnOffCache().onAvatarChange(new OnAvatarChange() {
                                @Override
                                public void onChange(boolean fromCache) {
                                    imgCircleImageView.setPadding(0, 0, 0, 0);
                                }
                            }), finalMAvatarId);*/
                }
            });
        };

        RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        });

        //todo:fixed it
        if (realmRoom == null || realmRoom.getChannelRoom() == null) {
            goBack.setValue(true);
            return;
        }
        realmChannelRoom = realmRoom.getChannelRoom();
        /*initials = realmRoom.getInitials();*/
        role = realmChannelRoom.getRole();
        isPrivate = realmChannelRoom.isPrivate();
        channelType.set(isPrivate ? R.string.private_channel : R.string.public_channel);
        isSignedMessage.set(realmChannelRoom.isSignature());
        isReactionMessage.set(realmChannelRoom.isReactionStatus());
        channelName.set(realmRoom.getTitle());
        channelDescription.set(realmChannelRoom.getDescription());
        linkUsername = realmChannelRoom.getUsername();
        inviteLink = realmChannelRoom.getInviteLink();

        if (isPrivate) {
            channelUsername.set("");
            showUsername.set(View.GONE);
        } else {
            channelUsername.set(Config.IGAP_LINK_PREFIX + linkUsername);
            showUsername.set(View.VISIBLE);
        }

        /*isVerifiedChannel.setValue(realmChannelRoom.isVerified());*/
        /*if (isPrivate) {
            channelLink.setValue(realmChannelRoom.getInviteLink());
            channelLinkTitle.setValue(G.fragmentActivity.getResources().getString(R.string.channel_link));
        } else {
            channelLink.setValue(realmChannelRoom.getUsername());
            channelLinkTitle.setValue(G.fragmentActivity.getResources().getString(R.string.st_username));
        }*/
        /*isShowLink.setValue(!(isPrivate && ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR))));*/

        if (role == ChannelChatRole.OWNER) {
            showLayoutReactStatus.set(View.VISIBLE);
            G.onChannelUpdateReactionStatus = this;
        } else {
            showLayoutReactStatus.set(View.GONE);
            G.onChannelUpdateReactionStatus = null;
        }

        /*try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }*/

        String subCount = String.valueOf(realmChannelRoom.getParticipantsCountLabel());
        subscribersCount.set(G.isAppRtl ? HelperCalander.convertToUnicodeFarsiNumber(subCount) : subCount);
        DbManager.getInstance().doRealmTask(realm -> {
            String adminsCount = String.valueOf(RealmMember.filterMember(realm, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString()).size());
            administratorsCount.set(G.isAppRtl ? HelperCalander.convertToUnicodeFarsiNumber(adminsCount) : adminsCount);
        });

        if (role == ChannelChatRole.OWNER) {
            leaveChannelText.set(R.string.channel_delete);
        } else {
            leaveChannelText.set(R.string.channel_left);
        }

        /*if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            addMemberVisibility.set(View.GONE);
        }*/
        /*if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
            menuPopupVisibility.setValue(View.VISIBLE);
        } else {
            menuPopupVisibility.setValue(View.GONE);
        }*/

        G.onChannelUpdateSignature = new OnChannelUpdateSignature() {
            @Override
            public void onChannelUpdateSignatureResponse(final long roomId, final boolean signature) {
                // handle realm to response class
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (roomId == EditChannelViewModel.this.roomId) {
                            isShowLoading.set(View.GONE);
                            isSignedMessage.set(signature);
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(() -> {
                    if (isSignedMessage.get()) {
                        isSignedMessage.set(false);
                    } else {
                        isSignedMessage.set(true);
                    }
                });
            }
        };
        /*attachFile = new AttachFile(G.fragmentActivity);*/
    }

    public void usernameClicked() {
        showChangeUsername.setValue(linkUsername);
    }

    public void updateGroupRole() {
        if (realmChannelRoom == null) return;
        role = realmChannelRoom.getRole();
        if (role.toString().equals(ProtoGlobal.ChannelRoom.Role.MEMBER.toString()) ||
                role.toString().equals(ProtoGlobal.ChannelRoom.Role.MODERATOR.toString())) {
            closePageImediatly.setValue(true);
        }
    }

    public void chooseImage() {
        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
            showSelectImageDialog.setValue(true);
        } else {
            showSelectImageDialog.setValue(false);
        }
    }

    public void onEmojiClickListener() {
        if (initEmoji.getValue() != null) {
            initEmoji.setValue(!initEmoji.getValue());
        } else {
            initEmoji.setValue(false);
        }
    }

    public void onSignMessageClick() {
        onSignClickListener.postValue(true);
    }

    public void onChannelTypeClick() {
        if (role == ChannelChatRole.OWNER) {
            showConvertChannelDialog.setValue(isPrivate);
        }
    }

    public void onSingedMessageCheckedChange(boolean state) {
        if (state != isSignedMessage.get()) {
            if (state) {
                new RequestChannelUpdateSignature().channelUpdateSignature(roomId, true);
            } else {
                new RequestChannelUpdateSignature().channelUpdateSignature(roomId, false);
            }
            isShowLoading.set(View.VISIBLE);
        }
    }

    public void onReactionMessageClick() {
        onReactionMessageClickListener.postValue(true);
    }

    public void onReactionMessageCheckedChange(boolean state) {
        if (state != isReactionMessage.get()) {
            new RequestChannelUpdateReactionStatus().channelUpdateReactionStatus(roomId, state);
            isShowLoading.set(View.VISIBLE);
        }
    }

    public void onAdministratorClick() {
        goToAdministratorPage.setValue(true);
    }

    public void onModeratorClick() {
        goToModeratorPage.setValue(true);
    }

    public void onMemberClick() {
        goToMembersPage.setValue(true);
    }

    public void onRecentActionClick() {

    }

    public void onDeleteChannelClick() {
        showDeleteChannelDialog.setValue(role.equals(ChannelChatRole.OWNER));
    }

    public void setData(String channelName, String channelDescription) {
        isShowLoading.set(View.VISIBLE);
        new RequestChannelEdit().channelEdit(roomId, channelName, channelDescription, new OnChannelEdit() {
            @Override
            public void onChannelEdit(final long roomId, final String name, final String description) {
                G.handler.post(() -> {
                    isShowLoading.set(View.GONE);
                    EditChannelViewModel.this.channelName.set(name);
                    EditChannelViewModel.this.channelDescription.set(description);
                    /*SpannableStringBuilder spannableStringBuilder = HelperUrl.setUrlLink(description, true, false, null, true);
                    if (spannableStringBuilder != null) {
                        EditChannelViewModel.this.channelDescription.set(spannableStringBuilder);
                    } else {
                        EditChannelViewModel.this.channelDescription.set(new SpannableStringBuilder(""));
                    }*/
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    goBack.setValue(true);
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(() -> isShowLoading.set(View.GONE));
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> isShowLoading.set(View.GONE));
            }
        });
    }

    @Override
    public void onCreateFragment(BaseFragment fragment) {
        showUploadProgressLiveData.postValue(View.GONE);
    }

    @Override
    public void onChannelAvatarDelete(long roomId, long avatarId) {
        channelAvatarUpdatedLiveData.postValue(roomId);
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void OnChannelUpdateReactionStatusError() {
        G.handler.post(() -> isShowLoading.set(View.GONE));
    }

    @Override
    public void OnChannelUpdateReactionStatusResponse(long roomId, boolean status) {
        G.handler.post(() -> {
            if (roomId == EditChannelViewModel.this.roomId) {
                isShowLoading.set(View.GONE);

                isReactionMessage.set(status);
            }
        });
    }

    public void setPrivate(boolean aPrivate, String username) {
        isPrivate = aPrivate;
        showUsername.set(isPrivate ? View.GONE : View.VISIBLE);
        channelUsername.set(isPrivate ? username : Config.IGAP_LINK_PREFIX + username);
        channelType.set(isPrivate ? R.string.private_channel : R.string.public_channel);
    }

    private void closeActivity() {
        G.handler.post(() -> {
            isShowLoading.set(View.GONE);
            goToChatRoom.setValue(true);
        });
    }

    public MutableLiveData<Long> getChannelAvatarUpdatedLiveData() {
        return channelAvatarUpdatedLiveData;
    }

    public MutableLiveData<Integer> getShowUploadProgressLiveData() {
        return showUploadProgressLiveData;
    }
}
