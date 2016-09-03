package com.iGap.helper;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmChatRoom;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.enums.ChannelChatRole;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.realm.enums.RoomType;

/**
 * helper methods while working with Realm
 * note: when any field of classes was changed, update this helper.
 */
public class HelperRealm {
    private HelperRealm() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    /**
     * convert ProtoGlobal.Room to RealmRoom for saving into database
     *
     * @param room ProtoGlobal.Room
     * @return RealmRoom
     */
    public static RealmRoom convert(ProtoGlobal.Room room) {
        RealmRoom realmRoom = new RealmRoom();
        realmRoom.setColor(room.getColor());
        realmRoom.setId(room.getId());
        realmRoom.setInitials(room.getInitials());
        realmRoom.setTitle(room.getTitle());
        realmRoom.setType(convert(room.getType()));
        realmRoom.setUnreadCount(room.getUnreadCount());
        switch (room.getType()) {
            case CHANNEL:
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setChannelRoom(convert(room.getChannelRoom()));
                break;
            case CHAT:
                realmRoom.setType(RoomType.CHAT);
                realmRoom.setChatRoom(convert(room.getChatRoom()));
                break;
            case GROUP:
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setGroupRoom(convert(room.getGroupRoom()));
                break;
        }

        return realmRoom;
    }

    /**
     * convert ProtoGlobal.Room.Type to RoomType
     *
     * @param type ProtoGlobal.Room.Type
     * @return RoomType
     */
    public static RoomType convert(ProtoGlobal.Room.Type type) {
        switch (type) {
            case CHANNEL:
                return RoomType.CHANNEL;
            case CHAT:
                return RoomType.CHAT;
            case GROUP:
                return RoomType.GROUP;
            default:
                return null;
        }
    }

    /**
     * convert ProtoGlobal.ChannelRoom to RealmChannelRoom
     *
     * @param room ProtoGlobal.ChannelRoom
     * @return RealmChannelRoom
     */
    public static RealmChannelRoom convert(ProtoGlobal.ChannelRoom room) {
        RealmChannelRoom realmChannelRoom = new RealmChannelRoom();
        realmChannelRoom.setParticipantsCountLabel(room.getParticipantsCountLabel());
        realmChannelRoom.setRole(convert(room.getRole()));
        return realmChannelRoom;
    }

    /**
     * convert ProtoGlobal.GroupRoom to RealmGroupRoom
     *
     * @param room ProtoGlobal.GroupRoom
     * @return RealmGroupRoom
     */
    public static RealmGroupRoom convert(ProtoGlobal.GroupRoom room) {
        RealmGroupRoom realmGroupRoom = new RealmGroupRoom();
        realmGroupRoom.setParticipantsCountLabel(room.getParticipantsCountLabel());
        realmGroupRoom.setLeft(room.getLeft());
        realmGroupRoom.setRole(convert(room.getRole()));
        return realmGroupRoom;
    }

    /**
     * convert ProtoGlobal.GroupRoom.Role to GroupChatRole
     *
     * @param role ProtoGlobal.GroupRoom.Role
     * @return GroupChatRole
     */
    public static GroupChatRole convert(ProtoGlobal.GroupRoom.Role role) {
        switch (role) {
            case ADMIN:
                return GroupChatRole.ADMIN;
            case MEMBER:
                return GroupChatRole.MEMBER;
            case MODERATOR:
                return GroupChatRole.MODERATOR;
            case OWNER:
                return GroupChatRole.OWNER;
            default:
                return null;
        }
    }

    /**
     * convert ProtoGlobal.ChatRoom to RealmChatRoom
     *
     * @param room ProtoGlobal.ChatRoom
     * @return RealmChatRoom
     */
    public static RealmChatRoom convert(ProtoGlobal.ChatRoom room) {
        RealmChatRoom realmChatRoom = new RealmChatRoom();
        realmChatRoom.setPeerId(room.getPeerId());
        return realmChatRoom;
    }

    /**
     * convert ProtoGlobal.ChannelRoom.Role to ChannelChatRole
     *
     * @param role ProtoGlobal.ChannelRoom.Role
     * @return ChannelChatRole
     */
    public static ChannelChatRole convert(ProtoGlobal.ChannelRoom.Role role) {
        switch (role) {
            case ADMIN:
                return ChannelChatRole.ADMIN;
            case MEMBER:
                return ChannelChatRole.MEMBER;
            case MODERATOR:
                return ChannelChatRole.MODERATOR;
            case OWNER:
                return ChannelChatRole.OWNER;
            default:
                return null;
        }
    }
}
