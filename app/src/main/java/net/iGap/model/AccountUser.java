package net.iGap.model;

import androidx.annotation.NonNull;

import net.iGap.R;

public class AccountUser {
    //ToDo: should be review and change and remove not use item
    private long id;
    private String dbName;
    private String name;
    private String avatarPath;
    private String initialString;
    private String avatarColor;
    private int unReadMessageCount;
    private boolean isActive;
    private transient boolean isAddNew ; // flag for show add new or not

    public AccountUser() {
    }

    public AccountUser(long id, String dbName, String name, String avatarPath, String initialString, String avatarColor, int unReadMessageCount) {
        this.id = id;
        this.dbName = dbName;
        this.name = name;
        this.avatarPath = avatarPath;
        this.initialString = initialString;
        this.avatarColor = avatarColor;
        this.unReadMessageCount = unReadMessageCount;
    }


    public AccountUser(boolean isAddNew , String name) {
        this.name = name;
        this.isAddNew = isAddNew;
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

    public boolean isAddNew() {
        return isAddNew;
    }

    public void setAddNew(boolean addNew) {
        isAddNew = addNew;
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + id +
                "dbName: " + dbName +
                "name: " + name +
                "avatarPath: " + avatarPath +
                "initialString: " + initialString +
                "avatarColor: " + avatarColor +
                "unReadMessageCount: " + unReadMessageCount +
                "isActive: " + isActive +
                "isAddNew: " + isAddNew;
    }
}
