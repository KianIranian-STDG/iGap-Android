package net.iGap.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.cells.EmptyCell;
import net.iGap.adapter.items.cells.MemberCell;
import net.iGap.adapter.items.cells.TextCell;
import net.iGap.adapter.items.cells.ToggleButtonCell;
import net.iGap.helper.HelperMember;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoChannelKickAdmin;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupAddAdmin;
import net.iGap.proto.ProtoGroupKickAdmin;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomAccess;
import net.iGap.realm.RealmRoomAccessFields;
import net.iGap.request.RequestChannelAddAdmin;
import net.iGap.request.RequestChannelKickAdmin;
import net.iGap.request.RequestGroupAddAdmin;
import net.iGap.request.RequestGroupKickAdmin;

public class AdminRightsEditFragment extends BaseFragment implements ToolbarListener {
    private RealmRoom realmRoom;
    private RealmRoomAccess currentRealmAccess;
    private RealmRegisteredInfo realmRegisteredInfo;

    private long userId;
    private long roomId;
    private boolean isAdmin;

    private ToggleButtonCell modifyRoomCell;
    private ToggleButtonCell postMessageCell;
    private ToggleButtonCell editMessageCell;
    private ToggleButtonCell deleteMessageCell;
    private ToggleButtonCell pinMessageCell;
    private ToggleButtonCell addNewMemberCell;
    private ToggleButtonCell banMemberCell;
    private ToggleButtonCell showMemberListCell;
    private ToggleButtonCell addNewAdminCell;

