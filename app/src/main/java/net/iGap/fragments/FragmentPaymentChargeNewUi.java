package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import net.iGap.R;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

public class FragmentPaymentChargeNewUi extends BaseFragment {
    private LinearLayout toolbar;
    private ConstraintLayout frameHamrah, frameIrancell, frameRightel;
    private RadioButton radioButtonHamrah, radioButtonIrancell, radioButtonRightel;

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
        radioButtonHamrah.setSelected(false);

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

        frameHamrah.setOnClickListener(v1 -> {
            if (frameHamrah.isSelected() && radioButtonHamrah.isChecked()) {
                frameHamrah.setSelected(false);
                radioButtonHamrah.setChecked(false);
            } else {
                frameHamrah.setSelected(true);
                radioButtonHamrah.setChecked(true);
            }
        });

        frameIrancell.setOnClickListener(v1 -> {
            if (frameIrancell.isSelected() && radioButtonIrancell.isChecked()) {
                frameIrancell.setSelected(false);
                radioButtonIrancell.setChecked(false);
            } else {
                frameIrancell.setSelected(true);
                radioButtonIrancell.setChecked(true);
            }
        });

        frameRightel.setOnClickListener(v1 -> {
            if (frameRightel.isSelected() && radioButtonRightel.isChecked()) {
                frameRightel.setSelected(false);
                radioButtonRightel.setChecked(false);
            } else {
                frameRightel.setSelected(true);
                radioButtonRightel.setChecked(true);
            }
        });

    }

    public void OnCLickFrames(View[] views) {
        for (int i = 0; i < views.length; i++) {
            switch (i){
                case 0:


            }
        }
    }
}
