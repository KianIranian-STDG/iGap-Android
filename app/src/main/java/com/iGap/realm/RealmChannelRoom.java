package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.enums.ChannelChatRole;

import io.realm.RealmObject;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/31/2016.
 */
public class RealmChannelRoom extends RealmObject {
    private String role;
    private String participants_count_label;

    public ChannelChatRole getRole() {
        return (role != null) ? ChannelChatRole.valueOf(role) : null;
    }

    public void setRole(ChannelChatRole role) {
        this.role = role.toString();
    }

    public String getParticipantsCountLabel() {
        return participants_count_label;
    }

    public void setParticipantsCountLabel(String participants_count_label) {
        this.participants_count_label = participants_count_label;
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
        realmChannelRoom.setRole(ChannelChatRole.convert(room.getRole()));
        return realmChannelRoom;
    }
}
