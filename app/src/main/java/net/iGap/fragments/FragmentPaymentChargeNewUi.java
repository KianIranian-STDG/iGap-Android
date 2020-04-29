package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;

import net.iGap.R;
import net.iGap.adapter.payment.AdapterHistoryNumber;
import net.iGap.adapter.payment.AdapterContactNumber;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

public class FragmentPaymentChargeNewUi extends BaseFragment {
    private LinearLayout toolbar;
    private ConstraintLayout frameContact, frameHistory, contactFrame, historyFrame;
    private RadioButton radioButtonHamrah, radioButtonIrancell, radioButtonRightel;
    private RadioGroup radioGroup;
    private AdapterContactNumber adapterContact;
    private AdapterHistoryNumber adapterHistory;
    private RecyclerView rvContact, rvHistory;
    private AppCompatTextView chooseNumber, closeImage;

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
        CreateCustomDialog();

    }


    public void CreateCustomDialog() {
        frameContact.setOnClickListener(v -> {
            adapterHistory = new AdapterHistoryNumber();
            MaterialDialog dialog = new MaterialDialog.Builder(getContext()).customView(R.layout.popup_paymet_number, true).build();
            View view1 = dialog.getCustomView();
            closeImage = view1.findViewById(R.id.iv_close);
            chooseNumber = view1.findViewById(R.id.choose_number);
            contactFrame = view1.findViewById(R.id.contact_frame);
            historyFrame = view1.findViewById(R.id.history_frame);
            rvContact = view1.findViewById(R.id.rv_contact);
            MaterialButton saveBtn = view1.findViewById(R.id.btn_save);

            contactFrame.setVisibility(View.VISIBLE);
            historyFrame.setVisibility(View.GONE);
            chooseNumber.setText("انتخاب از شماره های مخاطبین");
            rvContact.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rvContact.setAdapter(adapterHistory);
            dialog.show();

            closeImage.setOnClickListener(v12 -> dialog.dismiss());

            saveBtn.setOnClickListener(v13 -> {
                // TODO: 4/28/20 save number
            });
        });

        frameHistory.setOnClickListener(v -> {
            adapterContact = new AdapterContactNumber();
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

    }
}
