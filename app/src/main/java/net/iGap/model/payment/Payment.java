package net.iGap.model.payment;

public class Payment {

    private String status;
    private String message;
    private String orderId;
    private String tax;
    private String discount;

    public Payment(String status, String message, String orderId, String tax, String discount) {
        this.status = status;
        this.message = message;
        this.orderId = orderId;
        this.tax = tax;
        this.discount = discount;
    }

    public Payment(String status, String message, String orderId) {
        this.status = status;
        this.message = message;
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTax() {
        return tax;
    }

    public String getDiscount() {
        return discount;
    }
}
