package net.iGap.fragments.inquiryBill;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.OperatorType;
import net.iGap.observers.interfaces.GeneralResponseCallBack;
import net.iGap.request.RequestBillInquiryMci;

public class PaymentInquiryMobileViewModel extends ViewModel {

    private ObservableInt showLoadingView = new ObservableInt(View.GONE);
    private ObservableBoolean enableButton = new ObservableBoolean(true);
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<BillInquiryResponse> goToShowInquiryBillPage = new MutableLiveData<>();

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public ObservableBoolean getEnableButton() {
        return enableButton;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<BillInquiryResponse> getGoToShowInquiryBillPage() {
        return goToShowInquiryBillPage;
    }

    public void onInquiryClick(String phoneNumber) {
        if (G.userLogin) {
            if (!isNumeric(phoneNumber) || phoneNumber.length() < 11) {
                showErrorMessage.setValue(R.string.phone_number_is_not_valid);
            } else {
                if (new OperatorType().isMci(phoneNumber.substring(0, 4))) {
                    showLoadingView.set(View.VISIBLE);
                    enableButton.set(false);
                    new RequestBillInquiryMci().billInquiryMci(Long.parseLong(phoneNumber), new GeneralResponseCallBack<BillInquiryResponse>() {
                        @Override
                        public void onSuccess(BillInquiryResponse data) {
                            showLoadingView.set(View.GONE);
                            goToShowInquiryBillPage.postValue(data);
                            enableButton.set(true);
                        }

                        @Override
                        public void onError(int major, int minor) {
                            showLoadingView.set(View.GONE);
                            enableButton.set(true);
                            if (major == 5 && minor == 1) {
                                showErrorMessage.postValue(R.string.connection_error);
                            } else {
                                showErrorMessage.postValue(R.string.error);
                            }
                        }
                    });
                } else {
                    showErrorMessage.setValue(R.string.mci_opreator_check);
                }
            }
        } else {
            showErrorMessage.setValue(R.string.there_is_no_connection_to_server);
        }
    }

    private boolean isNumeric(String strNum) {
        try {
            Long.parseLong(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }
}
