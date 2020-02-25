package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

public class SaleBill {

    @SerializedName("cold_normal_cons")
    private String coldNormalCons;
    @SerializedName("cold_peak_cons")
    private String coldPeakCons;
    @SerializedName("cold_low_cons")
    private String coldLowCons;
    @SerializedName("warm_normal_cons")
    private String warmNormalCons;
    @SerializedName("warm_peak_cons")
    private String warmPeakCons;
    @SerializedName("warm_low_cons")
    private String warmLowCons;
    @SerializedName("bill_identifier")
    private String billID;
    @SerializedName("bill_serial")
    private String billSerial;
    @SerializedName("sale_year")
    private String saleYear;
    @SerializedName("sale_prd")
    private String salePeriod;
    @SerializedName("process_status")
    private String processStatus;
    @SerializedName("issue_date")
    private String issueDate;
    @SerializedName("prev_reading_date")
    private String prevReadingDate;
    @SerializedName("reject_date")
    private String rejectDate;
    @SerializedName("reading_date")
    private String readingDate;
    @SerializedName("normal_cons")
    private int normalCons;
    @SerializedName("peak_cons")
    private int peakCons;
    @SerializedName("low_cons")
    private int lowCons;
    @SerializedName("friday_cons")
    private int fridayCons;
    @SerializedName("react_cons")
    private int reactCons;
    @SerializedName("demand_read")
    private int demandRead;
    @SerializedName("avg_cost")
    private int avgCost;
    @SerializedName("bill_amt")
    private int billAmount;
    @SerializedName("gross_amt")
    private int grossAmount;
    @SerializedName("insurance_amt")
    private int insuranceAmount;
    @SerializedName("tax_amt")
    private int taxAmount;
    @SerializedName("paytoll_amt")
    private int paytollAmount;
    @SerializedName("power_paytoll_amt")
    private int powerPaytollAmount;
    @SerializedName("previous_energy_deb")
    private int previousEnergyDebit;
    @SerializedName("energy_amt")
    private int energyAmount;
    @SerializedName("reactive_amt")
    private int reactiveAmount;
    @SerializedName("demand_amt")
    private int demandAmount;
    @SerializedName("subsc_amt")
    private String subscAmount;
    @SerializedName("license_expire_amt")
    private int licenseExpireAmount;
    @SerializedName("season_amt")
    private String seasonAmount;
    @SerializedName("free_amt")
    private String freeAmount;
    @SerializedName("warm_normal_amt")
    private String warmNormalAmount;
    @SerializedName("warm_peak_amt")
    private String warmPeakAmount;
    @SerializedName("warm_low_amt")
    private String warmLowAmount;
    @SerializedName("cold_normal_amt")
    private String coldNormalAmount;
    @SerializedName("cold_peak_amt")
    private String coldPeakAmount;
    @SerializedName("cold_low_amt")
    private String coldLowAmount;
    @SerializedName("gas_discount_amt")
    private String gasDiscountAmount;
    @SerializedName("discount_amt")
    private String discountAmount;
    @SerializedName("warm_days")
    private String warmDays;
    @SerializedName("cold_days")
    private String coldDays;
    @SerializedName("total_days")
    private String totalDays;

    public String getColdNormalCons() {
        return coldNormalCons;
    }

    public void setColdNormalCons(String coldNormalCons) {
        this.coldNormalCons = coldNormalCons;
    }

    public String getColdPeakCons() {
        return coldPeakCons;
    }

    public void setColdPeakCons(String coldPeakCons) {
        this.coldPeakCons = coldPeakCons;
    }

    public String getColdLowCons() {
        return coldLowCons;
    }

    public void setColdLowCons(String coldLowCons) {
        this.coldLowCons = coldLowCons;
    }

    public String getWarmNormalCons() {
        return warmNormalCons;
    }

    public void setWarmNormalCons(String warmNormalCons) {
        this.warmNormalCons = warmNormalCons;
    }

    public String getWarmPeakCons() {
        return warmPeakCons;
    }

    public void setWarmPeakCons(String warmPeakCons) {
        this.warmPeakCons = warmPeakCons;
    }

    public String getWarmLowCons() {
        return warmLowCons;
    }

