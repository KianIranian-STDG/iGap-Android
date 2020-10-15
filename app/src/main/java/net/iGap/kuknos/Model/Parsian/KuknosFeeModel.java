package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KuknosFeeModel {

    @SerializedName("fees")
    private List<Fee> fees;

    public List<Fee> getFees() {
        return fees;
    }

    public void setFees(List<Fee> fees) {
        this.fees = fees;
    }

    public class Fee {
        @SerializedName("method")
        private String method;
        @SerializedName("discount")
        private int discount;
        @SerializedName("fee")
        private double fee;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public double getFee() {
            return fee;
        }

        public void setFee(double fee) {
            this.fee = fee;
        }
    }
}
