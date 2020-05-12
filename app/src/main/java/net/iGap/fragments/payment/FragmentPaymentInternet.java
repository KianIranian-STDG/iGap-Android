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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.R;
import net.iGap.adapter.payment.AdapterContactNumber;
import net.iGap.adapter.payment.AdapterHistoryNumber;
import net.iGap.adapter.payment.ContactNumber;
import net.iGap.adapter.payment.HistoryNumber;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.OperatorType;
import net.iGap.module.Contacts;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmRecentChargeNumber;
import net.iGap.realm.RealmRecentChargeNumberFields;


import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class FragmentPaymentInternet extends BaseFragment {

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
    private HistoryNumber historyNumber;
    private View closeView, closeView2;
    private OperatorType.Type operatorType;

    List<ContactNumber> contactNumberList = new ArrayList<>();

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


        onContactNumberButtonClick();
        onHistoryNumberButtonClick();
        onPhoneNumberInputClick();
        onItemOperatorSelect();

        enterBtn.setOnClickListener(v -> {
            if (operatorType != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentPaymentInternetPackage()).setReplace(false).load();
            } else {
                ShowError(getContext().getResources().getString(R.string.phone_number_is_not_valid));
            }

        });
    }

    public void ShowError(String errorMessage) {
        if (errorMessage != null) {
            hideKeyboard();
            HelperError.showSnackMessage(errorMessage, false);
        }
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
                return realm.where(RealmRecentChargeNumber.class).equalTo(RealmRecentChargeNumberFields.TYPE, 1).findAll();
            });

            if (numbers == null || numbers.size() == 0) {
                ShowError(getContext().getResources().getString(R.string.phone_number_is_not_valid));
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
                        switch (opt) {
                            case HAMRAH_AVAL:
                                operatorType = OperatorType.Type.HAMRAH_AVAL;
                                setSelectedOperator(radioButtonHamrah, radioButtonIrancell, radioButtonRightel, frameHamrah, frameIrancel, frameRightel);
                                break;
                            case IRANCELL:
                                operatorType = OperatorType.Type.IRANCELL;
                                setSelectedOperator(radioButtonIrancell, radioButtonHamrah, radioButtonRightel, frameIrancel, frameHamrah, frameRightel);
                                break;
                            case RITEL:
                                operatorType = OperatorType.Type.RITEL;
                                setSelectedOperator(radioButtonRightel, radioButtonIrancell, radioButtonHamrah, frameRightel, frameIrancel, frameHamrah);
                                break;
                        }
                    }

                }
            }
        });
    }


    private void onItemOperatorSelect() {
        if (editTextNumber.getText() != null) {
            frameHamrah.setOnClickListener(v -> {
                operatorType = OperatorType.Type.HAMRAH_AVAL;
                setSelectedOperator(radioButtonHamrah, radioButtonIrancell, radioButtonRightel, frameHamrah, frameIrancel, frameRightel);
            });

            frameRightel.setOnClickListener(v -> {
                operatorType = OperatorType.Type.RITEL;
                setSelectedOperator(radioButtonRightel, radioButtonIrancell, radioButtonHamrah, frameRightel, frameIrancel, frameHamrah);
            });

            frameIrancel.setOnClickListener(v -> {
                operatorType = OperatorType.Type.IRANCELL;
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
