package com.iGap.response;

import android.content.Context;
import android.util.Log;

import com.iGap.G;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.module.Contacts;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserContactsGetList;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;

import io.realm.Realm;

public class UserContactsGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    public Context context;

    public UserContactsGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        Log.i("OOO", "UserContactsGetListResponse : " + message);
        final ProtoUserContactsGetList.UserContactsGetListResponse.Builder builder =
                (ProtoUserContactsGetList.UserContactsGetListResponse.Builder) message;
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(RealmContacts.class);

                for (ProtoGlobal.RegisteredUser registerUser : builder.getRegisteredUserList()) {
                    RealmRegisteredInfo realmRegisteredInfo =
                            realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, registerUser.getId()).findFirst();
                    if (realmRegisteredInfo == null) {
                        realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                        realmRegisteredInfo.setRegisteredUserInfo(registerUser, realmRegisteredInfo, realm);
                    } else {
                        realmRegisteredInfo.updateRegisteredUserInfo(registerUser, realmRegisteredInfo, realm);
                    }

                    RealmContacts listResponse = realm.createObject(RealmContacts.class);
                    listResponse.setId(registerUser.getId());
                    listResponse.setUsername(registerUser.getUsername());
                    listResponse.setPhone(registerUser.getPhone());
                    listResponse.setFirst_name(registerUser.getFirstName());
                    listResponse.setLast_name(registerUser.getLastName());
                    listResponse.setDisplay_name(registerUser.getDisplayName());
                    listResponse.setInitials(registerUser.getInitials());
                    listResponse.setColor(registerUser.getColor());
                    listResponse.setStatus(registerUser.getStatus().toString());
                    listResponse.setLast_seen(registerUser.getLastSeen());
                    listResponse.setAvatarCount(registerUser.getAvatarCount());

                    listResponse.setAvatar(RealmAvatar.put(registerUser.getId(), registerUser.getAvatar()));
                }
            }
        });

        G.onUserContactGetList.onContactGetList();

        realm.close();

        HelperPermision.getContactPermision(context, new OnGetPermision() {
            @Override
            public void Allow() {
                Contacts.FillRealmInviteFriend();
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
    }
}