    public static AdminRightsEditFragment getIncense(RealmRoom realmRoom, long userId, boolean isAdmin) {
        AdminRightsEditFragment chatRightsEditFragment = new AdminRightsEditFragment();
        chatRightsEditFragment.realmRoom = realmRoom;
        chatRightsEditFragment.userId = userId;
        chatRightsEditFragment.roomId = realmRoom.getId();
        chatRightsEditFragment.isAdmin = isAdmin;
        return chatRightsEditFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentRealmAccess = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoomAccess.class).equalTo(RealmRoomAccessFields.USER_ID, userId).equalTo(RealmRoomAccessFields.ROOM_ID, realmRoom.getId()).findFirst();
        });

        realmRegisteredInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        HelperToolbar helperToolbar = HelperToolbar.create();

        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setRightIcons(R.string.check_icon)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(getString(R.string.new_channel))
                .setDefaultTitle("Admin Rights Edit")
                .getView();

        FrameLayout rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Theme.getInstance().getRootColor(getContext()));
        rootView.addView(toolBar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));

        ScrollView scrollView;
        rootView.addView(scrollView = new ScrollView(getContext()), LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, LayoutCreator.getDimen(R.dimen.toolbar_height), 0, 0));

        LinearLayout linearLayout;
        scrollView.addView(linearLayout = new LinearLayout(getContext()), LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(0, 0, 0, LayoutCreator.dp(100));

        MemberCell memberCell = new MemberCell(getContext());
        if (realmRegisteredInfo != null) {
            String userStatus;
            if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                userStatus = LastSeenTimeUtil.computeTime(getContext(), userId, realmRegisteredInfo.getLastSeen(), false);
            } else {
                userStatus = realmRegisteredInfo.getStatus();
            }

            memberCell.setInfo(avatarHandler, userId, realmRegisteredInfo.getDisplayName(), userStatus);
        }
        linearLayout.addView(memberCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        EmptyCell emptyCell = new EmptyCell(getContext(), 12);
        emptyCell.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        linearLayout.addView(emptyCell);

        modifyRoomCell = new ToggleButtonCell(getContext(), true);
        modifyRoomCell.setText("Modify room");
        linearLayout.addView(modifyRoomCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        postMessageCell = new ToggleButtonCell(getContext(), true);
        postMessageCell.setText("Post message");
        linearLayout.addView(postMessageCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));


        if (realmRoom.getType().equals(ProtoGlobal.Room.Type.CHANNEL)) {
            editMessageCell = new ToggleButtonCell(getContext(), true);
            editMessageCell.setText("Edit others message");

            postMessageCell.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    editMessageCell.setChecked(false);
                    editMessageCell.setEnabled(false);
                } else {
                    editMessageCell.setEnabled(true);
                }
            });

            linearLayout.addView(editMessageCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));
        }

        deleteMessageCell = new ToggleButtonCell(getContext(), true);
        deleteMessageCell.setText("Delete others message");
        linearLayout.addView(deleteMessageCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        pinMessageCell = new ToggleButtonCell(getContext(), true);
        pinMessageCell.setText("Pin message");
        linearLayout.addView(pinMessageCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        addNewMemberCell = new ToggleButtonCell(getContext(), true);
        addNewMemberCell.setText("Add members");
        linearLayout.addView(addNewMemberCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        showMemberListCell = new ToggleButtonCell(getContext(), true);
        showMemberListCell.setText("Show members");
        linearLayout.addView(showMemberListCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        banMemberCell = new ToggleButtonCell(getContext(), true);
        banMemberCell.setText("Ban members");
        linearLayout.addView(banMemberCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        addNewAdminCell = new ToggleButtonCell(getContext(), false);
        addNewAdminCell.setText("Add new admin");
        linearLayout.addView(addNewAdminCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));


        showMemberListCell.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                banMemberCell.setChecked(false);
                addNewAdminCell.setChecked(false);
                banMemberCell.setEnabled(false);
                addNewAdminCell.setEnabled(false);
            } else {
                banMemberCell.setEnabled(true);
                addNewAdminCell.setEnabled(true);
            }
        });

        if (isAdmin) {
            EmptyCell emptyCell2 = new EmptyCell(getContext(), 12);
            emptyCell2.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
            linearLayout.addView(emptyCell2);

            TextCell dismissAdminCell = new TextCell(getContext(), true);
            dismissAdminCell.setValue("Dismiss admin");
            dismissAdminCell.setTextColor(getContext().getResources().getColor(R.color.red));
            linearLayout.addView(dismissAdminCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

            dismissAdminCell.setOnClickListener(v -> dismissAdmin());
        }

        if (currentRealmAccess != null) {
            modifyRoomCell.setChecked(currentRealmAccess.isCanModifyRoom());
            postMessageCell.setChecked(currentRealmAccess.isCanPostMessage());

            if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                editMessageCell.setChecked(currentRealmAccess.isCanEditMessage());
                editMessageCell.setEnabled(currentRealmAccess.isCanPostMessage());
            }

            deleteMessageCell.setChecked(currentRealmAccess.isCanDeleteMessage());
            pinMessageCell.setChecked(currentRealmAccess.isCanPinMessage());
            addNewMemberCell.setChecked(currentRealmAccess.isCanAddNewMember());
            banMemberCell.setChecked(currentRealmAccess.isCanBanMember());
            showMemberListCell.setChecked(currentRealmAccess.isCanGetMemberList());
            addNewAdminCell.setChecked(currentRealmAccess.isCanAddNewAdmin());
        }

        addNewAdminCell.setEnabled(showMemberListCell.isChecked());
        banMemberCell.setEnabled(showMemberListCell.isChecked());


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
        } else {
            sendAddAdminRequest();
        }
    }

    private void sendAddAdminRequest() {

        ProtoGlobal.RoomAccess.Builder roomAccessBuilder = ProtoGlobal.RoomAccess.newBuilder();

        roomAccessBuilder.setModifyRoom(modifyRoomCell.isChecked());
        roomAccessBuilder.setDeleteMessage(deleteMessageCell.isChecked());
        roomAccessBuilder.setPinMessage(pinMessageCell.isChecked());
        roomAccessBuilder.setAddMember(addNewMemberCell.isChecked());
        roomAccessBuilder.setBanMember(banMemberCell.isChecked());
        roomAccessBuilder.setGetMember(showMemberListCell.isChecked());
        roomAccessBuilder.setAddAdmin(addNewAdminCell.isChecked());
        roomAccessBuilder.setPostMessage(postMessageCell.isChecked());

        if (realmRoom.getType().equals(ProtoGlobal.Room.Type.CHANNEL)) {
            roomAccessBuilder.setEditMessage(editMessageCell.isChecked());

            new RequestChannelAddAdmin().channelAddAdmin(roomId, userId, roomAccessBuilder.build(), (response, error) -> {
                if (error == null) {
                    G.runOnUiThread(() -> onLeftIconClickListener(null));
                }
            });
        } else {
            new RequestGroupAddAdmin().groupAddAdmin(roomId, userId, roomAccessBuilder.build(), (response, error) -> {
                if (error == null) {
                    ProtoGroupAddAdmin.GroupAddAdminResponse.Builder builder = (ProtoGroupAddAdmin.GroupAddAdminResponse.Builder) response;
                    HelperMember.updateRole(builder.getRoomId(), builder.getMemberId(), ChannelChatRole.ADMIN.toString());

                    G.runOnUiThread(() -> onLeftIconClickListener(null));

                    DbManager.getInstance().doRealmTask(realm -> {
                        realm.executeTransactionAsync(asyncRealm -> RealmRoomAccess.putOrUpdate(builder.getPermission(), userId, roomId, asyncRealm));
                    });
                }
            });
        }
    }

    private void dismissAdmin() {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity())
                    .content(R.string.do_you_want_to_set_admin_role_to_member)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> {
                        if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                            new RequestChannelKickAdmin().channelKickAdmin(roomId, userId, (response, error) -> {
                                if (error == null) {
                                    ProtoChannelKickAdmin.ChannelKickAdminResponse.Builder builder = (ProtoChannelKickAdmin.ChannelKickAdminResponse.Builder) response;
                                    HelperMember.updateRole(builder.getRoomId(), builder.getMemberId(), ChannelChatRole.MEMBER.toString());

                                    G.runOnUiThread(() -> onLeftIconClickListener(null));

                                    DbManager.getInstance().doRealmTask(realm -> {
                                        realm.executeTransactionAsync(asyncRealm -> RealmRoomAccess.getAccess(userId, roomId, asyncRealm));
                                    });
                                }
                            });
                        } else {
                            new RequestGroupKickAdmin().groupKickAdmin(roomId, userId, (response, error) -> {
                                if (error == null) {
                                    ProtoGroupKickAdmin.GroupKickAdminResponse.Builder builder = (ProtoGroupKickAdmin.GroupKickAdminResponse.Builder) response;
                                    HelperMember.updateRole(builder.getRoomId(), builder.getMemberId(), ChannelChatRole.MEMBER.toString());

                                    G.runOnUiThread(() -> onLeftIconClickListener(null));

                                    DbManager.getInstance().doRealmTask(realm -> {
                                        realm.executeTransactionAsync(asyncRealm -> RealmRoomAccess.getAccess(userId, roomId, asyncRealm));
                                    });
                                }
                            });
                        }
                    }).show();
        }
    }

    private boolean mustDismissAdmin() {
        return isAdmin && !modifyRoomCell.isChecked() && (postMessageCell != null && !postMessageCell.isChecked()) && (editMessageCell != null && !editMessageCell.isChecked()) && !editMessageCell.isChecked() && !deleteMessageCell.isChecked() && !pinMessageCell.isChecked() && !addNewMemberCell.isChecked() && !banMemberCell.isChecked() && !showMemberListCell.isChecked() && !addNewAdminCell.isChecked();
    }
}
