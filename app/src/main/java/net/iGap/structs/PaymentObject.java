package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageWalletPayment;

public class PaymentObject {
    public long fromUserId;
    public long toUserId;
    public long amount;
    public long traceNumber;
    public long invoiceNumber;
    public int payTime;
    public String description;
    public String cardNumber;
    public long rrn;


    public static PaymentObject create(RealmRoomMessageWalletPayment realmPayment) {
        PaymentObject paymentObject = new PaymentObject();
        paymentObject.fromUserId = realmPayment.getFromUserId();
        paymentObject.toUserId = realmPayment.getToUserId();
        paymentObject.amount = realmPayment.getAmount();
        paymentObject.traceNumber = realmPayment.getTraceNumber();
        paymentObject.invoiceNumber = realmPayment.getInvoiceNumber();
        paymentObject.payTime = realmPayment.getPayTime();
        paymentObject.description = realmPayment.getDescription();
        paymentObject.cardNumber = realmPayment.getCardNumber();
        paymentObject.rrn = realmPayment.getRrn();

        return paymentObject;

    }


}
