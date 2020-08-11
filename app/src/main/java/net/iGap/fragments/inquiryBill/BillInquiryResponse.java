package net.iGap.fragments.inquiryBill;

import android.os.Parcel;
import android.os.Parcelable;

import net.iGap.proto.ProtoBillInquiryMci;
import net.iGap.proto.ProtoBillInquiryTelecom;

public class BillInquiryResponse implements Parcelable {

    private BillInquiry midTerm;
    private BillInquiry lastTerm;

    public BillInquiryResponse(Object midTerm, Object lastTerm) {
        if (midTerm instanceof net.iGap.proto.ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo &&
                lastTerm instanceof net.iGap.proto.ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo) {
            this.midTerm = new BillInquiry(((ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo) midTerm).getBillId(),
                    ((ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo) midTerm).getPayId(),
                    ((ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo) midTerm).getAmount(),
                    ""
            );
            this.lastTerm = new BillInquiry(((ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo) lastTerm).getBillId(),
                    ((ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo) lastTerm).getPayId(),
                    ((ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo) lastTerm).getAmount(),
                    ""
            );
        } else if (midTerm instanceof net.iGap.proto.ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo &&
                lastTerm instanceof net.iGap.proto.ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) {
            this.midTerm = new BillInquiry(((ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) midTerm).getBillId(),
                    ((ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) midTerm).getPayId(),
                    ((ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) midTerm).getAmount(),
                    ((ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) midTerm).getMessage()
            );
            this.lastTerm = new BillInquiry(((ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) lastTerm).getBillId(),
                    ((ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) lastTerm).getPayId(),
                    ((ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) lastTerm).getAmount(),
                    ((ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo) lastTerm).getMessage()
            );
        }
    }

    protected BillInquiryResponse(Parcel in) {
        midTerm = in.readParcelable(BillInquiry.class.getClassLoader());
        lastTerm = in.readParcelable(BillInquiry.class.getClassLoader());
    }

    public static final Creator<BillInquiryResponse> CREATOR = new Creator<BillInquiryResponse>() {
        @Override
        public BillInquiryResponse createFromParcel(Parcel in) {
            return new BillInquiryResponse(in);
        }

        @Override
        public BillInquiryResponse[] newArray(int size) {
            return new BillInquiryResponse[size];
        }
    };

    public BillInquiry getMidTerm() {
        return midTerm;
    }

    public BillInquiry getLastTerm() {
        return lastTerm;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(midTerm, flags);
        dest.writeParcelable(lastTerm, flags);
    }
}
