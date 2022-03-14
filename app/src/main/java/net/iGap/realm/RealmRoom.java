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

import android.text.format.DateUtils;

import net.iGap.G;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperString;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.observers.interfaces.OnClientGetRoomMessage;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestChannelUpdateDraft;
import net.iGap.request.RequestChatUpdateDraft;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestClientGetRoomMessage;
import net.iGap.request.RequestGroupUpdateDraft;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class RealmRoom extends RealmObject {
    @PrimaryKey
    public long id;
    public String type;
    public String title;
    public String initials;
    public String color;
    public int unreadCount;
    public boolean readOnly;
    public RealmChatRoom chatRoom;
    public boolean mute;
    public RealmGroupRoom groupRoom;
    public RealmChannelRoom channelRoom;
    public RealmRoomMessage lastMessage;
    public RealmRoomMessage firstUnreadMessage;
    public RealmRoomDraft draft;
    public RealmDraftFile draftFile;
    public RealmAvatar avatar;
    public long updatedTime;
    public String sharedMediaCount = "";
    //if it was needed in the future we can combine this two under fields in RealmAction (actionStateUserId and actionState).
    public long actionStateUserId;
    public String actionState;
    public boolean isDeleted = false;
    public boolean isPinned;
    public long pinId;
    public long pinMessageId;
    public long pinDocumentId;
    public long pinMessageIdDeleted;
    public long pinMessageDocumentIdDeleted;
    public int priority;
    public boolean isFromPromote;
    public long promoteId;

    public long getPromoteId() {
        return promoteId;
    }

    public void setPromoteId(long promoteId) {
        this.promoteId = promoteId;
    }

    public boolean isFromPromote() {
        return isFromPromote;
    }

    public void setFromPromote(boolean fromPromote) {
        isFromPromote = fromPromote;
    }

    /**
     * client need keepRoom info for show in forward message that forward
     * from a room that user don't have that room
     */
    private boolean keepRoom = false;
    private long lastScrollPositionMessageId;
    private long lastScrollPositionDocumentId;
    private int lastScrollPositionOffset;

    public RealmRoom() {

    }

    public RealmRoom(long id) {
        this.id = id;
    }

    public static RealmRoom getRealmRoom(Realm realm, long roomId) {
        return realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
    }

    public static void putOrUpdate(final ProtoGlobal.Room room) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            putOrUpdate(room, realm);
        });
    }

    /**
     * convert ProtoGlobal.Room to RealmRoom for saving into database
     * hint : call this method in execute transaction
     *
     * @param room ProtoGlobal.Room
     * @return RealmRoom
     */
    public static RealmRoom putOrUpdate(ProtoGlobal.Room room, Realm realm) {

        RealmClientCondition.putOrUpdateIncomplete(realm, room.getId());

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", room.getId()).findFirst();

        if (realmRoom == null) {
            realmRoom = realm.createObject(RealmRoom.class, room.getId());
        }


        realmRoom.isDeleted = false;
        realmRoom.keepRoom = false;

        realmRoom.setColor(room.getColor());
        realmRoom.setInitials(room.getInitials());
        realmRoom.setTitle(room.getTitle());
        realmRoom.setType(RoomType.convert(room.getType()));
        realmRoom.setUnreadCount(room.getUnreadCount());
        realmRoom.setReadOnly(room.getReadOnly());
        realmRoom.setMute(room.getRoomMute());
        realmRoom.setPriority(room.getPriority());
        realmRoom.setPinId(room.getPinId());

        if (room.getPinId() > 0) {
            realmRoom.setPinned(true);
        } else {
            realmRoom.setPinned(false);
        }


        if (room.getPinnedMessage() != null) {
            realmRoom.setPinMessageId(room.getPinnedMessage().getMessageId());
            realmRoom.setPinDocumentId(room.getPinnedMessage().getDocumentId());
        }

        realmRoom.setActionState(null, 0);
        switch (room.getType()) {
            case CHANNEL:
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setChannelRoom(RealmChannelRoom.convert(room.getChannelRoomExtra(), realmRoom.getChannelRoom(), realm));
                realmRoom.getChannelRoom().setDescription(room.getChannelRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, realmRoom.getId(), room.getChannelRoomExtra().getAvatar()));
                realmRoom.getChannelRoom().setInviteLink(room.getChannelRoomExtra().getPrivateExtra().getInviteLink());
                realmRoom.getChannelRoom().setInvite_token(room.getChannelRoomExtra().getPrivateExtra().getInviteToken());
                realmRoom.getChannelRoom().setUsername(room.getChannelRoomExtra().getPublicExtra().getUsername());
                realmRoom.getChannelRoom().setSeenId(room.getChannelRoomExtra().getSeenId());
                realmRoom.getChannelRoom().setPrivate(room.getChannelRoomExtra().hasPrivateExtra());
                realmRoom.getChannelRoom().setVerified(room.getChannelRoomExtra().getVerified());
                realmRoom.getChannelRoom().setReactionStatus(room.getChannelRoomExtra().getReactionStatus());

                if (room.getPermission() != null) {
                    RealmRoomAccess.putOrUpdate(room.getPermission(), AccountManager.getInstance().getCurrentUser().getId(), room.getId(), realm);
                }

                break;
            case CHAT:
                realmRoom.setType(RoomType.CHAT);
                realmRoom.setChatRoom(RealmChatRoom.convert(realm, room.getChatRoomExtra()));
                /**
                 * update user info for detect current status(online,offline,...)
                 * and also update another info
                 */

                RealmRegisteredInfo.putOrUpdate(realm, room.getChatRoomExtra().getPeer());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, room.getChatRoomExtra().getPeer().getId(), room.getChatRoomExtra().getPeer().getAvatar()));
                break;
            case GROUP:
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setGroupRoom(RealmGroupRoom.putOrUpdate(room.getGroupRoomExtra(), realmRoom.getGroupRoom(), realm));
                realmRoom.getGroupRoom().setDescription(room.getGroupRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, realmRoom.getId(), room.getGroupRoomExtra().getAvatar()));
                realmRoom.getGroupRoom().setInvite_token(room.getGroupRoomExtra().getPrivateExtra().getInviteToken());
                if (!room.getGroupRoomExtra().getPrivateExtra().getInviteLink().isEmpty()) {
                    realmRoom.getGroupRoom().setInvite_link(room.getGroupRoomExtra().getPrivateExtra().getInviteLink());
                }
                realmRoom.getGroupRoom().setUsername(room.getGroupRoomExtra().getPublicExtra().getUsername());
                realmRoom.getGroupRoom().setPrivate(room.getGroupRoomExtra().hasPrivateExtra());

                if (room.getGroupRoomExtra().getRoomRights() != null) {
                    RealmRoomAccess.groupMemberPutOrUpdate(room.getGroupRoomExtra().getRoomRights(), 0, room.getId(), realm);
                }

                if (room.getPermission() != null) {
                    RealmRoomAccess.putOrUpdate(room.getPermission(), AccountManager.getInstance().getCurrentUser().getId(), room.getId(), realm);
                }

                break;
        }

        /**
         * set setFirstUnreadMessage
         */
        if (room.hasFirstUnreadMessage()) {
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(realm, room.getId(), room.getFirstUnreadMessage(), new StructMessageOption());
            realmRoomMessage.setFutureMessageId(room.getFirstUnreadMessage().getMessageId());
            realmRoomMessage.setPreviousMessageId(room.getFirstUnreadMessage().getMessageId());
            realmRoom.setFirstUnreadMessage(realmRoomMessage);
        }

        if (room.hasLastMessage()) {
            /**
             * if this message not exist set gap otherwise don't change in gap state
             */
            boolean setGap = false;
            if (!RealmRoomMessage.existMessageInRoom(room.getLastMessage().getMessageId(), room.getId())) {
                setGap = true;
            }
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(realm, room.getId(), room.getLastMessage(), new StructMessageOption());
            if (setGap) {
                realmRoomMessage.setPreviousMessageId(room.getLastMessage().getMessageId());
                realmRoomMessage.setFutureMessageId(room.getLastMessage().getMessageId());
            }
            realmRoom.setLastMessage(realmRoomMessage);
            if (room.getLastMessage().getUpdateTime() == 0) {
                realmRoom.setUpdatedTime(room.getLastMessage().getCreateTime() * (DateUtils.SECOND_IN_MILLIS));
            } else {
                realmRoom.setUpdatedTime(room.getLastMessage().getUpdateTime() * (DateUtils.SECOND_IN_MILLIS));
            }
        }

        realmRoom.setDraft(RealmRoomDraft.putOrUpdate(realm, realmRoom.getDraft(), room.getDraft().getMessage(), room.getDraft().getReplyTo(), room.getDraft().getDraftTime()));

        return realmRoom;
    }

    /**
     * put fetched chat to database
     *
     * @param rooms ProtoGlobal.Room
     */
    public static void putChatToDatabase(final List<ProtoGlobal.Room> rooms, long offset, long size) {

        /**
         * (( hint : i don't used from mRealm instance ,because i have an error
         * that realm is closed, and for avoid from that error i used from
         * new instance for this action ))
         */
        DbManager.getInstance().doRealmTransaction(realm -> {
            for (int i = 0; i < rooms.size(); i++) {
                RealmRoom.putOrUpdate(rooms.get(i), realm);
            }
        });
    }

    public static void convertAndSetDraft(final long roomId, final String message, final long replyToMessageId, int draftTime) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setDraft(RealmRoomDraft.put(realm, message, replyToMessageId, draftTime));
                if (!message.isEmpty() && draftTime != 0) {
                    realmRoom.setUpdatedTime(draftTime * (DateUtils.SECOND_IN_MILLIS));
                }
                if (realmRoom.getDraft() == null) {
                    realmRoom.setDraft(RealmRoomDraft.put(realm, message, replyToMessageId, draftTime));
                } else {
                    realmRoom.setDraft(RealmRoomDraft.putOrUpdate(realm, realmRoom.getDraft(), message, replyToMessageId, draftTime));
                }
            }
        });
    }

    /**
     * create RealmRoom without info ,just have roomId and type
     * use this for detect that a room is a private channel
     * set deleted true and keep true for not showing in room list
     * and keep info for use in another subjects
     */
    public static void createEmptyRoom(final long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom == null) {
                realmRoom = realm.createObject(RealmRoom.class, roomId);
            }
            realmRoom.setType(RoomType.CHANNEL);
            realmRoom.setTitle("private channel");
            realmRoom.setDeleted(true);
            realmRoom.setKeepRoom(true);
        });
    }

    public static void needGetRoom(long roomId) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom == null) {
                new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.justInfo);
            }
        });
    }

    /**
     * check with this roomId that room is showing in room list or no
     */
    public static boolean isMainRoom(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            boolean isMainRoom = false;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).equalTo("isDeleted", false).findFirst();
            if (realmRoom != null) {
                isMainRoom = true;
            }
            return isMainRoom;
        });
    }

    /**
     * check updater author for detect that updater is another device for
     * this account and finally update unread count if another account
     * was saw message for this room
     *
     * @param roomId     roomId for room that get update status from that
     * @param authorHash updater author hash
     */
    public static void clearUnreadCount(long roomId, String authorHash, ProtoGlobal.RoomMessageStatus messageStatus, long messageId) {
        if (RealmUserInfo.getCurrentUserAuthorHash().equals(authorHash) && messageStatus == ProtoGlobal.RoomMessageStatus.SEEN) {
            DbManager.getInstance().doRealmTask(realm -> {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                if (realmRoom != null && (realmRoom.getLastMessage() != null && realmRoom.getLastMessage().getMessageId() <= messageId)) {
                    realmRoom.setUnreadCount(0);
                }
            });
        }
    }

    public static void updateMineRole(long roomId, long memberId, final String role) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            if (memberId == AccountManager.getInstance().getCurrentUser().getId()) {
                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                if (realmRoom == null) {
                    return;
                }

                if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                    GroupChatRole mRole;
                    if (role.contains(GroupChatRole.ADMIN.toString())) {
                        mRole = GroupChatRole.ADMIN;
                    } else if (role.contains(GroupChatRole.MODERATOR.toString())) {
                        mRole = GroupChatRole.MODERATOR;
                    } else {
                        mRole = GroupChatRole.MEMBER;
                    }
                    if (realmRoom.getGroupRoom() != null) {
                        realmRoom.getGroupRoom().setRole(mRole);
                    }
                } else {
                    ChannelChatRole mRole;
                    if (role.contains(ChannelChatRole.ADMIN.toString())) {
                        mRole = ChannelChatRole.ADMIN;
                    } else if (role.contains(ChannelChatRole.MODERATOR.toString())) {
                        mRole = ChannelChatRole.MODERATOR;
                    } else {
                        mRole = ChannelChatRole.MEMBER;
                    }
                    if (realmRoom.getChannelRoom() != null) {
                        realmRoom.getChannelRoom().setRole(mRole);
                    }

                    updateReadOnlyChannel(mRole, realmRoom);
                }
            }
        });
    }

    private static void updateReadOnlyChannel(ChannelChatRole role, RealmRoom realmRoom) {
        switch (role) {
            case MODERATOR:
            case ADMIN:
            case OWNER:
                realmRoom.setReadOnly(false);
                break;
            default:
                realmRoom.setReadOnly(true);
                break;
        }
    }

    public static void updateMemberRole(final long roomId, final long userId, final String role) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                RealmList<RealmMember> realmMemberRealmList = null;
                if (realmRoom.getType() == GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        realmMemberRealmList = realmGroupRoom.getMembers();
                    }
                } else if (realmRoom.getType() == CHANNEL) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        realmMemberRealmList = realmChannelRoom.getMembers();
                    }
                }

                if (realmMemberRealmList != null) {
                    for (RealmMember member : realmMemberRealmList) {
                        if (member.getPeerId() == userId) {
                            member.setRole(role);
                            break;
                        }
                    }
                }
            }
        });
    }

    public static String getMemberCount(Realm realm, long roomId) {

        String memberCount = "";
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom != null) {
            if (realmRoom.getType() == GROUP) {
                memberCount = realmRoom.getGroupRoom().getParticipantsCountLabel();
            } else if (realmRoom.getType() == CHANNEL) {
                memberCount = realmRoom.getGroupRoom().getParticipantsCountLabel();
            }
        }

        return memberCount;
    }

    /**
     * delete room with transaction from realm and also delete all messages
     * from this room and finally delete RealmClientCondition
     */
    public static void deleteRoom(final long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.deleteFromRealm();
            }

            RealmClientCondition.deleteCondition(realm, roomId);
            RealmRoomMessage.deleteAllMessage(realm, roomId);
        });
    }

    public static void addOwnerToDatabase(long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getType() == CHANNEL) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        final RealmList<RealmMember> members = realmChannelRoom.getMembers();
                        members.add(RealmMember.put(realm, AccountManager.getInstance().getCurrentUser().getId(), ProtoGlobal.ChannelRoom.Role.OWNER.toString()));
                    }
                } else if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        final RealmList<RealmMember> members = realmGroupRoom.getMembers();
                        members.add(RealmMember.put(realm, AccountManager.getInstance().getCurrentUser().getId(), ProtoGlobal.GroupRoom.Role.OWNER.toString()));
                    }
                }
            }
        });
    }

    public static boolean showSignature(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            boolean signature = false;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().isSignature()) {
                signature = true;
            }
            return signature;
        });
    }

    /**
     * if room isn't exist get info from server
     */
    public static boolean needUpdateRoomInfo(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                return false;
            }
            new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.justInfo);
            return true;
        });
    }

    public static void updateChatTitle(final long userId, final String title) {// TODO [Saeed Mozaffari] [2017-10-24 3:36 PM] - Can Write Better Code?
        DbManager.getInstance().doRealmTransaction(realm -> {
            for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo("type", ProtoGlobal.Room.Type.CHAT.toString()).findAll()) {
                if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == userId) {
                    realmRoom.setTitle(title.trim());
                }
            }
        });
    }

    public static void updateMemberCount(long roomId, final ProtoGlobal.Room.Type roomType, final long memberCount) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (roomType == CHANNEL) {
                if (realmRoom != null && realmRoom.getChannelRoom() != null) {
                    realmRoom.getChannelRoom().setParticipantsCountLabel(memberCount + "");
                }
            } else {
                if (realmRoom != null && realmRoom.getGroupRoom() != null) {
                    realmRoom.getGroupRoom().setParticipantsCountLabel(memberCount + "");
                }
            }
        });
    }

    public static void updateMemberCount(final long roomId, final boolean plus) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            updateMemberCount(realm, roomId, plus);
        });
    }

    public static int updateMemberCount(Realm realm, final long roomId, final boolean plus) {
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom != null) {
            String participantsCountLabel;
            if (realmRoom.getType() == GROUP) {
                if (realmRoom.getGroupRoom() == null) {
                    return 0;
                }
                participantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
            } else {
                if (realmRoom.getChannelRoom() == null) {
                    return 0;
                }
                participantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
            }

            if (HelperString.isNumeric(participantsCountLabel)) {
                int memberCount = Integer.parseInt(participantsCountLabel);
                if (plus) {
                    memberCount++;
                } else {
                    memberCount--;
                }

                if (realmRoom.getType() == GROUP) {
                    realmRoom.getGroupRoom().setParticipantsCountLabel(memberCount + "");
                } else {
                    realmRoom.getChannelRoom().setParticipantsCountLabel(memberCount + "");
                }
                return memberCount;
            }
        }
        return 0;
    }

    public static void updatePin(final long roomId, final boolean pin, final long pinId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
            if (room != null) {
                room.setPinned(pin);
                room.setPinId(pinId);
            }
        });
    }

    public static void updateSignature(final long roomId, final boolean signature) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom != null) {
                    realmChannelRoom.setSignature(signature);
                }
            }
        });
    }

    public static void updateUsername(final long roomId, final String username) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getType() == GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        realmGroupRoom.setUsername(username);
                        realmGroupRoom.setPrivate(false);
                    }
                } else {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        realmChannelRoom.setUsername(username);
                        realmChannelRoom.setPrivate(false);
                    }
                }
            }
        });
    }

    /**
     * check exist chat room with userId(peerId) and set a value for notify room item
     */
    public static void updateChatRoom(final long userId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom room = realm.where(RealmRoom.class).equalTo("type", CHAT.toString()).equalTo("chatRoom.peer_id", userId).findFirst();
            if (room != null) {
                room.setReadOnly(room.getReadOnly());// set data for update room item
            }
        });
    }

    public static void updateTime(Realm realm, long roomId, long time) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom != null) {
            realmRoom.setUpdatedTime(time);
        }
    }

    public static void setPrivateInTransaction(Realm realm, final long roomId) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom != null) {
            if (realmRoom.getType() == GROUP) {
                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                if (realmGroupRoom != null) {
                    realmGroupRoom.setPrivate(true);
                }
            } else {
                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom != null) {
                    realmChannelRoom.setPrivate(true);
                }
            }
        }
    }

    public static void setPrivate(final long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            setPrivateInTransaction(realm, roomId);
        });
    }

    public static void setPrivate(final long roomId, Realm.Transaction.OnSuccess onSuccess) {
        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    setPrivateInTransaction(realm, roomId);
                }
            }, onSuccess);
        });
    }

    public static void setCountShearedMedia(final long roomId, final String count) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (room != null) {
                room.setSharedMediaCount(count);
            }
        });
    }

    public static RealmRoom setCount(Realm realm, final long roomId, final int count) {
        RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (room != null) {
            room.setUnreadCount(count);
        }
        return room;
    }

    public static RealmRoom removeFirstUnreadMessage(Realm realm, final long roomId) {
        RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (room != null) {
            room.setFirstUnreadMessage(null);
        }
        return room;
    }

    public static RealmRoom setCountWithCallBack(Realm realm, final long roomId, final int count) {
        RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (room != null) {
            room.setUnreadCount(count);
        }

        return room;
    }

    public static void setAction(final long roomId, final long userId, final String action) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setActionState(action, userId);
            }
        });
    }

    public static void clearAllScrollPositions() {
        DbManager.getInstance().doRealmTask(realm -> {
            for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                setLastScrollPosition(realm, realmRoom.id);
            }
        });
    }

    private static void setLastScrollPosition(Realm realm, long roomId) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom != null) {
            realmRoom.setLastScrollPositionMessageId(0);
            realmRoom.setLastScrollPositionDocumentId(0);
            realmRoom.setLastScrollPositionOffset(0);
        }
    }


    public static void setDraft(final long roomId, final String message, final long replyToMessageId, ProtoGlobal.Room.Type chatType) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

            if (realmRoom != null) {
                if (realmRoom.getDraft() == null || realmRoom.getDraft().getMessage() == null || !realmRoom.getDraft().getMessage().equals(message)) {
                    if (chatType == CHAT) {
                        new RequestChatUpdateDraft().chatUpdateDraft(roomId, message, replyToMessageId);
                    } else if (chatType == GROUP) {
                        new RequestGroupUpdateDraft().groupUpdateDraft(roomId, message, replyToMessageId);
                    } else if (chatType == CHANNEL) {
                        new RequestChannelUpdateDraft().channelUpdateDraft(roomId, message, replyToMessageId);
                    }
                }
            }
        });
    }

    public static void editRoom(final long roomId, final String title, final String description) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setTitle(title);
                if (realmRoom.getType() == GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        realmGroupRoom.setDescription(description);
                    }
                } else {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        realmChannelRoom.setDescription(description);
                    }
                }
            }
        });
    }

    public static void clearDraft(final long roomId) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                if (realmRoom != null && realmRoom.getLastMessage() != null) {
                    if (realmRoom.getLastMessage().getUpdateTime() == 0) {
                        realmRoom.setUpdatedTime(realmRoom.getLastMessage().getCreateTime());
                    } else {
                        realmRoom.setUpdatedTime(realmRoom.getLastMessage().getUpdateTime());
                    }
                }
            });
        }).start();

    }

    /**
     * clear all actions from RealmRoom for all rooms
     */
    public static void clearAllActions() {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                    realmRoom.setActionState(null, 0);
                }
            });
        }).start();
    }

    public static void joinRoom(final long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null && realmRoom.isValid()) {
                realmRoom.setDeleted(false);
                if (realmRoom.getType() == GROUP) {
                    realmRoom.setReadOnly(false);
                }
            } else {
                new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
            }
        });
    }

    public static void joinByInviteLink(long roomId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                    realmRoom.setReadOnly(false);
                }
                realmRoom.setDeleted(false);
            }
        });
    }

    public static boolean isNotificationServices(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            boolean isNotificationService = false;
            RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (room != null && room.getType() == CHAT && room.getChatRoom() != null) {
                RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, room.getChatRoom().getPeerId());
                if (realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.SERVICE_NOTIFICATIONS.toString())) {
                    isNotificationService = true;
                }
            }
            return isNotificationService;
        });
    }

    public static ProtoGlobal.Room.Type detectType(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            ProtoGlobal.Room.Type roomType = ProtoGlobal.Room.Type.UNRECOGNIZED;
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                roomType = realmRoom.getType();
            }
            return roomType;

        });
    }

    public static String detectTitle(long roomId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            String title = "";
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                title = realmRoom.getTitle();
            }
            return title;
        });
    }

    public static void setLastMessageWithRoomMessage(Realm realm, long roomId, RealmRoomMessage roomMessage) {
        if (roomMessage != null) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setLastMessage(roomMessage);
            }
        }
    }

    public static void setLastMessageWithRoomMessage(final long roomId, final RealmRoomMessage roomMessage) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            setLastMessageWithRoomMessage(realm, roomId, roomMessage);
        });
    }

    public static void setLastMessageAfterLocalDelete(final long roomId, final long messageId) { // FragmentChat, is need this method?
        //TODO [Saeed Mozaffari] [2017-10-23 9:38 AM] - Write Better Code
        DbManager.getInstance().doRealmTransaction(realm -> {
            try {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                RealmRoomMessage realmRoomMessage = null;
                RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo("edited", false).equalTo("roomId", roomId).lessThan("messageId", messageId).findAll();
                if (realmRoomMessages.size() > 0) {
                    realmRoomMessage = realmRoomMessages.last();
                }

                if (realmRoom != null && realmRoomMessage != null) {
                    realmRoom.setLastMessage(realmRoomMessage);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }

    public static void convertChatToGroup(final long roomId, final String title, final String description, final ProtoGlobal.GroupRoom.Role role) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setTitle(title);
                realmRoom.setGroupRoom(RealmGroupRoom.putIncomplete(realm, role, description, "2"));
                realmRoom.setChatRoom(null);
            }
        });
    }

    public static long getRoomIdByPeerId(long peerId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", peerId).findFirst();
            if (realmRoom != null) {
                return realmRoom.getId();
            }
            return 0L;
        });
    }

    public static void clearMessage(final long roomId, final long clearId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null && ((realmRoom.getLastMessage() == null) || (realmRoom.getLastMessage().getMessageId() <= clearId))) {
                realmRoom.setUnreadCount(0);
                realmRoom.setLastMessage(null);
            }
        });
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProtoGlobal.Room.Type getType() {
        return (isValid() && type != null) ? ProtoGlobal.Room.Type.valueOf(type) : null;
    }

    public void setType(RoomType type) {
        this.type = type.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        try {
            this.title = title;
        } catch (Exception e) {
            this.title = HelperString.getUtf8String(title);
        }
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
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
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

    public void setMute(ProtoGlobal.RoomMute muteState) {
        this.mute = muteState == ProtoGlobal.RoomMute.MUTE;
    }

    public RealmChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(RealmChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public RealmGroupRoom getGroupRoom() {
        return groupRoom;
    }

    public void setGroupRoom(RealmGroupRoom groupRoom) {
        this.groupRoom = groupRoom;
    }

    public RealmChannelRoom getChannelRoom() {
        return channelRoom;
    }

    public void setChannelRoom(RealmChannelRoom channelRoom) {
        this.channelRoom = channelRoom;
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

    public String getSharedMediaCount() {
        return sharedMediaCount;

        /*String[] countList = sharedMediaCount.split("\n");
        try {

            int countOFImage = Integer.parseInt(countList[0]);
            int countOFVIDEO = Integer.parseInt(countList[1]);
            int countOFAUDIO = Integer.parseInt(countList[2]);
            int countOFVOICE = Integer.parseInt(countList[3]);
            int countOFGIF = Integer.parseInt(countList[4]);
            int countOFFILE = Integer.parseInt(countList[5]);
            int countOFLink = Integer.parseInt(countList[6]);

            String result = "";

            if (countOFImage > 0)
                result += "\n" + countOFImage + " " + context.getString(R.string.shared_image);
            if (countOFVIDEO > 0)
                result += "\n" + countOFVIDEO + " " + context.getString(R.string.shared_video);
            if (countOFAUDIO > 0)
                result += "\n" + countOFAUDIO + " " + context.getString(R.string.shared_audio);
            if (countOFVOICE > 0)
                result += "\n" + countOFVOICE + " " + context.getString(R.string.shared_voice);
            if (countOFGIF > 0)
                result += "\n" + countOFGIF + " " + context.getString(R.string.shared_gif);
            if (countOFFILE > 0)
                result += "\n" + countOFFILE + " " + context.getString(R.string.shared_file);
            if (countOFLink > 0)
                result += "\n" + countOFLink + " " + context.getString(R.string.shared_links);

            result = result.trim();

            if (result.length() < 1) {
                result = context.getString(R.string.there_is_no_sheared_media);
            }

            return result;
        } catch (Exception e) {

            return sharedMediaCount;
        }*/
    }

    public void setSharedMediaCount(String sharedMediaCount) {
        this.sharedMediaCount = sharedMediaCount;
    }

    public String getActionState() {
        return actionState;
    }

    public void setActionState(String actionState, long userId) {
        this.actionState = actionState;
        this.actionStateUserId = userId;
    }

    public long getActionStateUserId() {
        return actionStateUserId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public long getPinId() {
        return pinId;
    }

    public void setPinId(long pinId) {
        this.pinId = pinId;
    }


    public long getPinMessageId() {
        return pinMessageId;
    }

    public void setPinMessageId(long pinMessageId) {
        this.pinMessageId = pinMessageId;
    }

    public long getPinDocumentId() {
        return pinDocumentId;
    }

    public void setPinDocumentId(long pinDocumentId) {
        this.pinDocumentId = pinDocumentId;
    }

    public long getPinMessageIdDeleted() {
        return pinMessageIdDeleted;
    }

    public void setPinMessageIdDeleted(long pinMessageIdDeleted) {
        this.pinMessageIdDeleted = pinMessageIdDeleted;
    }

    public long getPinMessageDocumentIdDeleted() {
        return pinMessageDocumentIdDeleted;
    }

    public void setPinMessageDocumentIdDeleted(long pinMessageDocumentIdDeleted) {
        this.pinMessageDocumentIdDeleted = pinMessageDocumentIdDeleted;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public static boolean isPinedMessage(long roomId, long messageId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            boolean result = false;
            RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
            if (room != null) {
                if (room.getPinMessageId() == messageId) {
                    result = true;
                }
            }
            return result;
        });
    }

    public static void updatePinedMessageDeleted(long roomId, final boolean reset) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
            if (room != null) {
                room.setPinMessageIdDeleted(reset ? 0 : room.getPinMessageId());
                room.setPinMessageDocumentIdDeleted(reset ? 0 : room.getPinDocumentId());
            }
        });
    }

    public static long hasPinedMessage(Realm realm, long roomId, boolean needDocumentId) {
        long result = 0;
        RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
        if (room != null) {
            if (room.getPinMessageId() > 0) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).
                        equalTo("messageId", room.getPinMessageId()).findFirst();
                if (roomMessage == null) {
                    new RequestClientGetRoomMessage().clientGetRoomMessage(roomId, room.getPinMessageId(), room.getPinDocumentId(), new OnClientGetRoomMessage() {
                        @Override
                        public void onClientGetRoomMessageResponse(ProtoGlobal.RoomMessage message) {
                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (G.onPinedMessage != null) {
                                        G.onPinedMessage.onPinMessage();
                                    }
                                }
                            }, 200);
                        }

                        @Override
                        public void onError(int majorCode, int minorCode) {

                        }
                    });
                } else {
                    RealmRoomMessage roomMessage1 = realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).
                            equalTo("messageId", room.getPinMessageId()).notEqualTo("messageId", room.getPinMessageIdDeleted()).
                            equalTo("deleted", false).equalTo("showMessage", true).findFirst();
                    if (roomMessage1 != null) {
                        if (needDocumentId) {
                            result = roomMessage1.getDocumentId();
                        } else {
                            result = roomMessage1.getMessageId();
                        }

                    }
                }
            }
        }
        return result;

    }

    public long getUpdatedTime() {
        if (getLastMessage() != null && getLastMessage().isValid()) {
            if (getLastMessage().getUpdateOrCreateTime() > updatedTime) {
                return getLastMessage().getUpdateOrCreateTime();
            }
        }
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isKeepRoom() {
        return keepRoom;
    }

    public void setKeepRoom(boolean keepRoom) {
        this.keepRoom = keepRoom;
    }

    public long getLastScrollPositionMessageId() {
        if (lastScrollPositionMessageId == 0 || !RealmRoomMessage.existMessageInRoom(lastScrollPositionMessageId, id))
            return 0;

        return lastScrollPositionMessageId;
    }

    public void setLastScrollPositionMessageId(long lastScrollPositionMessageId) {
        this.lastScrollPositionMessageId = lastScrollPositionMessageId;
    }

    public long getLastScrollPositionDocumentId() {
        return lastScrollPositionDocumentId;
    }

    public void setLastScrollPositionDocumentId(long lastScrollPositionDocumentId) {
        this.lastScrollPositionDocumentId = lastScrollPositionDocumentId;
    }

    public int getLastScrollPositionOffset() {
        return lastScrollPositionOffset;
    }

    public void setLastScrollPositionOffset(int lastScrollPositionOffset) {
        this.lastScrollPositionOffset = lastScrollPositionOffset;
    }

    public RealmRoomMessage getFirstUnreadMessage() {
        return firstUnreadMessage;
    }

    public void setFirstUnreadMessage(RealmRoomMessage firstUnreadMessage) {
        this.firstUnreadMessage = firstUnreadMessage;
    }

    public RealmRoomMessage getLastMessage() {
        return lastMessage;
    }

    public static void setLastMessage(final long roomId) {
        //TODO [Saeed Mozaffari] [2017-10-22 5:26 PM] - Write Better Code
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).findAll().sort("messageId", Sort.DESCENDING);
            if (realmRoomMessages.size() > 0 && realmRoomMessages.first() != null) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setLastMessage(realmRoomMessages.first());
                }
            }
        });
    }

    public void setLastMessage(RealmRoomMessage lastMessage) {
        if (lastMessage != null) {
            setUpdatedTime(lastMessage.getUpdateOrCreateTime());
        }
        this.lastMessage = lastMessage;
    }

    public long getOwnerId() {
        if (ProtoGlobal.Room.Type.valueOf(type) == CHAT) {
            return getChatRoom().getPeerId();
        }
        return id;
    }

    public static boolean isBot(long userId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (realmRegisteredInfo != null) {
                return realmRegisteredInfo.isBot();
            } else
                return false;
        });
    }

    public static String[] getUnreadCountPages() {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmResults<RealmRoom> results = realm.where(RealmRoom.class).equalTo("keepRoom", false).equalTo("mute", false).equalTo("isDeleted", false).findAll();
            int all = 0, chat = 0, group = 0, channel = 0;
            for (RealmRoom rm : results) {
                switch (rm.getType()) {
                    case CHANNEL:
                        channel += rm.getUnreadCount();
                        break;
                    case CHAT:
                        chat += rm.getUnreadCount();
                        break;
                    case GROUP:
                        group += rm.getUnreadCount();
                        break;
                }
                all += rm.getUnreadCount();
            }
            String[] ar;
            if (HelperCalander.isPersianUnicode) {
                ar = new String[]{"0", "0", all + ""};
            } else {
                ar = new String[]{all + "", "0", "0"};
            }
            return ar;
        });
    }

    public static boolean isPromote(Long id) {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", id).findFirst();
            if (realmRoom != null) {
                return realmRoom.isFromPromote();
            }
            return false;
        });
    }
}
