package net.iGap.realm;

import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoChannelAddAdmin;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupAddAdmin;
import net.iGap.proto.ProtoGroupChangeMemberRights;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRoomAccess extends RealmObject {

    @PrimaryKey
    private String id;
    private long userId;
    private long roomId;
    private RealmPostMessageRights realmPostMessageRights;
    private boolean canModifyRoom;
    private boolean canEditMessage;
    private boolean canDeleteMessage;
    private boolean canPinMessage;
    private boolean canAddNewMember;
    private boolean canBanMember;
    private boolean canGetMemberList;
    private boolean canAddNewAdmin;
    private boolean canAddNewStory;

    public static void putOrUpdate(ProtoGlobal.RoomAccess roomAccess, long userId, long roomId, Realm realm) {

        RealmRoomAccess realmRoomAccess = realm.where(RealmRoomAccess.class).equalTo("id", roomId + "_" + userId).findFirst();

        if (realmRoomAccess == null) {
            realmRoomAccess = realm.createObject(RealmRoomAccess.class, roomId + "_" + userId);
        }

        RealmPostMessageRights realmPostMessageRights = realmRoomAccess.getRealmPostMessageRights();

        if (realmPostMessageRights == null) {
            realmPostMessageRights = realm.createObject(RealmPostMessageRights.class);
        }

        if (realmPostMessageRights != null && roomAccess.getPostMessage() != null) {
            realmPostMessageRights.setCanSendGif(roomAccess.getPostMessage().getSendGif());
            realmPostMessageRights.setCanSendLink(roomAccess.getPostMessage().getSendLink());
            realmPostMessageRights.setCanSendMedia(roomAccess.getPostMessage().getSendMedia());
            realmPostMessageRights.setCanSendSticker(roomAccess.getPostMessage().getSendSticker());
            realmPostMessageRights.setCanSendText(roomAccess.getPostMessage().getSendText());
        }

        realmRoomAccess.setUserId(userId);
        realmRoomAccess.setRoomId(roomId);

        realmRoomAccess.setCanModifyRoom(roomAccess.getModifyRoom());
        realmRoomAccess.setCanAddNewStory(roomAccess.getAddStory());
        realmRoomAccess.setCanEditMessage(roomAccess.getEditMessage());
        realmRoomAccess.setCanDeleteMessage(roomAccess.getDeleteMessage());
        realmRoomAccess.setCanPinMessage(roomAccess.getPinMessage());
        realmRoomAccess.setCanAddNewMember(roomAccess.getAddMember());
        realmRoomAccess.setCanBanMember(roomAccess.getBanMember());
        realmRoomAccess.setCanGetMemberList(roomAccess.getGetMember());
        realmRoomAccess.setCanAddNewAdmin(roomAccess.getAddAdmin());
        realmRoomAccess.setRealmPostMessageRights(realmPostMessageRights);

    }

    public static void channelAdminPutOrUpdate(ProtoChannelAddAdmin.ChannelAddAdmin.AdminRights adminRights, long userId, long roomId, Realm realm) {

        RealmRoomAccess realmRoomAccess = realm.where(RealmRoomAccess.class).equalTo("id", roomId + "_" + userId).findFirst();

        if (realmRoomAccess == null) {
            realmRoomAccess = realm.createObject(RealmRoomAccess.class, roomId + "_" + userId);
        }

        RealmPostMessageRights realmPostMessageRights = realmRoomAccess.getRealmPostMessageRights();

        if (realmPostMessageRights == null) {
            realmPostMessageRights = realm.createObject(RealmPostMessageRights.class);
        }

        if (realmPostMessageRights != null)
            realmPostMessageRights.setPostMessage(adminRights.getPostMessage());

        realmRoomAccess.setUserId(userId);
        realmRoomAccess.setRoomId(roomId);

        realmRoomAccess.setCanModifyRoom(adminRights.getModifyRoom());
        realmRoomAccess.setCanAddNewStory(adminRights.getAddStory());
        realmRoomAccess.setRealmPostMessageRights(realmPostMessageRights);
        realmRoomAccess.setCanEditMessage(adminRights.getEditMessage());
        realmRoomAccess.setCanDeleteMessage(adminRights.getDeleteMessage());
        realmRoomAccess.setCanPinMessage(adminRights.getPinMessage());
        realmRoomAccess.setCanAddNewMember(adminRights.getAddMember());
        realmRoomAccess.setCanBanMember(adminRights.getBanMember());
        realmRoomAccess.setCanGetMemberList(adminRights.getGetMember());
        realmRoomAccess.setCanAddNewAdmin(adminRights.getAddAdmin());

    }

    public static void groupAdminPutOrUpdate(ProtoGroupAddAdmin.GroupAddAdmin.AdminRights adminRights, long userId, long roomId, Realm realm) {

        RealmRoomAccess realmRoomAccess = realm.where(RealmRoomAccess.class).equalTo("id", roomId + "_" + userId).findFirst();

        if (realmRoomAccess == null) {
            realmRoomAccess = realm.createObject(RealmRoomAccess.class, roomId + "_" + userId);
        }

        RealmPostMessageRights realmPostMessageRights = realmRoomAccess.getRealmPostMessageRights();

        if (realmPostMessageRights == null) {
            realmPostMessageRights = realm.createObject(RealmPostMessageRights.class);
        }

        if (realmPostMessageRights != null) {
            realmPostMessageRights.setPostMessage(true);
        }

        realmRoomAccess.setUserId(userId);
        realmRoomAccess.setRoomId(roomId);
        realmRoomAccess.setRealmPostMessageRights(realmPostMessageRights);
        realmRoomAccess.setCanModifyRoom(adminRights.getModifyRoom());
        realmRoomAccess.setCanDeleteMessage(adminRights.getDeleteMessage());
        realmRoomAccess.setCanPinMessage(adminRights.getPinMessage());
        realmRoomAccess.setCanAddNewMember(adminRights.getAddMember());
        realmRoomAccess.setCanBanMember(adminRights.getBanMember());
        realmRoomAccess.setCanGetMemberList(adminRights.getGetMember());
        realmRoomAccess.setCanAddNewAdmin(adminRights.getAddAdmin());

    }

    public static void groupMemberPutOrUpdate(ProtoGroupChangeMemberRights.GroupChangeMemberRights.MemberRights memberRights, long userId, long roomId, Realm realm) {
        RealmRoomAccess realmRoomAccess = realm.where(RealmRoomAccess.class).equalTo("id", roomId + "_" + userId).findFirst();

        if (realmRoomAccess == null) {
            realmRoomAccess = realm.createObject(RealmRoomAccess.class, roomId + "_" + userId);
        }

        RealmPostMessageRights realmPostMessageRights = realmRoomAccess.getRealmPostMessageRights();

        if (realmPostMessageRights == null) {
            realmPostMessageRights = realm.createObject(RealmPostMessageRights.class);
        }

        if (realmPostMessageRights != null) {
            realmPostMessageRights.setCanSendText(memberRights.getSendText());
            realmPostMessageRights.setCanSendSticker(memberRights.getSendSticker());
            realmPostMessageRights.setCanSendMedia(memberRights.getSendMedia());
            realmPostMessageRights.setCanSendLink(memberRights.getSendLink());
            realmPostMessageRights.setCanSendGif(memberRights.getSendGif());
        }

        realmRoomAccess.setUserId(userId);
        realmRoomAccess.setRoomId(roomId);
        realmRoomAccess.setRealmPostMessageRights(realmPostMessageRights);
        realmRoomAccess.setCanPinMessage(memberRights.getPinMessage());
        realmRoomAccess.setCanGetMemberList(memberRights.getGetMember());
        realmRoomAccess.setCanAddNewMember(memberRights.getAddMember());
        realmRoomAccess.setCanDeleteMessage(false);

    }

    public static void getAccess(long userId, long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoomAccess realmRoomAccess = realm.where(RealmRoomAccess.class).equalTo("id", roomId + "_" + userId).findFirst();

            if (realmRoomAccess != null) {
                if (realmRoomAccess.getRealmPostMessageRights() != null) {
                    realmRoomAccess.getRealmPostMessageRights().setPostMessage(false);
                }

                realmRoomAccess.setCanModifyRoom(false);
                realmRoomAccess.setCanDeleteMessage(false);
                realmRoomAccess.setCanPinMessage(false);
                realmRoomAccess.setCanAddNewMember(false);
                realmRoomAccess.setCanBanMember(false);
                realmRoomAccess.setCanAddNewStory(false);
                realmRoomAccess.setCanGetMemberList(false);
                realmRoomAccess.setCanAddNewAdmin(false);
            }
        });
    }

    private static void delete(long userId, long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoomAccess realmRoomAccess = realm.where(RealmRoomAccess.class).equalTo("id", roomId + "_" + userId)
                    .findFirst();

            if (realmRoomAccess != null) {
                if (realmRoomAccess.getRealmPostMessageRights() != null) {
                    realmRoomAccess.getRealmPostMessageRights().deleteFromRealm();
                }

                realmRoomAccess.deleteFromRealm();
            }
        });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCanModifyRoom() {
        return canModifyRoom;
    }

    private void setCanModifyRoom(boolean canModifyRoom) {
        this.canModifyRoom = canModifyRoom;
    }

    public boolean isCanPostMessage() {
        return realmPostMessageRights != null && realmPostMessageRights.canPostMessage();
    }

    private void setRealmPostMessageRights(RealmPostMessageRights realmPostMessageRights) {
        this.realmPostMessageRights = realmPostMessageRights;
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

    public boolean isCanAddNewStory() {
        return canAddNewStory;
    }

    public void setCanAddNewStory(boolean canAddNewStory) {
        this.canAddNewStory = canAddNewStory;
    }

    private void setCanAddNewAdmin(boolean canAddNewAdmin) {
        this.canAddNewAdmin = canAddNewAdmin;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public RealmPostMessageRights getRealmPostMessageRights() {
        return realmPostMessageRights;
    }
}