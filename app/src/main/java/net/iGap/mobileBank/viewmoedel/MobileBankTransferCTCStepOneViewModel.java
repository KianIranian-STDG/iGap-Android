package net.iGap.mobileBank.viewmoedel;

import android.view.View;

import androidx.databinding.ObservableField;

import net.iGap.api.apiService.BaseAPIViewModel;

public class MobileBankTransferCTCStepOneViewModel extends BaseAPIViewModel {

    private ObservableField<Integer> originCardNameVisibility = new ObservableField<>(View.GONE);
    private ObservableField<Integer> destCardNameVisibility = new ObservableField<>(View.GONE);
    private ObservableField<Integer> originBankVisibility = new ObservableField<>(View.INVISIBLE);
    private ObservableField<Integer> destBankVisibility = new ObservableField<>(View.INVISIBLE);
    private ObservableField<Integer> amountTextVisibility = new ObservableField<>(View.INVISIBLE);

    private ObservableField<String> amountTranslate = new ObservableField<>("");
    private ObservableField<String> amountEntry = new ObservableField<>("");
    private ObservableField<String> originCardName = new ObservableField<>("Fetching data...");
    private ObservableField<String> destCardName = new ObservableField<>("Fetching data...");

    private Boolean completeOrigin = false;
    private Boolean completeDest = false;
    private String originCard = null;
    private String destCard = null;
    private String amount = null;

    private void getCardInfo(String cardNum, boolean isOrigin) {
        // api call
    }

    private boolean getCardBank(String cardNum) {
        // bank logo
        return cardNum != null;
    }

    private void translateValue() {
        // show text of amount
        if (amount != null)
            amountTextVisibility.set(View.VISIBLE);
        else
            amountTextVisibility.set(View.INVISIBLE);

        if (amount.length() > 10) {
            amountTranslate.set("error");
            return;
        }

        if (amount.length() == 0) {
            amountTranslate.set("");
            return;
        }

        String translate = "";
        int position = 0;
        if (amount.charAt(amount.length() - 1) != '0')
            translate = " و " + amount.charAt(amount.length() - 1) + getCurrency(position);

        if (amount.length() > 1) {
            int original;
            if (amount.length() == 2)
                original = Integer.parseInt("" + amount.charAt(0));
            else
                original = Integer.parseInt(amount.substring(0, amount.length() - 1));

            while (original != 0) {
                position++;
                if (original % 1000 != 0) {
                    /*if (translate.length() != 0)
                        translate = " و" + translate;*/
                    translate = " و " + original % 1000 + getCurrency(position) + translate;
                } else {
                    if (position == 1)
                        translate = getCurrency(position) + translate;
                }
                original = original / 1000;
            }
        }
        translate = translate.substring(2);
        amountTranslate.set(translate);
    }

    private String getCurrency(int position) {
        switch (position) {
            case 0:
                return " ریال";
            case 1:
                return " تومان";
            case 2:
                return " هزار";
            case 3:
                return " میلیون";
            case 4:
                return " میلیارد";
            default:
                return "";
        }
    }

    public ObservableField<Integer> getOriginCardNameVisibility() {
        return originCardNameVisibility;
    }

    public ObservableField<Integer> getDestCardNameVisibility() {
        return destCardNameVisibility;
    }

    public ObservableField<Integer> getAmountTextVisibility() {
        return amountTextVisibility;
    }

    public void setCompleteOrigin(Boolean completeOrigin, String cardNum) {
        this.completeOrigin = completeOrigin;
        if (completeOrigin) {
            originCardNameVisibility.set(View.VISIBLE);
            originCard = cardNum;
            getCardInfo(cardNum, true);
        } else {
            originCardNameVisibility.set(View.GONE);
            originCard = cardNum.substring(0, 5);
        }
    }

    public void setCompleteDest(Boolean completeDest, String cardNum) {
        this.completeDest = completeDest;
        if (completeDest) {
            destCardNameVisibility.set(View.VISIBLE);
            destCard = cardNum;
            getCardInfo(cardNum, false);
        } else {
            destCardNameVisibility.set(View.GONE);
            destCard = cardNum.substring(0, 5);
        }
    }

    public void setOriginCard(String originCard) {
        this.originCard = originCard;
        if (getCardBank(originCard)) {
            originBankVisibility.set(View.VISIBLE);
        } else {
            originBankVisibility.set(View.INVISIBLE);
        }
    }

    public void setDestCard(String destCard) {
        this.destCard = destCard;
        if (getCardBank(destCard)) {
            destBankVisibility.set(View.VISIBLE);
        } else {
            destBankVisibility.set(View.INVISIBLE);
        }
    }

    public void setAmount(String amount) {
        this.amount = amount;
        translateValue();
    }

    public ObservableField<Integer> getOriginBankVisibility() {
        return originBankVisibility;
    }

    public ObservableField<Integer> getDestBankVisibility() {
        return destBankVisibility;
    }

    public ObservableField<String> getAmountTranslate() {
        return amountTranslate;
    }

    public ObservableField<String> getOriginCardName() {
        return originCardName;
    }

    public ObservableField<String> getDestCardName() {
        return destCardName;
    }

    public ObservableField<String> getAmountEntry() {
        return amountEntry;
    }

    public void setAmountEntry(ObservableField<String> amountEntry) {
        this.amountEntry = amountEntry;
    }

    public String getAmount() {
        return amount;
    }
}
