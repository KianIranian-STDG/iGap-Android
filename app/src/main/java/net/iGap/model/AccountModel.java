package net.iGap.model;

public class AccountModel {

    private long id;
    private String name;
    private String picture;
    private int unread;
    private boolean isActive;
    private transient boolean isAddNew ; // flag for show add new or not

    public AccountModel() {
    }

    public AccountModel(boolean isAddNew) {
        this.name = "Add New Account";
        this.picture = "" ;
        this.isAddNew = isAddNew;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
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
}
