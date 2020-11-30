/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.realm;

import net.iGap.helper.HelperString;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmGroupRoom extends RealmObject {
    public String role;
    public int participants_count;
    public String participants_count_label;
    public String participants_count_limit_label;
    public String description;
    public int avatarCount;
    public RealmNotificationSetting realmNotificationSetting;
    public RealmList<RealmMember> members;
    public String invite_link;
    public String invite_token;
    public boolean isPrivate;
    public String username;
    public int startFrom;


    public static RealmGroupRoom putIncomplete(Realm realm, ProtoGlobal.GroupRoom.Role role, String description, String participantsCountLabel) {
        RealmGroupRoom realmGroupRoom = realm.createObject(RealmGroupRoom.class);
        if (role == ProtoGlobal.GroupRoom.Role.OWNER) {
            realmGroupRoom.setRole(GroupChatRole.OWNER);
        } else {
            realmGroupRoom.setRole(GroupChatRole.MEMBER);
        }
        realmGroupRoom.setDescription(description);
        realmGroupRoom.setParticipantsCountLabel(participantsCountLabel);
        return realmGroupRoom;
    }

    /**
     * convert ProtoGlobal.GroupRoom to RealmGroupRoom
     *
     * @param room ProtoGlobal.GroupRoom
     * @return RealmGroupRoom
     */
    public static RealmGroupRoom putOrUpdate(ProtoGlobal.GroupRoom room, RealmGroupRoom realmGroupRoom, Realm realm) {
        if (realmGroupRoom == null) {
            realmGroupRoom = realm.createObject(RealmGroupRoom.class);
        }
        realmGroupRoom.setRole(GroupChatRole.convert(room.getRole()));
        realmGroupRoom.setParticipants_count(room.getParticipantsCount());
        realmGroupRoom.setParticipantsCountLabel(room.getParticipantsCountLabel());
        realmGroupRoom.setDescription(room.getDescription());
        if (!room.getPrivateExtra().getInviteLink().isEmpty()) {
            realmGroupRoom.setInvite_link(room.getPrivateExtra().getInviteLink());
        }
        realmGroupRoom.setInvite_token(room.getPrivateExtra().getInviteToken());
        realmGroupRoom.setUsername(room.getPublicExtra().getUsername());
        return realmGroupRoom;
    }

    public static void revokeLink(long roomId, final String inviteLink, final String inviteToken) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                final RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                if (realmGroupRoom != null) {
                    realmGroupRoom.setInvite_link(inviteLink);
                    realmGroupRoom.setInvite_token(inviteToken);
                }
            }
        });
    }

    public static ProtoGlobal.GroupRoom.Role detectMineRole(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            ProtoGlobal.GroupRoom.Role role = ProtoGlobal.GroupRoom.Role.UNRECOGNIZED;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                if (realmGroupRoom != null) {
                    role = realmGroupRoom.getMainRole();
                }
            }
            return role;
        });

    }

    public static GroupChatRole detectMemberRole(long roomId, long messageSenderId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            GroupChatRole role = GroupChatRole.UNRECOGNIZED;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getGroupRoom() != null) {
                    RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();
                    for (RealmMember realmMember : realmMembers) {
                        if (realmMember.getPeerId() == messageSenderId) {
                            role = GroupChatRole.valueOf(realmMember.getRole());
                        }
                    }
                }
            }
            return role;
        });
    }

    public static ProtoGlobal.GroupRoom.Role detectMemberRoleServerEnum(long roomId, long messageSenderId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            ProtoGlobal.GroupRoom.Role role = ProtoGlobal.GroupRoom.Role.UNRECOGNIZED;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getGroupRoom() != null) {
                    RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();
                    for (RealmMember realmMember : realmMembers) {
                        if (realmMember.getPeerId() == messageSenderId) {
                            role = ProtoGlobal.GroupRoom.Role.valueOf(realmMember.getRole());
                        }
                    }
                }
            }
            return role;
        });
    }

    public GroupChatRole getRole() {
        return (role != null) ? GroupChatRole.valueOf(role) : null;
    }

    //public String getParticipants_count_limit_label() {
    //    return participants_count_limit_label;
    //}
    //
    //public void setParticipants_count_limit_label(String participants_count_limit_label) {
    //    this.participants_count_limit_label = participants_count_limit_label;
    //}

    public void setRole(GroupChatRole role) {
        if (role == GroupChatRole.MODERATOR) {
            role = GroupChatRole.ADMIN;
        }
        this.role = role.toString();
    }

    public ProtoGlobal.GroupRoom.Role getMainRole() {
        return (role != null) ? ProtoGlobal.GroupRoom.Role.valueOf(role) : ProtoGlobal.GroupRoom.Role.UNRECOGNIZED;
    }

    public int getParticipants_count() {
        return participants_count;
    }

    public void setParticipants_count(int participants_count) {
        this.participants_count = participants_count;
    }

    public String getParticipantsCountLabel() {
        if (HelperString.isNumeric(participants_count_label)) {
            return participants_count_label;
        }
        return Integer.toString(getParticipants_count());
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

    public String getInvite_link() {
        return invite_link;
    }

    public void setInvite_link(String invite_link) {
        this.invite_link = "https://" + invite_link;
    }

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
        try {
            this.username = username;
        } catch (Exception e) {
            this.username = HelperString.getUtf8String(username);
        }
    }

    public int getStartFrom() {
        return startFrom;
    }

    public void setStartFrom(int startFrom) {
        this.startFrom = startFrom;
    }

    public static void setStartFrom(long id) {

    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
