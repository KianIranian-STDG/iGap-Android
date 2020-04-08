package net.iGap.realm;

import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmRoomAccess extends RealmObject {

    private long userId;
    private long roomId;
    private boolean canModifyRoom;
    private boolean canPostMessage;
    private boolean canEditMessage;
    private boolean canDeleteMessage;
    private boolean canPinMessage;
    private boolean canAddNewMember;
    private boolean canBanMember;
    private boolean canGetMemberList;
    private boolean canAddNewAdmin;

    public static RealmRoomAccess putOrUpdate(ProtoGlobal.RoomAccess roomAccess, long userId, long roomId, Realm realm) {

        RealmRoomAccess realmRoomAccess = DbManager.getInstance().doRealmTask(realm1 -> {
            return realm1.where(RealmRoomAccess.class).equalTo(RealmRoomAccessFields.ROOM_ID, roomId)
                    .equalTo(RealmRoomAccessFields.USER_ID, userId)
                    .findFirst();
        });

        if (realmRoomAccess == null) {
            realmRoomAccess = realm.createObject(RealmRoomAccess.class);
        }

        realmRoomAccess.setUserId(userId);
        realmRoomAccess.setRoomId(roomId);

        realmRoomAccess.setCanModifyRoom(roomAccess.getModifyRoom());
        realmRoomAccess.setCanPostMessage(roomAccess.getPostMessage());
        realmRoomAccess.setCanEditMessage(roomAccess.getEditMessage());
        realmRoomAccess.setCanDeleteMessage(roomAccess.getDeleteMessage());
        realmRoomAccess.setCanPinMessage(roomAccess.getPinMessage());
        realmRoomAccess.setCanAddNewMember(roomAccess.getAddMember());
        realmRoomAccess.setCanBanMember(roomAccess.getBanMember());
        realmRoomAccess.setCanGetMemberList(roomAccess.getGetMember());
        realmRoomAccess.setCanAddNewAdmin(roomAccess.getAddAdmin());

        return realmRoomAccess;
    }

    public boolean isCanModifyRoom() {
        return canModifyRoom;
    }

    private void setCanModifyRoom(boolean canModifyRoom) {
        this.canModifyRoom = canModifyRoom;
    }

    public boolean isCanPostMessage() {
        return canPostMessage;
    }

    private void setCanPostMessage(boolean canPostMessage) {
        this.canPostMessage = canPostMessage;
    }

    public boolean isCanEditMessage() {
        return canEditMessage;
    }

    private void setCanEditMessage(boolean canEditMessage) {
        this.canEditMessage = canEditMessage;
    }

    public boolean isCanDeleteMessage() {
        return canDeleteMessage;
    }

    private void setCanDeleteMessage(boolean canDeleteMessage) {
        this.canDeleteMessage = canDeleteMessage;
    }

    public boolean isCanPinMessage() {
        return canPinMessage;
    }

    private void setCanPinMessage(boolean canPinMessage) {
        this.canPinMessage = canPinMessage;
    }

    public boolean isCanAddNewMember() {
        return canAddNewMember;
    }

    private void setCanAddNewMember(boolean canAddNewMember) {
        this.canAddNewMember = canAddNewMember;
    }

    public boolean isCanBanMember() {
        return canBanMember;
    }

    private void setCanBanMember(boolean canBanMember) {
        this.canBanMember = canBanMember;
    }

    public boolean isCanGetMemberList() {
        return canGetMemberList;
    }

    private void setCanGetMemberList(boolean canGetMemberList) {
        this.canGetMemberList = canGetMemberList;
    }

    public boolean isCanAddNewAdmin() {
        return canAddNewAdmin;
    }

    private void setCanAddNewAdmin(boolean canAddNewAdmin) {
        this.canAddNewAdmin = canAddNewAdmin;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}