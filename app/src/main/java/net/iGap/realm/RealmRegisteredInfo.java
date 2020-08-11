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

import androidx.annotation.Nullable;

import net.iGap.G;
import net.iGap.helper.HelperString;
import net.iGap.module.AppUtils;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnInfo;
import net.iGap.observers.interfaces.OnRegistrationInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserInfo;

import java.util.concurrent.atomic.AtomicBoolean;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class RealmRegisteredInfo extends RealmObject {
    @PrimaryKey
    private long id;
    private String username;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String displayName;
    private String initials;
    private String color;
    private String status;
    private String cacheId;
    private int lastSeen;
    private int avatarCount;
    private String bio;
    private boolean verified;
    private boolean isBot;
    private boolean mutual;
    private boolean blockUser = false;
    private boolean DoNotshowSpamBar = false;

    public static RealmRegisteredInfo putOrUpdate(Realm realm, ProtoGlobal.RegisteredUser input) {
        RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, input.getId());
        if (registeredInfo == null) {
            registeredInfo = realm.createObject(RealmRegisteredInfo.class, input.getId());
            registeredInfo.setDoNotshowSpamBar(false);
        }

        registeredInfo.setUsername(input.getUsername());
        registeredInfo.setDisplayName(input.getDisplayName());
        registeredInfo.setStatus(input.getStatus().toString());
        registeredInfo.setAvatarCount(input.getAvatarCount());
        registeredInfo.setCacheId(input.getCacheId());
        registeredInfo.setColor(input.getColor());
        registeredInfo.setFirstName(input.getFirstName());
        registeredInfo.setInitials(input.getInitials());
        registeredInfo.setLastName(input.getLastName());
        registeredInfo.setLastSeen(input.getLastSeen());
        registeredInfo.setMutual(input.getMutual());
        registeredInfo.setPhoneNumber(Long.toString(input.getPhone()));
        registeredInfo.setUsername(input.getUsername());
        registeredInfo.setBio(input.getBio());
        registeredInfo.setVerified(input.getVerified());
        registeredInfo.setBot(input.getBot());

        return registeredInfo;
    }

    /**
     * compare user cacheId , if was equal don't do anything
     * otherwise send request for get user info
     *
     * @param userId  userId for get old cacheId from RealmRegisteredInfo
     * @param cacheId new cacheId
     * @return return true if need update otherwise return false
     */

    public static boolean needUpdateUser(long userId, String cacheId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);

            if (realmRegisteredInfo != null && cacheId != null && realmRegisteredInfo.getCacheId().equals(cacheId)) {
                return false;
            }
            new RequestUserInfo().userInfoAvoidDuplicate(userId);
            return true;
        });
    }

    /**
     * check info existence in realm if exist and cacheId is equal, will be returned;
     * otherwise send 'RequestUserInfo' to server and after get response will be called
     * 'OnInfo' for return 'RealmRegisteredInfo'
     *
     * @param onRegistrationInfo RealmRegisteredInfo will be returned with this interface
     */
    public static void getRegistrationInfo(long userId, @Nullable String cacheId, Realm realm, final OnInfo onRegistrationInfo) {

        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
        if (realmRegisteredInfo != null && (cacheId == null || realmRegisteredInfo.getCacheId().equals(cacheId))) {
            onRegistrationInfo.onInfo(realmRegisteredInfo.getId());
        } else {
            RequestUserInfo.infoHashMap.put(userId, onRegistrationInfo);
            G.onRegistrationInfo = new OnRegistrationInfo() {
                @Override
                public void onInfo(ProtoGlobal.RegisteredUser registeredInfo) {
                    DbManager.getInstance().doRealmTask(realm -> {
                        OnInfo InfoListener = RequestUserInfo.infoHashMap.get(registeredInfo.getId());
                        if (InfoListener != null) {
                            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, registeredInfo.getId());
                            if (realmRegisteredInfo != null) {
                                InfoListener.onInfo(realmRegisteredInfo.getId());
                            }
                        }
                        RequestUserInfo.infoHashMap.remove(registeredInfo.getId());
                    });
                }
            };
            new RequestUserInfo().userInfo(userId, RequestUserInfo.InfoType.JUST_INFO.toString());
        }

    }

    public static void getRegistrationInfo(long userId, final OnInfo onRegistrationInfo) {
        DbManager.getInstance().doRealmTask(realm -> {
            getRegistrationInfo(userId, null, realm, onRegistrationInfo);
        });
    }

    public static RealmRegisteredInfo getRegistrationInfo(Realm realm, long userId) {
        return realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
    }

    public static String getNameWithId(long userId) {
        return DbManager.getInstance().doRealmTask(realm -> {
            String displayName = "";
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
            if (realmRegisteredInfo != null) {
                displayName = realmRegisteredInfo.getDisplayName();
            }
            return displayName;
        });
    }

    public static void updateBio(long userId, final String bio) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRegisteredInfo.getRegistrationInfo(realm, userId).setBio(bio);
        });
    }

    public static void updateName(final long userId, final String firstName, final String lastName, final String initials) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (registeredInfo != null) {
                registeredInfo.setFirstName(firstName);
                registeredInfo.setLastName(lastName);
                registeredInfo.setDisplayName((firstName + " " + lastName).trim());
                registeredInfo.setInitials(initials);
            }
        });
    }

    public static void updateBlock(final long userId, final boolean block) {
        DbManager.getInstance().doRealmTask(realm -> {
            updateBlock(userId, block, realm);
        });
    }

    public static void updateBlock(final long userId, final boolean block, Realm realm) {

        final RealmRegisteredInfo registeredInfo = getRegistrationInfo(realm, userId);
        if (registeredInfo != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    registeredInfo.setBlockUser(block);
                }
            });
        }

    }

    public static void updateMutual(final String phone, final boolean mutual) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.PHONE_NUMBER, phone + "").findFirst();
            if (realmRegisteredInfo != null) {
                realmRegisteredInfo.setMutual(mutual);
            }
        });
    }

    public static boolean updateStatus(long userId, final int lastSeen, final String userStatus) {
        AtomicBoolean result = new AtomicBoolean(true);
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (realmRegisteredInfo != null) {
                if (userStatus.toLowerCase().equals("offline") && !realmRegisteredInfo.getOriginalStatus().toLowerCase().equals("online")) {
                    result.set(false);
                }

                realmRegisteredInfo.setStatus(userStatus);
                realmRegisteredInfo.setLastSeen(lastSeen);
            }

            result.set(true);
        });

        return result.get();
    }

    public static long getUserInfo(Realm realm, String phoneNumber) {
        if (realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.PHONE_NUMBER, phoneNumber).findFirst() != null) {
            return realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.PHONE_NUMBER, phoneNumber).findFirst().getId();
        } else {
            return 0;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        try {
            this.firstName = firstName;
        } catch (Exception e) {
            this.firstName = HelperString.getUtf8String(firstName);
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        try {
            this.lastName = lastName;
        } catch (Exception e) {
            this.lastName = HelperString.getUtf8String(lastName);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        try {
            this.displayName = displayName;
        } catch (Exception e) {
            this.displayName = HelperString.getUtf8String(displayName);
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

    public String getStatus() {
        return AppUtils.getStatsForUser(status);
    }

    public String getOriginalStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * this method will be returned exactly the value that got from server
     */
    public String getMainStatus() {
        return status;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    public int getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(int lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getAvatarCount() {
        return avatarCount;
    }

    public void setAvatarCount(int avatarCount) {
        this.avatarCount = avatarCount;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isBot() {
        return isBot;
    }

    public void setBot(boolean bot) {
        isBot = bot;
    }

    public boolean isMutual() {
        return mutual;
    }

    public void setMutual(boolean mutual) {
        this.mutual = mutual;
    }

    public boolean isBlockUser() {
        return blockUser;
    }

    public void setBlockUser(boolean blockUser) {
        this.blockUser = blockUser;
    }

    public boolean getDoNotshowSpamBar() {
        return DoNotshowSpamBar;
    }

    public void setDoNotshowSpamBar(boolean doNotshowSpamBar) {
        DoNotshowSpamBar = doNotshowSpamBar;
    }

    public RealmList<RealmAvatar> getAvatars(Realm realm) {
        RealmList<RealmAvatar> avatars = new RealmList<>();
        avatars.addAll(realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, id).findAll().sort(RealmAvatarFields.ID, Sort.ASCENDING));
        return avatars;
    }

    public RealmAvatar getLastAvatar(Realm realm) {
        RealmList<RealmAvatar> avatars = getAvatars(realm);
        if (avatars.isEmpty()) {
            return null;
        }
        // make sure return last avatar which has attachment
        for (int i = avatars.size() - 1; i >= 0; i--) {
            RealmAvatar avatar = getAvatars(realm).get(i);
            if (avatar.getFile() != null) {
                return avatar;
            }
        }
        return null;
    }

}
