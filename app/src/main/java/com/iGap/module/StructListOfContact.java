package com.iGap.module;

public class StructListOfContact {

    public String phone;
    public String firstName;
    public String lastName;
    public String displayName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {

        if (phone == null) {
            this.phone = "";
        } else {
            this.phone = phone;
        }
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        if (firstName == null) {
            this.firstName = "";
        } else {
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {

        if (lastName == null) {
            this.lastName = "";
        } else {
            this.lastName = lastName;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
