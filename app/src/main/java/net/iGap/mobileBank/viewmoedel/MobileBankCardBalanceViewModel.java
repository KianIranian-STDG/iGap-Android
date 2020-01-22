package net.iGap.mobileBank.viewmoedel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;

public class MobileBankCardBalanceViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<Boolean> complete = new MutableLiveData<>();

    private ObservableField<String> cardNumber = new ObservableField<>();
    private ObservableField<Integer> cardNumberError = new ObservableField<>(0);
    private ObservableField<String> password = new ObservableField<>();
    private ObservableField<Integer> passwordError = new ObservableField<>(0);
    private ObservableField<String> CVV = new ObservableField<>();
    private ObservableField<Integer> CVVError = new ObservableField<>(0);
    private ObservableField<String> date = new ObservableField<>();
    private ObservableField<Integer> dateError = new ObservableField<>(0);
    private ObservableField<Integer> progress = new ObservableField<>(R.string.inquiry);

    public MobileBankCardBalanceViewModel() {

    }

    private boolean checkData() {
        // check cardNumber
        if (cardNumber.get() != null) {
            if (!cardNumber.get().isEmpty()) {

            } else {
                cardNumberError.set(R.string.mobile_bank_balance_cardError);
                return false;
            }
        } else {
            cardNumberError.set(R.string.mobile_bank_balance_cardError);
            return false;
        }
        // check password
        if (password.get() != null) {
            if (!password.get().isEmpty()) {

            } else {
                passwordError.set(R.string.mobile_bank_balance_passwordError);
                return false;
            }
        } else {
            passwordError.set(R.string.mobile_bank_balance_passwordError);
            return false;
        }
        // check CVV
        if (CVV.get() != null) {
            if (!CVV.get().isEmpty()) {

            } else {
                CVVError.set(R.string.mobile_bank_balance_CVVError);
                return false;
            }
        } else {
            CVVError.set(R.string.mobile_bank_balance_CVVError);
            return false;
        }
        // check date
        if (date.get() != null) {
            if (!date.get().isEmpty()) {
                return true;
            } else {
                dateError.set(R.string.mobile_bank_balance_dateError);
                return false;
            }
        } else {
            dateError.set(R.string.mobile_bank_balance_dateError);
            return false;
        }
    }

    private void sendData() {
        progress.set(R.string.news_add_comment_load);
        /*repo.postNewsComment(newsID, CVV.get(), cardNumber.get(), password.get(), this, new ResponseCallback<NewsDetail>() {
            @Override
            public void onSuccess(NewsDetail data) {
                progress.set(R.string.news_add_comment_success);
                complete.setValue(true);
            }

            @Override
            public void onError(String error) {
                progress.set(R.string.news_add_comment_fail);
                complete.setValue(false);
            }

            @Override
            public void onFailed() {
                progress.set(R.string.connection_error);
                complete.setValue(false);
            }
        });*/
    }

    public void onContinueBtnClick() {
        if (!checkData())
            return;
        sendData();
    }

    public ObservableField<Integer> getProgress() {
        return progress;
    }

    public void setProgress(ObservableField<Integer> progress) {
        this.progress = progress;
    }

    public ObservableField<String> getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber.set(cardNumber);
    }

    public ObservableField<Integer> getCardNumberError() {
        return cardNumberError;
    }

    public void setCardNumberError(ObservableField<Integer> cardNumberError) {
        this.cardNumberError = cardNumberError;
    }

    public ObservableField<String> getPassword() {
        return password;
    }

    public void setPassword(ObservableField<String> password) {
        this.password = password;
    }

    public ObservableField<Integer> getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(ObservableField<Integer> passwordError) {
        this.passwordError = passwordError;
    }

    public ObservableField<String> getCVV() {
        return CVV;
    }

    public void setCVV(ObservableField<String> CVV) {
        this.CVV = CVV;
    }

    public ObservableField<Integer> getCVVError() {
        return CVVError;
    }

    public void setCVVError(ObservableField<Integer> CVVError) {
        this.CVVError = CVVError;
    }

    public MutableLiveData<Boolean> getComplete() {
        return complete;
    }

    public void setComplete(MutableLiveData<Boolean> complete) {
        this.complete = complete;
    }

    public ObservableField<String> getDate() {
        return date;
    }

    public ObservableField<Integer> getDateError() {
        return dateError;
    }
}
