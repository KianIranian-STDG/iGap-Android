package net.iGap.adapter.payment;

import java.text.DecimalFormat;

public class Amount {
    private String textAmount;
    private long amount;

    public Amount(long amount) {
        this.amount = amount;
        textAmount = new DecimalFormat(",###").format(amount);
    }

    public Amount(Amount currentAmount, boolean plus) {
        amount = plus ? currentAmount.amount + 10000 : currentAmount.amount - 10000;
        textAmount = new DecimalFormat(",###").format(amount);
    }

    public long getAmount() {
        return amount;
    }

    public String getTextAmount() {
        return textAmount;
    }
}
