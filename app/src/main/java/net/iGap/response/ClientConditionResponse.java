/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import io.realm.Realm;
import io.realm.RealmList;
import net.iGap.G;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmOfflineDelete;
import net.iGap.realm.RealmOfflineEdited;
import net.iGap.realm.RealmOfflineListen;
import net.iGap.realm.RealmOfflineSeen;

public class ClientConditionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientConditionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (final RealmClientCondition realmClientCondition : realm.where(RealmClientCondition.class).findAll()) {
                    realmClientCondition.setOfflineEdited(new RealmList<RealmOfflineEdited>());
                    realmClientCondition.setOfflineDeleted(new RealmList<RealmOfflineDelete>());
                    realmClientCondition.setOfflineSeen(new RealmList<RealmOfflineSeen>());
                    realmClientCondition.setOfflineListen(new RealmList<RealmOfflineListen>());
                }
            }
        });
        realm.close();

        if (G.onClientCondition != null) {
            G.onClientCondition.onClientCondition();
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        /**
         * timeOut call error also
         */
    }

    @Override
    public void error() {
        super.error();
        if (G.onClientCondition != null) {
            G.onClientCondition.onClientConditionError();
        }
    }
}


