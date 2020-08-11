package net.iGap.model.bill;

import com.google.gson.annotations.SerializedName;

import net.iGap.helper.HelperCalander;

public class ServiceDebit {

    @SerializedName("bill_identifier")
    private String billID;
    @SerializedName("total_register_debt")
    private int totalRegisterDebt;
    @SerializedName("payment_identifier")
    private String paymentID;
    @SerializedName("total_bill_debt")
    private String totalElectricityBillDebt;
    @SerializedName("payment_amount")
    private String totalGasBillDebt;
    @SerializedName("other_account_debt")
    private String otherAccountDebt;
    @SerializedName("payment_dead_line")
    private String paymentDeadLineDate;
    @SerializedName("last_read_date")
    private String lastReadDate;
    @SerializedName("last_gross_amt")
    private int lastGrossAmount;
    @SerializedName("last_sale_year")
    private int lastSaleYear;
    @SerializedName("last_sale_prd")
    private String lastSalePeriod;
    @SerializedName("lastupdatetime")
    private String lastUpdateTime;

    private boolean loading;
    private boolean fail;

    public ServiceDebit() {
        loading = true;
        fail = false;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public int getTotalRegisterDebt() {
        return totalRegisterDebt;
    }

    public void setTotalRegisterDebt(int totalRegisterDebt) {
        this.totalRegisterDebt = totalRegisterDebt;
    }

    public String getPaymentIDConverted() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(paymentID));
        }
        return paymentID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getTotalBillDebtConverted() {
        return totalElectricityBillDebt;
    }

    public String getTotalElectricityBillDebt() {
        return totalElectricityBillDebt;
    }

    public void setTotalElectricityBillDebt(String totalElectricityBillDebt) {
        this.totalElectricityBillDebt = totalElectricityBillDebt;
    }

    public String getOtherAccountDebt() {
        return otherAccountDebt;
    }

    public void setOtherAccountDebt(String otherAccountDebt) {
        this.otherAccountDebt = otherAccountDebt;
    }

    public String getPaymentDeadLineDate() {
        if (paymentDeadLineDate == null || paymentDeadLineDate.isEmpty())
            return "";
        return paymentDeadLineDate.replaceAll("-", "/");
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        try {
            Date mDate = sdf.parse(paymentDeadLineDate.replace("T", " ").replace("Z"," "));
            long timeInMilliseconds = mDate.getTime();
            if (HelperCalander.isPersianUnicode) {
                return HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS)));
            }
            return HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }*/
    }

    public void setPaymentDeadLineDate(String paymentDeadLineDate) {
        this.paymentDeadLineDate = paymentDeadLineDate;
    }

    public String getLastReadDate() {
        return lastReadDate;
    }

    public void setLastReadDate(String lastReadDate) {
        this.lastReadDate = lastReadDate;
    }

    public int getLastGrossAmount() {
        return lastGrossAmount;
    }

    public void setLastGrossAmount(int lastGrossAmount) {
        this.lastGrossAmount = lastGrossAmount;
    }

    public int getLastSaleYear() {
        return lastSaleYear;
    }

    public void setLastSaleYear(int lastSaleYear) {
        this.lastSaleYear = lastSaleYear;
    }

    public String getLastSalePeriod() {
        return lastSalePeriod;
    }

    public void setLastSalePeriod(String lastSalePeriod) {
        this.lastSalePeriod = lastSalePeriod;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public String getTotalGasBillDebt() {
        return totalGasBillDebt;
    }

    public void setTotalGasBillDebt(String totalGasBillDebt) {
        this.totalGasBillDebt = totalGasBillDebt;
    }

    public boolean isFail() {
        return fail;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }
}
