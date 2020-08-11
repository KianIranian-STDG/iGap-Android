package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

public class BankChequeBookListModel {

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

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getPartialCash() {
        return partialCash;
    }

    public void setPartialCash(int partialCash) {
        this.partialCash = partialCash;
    }

    public int getPassCheque() {
        return passCheque;
    }

    public void setPassCheque(int passCheque) {
        this.passCheque = passCheque;
    }

    public int getPermanentBlocked() {
        return permanentBlocked;
    }

    public void setPermanentBlocked(int permanentBlocked) {
        this.permanentBlocked = permanentBlocked;
    }

    public int getReject() {
        return reject;
    }

    public void setReject(int reject) {
        this.reject = reject;
    }

    public int getTemporaryBlock() {
        return temporaryBlock;
    }

    public void setTemporaryBlock(int temporaryBlock) {
        this.temporaryBlock = temporaryBlock;
    }

    public int getUnusedCheque() {
        return unusedCheque;
    }

    public void setUnusedCheque(int unusedCheque) {
        this.unusedCheque = unusedCheque;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
