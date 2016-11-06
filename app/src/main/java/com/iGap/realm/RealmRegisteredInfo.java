package com.iGap.realm;

import android.util.Log;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRegisteredInfo extends RealmObject {
    @PrimaryKey private long id;
    private String username;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String displayName;
    private String initials;
    private String color;
    private String status;
    private int lastSeen;
    private int avatarCount;
    private boolean mutual;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public boolean isMutual() {
        return mutual;
    }

    public void setMutual(boolean mutual) {
        this.mutual = mutual;
    }

    public RealmList<RealmAvatar> getAvatars() {
        RealmList<RealmAvatar> avatars = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, id).findAll()) {
            avatars.add(avatar);
        }
        realm.close();
        return avatars;
    }

    public RealmAvatar getLastAvatar() {
        RealmList<RealmAvatar> avatars = getAvatars();
        if (avatars.isEmpty()) {
            return null;
        }
        // make sure return last avatar which has attachment
        for (int i = avatars.size() - 1; i >= 0; i--) {
            RealmAvatar avatar = getAvatars().get(i);
            if (avatar.getFile() != null) {
                return avatar;
            }
        }
        return null;
    }

    /**
     * create new object from RealmRegisteredInfo and set all fields with registeredUser Proto
     *
     * @param registeredUser proto that get from server
     * @param realm realm that get from executeTransaction
     */

    public void setRegisteredUserInfo(ProtoGlobal.RegisteredUser registeredUser, RealmRegisteredInfo realmRegisteredInfo, Realm realm) {
        fillRegisteredUserInfo(registeredUser, realmRegisteredInfo, realm);
    }

    /**
     * get exist row from RealmRegisteredInfo and set all fields with registeredUser Proto
     *
     * @param registeredUser proto that get from server
     * @param realmRegisteredInfo current object from realm
     * @param realm realm that get from executeTransaction
     */
    public void updateRegisteredUserInfo(ProtoGlobal.RegisteredUser registeredUser, RealmRegisteredInfo realmRegisteredInfo, Realm realm) {
        fillRegisteredUserInfo(registeredUser, realmRegisteredInfo, realm);
    }

    /**
     * fill object from proto to realm
     *
     * @param registeredUser proto that get from server
     * @param info object from RealmRegisteredInfo
     * @param realm realm that get from executeTransaction
     */

    private void fillRegisteredUserInfo(ProtoGlobal.RegisteredUser registeredUser, RealmRegisteredInfo info, Realm realm) {

        info.setId(registeredUser.getId());
        info.setUsername(registeredUser.getUsername());
        info.setPhoneNumber(Long.toString(registeredUser.getPhone()));
        info.setFirstName(registeredUser.getFirstName());
        info.setLastName(registeredUser.getLastName());
        info.setDisplayName(registeredUser.getDisplayName());
        info.setInitials(registeredUser.getInitials());
        info.setColor(registeredUser.getColor());
        info.setStatus(registeredUser.getStatus().toString());
        info.setLastName(registeredUser.getLastName());
        info.setAvatarCount(registeredUser.getAvatarCount());
        info.setMutual(registeredUser.getMutual());

        if (registeredUser.getAvatarCount() > 0) {
            Log.i("SSS", "Avatar Exist");
            RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.FILE.TOKEN, registeredUser.getAvatar().getFile().getToken()).findFirst();
            Log.i("SSS", "realmAvatar : " + realmAvatar);
        }
    }
}
