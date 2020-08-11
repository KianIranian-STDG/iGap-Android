package net.iGap.model.mobileBank;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BankBlockCheque {
    @SerializedName("blockingStatus")
    private List<Status> blockStatus;

    public List<Status> getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(List<Status> blockStatus) {
        this.blockStatus = blockStatus;
    }

    public class Status {
        @SerializedName("chequeNumber")
        private String chNumber;
        @SerializedName("blockingStatus")
        private String chStatus;

        public String getChNumber() {
            return chNumber;
        }

        public void setChNumber(String chNumber) {
            this.chNumber = chNumber;
        }

        public String getChStatus() {
            return chStatus;
        }

        public void setChStatus(String chStatus) {
            this.chStatus = chStatus;
        }
    }
}
