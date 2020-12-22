package net.iGap.fragments.inquiryBill;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;
import net.iGap.proto.ProtoMplGetBillToken;
import net.iGap.request.RequestMplGetBillToken;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ShowBillInquiryViewModel extends ViewModel {

    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private ObservableInt showMidTermMessage = new ObservableInt(View.VISIBLE);
    private ObservableInt showLastTermMessage = new ObservableInt(View.VISIBLE);
    private ObservableInt lastTermHaveAmount = new ObservableInt(View.VISIBLE);
    private ObservableInt midTermHaveAmount = new ObservableInt(View.VISIBLE);
    private BillInquiryResponse response;

    ShowBillInquiryViewModel(BillInquiryResponse response) {
        this.response = response;

        showMidTermMessage.set(response.getMidTerm().getMessage().equals("") ? View.GONE : View.VISIBLE);
        showLastTermMessage.set(response.getLastTerm().getMessage().equals("") ? View.GONE : View.VISIBLE);

        if (response.getLastTerm().getAmount() > 0) {
            lastTermHaveAmount.set(View.VISIBLE);
        } else {
            lastTermHaveAmount.set(View.GONE);
        }

        if (response.getMidTerm().getAmount() > 0) {
            midTermHaveAmount.set(View.VISIBLE);
        } else {
            midTermHaveAmount.set(View.GONE);
        }
    }

    public BillInquiryResponse getResponse() {
        return response;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public ObservableInt getShowMidTermMessage() {
        return showMidTermMessage;
    }

    public ObservableInt getShowLastTermMessage() {
        return showLastTermMessage;
    }

    public ObservableInt getLastTermHaveAmount() {
        return lastTermHaveAmount;
    }

    public ObservableInt getMidTermHaveAmount() {
        return midTermHaveAmount;
    }

    public String getCommaSeparatedPrice(long price) {
        DecimalFormat anotherDFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        anotherDFormat.setGroupingUsed(true);
        anotherDFormat.setGroupingSize(3);
        return anotherDFormat.format(price);
    }

    public void onLastTermPayment() {
        if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            new RequestMplGetBillToken().mplGetBillToken(response.getLastTerm().getBillId(), response.getLastTerm().getPayId(), ProtoMplGetBillToken.MplGetBillToken.Type.LAST_TERM_VALUE);
            goBack.setValue(true);
        } else {
            showErrorMessage.setValue(R.string.there_is_no_connection_to_server);
        }
    }

    public void onMidTermPayment() {
        if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            new RequestMplGetBillToken().mplGetBillToken(response.getMidTerm().getBillId(), response.getMidTerm().getPayId(), ProtoMplGetBillToken.MplGetBillToken.Type.MID_TERM_VALUE);
            goBack.setValue(true);
        } else {
            showErrorMessage.setValue(R.string.there_is_no_connection_to_server);
        }
    }

}
