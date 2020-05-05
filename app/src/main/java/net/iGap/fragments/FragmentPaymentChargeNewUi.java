package net.iGap.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.R;
import net.iGap.adapter.payment.AdapterChargeAmount;
import net.iGap.adapter.payment.AdapterChargeType;
import net.iGap.adapter.payment.AdapterContactNumber;
import net.iGap.adapter.payment.AdapterHistoryNumber;
import net.iGap.adapter.payment.Amount;
import net.iGap.adapter.payment.ChargeType;
import net.iGap.adapter.payment.ContactNumber;
import net.iGap.adapter.payment.HistoryNumber;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentPaymentChargeNewUi extends BaseFragment {
    private LinearLayout toolbar;
    private ConstraintLayout frameContact;
    private ConstraintLayout frameHistory;
    private RadioButton radioButtonHamrah;
    private RadioButton radioButtonIrancell;
    private RadioButton radioButtonRightel;
    private RadioGroup radioGroup;
    private AdapterChargeAmount adapterAmount;
    private AdapterChargeType adapterChargeType;
    private AdapterHistoryNumber adapterHistory;
    private AdapterContactNumber adapterContact;
    private RecyclerView rvContact;
    private RecyclerView rvHistory;
    private RecyclerView rvAmount;
    private AppCompatTextView amountTxt;
    private AppCompatTextView closeImage1, closeImage2, closeImage3, closeImage4;
    private AppCompatTextView editType;
    private AppCompatTextView chooseType;
    private AppCompatImageView ivAdd;
    private AppCompatImageView lowView;
    private MaterialButton saveBtn1, saveBtn2, saveBtn3, saveBtn4;
    private MaterialButton amountChoose;
    private MaterialButton btnChargeType;
    private MaterialButton enterBtn;
    private AppCompatEditText editTextNumber;
    private int selectedIndex;
    private ContactNumber contactNumber;
    private HistoryNumber historyNumber;
    private Amount amount;
    private ChargeType chargeTypes;
    private ScrollView scrollView;

    public static FragmentPaymentChargeNewUi newInstance() {


        Bundle args = new Bundle();

        FragmentPaymentChargeNewUi fragment = new FragmentPaymentChargeNewUi();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_payment_charge_newui, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.payment_toolbar);
        radioButtonHamrah = view.findViewById(R.id.radio_hamrahAval);
        radioButtonIrancell = view.findViewById(R.id.radio_irancell);
        radioButtonRightel = view.findViewById(R.id.radio_rightel);
        radioGroup = view.findViewById(R.id.radioGroup);
        frameContact = view.findViewById(R.id.frame_contact);
        frameHistory = view.findViewById(R.id.frame_history);
        amountChoose = view.findViewById(R.id.choose_amount);
        btnChargeType = view.findViewById(R.id.btn_charge_type);
        ivAdd = view.findViewById(R.id.add_amount);
        lowView = view.findViewById(R.id.low_amount);
        amountTxt = view.findViewById(R.id.tv_amount_btn);
        editTextNumber = view.findViewById(R.id.phoneNumberInput);
        editType = view.findViewById(R.id.iv_edit);
        chooseType = view.findViewById(R.id.tv_choose);
        enterBtn = view.findViewById(R.id.btn_next);
        scrollView = view.findViewById(R.id.scroll_payment);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            enterBtn.setBackgroundTintList(getContext().getColorStateList(R.color.background_editText));
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

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (radioButtonHamrah.isChecked()) {
                radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
            } else {
                radioButtonHamrah.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
            }
            if (radioButtonIrancell.isChecked()) {
                radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
            } else {
                radioButtonIrancell.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
            }
            if (radioButtonRightel.isChecked()) {
                radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_select));
            } else {
                radioButtonRightel.setBackground(getContext().getResources().getDrawable(R.drawable.shape_topup_diselect));
            }
        });

        OnClickedButtons();
    }

    public void OnClickedButtons() {

        frameContact.setOnClickListener(v -> {
            adapterContact = new AdapterContactNumber();
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
                contactNumber = adapterContact.getAmountList().get(selectedIndex);
                editTextNumber.setText(contactNumber.getContactNumber());
                dialog.dismiss();
            });
            dialog.show();

            closeImage1.setOnClickListener(v12 -> dialog.dismiss());

            rvContact.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvContact.setAdapter(adapterContact);
        });

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

        amountChoose.setOnClickListener(v -> {

            adapterAmount = new AdapterChargeAmount();
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_amount, false).build();
            View view = dialog.getCustomView();
            rvAmount = view.findViewById(R.id.rv_amount);
            closeImage3 = view.findViewById(R.id.iv_close3);
            saveBtn3 = view.findViewById(R.id.btn_dialog3);

            saveBtn3.setOnClickListener(v1 -> {
                scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));

                if (adapterAmount.getSelectedPosition() == -1) {
                    return;
                }

                selectedIndex = adapterAmount.getSelectedPosition();
                amount = adapterAmount.getAmountList().get(selectedIndex);
                amountTxt.setText(amount.getTextAmount());

                ivAdd.setVisibility(View.VISIBLE);
                lowView.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    amountChoose.setBackgroundTintList(getContext().getColorStateList(R.color.background_editText));
                    btnChargeType.setBackgroundTintList(getContext().getColorStateList(R.color.green));
                }
                chooseType.setTextColor(getContext().getResources().getColor(R.color.white));
                amountChoose.setClickable(false);
                dialog.dismiss();
            });

            closeImage3.setOnClickListener(v12 -> dialog.dismiss());

            ViewGroup.LayoutParams params = rvAmount.getLayoutParams();
            params.height = 500;
            rvAmount.setLayoutParams(params);
            rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvAmount.setAdapter(adapterAmount);
            dialog.show();
        });

        ivAdd.setOnClickListener(v -> {
            if (selectedIndex < adapterAmount.getAmountList().size()) {
                amount = adapterAmount.getAmountList().get(selectedIndex = selectedIndex + 1);
                amountTxt.setText(amount.getTextAmount());
            }
        });


        lowView.setOnClickListener(v -> {
            if (selectedIndex < adapterAmount.getAmountList().size()) {
                amount = adapterAmount.getAmountList().get(selectedIndex = selectedIndex - 1);
                amountTxt.setText(amount.getTextAmount());
            }
        });

        btnChargeType.setOnClickListener(v -> {
            adapterChargeType = new AdapterChargeType();
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_type, false).build();

            View view = dialog.getCustomView();
            rvAmount = view.findViewById(R.id.rv_type);
            closeImage4 = view.findViewById(R.id.iv_close4);
            saveBtn4 = view.findViewById(R.id.btn_dialog4);

            saveBtn4.setOnClickListener(v16 -> {
                if (adapterChargeType.getSelectedPosition() == -1) {
                    return;
                }

                selectedIndex = adapterChargeType.getSelectedPosition();
                chargeTypes = adapterChargeType.getChargeTypes().get(selectedIndex);
                chooseType.setText(chargeTypes.getChargeType());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnChargeType.setBackgroundTintList(getContext().getColorStateList(R.color.background_editText));
                    enterBtn.setBackgroundTintList(getContext().getColorStateList(R.color.green));

                }
                chooseType.setTextColor(getContext().getResources().getColor(R.color.white));
                editType.setVisibility(View.VISIBLE);
                dialog.dismiss();

            });

            closeImage4.setOnClickListener(v12 -> dialog.dismiss());

            rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvAmount.setAdapter(adapterChargeType);
            dialog.show();
        });
    }
}
