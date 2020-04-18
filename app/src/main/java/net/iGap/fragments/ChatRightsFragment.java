package net.iGap.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.cells.EmptyCell;
import net.iGap.adapter.items.cells.MemberCell;
import net.iGap.adapter.items.cells.TextCell;
import net.iGap.adapter.items.cells.ToggleButtonCell;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoChannelAddAdmin;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupAddAdmin;
import net.iGap.proto.ProtoGroupChangeMemberRights;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomAccess;
import net.iGap.realm.RealmRoomAccessFields;
import net.iGap.request.RequestChannelAddAdmin;
import net.iGap.request.RequestChannelKickAdmin;
import net.iGap.request.RequestGroupAddAdmin;
import net.iGap.request.RequestGroupChangeMemberRights;
import net.iGap.request.RequestGroupKickAdmin;

public class ChatRightsFragment extends BaseFragment implements ToolbarListener, RecyclerListView.OnItemClickListener {
    private RealmRoom realmRoom;
    private RealmRegisteredInfo realmRegisteredInfo;
    @Nullable
    private RealmRoomAccess currentUserAccess;

    private long userId;
    private long roomId;
    private int currentMode;

    private RecyclerListView recyclerListView;

    private int rowCount;
    private int userProfileRow;
    private int emptyRow;
    private int empty2Row;
    private int postMessageRow;
    private int editMessageRow;
    private int deleteMessageRow;
    private int pinMessageRow;
    private int modifyRow;
    private int getMemberListRow;
    private int addNewMemberRow;
    private int banMemberRow;
    private int addNewAdminRow;
    private int dismissAdminRow;

    private int sendTextRow;
    private int sendMediaRow;
    private int sendGifRow;
    private int sendStickerRow;
    private int sendLinkRow;

    private boolean canPostMessage;
    private boolean canEditOthersMessage;
    private boolean canDeleteOtherMessage;
    private boolean canPinMessage;
    private boolean canModifyRoom;
    private boolean canGetMemberList;
    private boolean canAddNewMember;
    private boolean canBanMember;
    private boolean canAddNewAdmin;

    private boolean canSendText;
    private boolean canSendMedia;
    private boolean canSendGif;
    private boolean canSendSticker;
    private boolean canSendLink;

    public static ChatRightsFragment getIncense(RealmRoom realmRoom, RealmRoomAccess currentUserAccess, long userId, int mode) {
        return new ChatRightsFragment(realmRoom, currentUserAccess, userId, mode);
    }

