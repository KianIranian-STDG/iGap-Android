package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.util.List;
import net.iGap.module.SerializationUtils;
import net.iGap.proto.ProtoSignalingGetLog;

/**
 * Created by android3 on 5/15/2017.
 */

public class RealmCallLog extends RealmObject {

    @PrimaryKey private long id;
    private String name;
    private long time;
    private byte[] logProto;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog getlogProto() {
        return logProto == null ? null : (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog) SerializationUtils.deserialize(logProto);
    }

    public void setlogProto(ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog logProto) {
        this.logProto = SerializationUtils.serialize(logProto);
    }

    public static void addLog(ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog callLog, Realm realm) {

        RealmCallLog realmCallLog = realm.where(RealmCallLog.class).equalTo(RealmCallLogFields.ID, callLog.getId()).findFirst();

        if (realmCallLog == null) {
            realmCallLog = realm.createObject(RealmCallLog.class, callLog.getId());
        }

        realmCallLog.setlogProto(callLog);
        realmCallLog.setName(callLog.getPeer().getDisplayName());
        realmCallLog.setTime(callLog.getOfferTime());
    }

    public static void addLogList(final List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> list) {

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {

                for (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog item : list) {

                    addLog(item, realm);
                }
            }
        });

        realm.close();
    }
}
