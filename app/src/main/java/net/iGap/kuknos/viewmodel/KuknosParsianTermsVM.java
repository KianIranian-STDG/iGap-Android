package net.iGap.kuknos.viewmodel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.Model.KuknosError;
import net.iGap.kuknos.Model.KuknosPaymentResponse;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.kuknos.Repository.PanelRepo;

import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.util.ArrayList;
import java.util.List;

public class KuknosParsianTermsVM extends BaseAPIViewModel {

    private ObservableField<String> amount = new ObservableField<>();
    private ObservableField<Boolean> amountEnable = new ObservableField<>(false);
    private ObservableField<String> sum = new ObservableField<>();
    private ObservableField<String> assetPrice = new ObservableField<>("قیمت هر پیمان: ...");
    private MutableLiveData<KuknosError> error;
    private MutableLiveData<KuknosPaymentResponse> paymentData = new MutableLiveData<>(null);
    // 0 : nothing 1: connecting to server 2: connecting to bank
    private MutableLiveData<Integer> progressState;
    //go to bank
    private MutableLiveData<Boolean> nextPage;
    private SingleLiveEvent<Boolean> goToPin = new SingleLiveEvent<>();
    private PanelRepo panelRepo = new PanelRepo();
    private List<Boolean> termsAndConditionIsChecked = new ArrayList<>();

    public KuknosParsianTermsVM() {
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(0);
        nextPage = new MutableLiveData<>(false);
        for (int i = 0; i < 3; i++) {
            termsAndConditionIsChecked.add(true);
        }
    }

    public void onSubmitBtn() {
        if (checkForm()) {
            return;
        }
        sendDataServer();
    }

    public void sendDataServer() {
        progressState.setValue(1);
        panelRepo.setOptions(this, new ResponseCallback<KuknosResponseModel<SubmitTransactionResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<SubmitTransactionResponse> data) {
                progressState.setValue(0);
                nextPage.setValue(true);
            }

            @Override
            public void onError(String errorM) {
                progressState.setValue(0);
                error.setValue(new KuknosError(true, "wrong pin", errorM, R.string.kuknos_buyP_failS));
            }

            @Override
            public void onFailed() {
                progressState.setValue(0);
                error.setValue(new KuknosError(true, "wrong pin", "1", R.string.kuknos_buyP_failS));
            }
        });
    }

    private boolean checkForm() {
        //Terms and Condition
        for (int i = 0; i < 3; i++) {
            if (!termsAndConditionIsChecked.get(i)) {
                error.setValue(new KuknosError(true, "TermsAndConditionError", "1", R.string.kuknos_parsianTerms_errorTermAndCondition));
                return true;
            }
        }
        return false;
    }

    public void termsOnCheckChange(int position, boolean isChecked) {
        termsAndConditionIsChecked.set(position, isChecked);
    }

    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Boolean> nextPage) {
        this.nextPage = nextPage;
    }

    public ObservableField<String> getAmount() {
        return amount;
    }

    public void setAmount(ObservableField<String> amount) {
        this.amount = amount;
    }

    public ObservableField<String> getSum() {
        return sum;
    }

    public MutableLiveData<Integer> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Integer> progressState) {
        this.progressState = progressState;
    }

    public ObservableField<String> getAssetPrice() {
        return assetPrice;
    }

    public void setAssetPrice(ObservableField<String> assetPrice) {
        this.assetPrice = assetPrice;
    }

    public SingleLiveEvent<Boolean> getGoToPin() {
        return goToPin;
    }

    public ObservableField<Boolean> getAmountEnable() {
        return amountEnable;
    }

    public MutableLiveData<KuknosPaymentResponse> getPaymentData() {
        return paymentData;
    }

}
