package net.iGap.observers.interfaces;

public interface RealmMoneyTransfer {
    long getFromUserId();

    void setFromUserId(long fromUserId);

    long getToUserId();

    void setToUserId(long toUserId);

    long getAmount();

    void setAmount(long amount);

    long getTraceNumber();

    void setTraceNumber(long traceNumber);

    long getInvoiceNumber();

    void setInvoiceNumber(long invoiceNumber);

    int getPayTime();

    void setPayTime(int payTime);

    String getDescription();

    void setDescription(String description);

    String getCardNumber();

    void setCardNumber(String cardNumber);

    long getRrn();

    void setRrn(long rrn);
}
