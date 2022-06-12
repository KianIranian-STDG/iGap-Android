package net.iGap.fragments.payment;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.OperatorAdapter;
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
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.model.paymentPackage.Config;
import net.iGap.model.paymentPackage.ConfigData;
import net.iGap.model.paymentPackage.FavoriteNumber;
import net.iGap.model.paymentPackage.GetFavoriteNumber;
import net.iGap.model.paymentPackage.Operator;
import net.iGap.model.paymentPackage.PackageChargeType;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.repository.PaymentRepository;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.helper.HelperPermission.showDeniedPermissionMessage;
import static net.iGap.helper.HelperString.isNumeric;

public class InternetFragment extends BaseFragment implements HandShakeCallback {

    private View frameHistory;
    private View frameContact;
    private AppCompatEditText editTextNumber;
    private MaterialButton goNextButton;
    private MaterialDesignTextView removeButton;
    private RadioGroup radioGroup;
    private Toolbar toolbar;
    private FrameLayout toolbarContainer;
    private Config config;
    private String phoneNumber;
    private long peerId;
    private CircleImageView avatar;
    private RecyclerView lstOperator;
    private ScrollView scrollView;
    private ChargeApi chargeApi;
    private View progressBar;
    private TextWatcher watcher;
    private int clickedPosition = -1;
    private int selectedHistoryPosition = -1;
    private final List<Operator> operators = new ArrayList<>();
    private PaymentRepository paymentRepository;
    private OperatorAdapter operatorAdapter;
    private String userNumber;
    private ConfigData currentConfigData;
    private Operator currentOperator;
    private List<PackageChargeType> packageChargeTypes = new ArrayList<>();
    private String currentSimType = null;
    private FavoriteNumber historyNumber;


