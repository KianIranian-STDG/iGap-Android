package net.iGap.fragments.payment;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.payment.AdapterChargeAmount;
import net.iGap.adapter.payment.AdapterChargeType;
import net.iGap.adapter.payment.AdapterContactNumber;
import net.iGap.adapter.payment.AdapterHistoryNumber;
import net.iGap.adapter.payment.Amount;
import net.iGap.adapter.payment.ChargeType;
import net.iGap.adapter.payment.ContactNumber;
import net.iGap.api.ChargeApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.OperatorType;
import net.iGap.model.paymentPackage.FavoriteNumber;
import net.iGap.model.paymentPackage.GetFavoriteNumber;
import net.iGap.model.paymentPackage.MciPurchaseResponse;
import net.iGap.module.Contacts;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmRegisteredInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPaymentChargeNewUi extends BaseFragment {
    private LinearLayout toolbar;
    private ConstraintLayout frameContact;
    private ConstraintLayout frameHistory;
    private ConstraintLayout frameHamrah;
    private ConstraintLayout frameIrancel;
    private ConstraintLayout frameRightel;
    private RadioButton radioButtonHamrah;
    private RadioButton radioButtonIrancell;
    private RadioButton radioButtonRightel;
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
    private AppCompatTextView removeNumber;
    private AppCompatImageView ivAdd;
    private AppCompatImageView lowView;
    private MaterialButton saveBtn1, saveBtn2, saveBtn3, saveBtn4;
    private MaterialButton priceChoose;
    private MaterialButton btnChargeType;
    private MaterialButton enterBtn;
    private AppCompatEditText editTextNumber;
    private ContactNumber contactNumber;
    private FavoriteNumber historyNumber;
    private Amount amount;
    private ChargeType typeList;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private LinearLayout linearWarning;
    private View closeView, closeView2, closeView3, closeView4;
    private List<Amount> amountList = new ArrayList<>();
    private List<ChargeType> chargeTypeList = new ArrayList<>();
    private ChargeApi chargeApi;
    private OperatorType.Type operatorType;
    private RealmRegisteredInfo userInfo;
    private int selectedIndex;
    private int selectedChargeTypeIndex;
    private int selectedPriceIndex;
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
        return LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_charge_newui, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.payment_toolbar);
        radioButtonHamrah = view.findViewById(R.id.radio_hamrahAval);
        radioButtonIrancell = view.findViewById(R.id.radio_irancell);
        radioButtonRightel = view.findViewById(R.id.radio_rightel);
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
        enterBtn = view.findViewById(R.id.btn_pay);
        scrollView = view.findViewById(R.id.scroll_payment);
        progressBar = view.findViewById(R.id.loadingView);
        frameHamrah = view.findViewById(R.id.view12);
        frameIrancel = view.findViewById(R.id.view13);
        frameRightel = view.findViewById(R.id.view14);
        linearWarning = view.findViewById(R.id.llWarning);
        removeNumber = view.findViewById(R.id.btnRemoveSearch);
        chargeApi = new RetrofitFactory().getChargeRetrofit();

        DbManager.getInstance().doRealmTask(realm -> {
            userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            if (userInfo != null)
                editTextNumber.setText(userInfo.getPhoneNumber().replace("98", "0").replace("+98", "0").replace("0098", "0").replace(" ", "")
                        .replace("-", ""));
            onPhoneNumberInput();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            enterBtn.setBackgroundTintList(getContext().getColorStateList(R.color.gray_6c));
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
            hideKeyboard();
            try {
                HelperPermission.getContactPermision(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() {
                        new Contacts().getAllPhoneContactForPayment(contactNumbers -> adapterContact = new AdapterContactNumber(contactNumbers));
                        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_contact, false).build();
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
                    }

                    @Override
                    public void deny() {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void onHistoryNumberButtonClick() {
        frameHistory.setOnClickListener(v -> {
            hideKeyboard();
            progressBar.setVisibility(View.VISIBLE);
            chargeApi.getFavoriteChargeNUmber().enqueue(new Callback<GetFavoriteNumber>() {
                @Override
                public void onResponse(Call<GetFavoriteNumber> call, Response<GetFavoriteNumber> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body().getData() != null) {
                        adapterHistory = new AdapterHistoryNumber(response.body().getData());
                        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_history, false).build();
                        View view = dialog.getCustomView();
                        rvHistory = view.findViewById(R.id.rv_history);
                        saveBtn2 = view.findViewById(R.id.btn_dialog2);
                        closeView2 = view.findViewById(R.id.iv_close2);
                        closeView2.setOnClickListener(v12 -> dialog.dismiss());

                        saveBtn2.setOnClickListener(v13 -> {
                            if (adapterHistory.getSelectedPosition() == -1) {
                                return;
                            }
                            selectedIndex = adapterHistory.getSelectedPosition();
                            historyNumber = adapterHistory.getHistoryNumberList().get(selectedIndex);
                            editTextNumber.setText(historyNumber.getPhoneNumber().replace(" ", "").replace("-", "").replace("+98", "0"));
                            amountTxt.setText(historyNumber.getAmount().toString());
                            chooseType.setText(historyNumber.getChargeTypeDescription());
                            dialog.dismiss();
                        });

                        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        rvHistory.setAdapter(adapterHistory);
                        dialog.show();

                    } else {
                        ShowError((getContext().getResources().getString(R.string.list_empty)));
                    }
                }

                @Override
                public void onFailure(Call<GetFavoriteNumber> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ShowError(getContext().getResources().getString(R.string.there_is_no_connection_to_server));

                }
            });

        });

    }

    private void onPhoneNumberInputClick() {
        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onPhoneNumberInput();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void onPhoneNumberInput() {
        if (editTextNumber.getText().length() > 0) {
            removeNumber.setVisibility(View.VISIBLE);
            if (editTextNumber.getText().length() == 10)
                if (editTextNumber.getText().charAt(0) != '0')
                    editTextNumber.setText("0" + editTextNumber.getText().toString());
            if (editTextNumber.getText().length() == 11 || editTextNumber.getText().length() == 4) {
                if (editTextNumber.getText().length() == 11) {
                    scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                    hideKeyboard();
                    linearWarning.setVisibility(View.VISIBLE);
                }
                String number = editTextNumber.getText().toString().substring(0, 4);
                OperatorType.Type opt = new OperatorType().getOperation(number);
                if (opt != null) {
                    setAdapterValue(opt);
                    switch (opt) {
                        case HAMRAH_AVAL:
                            setAdapterValue(OperatorType.Type.HAMRAH_AVAL);
                            setSelectedOperator(radioButtonHamrah, radioButtonIrancell, radioButtonRightel, frameHamrah, frameIrancel, frameRightel);
                            break;
                        case IRANCELL:
                            setAdapterValue(OperatorType.Type.IRANCELL);
                            setSelectedOperator(radioButtonIrancell, radioButtonHamrah, radioButtonRightel, frameIrancel, frameHamrah, frameRightel);
                            break;
                        case RITEL:
                            setAdapterValue(OperatorType.Type.RITEL);
                            setSelectedOperator(radioButtonRightel, radioButtonIrancell, radioButtonHamrah, frameRightel, frameIrancel, frameHamrah);
                            break;
                    }
                }
            }
            removeNumber.setOnClickListener(view1 -> {
                editTextNumber.setText("");
                removeNumber.setVisibility(View.GONE);
                if (editTextNumber.getText().toString() == "") {
                    radioButtonHamrah.setChecked(false);
                    radioButtonIrancell.setChecked(false);
                    radioButtonRightel.setChecked(false);
                    frameHamrah.setSelected(false);
                    frameRightel.setSelected(false);
                    frameIrancel.setSelected(false);
                }

                openKeyBoard();
            });
        }
    }

    private void onItemOperatorSelect() {
        frameHamrah.setOnClickListener(v -> {
            setAdapterValue(OperatorType.Type.HAMRAH_AVAL);
            setSelectedOperator(radioButtonHamrah, radioButtonIrancell, radioButtonRightel, frameHamrah, frameIrancel, frameRightel);
        });

        frameRightel.setOnClickListener(v -> {
            setAdapterValue(OperatorType.Type.RITEL);
            setSelectedOperator(radioButtonRightel, radioButtonIrancell, radioButtonHamrah, frameRightel, frameIrancel, frameHamrah);
        });

        frameIrancel.setOnClickListener(v -> {
            setAdapterValue(OperatorType.Type.IRANCELL);
            setSelectedOperator(radioButtonIrancell, radioButtonHamrah, radioButtonRightel, frameIrancel, frameHamrah, frameRightel);
        });
    }

    private void setSelectedOperator(RadioButton radioButton1, RadioButton radioButton2, RadioButton radioButton3, View view1, View view2, View view3) {
        radioButton1.setChecked(true);
        radioButton2.setChecked(false);
        radioButton3.setChecked(false);
        view1.setSelected(true);
        view2.setSelected(false);
        view3.setSelected(false);
    }

    private void onPriceChooseClick() {
        priceChoose.setOnClickListener(v -> {
            if (operatorType != null) {
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
                        priceChoose.setBackgroundTintList(getContext().getColorStateList(R.color.border_editText));
                        btnChargeType.setBackgroundTintList(getContext().getColorStateList(R.color.green));
                    }
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
            } else {
                ShowError(getContext().getResources().getString(R.string.please_select_operator));
            }

        });

        ivAdd.setOnClickListener(v -> {
            if (selectedPriceIndex + 1 < amountList.size()) {
                amount = amountList.get(selectedPriceIndex = selectedPriceIndex + 1);
                amountTxt.setText(amount.getTextAmount());
            } else {
                amount = amountList.get(selectedPriceIndex);
                amountTxt.setText(amount.getTextAmount());
            }
        });

        lowView.setOnClickListener(v -> {
            if (selectedPriceIndex - 1 < amountList.size() && selectedPriceIndex > 0) {
                amount = amountList.get(selectedPriceIndex = selectedPriceIndex - 1);
                amountTxt.setText(amount.getTextAmount());
            } else {
                amount = amountList.get(selectedPriceIndex);
                amountTxt.setText(amount.getTextAmount());
            }
        });
    }

    private void onChargeTypeChooseClick() {
        btnChargeType.setOnClickListener(v -> {
            if (operatorType != null) {
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
                        btnChargeType.setBackgroundTintList(getContext().getColorStateList(R.color.border_editText));
                        enterBtn.setBackgroundTintList(getContext().getColorStateList(R.color.green));

                    }
                    chooseType.setTextColor(getContext().getResources().getColor(R.color.white));

                    editType.setVisibility(View.VISIBLE);
                    dialog.dismiss();

                });

                closeView4.setOnClickListener(v12 -> dialog.dismiss());

                rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                rvAmount.setAdapter(adapterChargeType);
                dialog.show();
            } else {
                btnChargeType.setClickable(false);
            }

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
                                        switch (selectedChargeTypeIndex) {
                                            case 0:
                                                chooseChargeType = ChooseChargeType.DIRECT;
                                                break;
                                            case 1:
                                                chooseChargeType = ChooseChargeType.YOUTH;
                                                break;
                                            case 2:
                                                chooseChargeType = ChooseChargeType.LADIES;
                                                break;
                                        }
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
        progressBar.setVisibility(View.GONE);
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
        MTN_NORMAL, MTN_AMAZING, RIGHTEL_NORMAL, RIGHTEL_EXCITING, DIRECT, AMAZING, YOUTH, LADIES
    }

    private void sendRequestCharge(String operator, ChooseChargeType chargeType, String phoneNumber, int price) {
        chargeApi.topUpPurchase(operator, chargeType != null ? chargeType.name() : null, phoneNumber, price).enqueue(new Callback<MciPurchaseResponse>() {
            @Override
            public void onResponse(Call<MciPurchaseResponse> call, Response<MciPurchaseResponse> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    String token = response.body().getToken();
                    if (getActivity() != null && token != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.buy_charge), token, result -> {
                            if (result.isSuccess()) {
                                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(R.string.save_purchase)
                                        .titleGravity(GravityEnum.START).negativeText(R.string.cansel)
                                        .positiveText(R.string.ok)
                                        .onNegative((dialog1, which) -> dialog1.dismiss()).show();
                                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
                                    progressBar.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("phone_number", phoneNumber);
                                    jsonObject.addProperty("charge_type", chargeType.toString());
                                    jsonObject.addProperty("amount", price);
                                    chargeApi.setFavoriteChargeNumber(operator, jsonObject).enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            progressBar.setVisibility(View.GONE);
                                            HelperError.showSnackMessage(getContext().getResources().getString(R.string.server_do_not_response), false);
                                        }
                                    });
                                });
                            }
                        });
                    }
                    goBack();
                }
            }

            @Override
            public void onFailure(Call<MciPurchaseResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                hideKeyboard();
                HelperError.showSnackMessage(getContext().getResources().getString(R.string.server_do_not_response), false);
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
