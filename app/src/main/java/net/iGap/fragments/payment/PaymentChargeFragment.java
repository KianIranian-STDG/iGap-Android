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
import android.widget.LinearLayout;
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
import net.iGap.adapter.OperatorRecyclerAdapter;
import net.iGap.adapter.payment.AdapterChargeAmount;
import net.iGap.adapter.payment.AdapterChargeType;
import net.iGap.adapter.payment.Amount;
import net.iGap.adapter.payment.ChargeContactNumberAdapter;
import net.iGap.adapter.payment.ChargeHistoryNumberAdapter;
import net.iGap.adapter.payment.ChargeType;
import net.iGap.adapter.payment.ContactNumber;
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
import net.iGap.model.paymentPackage.FaceValue;
import net.iGap.model.paymentPackage.FavoriteNumber;
import net.iGap.model.paymentPackage.GetFavoriteNumber;
import net.iGap.model.paymentPackage.MciPurchaseResponse;
import net.iGap.model.paymentPackage.Operator;
import net.iGap.model.paymentPackage.TopupChargeType;
import net.iGap.module.CircleImageView;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.repository.PaymentRepository;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.model.OperatorType.Type.IRANCELL;
import static net.iGap.model.OperatorType.Type.RITEL;

public class PaymentChargeFragment extends BaseFragment {
    public static final String MCI = "mci";
    public static final String MTN = "mtn";
    public static final String RIGHTEL = "rightel";
    private final List<ChargeType> chargeTypeList = new ArrayList<>();
    private final List<Amount> mciFaceValues = new ArrayList<>();
    private final List<Amount> mtnFaceValues = new ArrayList<>();
    private final List<Amount> rightelFaceValues = new ArrayList<>();
    private final List<Operator> operators = new ArrayList<>();
    private View frameContact;
    private View frameHistory;
    private RecyclerView buyHistoryRecyclerView;
    private RecyclerView amountRecyclerView;
    private AppCompatTextView removeNumber;
    private MaterialButton chosePriceTextView;
    private MaterialButton chargeTypeButton;
    private MaterialButton enterBtn;
    private AppCompatEditText editTextNumber;
    private ContactNumber contactNumber;
    private FavoriteNumber historyNumber;
    private LinearLayout toolbar;
    private AppCompatImageView amountPlusImageView;
    private AppCompatImageView amountMinesImageView;
    private ScrollView scrollView;
    private Amount currentAmount;
    private ChargeType typeList;
    private FrameLayout progressBar;
    private LinearLayout linearWarning;
    private ChargeApi chargeApi;
    private OperatorType.Type currentOperator;
    private RecyclerView lstOperator;
    private int selectedChargeTypeIndex;
    private CompositeDisposable compositeDisposable;
    private TextWatcher textWatcher;
    private OperatorRecyclerAdapter operatorRecyclerAdapter;
    private int mciAmountDefaultIndex;
    private int mtnAmountDefaultIndex;
    private int contactPositionClicked = -1;
    private int historyItemClicked = -1;
    private boolean isSelectedFromHistory = false;
    private PaymentRepository paymentRepository;
    private List<TopupChargeType> mciTopupChargeTypes = new ArrayList<>();
    private List<TopupChargeType> mtnTopupChargeTypes = new ArrayList<>();
    private List<TopupChargeType> rightelTopupChargeTypes = new ArrayList<>();
    private int rightelAmountDefaultIndex;
    private String userNumber;
    private long userId;
    private String phoneNumber;
    private long peerId;
    private CircleImageView avatar;

