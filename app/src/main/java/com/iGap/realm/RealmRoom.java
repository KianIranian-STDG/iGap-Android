package com.iGap.realm;

import android.text.format.DateUtils;

import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.enums.RoomType;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmRoom extends RealmObject {
    @PrimaryKey
    private long id;
    private String type;
    private String title;
    private String initials;
    private String color;
    private int unread_count;
    private boolean readOnly;
    private RealmChatRoom chat_room;
    private boolean mute;
    private RealmGroupRoom group_room;
    private RealmChannelRoom channel_room;
    private long lastMessageId;
    private long lastMessageTime;
    private String lastMessage;
    private String lastMessageStatus;
    private RealmRoomDraft draft;
    private RealmDraftFile draftFile;
    private RealmAvatar avatar;

    /**
     * convert ProtoGlobal.Room to RealmRoom for saving into database
     *
     * @param room ProtoGlobal.Room
     * @return RealmRoom
     */
    public static RealmRoom convert(ProtoGlobal.Room room, Realm realm) {
        putChatToClientCondition(room, realm);

        RealmRoom realmRoom =
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();
        if (realmRoom == null) {
            realmRoom = new RealmRoom();
        }
        realmRoom.setColor(room.getColor());
        realmRoom.setId(room.getId());
        realmRoom.setInitials(room.getInitials());
        realmRoom.setTitle(room.getTitle());
        realmRoom.setType(RoomType.convert(room.getType()));
        realmRoom.setUnreadCount(room.getUnreadCount());
        realmRoom.setReadOnly(room.getReadOnly());
        realmRoom.setMute(
                false); //TODO [Saeed Mozaffari] [2016-09-07 9:59 AM] - agar mute ro az server
        // gereftim be jaye false sabt mikonim
        switch (room.getType()) {
            case CHANNEL:
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setChannelRoom(
                        RealmChannelRoom.convert(room.getChannelRoom(), realmRoom.getChannelRoom(),
                                realm));
                realmRoom.setAvatar(RealmAvatar.convert(room, realm));
                break;
            case CHAT:
                realmRoom.setType(RoomType.CHAT);
                realmRoom.setChatRoom(RealmChatRoom.convert(room.getChatRoom()));
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                        .equalTo(RealmRegisteredInfoFields.ID, room.getChatRoom().getPeer().getId())
                        .findFirst();
                realmRoom.setAvatar(
                        realmRegisteredInfo != null ? realmRegisteredInfo.getLastAvatar() : null);
                break;
            case GROUP:
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setGroupRoom(
                        RealmGroupRoom.convert(room.getGroupRoom(), realmRoom.getGroupRoom(), realm));
                realmRoom.getGroupRoom().setDescription(room.getGroupRoom().getDescription());
                realmRoom.setAvatar(RealmAvatar.convert(room, realm));
                break;
        }
        realmRoom.setLastMessageTime(room.getLastMessage().getUpdateTime());
        realmRoom.setLastMessage(room.getLastMessage().getMessage());
        realmRoom.setLastMessageId(room.getLastMessage().getMessageId());
        realmRoom.setLastMessageStatus(room.getLastMessage().getStatus().toString());

        RealmRoomDraft realmRoomDraft = realmRoom.getDraft();
        if (realmRoomDraft == null) {
            realmRoomDraft = realm.createObject(RealmRoomDraft.class);
        }
        realmRoomDraft.setMessage(room.getDraft().getMessage());
        realmRoomDraft.setReplyToMessageId(room.getDraft().getReplyTo());

        realmRoom.setDraft(realmRoomDraft);

        return realmRoom;
    }

    private static void putChatToClientCondition(final ProtoGlobal.Room room, Realm realm) {

        if (realm.where(RealmClientCondition.class)
                .equalTo(RealmClientConditionFields.ROOM_ID, room.getId())
                .findFirst() == null) {
            RealmClientCondition realmClientCondition =
                    realm.createObject(RealmClientCondition.class);
            realmClientCondition.setRoomId(room.getId());
        }
    }

    public static void convertAndSetDraft(final long roomId, final String message,
                                          final long replyToMessageId) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom =
                        realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                RealmRoomDraft realmRoomDraft = realm.createObject(RealmRoomDraft.class);
                realmRoomDraft.setMessage(message);
                realmRoomDraft.setReplyToMessageId(replyToMessageId);

                realmRoom.setDraft(realmRoomDraft);

                if (G.onDraftMessage != null) {
                    G.onDraftMessage.onDraftMessage(roomId, message);
                }
            }
        });

        realm.close();
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(int lastMessageTime) {
        this.lastMessageTime = lastMessageTime * DateUtils.SECOND_IN_MILLIS;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RoomType getType() {
        return (type != null) ? RoomType.valueOf(type) : null;
    }

    public void setType(RoomType type) {
        this.type = type.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getUnreadCount() {
        return unread_count;
    }

    public void setUnreadCount(int unread_count) {
        this.unread_count = unread_count;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean getMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public RealmChatRoom getChatRoom() {
        return chat_room;
    }

    public void setChatRoom(RealmChatRoom chat_room) {
        this.chat_room = chat_room;
    }

    public RealmGroupRoom getGroupRoom() {
        return group_room;
    }

    public void setGroupRoom(RealmGroupRoom group_room) {
        this.group_room = group_room;
    }

    public RealmChannelRoom getChannelRoom() {
        return channel_room;
    }

    public void setChannelRoom(RealmChannelRoom channel_room) {
        this.channel_room = channel_room;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageStatus() {
        return lastMessageStatus;
    }

    public void setLastMessageStatus(String lastMessageStatus) {
        this.lastMessageStatus = lastMessageStatus;
    }

    public RealmRoomDraft getDraft() {
        return draft;
    }

    public void setDraft(RealmRoomDraft draft) {
        this.draft = draft;
    }

    public RealmDraftFile getDraftFile() {
        return draftFile;
    }

    public void setDraftFile(RealmDraftFile draftFile) {
        this.draftFile = draftFile;
    }

    public RealmAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(RealmAvatar avatar) {
        this.avatar = avatar;
    }
}
