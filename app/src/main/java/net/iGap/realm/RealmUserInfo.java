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

import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperString;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestClientRegisterDevice;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmUserInfo extends RealmObject {

    private RealmRegisteredInfo userInfo;
    private boolean registrationStatus;
    private String email;
    private int gender;
    private int selfRemove;
    private String token;
    private String authorHash;
    private String pushNotificationToken;
    private String representPhoneNumber;
    private String accessToken;
    private boolean isWalletRegister;
    private boolean isWalletActive;
    private boolean isMplActive;
    private long walletAmount;
    private long ivandScore;
    private String nationalCode;


    public static RealmUserInfo getRealmUserInfo(Realm realm) {
        return realm.where(RealmUserInfo.class).findFirst();
    }

    public static long queryWalletAmount() {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo user = realm.where(RealmUserInfo.class).findFirst();
            if (user != null) {
                return user.getWalletAmount();
            }
            return 0L;
        });
    }

    public static RealmUserInfo putOrUpdate(Realm realm, long userId, String username, String phoneNumber, String token, String authorHash) {
        RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
        if (userInfo == null) {
            userInfo = realm.createObject(RealmUserInfo.class);
        }
        if (userInfo.getUserInfo() == null) {
            RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (registeredInfo == null) {
                registeredInfo = realm.createObject(RealmRegisteredInfo.class, userId);
            }
            userInfo.setUserInfo(registeredInfo);
        }
        userInfo.getUserInfo().setUsername(username);
        userInfo.getUserInfo().setPhoneNumber(phoneNumber);
        userInfo.setToken(token);
        userInfo.setAuthorHash(authorHash);
        userInfo.setUserRegistrationState(true);
        return userInfo;
    }

    public static RealmUserInfo putOrUpdate(Realm realm, ProtoGlobal.RegisteredUser registeredUser) {
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            realmUserInfo.setUserInfo(RealmRegisteredInfo.putOrUpdate(realm, registeredUser));
            realmUserInfo.getUserInfo().setDisplayName(registeredUser.getDisplayName());
            realmUserInfo.getUserInfo().setInitials(registeredUser.getInitials());
            realmUserInfo.getUserInfo().setColor(registeredUser.getColor());
            realmUserInfo.setUserRegistrationState(true);
        }
        return realmUserInfo;
    }

    public static void setPushNotification(final String pushToken) {
        new Thread(() -> {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setPushNotificationToken(pushToken);
                } else {
                    realmUserInfo = realm.createObject(RealmUserInfo.class);
                    realmUserInfo.setPushNotificationToken(pushToken);
                }
            });
        }).start();

    }

    public static void sendPushNotificationToServer() {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                String token = realmUserInfo.getPushNotificationToken();
                if (token != null && token.length() > 0) {
                    new RequestClientRegisterDevice().clientRegisterDevice(token);
                } else {
                    HelperLog.getInstance().setErrorLog(new Exception("FCM Token is Empty!" + token));
                }
            }
        });
    }

    public static void updateGender(final ProtoGlobal.Gender gender) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                realmUserInfo.setGender(gender);
            }
        });
    }

    public static void updateSelfRemove(final int selfRemove) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                realmUserInfo.setSelfRemove(selfRemove);
            }
        });
    }

    public static void updateEmail(final String email) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                realmUserInfo.setEmail(email);
            }
        });
    }

    public static void updateNickname(final String displayName, final String initials) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                RealmRegisteredInfo realmRegisteredInfo = realmUserInfo.getUserInfo();
                if (realmRegisteredInfo != null) {
                    realmRegisteredInfo.setDisplayName(displayName);
                    realmRegisteredInfo.setInitials(initials);
                }
            }
        });
    }

    public static void updateUsername(final String username) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                RealmRegisteredInfo realmRegisteredInfo = realmUserInfo.getUserInfo();
                if (realmRegisteredInfo != null) {
                    realmRegisteredInfo.setUsername(username);
                }
            }
        });
    }

    public RealmRegisteredInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(RealmRegisteredInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean getUserRegistrationState() {
        return this.registrationStatus;
    }

    public void setUserRegistrationState(boolean value) {
        this.registrationStatus = value;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {

        try {
            this.email = email;
        } catch (Exception e) {
            this.email = HelperString.getUtf8String(email);
        }

    }

    public ProtoGlobal.Gender getGender() {
        return ProtoGlobal.Gender.valueOf(this.gender);
    }

    public void setGender(ProtoGlobal.Gender value) {
        this.gender = value.getNumber();
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    public int getSelfRemove() {
        return selfRemove;
    }

    public void setSelfRemove(int selfRemove) {
        this.selfRemove = selfRemove;
    }

    public long getUserId() {
        return this.userInfo.getId();
    }

    public String getAuthorHash() {
        return authorHash;
    }

    public void setAuthorHash(String authorHash) {
        this.authorHash = authorHash;
    }

    public boolean isAuthorMe(String author) {
        return author.equals(authorHash);
    }

    public String getPushNotificationToken() {
        return pushNotificationToken;
    }

    public void setPushNotificationToken(String pushNotificationToken) {
        this.pushNotificationToken = pushNotificationToken;
    }

    public String getRepresentPhoneNumber() {
        return representPhoneNumber;
    }

    public void setRepresentPhoneNumber(String representPhoneNumber) {
        this.representPhoneNumber = representPhoneNumber;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public static void setRepresentPhoneNumber(Realm realm, String representPhoneNumber) {
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        setRepresentPhoneNumber(realm, realmUserInfo, representPhoneNumber);
    }

    public static void setRepresentPhoneNumber(Realm realm, RealmUserInfo realmUserInfo, String representPhoneNumber) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realmUserInfo != null) {
                    realmUserInfo.setRepresentPhoneNumber(representPhoneNumber);
                }
            }
        });
    }

    public static String getCurrentUserAuthorHash() {
        return DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo realmUser = realm.where(RealmUserInfo.class).findFirst();
            if (realmUser != null) {
                return realmUser.getAuthorHash();
            }
            return "";
        });
    }

    // Kuknos seed key save and get process

