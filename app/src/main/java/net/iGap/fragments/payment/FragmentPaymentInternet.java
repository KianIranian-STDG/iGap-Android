package net.iGap.fragments.payment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
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
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.OperatorType;
import net.iGap.observers.interfaces.ToolbarListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentPaymentInternet extends BaseFragment {

    private LinearLayout toolbar;
    private ConstraintLayout frameContact;
    private ConstraintLayout frameHistory;
    private RadioButton radioButtonHamrah;
    private RadioButton radioButtonIrancell;
    private RadioButton radioButtonRightel;
    private RadioGroup radioGroup;
    private AdapterHistoryNumber adapterHistory;
    private AdapterContactNumber adapterContact;
    private RecyclerView rvContact;
    private RecyclerView rvHistory;
    private AppCompatTextView closeImage1, closeImage2;
    private MaterialButton enterBtn;
    private MaterialButton saveBtn1;
    private MaterialButton saveBtn2;
    private AppCompatEditText editTextNumber;
    private int selectedIndex;
    private ContactNumber contactNumber;
    private HistoryNumber historyNumber;
    private ScrollView scrollView;
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
        radioGroup = view.findViewById(R.id.radioGroup);
        frameContact = view.findViewById(R.id.frame_contact);
        frameHistory = view.findViewById(R.id.frame_history);
        editTextNumber = view.findViewById(R.id.phoneNumberInput);
        enterBtn = view.findViewById(R.id.btn_next);
        scrollView = view.findViewById(R.id.scroll_payment);


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

        enterBtn.setOnClickListener(v -> {
            new HelperFragment(getActivity().getSupportFragmentManager(), FragmentPaymentInternetPackage.newInstance()).setReplace(false).load();

        });
        onContactNumberButtonClick();
        onHistoryNumberButtonClick();
        onPhoneNumberInputClick();
        onItemOperatorSelect();
    }

    private void onContactNumberButtonClick() {
        frameContact.setOnClickListener(v -> {
            adapterContact = new AdapterContactNumber(contactNumberList);
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_contact, true).build();
            View view = dialog.getCustomView();
            closeImage1 = view.findViewById(R.id.iv_close1);
            rvContact = view.findViewById(R.id.rv_contact);
            saveBtn1 = view.findViewById(R.id.btn_dialog1);

            saveBtn1.setOnClickListener(v15 -> {
                if (adapterContact.getSelectedPosition() == -1) {
                    return;
                }
                selectedIndex = adapterContact.getSelectedPosition();
                contactNumber = adapterContact.getContactNumbers().get(selectedIndex);
                editTextNumber.setText(contactNumber.getPhone());
                dialog.dismiss();
            });
            dialog.show();

            closeImage1.setOnClickListener(v12 -> dialog.dismiss());

            rvContact.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvContact.setAdapter(adapterContact);
        });
    }

    private void onHistoryNumberButtonClick() {
        frameHistory.setOnClickListener(v -> {
            adapterHistory = new AdapterHistoryNumber();
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_history, false).build();
            View view = dialog.getCustomView();
            closeImage2 = view.findViewById(R.id.iv_close2);
            rvHistory = view.findViewById(R.id.rv_history);
            saveBtn2 = view.findViewById(R.id.btn_dialog2);

            closeImage2.setOnClickListener(v12 -> dialog.dismiss());

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
            switch (id) {
                case R.id.radio_hamrahAval:
                    radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
                    radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                    radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                    break;
                case R.id.radio_irancell:
                    radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
                    radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                    radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                    break;
                case R.id.radio_rightel:
                    radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
                    radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                    radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
                    break;
            }
        });

    }


}
