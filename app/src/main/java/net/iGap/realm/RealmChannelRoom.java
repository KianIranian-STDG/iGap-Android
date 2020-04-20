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
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmChannelRoom extends RealmObject {
    private String role;
    private int participants_count;
    private String participants_count_label;
    private String participants_count_limit_label;
    private String description;
    private String inviteLink;
    private int avatarCount;
    private RealmNotificationSetting realmNotificationSetting;
    private RealmList<RealmMember> members;
    private String invite_token;
    private String username;
    private boolean isPrivate;
    private boolean isSignature = false;
    private long seenId;
    private boolean reactionStatus;
    private boolean verified;

    /**
     * convert ProtoGlobal.ChannelRoom to RealmChannelRoom
     *
     * @param room ProtoGlobal.ChannelRoom
     * @return RealmChannelRoom
     */
    public static RealmChannelRoom convert(ProtoGlobal.ChannelRoom room, RealmChannelRoom realmChannelRoom, Realm realm) {
        if (realmChannelRoom == null) {
            realmChannelRoom = realm.createObject(RealmChannelRoom.class);
        }
        realmChannelRoom.setParticipants_count(room.getParticipantsCount());
        realmChannelRoom.setParticipantsCountLabel(room.getParticipantsCountLabel());
        realmChannelRoom.setRole(ChannelChatRole.convert(room.getRole()));
        realmChannelRoom.setInviteLink(room.getPrivateExtra().getInviteLink());
        realmChannelRoom.setInvite_token(room.getPrivateExtra().getInviteToken());
        realmChannelRoom.setUsername(room.getPublicExtra().getUsername());
        return realmChannelRoom;
    }

    /**
     * create room with empty info , just Id and inviteLink
     *
     * @param roomId     roomId
     * @param inviteLink inviteLink
     */

    public static void createChannelRoom(final long roomId, final String inviteLink, final String channelName) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom == null) {
                realmRoom = realm.createObject(RealmRoom.class, roomId);
            }
            if (channelName != null) {
                realmRoom.setTitle(channelName);
            }
            realmRoom.setType(RoomType.CHANNEL);
            RealmChannelRoom realmChannelRoom = realm.createObject(RealmChannelRoom.class);
            realmChannelRoom.setInviteLink(inviteLink);
            realmChannelRoom.setRole(ChannelChatRole.MEMBER);// set default role

            realmRoom.setChannelRoom(realmChannelRoom);
        });
    }

    public static void revokeLink(long roomId, final String inviteLink, final String inviteToken) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                final RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom != null) {
                    realmChannelRoom.setInviteLink(inviteLink);
                    realmChannelRoom.setInvite_token(inviteToken);
                }
            }
        });
    }

    public static void removeUsername(final long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom != null) {
                    realmChannelRoom.setPrivate(true);
                }
            }
        });
    }

    public static ProtoGlobal.ChannelRoom.Role detectMineRole(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            ProtoGlobal.ChannelRoom.Role role = ProtoGlobal.ChannelRoom.Role.UNRECOGNIZED;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom != null) {
                    role = realmChannelRoom.getMainRole();
                }
            }
            return role;
        });
    }

    public static ChannelChatRole detectMemberRole(long roomId, long messageSenderId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            ChannelChatRole role = ChannelChatRole.UNRECOGNIZED;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getChannelRoom() != null) {
                    RealmList<RealmMember> realmMembers = realmRoom.getChannelRoom().getMembers();
                    for (RealmMember realmMember : realmMembers) {
                        if (realmMember.getPeerId() == messageSenderId) {
                            role = ChannelChatRole.valueOf(realmMember.getRole());
                        }
                    }
                }
            }
            return role;
        });
    }

    public static ProtoGlobal.ChannelRoom.Role detectMemberRoleServerEnum(long roomId, long messageSenderId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            ProtoGlobal.ChannelRoom.Role role = ProtoGlobal.ChannelRoom.Role.UNRECOGNIZED;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getChannelRoom() != null) {
                    RealmList<RealmMember> realmMembers = realmRoom.getChannelRoom().getMembers();
                    for (RealmMember realmMember : realmMembers) {
                        if (realmMember.getPeerId() == messageSenderId) {
                            role = ProtoGlobal.ChannelRoom.Role.valueOf(realmMember.getRole());
                        }
                    }
                }
            }
            return role;

        });
    }

    public static void updateReactionStatus(final long roomId, final boolean statusReaction) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null && realmRoom.getChannelRoom() != null) {
                realmRoom.getChannelRoom().setReactionStatus(statusReaction);
            }
        });
    }

    public ChannelChatRole getRole() {
        return (role != null) ? ChannelChatRole.valueOf(role) : null;
    }

    public void setRole(ChannelChatRole role) {
        if (role == ChannelChatRole.MODERATOR) {
            role = ChannelChatRole.ADMIN;
        }
        this.role = role.toString();
    }

    public ProtoGlobal.ChannelRoom.Role getMainRole() {
        return (role != null) ? ProtoGlobal.ChannelRoom.Role.valueOf(role) : ProtoGlobal.ChannelRoom.Role.UNRECOGNIZED;
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

    //public String getParticipants_count_limit_label() {
    //    return participants_count_limit_label;
    //}
    //
    //public void setParticipants_count_limit_label(String participants_count_limit_label) {
    //    this.participants_count_limit_label = participants_count_limit_label;
    //}

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
        this.inviteLink = "https://" + inviteLink;
    }

    public RealmList<RealmMember> getMembers() {
        return members;
    }

    public void setMembers(RealmList<RealmMember> members) {
        this.members = members;
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isSignature() {
        return isSignature;
    }

    public void setSignature(boolean signature) {
        isSignature = signature;
    }

    public long getSeenId() {
        return seenId;
    }

    public void setSeenId(long seenId) {
        this.seenId = seenId;
    }

    public boolean isReactionStatus() {
        return reactionStatus;
    }

    public void setReactionStatus(boolean reactionStatus) {
        this.reactionStatus = reactionStatus;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
