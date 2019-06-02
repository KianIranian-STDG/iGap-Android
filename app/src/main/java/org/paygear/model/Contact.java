package org.paygear.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Software1 on 2/22/2017.
 */

public class Contact {

    public String name;
    @SerializedName("mobile_phone")
    public String mobile;

    public Contact(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
    }
}
