package org.paygear.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transport implements Serializable {

    public static final int TYPE_KHATTI = 0;
    public static final int TYPE_GARDESHI = 1;
    public static final int TYPE_AGENCY = 2;

    @SerializedName("_id")
    public String id;
    public String name;
    public long value;
    @SerializedName("transport_type")
    public int type;
    @SerializedName("country_id")
    public long countryId;
    @SerializedName("province_id")
    public long provinceId;
    @SerializedName("city_id")
    public long cityId;
}
