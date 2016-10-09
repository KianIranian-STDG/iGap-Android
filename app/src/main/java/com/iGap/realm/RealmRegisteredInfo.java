package com.iGap.realm;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRegisteredInfo extends RealmObject {
    @PrimaryKey
    private long id;
    private String username;
    private long phone;
    private String firstName;
    private String lastName;
    private String displayName;
    private String initials;
    private String color;
    private String status;
    private int lastSeen;
    private int avatarCount;
    private RealmAvatar avatar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(int lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getAvatarCount() {
        return avatarCount;
    }

    public void setAvatarCount(int avatarCount) {
        this.avatarCount = avatarCount;
    }

    public RealmAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(RealmAvatar avatar) {
        this.avatar = avatar;
    }

    public void setAvatar(ProtoGlobal.Avatar avatar) {
        if (avatar == null || avatar.getFile() == null || (avatar.getFile().getToken() == null || avatar.getFile().getToken().isEmpty())) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo("id", avatar.getId()).findFirst();

        if (realmAvatar == null) {
            realmAvatar = new RealmAvatar(avatar.getId());
        } else {
            if (realmAvatar.getFile() != null) {
                realmAvatar.getFile().deleteFromRealm();
            }
        }

        realmAvatar.setFile(RealmAttachment.build(avatar.getFile()));

        this.avatar = realmAvatar;
        realm.close();
    }
}