    private ChatRightsFragment(RealmRoom realmRoom, @Nullable RealmRoomAccess currentUserAccess, long userId, int mode) {
        this.realmRoom = realmRoom;
        this.userId = userId;
        this.roomId = realmRoom.getId();
        this.currentMode = mode;
        this.currentUserAccess = currentUserAccess;

        RealmRoomAccess roomAccess = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoomAccess.class).equalTo(RealmRoomAccessFields.USER_ID, userId)
                    .equalTo(RealmRoomAccessFields.ROOM_ID, realmRoom.getId()).findFirst();
        });

        realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
        });

        if (currentMode == 0) {
            if (currentUserAccess == null) {
                canPostMessage = true;
                canEditOthersMessage = true;
                canDeleteOtherMessage = true;
                canPinMessage = true;
                canModifyRoom = true;
                canGetMemberList = true;
                canAddNewMember = true;
                canBanMember = false;
                canAddNewAdmin = false;
            } else {
                canPostMessage = currentUserAccess.isCanPostMessage();
                canEditOthersMessage = currentUserAccess.isCanEditMessage();
                canDeleteOtherMessage = currentUserAccess.isCanDeleteMessage();
                canPinMessage = currentUserAccess.isCanPinMessage();
                canModifyRoom = currentUserAccess.isCanModifyRoom();
                canGetMemberList = currentUserAccess.isCanGetMemberList();
                canAddNewMember = currentUserAccess.isCanAddNewMember();
                canBanMember = currentUserAccess.isCanBanMember();
                canAddNewAdmin = currentUserAccess.isCanAddNewAdmin();
            }
        } else if (currentMode == 1 && roomAccess != null) {
            canPostMessage = roomAccess.isCanPostMessage();
            canEditOthersMessage = roomAccess.isCanEditMessage();
            canDeleteOtherMessage = roomAccess.isCanDeleteMessage();
            canPinMessage = roomAccess.isCanPinMessage();
            canModifyRoom = roomAccess.isCanModifyRoom();
            canGetMemberList = roomAccess.isCanGetMemberList();
            canAddNewMember = roomAccess.isCanAddNewMember();
            canBanMember = roomAccess.isCanBanMember();
            canAddNewAdmin = roomAccess.isCanAddNewAdmin();
        } else if (currentMode == 2 && (roomAccess == null || roomAccess.getRealmPostMessageRights() == null)) {
            canSendText = true;
            canSendMedia = true;
            canSendGif = true;
            canSendSticker = true;
            canSendLink = true;
            canPinMessage = false;
            canAddNewMember = false;
            canGetMemberList = false;
        } else if (currentMode == 2 && roomAccess.getRealmPostMessageRights() != null) {
            canSendText = roomAccess.getRealmPostMessageRights().isCanSendText();
            canSendMedia = roomAccess.getRealmPostMessageRights().isCanSendMedia();
            canSendGif = roomAccess.getRealmPostMessageRights().isCanSendGif();
            canSendSticker = roomAccess.getRealmPostMessageRights().isCanSendSticker();
            canSendLink = roomAccess.getRealmPostMessageRights().isCanSendLink();
            canPinMessage = roomAccess.isCanPinMessage();
            canAddNewMember = roomAccess.isCanAddNewMember();
            canGetMemberList = roomAccess.isCanGetMemberList();
        }

        setRows();
    }

    private void setRows() {
        if (currentMode == 0) {//add admin
            userProfileRow = rowCount++;
            emptyRow = rowCount++;
            if (isChannel()) {
                postMessageRow = rowCount++;
                editMessageRow = rowCount++;
            }
            deleteMessageRow = rowCount++;
            pinMessageRow = rowCount++;
            modifyRow = rowCount++;
            getMemberListRow = rowCount++;
            addNewMemberRow = rowCount++;
            banMemberRow = rowCount++;
            addNewAdminRow = rowCount++;
        } else if (currentMode == 1) {//edit admin
            userProfileRow = rowCount++;
            emptyRow = rowCount++;
            if (isChannel()) {
                postMessageRow = rowCount++;
                editMessageRow = rowCount++;
            }
            deleteMessageRow = rowCount++;
            pinMessageRow = rowCount++;
            modifyRow = rowCount++;
            getMemberListRow = rowCount++;
            addNewMemberRow = rowCount++;
            banMemberRow = rowCount++;
            addNewAdminRow = rowCount++;
            empty2Row = rowCount++;
            dismissAdminRow = rowCount++;
        } else if (currentMode == 2) {// edit member or group
            userProfileRow = rowCount++;
            emptyRow = rowCount++;
            sendTextRow = rowCount++;
            sendMediaRow = rowCount++;
            sendGifRow = rowCount++;
            sendStickerRow = rowCount++;
            sendLinkRow = rowCount++;
            pinMessageRow = rowCount++;
            getMemberListRow = rowCount++;
            addNewMemberRow = rowCount++;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        HelperToolbar helperToolbar = HelperToolbar.create();

        String title = currentMode == 0 || currentMode == 1 ? "Admin Rights Edit" : isRoom() ? "Group Rights Edit" : "Member Rights Edit";

        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setRightIcons(R.string.check_icon)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(getString(R.string.new_channel))
                .setDefaultTitle(title)
                .getView();

        FrameLayout rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        rootView.addView(toolBar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));

        rootView.addView(recyclerListView = new RecyclerListView(getContext()), LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, LayoutCreator.getDimen(R.dimen.toolbar_height), 0, 0));

        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerListView.setAdapter(new ListAdapter());
        recyclerListView.setClipToPadding(false);
        recyclerListView.setPadding(0, 0, 0, LayoutCreator.dp(30));

        return rootView;
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onRightIconClickListener(View view) {
        if (mustDismissAdmin()) {
            dismissAdmin();
        } else if (currentMode != 2) {
            if (isChannel()) {
                ProtoChannelAddAdmin.ChannelAddAdmin.AdminRights.Builder builder = ProtoChannelAddAdmin.ChannelAddAdmin.AdminRights.newBuilder();
                builder.setAddAdmin(canAddNewAdmin)
                        .setAddMember(canAddNewMember)
                        .setBanMember(canBanMember)
                        .setDeleteMessage(canDeleteOtherMessage)
                        .setEditMessage(canEditOthersMessage)
                        .setGetMember(canGetMemberList)
                        .setModifyRoom(canModifyRoom)
                        .setPinMessage(canPinMessage)
                        .setPostMessage(canPostMessage);

                new RequestChannelAddAdmin().channelAddAdmin(roomId, userId, builder.build(), (response, error) -> {
                    if (error == null) {
                        G.runOnUiThread(() -> onLeftIconClickListener(null));
                    }
                });
            } else if (isGroup()) {
                ProtoGroupAddAdmin.GroupAddAdmin.AdminRights.Builder builder = ProtoGroupAddAdmin.GroupAddAdmin.AdminRights.newBuilder();
                builder.setAddAdmin(canAddNewAdmin)
                        .setAddMember(canAddNewMember)
                        .setBanMember(canBanMember)
                        .setDeleteMessage(canDeleteOtherMessage)
                        .setGetMember(canGetMemberList)
                        .setModifyRoom(canModifyRoom)
                        .setPinMessage(canPinMessage);

                new RequestGroupAddAdmin().groupAddAdmin(roomId, userId, builder.build(), (response, error) -> {
                    if (error == null) {
                        G.runOnUiThread(() -> onLeftIconClickListener(null));
                    }
                });
            }
        } else {
            if (isRoom()) {
                ProtoGroupChangeMemberRights.GroupChangeMemberRights.MemberRights.Builder builder = ProtoGroupChangeMemberRights.GroupChangeMemberRights.MemberRights.newBuilder();
                builder.setPinMessage(canPinMessage)
                        .setGetMember(canGetMemberList)
                        .setAddMember(canAddNewMember)
                        .setSendGif(canSendGif)
                        .setSendLink(canSendLink)
                        .setSendText(canSendText)
                        .setSendSticker(canSendSticker)
                        .setSendMedia(canSendMedia);

                new RequestGroupChangeMemberRights().groupChangeRights(roomId, builder.build(), (response, error) -> {
                    if (error == null) {
                        G.runOnUiThread(() -> onLeftIconClickListener(null));
                    }
                });
            } else {
                ProtoGroupChangeMemberRights.GroupChangeMemberRights.MemberRights.Builder builder = ProtoGroupChangeMemberRights.GroupChangeMemberRights.MemberRights.newBuilder();
                builder.setPinMessage(canPinMessage)
                        .setGetMember(canGetMemberList)
                        .setAddMember(canAddNewMember)
                        .setSendGif(canSendGif)
                        .setSendLink(canSendLink)
                        .setSendText(canSendText)
                        .setSendSticker(canSendSticker)
                        .setSendMedia(canSendMedia);

                new RequestGroupChangeMemberRights().groupChangeMemberRights(roomId, userId, builder.build(), (response, error) -> {
                    if (error == null) {
                        G.runOnUiThread(() -> onLeftIconClickListener(null));
                    }
                });
            }
        }
    }

    private void dismissAdmin() {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity())
                    .content(R.string.do_you_want_to_set_admin_role_to_member)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> {
                        if (isChannel()) {
                            new RequestChannelKickAdmin().channelKickAdmin(roomId, userId, (response, error) -> {
                                if (error == null) {

                                    G.runOnUiThread(() -> onLeftIconClickListener(null));
                                }
                            });
                        } else {
                            new RequestGroupKickAdmin().groupKickAdmin(roomId, userId, (response, error) -> {
                                if (error == null) {
                                    G.runOnUiThread(() -> onLeftIconClickListener(null));
                                }
                            });
                        }
                    }).show();
        }
    }

    @Override
    public void onClick(View view, int position) {
        if ((currentMode == 1 || currentMode == 0) && position == userProfileRow) {
            long roomId = RealmRoom.getRoomIdByPeerId(userId);
            if (getActivity() != null)
                if (roomId != 0) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(roomId, userId, ProtoGlobal.Room.Type.CHANNEL.toString())).setReplace(false).load();
                } else {
                    new HelperFragment(getActivity().getSupportFragmentManager(), FragmentContactsProfile.newInstance(0, userId, "Others")).setReplace(false).load();
                }
            return;
        }

        if (view instanceof ToggleButtonCell) {
            ToggleButtonCell buttonCell = (ToggleButtonCell) view;

            if (!buttonCell.isEnabled())
                return;

            buttonCell.setChecked(!buttonCell.isChecked());

            if (currentMode == 0 || currentMode == 1) {
                if (position == postMessageRow) {
                    canPostMessage = !canPostMessage;
                    RecyclerListView.ViewHolder holder = recyclerListView.findViewHolderForAdapterPosition(editMessageRow);
                    if (holder != null) {
                        if (canPostMessage) {
                            holder.itemView.setEnabled(true);
                        } else {
                            canEditOthersMessage = false;
                            ((ToggleButtonCell) holder.itemView).setChecked(false);
                            holder.itemView.setEnabled(canEditOthersMessage);
                        }
                    }
                } else if (position == editMessageRow) {
                    canEditOthersMessage = !canEditOthersMessage;
                } else if (position == deleteMessageRow) {
                    canDeleteOtherMessage = !canDeleteOtherMessage;
                } else if (position == pinMessageRow) {
                    canPinMessage = !canPinMessage;
                } else if (position == modifyRow) {
                    canModifyRoom = !canModifyRoom;
                } else if (position == getMemberListRow) {
                    canGetMemberList = !canGetMemberList;

                    RecyclerListView.ViewHolder newMemberHolder = recyclerListView.findViewHolderForAdapterPosition(addNewMemberRow);
                    if (newMemberHolder != null) {
                        if (canGetMemberList) {
                            newMemberHolder.itemView.setEnabled(true);
                        } else {
                            canAddNewMember = false;
                            ((ToggleButtonCell) newMemberHolder.itemView).setChecked(false);
                            newMemberHolder.itemView.setEnabled(false);
                        }
                    }

                    RecyclerListView.ViewHolder banMemberHolder = recyclerListView.findViewHolderForAdapterPosition(banMemberRow);
                    if (banMemberHolder != null) {
                        if (canGetMemberList) {
                            banMemberHolder.itemView.setEnabled(true);
                        } else {
                            canBanMember = false;
                            ((ToggleButtonCell) banMemberHolder.itemView).setChecked(false);
                            banMemberHolder.itemView.setEnabled(false);
                        }
                    }

                    RecyclerListView.ViewHolder addNewAdminHolder = recyclerListView.findViewHolderForAdapterPosition(addNewAdminRow);
                    if (addNewAdminHolder != null) {
                        if (canGetMemberList) {
                            addNewAdminHolder.itemView.setEnabled(true);
                        } else {
                            canAddNewAdmin = false;
                            ((ToggleButtonCell) addNewAdminHolder.itemView).setChecked(false);
                            addNewAdminHolder.itemView.setEnabled(false);
                        }
                    }

                } else if (position == addNewMemberRow) {
                    canAddNewMember = !canAddNewMember;
                } else if (position == banMemberRow) {
                    canBanMember = !canBanMember;
                } else if (position == addNewAdminRow) {
                    canAddNewAdmin = !canAddNewAdmin;
                }

                Log.i("abbasiChanges", "changes" +
                        "\ncanPostMessage        -> " + canPostMessage +
                        "\ncanEditOthersMessage  -> " + canEditOthersMessage +
                        "\ncanDeleteOtherMessage -> " + canDeleteOtherMessage +
                        "\ncanPinMessage         -> " + canPinMessage +
                        "\ncanModifyRoom         -> " + canModifyRoom +
                        "\ncanGetMemberList      -> " + canGetMemberList +
                        "\ncanAddNewMember       -> " + canAddNewMember +
                        "\ncanBanMember          -> " + canBanMember +
                        "\ncanAddNewAdmin        -> " + canAddNewAdmin);


            } else {
                if (position == sendTextRow) {
                    canSendText = !canSendText;
                } else if (position == sendGifRow) {
                    canSendGif = !canSendGif;
                } else if (position == sendLinkRow) {
                    canSendLink = !canSendLink;
                } else if (position == sendMediaRow) {
                    canSendMedia = !canSendMedia;
                } else if (position == sendStickerRow) {
                    canSendSticker = !canSendSticker;
                } else if (position == addNewMemberRow) {
                    canAddNewMember = !canAddNewMember;
                } else if (position == pinMessageRow) {
                    canPinMessage = !canPinMessage;
                } else if (position == getMemberListRow) {
                    canGetMemberList = !canGetMemberList;
                }

                Log.i("abbasiChanges", "changes" +
                        "\ncanSendText    -> " + canSendText +
                        "\ncanSendMedia   -> " + canSendMedia +
                        "\ncanSendGif     -> " + canSendGif +
                        "\ncanSendSticker -> " + canSendSticker +
                        "\ncanSendLink    -> " + canSendLink);

            }
        }

        if ((currentMode == 1 || currentMode == 0) && position == dismissAdminRow) {
            dismissAdmin();
        }
    }

    private boolean isChannel() {
        return realmRoom.getType().equals(ProtoGlobal.Room.Type.CHANNEL) && userId != 0;
    }

    private boolean isGroup() {
        return realmRoom.getType().equals(ProtoGlobal.Room.Type.GROUP) && userId != 0;
    }

    private boolean isRoom() {
        return realmRoom.getType().equals(ProtoGlobal.Room.Type.GROUP) && userId == 0 || userId == -1;
    }

    private boolean mustDismissAdmin() {
        return currentMode == 1 && !canPostMessage && !canEditOthersMessage && !canDeleteOtherMessage && !canPinMessage && !canModifyRoom && !canGetMemberList && !canAddNewMember && !canAddNewAdmin && !canBanMember;
    }

    private class ListAdapter extends RecyclerListView.ItemAdapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView;
            switch (viewType) {
                case 0:
                    cellView = new MemberCell(parent.getContext());
                    cellView.setBackgroundColor(Theme.getInstance().getRootColor(parent.getContext()));
                    cellView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
                    break;
                case 1:
                    cellView = new EmptyCell(parent.getContext(), 12);
                    cellView.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
                    break;
                case 2:
                    cellView = new ToggleButtonCell(parent.getContext());
                    cellView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));
                    cellView.setBackgroundColor(Theme.getInstance().getRootColor(parent.getContext()));
                    break;
                case 3:
                    cellView = new TextCell(parent.getContext());
                    cellView.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));
                    cellView.setBackgroundColor(Theme.getInstance().getRootColor(parent.getContext()));
                    break;
                default:
                    cellView = new View(parent.getContext());
            }
            return new RecyclerListView.ItemViewHolder(cellView, ChatRightsFragment.this);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == userProfileRow) {
                        MemberCell memberCell = (MemberCell) holder.itemView;
                        if (realmRegisteredInfo != null) {
                            String userStatus;
                            if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                userStatus = LastSeenTimeUtil.computeTime(getContext(), userId, realmRegisteredInfo.getLastSeen(), false);
                            } else {
                                userStatus = realmRegisteredInfo.getStatus();
                            }

                            memberCell.setInfo(avatarHandler, userId, realmRegisteredInfo.getDisplayName(), userStatus);
                        }
                    }
                    break;
                case 2:
                    ToggleButtonCell toggleButtonCell = (ToggleButtonCell) holder.itemView;
                    if (position == postMessageRow) {
                        toggleButtonCell.setTextAndCheck("Post message", canPostMessage, true);
                    } else if (position == editMessageRow) {
                        toggleButtonCell.setTextAndCheck("Edit others message", canEditOthersMessage, true);
                    } else if (position == deleteMessageRow) {
                        toggleButtonCell.setTextAndCheck("Delete others message", canDeleteOtherMessage, true);
                    } else if (position == pinMessageRow) {
                        toggleButtonCell.setTextAndCheck("Pin message", canPinMessage, true);
                    } else if (position == modifyRow) {
                        toggleButtonCell.setTextAndCheck("Modify Room", canModifyRoom, true);
                    } else if (position == getMemberListRow) {
                        toggleButtonCell.setTextAndCheck("Show members", canGetMemberList, true);
                    } else if (position == addNewMemberRow) {
                        toggleButtonCell.setTextAndCheck("Add new member", canAddNewMember, true);
                    } else if (position == banMemberRow) {
                        toggleButtonCell.setTextAndCheck("Ban member", canBanMember, true);
                    } else if (position == addNewAdminRow) {
                        toggleButtonCell.setTextAndCheck("Add new admin", canAddNewAdmin, true);
                    } else if (position == sendTextRow) {
                        toggleButtonCell.setTextAndCheck("Send Text message", canSendText, true);
                    } else if (position == sendGifRow) {
                        toggleButtonCell.setTextAndCheck("Send gif message", canSendGif, true);
                    } else if (position == sendMediaRow) {
                        toggleButtonCell.setTextAndCheck("Send media message", canSendMedia, true);
                    } else if (position == sendStickerRow) {
                        toggleButtonCell.setTextAndCheck("Send sticker message", canSendSticker, true);
                    } else if (position == sendLinkRow) {
                        toggleButtonCell.setTextAndCheck("Send link message", canSendLink, false);
                    }

                    if (position == sendMediaRow || position == sendStickerRow) {
                        toggleButtonCell.setEnabled(canPostMessage);
                    } else if (currentMode == 2 && position == addNewMemberRow) {
                        toggleButtonCell.setEnabled(canGetMemberList);
                    }

                    if (position == addNewMemberRow || position == banMemberRow || position == addNewAdminRow) {
                        toggleButtonCell.setEnabled(canGetMemberList);
                    }

                    break;
                case 3:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == dismissAdminRow) {
                        textCell.setValue("Dismiss admin");
                        textCell.setTextColor(textCell.getContext().getResources().getColor(R.color.red));
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == userProfileRow)
                return 0;
            else if (position == empty2Row || position == emptyRow)
                return 1;
            else if (position == postMessageRow || position == editMessageRow || position == deleteMessageRow ||
                    position == pinMessageRow || position == modifyRow || position == getMemberListRow ||
                    position == addNewMemberRow || position == addNewAdminRow || position == banMemberRow ||
                    position == sendTextRow || position == sendMediaRow || position == sendGifRow || position == sendStickerRow || position == sendLinkRow)
                return 2;
            else if (position == dismissAdminRow)
                return 3;
            else
                return 4;
        }

        @Override
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            if (currentMode == 0 || currentMode == 1 && viewType == 2) {
                if (position == postMessageRow) {
                    return currentUserAccess == null || currentUserAccess.isCanPostMessage();
                } else if (position == editMessageRow) {
                    return canPostMessage && (currentUserAccess == null || currentUserAccess.isCanEditMessage());
                } else if (position == deleteMessageRow) {
                    return currentUserAccess == null || currentUserAccess.isCanDeleteMessage();
                } else if (position == pinMessageRow) {
                    return currentUserAccess == null || currentUserAccess.isCanPinMessage();
                } else if (position == modifyRow) {
                    return currentUserAccess == null || currentUserAccess.isCanModifyRoom();
                } else if (position == getMemberListRow) {
                    return currentUserAccess == null || currentUserAccess.isCanGetMemberList();
                } else if (position == addNewAdminRow) {
                    return canGetMemberList && (currentUserAccess == null || currentUserAccess.isCanAddNewAdmin());
                } else if (position == banMemberRow) {
                    return canGetMemberList && (currentUserAccess == null || currentUserAccess.isCanBanMember());
                } else if (position == addNewMemberRow) {
                    return canGetMemberList && (currentUserAccess == null || currentUserAccess.isCanAddNewMember());
                }
            }
            return viewType != 1;
        }
    }
}
