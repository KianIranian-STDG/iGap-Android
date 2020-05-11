package net.iGap.fragments.payment;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.payment.AdapterChargeAmount;
import net.iGap.adapter.payment.AdapterChargeType;
import net.iGap.adapter.payment.AdapterContactNumber;
import net.iGap.adapter.payment.AdapterHistoryNumber;
import net.iGap.adapter.payment.Amount;
import net.iGap.adapter.payment.ChargeType;
import net.iGap.adapter.payment.ContactNumber;
import net.iGap.adapter.payment.HistoryNumber;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.MciPurchaseResponse;
import net.iGap.model.OperatorType;
import net.iGap.module.Contacts;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmRecentChargeNumber;
import net.iGap.realm.RealmRecentChargeNumberFields;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmResults;

public class FragmentPaymentChargeNewUi extends BaseFragment {
    private LinearLayout toolbar;
    private ConstraintLayout frameContact;
    private ConstraintLayout frameHistory;
    private RadioButton radioButtonHamrah;
    private RadioButton radioButtonIrancell;
    private RadioButton radioButtonRightel;
    private RadioGroup radioGroup;
    private AdapterChargeAmount adapterAmount;
    private AdapterChargeType adapterChargeType;
    private AdapterHistoryNumber adapterHistory;
    private AdapterContactNumber adapterContact;
    private RecyclerView rvContact;
    private RecyclerView rvHistory;
    private RecyclerView rvAmount;
    private AppCompatTextView amountTxt;
    private AppCompatTextView editType;
    private AppCompatTextView chooseType;
    private AppCompatImageView ivAdd;
    private AppCompatImageView lowView;
    private MaterialButton saveBtn1, saveBtn2, saveBtn3, saveBtn4;
    private MaterialButton priceChoose;
    private MaterialButton btnChargeType;
    private MaterialButton enterBtn;
    private AppCompatEditText editTextNumber;
    private int selectedIndex;
    private int selectedChargeTypeIndex;
    private int selectedPriceIndex;
    private ContactNumber contactNumber;
    private HistoryNumber historyNumber;
    private Amount amount;
    private ChargeType typeList;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private View closeView, closeView2, closeView3, closeView4;
    List<Amount> amountList = new ArrayList<>();
    List<ChargeType> chargeTypeList = new ArrayList<>();

    private OperatorType.Type operatorType;

    public static final String MCI = "mci";
    public static final String MTN = "mtn";
    public static final String RIGHTEL = "rightel";

    public static FragmentPaymentChargeNewUi newInstance() {


        Bundle args = new Bundle();

        FragmentPaymentChargeNewUi fragment = new FragmentPaymentChargeNewUi();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_charge_newui, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.payment_toolbar);
        radioButtonHamrah = view.findViewById(R.id.radio_hamrahAval);
        radioButtonIrancell = view.findViewById(R.id.radio_irancell);
        radioButtonRightel = view.findViewById(R.id.radio_rightel);
        radioGroup = view.findViewById(R.id.radioGroup);
        frameContact = view.findViewById(R.id.frame_contact);
        frameHistory = view.findViewById(R.id.frame_history);
        priceChoose = view.findViewById(R.id.choose_amount);
        btnChargeType = view.findViewById(R.id.btn_charge_type);
        ivAdd = view.findViewById(R.id.add_amount);
        lowView = view.findViewById(R.id.low_amount);
        amountTxt = view.findViewById(R.id.tv_amount_btn);
        editTextNumber = view.findViewById(R.id.phoneNumberInput);
        editType = view.findViewById(R.id.iv_edit);
        chooseType = view.findViewById(R.id.tv_choose);
        enterBtn = view.findViewById(R.id.btn_next);
        scrollView = view.findViewById(R.id.scroll_payment);
        progressBar = view.findViewById(R.id.loadingView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            enterBtn.setBackgroundTintList(getContext().getColorStateList(R.color.gray));
        }

        toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(getString(R.string.buy_charge))
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).getView());

        onChargeTypeChooseClick();
        onContactNumberButtonClick();
        onHistoryNumberButtonClick();
        onPriceChooseClick();
        onPhoneNumberInputClick();
        onItemOperatorSelect();
        onSaveBtnClicked();
    }

    private void onContactNumberButtonClick() {
        frameContact.setOnClickListener(v -> {

            new Contacts().getAllPhoneContactForPayment(contactNumbers -> adapterContact = new AdapterContactNumber(contactNumbers));

            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_contact, true).build();
            View view = dialog.getCustomView();
            rvContact = view.findViewById(R.id.rv_contact);
            saveBtn1 = view.findViewById(R.id.btn_dialog1);
            closeView = view.findViewById(R.id.closeView);


            saveBtn1.setOnClickListener(v15 -> {
                if (adapterContact.getSelectedPosition() == -1) {
                    return;
                }
                selectedIndex = adapterContact.getSelectedPosition();
                contactNumber = adapterContact.getContactNumbers().get(selectedIndex);
                editTextNumber.setText(contactNumber.getPhone().replace(" ", "").replace("-", "").replace("+98", "0"));
                dialog.dismiss();
            });
            dialog.show();

            closeView.setOnClickListener(v12 -> dialog.dismiss());

            rvContact.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvContact.setAdapter(adapterContact);
        });
    }

    private void onHistoryNumberButtonClick() {
        frameHistory.setOnClickListener(v -> {

            RealmResults<RealmRecentChargeNumber> numbers = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRecentChargeNumber.class).equalTo(RealmRecentChargeNumberFields.TYPE, 0).findAll();
            });

            if (numbers == null || numbers.size() == 0) {
                Toast.makeText(getContext(), "number is empty", Toast.LENGTH_SHORT).show();
            } else {
                adapterHistory = new AdapterHistoryNumber(numbers);

                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_history, false).build();
                View view = dialog.getCustomView();
                rvHistory = view.findViewById(R.id.rv_history);
                saveBtn2 = view.findViewById(R.id.btn_dialog2);
                closeView2 = view.findViewById(R.id.close_view);

                closeView2.setOnClickListener(v12 -> dialog.dismiss());

                saveBtn2.setOnClickListener(v13 -> {
                    if (adapterHistory.getSelectedPosition() == -1) {
                        return;
                    }
                    selectedIndex = adapterHistory.getSelectedPosition();
                    historyNumber = adapterHistory.getHistoryNumberList().get(selectedIndex);
                    editTextNumber.setText(historyNumber.getHistoryNumber());
                    dialog.dismiss();
                });

                rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                rvHistory.setAdapter(adapterHistory);
                dialog.show();
            }
        });
    }

    private void onPhoneNumberInputClick() {
        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextNumber.getText().length() == 11) {
                    String number = editTextNumber.getText().toString().substring(0, 4);
                    OperatorType.Type opt = new OperatorType().getOperation(number);
                    if (opt != null) {
                        setAdapterValue(opt);
                        switch (opt) {
                            case HAMRAH_AVAL:
                                radioButtonHamrah.setChecked(true);
                                break;
                            case IRANCELL:
                                radioButtonIrancell.setChecked(true);
                                break;
                            case RITEL:
                                radioButtonRightel.setChecked(true);
                                break;
                        }
                        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                    }

                }
            }
        });
    }

    private void onItemOperatorSelect() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int id = radioGroup.getCheckedRadioButtonId();
            if (editTextNumber.getText() != null) {
                switch (id) {
                    case R.id.radio_hamrahAval:
                        setAdapterValue(OperatorType.Type.HAMRAH_AVAL);
                        radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
                        radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                        radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                        break;
                    case R.id.radio_irancell:
                        setAdapterValue(OperatorType.Type.IRANCELL);
                        radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
                        radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                        radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                        break;
                    case R.id.radio_rightel:
                        setAdapterValue(OperatorType.Type.RITEL);
                        radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
                        radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                        radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                        break;
                }
            } else {
                radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                ShowError("شماره تماس راوارد کنید");

            }

        });

    }

    private void onPriceChooseClick() {
        priceChoose.setOnClickListener(v -> {
            adapterAmount = new AdapterChargeAmount(amountList);
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_amount, false).build();
            View view = dialog.getCustomView();
            rvAmount = view.findViewById(R.id.rv_amount);
            closeView3 = view.findViewById(R.id.close_view3);
            saveBtn3 = view.findViewById(R.id.btn_dialog3);

            saveBtn3.setOnClickListener(v1 -> {
                if (adapterAmount.getSelectedPosition() == -1) {
                    return;
                }

                selectedPriceIndex = adapterAmount.getSelectedPosition();
                amount = amountList.get(selectedPriceIndex);
                amountTxt.setText(amount.getTextAmount());

                ivAdd.setVisibility(View.VISIBLE);
                lowView.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    priceChoose.setBackgroundTintList(getContext().getColorStateList(R.color.background_editText));
                    btnChargeType.setBackgroundTintList(getContext().getColorStateList(R.color.green));
                }
                amountTxt.setTextColor(getContext().getResources().getColor(R.color.gray));
                chooseType.setTextColor(getContext().getResources().getColor(R.color.white));
                priceChoose.setClickable(false);
                dialog.dismiss();
            });

            closeView3.setOnClickListener(v12 -> dialog.dismiss());

            ViewGroup.LayoutParams params = rvAmount.getLayoutParams();
            params.height = 500;
            rvAmount.setLayoutParams(params);
            rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvAmount.setAdapter(adapterAmount);
            dialog.show();
        });
        ivAdd.setOnClickListener(v -> {
            if (selectedPriceIndex + 1 < amountList.size()) {
                amount = amountList.get(selectedPriceIndex = selectedPriceIndex + 1);
                amountTxt.setText(amount.getTextAmount());
            } else {
                ivAdd.setClickable(false);
            }
        });


        lowView.setOnClickListener(v -> {
            if (selectedPriceIndex - 1 < amountList.size() && selectedPriceIndex > 0) {
                amount = amountList.get(selectedPriceIndex = selectedPriceIndex - 1);
                amountTxt.setText(amount.getTextAmount());
            }
        });
    }

    private void onChargeTypeChooseClick() {
        btnChargeType.setOnClickListener(v -> {
            adapterChargeType = new AdapterChargeType(chargeTypeList);
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_type, false).build();

            View view = dialog.getCustomView();
            rvAmount = view.findViewById(R.id.rv_type);
            closeView4 = view.findViewById(R.id.close_view4);
            saveBtn4 = view.findViewById(R.id.btn_dialog4);

            saveBtn4.setOnClickListener(v16 -> {
                if (adapterChargeType.getSelectedPosition() == -1) {
                    return;
                }

                selectedChargeTypeIndex = adapterChargeType.getSelectedPosition();
                typeList = chargeTypeList.get(selectedChargeTypeIndex);
                chooseType.setText(typeList.getChargeType());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnChargeType.setBackgroundTintList(getContext().getColorStateList(R.color.background_editText));
                    enterBtn.setBackgroundTintList(getContext().getColorStateList(R.color.green));

                }
                chooseType.setTextColor(getContext().getResources().getColor(R.color.white));
                chooseType.setTextColor(getContext().getResources().getColor(R.color.gray));

                editType.setVisibility(View.VISIBLE);
                dialog.dismiss();

            });

            closeView4.setOnClickListener(v12 -> dialog.dismiss());

            rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvAmount.setAdapter(adapterChargeType);
            dialog.show();
        });
    }

    private void setAdapterValue(@NotNull OperatorType.Type operator) {
        List<String> prices;
        List<String> chargeType;
        switch (operator) {
            case HAMRAH_AVAL:
                operatorType = OperatorType.Type.HAMRAH_AVAL;
                prices = Arrays.asList(getResources().getStringArray(R.array.charge_price));
                amountList.clear();
                for (int i = 0; i < prices.size(); i++) {
                    amountList.add(new Amount(prices.get(i)));
                }
                chargeType = Arrays.asList(getResources().getStringArray(R.array.charge_type_hamrahe_aval));
                chargeTypeList.clear();
                for (int i = 0; i < chargeType.size(); i++) {
                    chargeTypeList.add(new ChargeType(chargeType.get(i)));
                }

                break;
            case IRANCELL:
                operatorType = OperatorType.Type.IRANCELL;
                prices = Arrays.asList(getResources().getStringArray(R.array.charge_price_irancell));
                amountList.clear();
                for (int i = 0; i < prices.size(); i++) {
                    amountList.add(new Amount(prices.get(i)));
                }
                chargeType = Arrays.asList(getResources().getStringArray(R.array.charge_type_irancell));
                chargeTypeList.clear();
                for (int i = 0; i < chargeType.size(); i++) {
                    chargeTypeList.add(new ChargeType(chargeType.get(i)));
                }
                break;
            case RITEL:
                operatorType = OperatorType.Type.RITEL;
                prices = Arrays.asList(getResources().getStringArray(R.array.charge_price));
                amountList.clear();
                for (int i = 0; i < prices.size(); i++) {
                    amountList.add(new Amount(prices.get(i)));
                }
                chargeType = Arrays.asList(getResources().getStringArray(R.array.charge_type_ritel));
                chargeTypeList.clear();
                for (int i = 0; i < chargeType.size(); i++) {
                    chargeTypeList.add(new ChargeType(chargeType.get(i)));
                }
                break;
        }
    }


    private void onSaveBtnClicked() {
        enterBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (G.userLogin) {
                if (isNumeric(editTextNumber.getText().toString()) && editTextNumber.getText().length() == 11) {
                    if (operatorType != null) {
                        if (selectedChargeTypeIndex != -1) {
                            if (selectedPriceIndex != -1) {
                                ChooseChargeType chooseChargeType = null;
                                switch (operatorType) {
                                    case HAMRAH_AVAL:
                                        chooseChargeType = null;
                                        break;
                                    case IRANCELL:
                                        switch (selectedChargeTypeIndex) {
                                            case 0:
                                                chooseChargeType = ChooseChargeType.MTN_NORMAL;
                                                break;
                                            case 1:
                                                chooseChargeType = ChooseChargeType.MTN_AMAZING;
                                                break;
                                        }
                                        break;
                                    case RITEL:
                                        switch (selectedChargeTypeIndex) {
                                            case 0:
                                                chooseChargeType = ChooseChargeType.RIGHTEL_NORMAL;
                                                break;
                                            case 1:
                                                chooseChargeType = ChooseChargeType.RIGHTEL_EXCITING;
                                                break;
                                        }
                                        break;
                                }
                                long price = 0;
                                switch (selectedPriceIndex) {
                                    case 0:
                                        price = 10000;
                                        break;
                                    case 1:
                                        price = 20000;
                                        break;
                                    case 2:
                                        price = 50000;
                                        break;
                                    case 3:
                                        price = 100000;
                                        break;
                                    case 4:
                                        price = 200000;
                                        break;
                                }

                                if (operatorType == OperatorType.Type.HAMRAH_AVAL) {
                                    sendRequestCharge(MCI, chooseChargeType, editTextNumber.getText().toString().substring(1), (int) price);
                                } else if (operatorType == OperatorType.Type.IRANCELL) {
                                    sendRequestCharge(MTN, chooseChargeType, editTextNumber.getText().toString().substring(1), (int) price);
                                } else if (operatorType == OperatorType.Type.RITEL) {
                                    sendRequestCharge(RIGHTEL, chooseChargeType, editTextNumber.getText().toString().substring(1), (int) price);
                                }

                            } else {
                                ShowError(getContext().getResources().getString(R.string.charge_price_error_message));
                            }
                        } else {
                            ShowError(getContext().getResources().getString(R.string.charge_type_error_message));
                        }
                    } else {
                        ShowError(getContext().getResources().getString(R.string.please_select_operator));
                    }
                } else {
                    ShowError(getContext().getResources().getString(R.string.phone_number_is_not_valid));
                }
            } else {
                ShowError(getContext().getResources().getString(R.string.there_is_no_connection_to_server));
            }
        });
    }

    public static boolean isNumeric(String strNum) {
        try {
            long d = Long.parseLong(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    enum ChooseChargeType {
        MTN_NORMAL, MTN_AMAZING, RIGHTEL_NORMAL, RIGHTEL_EXCITING;
    }

    private void sendRequestCharge(String operator, ChooseChargeType chargeType, String phoneNumber, int price) {
        new ApiInitializer<MciPurchaseResponse>().initAPI(
                new RetrofitFactory().getChargeRetrofit().topUpPurchase(operator, chargeType != null ? chargeType.name() : null, phoneNumber, price),
                null, new ResponseCallback<MciPurchaseResponse>() {
                    @Override
                    public void onSuccess(MciPurchaseResponse data) {
                        progressBar.setVisibility(View.GONE);
                        String token = data.getToken();
                        if (getActivity() != null && token != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.buy_charge), token, result -> {
                                if (result.isSuccess()) {

                                    DbManager.getInstance().doRealmTask(realm -> {
                                        RealmRecentChargeNumber.put(realm, "0" + phoneNumber, 0);
                                    });

                                    goBack();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(_mActivity, error, Toast.LENGTH_SHORT).show();
                        //   observeEnabledPayment.set(true);
                        // showMciPaymentError.setValue(new ErrorModel("", error));
                    }

                    @Override
                    public void onFailed() {
                        progressBar.setVisibility(View.GONE);
                        //ToDO: handle this event
                        /*observeEnabledPayment.set(true);
                        showMciPaymentError.setValue(error);*/
                    }
                });
    }

    private void goBack() {
        G.handler.post(() -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    public void ShowError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }

}
