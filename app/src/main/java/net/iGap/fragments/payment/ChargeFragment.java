package net.iGap.fragments.payment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.OperatorAdapter;
import net.iGap.adapter.payment.AdapterChargeAmount;
import net.iGap.adapter.payment.AdapterChargeType;
import net.iGap.adapter.payment.Amount;
import net.iGap.adapter.payment.ChargeContactNumberAdapter;
import net.iGap.adapter.payment.ChargeHistoryNumberAdapter;
import net.iGap.adapter.payment.ContactNumber;
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
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.model.paymentPackage.Config;
import net.iGap.model.paymentPackage.ConfigData;
import net.iGap.model.paymentPackage.FaceValue;
import net.iGap.model.paymentPackage.GetFavoriteNumber;
import net.iGap.model.paymentPackage.MciPurchaseResponse;
import net.iGap.model.paymentPackage.Operator;
import net.iGap.model.paymentPackage.TopupChargeType;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.repository.PaymentRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChargeFragment extends BaseFragment {

    private AppCompatEditText editTextNumber;
    private View contactButton;
    private View historyButton;
    private RecyclerView RecyclerViewHistory;
    private RecyclerView RecyclerViewAmount;
    private AppCompatTextView iconRemove;
    private MaterialButton buttonAmount;
    private MaterialButton buttonChargeType;
    private MaterialButton buttonEnter;
    private AppCompatImageView amountPlusButton;
    private AppCompatImageView amountMinesButton;
    private ScrollView scrollView;
    private FrameLayout progressBar;
    private CircleImageView imageViewAvatar;
    private Operator currentOperator;
    private Amount currentAmount;
    private ConfigData currentConfigData;
    private Config config;
    private final List<Operator> operators = new ArrayList<>();
    private final List<Amount> faceValueList = new ArrayList<>();
    private final List<TopupChargeType> chargeTypesList = new ArrayList<>();
    private ChargeApi chargeApi;
    private RecyclerView recyclerViewOperator;
    private OperatorAdapter adapterOperator;
    private CompositeDisposable compositeDisposable;
    private TextWatcher textWatcher;
    private PaymentRepository paymentRepository;
    private String userNumber;
    private String phoneNumber;
    private long peerId;
    private int contactPositionClicked = -1;
    private int historyItemClicked = -1;
    private boolean isSelectedFromHistory = false;
    private int amountDefaultIndex;
    private int amountSelectedIndex = -1;
    private int chargeTypeDefaultIndex;
    private int chargeTypeSelectedIndex;

    public static ChargeFragment newInstance() {
        Bundle args = new Bundle();
        ChargeFragment fragment = new ChargeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_charge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollView = view.findViewById(R.id.scroll_payment);
        contactButton = view.findViewById(R.id.frame_contact);
        historyButton = view.findViewById(R.id.frame_history);
        buttonAmount = view.findViewById(R.id.choose_amount);
        buttonChargeType = view.findViewById(R.id.btn_charge_type);
        amountPlusButton = view.findViewById(R.id.add_amount);
        amountMinesButton = view.findViewById(R.id.low_amount);
        editTextNumber = view.findViewById(R.id.phoneNumber);
        buttonEnter = view.findViewById(R.id.btn_pay);
        progressBar = view.findViewById(R.id.loadingView);
        iconRemove = view.findViewById(R.id.btnRemoveSearch);
        recyclerViewOperator = view.findViewById(R.id.lstOperator);
        imageViewAvatar = view.findViewById(R.id.avatar);
        compositeDisposable = new CompositeDisposable();
        chargeApi = new RetrofitFactory().getChargeRetrofit();
        progressBar.setVisibility(View.VISIBLE);

        //get phoneNumber AND peerId
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            phoneNumber = bundle.getString("phoneNumber", "");
            peerId = bundle.getLong("peerId", 0);
        }

        //init toolbar
        FrameLayout toolbarContainer = view.findViewById(R.id.charge_toolbar);
        Toolbar toolbar = new Toolbar(requireContext());
        toolbar.setTitle(getString(R.string.buy_charge));
        toolbar.setBackIcon(new BackDrawable(false));
        toolbarContainer.addView(toolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 64, Gravity.TOP));
        toolbar.setListener(i -> {
            if (i == -1) {
                getActivity().onBackPressed();
            }
        });

        if (G.themeColor == Theme.DARK) {
            historyButton.setBackground(getContext().getResources().getDrawable(R.drawable.shape_payment_charge_dark));
            contactButton.setBackground(getContext().getResources().getDrawable(R.drawable.shape_payment_charge_dark));
        }

        paymentRepository = PaymentRepository.getInstance();
        paymentRepository.getConfigs(TokenContainer.getInstance().getToken(), config -> {
            if (config != null) {
                this.config = config;
                initForm();
            }
        });

        recyclerViewOperator.setLayoutManager(new GridLayoutManager(context, 3, RecyclerView.VERTICAL, false));

    }

    private void initForm() {
        //init number from dataBase
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            if (userInfo != null && editTextNumber.getText() != null) {
                userNumber = userInfo.getPhoneNumber();
                if (phoneNumber != null && !phoneNumber.isEmpty() && !phoneNumber.equals("0")) {
                    setPhoneNumberEditText(phoneNumber);
                    if (peerId != 0) {
                        avatarHandler.getAvatar(new ParamWithAvatarType(imageViewAvatar, peerId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
                    }
                } else if (phoneNumber != null && phoneNumber.equals("0")) {
                    imageViewAvatar.setVisibility(View.GONE);
                    editTextNumber.setHint(getResources().getString(R.string.please_enter_phone_number));
                    iconRemove.setText(R.string.icon_edit);
                } else {
                    imageViewAvatar.setVisibility(View.GONE);
                    setPhoneNumberEditText(userNumber);
                }
            }
        });

        //init RecyclerViewOperator
        adapterOperator = new OperatorAdapter(getContext(), operators, operatorName -> {
            for (ConfigData configData : config.getData()) {
                if (configData.getOperator().getKey().equals(operatorName)) {
                    currentConfigData = configData;
                    changeOperator(currentConfigData.getOperator());
                    break;
                }
            }
        });
        recyclerViewOperator.setAdapter(adapterOperator);

        detectOperatorByNumber(editTextNumber.getText().toString());
        editTextNumber.addTextChangedListener(editTextNumberWatcher());
        iconRemove.setOnClickListener(removeNumberClicked());
        historyButton.setOnClickListener(historyButtonClicked());
        contactButton.setOnClickListener(contactButtonClicked());
        buttonAmount.setOnClickListener(v -> {
            choosePriceButtonClicked();
        });
        amountPlusButton.setOnClickListener(v -> {
            isHistorySelected(false);
            currentAmount = new Amount(currentAmount, true);
            buttonAmount.setText(currentAmount.getTextAmount());
        });
        amountMinesButton.setOnClickListener(v -> {
            if (currentAmount.getAmount() != 10000) {
                isHistorySelected(false);
                currentAmount = new Amount(currentAmount, false);
                buttonAmount.setText(currentAmount.getTextAmount());
            }
        });
        buttonChargeType.setOnClickListener(v -> {
            chooseChargeTypeClicked();
        });
        buttonEnter.setOnClickListener(v -> onSaveBtnClicked());
        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void detectOperatorByNumber(String editText) {
        if (editText.length() == 10 && editText.charAt(0) != '0')
            editTextNumber.setText("0".concat(Objects.requireNonNull(editTextNumber.getText()).toString()));
        if (editText.length() == 11 || iconRemove.getText().toString().equals(getResources().getString(R.string.icon_edit))) {
            operators.clear();
            for (ConfigData configData : config.getData()) {
                operators.add(configData.getOperator());
                if (iconRemove.getText().toString().equals(getResources().getString(R.string.icon_edit))) {
                    currentConfigData = config.getData().get(0);
                } else {
                    for (String preNumber : configData.getPreNumbers()) {
                        if (preNumber.equals(editText.substring(0, 4))) {
                            currentConfigData = configData;
                            break;
                        }
                    }
                }
            }
        }

        if (currentConfigData != null) {
            faceValueList.clear();
            for (FaceValue faceValue : currentConfigData.getFaceValues()) {
                faceValueList.add(new Amount(faceValue.getKey()));
                if (faceValue.getSelected())
                    amountDefaultIndex = faceValueList.size() - 1;
            }

            chargeTypesList.clear();
            chargeTypesList.addAll(currentConfigData.getTopupChargeTypes());
        }

        if (currentConfigData != null)
            changeOperator(currentConfigData.getOperator());
    }

    private void changeOperator(Operator operator) {
        if (currentOperator == operator) {
            return;
        } else {
            clearAmountAndType();
            currentOperator = operator;
            adapterOperator.setCheckedRadioButton(currentOperator.getKey());
            currentAmount = faceValueList.get(amountDefaultIndex);
            buttonAmount.setText(currentAmount.getTextAmount());
            chargeTypesList.clear();
            chargeTypesList.addAll(currentConfigData.getTopupChargeTypes());
            if (chargeTypesList.size() > 0)
                buttonChargeType.setText(chargeTypesList.get(chargeTypeDefaultIndex).getTitle());
            progressBar.setVisibility(View.GONE);
        }
    }

    private TextWatcher editTextNumberWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isHistorySelected(false);
                if (s.length() > 0)
                    iconRemove.setText(R.string.icon_close);
                if (s.length() == 0)
                    iconRemove.setText(R.string.icon_edit);
                imageViewAvatar.setVisibility(View.GONE);

                String number = editTextNumber.getText().toString();
                if (number.length() == 11) {
                    detectOperatorByNumber(number);
                    closeKeyboard(editTextNumber);
                }
            }
        };
    }

    private View.OnClickListener removeNumberClicked() {
        return view1 -> {
            imageViewAvatar.setVisibility(View.GONE);
            ChargeFragment.this.isHistorySelected(false);
            if (editTextNumber.getText().length() > 0) {
                editTextNumber.setText(null);
                iconRemove.setText(R.string.icon_edit);
            }
            editTextNumber.requestFocus();
            AndroidUtils.showKeyboard(editTextNumber);
        };
    }

    private View.OnClickListener historyButtonClicked() {
        return v -> {
            ChargeFragment.this.hideKeyboard();
            progressBar.setVisibility(View.VISIBLE);
            historyButton.setEnabled(false);
            closeKeyboard(editTextNumber);
            chargeApi.getFavoriteChargeNUmber().enqueue(new Callback<GetFavoriteNumber>() {
                @Override
                public void onResponse(@NonNull Call<GetFavoriteNumber> call, @NonNull Response<GetFavoriteNumber> response) {
                    progressBar.setVisibility(View.GONE);
                    historyButton.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        if (response.body().getData().size() > 0) {
                            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_history, false).build();
                            View historyDialogView = dialog.getCustomView();
                            if (historyDialogView != null) {
                                RecyclerViewHistory = historyDialogView.findViewById(R.id.rv_history);
                                setDialogBackground(RecyclerViewHistory);
                                RecyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                                ChargeHistoryNumberAdapter adapter = new ChargeHistoryNumberAdapter(response.body().getData());
                                RecyclerViewHistory.setAdapter(adapter);
                                adapter.setOnItemClickListener(position -> {
                                    historyItemClicked = position;
                                    onHistoryItemClicked();
                                    isHistorySelected(true);
                                    dialog.dismiss();
                                });
                                historyDialogView.findViewById(R.id.iv_close2).setOnClickListener(v12 -> dialog.dismiss());
                            }
                            dialog.show();
                        } else {
                            HelperError.showSnackMessage(getResources().getString(R.string.error), false);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GetFavoriteNumber> call, @NonNull Throwable t) {
                    historyButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    showError(getContext().getResources().getString(R.string.there_is_no_connection_to_server));
                }
            });
        };
    }

    private View.OnClickListener contactButtonClicked() {
        return v -> {
            hideKeyboard();
            progressBar.setVisibility(View.VISIBLE);
            closeKeyboard(editTextNumber);
            try {
                HelperPermission.getContactPermision(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() {
                        ChargeContactNumberAdapter adapter = new ChargeContactNumberAdapter();
                        contactButton.setEnabled(false);
                        PhoneContactProvider.getInstance().getAllPhoneContactForPayment(contactNumbers -> {
                            if (getContext() == null) {
                                return;
                            }
                            contactButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            if (contactNumbers.size() == 0) {
                                HelperError.showSnackMessage(getResources().getString(R.string.no_number_found), false);
                            } else {
                                adapter.setContactNumbers(contactNumbers);
                                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_contact, false).build();
                                View contactDialogView = dialog.getCustomView();
                                if (contactDialogView != null) {
                                    RecyclerView contactRecyclerView = contactDialogView.findViewById(R.id.rv_contact);
                                    EditText editText = contactDialogView.findViewById(R.id.etSearch);
                                    setDialogBackground(contactRecyclerView);
                                    setDialogBackground(editText);
                                    contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                                    contactRecyclerView.setAdapter(adapter);
                                    adapter.setOnItemClickListener(position -> {
                                        contactPositionClicked = position;
                                        onContactItemClicked(adapter);
                                        dialog.dismiss();
                                    });
                                    ChargeContactNumberAdapter adapterContactNumber = (ChargeContactNumberAdapter) contactRecyclerView.getAdapter();
                                    if (textWatcher != null)
                                        editText.removeTextChangedListener(textWatcher);
                                    textWatcher = new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if (adapterContactNumber != null)
                                                adapterContactNumber.search(s.toString());
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    };
                                    editText.addTextChangedListener(textWatcher);
                                    contactDialogView.findViewById(R.id.closeView).setOnClickListener(v12 -> dialog.dismiss());
                                }
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void deny() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private void onContactItemClicked(ChargeContactNumberAdapter adapter) {
        if (contactPositionClicked == -1) {
            return;
        }
        ContactNumber contactNumber = adapter.getContactNumbers().get(contactPositionClicked);
        setPhoneNumberEditText(contactNumber.getPhone());
        clearAmountAndType();
        if (editTextNumber.getText() != null && editTextNumber.getText().length() == 11) {
            detectOperatorByNumber(editTextNumber.getText().toString());
        } else {
            showError(getResources().getString(R.string.ivnalid_data_provided));
        }
    }

    private void onHistoryItemClicked() {
        ChargeHistoryNumberAdapter adapterHistory = (ChargeHistoryNumberAdapter) RecyclerViewHistory.getAdapter();
        if (adapterHistory != null) {
            if (historyItemClicked == -1) {
                return;
            }
            String historyNumberPhone = adapterHistory.getHistoryNumberList().get(historyItemClicked).getPhoneNumber();
            adapterHistory.getHistoryNumberList().get(historyItemClicked).getChargeType();
            setPhoneNumberEditText(historyNumberPhone);
            currentAmount = new Amount(adapterHistory.getHistoryNumberList().get(historyItemClicked).getAmount());
            buttonAmount.setText(currentAmount.getTextAmount());
            detectOperatorByNumber(editTextNumber.getText().toString());
        }
    }

    private void isHistorySelected(boolean isSelectedFromHistory) {
        this.isSelectedFromHistory = isSelectedFromHistory;
    }

    private void choosePriceButtonClicked() {
        if (currentOperator != null) {
            MaterialDialog dialog = new MaterialDialog.Builder(requireContext()).customView(R.layout.popup_paymet_amount, false).build();
            View amountDialogView = dialog.getCustomView();
            if (amountDialogView != null) {
                RecyclerViewAmount = amountDialogView.findViewById(R.id.rv_amount);
                RecyclerViewAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                RecyclerViewAmount.setAdapter(new AdapterChargeAmount(faceValueList, amountSelectedIndex != -1 ? amountSelectedIndex : amountDefaultIndex));

                amountDialogView.findViewById(R.id.close_view3).setOnClickListener(v12 -> dialog.dismiss());
                amountDialogView.findViewById(R.id.btn_dialog3).setOnClickListener(v1 -> {
                    AdapterChargeAmount adapterAmount = (AdapterChargeAmount) RecyclerViewAmount.getAdapter();
                    if (adapterAmount != null) {
                        if (adapterAmount.getSelectedPosition() == -1) {
                            return;
                        }
                        amountSelectedIndex = adapterAmount.getSelectedPosition();
                        currentAmount = faceValueList.get(amountSelectedIndex);
                        buttonAmount.setText(currentAmount.getTextAmount());
                        dialog.dismiss();
                    }
                });
            }
            dialog.show();
        } else {
            showError(getContext().getResources().getString(R.string.please_select_operator));
        }
    }

    private void chooseChargeTypeClicked() {
        if (currentOperator != null) {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_type, false).build();
            View typeDialogView = dialog.getCustomView();
            if (typeDialogView != null) {
                typeDialogView.findViewById(R.id.close_view4).setOnClickListener(v1 -> dialog.dismiss());
                RecyclerViewAmount = typeDialogView.findViewById(R.id.rv_type);
                RecyclerViewAmount.setAdapter(new AdapterChargeType(chargeTypesList, chargeTypeSelectedIndex != -1 ? chargeTypeSelectedIndex : chargeTypeDefaultIndex));
                RecyclerViewAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

                typeDialogView.findViewById(R.id.btn_dialog4).setOnClickListener(v16 -> {
                    AdapterChargeType adapterChargeType = (AdapterChargeType) RecyclerViewAmount.getAdapter();
                    if (adapterChargeType != null) {
                        if (adapterChargeType.getSelectedPosition() == -1) {
                            return;
                        }
                        chargeTypeSelectedIndex = chargeTypeDefaultIndex = adapterChargeType.getSelectedPosition();
                        buttonChargeType.setText(chargeTypesList.get(chargeTypeDefaultIndex).getTitle());
                        dialog.dismiss();
                    }
                });
            }
            dialog.show();
        } else {
            buttonChargeType.setClickable(false);
        }
    }

    private void saveChargeNumberInHistory(String chargeType, String phoneNumber, int price, String operator) {
        if (isSelectedFromHistory)
            return;
        MaterialDialog dialog = new MaterialDialog.Builder(getContext()).title(R.string.save_purchase)
                .titleGravity(GravityEnum.START).negativeText(R.string.cansel)
                .positiveText(R.string.ok)
                .onNegative((dialog1, which) -> dialog1.dismiss()).show();
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            if (chargeType != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("phone_number", phoneNumber);
                jsonObject.addProperty("charge_type", chargeType);
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
            }
            dialog.dismiss();
        });
    }

    public void setDialogBackground(View view) {
        if (G.themeColor == Theme.DARK) {
            view.setBackground(getContext().getResources().getDrawable(R.drawable.search_contact_background));
        }
    }

    public boolean isNumeric(String strNum) {
        try {
            long d = Long.parseLong(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    private void clearAmountAndType() {
        chargeTypeDefaultIndex = 0;
        chargeTypeSelectedIndex = -1;
        amountSelectedIndex = -1;
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

    private void setPhoneNumberEditText(String phone) {
        phone = phone.replace("+", "");
        if (phone.contains("+") && !phone.contains("+98")) {
            showError(getResources().getString(R.string.phone_number_is_not_valid));
            return;
        }
        if (phone.startsWith("98")) {
            phone = "0".concat(phone.substring(2));
        }
        editTextNumber.setText(phone.replace("+98", "0")
                .replace(" ", "")
                .replace("-", ""));
    }

    private boolean isNumberFromIran(String phoneNumber) {
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

    private void onSaveBtnClicked() {
        if (editTextNumber.getText() == null)
            return;
        String phoneNumber = editTextNumber.getText().toString().trim();
        if (!isNumeric(phoneNumber) || phoneNumber.length() < 11) {
            editTextNumber.setError(getString(R.string.phone_number_is_not_valid));
            return;
        }
        if (editTextNumber.getText() != null && (editTextNumber.getText().toString().equals("") || !isNumberFromIran(editTextNumber.getText().toString()))) {
            editTextNumber.setError(getString(R.string.phone_number_is_not_valid));
            return;
        }
        if (editTextNumber.getText() != null && isNumeric(editTextNumber.getText().toString()) && editTextNumber.getText().length() == 11) {
            if (currentOperator != null) {
                if (chargeTypeDefaultIndex != -1) {
                    if (currentAmount != null) {
                        String chooseChargeType = chargeTypesList.get(chargeTypeDefaultIndex).getKey();
                        long price = currentAmount.getAmount();
                        sendRequestCharge(currentOperator.getKey(), chooseChargeType, editTextNumber.getText().toString(), userNumber != null ? userNumber : phoneNumber, (int) price);

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

    private void sendRequestCharge(String operator, String chargeType, String rechargeableNumber, String chargerNumber, int price) {
        progressBar.setVisibility(View.VISIBLE);
        buttonEnter.setEnabled(false);
        chargeApi.topUpPurchase(operator, chargeType, rechargeableNumber, chargerNumber, price)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new IGSingleObserver<MciPurchaseResponse>(compositeDisposable) {
                    @Override
                    public void onSuccess(MciPurchaseResponse mciPurchaseResponse) {
                        progressBar.setVisibility(View.GONE);
                        if (getActivity() != null && mciPurchaseResponse.getToken() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.buy_charge), mciPurchaseResponse.getToken(), result -> {
                                buttonEnter.setEnabled(true);
                                if (result.isSuccess()) {
                                    saveChargeNumberInHistory(chargeType, rechargeableNumber, price, operator);
                                }
                            });
                        }
                        goBack();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        buttonEnter.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }
}