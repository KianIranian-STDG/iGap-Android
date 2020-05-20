package net.iGap.fragments.payment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.G;
import net.iGap.R;
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
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.repository.MciInternetPackageRepository;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.helper.HelperString.isNumeric;
import static net.iGap.model.OperatorType.Type.HAMRAH_AVAL;
import static net.iGap.model.OperatorType.Type.IRANCELL;
import static net.iGap.model.OperatorType.Type.RITEL;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.MCI;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.MTN;
import static net.iGap.viewmodel.FragmentPaymentChargeViewModel.RIGHTEL;

public class FragmentPaymentInternet extends BaseFragment implements HandShakeCallback {

    public static final String SIM_TYPE_CREDIT = "CREDIT";
    public static final String SIM_TYPE_PERMANENT = "PERMANENT";
    public static final String SIM_TYPE_TD_LTE_CREDIT = "CREDIT_TD_LTE";
    public static final String SIM_TYPE_TD_LTE_PERMANENT = "PERMANENT_TD_LTE";
    public static final String SIM_TYPE_DATA = "DATA";

    private LinearLayout toolbar;
    private ConstraintLayout frameContact;
    private ConstraintLayout frameHistory;
    private ConstraintLayout frameHamrah;
    private ConstraintLayout frameIrancel;
    private ConstraintLayout frameRightel;
    private RadioButton radioButtonHamrah;
    private RadioButton radioButtonIrancell;
    private RadioButton radioButtonRightel;
    private AdapterHistoryNumber adapterHistory;
    private AdapterContactNumber adapterContact;
    private RecyclerView rvContact;
    private RecyclerView rvHistory;
    private MaterialButton enterBtn;
    private MaterialButton saveBtn1;
    private MaterialButton saveBtn2;
    private AppCompatEditText editTextNumber;
    private int selectedIndex;
    private ContactNumber contactNumber;
    private View closeView, closeView2;
    private OperatorType.Type operatorType;
    private RadioGroup rdGroup;
    private RadioButton rbCredit;
    private RadioButton rbPermanent;
    private RadioButton rbTdLteCredit;
    private RadioButton rbTdLtePermanent;
    private RadioButton rbData;
    private String simType = SIM_TYPE_CREDIT;
    private ChargeApi chargeApi;
    private FavoriteNumber historyNumber;
    private ProgressBar progressBar;
    private MaterialDesignTextView btnRemoveSearch;
    private MciInternetPackageRepository repository;

    private List<Amount> amountList = new ArrayList<>();
    private List<ChargeType> chargeTypeList = new ArrayList<>();
    private ScrollView scrollView;
    private FavoriteNumber favoriteNumber;

