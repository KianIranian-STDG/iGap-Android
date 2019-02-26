package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

public class AvailableClubs_Result {

    /**
     * ID : 5bfd922091a020000f03a794
     * Min : 10000
     * max : 100000000
     * merchant_id : merchant1
     * member_id : member1
     */

    @SerializedName(value="id" , alternate = {"ID"})
    private String id;
    @SerializedName(value="min" , alternate = {"Min"})
    private Long min;
    private Long max;
    private String merchant_id;
    private String member_id;

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long Min) {
        this.min = Min;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }
}
