package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.enums.GroupChatRole;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/31/2016.
 */
public class RealmGroupRoom extends RealmObject {
    private String role;
    private String participants_count_label;
    private boolean left;
    private RealmList<RealmMember> members;
    private String description;

    public GroupChatRole getRole() {
        return (role != null) ? GroupChatRole.valueOf(role) : null;
    }

    public void setRole(GroupChatRole role) {
        this.role = role.toString();
    }

    public String getParticipantsCountLabel() {
        return participants_count_label;
    }

    public void setParticipantsCountLabel(String participants_count_label) {
        this.participants_count_label = participants_count_label;
    }

    public boolean getLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public RealmList<RealmMember> getMembers() {
        return members;
    }

    public void setMembers(RealmList<RealmMember> members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        realmGroupRoom.setRole(GroupChatRole.convert(room.getRole()));
        return realmGroupRoom;
    }
}
