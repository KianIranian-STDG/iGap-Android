package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosVirtualRefund {

    @SerializedName("ref_no")
    private int refNumber;

    public KuknosVirtualRefund(int refNumber) {
        this.refNumber = refNumber;
    }

    public KuknosVirtualRefund() {
    }

    public int getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(int refNumber) {
        this.refNumber = refNumber;
    }
}
