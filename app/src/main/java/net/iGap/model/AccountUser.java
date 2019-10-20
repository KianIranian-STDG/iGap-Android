package net.iGap.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AccountUser {
    //ToDo: should be review and change and remove not use item
    private long id;
    private String dbName;
    private String name;
    private String phoneNumber;
    private String avatarPath;
    private String initialString;
    private String avatarColor;
    private int unReadMessageCount;
    private boolean isActive;
    private boolean isAssigned; // flag for show add new or not

    public AccountUser(long id) {
        this.id = id;
    }

    public AccountUser(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AccountUser(long id, String dbName, String name, String phoneNumber, String avatarPath, String initialString, String avatarColor, int unReadMessageCount, boolean isAssigned) {
        this.id = id;
        this.dbName = dbName;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.avatarPath = avatarPath;
        this.initialString = initialString;
        this.avatarColor = avatarColor;
        this.unReadMessageCount = unReadMessageCount;
        this.isAssigned = isAssigned;
    }


    public AccountUser(boolean isAddNew, String name) {
        this.name = name;
        this.isAssigned = isAddNew;
    }


    public String getDbName() {
        return dbName;
    }

    public String getName() {
        return name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getInitialString() {
        return initialString;
    }

    public String getAvatarColor() {
        return avatarColor;
    }

    public int getUnReadMessageCount() {
        return unReadMessageCount;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setInitialString(String initialString) {
        this.initialString = initialString;
    }

    public void setAvatarColor(String avatarColor) {
        this.avatarColor = avatarColor;
    }

    public void setUnReadMessageCount(int unReadMessageCount) {
        this.unReadMessageCount = unReadMessageCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof AccountUser) {
            return id == ((AccountUser) obj).getId();
        }
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + id + "\n" +
                "dbName: " + dbName + "\n" +
                "name: " + name + "\n" +
                "avatarPath: " + avatarPath + "\n" +
                "initialString: " + initialString + "\n" +
                "avatarColor: " + avatarColor + "\n" +
                "unReadMessageCount: " + unReadMessageCount + "\n" +
                "isActive: " + isActive + "\n" +
                "isAssigned: " + isAssigned;
    }
}
