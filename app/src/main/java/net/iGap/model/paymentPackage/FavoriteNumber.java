
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

public class FavoriteNumber {

    @SerializedName("amount")
    private Long mAmount;
    @SerializedName("charge_type")
    private String mChargeType;
    @SerializedName("charge_type_description")
    private String mChargeTypeDescription;
    @SerializedName("operator")
    private String mOperator;
    @SerializedName("operator_title")
    private String mOperatorTitle;
    @SerializedName("phone_number")
    private String mPhoneNumber;
    @SerializedName("type")
    private String mType;

    public Long getAmount() {
        return mAmount;
    }

    public void setAmount(Long amount) {
        mAmount = amount;
    }

    public String getChargeType() {
        return mChargeType;
    }

    public void setChargeType(String chargeType) {
        mChargeType = chargeType;
    }

    public String getChargeTypeDescription() {
        return mChargeTypeDescription;
    }

    public void setChargeTypeDescription(String chargeTypeDescription) {
        mChargeTypeDescription = chargeTypeDescription;
    }

    public String getOperator() {
        return mOperator;
    }

    public void setOperator(String operator) {
        mOperator = operator;
    }

    public String getOperatorTitle() {
        return mOperatorTitle;
    }

    public void setOperatorTitle(String operatorTitle) {
        mOperatorTitle = operatorTitle;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
