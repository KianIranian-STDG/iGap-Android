package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosCreateAccountOpResponse extends KuknosOperationResponse.OperationResponse  {
    @SerializedName("account")
    protected String account;
    @SerializedName("funder")
    protected String funder;
    @SerializedName("starting_balance")
    protected String startingBalance;

    public String getAccount() {
        return account;
    }

    public String getStartingBalance() {
        return startingBalance;
    }

    public String getFunder() {
        return funder;
    }
}
