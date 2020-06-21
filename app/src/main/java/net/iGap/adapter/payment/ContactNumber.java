package net.iGap.adapter.payment;

import androidx.annotation.Nullable;

import net.iGap.module.structs.StructListOfContact;

public class ContactNumber implements Comparable<ContactNumber> {
    public String contactNumber;
    public String firstName;
    public String lastName;
    public String displayName;

    public String getPhone() {
        return contactNumber;
    }

    public void setPhone(String phone) {

        if (phone == null) {
            this.contactNumber = "";
        } else {
            this.contactNumber = phone;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof StructListOfContact) {
            return this.contactNumber.equals(((StructListOfContact) obj).phone);
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(ContactNumber that) {
        if (getDisplayName().compareTo(that.getDisplayName()) == 0)
            return getPhone().compareTo(that.getPhone());
        else
            return getDisplayName().compareTo(that.getDisplayName());
    }
}
