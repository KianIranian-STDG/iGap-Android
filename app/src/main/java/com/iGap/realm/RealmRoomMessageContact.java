package com.iGap.realm;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmRoomMessageContact extends RealmObject {

    private String firstName;
    private String lastName;
    private String nickName;
    private RealmList<RealmPhone> phones = new RealmList<>();
    private RealmList<RealmEmail> emails = new RealmList<>();

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

    public RealmList<RealmPhone> getPhones() {
        return phones;
    }

    public void setPhones(RealmList<RealmPhone> phones) {
        this.phones = phones;
    }

    public void addPhone(String phone) {
        Realm realm = Realm.getDefaultInstance();
        RealmPhone realmPhone = realm.createObject(RealmPhone.class);
        realmPhone.setPhone(phone);
        phones.add(realmPhone);
        realm.close();
    }

    public RealmList<RealmEmail> getEmails() {
        return emails;
    }

    public void setEmails(RealmList<RealmEmail> emails) {
        this.emails = emails;
    }
}
