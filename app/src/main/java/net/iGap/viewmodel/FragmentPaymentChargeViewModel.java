package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.databinding.ObservableInt;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentPaymentChargeBinding;
import net.iGap.helper.HelperError;
import net.iGap.proto.ProtoMplGetTopupToken;
import net.iGap.request.RequestMplGetTopupToken;

import java.util.HashMap;


public class FragmentPaymentChargeViewModel {

    public enum OperatorType {
        HAMRAH_AVAL, IRANCELL, RITEL;
    }

    HashMap<String, OperatorType> phoneMap = new HashMap<String, OperatorType>() {
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

    private FragmentPaymentChargeBinding fragmentPaymentChargeBinding;
    private OperatorType operatorType;

    public ObservableInt observeTarabord = new ObservableInt(View.GONE);


    public FragmentPaymentChargeViewModel(FragmentPaymentChargeBinding fragmentPaymentChargeBinding) {
        this.fragmentPaymentChargeBinding = fragmentPaymentChargeBinding;
    }


    //****************************************************************************************

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 11) {
            if (fragmentPaymentChargeBinding.fpcCheckBoxTrabord.isChecked()) {
                fragmentPaymentChargeBinding.fpcCheckBoxTrabord.setChecked(false);
            } else {
                setOperator(s.toString());
            }
        }
    }

    public void checkBoxTarabordChanged(View v, boolean checked) {
        if (checked) {
            observeTarabord.set(View.VISIBLE);
            fragmentPaymentChargeBinding.fpcSpinnerOperator.setSelection(0);

        } else {
            observeTarabord.set(View.GONE);
            setOperator(fragmentPaymentChargeBinding.fpcEditTextPhoneNumber.getText().toString());
        }
    }

    public void onItemSelecteSpinerOperator(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                operatorType = null;
                fragmentPaymentChargeBinding.fpcSpinnerChargeType.setAdapter(null);
                fragmentPaymentChargeBinding.fpcSpinnerPrice.setAdapter(null);
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

    private void setAdapterValue(OperatorType operator) {
        switch (operator) {
            case HAMRAH_AVAL:
                operatorType = OperatorType.HAMRAH_AVAL;
                onOpereatorChange(R.array.charge_type_hamrahe_aval);
                onPriceChange(R.array.charge_price);
                break;
            case IRANCELL:
                operatorType = OperatorType.IRANCELL;
                onOpereatorChange(R.array.charge_type_irancell);
                onPriceChange(R.array.charge_price_irancell);
                break;
            case RITEL:
                operatorType = OperatorType.RITEL;
                onOpereatorChange(R.array.charge_type_ritel);
                onPriceChange(R.array.charge_price);
                break;
        }
    }

    private void onOpereatorChange(int arrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(G.context, arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentPaymentChargeBinding.fpcSpinnerChargeType.setAdapter(adapter);
        fragmentPaymentChargeBinding.fpcSpinnerChargeType.setSelection(0);
    }

    private void onPriceChange(int arrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(G.context, arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentPaymentChargeBinding.fpcSpinnerPrice.setAdapter(adapter);
        fragmentPaymentChargeBinding.fpcSpinnerPrice.setSelection(0);
    }


    public void onBuyClick(View v) {

        if (!G.userLogin) {
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            return;
        }

        String phoneNumber = fragmentPaymentChargeBinding.fpcEditTextPhoneNumber.getText().toString();

        if (phoneNumber.length() != 11) {
            Toast.makeText(G.currentActivity, R.string.phone_number_is_not_valid, Toast.LENGTH_SHORT).show();
            return;
        }

        ProtoMplGetTopupToken.MplGetTopupToken.Type type = null;

        if (operatorType == null) {
            Toast.makeText(G.currentActivity, R.string.please_select_operator, Toast.LENGTH_SHORT).show();
            return;
        }

        switch (operatorType) {
            case HAMRAH_AVAL:
                type = ProtoMplGetTopupToken.MplGetTopupToken.Type.MCI;
                break;
            case IRANCELL:
                switch (fragmentPaymentChargeBinding.fpcSpinnerChargeType.getSelectedItemId() + "") {
                    case "0":
                        type = ProtoMplGetTopupToken.MplGetTopupToken.Type.IRANCELL_PREPAID;
                        break;
                    case "1":
                        type = ProtoMplGetTopupToken.MplGetTopupToken.Type.IRANCELL_WOW;
                        break;
                    case "2":
                        type = ProtoMplGetTopupToken.MplGetTopupToken.Type.IRANCELL_WIMAX;
                        break;
                    case "3":
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

        switch (fragmentPaymentChargeBinding.fpcSpinnerPrice.getSelectedItemId() + "") {
            case "0":
                if (isIranCell) {
                    price = 10900;
                } else {
                    price = 10000;
                }
                break;
            case "1":
                if (isIranCell) {
                    price = 21180;
                } else {
                    price = 20000;
                }
                break;
            case "2":
                if (isIranCell) {
                    price = 54500;
                } else {
                    price = 50000;
                }
                break;
            case "3":
                if (isIranCell) {
                    price = 109000;
                } else {
                    price = 100000;
                }
                break;
            case "4":
                if (isIranCell) {
                    price = 218000;
                } else {
                    price = 200000;
                }
                break;
        }

        RequestMplGetTopupToken requestMplGetTopupToken = new RequestMplGetTopupToken();
        requestMplGetTopupToken.mplGetTopupToken(Long.parseLong(phoneNumber), price, type);

        fragmentPaymentChargeBinding.getBackHandler().onBack();

    }

}
