package net.iGap.mobileBank.repository.model;

import com.google.gson.annotations.SerializedName;

public class ChequeModel {

    @SerializedName("issue_date")
    private String issue_date;

    @SerializedName("number")
    private String number;

    @SerializedName("number_of_partial_cash_cheque")
    private int partialCash;

    @SerializedName("number_of_pass_cheque")
    private int passCheque;

    @SerializedName("number_of_permanent_blocked_cheque")
    private int permanentBlocked;

    @SerializedName("number_of_reject_cheque")
    private int reject;

    @SerializedName("number_of_temporary_block_cheque")
    private int temporaryBlock;

    @SerializedName("number_of_unused_cheque")
    private int unusedCheque;

    @SerializedName("page_count")
    private int pageCount;
}
