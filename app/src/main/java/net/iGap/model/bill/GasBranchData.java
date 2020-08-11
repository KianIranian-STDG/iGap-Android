package net.iGap.model.bill;

import com.google.gson.annotations.SerializedName;

public class GasBranchData {

    @SerializedName("bill_identifier")
    private String billID;
    @SerializedName("payment_identifier")
    private String payID;
    @SerializedName("payment_amount")
    private String payAmount;
    @SerializedName("payment_dead_line")
    private String paymentDeadLineDate;
    @SerializedName("city_name")
    private String city;
    @SerializedName("unit")
    private String unit;
    @SerializedName("domain_code")
    private String domainCode;
    @SerializedName("building_id")
    private String buildID;
    @SerializedName("serial_no")
    private String serialNumber;
    @SerializedName("capacity")
    private String capacity;
    @SerializedName("kind")
    private String kind;
    @SerializedName("prev_date")
    private String previousDate;
    @SerializedName("curr_date")
    private String currentDate;
    @SerializedName("prev_value")
    private String previousValue;
    @SerializedName("curr_value")
    private String currentValue;
    @SerializedName("standard_consuption")
    private String standardConsumption;
    @SerializedName("gas_price_value")
    private String gasPriceValue;
    @SerializedName("abonnman_value")
    private String abonmanValue;
    @SerializedName("tax")
    private String tax;
    @SerializedName("assurance")
    private String assurance;
    @SerializedName("misc_cost_value")
    private String costValue;
    @SerializedName("not_moving_amount")
    private String notMovingAmount;
    @SerializedName("moving_amount")
    private String movingAmount;
    @SerializedName("not_payed_bills")
    private String notPayedBills;
    @SerializedName("sequence_number")
    private String sequenceNumber;
    @SerializedName("prev_rounding")
    private String previousRounding;
    @SerializedName("curr_rounding")
    private String currentRounding;
    @SerializedName("village_tax")
    private String villageTax;

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getPayID() {
        return payID;
    }

    public void setPayID(String payID) {
        this.payID = payID;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getPaymentDeadLineDate() {
        return paymentDeadLineDate;
    }

    public void setPaymentDeadLineDate(String paymentDeadLineDate) {
        this.paymentDeadLineDate = paymentDeadLineDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getBuildID() {
        return buildID;
    }

    public void setBuildID(String buildID) {
        this.buildID = buildID;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(String previousDate) {
        this.previousDate = previousDate;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getStandardConsumption() {
        return standardConsumption;
    }

    public void setStandardConsumption(String standardConsumption) {
        this.standardConsumption = standardConsumption;
    }

    public String getGasPriceValue() {
        return gasPriceValue;
    }

    public void setGasPriceValue(String gasPriceValue) {
        this.gasPriceValue = gasPriceValue;
    }

    public String getAbonmanValue() {
        return abonmanValue;
    }

    public void setAbonmanValue(String abonmanValue) {
        this.abonmanValue = abonmanValue;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getAssurance() {
        return assurance;
    }

    public void setAssurance(String assurance) {
        this.assurance = assurance;
    }

    public String getCostValue() {
        return costValue;
    }

    public void setCostValue(String costValue) {
        this.costValue = costValue;
    }

    public String getNotMovingAmount() {
        return notMovingAmount;
    }

    public void setNotMovingAmount(String notMovingAmount) {
        this.notMovingAmount = notMovingAmount;
    }

    public String getMovingAmount() {
        return movingAmount;
    }

    public void setMovingAmount(String movingAmount) {
        this.movingAmount = movingAmount;
    }

    public String getNotPayedBills() {
        return notPayedBills;
    }

    public void setNotPayedBills(String notPayedBills) {
        this.notPayedBills = notPayedBills;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getPreviousRounding() {
        return previousRounding;
    }

    public void setPreviousRounding(String previousRounding) {
        this.previousRounding = previousRounding;
    }

    public String getCurrentRounding() {
        return currentRounding;
    }

    public void setCurrentRounding(String currentRounding) {
        this.currentRounding = currentRounding;
    }

    public String getVillageTax() {
        return villageTax;
    }

    public void setVillageTax(String villageTax) {
        this.villageTax = villageTax;
    }
}
