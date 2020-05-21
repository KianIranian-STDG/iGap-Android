package net.iGap.fragments.payment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
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
    private View frameHamrah;
    private View frameIrancel;
    private View frameRightel;
    private RadioButton radioButtonHamrah;
    private RadioButton radioButtonIrancell;
    private RadioButton radioButtonRightel;
    private RecyclerView rvContact;
    private RecyclerView rvHistory;
    private RecyclerView rvAmount;
    private AppCompatTextView amountTxt;
    private AppCompatTextView removeNumber;
    private AppCompatImageView ivAdd;
    private AppCompatImageView lowView;
    private MaterialButton priceChoose;
    private MaterialButton btnChargeType;
    private MaterialButton enterBtn;
    private AppCompatEditText editTextNumber;
    private ContactNumber contactNumber;
    private FavoriteNumber historyNumber;
    private Amount amount;
    private ChargeType typeList;
    private ScrollView scrollView;
    private FrameLayout progressBar;
    private LinearLayout linearWarning;
    private List<Amount> amountList = new ArrayList<>();
    private List<ChargeType> chargeTypeList = new ArrayList<>();
    private ChargeApi chargeApi;
    private OperatorType.Type currentOperator;
    private int selectedIndex;
    private int selectedChargeTypeIndex;
    private int selectedPriceIndex;
    private static final String MCI = "mci";
    private static final String MTN = "mtn";
    private static final String RIGHTEL = "rightel";

    public static FragmentPaymentChargeNewUi newInstance() {
        return new FragmentPaymentChargeNewUi();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_charge_newui, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null)
            return;

        LinearLayout toolbar = view.findViewById(R.id.payment_toolbar);
        radioButtonHamrah = view.findViewById(R.id.radio_hamrahAval);
        radioButtonIrancell = view.findViewById(R.id.radio_irancell);
        radioButtonRightel = view.findViewById(R.id.radio_rightel);
        View frameContact = view.findViewById(R.id.frame_contact);
        View frameHistory = view.findViewById(R.id.frame_history);
        priceChoose = view.findViewById(R.id.choose_amount);
        btnChargeType = view.findViewById(R.id.btn_charge_type);
        ivAdd = view.findViewById(R.id.add_amount);
        lowView = view.findViewById(R.id.low_amount);
        amountTxt = view.findViewById(R.id.tv_amount_btn);
        editTextNumber = view.findViewById(R.id.phoneNumberInput);
        enterBtn = view.findViewById(R.id.btn_pay);
        scrollView = view.findViewById(R.id.scroll_payment);
        progressBar = view.findViewById(R.id.loadingView);
        frameHamrah = view.findViewById(R.id.view12);
        frameIrancel = view.findViewById(R.id.view13);
        frameRightel = view.findViewById(R.id.view14);
        linearWarning = view.findViewById(R.id.llWarning);
        removeNumber = view.findViewById(R.id.btnRemoveSearch);
        chargeApi = new RetrofitFactory().getChargeRetrofit();

        editTextNumber.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);

        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            if (userInfo != null)
                editTextNumber.setText(userInfo.getPhoneNumber().replace("98", "0").replace("+98", "0").replace("0098", "0").replace(" ", "").replace("-", ""));
        });

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

        btnChargeType.setOnClickListener(v -> {
            if (currentOperator != null) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_type, false).build();
                View typeDialogView = dialog.getCustomView();

                if (typeDialogView != null) {
                    typeDialogView.findViewById(R.id.close_view4).setOnClickListener(v1 -> dialog.dismiss());
                    rvAmount = typeDialogView.findViewById(R.id.rv_type);
                    rvAmount.setAdapter(new AdapterChargeType(chargeTypeList));
                    rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

                    typeDialogView.findViewById(R.id.btn_dialog4).setOnClickListener(v16 -> {
                        AdapterChargeType adapterChargeType = (AdapterChargeType) rvAmount.getAdapter();
                        if (adapterChargeType != null) {
                            if (adapterChargeType.getSelectedPosition() == -1) {
                                return;
                            }

                            selectedChargeTypeIndex = adapterChargeType.getSelectedPosition();
                            typeList = chargeTypeList.get(selectedChargeTypeIndex);
                            btnChargeType.setText(typeList.getChargeType());
                            dialog.dismiss();
                        }
                    });
                }

                dialog.show();
            } else {
                btnChargeType.setClickable(false);
            }
        });

        frameContact.setOnClickListener(v -> {
            hideKeyboard();
            try {
                HelperPermission.getContactPermision(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() {
                        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_contact, false).build();
                        View contactDialogView = dialog.getCustomView();

                        if (contactDialogView != null) {
                            rvContact = contactDialogView.findViewById(R.id.rv_contact);
                            rvContact.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                            rvContact.setAdapter(new AdapterContactNumber());

                            AdapterContactNumber adapterContactNumber = (AdapterContactNumber) rvContact.getAdapter();
                            if (adapterContactNumber != null) {
                                new Contacts().getAllPhoneContactForPayment(adapterContactNumber::setContactNumbers);

                                contactDialogView.findViewById(R.id.btn_dialog1).setOnClickListener(v15 -> {
                                    if (adapterContactNumber.getSelectedPosition() == -1) {
                                        return;
                                    }

                                    selectedIndex = adapterContactNumber.getSelectedPosition();
                                    contactNumber = adapterContactNumber.getContactNumbers().get(selectedIndex);
                                    editTextNumber.setText(contactNumber.getPhone().replace(" ", "").replace("-", "").replace("+98", "0"));
                                    ivAdd.setVisibility(View.GONE);
                                    lowView.setVisibility(View.GONE);
                                    amountTxt.setVisibility(View.GONE);
                                    btnChargeType.setText(R.string.Select_the_type_of_charge);
                                    priceChoose.setText(R.string.select_the_amount);

                                    OperatorType.Type opt = new OperatorType().getOperation(editTextNumber.getText().toString().substring(0, 4));

                                    if (opt != null) {
                                        changeOperator(opt);
                                    }

                                    dialog.dismiss();
                                });
                            }
                            contactDialogView.findViewById(R.id.closeView).setOnClickListener(v12 -> dialog.dismiss());
                        }
                        dialog.show();
                    }

                    @Override
                    public void deny() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        frameHistory.setOnClickListener(v -> {
            hideKeyboard();
            progressBar.setVisibility(View.VISIBLE);
            chargeApi.getFavoriteChargeNUmber().enqueue(new Callback<GetFavoriteNumber>() {
                @Override
                public void onResponse(@NonNull Call<GetFavoriteNumber> call, @NonNull Response<GetFavoriteNumber> response) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        if (response.body().getData().size() > 0) {

                            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_history, false).build();
                            View historyDialogView = dialog.getCustomView();

                            if (historyDialogView != null) {
                                rvHistory = historyDialogView.findViewById(R.id.rv_history);
                                rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                                rvHistory.setAdapter(new AdapterHistoryNumber(response.body().getData()));

                                historyDialogView.findViewById(R.id.iv_close2).setOnClickListener(v12 -> dialog.dismiss());

                                historyDialogView.findViewById(R.id.btn_dialog2).setOnClickListener(v13 -> {
                                    AdapterHistoryNumber adapterHistory = (AdapterHistoryNumber) rvHistory.getAdapter();

                                    if (adapterHistory != null) {
                                        if (adapterHistory.getSelectedPosition() == -1) {
                                            return;
                                        }

                                        selectedIndex = adapterHistory.getSelectedPosition();
                                        historyNumber = adapterHistory.getHistoryNumberList().get(selectedIndex);

                                        editTextNumber.setText(historyNumber.getPhoneNumber().replace(" ", "").replace("-", "").replace("+98", "0"));
                                        amountTxt.setText(String.valueOf(historyNumber.getAmount()));
                                        priceChoose.setText("");
                                        btnChargeType.setText(historyNumber.getChargeType());

                                        dialog.dismiss();
                                    }
                                });
                            }
                            dialog.show();
                        } else {
                            showError((getContext().getResources().getString(R.string.list_empty)));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetFavoriteNumber> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    showError(getContext().getResources().getString(R.string.there_is_no_connection_to_server));

                }
            });

        });

        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextNumber.getText() != null && editTextNumber.getText().length() > 0) {
                    removeNumber.setVisibility(View.VISIBLE);

                    if (editTextNumber.getText().length() == 10 && editTextNumber.getText().charAt(0) != '0')
                        editTextNumber.setText("0".concat(editTextNumber.getText().toString()));

                    if (editTextNumber.getText().length() == 11 || editTextNumber.getText().length() == 4) {
                        if (editTextNumber.getText().length() == 11) {
                            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                            linearWarning.setVisibility(View.VISIBLE);
                        }
                        String number = editTextNumber.getText().toString().substring(0, 4);
                        OperatorType.Type operator = new OperatorType().getOperation(number);
                        if (operator != null) {
                            changeOperator(operator);
                        }
                    }
                    removeNumber.setOnClickListener(view1 -> {
                        editTextNumber.setText("");

                        removeNumber.setVisibility(View.INVISIBLE);

                        if (editTextNumber.getText().toString().equals("")) {
                            changeOperator(OperatorType.Type.UNKNOWN);
                        }

                        openKeyBoard();
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        frameHamrah.setOnClickListener(v -> changeOperator(OperatorType.Type.HAMRAH_AVAL));
        frameRightel.setOnClickListener(v -> changeOperator(OperatorType.Type.RITEL));
        frameIrancel.setOnClickListener(v -> changeOperator(OperatorType.Type.IRANCELL));

        enterBtn.setOnClickListener(v -> onSaveBtnClicked());
        priceChoose.setOnClickListener(v -> chosePriceClicked());

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

    private void chosePriceClicked() {
        if (currentOperator != null) {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_amount, false).build();
            View amountDialogView = dialog.getCustomView();

            if (amountDialogView != null) {
                rvAmount = amountDialogView.findViewById(R.id.rv_amount);
                rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                rvAmount.setAdapter(new AdapterChargeAmount(amountList));
                amountDialogView.findViewById(R.id.close_view3).setOnClickListener(v12 -> dialog.dismiss());

                amountDialogView.findViewById(R.id.btn_dialog3).setOnClickListener(v1 -> {
                    AdapterChargeAmount adapterAmount = (AdapterChargeAmount) rvAmount.getAdapter();
                    if (adapterAmount != null) {

                        if (adapterAmount.getSelectedPosition() == -1) {
                            return;
                        }

                        ivAdd.setVisibility(View.VISIBLE);
                        lowView.setVisibility(View.VISIBLE);
                        amountTxt.setVisibility(View.VISIBLE);
                        selectedPriceIndex = adapterAmount.getSelectedPosition();
                        amount = amountList.get(selectedPriceIndex);
                        amountTxt.setText(amount.getTextAmount());
                        priceChoose.setText("");
                        priceChoose.setClickable(false);
                        dialog.dismiss();
                    }
                });
            }


            dialog.show();
        } else {
            showError(getContext().getResources().getString(R.string.please_select_operator));
        }
    }

    private void changeOperator(OperatorType.Type operator) {
        currentOperator = operator;

        radioButtonHamrah.setChecked(currentOperator == OperatorType.Type.HAMRAH_AVAL);
        frameHamrah.setSelected(currentOperator == OperatorType.Type.HAMRAH_AVAL);

        radioButtonIrancell.setChecked(currentOperator == OperatorType.Type.IRANCELL);
        frameIrancel.setSelected(currentOperator == OperatorType.Type.IRANCELL);

        radioButtonRightel.setChecked(currentOperator == OperatorType.Type.RITEL);
        frameRightel.setSelected(currentOperator == OperatorType.Type.RITEL);
        setAdapterValue(operator);
    }

    private void setAdapterValue(@NotNull OperatorType.Type operator) {
        List<String> prices;
        List<String> chargeType;
        switch (operator) {
            case HAMRAH_AVAL:
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
        progressBar.setVisibility(View.VISIBLE);
        if (editTextNumber.getText() != null && isNumeric(editTextNumber.getText().toString()) && editTextNumber.getText().length() == 11) {
            if (currentOperator != null) {
                if (selectedChargeTypeIndex != -1) {
                    if (selectedPriceIndex != -1) {
                        ChooseChargeType chooseChargeType = null;
                        switch (currentOperator) {
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

                        if (currentOperator == OperatorType.Type.HAMRAH_AVAL) {
                            sendRequestCharge(MCI, chooseChargeType, editTextNumber.getText().toString().substring(1), (int) price);
                        } else if (currentOperator == OperatorType.Type.IRANCELL) {
                            sendRequestCharge(MTN, chooseChargeType, editTextNumber.getText().toString().substring(1), (int) price);
                        } else if (currentOperator == OperatorType.Type.RITEL) {
                            sendRequestCharge(RIGHTEL, chooseChargeType, editTextNumber.getText().toString().substring(1), (int) price);
                        }

                    } else {
                        showError(getContext().getResources().getString(R.string.charge_price_error_message));
                    }
                } else {
                    showError(getContext().getResources().getString(R.string.charge_type_error_message));
                }
            } else {
                showError(getContext().getResources().getString(R.string.please_select_operator));
            }
        } else {
            showError(getContext().getResources().getString(R.string.phone_number_is_not_valid));
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

    enum ChooseChargeType {
        MTN_NORMAL, MTN_AMAZING, RIGHTEL_NORMAL, RIGHTEL_EXCITING, DIRECT, AMAZING, YOUTH, LADIES
    }

    private void sendRequestCharge(String operator, ChooseChargeType chargeType, String phoneNumber, int price) {
        enterBtn.setEnabled(false);
        chargeApi.topUpPurchase(operator, chargeType != null ? chargeType.name() : null, phoneNumber, price).enqueue(new Callback<MciPurchaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<MciPurchaseResponse> call, @NonNull Response<MciPurchaseResponse> response) {
                enterBtn.setEnabled(true);
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
                                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
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
            public void onFailure(@NonNull Call<MciPurchaseResponse> call, @NonNull Throwable t) {
                enterBtn.setEnabled(true);
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

    private void showError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }
}
