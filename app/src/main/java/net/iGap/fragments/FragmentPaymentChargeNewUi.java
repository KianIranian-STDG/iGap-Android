package net.iGap.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.material.button.MaterialButton;

import net.iGap.R;
import net.iGap.adapter.payment.AdapterContactNumber;
import net.iGap.adapter.payment.AdapterHistoryNumber;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

public class FragmentPaymentChargeNewUi extends BaseFragment {
    private LinearLayout toolbar;
    private ConstraintLayout frameHamrah, frameIrancell, frameRightel, frameContact, frameHistory;
    private RadioButton radioButtonHamrah, radioButtonIrancell, radioButtonRightel;
    private AdapterHistoryNumber adapterHistory;
    private AdapterContactNumber adapterContact;

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
        frameHamrah = view.findViewById(R.id.view12);
        frameIrancell = view.findViewById(R.id.view13);
        frameRightel = view.findViewById(R.id.view14);
        radioButtonHamrah = view.findViewById(R.id.radio_hamrahAval);
        radioButtonIrancell = view.findViewById(R.id.radio_irancell);
        radioButtonRightel = view.findViewById(R.id.radio_rightel);
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

        radioButtonHamrah.setOnClickListener(v1 -> {
            if (radioButtonHamrah.isChecked() && radioButtonHamrah.isSelected()) {
                frameHamrah.setSelected(false);
                radioButtonHamrah.setChecked(false);
            } else {
                SetSelected(frameHamrah, radioButtonHamrah, frameIrancell, radioButtonIrancell, frameRightel, radioButtonRightel);
            }
        });

        radioButtonIrancell.setOnClickListener(v1 -> {
            if (radioButtonIrancell.isChecked() && radioButtonIrancell.isSelected()) {
                frameIrancell.setSelected(false);
                radioButtonIrancell.setChecked(false);
            } else {
                SetSelected(frameIrancell, radioButtonIrancell, frameHamrah, radioButtonHamrah, frameRightel, radioButtonRightel);
            }
        });

        radioButtonRightel.setOnClickListener(v1 -> {
            if (radioButtonRightel.isChecked() && radioButtonRightel.isSelected()) {
                frameRightel.setSelected(false);
                radioButtonRightel.setChecked(false);
            } else {
                SetSelected(frameRightel, radioButtonRightel, frameHamrah, radioButtonHamrah, frameIrancell, radioButtonIrancell);
            }
        });

        CreateCustomDialog();

    }

    public void SetSelected(View v0, RadioButton v1, View v2, RadioButton v3, View v4, RadioButton v5) {
        v0.setSelected(true);
        v1.setChecked(true);
        v2.setSelected(false);
        v3.setChecked(false);
        v4.setSelected(false);
        v5.setChecked(false);
    }

    public void CreateCustomDialog() {
        frameContact.setOnClickListener(v -> {
            adapterHistory = new AdapterHistoryNumber();
            MaterialDialog dialog = new MaterialDialog.Builder(v.getContext()).customView(R.layout.popup_paymet_charge, true).build();

            View view1 = dialog.getCustomView();
            RecyclerView rv = view1.findViewById(R.id.rv_payment);
            AppCompatTextView closeImage = view1.findViewById(R.id.iv_close);
            MaterialButton saveBtn = view1.findViewById(R.id.btn_save);

            rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rv.setAdapter(adapterHistory);
            dialog.show();

            closeImage.setOnClickListener(v12 -> dialog.dismiss());

            saveBtn.setOnClickListener(v13 -> {
                // TODO: 4/28/20 save number
            });
        });

        frameHistory.setOnClickListener(v -> {
            adapterContact = new AdapterContactNumber();
            MaterialDialog dialog = new MaterialDialog.Builder(v.getContext()).build();

            RecyclerView rv = new RecyclerView(getContext());

            rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            rv.setAdapter(adapterContact);
            dialog.show();

        });
    }
}
