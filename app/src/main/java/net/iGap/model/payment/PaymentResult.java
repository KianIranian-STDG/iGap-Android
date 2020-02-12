package net.iGap.model.payment;

public class PaymentResult {

    private String token;
    private boolean success;

    public PaymentResult() {
        this.token = "";
        this.success = false;
    }

    public PaymentResult(String token, boolean success) {
        this.token = token;
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public boolean isSuccess() {
        return success;
    }
}
