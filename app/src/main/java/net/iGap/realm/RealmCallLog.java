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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.iGap.module.accountManager.DbManager;
import net.iGap.helper.HelperPreferences;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoSignalingGetLog;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class RealmCallLog extends RealmObject {

    @PrimaryKey
    private long id;
    private long logId;
    private String type;
    private String status;
    private RealmRegisteredInfo user;
    private int offerTime;
    private int duration;
    // log id

    private static void addLog(ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog callLog, Realm realm) {
        RealmCallLog realmCallLog = realm.where(RealmCallLog.class).equalTo(RealmCallLogFields.ID, callLog.getId()).findFirst();
        if (realmCallLog == null) {
            realmCallLog = realm.createObject(RealmCallLog.class, callLog.getId());
        }
        realmCallLog.setType(callLog.getType().toString());
        realmCallLog.setStatus(callLog.getStatus().toString());
        realmCallLog.setUser(RealmRegisteredInfo.putOrUpdate(realm, callLog.getPeer()));
        realmCallLog.setOfferTime(callLog.getOfferTime());
        realmCallLog.setDuration(callLog.getDuration());
        realmCallLog.setLogId(callLog.getLogId());
    }

    public static void addLogList(final List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> list) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            for (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog item : list) {
                addLog(item, realm);
            }
        });
    }

    public static void clearCallLog(final long clearId) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            realm.where(RealmCallLog.class).lessThanOrEqualTo(RealmCallLogFields.ID, clearId).findAll().deleteAllFromRealm();
        });
    }

    /**
     * this method shouldn't be async because at start of {@link net.iGap.fragments.FragmentCall} and on
     * {@link net.iGap.fragments.FragmentCall#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * should be check state of call and clear should be execute synchronise
     */
    private static void clearAllCallLog() {
        DbManager.getInstance().doRealmTransaction(realm -> {
            realm.where(RealmCallLog.class).findAll().deleteAllFromRealm();
        });
    }

    /**
     * clear call log if needed for new struct of "RealmCallLog" for schema version 33
     */
    public static void manageClearCallLog() {
        if (!HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_CLEAR_CALL_LOG)) {
            HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_CLEAR_CALL_LOG, true);
            clearAllCallLog();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RealmRegisteredInfo getUser() {
        return user;
    }

    public void setUser(RealmRegisteredInfo user) {
        this.user = user;
    }

    public int getOfferTime() {
        return offerTime;
    }

    public void setOfferTime(int offerTime) {
        this.offerTime = offerTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }
}
