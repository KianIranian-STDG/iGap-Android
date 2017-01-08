package com.iGap.response;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.iGap.G;
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

    public UserContactsGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        Log.i("QQQ", "2 contactsGetList");
        final ProtoUserContactsGetList.UserContactsGetListResponse.Builder builder = (ProtoUserContactsGetList.UserContactsGetListResponse.Builder) message;
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(RealmContacts.class);

                for (ProtoGlobal.RegisteredUser registerUser : builder.getRegisteredUserList()) {
                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, registerUser.getId()).findFirst();
                    if (realmRegisteredInfo == null) {
                        realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                        realmRegisteredInfo.setShowSpamBar(true);
                    }
                    realmRegisteredInfo.fillRegisteredUserInfo(registerUser, realmRegisteredInfo);

                    // because we have a realm just for avatars don't need to call put twice here
                    RealmAvatar.put(registerUser.getId(), registerUser.getAvatar(), true);

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
                    listResponse.setCacheId(registerUser.getCacheId());

                    listResponse.setAvatar(RealmAvatar.put(registerUser.getId(), registerUser.getAvatar(), true));
                }
            }
        });

        if (G.onUserContactGetList != null) {
            G.onUserContactGetList.onContactGetList();
        }

        realm.close();

        if (ContextCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Contacts.FillRealmInviteFriend();
        }

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