    public static FragmentPaymentInternet newInstance() {

        Bundle args = new Bundle();

        FragmentPaymentInternet fragment = new FragmentPaymentInternet();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_internet, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        radioButtonHamrah = view.findViewById(R.id.radio_hamrahAval);
        radioButtonIrancell = view.findViewById(R.id.radio_irancell);
        radioButtonRightel = view.findViewById(R.id.radio_rightel);
        frameContact = view.findViewById(R.id.frame_contact);
        frameHistory = view.findViewById(R.id.frame_history);
        editTextNumber = view.findViewById(R.id.phoneNumber);
        enterBtn = view.findViewById(R.id.btn_nextpage);
        frameHamrah = view.findViewById(R.id.view12);
        frameIrancel = view.findViewById(R.id.view13);
        frameRightel = view.findViewById(R.id.view14);
        rdGroup = view.findViewById(R.id.rdGroup);
        rbCredit = view.findViewById(R.id.rbCredit);
        scrollView = view.findViewById(R.id.scroll_payment);
        rbPermanent = view.findViewById(R.id.rbPermanent);
        rbTdLteCredit = view.findViewById(R.id.rbTdLteCredit);
        rbTdLtePermanent = view.findViewById(R.id.rbTdLtePermanent);
        rbData = view.findViewById(R.id.rbData);
        progressBar = view.findViewById(R.id.loadingView);
        btnRemoveSearch = view.findViewById(R.id.btnRemoveSearch);

        chargeApi = new RetrofitFactory().getChargeRetrofit();
        repository = MciInternetPackageRepository.getInstance();

        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            editTextNumber.setText(userInfo.getPhoneNumber());
            String number = userInfo.getPhoneNumber();
            editTextNumber.setText(number
                    .replace("98", "0")
                    .replace("+98", "0")
                    .replace("0098", "0")
                    .replace(" ", "")
                    .replace("-", ""));
            onPhoneNumberInput();
            editTextNumber.setSelection(editTextNumber.getText() == null ? 0 : editTextNumber.getText().length());
        });

        toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(getString(R.string.buy_internet_package_title))
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

        btnRemoveSearch.setOnClickListener(v -> {
            editTextNumber.setText(null);
            btnRemoveSearch.setVisibility(View.GONE);
        });

        onContactNumberButtonClick();
        onHistoryNumberButtonClick();
        onPhoneNumberInputClick();
        onItemOperatorSelect();

        enterBtn.setOnClickListener(v -> {
            if (this.favoriteNumber != null) {
                requestPayment(favoriteNumber);
                return;
            }
            if (operatorType != null) {
                if (editTextNumber.getText() == null) {
                    editTextNumber.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                String phoneNumber = editTextNumber.getText().toString().trim();
                if (!isNumeric(phoneNumber) || phoneNumber.length() < 11) {
                    editTextNumber.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPaymentInternetPackage.newInstance(phoneNumber, convertOperatorToString(operatorType), simType)).setReplace(false).load();
            } else {
                ShowError(getResources().getString(R.string.sim_type_not_choosed));
            }
        });

        radioButtonHamrah.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                operatorType = HAMRAH_AVAL;
                updateRadioGroup(operatorType);
            }
        });

        radioButtonIrancell.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                operatorType = IRANCELL;
                updateRadioGroup(operatorType);
            }
        });

        radioButtonRightel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                operatorType = RITEL;
                updateRadioGroup(operatorType);
            }
        });

        rbCredit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                simType = SIM_TYPE_CREDIT;
            }
        });

        rbPermanent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                simType = SIM_TYPE_PERMANENT;
            }
        });

        rbTdLteCredit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                simType = SIM_TYPE_TD_LTE_CREDIT;
            }
        });

        rbTdLtePermanent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                simType = SIM_TYPE_TD_LTE_PERMANENT;
            }
        });

        rbData.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                simType = SIM_TYPE_DATA;
            }
        });
    }

    private void requestPayment(FavoriteNumber favoriteNumber) {
        progressBar.setVisibility(View.VISIBLE);
        repository.purchaseInternetPackage(favoriteNumber.getOperator(), favoriteNumber.getPhoneNumber().substring(1),
                String.valueOf(favoriteNumber.getPackageType()), this, new ResponseCallback<MciPurchaseResponse>() {
                    @Override
                    public void onSuccess(MciPurchaseResponse data) {
                        G.handler.post(() -> {
                            if (getActivity() != null && data.getToken() != null) {
                                new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.buy_internet_package_title), true, data.getToken(), result -> {
                                    if (result.isSuccess()) {

                                        if (getActivity() != null)
                                            getActivity().onBackPressed();
                                    }
                                });
                            }
                            progressBar.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        HelperError.showSnackMessage(error, false);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailed() {
                        progressBar.setVisibility(View.GONE);
                        HelperError.showSnackMessage(getContext().getResources().getString(R.string.time_out_error), false);
                    }
                });
    }

    private void onPhoneNumberInput() {
        if (editTextNumber.getText().length() == 4 || editTextNumber.getText().length() == 11) {
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
                updateRadioGroup(opt);
            }
        }
        if (editTextNumber.getText().length() == 11) {
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
            hideKeyboard();
        }
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

    private String convertOperatorToString(OperatorType.Type opt) {
        switch (opt) {
            case RITEL:
                return RIGHTEL;
            case IRANCELL:
                return MTN;

            case HAMRAH_AVAL:
                return MCI;
        }
        return MTN;
    }

    public void ShowError(String errorMessage) {
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }

    private void onContactNumberButtonClick() {
        frameContact.setOnClickListener(v -> {
            try {
                HelperPermission.getContactPermision(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() {
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
            progressBar.setVisibility(View.VISIBLE);
            chargeApi.getFavoriteInternetPackage().enqueue(new Callback<GetFavoriteNumber>() {
                @Override
                public void onResponse(Call<GetFavoriteNumber> call, Response<GetFavoriteNumber> response) {
                    if (response.isSuccessful() && response.body().getData() != null) {
                        progressBar.setVisibility(View.GONE);
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
                            editTextNumber.setText(historyNumber.getPhoneNumber());
                            setFavoriteNumber(historyNumber);
                            dialog.dismiss();
                        });

                        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        rvHistory.setAdapter(adapterHistory);
                        dialog.show();

                    } else {
                        progressBar.setVisibility(View.GONE);
                        HelperError.showSnackMessage(getResources().getString(R.string.no_history_found), false);
                    }
                }

                @Override
                public void onFailure(Call<GetFavoriteNumber> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    HelperError.showSnackMessage(getResources().getString(R.string.no_history_found), false);
                }
            });
        });
    }

    private void setFavoriteNumber(FavoriteNumber historyNumber) {
        this.favoriteNumber = historyNumber;
    }


    private void onPhoneNumberInputClick() {
        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && btnRemoveSearch.getVisibility() == View.GONE) {
                    btnRemoveSearch.setVisibility(View.VISIBLE);
                }
                if (s.length() == 0)
                    btnRemoveSearch.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                favoriteNumber = null;
                if (editTextNumber.getText().length() == 11) {
                    String number = editTextNumber.getText().toString().substring(0, 4);
                    OperatorType.Type opt = new OperatorType().getOperation(number);
                    if (opt != null) {
                        switch (opt) {
                            case HAMRAH_AVAL:
                                operatorType = HAMRAH_AVAL;
                                setSelectedOperator(radioButtonHamrah, radioButtonIrancell, radioButtonRightel, frameHamrah, frameIrancel, frameRightel);
                                break;
                            case IRANCELL:
                                operatorType = IRANCELL;
                                setSelectedOperator(radioButtonIrancell, radioButtonHamrah, radioButtonRightel, frameIrancel, frameHamrah, frameRightel);
                                break;
                            case RITEL:
                                operatorType = RITEL;
                                setSelectedOperator(radioButtonRightel, radioButtonIrancell, radioButtonHamrah, frameRightel, frameIrancel, frameHamrah);
                                break;
                        }
                        updateRadioGroup(opt);
                    }

                }
            }
        });
    }

    private void updateRadioGroup(OperatorType.Type opt) {
        rbCredit.setVisibility(View.VISIBLE);
        rbPermanent.setVisibility(View.VISIBLE);
        if (opt == RITEL) {
            rbTdLteCredit.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.GONE);
            rbData.setVisibility(View.VISIBLE);
        } else if (opt == IRANCELL) {
            rbData.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.VISIBLE);
            rbTdLteCredit.setVisibility(View.VISIBLE);
        } else if (opt == HAMRAH_AVAL) {
            rbData.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.GONE);
            rbTdLteCredit.setVisibility(View.GONE);
        }
    }


    private void onItemOperatorSelect() {
        if (editTextNumber.getText() != null) {
            frameHamrah.setOnClickListener(v -> {
                operatorType = HAMRAH_AVAL;
                setSelectedOperator(radioButtonHamrah, radioButtonIrancell, radioButtonRightel, frameHamrah, frameIrancel, frameRightel);
            });

            frameRightel.setOnClickListener(v -> {
                operatorType = RITEL;
                setSelectedOperator(radioButtonRightel, radioButtonIrancell, radioButtonHamrah, frameRightel, frameIrancel, frameHamrah);
            });

            frameIrancel.setOnClickListener(v -> {
                operatorType = IRANCELL;
                setSelectedOperator(radioButtonIrancell, radioButtonHamrah, radioButtonRightel, frameIrancel, frameHamrah, frameRightel);
            });
        }

    }

    private void setSelectedOperator(RadioButton radioButton1, RadioButton radioButton2, RadioButton radioButton3, View view1, View view2, View view3) {
        radioButton1.setChecked(true);
        radioButton2.setChecked(false);
        radioButton3.setChecked(false);
        view1.setSelected(true);
        view2.setSelected(false);
        view3.setSelected(false);
    }

}
