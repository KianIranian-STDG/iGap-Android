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

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.model.OperatorType;

import org.jetbrains.annotations.NotNull;

@Deprecated
public class FragmentPaymentChargeViewModel extends BaseAPIViewModel {

    public static final String MCI = "mci";
    public static final String MTN = "mtn";
    public static final String RIGHTEL = "rightel";

    private ObservableInt showDetail = new ObservableInt(View.GONE);
    private ObservableInt observeTarabord = new ObservableInt(View.GONE);
    private ObservableBoolean observeEnabledPayment = new ObservableBoolean(true);

    private MutableLiveData<Integer> onOpereatorChange = new MutableLiveData<>();
    private MutableLiveData<Integer> onPriceChange = new MutableLiveData<>();
    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<String> goToPaymentPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> needUpdate = new MutableLiveData<>();
    private MutableLiveData<ErrorModel> showMciPaymentError = new MutableLiveData<>();
    private MutableLiveData<Boolean> hideKeyWord = new MutableLiveData<>();
    private MutableLiveData<Integer> showError = new MutableLiveData<>();

    private int selectedChargeTypePosition = 0;
    private int selectedPricePosition = 0;

    private OperatorType.Type operatorType;


    public FragmentPaymentChargeViewModel() {

    }

    public ObservableInt getShowDetail() {
        return showDetail;
    }

    public ObservableInt getObserveTarabord() {
        return observeTarabord;
    }

    public ObservableBoolean getObserveEnabledPayment() {
        return observeEnabledPayment;
    }

    public MutableLiveData<Integer> getOnOpereatorChange() {
        return onOpereatorChange;
    }

