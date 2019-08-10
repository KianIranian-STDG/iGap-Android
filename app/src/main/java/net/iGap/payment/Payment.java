package net.iGap.payment;

public class Payment {

    private String status;
    private String message;
    private String orderId;

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
}
