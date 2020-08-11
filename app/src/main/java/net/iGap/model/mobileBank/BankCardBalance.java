package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankCardBalance {

    @SerializedName("available_balance")
    private Balance available;
    @SerializedName("ledger_balance")
    private Balance ledger;

    public Balance getAvailable() {
        return available;
    }

    public void setAvailable(Balance available) {
        this.available = available;
    }

    public Balance getLedger() {
        return ledger;
    }

    public void setLedger(Balance ledger) {
        this.ledger = ledger;
    }

    public class Balance {

        @SerializedName("currency")
        private String currency;
        @SerializedName("value")
        private String value;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