    public MutableLiveData<Integer> getOnPriceChange() {
        return onPriceChange;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<String> getGoToPaymentPage() {
        return goToPaymentPage;
    }

    public MutableLiveData<Boolean> getHideKeyWord() {
        return hideKeyWord;
    }

    public MutableLiveData<Integer> getShowError() {
        return showError;
    }

    public MutableLiveData<ErrorModel> getShowMciPaymentError() {
        return showMciPaymentError;
    }

    public void phoneNumberTextChangeListener(String phoneNumber) {
        showDetail.set(phoneNumber.length() == 11 ? View.VISIBLE : View.GONE);
        observeTarabord.set(View.GONE);
        setOperator(phoneNumber);
    }

    /*public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 11) {
            if (fragmentPaymentChargeBinding.fpcCheckBoxTrabord.isChecked()) {
                fragmentPaymentChargeBinding.fpcCheckBoxTrabord.setChecked(false);
            } else {
                setOperator(s.toString());
            }
        }
    }*/

    public void checkBoxTarabordChanged(boolean checked, String phoneNumber) {
        if (checked) {
            observeTarabord.set(View.VISIBLE);
            /*fragmentPaymentChargeBinding.fpcSpinnerOperator.setSelection(0);*/
        } else {
            observeTarabord.set(View.GONE);
            setOperator(phoneNumber);
        }
    }

    public void onItemSelecteSpinerOperator(int position) {
        switch (position) {
            case 0:
                operatorType = null;
                onOpereatorChange.setValue(null);
                selectedChargeTypePosition = 0;
                onPriceChange.setValue(null);
                break;
            case 1:
                setAdapterValue(OperatorType.Type.HAMRAH_AVAL);
                break;
            case 2:
                setAdapterValue(OperatorType.Type.IRANCELL);
                break;
            case 3:
                setAdapterValue(OperatorType.Type.RITEL);
                break;
        }
    }

    public void onItemSelecteSpinerType(int position) {
        selectedChargeTypePosition = position;
    }

    public void onItemSelecteSpinerPrice(int position) {
        selectedPricePosition = position;
    }

    //******************************************************************************************************

    private void setOperator(String phone) {
        if (phone.length() == 11) {
            String s = phone.substring(0, 4);
            OperatorType.Type opt = new OperatorType().getOperation(s);
            if (opt != null) {
                setAdapterValue(opt);
            } else {
                observeTarabord.set(View.VISIBLE);
            }
        }
    }

    private void setAdapterValue(@NotNull OperatorType.Type operator) {
        switch (operator) {
            case HAMRAH_AVAL:
                operatorType = OperatorType.Type.HAMRAH_AVAL;
                onOpereatorChange.setValue(R.array.charge_type_hamrahe_aval);
                onPriceChange.setValue(R.array.charge_price);
                break;
            case IRANCELL:
                operatorType = OperatorType.Type.IRANCELL;
                onOpereatorChange.setValue(R.array.charge_type_irancell);
                onPriceChange.setValue(R.array.charge_price_irancell);
                break;
            case RITEL:
                operatorType = OperatorType.Type.RITEL;
                onOpereatorChange.setValue(R.array.charge_type_ritel);
                onPriceChange.setValue(R.array.charge_price);
                break;
        }
    }


    public static boolean isNumeric(String strNum) {
        try {
            long d = Long.parseLong(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }


    public void onBuyClick(String phoneNumber) {

        if (G.userLogin) {
            if (isNumeric(phoneNumber) && phoneNumber.length() == 11) {
                if (operatorType != null) {
                    if (selectedChargeTypePosition > 0) {
                        if (selectedPricePosition > 0) {
                            ChargeType chargeType = null;
                            switch (operatorType) {
                                case HAMRAH_AVAL:
                                    chargeType = null;
                                    break;
                                case IRANCELL:
                                    switch (selectedChargeTypePosition) {
                                        case 1:
                                            chargeType = ChargeType.MTN_NORMAL;
                                            break;
                                        case 2:
                                            chargeType = ChargeType.MTN_AMAZING;
                                            break;
                                    }
                                    break;
                                case RITEL:
                                    switch (selectedChargeTypePosition) {
                                        case 1:
                                            chargeType = ChargeType.RIGHTEL_NORMAL;
                                            break;
                                        case 2:
                                            chargeType = ChargeType.RIGHTEL_EXCITING;
                                            break;
                                    }
                                    break;
                            }
                            long price = 0;
                            switch (selectedPricePosition) {
                                case 1:
                                    price = 10000;
                                    break;
                                case 2:
                                    price = 20000;
                                    break;
                                case 3:
                                    price = 50000;
                                    break;
                                case 4:
                                    price = 100000;
                                    break;
                                case 5:
                                    price = 200000;
                                    break;
                            }

                            if (operatorType == OperatorType.Type.HAMRAH_AVAL) {
                                sendRequestCharge(MCI, chargeType, phoneNumber.substring(1), (int) price);
                            } else if (operatorType == OperatorType.Type.IRANCELL) {
                                sendRequestCharge(MTN, chargeType, phoneNumber.substring(1), (int) price);
                            } else if (operatorType == OperatorType.Type.RITEL) {
                                sendRequestCharge(RIGHTEL, chargeType, phoneNumber.substring(1), (int) price);
                            }

                            hideKeyWord.setValue(true);
                            observeEnabledPayment.set(false);
                        } else {
                            showError.setValue(R.string.charge_price_error_message);
                        }
                    } else {
                        showError.setValue(R.string.charge_type_error_message);
                    }
                } else {
                    showError.setValue(R.string.please_select_operator);
                }
            } else {
                showError.setValue(R.string.phone_number_is_not_valid);
            }
        } else {
            showError.setValue(R.string.there_is_no_connection_to_server);
        }
    }

    private void sendRequestCharge(String operator, ChargeType chargeType, String phoneNumber, int price) {
//        new ApiInitializer<MciPurchaseResponse>().initAPI(
//                new RetrofitFactory().getChargeRetrofit().topUpPurchase(operator, chargeType != null ? chargeType.name() : null, phoneNumber, price),
//                this, new ResponseCallback<MciPurchaseResponse>() {
//                    @Override
//                    public void onSuccess(MciPurchaseResponse data) {
//                        observeEnabledPayment.set(true);
//                        goToPaymentPage.setValue(data.getToken());
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        observeEnabledPayment.set(true);
//                        showMciPaymentError.setValue(new ErrorModel("", error));
//                    }
//
//                    @Override
//                    public void onFailed() {
//                        //ToDO: handle this event
//                        /*observeEnabledPayment.set(true);
//                        showMciPaymentError.setValue(error);*/
//                    }
//                });
    }

    enum ChargeType {
        MTN_NORMAL, MTN_AMAZING, RIGHTEL_NORMAL, RIGHTEL_EXCITING;
    }

}
