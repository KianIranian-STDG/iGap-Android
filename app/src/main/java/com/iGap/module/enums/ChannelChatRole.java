package com.iGap.module.enums;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/31/2016.
 */
public enum ChannelChatRole {
    MEMBER,
    MODERATOR,
    ADMIN,
    OWNER;

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
