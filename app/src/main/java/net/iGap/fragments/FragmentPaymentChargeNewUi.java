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
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentPaymentChargeNewUi extends BaseFragment {
    private LinearLayout toolbar;
    private ConstraintLayout frameContact;
    private ConstraintLayout frameHistory;
    private ConstraintLayout contactFrame;
    private ConstraintLayout historyFrame;
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
    private AppCompatTextView closeImage;
    private AppCompatTextView chooseNumber;
    private AppCompatImageView addView;
    private AppCompatImageView lowView;
    private MaterialButton saveBtn;
    private MaterialButton amountChoose;
    private MaterialButton chargeType;
    private MaterialButton saveAmount;
    private AppCompatEditText editTextNumber;
    private int selectedIndex;
    private Amount selectedAmount;
    private List<Amount> amountList = new ArrayList<>();

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
        chargeType = view.findViewById(R.id.btn_charge_type);
        addView = view.findViewById(R.id.add_amount);
        lowView = view.findViewById(R.id.low_amount);
        amountTxt = view.findViewById(R.id.tv_amount_btn);
        editTextNumber = view.findViewById(R.id.phoneNumberInput);


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
            adapterContact = new AdapterContactNumber(amountList);
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_number, true).build();
            View view1 = dialog.getCustomView();
            closeImage = view1.findViewById(R.id.close_iv);
            chooseNumber = view1.findViewById(R.id.choose_number);
            contactFrame = view1.findViewById(R.id.contact_frame);
            historyFrame = view1.findViewById(R.id.history_frame);
            rvContact = view1.findViewById(R.id.rv_contact);
            saveBtn = view1.findViewById(R.id.btn_saveContact);

            saveBtn.setOnClickListener(v15 -> {

                if (adapterContact.getSelectedPosition() == -1) {
                    return;
                }

                selectedIndex = adapterContact.getSelectedPosition();
                selectedAmount = amountList.get(selectedIndex);
                editTextNumber.setText(selectedAmount.getTextAmount() );

                dialog.dismiss();
            });

            chooseNumber.setText("انتخاب از شماره های مخاطبین");
            rvContact.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvContact.setAdapter(adapterContact);
            dialog.show();

            closeImage.setOnClickListener(v12 -> dialog.dismiss());

        });

        frameHistory.setOnClickListener(v -> {
            adapterHistory = new AdapterHistoryNumber(amountList);
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_number, false).build();
            View view = dialog.getCustomView();
            closeImage = view.findViewById(R.id.iv_close);
            chooseNumber = view.findViewById(R.id.choose_number);
            contactFrame = view.findViewById(R.id.contact_frame);
            historyFrame = view.findViewById(R.id.history_frame);
            rvHistory = view.findViewById(R.id.rv_history);


            chooseNumber.setText("انتخاب از شماره های قبلی");
            contactFrame.setVisibility(View.GONE);
            historyFrame.setVisibility(View.VISIBLE);
            closeImage.setOnClickListener(v12 -> dialog.dismiss());
            rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvHistory.setAdapter(adapterContact);
            dialog.show();
        });

        amountChoose.setOnClickListener(v -> {
            adapterAmount = new AdapterChargeAmount(amountList);
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_charge, false).build();

            View view = dialog.getCustomView();
            rvAmount = view.findViewById(R.id.rv_payment);
            closeImage = view.findViewById(R.id.iv_close);
            saveAmount = view.findViewById(R.id.btn_save);

            adapterAmount.setOnItemClicked(v14 -> {
                saveAmount.setText("تایید");
            });

            saveAmount.setOnClickListener(v1 -> {
                if (adapterAmount.getSelectedPosition() == -1) {
                    return;
                }

                selectedIndex = adapterAmount.getSelectedPosition();
                selectedAmount = amountList.get(selectedIndex);
                amountTxt.setText(selectedAmount.getTextAmount());
                addView.setVisibility(View.VISIBLE);
                lowView.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    amountChoose.setBackgroundTintList(getContext().getColorStateList(R.color.background_editText));
                    chargeType.setBackgroundTintList(getContext().getColorStateList(R.color.buttonColor));
                }
                chargeType.setTextColor(getContext().getResources().getColor(R.color.white));
                amountChoose.setClickable(false);
                dialog.dismiss();
            });

            closeImage.setOnClickListener(v12 -> dialog.dismiss());

            ViewGroup.LayoutParams params = rvAmount.getLayoutParams();
            params.height = 500;
            rvAmount.setLayoutParams(params);
            rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvAmount.setAdapter(adapterAmount);
            dialog.show();
        });


        addView.setOnClickListener(v -> {
            if (selectedIndex < amountList.size()) {
                selectedAmount = amountList.get(selectedIndex = selectedIndex + 1);
                amountTxt.setText(selectedAmount.getTextAmount());
                Log.i("nazanin", "OnClickedButtons:+if ");
            }
        });


        lowView.setOnClickListener(v -> {
            if (selectedIndex < amountList.size()) {
                selectedAmount = amountList.get(selectedIndex = selectedIndex - 1);
                amountTxt.setText(selectedAmount.getTextAmount());
            }
        });


        chargeType.setOnClickListener(v -> {
            adapterChargeType = new AdapterChargeType(amountList);
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_charge, false).build();
            View view = dialog.getCustomView();
            rvAmount = view.findViewById(R.id.rv_payment);
            closeImage = view.findViewById(R.id.iv_close);

            closeImage.setOnClickListener(v12 -> dialog.dismiss());
            rvAmount.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvAmount.setAdapter(adapterChargeType);
            dialog.show();
        });
    }
}