    public static PaymentChargeFragment newInstance() {
        return new PaymentChargeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_payment_charge_newui, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            phoneNumber = bundle.getString("phoneNumber", "");
            peerId = bundle.getLong("peerId", 0);
        }
        if (getContext() == null)
            return;
        compositeDisposable = new CompositeDisposable();
        chargeApi = new RetrofitFactory().getChargeRetrofit();
        toolbar = view.findViewById(R.id.payment_toolbar);
        scrollView = view.findViewById(R.id.scroll_payment);
        frameContact = view.findViewById(R.id.frame_contact);
        frameHistory = view.findViewById(R.id.frame_history);
        chosePriceTextView = view.findViewById(R.id.choose_amount);
        chargeTypeButton = view.findViewById(R.id.btn_charge_type);
        amountPlusImageView = view.findViewById(R.id.add_amount);
        amountMinesImageView = view.findViewById(R.id.low_amount);
        editTextNumber = view.findViewById(R.id.phoneNumberInput);
        enterBtn = view.findViewById(R.id.btn_pay);
        progressBar = view.findViewById(R.id.loadingView);
        linearWarning = view.findViewById(R.id.llWarning);
        removeNumber = view.findViewById(R.id.btnRemoveSearch);
        lstOperator = view.findViewById(R.id.lstOperator);
        avatar = view.findViewById(R.id.avatar);
        editTextNumber.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
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
        if (G.themeColor == Theme.DARK) {
            frameHistory.setBackground(getContext().getResources().getDrawable(R.drawable.shape_payment_charge_dark));
            frameContact.setBackground(getContext().getResources().getDrawable(R.drawable.shape_payment_charge_dark));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        lstOperator.setLayoutManager(new LinearLayoutManager(getActivity()));
        lstOperator.setLayoutManager(layoutManager);
        String userToken = TokenContainer.getInstance().getToken();
        paymentRepository = PaymentRepository.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        paymentRepository.getConfigs(userToken, new PaymentRepository.ReceiveData() {
            @Override
            public void onReceiveData(Config config) {
                if (config != null) {
                    for (ConfigData configData : config.getData()) {
                        operators.add(configData.getOperator());
                        if (configData.getOperator().getKey().contentEquals(MCI)) {
                            for (FaceValue faceValue : configData.getFaceValues()) {
                                mciFaceValues.add(new Amount(faceValue.getKey()));
                                if (faceValue.getSelected())
                                    mciAmountDefaultIndex = mciFaceValues.size() - 1;
                            }
                            mciTopupChargeTypes = configData.getTopupChargeTypes();
                        }
                        if (configData.getOperator().getKey().contentEquals(MTN)) {
                            for (FaceValue faceValue : configData.getFaceValues()) {
                                mtnFaceValues.add(new Amount(faceValue.getKey()));
                                if (faceValue.getSelected())
                                    mtnAmountDefaultIndex = mtnFaceValues.size() - 1;
                            }
                            mtnTopupChargeTypes = configData.getTopupChargeTypes();
                        }
                        if (configData.getOperator().getKey().contentEquals(RIGHTEL)) {
                            for (FaceValue faceValue : configData.getFaceValues()) {
                                rightelFaceValues.add(new Amount(faceValue.getKey()));
                                if (faceValue.getSelected())
                                    rightelAmountDefaultIndex = rightelFaceValues.size() - 1;
                            }
                            rightelTopupChargeTypes = configData.getTopupChargeTypes();
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
                selectedChargeTypeIndex = 0;
                switch (operatorName) {
                    case MCI:
                        currentAmount = mciFaceValues.get(mciAmountDefaultIndex);
                        currentOperator = OperatorType.Type.HAMRAH_AVAL;
                        chosePriceTextView.setText(currentAmount.getTextAmount());
                        break;
                    case MTN:
                        currentAmount = mtnFaceValues.get(mtnAmountDefaultIndex);
                        currentOperator = OperatorType.Type.IRANCELL;
                        chosePriceTextView.setText(currentAmount.getTextAmount());
                        break;
                    case RIGHTEL:
                        currentAmount = rightelFaceValues.get(rightelAmountDefaultIndex);
                        currentOperator = OperatorType.Type.RITEL;
                        chosePriceTextView.setText(currentAmount.getTextAmount());
                        break;
                }
                setAdapterValue(currentOperator);
            }
        });
        lstOperator.setAdapter(operatorRecyclerAdapter);
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).findFirst();
            if (userInfo != null && editTextNumber.getText() != null) {
                userId = userInfo.getId();
                userNumber = userInfo.getPhoneNumber();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    setPhoneNumberEditText(phoneNumber);
                    if (peerId != 0) {
                        avatarHandler.getAvatar(new ParamWithAvatarType(avatar, peerId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
                    }
                } else {
                    setPhoneNumberEditText(userNumber);
                    avatar.setVisibility(View.GONE);
                }
                if (editTextNumber.getText() != null && editTextNumber.getText().length() == 11) {
                    String number = editTextNumber.getText().toString().substring(0, 4);
                    OperatorType.Type operator = new OperatorType().getOperation(number);
                    if (operator != null) {
                        changeOperator(operator);
                    }
                }
            }
        });
        chargeTypeButton.setOnClickListener(v -> {
            if (currentOperator != null) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_type, false).build();
                View typeDialogView = dialog.getCustomView();
                if (typeDialogView != null) {
                    typeDialogView.findViewById(R.id.close_view4).setOnClickListener(v1 -> dialog.dismiss());
                    amountRecyclerView = typeDialogView.findViewById(R.id.rv_type);
                    amountRecyclerView.setAdapter(new AdapterChargeType(chargeTypeList));
                    amountRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

                    typeDialogView.findViewById(R.id.btn_dialog4).setOnClickListener(v16 -> {
                        AdapterChargeType adapterChargeType = (AdapterChargeType) amountRecyclerView.getAdapter();
                        if (adapterChargeType != null) {
                            if (adapterChargeType.getSelectedPosition() == -1) {
                                return;
                            }

                            typeList = chargeTypeList.get(selectedChargeTypeIndex = adapterChargeType.getSelectedPosition());
                            chargeTypeButton.setText(typeList.getChargeType());
                            dialog.dismiss();
                        }
                    });
                }
                dialog.show();
            } else {
                chargeTypeButton.setClickable(false);
            }
        });
        frameContact.setOnClickListener(v -> {
            hideKeyboard();
            progressBar.setVisibility(View.VISIBLE);
            try {
                HelperPermission.getContactPermision(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() {
                        ChargeContactNumberAdapter adapter = new ChargeContactNumberAdapter();
                        frameContact.setEnabled(false);
                        PhoneContactProvider.getInstance().getAllPhoneContactForPayment(contactNumbers -> {
                            if (getContext() == null) {
                                return;
                            }
                            frameContact.setEnabled(true);
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
        });
        frameHistory.setOnClickListener(v -> {
            hideKeyboard();
            progressBar.setVisibility(View.VISIBLE);
            frameHistory.setEnabled(false);
            chargeApi.getFavoriteChargeNUmber().enqueue(new Callback<GetFavoriteNumber>() {
                @Override
                public void onResponse(@NonNull Call<GetFavoriteNumber> call, @NonNull Response<GetFavoriteNumber> response) {
                    progressBar.setVisibility(View.GONE);
                    frameHistory.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        if (response.body().getData().size() > 0) {
                            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_history, false).build();
                            View historyDialogView = dialog.getCustomView();
                            if (historyDialogView != null) {
                                buyHistoryRecyclerView = historyDialogView.findViewById(R.id.rv_history);
                                setDialogBackground(buyHistoryRecyclerView);
                                buyHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                                ChargeHistoryNumberAdapter adapter = new ChargeHistoryNumberAdapter(response.body().getData());
                                buyHistoryRecyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener(position -> {
                                    historyItemClicked = position;
                                    onHistoryItemClicked();
                                    toggleHistorySelected(true);
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
                    frameHistory.setEnabled(true);
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
                avatar.setVisibility(View.GONE);
                if (editTextNumber.getText() != null && editTextNumber.getText().length() > 0) {
                    removeNumber.setVisibility(View.VISIBLE);
                    if (editTextNumber.getText().length() == 10 && editTextNumber.getText().charAt(0) != '0')
                        editTextNumber.setText("0".concat(editTextNumber.getText().toString()));
                    if (editTextNumber.getText().length() == 11 || editTextNumber.getText().length() == 4) {
                        if (editTextNumber.getText().length() == 11) {
                            closeKeyboard(editTextNumber);
                            linearWarning.setVisibility(View.VISIBLE);
                        }
                        String number = editTextNumber.getText().toString().substring(0, 4);
                        OperatorType.Type operator = new OperatorType().getOperation(number);
                        if (operator != null) {
                            changeOperator(operator);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                toggleHistorySelected(false);
            }
        });
        removeNumber.setOnClickListener(view1 -> {
            avatar.setVisibility(View.GONE);
            toggleHistorySelected(false);
            editTextNumber.setText("");
            removeNumber.setVisibility(View.INVISIBLE);
            if (editTextNumber.getText() != null && editTextNumber.getText().toString().equals("")) {
                changeOperator(OperatorType.Type.UNKNOWN);
            }
            openKeyBoard();
        });
        enterBtn.setOnClickListener(v -> onSaveBtnClicked());
        chosePriceTextView.setOnClickListener(v -> {
            chosePriceClicked();
        });
        amountPlusImageView.setOnClickListener(v -> {
            toggleHistorySelected(false);
            currentAmount = new Amount(currentAmount, true);
            chosePriceTextView.setText(currentAmount.getTextAmount());
            historyNumber = null;
        });
        amountMinesImageView.setOnClickListener(v -> {
            if (currentAmount.getAmount() != 10000) {
                toggleHistorySelected(false);
                currentAmount = new Amount(currentAmount, false);
                chosePriceTextView.setText(currentAmount.getTextAmount());
                historyNumber = null;
            }
        });
        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void toggleHistorySelected(boolean isSelectedFromHistory) {
        this.isSelectedFromHistory = isSelectedFromHistory;
    }

    private void onHistoryItemClicked() {
        ChargeHistoryNumberAdapter adapterHistory = (ChargeHistoryNumberAdapter) buyHistoryRecyclerView.getAdapter();
        if (adapterHistory != null) {
            if (historyItemClicked == -1) {
                return;
            }
            historyNumber = adapterHistory.getHistoryNumberList().get(historyItemClicked);
            updatePaymentConfig(historyNumber);
            currentAmount = new Amount(historyNumber.getAmount());
            setPhoneNumberEditText(historyNumber.getPhoneNumber());
            chosePriceTextView.setText(currentAmount.getTextAmount());
            setChargeType(historyNumber.getChargeType());//can write better code with use key value container
        }
    }

    private void onContactItemClicked(ChargeContactNumberAdapter adapter) {
        if (contactPositionClicked == -1) {
            return;
        }
        contactNumber = adapter.getContactNumbers().get(contactPositionClicked);
        setPhoneNumberEditText(contactNumber.getPhone());
        clearAmountAndType();
        if (editTextNumber.getText() != null && editTextNumber.getText().length() == 11) {
            OperatorType.Type opt = new OperatorType().getOperation(editTextNumber.getText().toString().substring(0, 4));
            if (opt != null) {
                changeOperator(opt);
            }
        } else {
            showError(getResources().getString(R.string.ivnalid_data_provided));
        }
    }

    private void updatePaymentConfig(FavoriteNumber historyNumber) {
        changeOperator(findOperatorType(historyNumber.getOperator()));
        selectedChargeTypeIndex = findChargeType(historyNumber.getChargeType());
    }

    private int findChargeType(String chargeType) {
        switch (currentOperator) {
            case HAMRAH_AVAL:
                switch (chargeType) {
                    case "DIRECT":
                        return 0;
                    case "YOUTH":
                        return 1;
                    case "LADIES":
                        return 2;
                }
                break;
            case IRANCELL:
                switch (chargeType) {
                    case "MTN_NORMAL":
                        return 0;
                    case "MTN_AMAZING":
                        return 1;
                }
                break;
            case RITEL:
                switch (chargeType) {
                    case "RIGHTEL_NORMAL":
                        return 0;
                    case "RIGHTEL_EXCITING":
                        return 1;
                }
        }
        return 0;
    }

    private OperatorType.Type findOperatorType(String operator) {
        if (operator.equals(MCI))
            return OperatorType.Type.HAMRAH_AVAL;
        else if (operator.equals(MTN))
            return IRANCELL;
        else
            return RITEL;
    }

    private void setChargeType(String chargeType) {
        if (chargeType.equals(ChooseChargeType.MTN_NORMAL.toString()) || chargeType.equals(ChooseChargeType.RIGHTEL_NORMAL.toString()) || chargeType.equals(ChooseChargeType.DIRECT.toString())) {
            chargeTypeButton.setText(getResources().getString(R.string.normal_charge));
        } else if (chargeType.equals(ChooseChargeType.MTN_AMAZING.toString())) {
            chargeTypeButton.setText(getResources().getString(R.string.amazing));
        } else if (chargeType.equals(ChooseChargeType.RIGHTEL_EXCITING.toString())) {
            chargeTypeButton.setText(getResources().getString(R.string.Exciting));
        } else if (chargeType.equals(ChooseChargeType.AMAZING.toString())) {
            chargeTypeButton.setText(getResources().getString(R.string.amazing));
        } else if (chargeType.equals(ChooseChargeType.YOUTH.toString())) {
            chargeTypeButton.setText(getResources().getString(R.string.youth_charge));
        } else if (chargeType.equals(ChooseChargeType.LADIES.toString())) {
            chargeTypeButton.setText(getResources().getString(R.string.ladies_charge));
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

    private void chosePriceClicked() {
        if (currentOperator != null) {
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_amount, false).build();
            View amountDialogView = dialog.getCustomView();
            if (amountDialogView != null) {
                amountRecyclerView = amountDialogView.findViewById(R.id.rv_amount);
                amountRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                String operatorName = currentOperator.name();
                switch (operatorName) {
                    case "HAMRAH_AVAL":
                        amountRecyclerView.setAdapter(new AdapterChargeAmount(mciFaceValues, mciAmountDefaultIndex));
                        break;
                    case "IRANCELL":
                        amountRecyclerView.setAdapter(new AdapterChargeAmount(mtnFaceValues, mtnAmountDefaultIndex));
                        break;
                    case "RITEL":
                        amountRecyclerView.setAdapter(new AdapterChargeAmount(rightelFaceValues, rightelAmountDefaultIndex));
                        break;
                }
                amountDialogView.findViewById(R.id.close_view3).setOnClickListener(v12 -> dialog.dismiss());
                amountDialogView.findViewById(R.id.btn_dialog3).setOnClickListener(v1 -> {
                    AdapterChargeAmount adapterAmount = (AdapterChargeAmount) amountRecyclerView.getAdapter();
                    if (adapterAmount != null) {
                        if (adapterAmount.getSelectedPosition() == -1) {
                            return;
                        }
                        switch (operatorName) {
                            case "HAMRAH_AVAL":
                                currentAmount = mciFaceValues.get(adapterAmount.getSelectedPosition());
                                break;
                            case "IRANCELL":
                                currentAmount = mtnFaceValues.get(adapterAmount.getSelectedPosition());
                                break;
                            case "RITEL":
                                currentAmount = rightelFaceValues.get(adapterAmount.getSelectedPosition());
                                break;
                        }
                        chosePriceTextView.setText(currentAmount.getTextAmount());
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
        if (currentOperator == operator)
            return;
        clearAmountAndType();
        currentOperator = operator;
        String operatorName = currentOperator.name();
        switch (operatorName) {
            case "HAMRAH_AVAL":
                operatorRecyclerAdapter.setCheckedRadioButton(MCI);
                currentAmount = mciFaceValues.get(mciAmountDefaultIndex);
                currentOperator = OperatorType.Type.HAMRAH_AVAL;
                chosePriceTextView.setText(currentAmount.getTextAmount());
                break;
            case "IRANCELL":
                operatorRecyclerAdapter.setCheckedRadioButton(MTN);
                currentAmount = mtnFaceValues.get(mtnAmountDefaultIndex);
                currentOperator = OperatorType.Type.IRANCELL;
                chosePriceTextView.setText(currentAmount.getTextAmount());
                break;
            case "RITEL":
                operatorRecyclerAdapter.setCheckedRadioButton(RIGHTEL);
                currentAmount = rightelFaceValues.get(rightelAmountDefaultIndex);
                currentOperator = OperatorType.Type.RITEL;
                chosePriceTextView.setText(currentAmount.getTextAmount());
                break;
            case "UNKNOWN":
                break;
        }
        setAdapterValue(operator);
    }

    private void clearAmountAndType() {
        selectedChargeTypeIndex = 0;
    }

    private void setAdapterValue(@NotNull OperatorType.Type operator) {
        chargeTypeList.clear();
        switch (operator) {
            case HAMRAH_AVAL:
                for (TopupChargeType topupChargeType : mciTopupChargeTypes) {
                    chargeTypeList.add(new ChargeType(topupChargeType.getTitle()));
                }
                break;
            case IRANCELL:
                for (TopupChargeType topupChargeType : mtnTopupChargeTypes) {
                    chargeTypeList.add(new ChargeType(topupChargeType.getTitle()));
                }
                break;
            case RITEL:
                for (TopupChargeType topupChargeType : rightelTopupChargeTypes) {
                    chargeTypeList.add(new ChargeType(topupChargeType.getTitle()));
                }
                break;
        }
        if (chargeTypeList.size() > 0) {
            typeList = chargeTypeList.get(0);
            chargeTypeButton.setText(typeList.getChargeType());
        }
        progressBar.setVisibility(View.GONE);
    }

    private void
    onSaveBtnClicked() {
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
                if (selectedChargeTypeIndex != -1) {
                    if (currentAmount != null) {
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
                        long price = currentAmount.getAmount();
                        if (currentOperator == OperatorType.Type.HAMRAH_AVAL) {
                            sendRequestCharge(MCI, chooseChargeType, editTextNumber.getText().toString().substring(1), (int) price);
                        } else if (currentOperator == IRANCELL) {
                            sendRequestCharge(MTN, chooseChargeType, editTextNumber.getText().toString().substring(1), (int) price);
                        } else if (currentOperator == RITEL) {
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

    public boolean isNumeric(String strNum) {
        try {
            long d = Long.parseLong(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    private void sendRequestCharge(String operator, ChooseChargeType chargeType, String phoneNumber, int price) {
        progressBar.setVisibility(View.VISIBLE);
        enterBtn.setEnabled(false);
        chargeApi.topUpPurchase(operator, chargeType != null ? chargeType.name() : null, phoneNumber, price)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new IGSingleObserver<MciPurchaseResponse>(compositeDisposable) {
                    @Override
                    public void onSuccess(MciPurchaseResponse mciPurchaseResponse) {
                        progressBar.setVisibility(View.GONE);
                        if (getActivity() != null && mciPurchaseResponse.getToken() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.buy_charge), mciPurchaseResponse.getToken(), result -> {
                                enterBtn.setEnabled(true);
                                if (result.isSuccess()) {
                                    saveBoughtChargeInHistory(chargeType, phoneNumber, price, operator);
                                }
                            });
                        }
                        goBack();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        enterBtn.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void saveBoughtChargeInHistory(ChooseChargeType chargeType, String phoneNumber, int price, String operator) {
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
            }
            dialog.dismiss();
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

    private boolean isNumberFromIran(String phoneNumber) {
        if (phoneNumber.trim().charAt(0) == '0' && (new OperatorType().isValidType(phoneNumber.substring(0, 4)) || new OperatorType().isValidType(phoneNumber.substring(0, 5))))
            return true;
        String standardize = phoneNumber.replace("+98", "0")
                .replace(" ", "")
                .replace("-", "");

        return new OperatorType().isValidType(standardize.substring(0, 4));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    public void setDialogBackground(View view) {
        if (G.themeColor == Theme.DARK) {
            view.setBackground(getContext().getResources().getDrawable(R.drawable.search_contact_background));
        }
    }

    enum ChooseChargeType {
        MTN_NORMAL, MTN_AMAZING, RIGHTEL_NORMAL, RIGHTEL_EXCITING, DIRECT, AMAZING, YOUTH, LADIES
    }
}
