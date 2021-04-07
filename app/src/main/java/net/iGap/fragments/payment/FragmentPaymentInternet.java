package net.iGap.fragments.payment;

import android.graphics.Typeface;
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
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.OperatorRecyclerAdapter;
import net.iGap.adapter.payment.ChargeContactNumberAdapter;
import net.iGap.adapter.payment.ContactNumber;
import net.iGap.adapter.payment.InternetHistoryPackageAdapter;
import net.iGap.api.ChargeApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.controllers.PhoneContactProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.model.OperatorType;
import net.iGap.model.paymentPackage.Config;
import net.iGap.model.paymentPackage.ConfigData;
import net.iGap.model.paymentPackage.FavoriteNumber;
import net.iGap.model.paymentPackage.GetFavoriteNumber;
import net.iGap.model.paymentPackage.Operator;
import net.iGap.model.paymentPackage.PackageChargeType;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.repository.PaymentRepository;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.helper.HelperString.isNumeric;

public class FragmentPaymentInternet extends BaseFragment implements HandShakeCallback {

    public static final String MCI = "mci";
    public static final String MTN = "mtn";
    public static final String RIGHTEL = "rightel";
    private static final String SIM_TYPE_CREDIT = "CREDIT";
    private static final String SIM_TYPE_PERMANENT = "PERMANENT";
    private static final String SIM_TYPE_TD_LTE_CREDIT = "CREDIT_TD_LTE";
    private static final String SIM_TYPE_TD_LTE_PERMANENT = "PERMANENT_TD_LTE";
    private static final String SIM_TYPE_DATA = "DATA";
    private View frameHistory;
    private View frameContact;
    private AppCompatEditText numberEditText;
    private LinearLayout toolbar;
    private MaterialButton goNextButton;
    private MaterialDesignTextView btnRemoveSearch;
    private RadioGroup radioGroup;
    private OperatorType.Type currentOperator;
    private String currentSimType = null;
    private ChargeApi chargeApi;
    private FavoriteNumber historyNumber;
    private View progressBar;
    private TextWatcher watcher;
    private int clickedPosition = -1;
    private int selectedHistoryPosition = -1;
    private final List<Operator> operators = new ArrayList<>();
    private PaymentRepository paymentRepository;
    private List<PackageChargeType> mciPackageChargeTypes = new ArrayList<>();
    private List<PackageChargeType> mtnPackageChargeTypes = new ArrayList<>();
    private List<PackageChargeType> rightelPackageChargeTypes = new ArrayList<>();
    private OperatorRecyclerAdapter operatorRecyclerAdapter;
    private RecyclerView lstOperator;
    private ScrollView scrollView;
    private String userNumber;
    private long userId;
    private String phoneNumber;
    private long peerId;
    private CircleImageView avatar;

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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            phoneNumber = bundle.getString("phoneNumber", "");
            peerId = bundle.getLong("peerId", 0);
        }
        toolbar = view.findViewById(R.id.toolbar);
        frameContact = view.findViewById(R.id.frame_contact);
        frameHistory = view.findViewById(R.id.frame_history);
        numberEditText = view.findViewById(R.id.phoneNumber);
        goNextButton = view.findViewById(R.id.btn_nextpage);
        radioGroup = view.findViewById(R.id.rdGroup);
        progressBar = view.findViewById(R.id.loadingView);
        btnRemoveSearch = view.findViewById(R.id.btnRemoveSearch);
        lstOperator = view.findViewById(R.id.lstOperator);
        scrollView = view.findViewById(R.id.scroll_payment);
        avatar = view.findViewById(R.id.avatar);
        if (G.themeColor == Theme.DARK) {
            frameHistory.setBackground(getContext().getResources().getDrawable(R.drawable.shape_payment_charge_dark));
            frameContact.setBackground(getContext().getResources().getDrawable(R.drawable.shape_payment_charge_dark));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        lstOperator.setLayoutManager(new LinearLayoutManager(getActivity()));
        lstOperator.setLayoutManager(layoutManager);
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
        String userToken = TokenContainer.getInstance().getToken();
        paymentRepository = PaymentRepository.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        paymentRepository.getConfigs(userToken, new PaymentRepository.ReceiveData() {
            @Override
            public void onReceiveData(Config config) {
                if (config != null) {
                    for (ConfigData configData : config.getData()) {
                        operators.add(configData.getOperator());
                        if (configData.getOperator().getKey().contentEquals("mci")) {
                            mciPackageChargeTypes = configData.getPackageChargeTypes();
                        }
                        if (configData.getOperator().getKey().contentEquals("mtn")) {
                            mtnPackageChargeTypes = configData.getPackageChargeTypes();
                        }
                        if (configData.getOperator().getKey().contentEquals("rightel")) {
                            rightelPackageChargeTypes = configData.getPackageChargeTypes();
                        }
                    }
                    initForm();
                }
            }
        });
    }

    private void initForm() {
        operatorRecyclerAdapter = new OperatorRecyclerAdapter(getContext(), operators, new OperatorRecyclerAdapter.SelectedRadioButton() {
            @Override
            public void onSelectedRadioButton(String operatorName) {
                switch (operatorName) {
                    case MCI:
                        changeOperator(OperatorType.Type.HAMRAH_AVAL);
                        break;
                    case MTN:
                        changeOperator(OperatorType.Type.IRANCELL);
                        break;
                    case RIGHTEL:
                        changeOperator(OperatorType.Type.RITEL);
                        break;
                }
            }
        });
        lstOperator.setAdapter(operatorRecyclerAdapter);
        chargeApi = new RetrofitFactory().getChargeRetrofit();
        numberEditText.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        changeOperator(OperatorType.Type.HAMRAH_AVAL);
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            if (userInfo != null) {
                userId = userInfo.getId();
                userNumber = userInfo.getPhoneNumber();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    setPhoneNumberEditText(phoneNumber);
                    if (peerId != 0) {
                        avatarHandler.getAvatar(new ParamWithAvatarType(avatar, peerId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
                    }
                } else {
                    avatar.setVisibility(View.GONE);
                    setPhoneNumberEditText(userNumber);
                }
                onPhoneNumberInput();
                numberEditText.setSelection(numberEditText.getText() == null ? 0 : numberEditText.getText().length());
            }
        });
        btnRemoveSearch.setOnClickListener(v -> {
            avatar.setVisibility(View.GONE);
            numberEditText.setText(null);
            btnRemoveSearch.setVisibility(View.INVISIBLE);
        });
        numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                avatar.setVisibility(View.GONE);
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
        goNextButton.setOnClickListener(v -> {
            RadioButton radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            if (radioButton != null) {
                if (currentSimType == null) {
                    showError(getResources().getString(R.string.invalid_sim_type));
                    return;
                }
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
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
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
        RadioButton radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        String tag = radioButton.getTag().toString();
        if (tag.equals("CREDIT"))
            currentSimType = SIM_TYPE_CREDIT;
        else if (tag.equals("PERMANENT"))
            currentSimType = SIM_TYPE_PERMANENT;
        else if (tag.equals("CREDIT_TD_LTE"))
            currentSimType = SIM_TYPE_TD_LTE_CREDIT;
        else if (tag.equals("PERMANENT_TD_LTE"))
            currentSimType = SIM_TYPE_TD_LTE_PERMANENT;
        else if (tag.equals("DATA"))
            currentSimType = SIM_TYPE_DATA;
        else
            currentSimType = null;
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
        radioGroup.removeAllViewsInLayout();
        int index = 0;
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.main_font);
        int textColor = Theme.getInstance().getTitleTextColor(getContext());
        String operatorName = currentOperator.name();
        switch (operatorName) {
            case "HAMRAH_AVAL":
                operatorRecyclerAdapter.setCheckedRadioButton(MCI);
                currentOperator = OperatorType.Type.HAMRAH_AVAL;
                for (PackageChargeType packageChargeType : mciPackageChargeTypes) {
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setId(index++);
                    radioButton.setTextColor(textColor);
                    radioButton.setTag(packageChargeType.getKey());
                    radioButton.setText(packageChargeType.getTitle());
                    radioButton.setTypeface(typeface);
                    radioGroup.addView(radioButton);
                }
                break;
            case "IRANCELL":
                operatorRecyclerAdapter.setCheckedRadioButton(MTN);
                currentOperator = OperatorType.Type.IRANCELL;
                for (PackageChargeType packageChargeType : mtnPackageChargeTypes) {
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setId(index++);
                    radioButton.setTextColor(textColor);
                    radioButton.setTag(packageChargeType.getKey());
                    radioButton.setText(packageChargeType.getTitle());
                    radioButton.setTypeface(typeface);
                    radioGroup.addView(radioButton);
                }
                break;
            case "RITEL":
                operatorRecyclerAdapter.setCheckedRadioButton(RIGHTEL);
                currentOperator = OperatorType.Type.RITEL;
                for (PackageChargeType packageChargeType : rightelPackageChargeTypes) {
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setId(index++);
                    radioButton.setTextColor(textColor);
                    radioButton.setTag(packageChargeType.getKey());
                    radioButton.setText(packageChargeType.getTitle());
                    radioButton.setTypeface(typeface);
                    radioGroup.addView(radioButton);
                }
                break;
            case "UNKNOWN":
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

    private void showError(String errorMessage) {
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }

    private void onContactNumberButtonClick() {
        progressBar.setVisibility(View.VISIBLE);
        frameContact.setEnabled(false);
        try {
            HelperPermission.getContactPermision(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() {
                    ChargeContactNumberAdapter adapterContact = new ChargeContactNumberAdapter();
                    PhoneContactProvider.getInstance().getAllPhoneContactForPayment(contactNumbers -> {
                        if (getContext() == null) {
                            return;
                        }
                        frameContact.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        if (contactNumbers.size() == 0) {
                            HelperError.showSnackMessage(getResources().getString(R.string.no_number_found), false);
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

    public void setDialogBackground(View view) {
        if (G.themeColor == Theme.DARK) {
            view.setBackground(getContext().getResources().getDrawable(R.drawable.search_contact_background));
        }
    }
}
