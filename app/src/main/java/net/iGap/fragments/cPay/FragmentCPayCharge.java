package net.iGap.fragments.cPay;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.adapter.cPay.CPayChargeSpinnerAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentCpayChargeBinding;
import net.iGap.helper.HelperCPay;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentCPayChargeViewModel;


public class FragmentCPayCharge extends BaseAPIViewFrag<FragmentCPayChargeViewModel> implements ToolbarListener {

    private FragmentCpayChargeBinding binding;
    private String plaqueText;

    public FragmentCPayCharge() {
    }

    public static FragmentCPayCharge getInstance(String plaque) {
        FragmentCPayCharge fragmentCPayCharge = new FragmentCPayCharge();
        Bundle bundle = new Bundle();
        bundle.putString(HelperCPay.PLAQUE, plaque);
        fragmentCPayCharge.setArguments(bundle);
        return fragmentCPayCharge;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentCPayChargeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cpay_charge, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();

        if (getArguments() != null)
            plaqueText = getArguments().getString(HelperCPay.PLAQUE);

        if (plaqueText == null) {
            Toast.makeText(getContext(), getString(R.string.plaque_is_not_valid), Toast.LENGTH_LONG).show();
            popBackStackFragment();
        }

        viewModel.setPlaque(plaqueText);
        viewModel.getRequestAmountFromServer();

        binding.plaqueBg.setBackground(new Theme().tintDrawable(binding.plaqueBg.getBackground(), getContext(), R.attr.iGapCardViewColor));
        initPlaque();
        setupSpinner();
        initCallback();

    }

    private void initCallback() {

        viewModel.getEditTextVisibilityListener().observe(getViewLifecycleOwner(), isVisible -> {
            if (isVisible == null) return;
            binding.edtAmount.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        });

        viewModel.getLoaderListener().observe(getViewLifecycleOwner(), isVisible -> {
            if (isVisible == null) return;
            binding.loaderAmount.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            binding.txtCredit.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        });

        viewModel.getMessageToUser().observe(getViewLifecycleOwner(), resID -> {
            if (resID == null) return;
            Toast.makeText(getActivity(), getString(resID), Toast.LENGTH_LONG).show();
        });

        viewModel.getMessageToUserText().observe(getViewLifecycleOwner(), s -> {
            if (s == null) return;
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        });

        viewModel.getChargePaymentStateListener().observe(getViewLifecycleOwner(), token -> {
            if (getActivity() == null) return;
            if (token == null) {
                Toast.makeText(getContext(), getString(R.string.wallet_error_server), Toast.LENGTH_LONG).show();
                return;
            }
            new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.cpay_title), token, result -> {
                if (result.isSuccess()) {
                    Toast.makeText(getActivity(), getString(R.string.successful_payment), Toast.LENGTH_LONG).show();
                    popBackStackFragment();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.unsuccessful_payment), Toast.LENGTH_LONG).show();
                }
            });
        });

    }

    private void setupSpinner() {

        CPayChargeSpinnerAdapter adapter = new CPayChargeSpinnerAdapter(getResources().getStringArray(R.array.cpay_charge));
        binding.spCharge.setAdapter(adapter);

    }

    private void initPlaque() {

        String[] plaqueValue = HelperCPay.getPlaque(plaqueText);

        binding.plaqueView.setPlaque1(plaqueValue[0]);
        binding.plaqueView.setPlaqueAlphabet(HelperCPay.getPlaqueAlphabet(Integer.valueOf(plaqueValue[1])));
        binding.plaqueView.setPlaque2(plaqueValue[2]);
        binding.plaqueView.setPlaqueCity(plaqueValue[3]);

    }

    private void initToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.charge))
                .setLeftIcon(R.string.icon_back)
                .setListener(this);

        binding.toolbar.addView(toolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }
}
