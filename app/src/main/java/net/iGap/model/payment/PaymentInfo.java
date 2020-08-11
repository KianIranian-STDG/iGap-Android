package net.iGap.model.payment;

import com.google.gson.annotations.SerializedName;

public class PaymentInfo {

    @SerializedName("product")
    private BaseProduct product;
    @SerializedName("price")
    private int price;
    @SerializedName("vendor")
    private String vendor;
    @SerializedName("order_id")
    private String orderId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("rrn")
    private double rrn;
    @SerializedName("orderId")
    private String id;


    public BaseProduct getProduct() {
        return product;
    }

    public int getPrice() {
        return price;
    }

    public String getVendor() {
        return vendor;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public double getRrn() {
        return rrn;
    }

    public String getId() {
        return id;
    }
}
