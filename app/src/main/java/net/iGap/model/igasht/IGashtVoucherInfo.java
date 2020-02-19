package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

public class IGashtVoucherInfo {

    @SerializedName("ticket_count")
    private int ticketCount;
    @SerializedName("amount")
    private int amount;
    @SerializedName("status")
    private String status;
    @SerializedName("status_code")
    private String statusCode;
    @SerializedName("service_id")
    private int serviceId;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("service_english_name")
    private String serviceEnglishName;
    @SerializedName("voucher_type_id")
    private int voucherTypeId;
    @SerializedName("voucher_type_name")
    private String voucherTypeName;

    public int getTicketCount() {
        return ticketCount;
    }

    public int getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceEnglishName() {
        return serviceEnglishName;
    }

    public int getVoucherTypeId() {
        return voucherTypeId;
    }

    public String getVoucherTypeName() {
        return voucherTypeName;
    }
}
