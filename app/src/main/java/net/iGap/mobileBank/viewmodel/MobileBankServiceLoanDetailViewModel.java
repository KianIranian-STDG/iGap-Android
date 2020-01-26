package net.iGap.mobileBank.viewmodel;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankServiceLoanDetailModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;

public class MobileBankServiceLoanDetailViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<BankServiceLoanDetailModel> loan = new MutableLiveData<>();
    private ObservableInt pageVisibility = new ObservableInt(View.INVISIBLE);
    private MutableLiveData<ErrorModel> errorM = new MutableLiveData<>();

    private String loanNumber;
    private static final String TAG = "MobileBankServiceLoanDe";

    public MobileBankServiceLoanDetailViewModel() {
    }

    public void init() {
        getLoanDetail(0);
    }

    public void getLoanDetail(int offset) {
        if (offset == 0) {
            loan.setValue(null);
        }
        showLoading.set(View.VISIBLE);
        // set bills
        MobileBankRepository.getInstance().getLoanDetail(loanNumber, offset, 30, this,
                new ResponseCallback<BaseMobileBankResponse<BankServiceLoanDetailModel>>() {
                    @Override
                    public void onSuccess(BaseMobileBankResponse<BankServiceLoanDetailModel> data) {
                        loan.setValue(data.getData());
                        showLoading.set(View.GONE);
                        pageVisibility.set(View.VISIBLE);
                    }

                    @Override
                    public void onError(String error) {
                        showRequestErrorMessage.setValue(error);
                        showLoading.set(View.GONE);
                    }

                    @Override
                    public void onFailed() {
                        showLoading.set(View.GONE);
                    }
                });
    }

    private String compatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
    }

    public ObservableInt getPageVisibility() {
        return pageVisibility;
    }

    public MutableLiveData<ErrorModel> getErrorM() {
        return errorM;
    }

    public MutableLiveData<BankServiceLoanDetailModel> getLoan() {
        return loan;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }
}
