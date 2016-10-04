package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.module.Contacts;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserContactsGetList;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmFileAvatar;
import com.iGap.realm.RealmThumbnail;

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

        final ProtoUserContactsGetList.UserContactsGetListResponse.Builder builder = (ProtoUserContactsGetList.UserContactsGetListResponse.Builder) message;
        builder.toString().length();
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.delete(RealmContacts.class);

                for (ProtoGlobal.RegisteredUser registerUser : builder.getRegisteredUserList()) {
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

                    RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class);
                    realmAvatar.setId(registerUser.getId());
                    listResponse.setAvatar(realmAvatar);

                    RealmFileAvatar realmFileAvatar = realm.createObject(RealmFileAvatar.class);
                    ProtoGlobal.File file = registerUser.getAvatar().getFile();
                    realmFileAvatar.setToken(file.getToken());
                    realmFileAvatar.setName(file.getName());
                    realmFileAvatar.setSize(file.getSize());

                    ProtoGlobal.Thumbnail smallThumbnail = file.getSmallThumbnail();
                    ProtoGlobal.Thumbnail largeThumbnail = file.getLargeThumbnail();

                    RealmThumbnail realmThumbnailSmall = realm.createObject(RealmThumbnail.class);
                    realmThumbnailSmall.setSize(smallThumbnail.getSize());
                    realmThumbnailSmall.setWidth(smallThumbnail.getWidth());
                    realmThumbnailSmall.setHeight(smallThumbnail.getHeight());
                    realmThumbnailSmall.setCacheId(smallThumbnail.getCacheId());

                    RealmThumbnail realmThumbnailLarge = realm.createObject(RealmThumbnail.class);
                    realmThumbnailLarge.setSize(largeThumbnail.getSize());
                    realmThumbnailLarge.setWidth(largeThumbnail.getWidth());
                    realmThumbnailLarge.setHeight(largeThumbnail.getHeight());
                    realmThumbnailLarge.setCacheId(largeThumbnail.getCacheId());

                    realmFileAvatar.setLargeThumbnail(realmThumbnailLarge);
                    realmFileAvatar.setSmallThumbnail(realmThumbnailSmall);
                    realmFileAvatar.setWidth(file.getWidth());
                    realmFileAvatar.setHeight(file.getHeight());
                    realmFileAvatar.setDuration(file.getDuration());
                    realmFileAvatar.setCatchId(file.getCacheId());
                }
            }
        });

        G.onUserContactGetList.onContactGetList();

        realm.close();

        Contacts.FillRealmInviteFriend();
    }

    @Override
    public void timeOut() {
        Log.i("XXX", "UserContactsGetListResponse timeOut");
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        errorResponse.getMajorCode();
        errorResponse.getMinorCode();

        Log.i("XXX", "UserContactsGetListResponse errorReponse.getMajorCode() : " + errorResponse.getMajorCode());
        Log.i("XXX", "UserContactsGetListResponse errorReponse.getMinorCode() : " + errorResponse.getMinorCode());

    }
}


