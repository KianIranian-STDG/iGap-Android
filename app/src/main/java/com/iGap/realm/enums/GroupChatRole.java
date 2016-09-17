package com.iGap.realm.enums;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/31/2016.
 */
public enum GroupChatRole {
    MEMBER,
    MODERATOR,
    ADMIN,
    OWNER;

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
}
