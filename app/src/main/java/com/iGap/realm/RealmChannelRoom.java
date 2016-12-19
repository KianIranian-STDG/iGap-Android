package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.enums.ChannelChatRole;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmChannelRoom extends RealmObject {
    private String role;
    private String participants_count_label;
    private String participants_count_limit_label;
    private String description;
    private String inviteLink;
    private int avatarCount;
    private RealmNotificationSetting realmNotificationSetting;
    private RealmList<RealmMember> members;
    //    private String invite_link;
    private String invite_token;
    private String username;

    /**
     * convert ProtoGlobal.ChannelRoom to RealmChannelRoom
     *
     * @param room ProtoGlobal.ChannelRoom
     * @return RealmChannelRoom
     */
    public static RealmChannelRoom convert(ProtoGlobal.ChannelRoom room,
                                           RealmChannelRoom realmChannelRoom, Realm realm) {
        if (realmChannelRoom == null) {
            realmChannelRoom = realm.createObject(RealmChannelRoom.class);
        }
        realmChannelRoom.setParticipantsCountLabel(room.getParticipantsCountLabel());
        realmChannelRoom.setRole(ChannelChatRole.convert(room.getRole()));
        realmChannelRoom.setInviteLink(room.getPrivateExtra().getInviteLink());
        realmChannelRoom.setInvite_token(room.getPrivateExtra().getInviteToken());
        realmChannelRoom.setUsername(room.getPublicExtra().getUsername());
        return realmChannelRoom;
    }

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

    public String getParticipants_count_limit_label() {
        return participants_count_limit_label;
    }

    public void setParticipants_count_limit_label(String participants_count_limit_label) {
        this.participants_count_limit_label = participants_count_limit_label;
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

    public RealmNotificationSetting getRealmNotificationSetting() {
        return realmNotificationSetting;
    }

    public void setRealmNotificationSetting(RealmNotificationSetting realmNotificationSetting) {
        this.realmNotificationSetting = realmNotificationSetting;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }

    public RealmList<RealmMember> getMembers() {
        return members;
    }

    public void setMembers(RealmList<RealmMember> members) {
        this.members = members;
    }

//    public String getInvite_link() {
//        return invite_link;
//    }
//
//    public void setInvite_link(String invite_link) {
//        this.invite_link = invite_link;
//    }

    public String getInvite_token() {
        return invite_token;
    }

    public void setInvite_token(String invite_token) {
        this.invite_token = invite_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