/*
    public RealmKuknos getKuknosM() {
        return kuknosM;
    }

    public void setKuknosM(RealmKuknos kuknosM) {
        this.kuknosM = kuknosM;
    }*/

    /*public static void updateKuknos(RealmKuknos kuknosM) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo != null) {
                realmUserInfo.setKuknosM(kuknosM);
            }
        });
    }*/

    public void createKuknos() {
/*        if (kuknosM == null) {
            new Thread(() -> {
                DbManager.getInstance().doRealmTransaction(realm -> {
                    RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    if (realmUserInfo != null) {
                        realmUserInfo.setKuknosM(realm.createObject(RealmKuknos.class));
                    }
                });
            }).start();
        }*/
    }

    public void deleteKuknos() {
/*        if (kuknosM != null) {
            new Thread(() -> {
                DbManager.getInstance().doRealmTransaction(realm -> {
                    RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    if (realmUserInfo != null) {
                        realmUserInfo.getKuknosM().deleteFromRealm();
                    }
                });
            }).start();
        }*/
    }

    public boolean isWalletRegister() {
        return isWalletRegister;
    }

    public void setWalletRegister(boolean walletRegister) {
        isWalletRegister = walletRegister;
    }

    public boolean isWalletActive() {
        return isWalletActive;
    }

    public void setWalletActive(boolean walletActive) {
        isWalletActive = walletActive;
    }

    public boolean isMplActive() {
        return isMplActive;
    }

    public void setMplActive(boolean mplActive) {
        isMplActive = mplActive;
    }

    public long getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(long walletAmount) {
        this.walletAmount = walletAmount;
    }

    public long getIvandScore() {
        return ivandScore;
    }

    public void setIvandScore(long ivandScore) {
        this.ivandScore = ivandScore;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getNationalCode() {
        return nationalCode;
    }
}
