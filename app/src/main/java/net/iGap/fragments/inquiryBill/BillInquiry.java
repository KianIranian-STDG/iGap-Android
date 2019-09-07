package net.iGap.fragments.inquiryBill;

import android.os.Parcel;
import android.os.Parcelable;

public class BillInquiry implements Parcelable {
    private long billId;
    private long payId;
    private long amount;
    private String message;

    public BillInquiry(long billId, long payId, long amount, String message) {
        this.billId = billId;
        this.payId = payId;
        this.amount = amount;
        this.message = message;
    }

    protected BillInquiry(Parcel in) {
        billId = in.readLong();
        payId = in.readLong();
        amount = in.readLong();
        message = in.readString();
    }

    public static final Creator<BillInquiry> CREATOR = new Creator<BillInquiry>() {
        @Override
        public BillInquiry createFromParcel(Parcel in) {
            return new BillInquiry(in);
        }

        @Override
        public BillInquiry[] newArray(int size) {
            return new BillInquiry[size];
        }
    };

    public long getBillId() {
        return billId;
    }

    public long getPayId() {
        return payId;
    }

    public long getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(billId);
        dest.writeLong(payId);
        dest.writeLong(amount);
        dest.writeString(message);
    }
}
