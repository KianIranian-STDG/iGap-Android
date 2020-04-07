package net.iGap.realm;

import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmRoomAccess extends RealmObject {
    private boolean canModifyRoom;
    private boolean canPostMessage;
    private boolean canEditMessage;
    private boolean canDeleteMessage;
    private boolean canPinMessage;
    private boolean canAddMember;
    private boolean canBanMember;
    private boolean canGetMemberList;
    private boolean canAddNewAdmin;

    public static RealmRoomAccess convert(ProtoGlobal.RoomAccess roomAccess, RealmRoomAccess realmRoomAccess, Realm realm) {

        if (realmRoomAccess == null) {
            realmRoomAccess = realm.createObject(RealmRoomAccess.class);
        }

        realmRoomAccess.setCanModifyRoom(roomAccess.getModifyRoom());
        realmRoomAccess.setCanPostMessage(roomAccess.getPostMessage());
        realmRoomAccess.setCanEditMessage(roomAccess.getEditMessage());
        realmRoomAccess.setCanDeleteMessage(roomAccess.getDeleteMessage());
        realmRoomAccess.setCanPinMessage(roomAccess.getPinMessage());
        realmRoomAccess.setCanAddMember(roomAccess.getAddMember());
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

    public boolean isCanAddMember() {
        return canAddMember;
    }

    private void setCanAddMember(boolean canAddMember) {
        this.canAddMember = canAddMember;
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
}