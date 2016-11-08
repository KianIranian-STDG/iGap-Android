package com.iGap.response;

import android.content.Context;
import android.util.Log;

import com.iGap.G;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.module.Contacts;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserContactsGetList;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmThumbnail;

import io.realm.Realm;
import io.realm.Sort;

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

    private static long getCorrectId(Realm realm) {
        RealmThumbnail realmThumbnail = realm.where(RealmThumbnail.class).findAllSorted("id", Sort.DESCENDING).first();
        long id = (realmThumbnail.getId()) + 1;
        return id;
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

                    RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, registerUser.getId()).findFirst();
                    if (realmAvatar == null) {
                        realmAvatar = realm.createObject(RealmAvatar.class);
                        realmAvatar.setOwnerId(registerUser.getId());
                        realmAvatar.setId(registerUser.getAvatar().getId());
                    }

                    RealmAttachment realmAttachment =
                            realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, registerUser.getId()).findFirst();
                    if (realmAttachment == null) {
                        realmAttachment = realm.createObject(RealmAttachment.class);
                        realmAttachment.setId(registerUser.getId());
                    }
                    ProtoGlobal.File file = registerUser.getAvatar().getFile();
                    realmAttachment.setToken(file.getToken());
                    realmAttachment.setName(file.getName());
                    realmAttachment.setSize(file.getSize());

                    ProtoGlobal.Thumbnail smallThumbnail = file.getSmallThumbnail();
                    ProtoGlobal.Thumbnail largeThumbnail = file.getLargeThumbnail();

                    RealmThumbnail realmThumbnailSmall = realm.createObject(RealmThumbnail.class);
                    realmThumbnailSmall.setId(getCorrectId(realm));
                    realmThumbnailSmall.setSize(smallThumbnail.getSize());
                    realmThumbnailSmall.setWidth(smallThumbnail.getWidth());
                    realmThumbnailSmall.setHeight(smallThumbnail.getHeight());
                    realmThumbnailSmall.setCacheId(smallThumbnail.getCacheId());

                    RealmThumbnail realmThumbnailLarge = realm.createObject(RealmThumbnail.class);
                    realmThumbnailLarge.setId(getCorrectId(realm));
                    realmThumbnailLarge.setSize(largeThumbnail.getSize());
                    realmThumbnailLarge.setWidth(largeThumbnail.getWidth());
                    realmThumbnailLarge.setHeight(largeThumbnail.getHeight());
                    realmThumbnailLarge.setCacheId(largeThumbnail.getCacheId());

                    realmAttachment.setLargeThumbnail(realmThumbnailLarge);
                    realmAttachment.setSmallThumbnail(realmThumbnailSmall);
                    realmAttachment.setWidth(file.getWidth());
                    realmAttachment.setHeight(file.getHeight());
                    realmAttachment.setDuration(file.getDuration());
                    realmAttachment.setCacheId(file.getCacheId());

                    realmAvatar.setFile(realmAttachment);

                    listResponse.setAvatar(realmAvatar);
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


