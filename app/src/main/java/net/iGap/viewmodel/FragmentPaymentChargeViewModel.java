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

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.MciApi;
import net.iGap.api.PaymentApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.databinding.FragmentPaymentChargeBinding;
import net.iGap.helper.HelperError;
import net.iGap.interfaces.OnMplResult;
import net.iGap.model.MciPurchaseResponse;
import net.iGap.proto.ProtoMplGetTopupToken;
import net.iGap.request.RequestMplGetTopupToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentPaymentChargeViewModel extends ViewModel {

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

    public enum OperatorType {
        HAMRAH_AVAL, IRANCELL, RITEL;
    }

    private HashMap<String, OperatorType> phoneMap = new HashMap<String, OperatorType>() {
        {
            put("0910", OperatorType.HAMRAH_AVAL);
            put("0911", OperatorType.HAMRAH_AVAL);
            put("0912", OperatorType.HAMRAH_AVAL);
            put("0913", OperatorType.HAMRAH_AVAL);
            put("0914", OperatorType.HAMRAH_AVAL);
            put("0915", OperatorType.HAMRAH_AVAL);
            put("0916", OperatorType.HAMRAH_AVAL);
            put("0917", OperatorType.HAMRAH_AVAL);
            put("0918", OperatorType.HAMRAH_AVAL);
            put("0919", OperatorType.HAMRAH_AVAL);
            put("0990", OperatorType.HAMRAH_AVAL);
            put("0991", OperatorType.HAMRAH_AVAL);

            put("0901", OperatorType.IRANCELL);
            put("0902", OperatorType.IRANCELL);
            put("0903", OperatorType.IRANCELL);
            put("0930", OperatorType.IRANCELL);
            put("0933", OperatorType.IRANCELL);
            put("0935", OperatorType.IRANCELL);
            put("0936", OperatorType.IRANCELL);
            put("0937", OperatorType.IRANCELL);
            put("0938", OperatorType.IRANCELL);
            put("0939", OperatorType.IRANCELL);

            put("0920", OperatorType.RITEL);
            put("0921", OperatorType.RITEL);
            put("0922", OperatorType.RITEL);

        }
    };

    private OperatorType operatorType;


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
                setAdapterValue(OperatorType.HAMRAH_AVAL);
                break;
            case 2:
                setAdapterValue(OperatorType.IRANCELL);
                break;
            case 3:
                setAdapterValue(OperatorType.RITEL);
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
            OperatorType opt = phoneMap.get(s);
            if (opt != null) {
                setAdapterValue(opt);
            } else {
                observeTarabord.set(View.VISIBLE);
            }
        }
    }

    private void setAdapterValue(@NotNull OperatorType operator) {
        switch (operator) {
            case HAMRAH_AVAL:
                operatorType = OperatorType.HAMRAH_AVAL;
                onOpereatorChange.setValue(R.array.charge_type_hamrahe_aval);
                onPriceChange.setValue(R.array.charge_price);
                break;
            case IRANCELL:
                operatorType = OperatorType.IRANCELL;
                onOpereatorChange.setValue(R.array.charge_type_irancell);
                onPriceChange.setValue(R.array.charge_price_irancell);
                break;
            case RITEL:
                operatorType = OperatorType.RITEL;
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
                            ProtoMplGetTopupToken.MplGetTopupToken.Type type = null;
                            switch (operatorType) {
                                case HAMRAH_AVAL:
                                    type = ProtoMplGetTopupToken.MplGetTopupToken.Type.MCI;
                                    break;
                                case IRANCELL:
                                    switch (selectedChargeTypePosition) {
                                        case 1:
                                            type = ProtoMplGetTopupToken.MplGetTopupToken.Type.IRANCELL_PREPAID;
                                            break;
                                        case 2:
                                            type = ProtoMplGetTopupToken.MplGetTopupToken.Type.IRANCELL_WOW;
                                            break;
                                        case 3:
                                            type = ProtoMplGetTopupToken.MplGetTopupToken.Type.IRANCELL_WIMAX;
                                            break;
                                        case 4:
                                            type = ProtoMplGetTopupToken.MplGetTopupToken.Type.IRANCELL_POSTPAID;
                                            break;
                                    }
                                    break;
                                case RITEL:
                                    type = ProtoMplGetTopupToken.MplGetTopupToken.Type.RIGHTEL;
                                    break;
                            }
                            long price = 0;
                            boolean isIranCell = operatorType == OperatorType.IRANCELL;
                            switch (selectedPricePosition) {
                                case 1:
                                    if (isIranCell) {
                                        price = 10900;
                                    } else {
                                        price = 10000;
                                    }
                                    break;
                                case 2:
                                    if (isIranCell) {
                                        price = 21180;
                                    } else {
                                        price = 20000;
                                    }
                                    break;
                                case 3:
                                    if (isIranCell) {
                                        price = 54500;
                                    } else {
                                        price = 50000;
                                    }
                                    break;
                                case 4:
                                    if (isIranCell) {
                                        price = 109000;
                                    } else {
                                        price = 100000;
                                    }
                                    break;
                                case 5:
                                    if (isIranCell) {
                                        price = 218000;
                                    } else {
                                        price = 200000;
                                    }
                                    break;
                            }

                            /*if (operatorType == OperatorType.HAMRAH_AVAL) {
                                sendRequestChargeMci(phoneNumber.substring(1), (int) price);
                            } else {*/
                                RequestMplGetTopupToken requestMplGetTopupToken = new RequestMplGetTopupToken();
                                requestMplGetTopupToken.mplGetTopupToken(Long.parseLong(phoneNumber), price, type);
                                G.onMplResult = error -> {
                                    if (error) {
                                        observeEnabledPayment.set(true);
                                    } else {
                                        goBack.setValue(true);
                                    }
                                };
                            /*}*/
                            hideKeyWord.setValue(true);
                            observeEnabledPayment.set(false);
                        } else {
                            showError.setValue(R.string.error);
                        }
                    } else {
                        showError.setValue(R.string.error);
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

    private void sendRequestChargeMci(String phoneNumber, int price) {
        new RetrofitFactory().getMciRetrofit().create(MciApi.class).topUpPurchase(phoneNumber, price).enqueue(new Callback<MciPurchaseResponse>() {
            @Override
            public void onResponse(@NotNull Call<MciPurchaseResponse> call, @NotNull Response<MciPurchaseResponse> response) {
                observeEnabledPayment.set(true);
                if (response.code() == 200) {
                    goToPaymentPage.setValue(response.body().getToken());
                } else {
                    try {
                        showMciPaymentError.setValue(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<MciPurchaseResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
                observeEnabledPayment.set(true);
                if (new ErrorHandler().checkHandShakeFailure(t)) {
                    needUpdate.setValue(true);
                }
            }
        });
    }
}
