package com.iGap.realm;


import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.enums.GroupChatRole;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmGroupRoom extends RealmObject {
    private String role;
    private String participants_count_label;
    private String description;
    private int avatarCount;
    private RealmAvatar avatar;
    private RealmNotificationSetting realmNotificationSetting;
    private RealmList<RealmMember> members;

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
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvatarCount() {
        return avatarCount;
    }

    public void setAvatarCount(int avatarCount) {
        this.avatarCount = avatarCount;
    }

    public RealmAvatar getAvatar() {
        return avatar;
    }


    public void setAvatar(RealmAvatar avatar) {
        this.avatar = avatar;
    }

    public RealmNotificationSetting getRealmNotificationSetting() {
        return realmNotificationSetting;
    }

    public void setRealmNotificationSetting(RealmNotificationSetting realmNotificationSetting) {
        this.realmNotificationSetting = realmNotificationSetting;
    }

    public RealmList<RealmMember> getMembers() {
        return members;
    }

    public void setMembers(RealmList<RealmMember> members) {
        this.members = members;
    }

    /**
     * convert ProtoGlobal.GroupRoom to RealmGroupRoom
     *
     * @param room ProtoGlobal.GroupRoom
     * @return RealmGroupRoom
     */
    public static RealmGroupRoom convert(ProtoGlobal.GroupRoom room, RealmGroupRoom realmGroupRoom, Realm realm) {
        if (realmGroupRoom == null) {
            realmGroupRoom = realm.createObject(RealmGroupRoom.class);
        }
        realmGroupRoom.setRole(GroupChatRole.convert(room.getRole()));
        realmGroupRoom.setParticipantsCountLabel(room.getParticipantsCountLabel());
        realmGroupRoom.setDescription(room.getDescription());
        return realmGroupRoom;
    }
}
