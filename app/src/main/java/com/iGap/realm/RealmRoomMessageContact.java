package com.iGap.realm;

import com.iGap.module.SUID;
import com.iGap.module.StringListParcelConverter;
import com.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmRoomMessageContactRealmProxy;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {RealmRoomMessageContactRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {RealmRoomMessageContact.class})
public class RealmRoomMessageContact extends RealmObject {

    private String firstName;
    private String lastName;
    private String nickName;
    private RealmList<RealmString> phones = new RealmList<>();
    private RealmList<RealmString> emails = new RealmList<>();
    @PrimaryKey
    private long id;

    public static RealmRoomMessageContact build(final ProtoGlobal.RoomMessageContact input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageContact messageContact = realm.createObject(RealmRoomMessageContact.class);
        messageContact.setId(SUID.id().get());
        for (String phone : input.getPhoneList()) {
            messageContact.addPhone(phone);
        }
        messageContact.setLastName(input.getLastName());
        messageContact.setFirstName(input.getFirstName());
        for (String email : input.getEmailList()) {
            messageContact.addEmail(email);
        }
        messageContact.setNickName(input.getNickname());
        realm.close();

        return messageContact;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public RealmList<RealmString> getPhones() {
        return phones;
    }

    @ParcelPropertyConverter(StringListParcelConverter.class)
    public void setPhones(RealmList<RealmString> phones) {
        this.phones = phones;
    }

    public void addPhone(String phone) {
        Realm realm = Realm.getDefaultInstance();
        RealmString realmString = realm.createObject(RealmString.class);
        realmString.setString(phone);
        phones.add(realmString);
        realm.close();
    }

    public void addEmail(String email) {
        Realm realm = Realm.getDefaultInstance();
        RealmString realmString = realm.createObject(RealmString.class);
        realmString.setString(email);
        phones.add(realmString);
        realm.close();
    }

    public RealmList<RealmString> getEmails() {
        return emails;
    }

    @ParcelPropertyConverter(StringListParcelConverter.class)
    public void setEmails(RealmList<RealmString> emails) {
        this.emails = emails;
    }
}
