package net.iGap.model.payment;

public class PaymentResult {

    private String token;
    private String RRN;
    private boolean success;

    public PaymentResult() {
        this.token = "";
        this.success = false;
    }

    public PaymentResult(String token, boolean success) {
        this.token = token;
        this.success = success;
    }

    public PaymentResult(String token, String RRN, boolean success) {
        this.token = token;
        this.RRN = RRN;
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getRRN() {
        return RRN;
    }

    public void setRRN(String RRN) {
        this.RRN = RRN;
    }
}