    public void setWarmLowCons(String warmLowCons) {
        this.warmLowCons = warmLowCons;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getBillSerial() {
        return billSerial;
    }

    public void setBillSerial(String billSerial) {
        this.billSerial = billSerial;
    }

    public String getSaleYear() {
        return saleYear;
    }

    public void setSaleYear(String saleYear) {
        this.saleYear = saleYear;
    }

    public String getSalePeriod() {
        return salePeriod;
    }

    public void setSalePeriod(String salePeriod) {
        this.salePeriod = salePeriod;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getPrevReadingDate() {
        return prevReadingDate;
    }

    public void setPrevReadingDate(String prevReadingDate) {
        this.prevReadingDate = prevReadingDate;
    }

    public String getRejectDate() {
        return rejectDate;
    }

    public void setRejectDate(String rejectDate) {
        this.rejectDate = rejectDate;
    }

    public String getReadingDate() {
        return readingDate;
    }

    public void setReadingDate(String readingDate) {
        this.readingDate = readingDate;
    }

    public int getNormalCons() {
        return normalCons;
    }

    public void setNormalCons(int normalCons) {
        this.normalCons = normalCons;
    }

    public int getPeakCons() {
        return peakCons;
    }

    public void setPeakCons(int peakCons) {
        this.peakCons = peakCons;
    }

    public int getLowCons() {
        return lowCons;
    }

    public void setLowCons(int lowCons) {
        this.lowCons = lowCons;
    }

    public int getFridayCons() {
        return fridayCons;
    }

    public void setFridayCons(int fridayCons) {
        this.fridayCons = fridayCons;
    }

    public int getReactCons() {
        return reactCons;
    }

    public void setReactCons(int reactCons) {
        this.reactCons = reactCons;
    }

    public int getDemandRead() {
        return demandRead;
    }

    public void setDemandRead(int demandRead) {
        this.demandRead = demandRead;
    }

    public int getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(int avgCost) {
        this.avgCost = avgCost;
    }

    public int getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(int billAmount) {
        this.billAmount = billAmount;
    }

    public int getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(int grossAmount) {
        this.grossAmount = grossAmount;
    }

    public int getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(int insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public int getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(int taxAmount) {
        this.taxAmount = taxAmount;
    }

    public int getPaytollAmount() {
        return paytollAmount;
    }

    public void setPaytollAmount(int paytollAmount) {
        this.paytollAmount = paytollAmount;
    }

    public int getPowerPaytollAmount() {
        return powerPaytollAmount;
    }

    public void setPowerPaytollAmount(int powerPaytollAmount) {
        this.powerPaytollAmount = powerPaytollAmount;
    }

    public int getPreviousEnergyDebit() {
        return previousEnergyDebit;
    }

    public void setPreviousEnergyDebit(int previousEnergyDebit) {
        this.previousEnergyDebit = previousEnergyDebit;
    }

    public int getEnergyAmount() {
        return energyAmount;
    }

    public void setEnergyAmount(int energyAmount) {
        this.energyAmount = energyAmount;
    }

    public int getReactiveAmount() {
        return reactiveAmount;
    }

    public void setReactiveAmount(int reactiveAmount) {
        this.reactiveAmount = reactiveAmount;
    }

    public int getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(int demandAmount) {
        this.demandAmount = demandAmount;
    }

    public String getSubscAmount() {
        return subscAmount;
    }

    public void setSubscAmount(String subscAmount) {
        this.subscAmount = subscAmount;
    }

    public int getLicenseExpireAmount() {
        return licenseExpireAmount;
    }

    public void setLicenseExpireAmount(int licenseExpireAmount) {
        this.licenseExpireAmount = licenseExpireAmount;
    }

    public String getSeasonAmount() {
        return seasonAmount;
    }

    public void setSeasonAmount(String seasonAmount) {
        this.seasonAmount = seasonAmount;
    }

    public String getFreeAmount() {
        return freeAmount;
    }

    public void setFreeAmount(String freeAmount) {
        this.freeAmount = freeAmount;
    }

    public String getWarmNormalAmount() {
        return warmNormalAmount;
    }

    public void setWarmNormalAmount(String warmNormalAmount) {
        this.warmNormalAmount = warmNormalAmount;
    }

    public String getWarmPeakAmount() {
        return warmPeakAmount;
    }

    public void setWarmPeakAmount(String warmPeakAmount) {
        this.warmPeakAmount = warmPeakAmount;
    }

    public String getWarmLowAmount() {
        return warmLowAmount;
    }

    public void setWarmLowAmount(String warmLowAmount) {
        this.warmLowAmount = warmLowAmount;
    }

    public String getColdNormalAmount() {
        return coldNormalAmount;
    }

    public void setColdNormalAmount(String coldNormalAmount) {
        this.coldNormalAmount = coldNormalAmount;
    }

    public String getColdPeakAmount() {
        return coldPeakAmount;
    }

    public void setColdPeakAmount(String coldPeakAmount) {
        this.coldPeakAmount = coldPeakAmount;
    }

    public String getColdLowAmount() {
        return coldLowAmount;
    }

    public void setColdLowAmount(String coldLowAmount) {
        this.coldLowAmount = coldLowAmount;
    }

    public String getGasDiscountAmount() {
        return gasDiscountAmount;
    }

    public void setGasDiscountAmount(String gasDiscountAmount) {
        this.gasDiscountAmount = gasDiscountAmount;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getWarmDays() {
        return warmDays;
    }

    public void setWarmDays(String warmDays) {
        this.warmDays = warmDays;
    }

    public String getColdDays() {
        return coldDays;
    }

    public void setColdDays(String coldDays) {
        this.coldDays = coldDays;
    }

    public String getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(String totalDays) {
        this.totalDays = totalDays;
    }
}
