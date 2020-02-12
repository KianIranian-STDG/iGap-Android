package net.iGap.observers.interfaces;

import net.iGap.model.payment.PaymentResult;

public interface PaymentCallBack {
    void onPaymentFinished(PaymentResult result);
}
