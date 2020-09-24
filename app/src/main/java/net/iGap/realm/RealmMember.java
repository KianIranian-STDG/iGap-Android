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

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoChannelGetMemberList;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class RealmMember extends RealmObject {

    @PrimaryKey
    private long id;

    private long peerId;
    private String role;

    public static RealmMember put(Realm realm, long userId, String role) {
        RealmMember realmMember = realm.createObject(RealmMember.class, SUID.id().get());
        realmMember.setRole(role);
        realmMember.setPeerId(userId);
        realmMember = realm.copyToRealm(realmMember);
        return realmMember;
    }

    public static RealmMember put(Realm realm, ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member) {
        RealmMember realmMember = realm.createObject(RealmMember.class, SUID.id().get());
        realmMember.setRole(member.getRole().toString());
        realmMember.setPeerId(member.getUserId());
        realmMember = realm.copyToRealm(realmMember);
        return realmMember;
    }

    public static RealmMember put(Realm realm, ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member) {
        RealmMember realmMember = realm.createObject(RealmMember.class, SUID.id().get());
        realmMember.setRole(member.getRole().toString());
        realmMember.setPeerId(member.getUserId());
        realmMember = realm.copyToRealm(realmMember);
        return realmMember;
    }

    public static void deleteAllMembers(Realm realm, long roomId, String selectedRole) {
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom != null) {
            if (realmRoom.getType() == GROUP) {
                if (realmRoom.getGroupRoom().getMembers() != null) {
                    if (!selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString())) {
                        realmRoom.getGroupRoom().getMembers().where().equalTo("role", selectedRole).findAll().deleteAllFromRealm();
                    } else {
                        realmRoom.getGroupRoom().getMembers().where().findAll().deleteAllFromRealm();
                    }
                }
            } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                if (realmRoom.getChannelRoom().getMembers() != null) {
                    if (!selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString())) {
                        realmRoom.getChannelRoom().getMembers().where().equalTo("role", selectedRole).findAll().deleteAllFromRealm();
                    } else {
                        realmRoom.getChannelRoom().getMembers().where().findAll().deleteAllFromRealm();
                    }
                }
            }
        }
    }

    public static void addMember(final long roomId, final long userId, final String role) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

            if (realmRoom != null) {
                RealmList<RealmMember> members = new RealmList<>();
                if (realmRoom.getType() == GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        members = realmGroupRoom.getMembers();
                    }
                } else {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        members = realmChannelRoom.getMembers();
                    }
                }
                members.add(RealmMember.put(realm, userId, role));
            }
        });
    }

    public static void updateMemberRole(final long roomId, final long memberId, final String role) {
        //TODO [Saeed Mozaffari] [2017-10-24 6:05 PM] - Can Write Better Code?
        DbManager.getInstance().doRealmTransaction(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                RealmList<RealmMember> realmMemberRealmList = null;
                if (realmRoom.getType() == GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        realmMemberRealmList = realmGroupRoom.getMembers();
                    }
                } else {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        realmMemberRealmList = realmChannelRoom.getMembers();
                    }
                }

                if (realmMemberRealmList != null) {
                    for (RealmMember member : realmMemberRealmList) {
                        if (member.getPeerId() == memberId) {
                            member.setRole(role);
                            break;
                        }
                    }
                }
            }
        });
    }

    public static void kickMember(final long roomId, final long userId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            kickMember(realm, roomId, userId);
        });
    }

    public static boolean kickMember(Realm realm, final long roomId, final long userId) {

        //test this <code>realmRoom.getGroupRoom().getMembers().where().equalTo("peerId", builder.getMemberId()).findFirst();</code> for kick member
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom != null) {
            RealmList<RealmMember> realmMembers = new RealmList<>();
            if (realmRoom.getType() == GROUP) {
                if (realmRoom.getGroupRoom() != null) {
                    realmMembers = realmRoom.getGroupRoom().getMembers();
                }
            } else {
                if (realmRoom.getChannelRoom() != null) {
                    realmMembers = realmRoom.getChannelRoom().getMembers();
                }
            }
            for (RealmMember realmMember : realmMembers) {
                if (realmMember.getPeerId() == userId) {
                    realmMember.deleteFromRealm();
                    return true;
                }
            }
        }
        return false;
    }

    public static RealmList<RealmMember> getMembers(Realm realm, long roomId) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        RealmList<RealmMember> memberList = new RealmList<>();
        if (realmRoom != null) {
            if (realmRoom.getType() == GROUP) {
                if (realmRoom.getGroupRoom() != null) {
                    memberList = realmRoom.getGroupRoom().getMembers();
                }
            } else {
                if (realmRoom.getChannelRoom() != null) {
                    memberList = realmRoom.getChannelRoom().getMembers();
                }
            }
        }
        return memberList;
    }

    public static void convertProtoMemberListToRealmMember(final ProtoChannelGetMemberList.ChannelGetMemberListResponse.Builder builder, final String identity) {
        final RealmList<RealmMember> newMembers = new RealmList<>();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DbManager.getInstance().doRealmTask(realm -> {
                    final List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> members = new ArrayList<>();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", Long.parseLong(identity)).findFirst();
                            if (realmRoom != null) {

                                members.clear();
                                newMembers.clear();
                                for (ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : builder.getMemberList()) {

                                    if (member.getRole().equals(ProtoGlobal.ChannelRoom.Role.ADMIN) || member.getRole().equals(ProtoGlobal.ChannelRoom.Role.OWNER))
                                        RealmRoomAccess.channelAdminPutOrUpdate(member.getAdminRights(), member.getUserId(), realmRoom.getId(), realm);

                                    final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, member.getUserId());
                                    if (realmRegisteredInfo != null) {
                                        newMembers.add(RealmMember.put(realm, member.getUserId(), member.getRole().toString()));
                                    } else {
                                        members.add(member);
                                    }
                                }

                                newMembers.addAll(0, realmRoom.getChannelRoom().getMembers());
                                realmRoom.getChannelRoom().setMembers(newMembers);
                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            if (G.onChannelGetMemberList != null) {
                                G.onChannelGetMemberList.onChannelGetMemberList(members);
                            }
                        }
                    });
                });

            }
        });
    }

    public static void convertProtoMemberListToRealmMember(final ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder builder, final String identity) {
        final RealmList<RealmMember> newMembers = new RealmList<>();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DbManager.getInstance().doRealmTask(realm -> {
                    final List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> members = new ArrayList<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member>();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", Long.parseLong(identity)).findFirst();
                            if (realmRoom != null) {

                                members.clear();
                                newMembers.clear();
                                for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : builder.getMemberList()) {

                                    if (member.getRole().equals(ProtoGlobal.GroupRoom.Role.ADMIN) || member.getRole().equals(ProtoGlobal.GroupRoom.Role.OWNER))
                                        RealmRoomAccess.groupAdminPutOrUpdate(member.getAdminRights(), member.getUserId(), realmRoom.getId(), realm);
                                    else if (member.getRole().equals(ProtoGlobal.GroupRoom.Role.MEMBER))
                                        RealmRoomAccess.groupMemberPutOrUpdate(member.getMemberRights(), member.getUserId(), realmRoom.getId(), realm);

                                    final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, member.getUserId());
                                    if (realmRegisteredInfo != null) {
                                        newMembers.add(RealmMember.put(realm, member.getUserId(), member.getRole().toString()));
                                    } else {
                                        members.add(member);
                                    }
                                }

                                newMembers.addAll(0, realmRoom.getGroupRoom().getMembers());
                                realmRoom.getGroupRoom().setMembers(newMembers);
                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            if (G.onGroupGetMemberList != null) {
                                G.onGroupGetMemberList.onGroupGetMemberList(members);
                            }
                        }
                    });
                });
            }
        });
    }

    public static RealmResults<RealmMember> filterMember(Realm realm, long roomId, @Nullable String filter, ArrayList<String> unSelectedRule, String selectedRole) {

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
        if (realmRoom == null) {
            return emptyResult(realm);
        }

        RealmResults<RealmMember> searchMember = emptyResult(realm);
        try {
            RealmQuery<RealmMember> query;
            if (realmRoom.getType() == GROUP) {
                query = realmRoom.getGroupRoom().getMembers().where();
            } else {
                query = realmRoom.getChannelRoom().getMembers().where();
            }

            if (!selectedRole.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString())) {
                query = query.equalTo("role", selectedRole);
            }

            for (String role : unSelectedRule) {
                query = query.notEqualTo("role", role);
            }

            if (filter == null || filter.length() == 0) {
                searchMember = query.findAll();
            } else {
                RealmResults<RealmRegisteredInfo> findMember = realm.where(RealmRegisteredInfo.class).contains("displayName", filter, Case.INSENSITIVE).findAll();
                for (int i = 0; i < findMember.size(); i++) {
                    if (i != 0 && i != (findMember.size() - 1)) {
                        query = query.or();
                    }

                    if (i == 0) {
                        query = query.beginGroup();
                    }

                    query = query.equalTo("peerId", findMember.get(i).getId());

                    if (i == (findMember.size() - 1)) {
                        query = query.endGroup();
                    }
                }

                if (findMember.size() > 0) {
                    searchMember = query.findAll();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchMember;
    }

    public static RealmResults<RealmMember> filterMember(Realm realm, long roomId, long userId) {

        RealmResults<RealmMember> searchMember = emptyResult(realm);

        try {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
            if (realmRoom != null) {
                RealmQuery<RealmMember> query;
                if (realmRoom.getType() == GROUP) {
                    query = realmRoom.getGroupRoom().getMembers().where();
                } else {
                    query = realmRoom.getChannelRoom().getMembers().where();
                }

                query = query.equalTo("peerId", userId);
                searchMember = query.findAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchMember;

    }

    /**
     * make empty result for avoid from null state
     */
    public static RealmResults<RealmMember> emptyResult(Realm realm) {
        return realm.where(RealmMember.class).equalTo("id", -1).findAll();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeerId() {
        return peerId;
    }

    public void setPeerId(long peerId) {
        this.peerId = peerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
