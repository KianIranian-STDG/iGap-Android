package net.iGap.fragments.payment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.payment.ChargeContactNumberAdapter;
import net.iGap.adapter.payment.ContactNumber;
import net.iGap.adapter.payment.InternetHistoryPackageAdapter;
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
import net.iGap.module.Contacts;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmRegisteredInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

    private static final String SIM_TYPE_CREDIT = "CREDIT";
    private static final String SIM_TYPE_PERMANENT = "PERMANENT";
    private static final String SIM_TYPE_TD_LTE_CREDIT = "CREDIT_TD_LTE";
    private static final String SIM_TYPE_TD_LTE_PERMANENT = "PERMANENT_TD_LTE";
    private static final String SIM_TYPE_DATA = "DATA";

    private View frameHistory;
    private View frameContact;
    private View frameHamrah;
    private View frameIrancel;
    private View frameRightel;
    private RadioButton radioButtonHamrah;
    private RadioButton radioButtonIrancell;
    private RadioButton radioButtonRightel;
    private AppCompatEditText numberEditText;
    private LinearLayout toolbar;
    private MaterialButton goNextButton;
    private MaterialDesignTextView btnRemoveSearch;
    private RadioGroup radioGroup;
    private OperatorType.Type currentOperator;
    private RadioButton rbCredit;
    private RadioButton rbPermanent;
    private RadioButton rbTdLteCredit;
    private RadioButton rbTdLtePermanent;
    private RadioButton rbData;
    private String currentSimType = SIM_TYPE_CREDIT;
    private ChargeApi chargeApi;
    private FavoriteNumber historyNumber;
    private View progressBar;
    private TextWatcher watcher;
    private int clickedPosition = -1;
    private int selectedHistoryPosition = -1;

    public static FragmentPaymentInternet newInstance() {
        return new FragmentPaymentInternet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_internet, container, false);
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
        numberEditText = view.findViewById(R.id.phoneNumber);
        goNextButton = view.findViewById(R.id.btn_nextpage);
        frameHamrah = view.findViewById(R.id.view12);
        frameIrancel = view.findViewById(R.id.view13);
        frameRightel = view.findViewById(R.id.view14);
        rbCredit = view.findViewById(R.id.rbCredit);
        radioGroup = view.findViewById(R.id.rdGroup);
        rbPermanent = view.findViewById(R.id.rbPermanent);
        rbTdLteCredit = view.findViewById(R.id.rbTdLteCredit);
        rbTdLtePermanent = view.findViewById(R.id.rbTdLtePermanent);
        rbData = view.findViewById(R.id.rbData);
        progressBar = view.findViewById(R.id.loadingView);
        btnRemoveSearch = view.findViewById(R.id.btnRemoveSearch);

        chargeApi = new RetrofitFactory().getChargeRetrofit();
        numberEditText.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);


        setViewBackground(frameHamrah);
        setViewBackground(frameIrancel);
        setViewBackground(frameRightel);

        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            if (userInfo != null) {
                String number = userInfo.getPhoneNumber();
                setPhoneNumberEditText(number);
                onPhoneNumberInput();
                numberEditText.setSelection(numberEditText.getText() == null ? 0 : numberEditText.getText().length());
            }
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
            numberEditText.setText(null);
            btnRemoveSearch.setVisibility(View.INVISIBLE);
        });

        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && btnRemoveSearch.getVisibility() == View.INVISIBLE) {
                    btnRemoveSearch.setVisibility(View.VISIBLE);
                }
                if (s.length() == 0)
                    btnRemoveSearch.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (numberEditText.getText() != null && numberEditText.getText().length() == 11) {
                    String number = numberEditText.getText().toString().substring(0, 4);
                    OperatorType.Type opt = new OperatorType().getOperation(number);
                    if (opt != null) {
                        changeOperator(opt);
                    }
                    if (numberEditText.getText().length() == 11) {
                        closeKeyboard(numberEditText);
                    }
                }
            }
        });

        frameContact.setOnClickListener(v -> onContactNumberButtonClick());
        frameHistory.setOnClickListener(v -> onHistoryNumberButtonClick());
        frameHamrah.setOnClickListener(v -> changeOperator(HAMRAH_AVAL));
        frameRightel.setOnClickListener(v -> changeOperator(RITEL));
        frameIrancel.setOnClickListener(v -> changeOperator(IRANCELL));

        goNextButton.setOnClickListener(v -> {
            if (currentOperator != null) {
                if (numberEditText.getText() == null) {
                    numberEditText.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                String phoneNumber = numberEditText.getText().toString().trim();
                if (!isNumeric(phoneNumber) || phoneNumber.length() < 11) {
                    numberEditText.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                if (!isNumberFromIran(phoneNumber)) {
                    numberEditText.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                int packageType = historyNumber != null ? Integer.parseInt(historyNumber.getPackageType()) : -1;
                new HelperFragment(getActivity().getSupportFragmentManager(), PaymentInternetPackageFragment.newInstance(phoneNumber, convertOperatorToString(currentOperator), currentSimType, packageType)).setAnimated(false).setReplace(false).load();
            } else {
                showError(getResources().getString(R.string.sim_type_not_choosed));
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> changeSimType());


    }

    private boolean isNumberFromIran(String phoneNumber) {
        if (phoneNumber.trim().charAt(0) == '0' && (new OperatorType().isValidType(phoneNumber.substring(0, 4)) || new OperatorType().isValidType(phoneNumber.substring(0, 5))))
            return true;

        String standardize = phoneNumber.replace("+98", "0")
                .replace("0098", "0")
                .replace(" ", "")
                .replace("-", "");

        return new OperatorType().isValidType(standardize.substring(0, 4)) || new OperatorType().isValidType(phoneNumber.substring(0, 5));
    }

    private void changeSimType() {
        if (rbCredit.isChecked()) {
            currentSimType = SIM_TYPE_CREDIT;
        } else if (rbPermanent.isChecked()) {
            currentSimType = SIM_TYPE_PERMANENT;
        } else if (rbTdLteCredit.isChecked()) {
            currentSimType = SIM_TYPE_TD_LTE_CREDIT;
        } else if (rbTdLtePermanent.isChecked()) {
            currentSimType = SIM_TYPE_TD_LTE_PERMANENT;
        } else if (rbData.isChecked()) {
            currentSimType = SIM_TYPE_DATA;
        }
    }

    private void onPhoneNumberInput() {
        if (numberEditText.getText() != null && numberEditText.getText().length() == 4 || numberEditText.getText() != null && numberEditText.getText().length() == 11) {
            String number = numberEditText.getText().toString().substring(0, 4);
            OperatorType.Type opt = new OperatorType().getOperation(number);
            if (opt != null) {
                changeOperator(opt);
            }
        }

        if (numberEditText.getText() != null && numberEditText.getText().length() == 11) {
            hideKeyboard();
        }
    }


    private void changeOperator(OperatorType.Type operator) {
        if (currentOperator == operator)
            return;

        currentOperator = operator;

        radioButtonHamrah.setChecked(currentOperator == OperatorType.Type.HAMRAH_AVAL);
        frameHamrah.setSelected(currentOperator == OperatorType.Type.HAMRAH_AVAL);

        radioButtonIrancell.setChecked(currentOperator == OperatorType.Type.IRANCELL);
        frameIrancel.setSelected(currentOperator == OperatorType.Type.IRANCELL);

        radioButtonRightel.setChecked(currentOperator == OperatorType.Type.RITEL);
        frameRightel.setSelected(currentOperator == OperatorType.Type.RITEL);

        rbCredit.setVisibility(View.VISIBLE);
        rbPermanent.setVisibility(View.VISIBLE);

        rbCredit.setChecked(true);
        currentSimType = SIM_TYPE_CREDIT;

        if (currentOperator == RITEL) {
            rbTdLteCredit.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.GONE);
            rbData.setVisibility(View.VISIBLE);
        } else if (currentOperator == IRANCELL) {
            rbData.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.VISIBLE);
            rbTdLteCredit.setVisibility(View.VISIBLE);
        } else if (currentOperator == HAMRAH_AVAL) {
            rbData.setVisibility(View.GONE);
            rbTdLtePermanent.setVisibility(View.GONE);
            rbTdLteCredit.setVisibility(View.GONE);
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

    private void showError(String errorMessage) {
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }

    private void onContactNumberButtonClick() {
        try {
            HelperPermission.getContactPermision(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() {
                    ChargeContactNumberAdapter adapterContact = new ChargeContactNumberAdapter();
                    new Contacts().getAllPhoneContactForPayment(contactNumbers -> {
                        if (contactNumbers.size() == 0) {
                            HelperError.showSnackMessage(getResources().getString(R.string.no_number_found), false);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            adapterContact.setContactNumbers(contactNumbers);
                            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_contact, false).build();
                            View contactDialogView = dialog.getCustomView();

                            if (contactDialogView != null) {
                                RecyclerView contactRecyclerView = contactDialogView.findViewById(R.id.rv_contact);
                                EditText editText = contactDialogView.findViewById(R.id.etSearch);

                                setDialogBackground(contactRecyclerView);
                                setDialogBackground(editText);

                                contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                                adapterContact.setOnItemClickListener(position -> {
                                    clickedPosition = position;
                                    onContactClicked(adapterContact);
                                    dialog.dismiss();
                                });

                                contactRecyclerView.setAdapter(adapterContact);

                                if (watcher != null)
                                    editText.removeTextChangedListener(watcher);

                                watcher = new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (s != null)
                                            adapterContact.search(s.toString());
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                };
                                editText.addTextChangedListener(watcher);



                                contactDialogView.findViewById(R.id.closeView).setOnClickListener(v12 -> dialog.dismiss());
                            }
                            dialog.show();
                        }
                    });

                }

                @Override
                public void deny() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onContactClicked(ChargeContactNumberAdapter adapterContact) {
        if (clickedPosition == -1) {
            return;
        }

        ContactNumber contactNumber = adapterContact.getContactNumbers().get(clickedPosition);
        setPhoneNumberEditText(contactNumber.getPhone().trim());

    }

    private void setPhoneNumberEditText(String phone) {
        phone = phone.replace("+", "");

        if (phone.contains("+") && !phone.contains("+98")) {
            showError(getResources().getString(R.string.phone_number_is_not_valid));
            return;
        }

        if (phone.startsWith("98")) {
            phone = "0".concat(phone.substring(2));
        }

        numberEditText.setText(phone.replace("+98", "0")
                .replace("0098", "0")
                .replace(" ", "")
                .replace("-", ""));
    }

    private void onHistoryNumberButtonClick() {
        frameHistory.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        chargeApi.getFavoriteInternetPackage().enqueue(new Callback<GetFavoriteNumber>() {
            @Override
            public void onResponse(@NotNull Call<GetFavoriteNumber> call, @NotNull Response<GetFavoriteNumber> response) {
                frameHistory.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<FavoriteNumber> numbers = response.body().getData();
                    if (numbers.size() == 0) {
                        progressBar.setVisibility(View.GONE);
                        HelperError.showSnackMessage(getResources().getString(R.string.no_history_found), false);
                    } else {
                        progressBar.setVisibility(View.GONE);

                        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_history, false).build();
                        View historyDialogView = dialog.getCustomView();

                        if (historyDialogView != null) {
                            InternetHistoryPackageAdapter adapterHistory = new InternetHistoryPackageAdapter(numbers);
                            adapterHistory.setOnItemClickListener(position -> {
                                selectedHistoryPosition = position;
                                onHistoryItemClicked(adapterHistory);
                                dialog.dismiss();
                            });

                            RecyclerView rvHistory = historyDialogView.findViewById(R.id.rv_history);
                            rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                            rvHistory.setAdapter(adapterHistory);
                            setDialogBackground(rvHistory);

                            historyDialogView.findViewById(R.id.iv_close2).setOnClickListener(v12 -> dialog.dismiss());
                        }

                        dialog.show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    HelperError.showSnackMessage(getResources().getString(R.string.list_empty), false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetFavoriteNumber> call, @NotNull Throwable t) {
                frameHistory.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                HelperError.showSnackMessage(getResources().getString(R.string.no_history_found), false);
            }
        });
    }

    private void onHistoryItemClicked(InternetHistoryPackageAdapter adapterHistory) {
        if (selectedHistoryPosition == -1) {
            return;
        }

        historyNumber = adapterHistory.getHistoryNumberList().get(selectedHistoryPosition);
        setPhoneNumberEditText(historyNumber.getPhoneNumber());
    }

    public void setViewBackground(View view) {
        switch (G.themeColor) {
            case Theme.DARK:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_dark));
                frameHistory.setBackground(getContext().getResources().getDrawable(R.drawable.shape_payment_charge_dark));
                frameContact.setBackground(getContext().getResources().getDrawable(R.drawable.shape_payment_charge_dark));
                break;
            case Theme.AMBER:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_amber));
                break;
            case Theme.GREEN:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_green));
                break;
            case Theme.BLUE:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_blue));
                break;
            case Theme.PURPLE:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_purple));
                break;
            case Theme.PINK:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_pink));
                break;
            case Theme.RED:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_red));
                break;
            case Theme.ORANGE:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_orange));
                break;
            case Theme.GREY:
                view.setBackground(getContext().getResources().getDrawable(R.drawable.selector_topup_operator_dark_gray));
                break;
        }
    }

    public void setDialogBackground(View view) {
        if (G.themeColor==Theme.DARK) {
            view.setBackground(getContext().getResources().getDrawable(R.drawable.search_contact_background));
        }
    }
}
