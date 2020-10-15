package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperNumerical;
import net.iGap.request.RequestMplGetBillToken;

import org.jetbrains.annotations.NotNull;

public class FragmentPaymentBillViewModel extends BaseViewModel {

    private ObservableInt haveAmount = new ObservableInt(View.VISIBLE);
    private ObservableInt showLoadingView = new ObservableInt(View.INVISIBLE);
    private ObservableInt showScannerButton = new ObservableInt(View.GONE);
    private ObservableBoolean enabledPaymentButton = new ObservableBoolean(true);
    private ObservableField<String> payId = new ObservableField<>("");
    private ObservableField<String> billId = new ObservableField<>("");
    private ObservableField<String> billAmount = new ObservableField<>("");
    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goToScannerPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<Boolean> hideKeyword = new MutableLiveData<>();
    private MutableLiveData<Integer> billTypeImage = new MutableLiveData<>();

    private boolean isPolice;

    public FragmentPaymentBillViewModel(boolean isPolice, String PID_Str, String BID_Str) {

        this.isPolice = isPolice;

        if (isPolice) {
            haveAmount.set(View.GONE);
            billTypeImage.setValue(R.mipmap.trafic_police);
        } else {
            showScannerButton.set(View.VISIBLE);
        }

        if (PID_Str != null) {
            payId.set(PID_Str);
        } else {
            payId.set("");
        }

        if (BID_Str != null) {
            billId.set(BID_Str);
        } else {
            billId.set("");
        }
    }

    public MutableLiveData<Integer> getBillTypeImage() {
        return billTypeImage;
    }

    public ObservableInt getHaveAmount() {
        return haveAmount;
    }

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public ObservableInt getShowScannerButton() {
        return showScannerButton;
    }

    public ObservableBoolean getEnabledPaymentButton() {
        return enabledPaymentButton;
    }

    public ObservableField<String> getPayId() {
        return payId;
    }

    public ObservableField<String> getBillId() {
        return billId;
    }

    public ObservableField<String> getBillAmount() {
        return billAmount;
    }

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<Boolean> getGoToScannerPage() {
        return goToScannerPage;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<Boolean> getHideKeyword() {
        return hideKeyword;
    }

    public void onTextChangedBillId(String s) {
        if (!isPolice) {
            if (s.length() == 13) {
                billTypeImage.setValue(getCompany(s.substring(11, 12)));
            }
        }
    }

    public void onTextChangedPaymentCode(String s) {
        if (s != null && s.length() > 5 && s.length() < 12) {
            int price = Integer.parseInt(s.substring(0, s.length() - 5)) * 1000;
            billAmount.set(new HelperNumerical().getCommaSeparatedPrice(price));
        }
    }

    public void onClickBarCode() {
        goToScannerPage.setValue(true);
    }

    public void onPayBillClick(String billId, String payId) {
        hideKeyword.setValue(true);
        if (getRequestManager().isUserLogin()) {
            if (isPolice) {
                if (billId.length() == 0) {
                    showErrorMessage.setValue(R.string.biling_id_not_valid);
                    return;
                }

            } else {
                if (billId.length() != 13) {
                    showErrorMessage.setValue(R.string.biling_id_not_valid);
                    return;
                }
            }

            if (isPolice) {
                if (payId.length() == 0) {
                    showErrorMessage.setValue(R.string.pay_id_is_not_valid);
                    return;
                }
            } else {
                if (payId.length() > 13 || payId.length() < 5) {
                    showErrorMessage.setValue(R.string.pay_id_is_not_valid);
                    return;
                }
            }


            G.onMplResult = error -> {
                showLoadingView.set(View.INVISIBLE);
                if (error) {
                    enabledPaymentButton.set(true);
                } else {
                    goBack.setValue(true);
                }

            };

            showLoadingView.set(View.VISIBLE);

            RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
            requestMplGetBillToken.mplGetBillToken(Long.parseLong(billId), Long.parseLong(payId));

            enabledPaymentButton.set(false);
        } else {
            showErrorMessage.setValue(R.string.there_is_no_connection_to_server);
        }
    }

    public void setDataFromBarcodeScanner(String result) {
        if (result != null) {
            if (result.length() == 26) {
                String billId = result.substring(0, 13);
                String payId = result.substring(13, 26);
                String company_type = result.substring(11, 12);
                String price = result.substring(13, 21);
                while (payId.startsWith("0")) {
                    payId = payId.substring(1);
                }
                this.billId.set(billId);
                this.payId.set(payId);
                this.billTypeImage.setValue(getCompany(company_type));
                this.haveAmount.set(View.VISIBLE);
                this.billAmount.set(new HelperNumerical().getCommaSeparatedPrice((Integer.parseInt(price) * 1000)));
            }
        }
    }

    private @DrawableRes
    int getCompany(@NotNull String value) {
        switch (value) {
            case "1":
                return R.drawable.bill_water_pec;
            case "2":
                return R.drawable.bill_elc_pec;
            case "3":
                return R.drawable.bill_gaz_pec;
            case "4":
                return R.drawable.bill_telecom_pec;
            case "5":
                return R.drawable.bill_mci_pec;
            case "6":
                return R.drawable.bill_shahrdari_pec;
            default:
                return R.mipmap.empty;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        G.onMplResult = null;
    }
}
