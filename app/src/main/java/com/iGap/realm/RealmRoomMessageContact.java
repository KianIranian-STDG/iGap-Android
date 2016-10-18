package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRoomMessageContact extends RealmObject {

    private String firstName;
    private String lastName;
    private String nickName;
    private RealmList<RealmString> phones = new RealmList<>();
    private RealmList<RealmEmail> emails = new RealmList<>();
    @PrimaryKey
    private long id;

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

    public static RealmRoomMessageContact build(final ProtoGlobal.RoomMessageContact input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageContact messageContact = realm.createObject(RealmRoomMessageContact.class);
        messageContact.setId(System.nanoTime());
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

    public RealmList<RealmEmail> getEmails() {
        return emails;
    }

    public void setEmails(RealmList<RealmEmail> emails) {
        this.emails = emails;
    }
}
