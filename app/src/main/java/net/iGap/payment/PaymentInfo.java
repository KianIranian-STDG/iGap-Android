package net.iGap.payment;

import com.google.gson.annotations.SerializedName;

public class PaymentInfo {

    @SerializedName("product")
    private BaseProduct product;
    @SerializedName("price")
    private double price;
    @SerializedName("vendor")
    private String vendor;
    @SerializedName("order_id")
    private String orderId;

    public BaseProduct getProduct() {
        return product;
    }

    public double getPrice() {
        return price;
    }

    public String getVendor() {
        return vendor;
    }

    public String getOrderId() {
        return orderId;
    }
}
