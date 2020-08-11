package net.iGap.fragments.inquiryBill;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.R;
import net.iGap.observers.interfaces.GeneralResponseCallBack;
import net.iGap.request.RequestBillInquiryTelecom;

public class PaymentInquiryTelephoneViewModel extends ViewModel {

    private ObservableBoolean enableInquiryButton = new ObservableBoolean(true);
    private ObservableInt showLoadingView = new ObservableInt(View.GONE);
    private MutableLiveData<Boolean> hideKeyword = new MutableLiveData<>();
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<BillInquiryResponse> goToShowInquiryBillPage = new MutableLiveData<>();

    public ObservableBoolean getEnableInquiryButton() {
        return enableInquiryButton;
    }

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public MutableLiveData<Boolean> getHideKeyword() {
        return hideKeyword;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<BillInquiryResponse> getGoToShowInquiryBillPage() {
        return goToShowInquiryBillPage;
    }

    public void onInquiryClick(String areaCode, String telephoneNumber) {
        hideKeyword.setValue(true);
        enableInquiryButton.set(false);
        if (G.userLogin) {
            if (telephoneNumber.length() < 8 || areaCode.length() < 3) {
                enableInquiryButton.set(true);
                showErrorMessage.setValue(R.string.phone_number_is_not_valid);
            } else {
                showLoadingView.set(View.VISIBLE);
                new RequestBillInquiryTelecom().billInquiryTelecom(Integer.parseInt(areaCode), Integer.parseInt(telephoneNumber), new GeneralResponseCallBack<BillInquiryResponse>() {
                    @Override
                    public void onSuccess(BillInquiryResponse data) {
                        showLoadingView.set(View.GONE);
                        goToShowInquiryBillPage.postValue(data);
                        enableInquiryButton.set(true);
                    }

                    @Override
                    public void onError(int major, int minor) {
                        showLoadingView.set(View.GONE);
                        enableInquiryButton.set(true);
                        if (major == 5 && minor == 1) {
                            showErrorMessage.postValue(R.string.connection_error);
                        } else {
                            showErrorMessage.postValue(R.string.error);
                        }
                    }
                });
            }
        } else {
            showErrorMessage.setValue(R.string.there_is_no_connection_to_server);
            enableInquiryButton.set(true);
        }
    }
}