    public static InternetFragment newInstance() {
        return new InternetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_internet, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstraintLayout mainContainer = view.findViewById(R.id.mainContainer);
        mainContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            phoneNumber = bundle.getString("phoneNumber", "");
            peerId = bundle.getLong("peerId", 0);
        }
        AppCompatImageView iv_contact = view.findViewById(R.id.iv_contact);
        iv_contact.setBackgroundDrawable(Theme.tintDrawable(ContextCompat.getDrawable(context, R.drawable.ic_contact_new),context,Theme.getColor(Theme.key_icon)));
        AppCompatImageView iv_history = view.findViewById(R.id.iv_history);
        iv_history.setBackgroundDrawable(Theme.tintDrawable(ContextCompat.getDrawable(context, R.drawable.ic_recent),context,Theme.getColor(Theme.key_icon)));
        AppCompatTextView tv_contact =view.findViewById(R.id.tv_contact);
        tv_contact.setTextColor(Theme.getColor(Theme.key_default_text));
        AppCompatTextView tv_history =view.findViewById(R.id.tv_history);
        tv_history.setTextColor(Theme.getColor(Theme.key_default_text));
        AppCompatTextView operator_selection = view.findViewById(R.id.operator_selection);
        operator_selection.setTextColor(Theme.getColor(Theme.key_default_text));
        frameContact = view.findViewById(R.id.frame_contact);
        frameHistory = view.findViewById(R.id.frame_history);
        frameContact.setBackground(Theme.tintDrawable(getContext().getDrawable(R.drawable.shape_payment_charge),getContext(),Theme.getColor(Theme.key_window_background)));
        frameHistory.setBackground(Theme.tintDrawable(getContext().getDrawable(R.drawable.shape_payment_charge),getContext(),Theme.getColor(Theme.key_window_background)));
        editTextNumber = view.findViewById(R.id.phoneNumber);
        editTextNumber.setTextColor(Theme.getColor(Theme.key_default_text));
        editTextNumber.setHintTextColor(Theme.getColor(Theme.key_default_text));
        goNextButton = view.findViewById(R.id.btn_nextpage);
        goNextButton.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)));
        goNextButton.setTextColor(Theme.getColor(Theme.key_button_text));
        radioGroup = view.findViewById(R.id.rdGroup);
        progressBar = view.findViewById(R.id.loadingView);
        removeButton = view.findViewById(R.id.btnRemoveSearch);
        removeButton.setTextColor(Theme.getColor(Theme.key_icon));
        lstOperator = view.findViewById(R.id.lstOperator);
        scrollView = view.findViewById(R.id.scroll_payment);
        avatar = view.findViewById(R.id.avatar);
        toolbarContainer = view.findViewById(R.id.toolbar);
        toolbar = new Toolbar(getContext());
        toolbar.setTitle(getString(R.string.buy_internet_package_title));
        toolbar.setBackIcon(new BackDrawable(false));
        toolbarContainer.addView(toolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 64, Gravity.TOP));
        toolbar.setListener(i -> {
            if (i == -1) {
                getActivity().onBackPressed();
            }
        });
        lstOperator.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false));
        AppCompatTextView textView2 = view.findViewById(R.id.textView2);
        textView2.setTextColor(Theme.getColor(Theme.key_default_text));
        String userToken = TokenContainer.getInstance().getToken();
        paymentRepository = PaymentRepository.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        paymentRepository.getConfigs(userToken, config -> {
            if (config != null && getActivity() != null) {
                this.config = config;
                initForm();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paymentRepository.clearRepository();
        paymentRepository = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initForm() {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            if (userInfo != null) {
                userNumber = userInfo.getPhoneNumber();
                if (phoneNumber != null && !phoneNumber.isEmpty() && !phoneNumber.equals("0")) {
                    setPhoneNumberEditText(phoneNumber);
                    if (peerId != 0) {
                        avatarHandler.getAvatar(new ParamWithAvatarType(avatar, peerId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
                    }
                }
                if (phoneNumber != null && phoneNumber.equals("0")) {
                    avatar.setVisibility(View.GONE);
                    editTextNumber.setHint(getActivity().getString(R.string.please_enter_phone_number));
                    removeButton.setText(R.string.icon_edit);
                } else {
                    avatar.setVisibility(View.GONE);
                    setPhoneNumberEditText(userNumber);
                }

                if (editTextNumber.getText() != null && editTextNumber.getText().length() == 11) {
                    hideKeyboard();
                }
                editTextNumber.setSelection(editTextNumber.getText() == null ? 0 : editTextNumber.getText().length());
            }
        });
        operatorAdapter = new OperatorAdapter(getContext(), operators, operatorName -> {
            if (currentConfigData != null) {
                changeOperator(currentConfigData.getOperator());
            } else {
                operatorAdapter.setCheckedRadioButton(null);
                HelperError.showSnackMessage(getActivity().getString(R.string.please_enter_phone_number), false);
            }
        });
        lstOperator.setAdapter(operatorAdapter);
        detectOperatorByNumber(editTextNumber.getText().toString());
        chargeApi = new RetrofitFactory().getChargeRetrofit();
        editTextNumber.addTextChangedListener(editTextNumberWatcher());
        editTextNumber.requestFocus();
        removeButton.setOnClickListener(removeNumberClicked());
        frameContact.setOnClickListener(v -> onContactNumberButtonClick());
        frameHistory.setOnClickListener(v -> onHistoryNumberButtonClick());
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> InternetFragment.this.changeSimType());
        goNextButton.setOnClickListener(v -> {
            RadioButton radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            if (radioButton != null) {
                if (currentSimType == null) {
                    showError(getActivity().getString(R.string.invalid_sim_type));
                    return;
                }
                if (editTextNumber.getText() == null) {
                    editTextNumber.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                String rechargeableNumber = editTextNumber.getText().toString().trim();
                if (!isNumeric(rechargeableNumber) || rechargeableNumber.length() < 11) {
                    editTextNumber.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                if (!isNumberFromIran(rechargeableNumber)) {
                    editTextNumber.setError(getString(R.string.phone_number_is_not_valid));
                    return;
                }
                int packageType = historyNumber != null ? Integer.parseInt(historyNumber.getPackageType()) : -1;
                new HelperFragment(getActivity().getSupportFragmentManager(), PaymentInternetFragment.newInstance(userNumber, rechargeableNumber, currentOperator.getKey(), currentSimType, packageType)).setAnimated(false).setReplace(false).load();
            } else {
                showError(getActivity().getString(R.string.sim_type_not_choosed));
            }
        });
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void detectOperatorByNumber(String editText) {
        if (editText.length() == 10 && editText.charAt(0) != '0')
            editTextNumber.setText("0".concat(editTextNumber.getText().toString()));
        if (editText.length() == 11 || removeButton.getText().toString().equals(getActivity().getString(R.string.icon_edit))) {
            operators.clear();
            packageChargeTypes.clear();
            for (ConfigData configData : config.getData()) {
                operators.add(configData.getOperator());
                if (removeButton.getText().toString().equals(getActivity().getString(R.string.icon_edit))) {
                    currentConfigData = null;
                } else {
                    for (int j = 0; j < configData.getPreNumbers().size(); j++) {
                        if (editText.substring(0, 4).equals(configData.getPreNumbers().get(j))) {
                            currentConfigData = configData;
                        }
                    }
                }

            }
        }
        if (currentConfigData != null) {
            packageChargeTypes.addAll(currentConfigData.getPackageChargeTypes());
            changeOperator(currentConfigData.getOperator());
        } else {
            packageChargeTypes.clear();
        }
    }

    private TextWatcher editTextNumberWatcher () {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    removeButton.setText(R.string.icon_close);
                if (s.length() == 0)
                    removeButton.setText(R.string.icon_edit);
                avatar.setVisibility(View.GONE);

                String number = editTextNumber.getText().toString();
                if (number.length() == 11) {
                    operators.clear();
                    detectOperatorByNumber(number);
                    closeKeyboard(editTextNumber);
                }
            }
        };
    }

    private View.OnClickListener removeNumberClicked () {
        return v -> {
            avatar.setVisibility(View.GONE);
            if (editTextNumber.getText().length() > 0) {
                editTextNumber.setText(null);
                removeButton.setText(R.string.icon_edit);
            }
            editTextNumber.requestFocus();
            AndroidUtils.showKeyboard(editTextNumber);
        };
    }

    private boolean isNumberFromIran (String phoneNumber){
        boolean isValid = false;
        String phonePreNumber;
        if (phoneNumber.trim().charAt(0) == '0') {
            phonePreNumber = phoneNumber.substring(0, 4);
        } else {
            String standardize = phoneNumber.replace("+98", "0")
                    .replace("0098", "0");
            phonePreNumber = standardize.substring(0, 4);
        }
        for (ConfigData configData : config.getData()) {
            for (String preNumber : configData.getPreNumbers()) {
                if (phonePreNumber.equals(preNumber)) {
                    isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }

    private void changeSimType () {
        RadioButton radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        String tag = radioButton.getTag().toString();
        for (int i = 0; i < packageChargeTypes.size(); i++) {
            if (tag.equals(packageChargeTypes.get(i).getKey()))
                currentSimType = tag;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeOperator (Operator operator){
        if (currentOperator == operator)
            return;
        radioGroup.removeAllViewsInLayout();
        int index = 0;
        currentOperator = operator;
        operatorAdapter.setCheckedRadioButton(currentOperator.getKey());
        for (PackageChargeType packageChargeType : packageChargeTypes) {
            RadioButton radioButton = new RadioButton(getContext());
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{new int[]{-android.R.attr.state_enabled},new int[]{android.R.attr.state_enabled}},
                    new int[]{Theme.getColor(Theme.key_icon),Theme.getColor(Theme.key_theme_color)}
            );
            radioButton.setButtonTintList(colorStateList);
            radioButton.invalidate();
            radioButton.setId(index++);
            radioButton.setTextColor(Theme.getColor(Theme.key_title_text));
            radioButton.setTag(packageChargeType.getKey());
            radioButton.setText(packageChargeType.getTitle());
            radioButton.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.main_font));
            radioGroup.addView(radioButton);
        }
    }

    private void showError (String errorMessage){
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
    }

    private void onContactNumberButtonClick () {
        progressBar.setVisibility(View.VISIBLE);
        frameContact.setEnabled(false);
        closeKeyboard(editTextNumber);
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
                            HelperError.showSnackMessage(getActivity().getString(R.string.no_number_found), false);
                        } else {
                            adapterContact.setContactNumbers(contactNumbers);
                            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).backgroundColor(Theme.getColor(Theme.key_popup_background))
                                    .customView(R.layout.popup_paymet_contact, false)
                                    .negativeColor(Theme.getColor(Theme.key_button_background))
                                    .positiveColor(Theme.getColor(Theme.key_button_background)).build();
                            View contactDialogView = dialog.getCustomView();
                            if (contactDialogView != null) {
                                RecyclerView contactRecyclerView = contactDialogView.findViewById(R.id.rv_contact);
                                EditText editText = contactDialogView.findViewById(R.id.etSearch);
                                editText.setHintTextColor(Theme.getColor(Theme.key_default_text));
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
                    progressBar.setVisibility(View.GONE);
                    showDeniedPermissionMessage(G.context.getString(R.string.permission_contact));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onContactClicked (ChargeContactNumberAdapter adapterContact){
        if (clickedPosition == -1) {
            return;
        }
        ContactNumber contactNumber = adapterContact.getContactNumbers().get(clickedPosition);
        setPhoneNumberEditText(contactNumber.getPhone().trim());
        detectOperatorByNumber(editTextNumber.getText().toString());
    }

    private void setPhoneNumberEditText (String phone){
        phone = phone.replace("+", "");
        if (phone.contains("+") && !phone.contains("+98")) {
            showError(getActivity().getString(R.string.phone_number_is_not_valid));
            return;
        }
        if (phone.startsWith("98")) {
            phone = "0".concat(phone.substring(2));
        }
        editTextNumber.setText(phone.replace("+98", "0")
                .replace("0098", "0")
                .replace(" ", "")
                .replace("-", ""));
    }

    private void onHistoryNumberButtonClick () {
        frameHistory.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        closeKeyboard(editTextNumber);
        chargeApi.getFavoriteInternetPackage().clone().enqueue(new Callback<GetFavoriteNumber>() {
            @Override
            public void onResponse(@NotNull Call<GetFavoriteNumber> call, @NotNull Response<GetFavoriteNumber> response) {
                frameHistory.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<FavoriteNumber> numbers = response.body().getData();
                    if (numbers.size() == 0) {
                        progressBar.setVisibility(View.GONE);
                        HelperError.showSnackMessage(getActivity().getString(R.string.no_history_found), false);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                                .customView(R.layout.popup_paymet_history, false)
                                .negativeColor(Theme.getColor(Theme.key_button_background))
                                .positiveColor(Theme.getColor(Theme.key_button_background)).build();
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
                            AppCompatTextView iv_close2 = historyDialogView.findViewById(R.id.iv_close2);
                            iv_close2.setTextColor(Theme.getColor(Theme.key_default_text));
                            iv_close2.setOnClickListener(v12 -> dialog.dismiss());
                            AppCompatTextView txt_choose2 = historyDialogView.findViewById(R.id.txt_choose2);
                            txt_choose2.setTextColor(Theme.getColor(Theme.key_default_text));
                        }
                        dialog.show();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    HelperError.showSnackMessage(getActivity().getString(R.string.list_empty), false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetFavoriteNumber> call, @NotNull Throwable t) {
                frameHistory.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                HelperError.showSnackMessage(getActivity().getString(R.string.no_history_found), false);
            }
        });
    }

    private void onHistoryItemClicked (InternetHistoryPackageAdapter adapterHistory){
        if (selectedHistoryPosition == -1) {
            return;
        }
        historyNumber = adapterHistory.getHistoryNumberList().get(selectedHistoryPosition);
        setPhoneNumberEditText(historyNumber.getPhoneNumber());
        detectOperatorByNumber(editTextNumber.getText().toString());
    }

    public void setDialogBackground (View view){
        if (Theme.isDark() || Theme.isNight()) {
            view.setBackground(getActivity().getResources().getDrawable(R.drawable.search_contact_background));
        }
    }
}
