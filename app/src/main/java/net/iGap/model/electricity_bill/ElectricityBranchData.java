package net.iGap.model.electricity_bill;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import net.iGap.helper.HelperCalander;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ElectricityBranchData {

    @SerializedName("total_register_debt")
    private int totalRegisterDebt;
    @SerializedName("payment_identifier")
    private String paymentID;
    @SerializedName("total_bill_debt")
    private String totalBillDebt;
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
    private int lastSalePeriod;
    @SerializedName("ispaid")
    private boolean isPaid;
    @SerializedName("bill_identifier")
    private String billID;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("company_code")
    private int companyCode;
    @SerializedName("phase")
    private String phase;
    @SerializedName("voltage_type")
    private String voltageType;
    @SerializedName("amper")
    private String amper;
    @SerializedName("contract_demand")
    private double contractDemand;
    @SerializedName("tariff_type")
    private String tariffType;
    @SerializedName("customer_type")
    private String customerType;
    @SerializedName("national_code")
    private String national_code;
    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("customer_family")
    private String customerFamily;
    @SerializedName("tel_number")
    private String telNumber;
    @SerializedName("mobile_number")
    private String mobileNumber;
    @SerializedName("service_add")
    private String serviceAddress;
    @SerializedName("location_status")
    private String location_status;
    @SerializedName("serial_number")
    private String serialNumber;
    @SerializedName("license_expire_date")
    private int licenseExpireDate;
    @SerializedName("file_serial_number")
    private int fileSerialNumber;
    @SerializedName("subscription_id")
    private int subscriptionID;
    @SerializedName("x_degree")
    private int xDegree;
    @SerializedName("y_degree")
    private int yDegree;
    @SerializedName("service_post_code")
    private String servicePostCode;
    @SerializedName("lastupdatetime")
    private String lastUpdateTime;

    public int getTotalRegisterDebt() {
        return totalRegisterDebt;
    }

    public void setTotalRegisterDebt(int totalRegisterDebt) {
        this.totalRegisterDebt = totalRegisterDebt;
    }

    public String getPaymentID() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(paymentID);
        }
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getTotalBillDebt() {
        return totalBillDebt;
    }

    public void setTotalBillDebt(String totalBillDebt) {
        this.totalBillDebt = totalBillDebt;
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        try {
            Date mDate = sdf.parse(paymentDeadLineDate.replace("T", " ").replace("Z", " "));
            long timeInMilliseconds = mDate.getTime();
            if (HelperCalander.isPersianUnicode) {
                return HelperCalander.convertToUnicodeFarsiNumber(HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS));
            }
            return HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setPaymentDeadLineDate(String paymentDeadLineDate) {
        this.paymentDeadLineDate = paymentDeadLineDate;
    }

    public String getLastReadDate() {
        if (lastReadDate == null || lastReadDate.isEmpty())
            return "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        try {
            Date mDate = sdf.parse(lastReadDate.replace("T", " ").replace("Z", " "));
            long timeInMilliseconds = mDate.getTime();
            if (HelperCalander.isPersianUnicode) {
                return HelperCalander.convertToUnicodeFarsiNumber(HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS));
            }
            return HelperCalander.checkHijriAndReturnTime(timeInMilliseconds / DateUtils.SECOND_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
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

    public int getLastSalePeriod() {
        return lastSalePeriod;
    }

    public void setLastSalePeriod(int lastSalePeriod) {
        this.lastSalePeriod = lastSalePeriod;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public String getBillID() {
        return billID;
    }

    public void setBillID(String billID) {
        this.billID = billID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(companyCode));
        }
        return String.valueOf(companyCode);
    }

    public void setCompanyCode(int companyCode) {
        this.companyCode = companyCode;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getVoltageType() {
        return voltageType;
    }

    public void setVoltageType(String voltageType) {
        this.voltageType = voltageType;
    }

    public String getAmper() {
        return amper;
    }

    public void setAmper(String amper) {
        this.amper = amper;
    }

    public String getContractDemand() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(contractDemand));
        }
        return String.valueOf(contractDemand);
    }

    public void setContractDemand(int contractDemand) {
        this.contractDemand = contractDemand;
    }

    public String getTariffType() {
        return tariffType;
    }

    public void setTariffType(String tariffType) {
        this.tariffType = tariffType;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getNational_code() {
        return national_code;
    }

    public void setNational_code(String national_code) {
        this.national_code = national_code;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerFamily() {
        return customerFamily;
    }

    public void setCustomerFamily(String customerFamily) {
        this.customerFamily = customerFamily;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getServiceAddress() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(serviceAddress));
        }
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getLocation_status() {
        return location_status;
    }

    public void setLocation_status(String location_status) {
        this.location_status = location_status;
    }

    public String getSerialNumber() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(serialNumber);
        }
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLicenseExpireDate() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(licenseExpireDate));
        }
        return String.valueOf(licenseExpireDate);
    }

    public void setLicenseExpireDate(int licenseExpireDate) {
        this.licenseExpireDate = licenseExpireDate;
    }

    public int getFileSerialNumber() {
        return fileSerialNumber;
    }

    public void setFileSerialNumber(int fileSerialNumber) {
        this.fileSerialNumber = fileSerialNumber;
    }

    public int getSubscriptionID() {
        return subscriptionID;
    }

    public void setSubscriptionID(int subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    public int getxDegree() {
        return xDegree;
    }

    public void setxDegree(int xDegree) {
        this.xDegree = xDegree;
    }

    public int getyDegree() {
        return yDegree;
    }

    public void setyDegree(int yDegree) {
        this.yDegree = yDegree;
    }

    public String getServicePostCode() {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(servicePostCode);
        }
        return servicePostCode;
    }

    public void setServicePostCode(String servicePostCode) {
        this.servicePostCode = servicePostCode;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
