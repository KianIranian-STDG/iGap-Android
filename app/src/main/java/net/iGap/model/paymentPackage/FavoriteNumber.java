
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FavoriteNumber {

    @SerializedName("amount")
    private Long amount;
    @SerializedName("charge_type")
    private String chargeType;
    @SerializedName("charge_type_description")
    private String chargeTypeDescription;
    @Expose
    private String operator;
    @SerializedName("operator_title")
    private String operatorTitle;
    @SerializedName("phone_number")
    private String phoneNumber;
    @Expose
    private String type;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getChargeTypeDescription() {
        return chargeTypeDescription;
    }

    public void setChargeTypeDescription(String chargeTypeDescription) {
        this.chargeTypeDescription = chargeTypeDescription;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorTitle() {
        return operatorTitle;
    }

    public void setOperatorTitle(String operatorTitle) {
        this.operatorTitle = operatorTitle;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
