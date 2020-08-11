package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankPayLoanModel {

    @SerializedName("applied_amount")
    private String appliedAmount;

    @SerializedName("document_number")
    private String documentNumber;

    @SerializedName("document_title")
    private String documentُُTُitle;

    public String getAppliedAmount() {
        return appliedAmount;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getDocumentُُTُitle() {
        return documentُُTُitle;
    }
}
