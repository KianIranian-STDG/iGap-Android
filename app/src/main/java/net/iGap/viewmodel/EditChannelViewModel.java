package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;
import android.view.WindowManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.helper.HelperUploadFile;
import net.iGap.interfaces.OnChannelAvatarAdd;
import net.iGap.interfaces.OnChannelAvatarDelete;
import net.iGap.interfaces.OnChannelDelete;
import net.iGap.interfaces.OnChannelEdit;
import net.iGap.interfaces.OnChannelUpdateReactionStatus;
import net.iGap.interfaces.OnChannelUpdateSignature;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.SUID;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChannelAvatarAdd;
import net.iGap.request.RequestChannelEdit;
import net.iGap.request.RequestChannelUpdateReactionStatus;
import net.iGap.request.RequestChannelUpdateSignature;

import java.util.ArrayList;

import io.realm.Realm;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;

public class EditChannelViewModel extends ViewModel implements OnChannelAvatarAdd, OnChannelAvatarDelete, OnChannelUpdateReactionStatus, OnChannelDelete {

    public ObservableField<String> channelName = new ObservableField<>("");
    public ObservableField<String> channelDescription = new ObservableField<>("");
    public ObservableInt channelType = new ObservableInt(R.string.private_channel);
    public ObservableBoolean isSignedMessage = new ObservableBoolean(false);
    public ObservableBoolean isReactionMessage = new ObservableBoolean(false);
    public ObservableField<String> administratorsCount = new ObservableField<>("");
    public ObservableField<String> moderatorsCount = new ObservableField<>("");
    public ObservableField<String> subscribersCount = new ObservableField<>("");
    public ObservableInt showLayoutReactStatus = new ObservableInt(View.GONE);
    public ObservableBoolean canChangeChannelName = new ObservableBoolean(false);
    public ObservableInt isShowLoading = new ObservableInt(View.GONE);
    public ObservableInt leaveChannelText = new ObservableInt();
    //ui
    public MutableLiveData<Boolean> goToMembersPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToAdministratorPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToModeratorPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> initEmoji = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDialogLeaveGroup = new MutableLiveData<>();
    public MutableLiveData<Boolean> showSelectImageDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showConvertChannelDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> showDeleteChannelDialog = new MutableLiveData<>();
    public MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    public MutableLiveData<Boolean> goToChatRoom = new MutableLiveData<>();
    public MutableLiveData<Boolean> onSignClickListener = new MutableLiveData<>();
    public MutableLiveData<Boolean> onReactionMessageClickListener = new MutableLiveData<>();

    public long roomId;
    public ChannelChatRole role;
    public String inviteLink;
    public String linkUsername;
    private boolean isPrivate;
    private Realm realmChannelProfile;
    /*private AttachFile attachFile;*/
    private String pathSaveImage;

    public EditChannelViewModel(long roomId) {
        this.roomId = roomId;

        G.onChannelAvatarAdd = this;
        G.onChannelAvatarDelete = this;
        /*G.onChannelAddMember = this;*/
        /*G.onChannelAddAdmin = this;*/
        /*G.onChannelKickAdmin = this;*/
        /*G.onChannelAddModerator = this;*/
        /*G.onChannelKickModerator = this;*/
        G.onChannelDelete = this;
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
        FragmentEditImage.completeEditImage = (path, message, textImageList) -> {
            pathSaveImage = null;
            pathSaveImage = path;
            long avatarId = SUID.id().get();
            long lastUploadedAvatarId = avatarId + 1L;

            isShowLoading.set(View.VISIBLE);
            HelperUploadFile.startUploadTaskAvatar(pathSaveImage, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                @Override
                public void OnProgress(int progress, FileUploadStructure struct) {
                    if (progress < 100) {
                        /*prgWait.setProgress(progress);*/
                    } else {
                        new RequestChannelAvatarAdd().channelAvatarAdd(roomId, struct.token);
                    }
                }

                @Override
                public void OnError() {
                    isShowLoading.set(View.GONE);
                }
            });
        };

        realmChannelProfile = Realm.getDefaultInstance();

        RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        //todo:fixed it
        if (realmRoom == null || realmRoom.getChannelRoom() == null) {
            goBack.setValue(true);
            return;
        }
        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
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
            canChangeChannelName.set(true);
            G.onChannelUpdateReactionStatus = this;
        } else {
            showLayoutReactStatus.set(View.GONE);
            canChangeChannelName.set(false);
            G.onChannelUpdateReactionStatus = null;
        }

        /*try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }*/
        subscribersCount.set(String.valueOf(realmChannelRoom.getParticipantsCountLabel()));
        administratorsCount.set(String.valueOf(RealmMember.filterMember(realmChannelProfile, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ADMIN.toString()).size()));
        moderatorsCount.set(String.valueOf(RealmMember.filterMember(realmChannelProfile, roomId, "", new ArrayList<>(), ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.MODERATOR.toString()).size()));

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
                        isShowLoading.set(View.GONE);
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
        showConvertChannelDialog.setValue(isPrivate);
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
    public void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar) {
        /**
         * if another account do this action we haven't avatar source and have
         * to download avatars . for do this action call HelperAvatar.getAvatar
         */

        isShowLoading.set(View.GONE);
        /*if (pathSaveImage == null) {
            setAvatar();
        } else {
            avatarHandler.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                            setImage(avatarPath);
                        }
                    });
                }
            });
            pathSaveImage = null;
        }*/
    }

    @Override
    public void onAvatarAddError() {
        isShowLoading.set(View.GONE);
    }

    @Override
    public void onChannelAvatarDelete(long roomId, long avatarId) {

    }

    @Override
    public void onChannelDelete(long roomId) {
        closeActivity();
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onTimeOut() {

    }

    private Realm getRealm() {
        if (realmChannelProfile == null || realmChannelProfile.isClosed()) {
            realmChannelProfile = Realm.getDefaultInstance();
        }
        return realmChannelProfile;
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
            }
        });
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
        channelType.set(isPrivate ? R.string.private_channel : R.string.public_channel);
    }

    private void closeActivity() {
        G.handler.post(() -> {
            isShowLoading.set(View.GONE);
            goToChatRoom.setValue(true);
        });
    }
}
