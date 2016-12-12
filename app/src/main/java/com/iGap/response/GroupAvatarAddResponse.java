package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoGroupAvatarAdd;
import com.iGap.realm.RealmAvatar;

import io.realm.Realm;

public class GroupAvatarAddResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAvatarAddResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoGroupAvatarAdd.GroupAvatarAddResponse.Builder groupAvatarAddResponse = (ProtoGroupAvatarAdd.GroupAvatarAddResponse.Builder) message;
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmAvatar.put(groupAvatarAddResponse.getRoomId(), groupAvatarAddResponse.getAvatar());
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (G.onGroupAvatarResponse != null) {
                            G.onGroupAvatarResponse.onAvatarAdd(groupAvatarAddResponse.getRoomId(), groupAvatarAddResponse.getAvatar());
                        }
                    }
                });
                realm.close();
            }
        });

    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        if (G.onGroupAvatarResponse != null) {
            G.onGroupAvatarResponse.onAvatarAddError();
        }
    }
}


