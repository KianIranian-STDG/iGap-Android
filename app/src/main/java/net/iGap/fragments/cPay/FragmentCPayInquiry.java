package net.iGap.fragments.cPay;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentCpayInquiryBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCPay;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentCPayInquiryViewModel;

public class FragmentCPayInquiry extends BaseFragment implements ToolbarListener {

    private FragmentCpayInquiryBinding binding;
    private FragmentCPayInquiryViewModel viewModel;
    private String plaqueText;

    public FragmentCPayInquiry() {
    }

    public static FragmentCPayInquiry getInstance(String plaque) {
        FragmentCPayInquiry fragmentCPayInquiry = new FragmentCPayInquiry();
        Bundle bundle = new Bundle();
        bundle.putString(HelperCPay.PLAQUE, plaque);
        fragmentCPayInquiry.setArguments(bundle);
        return fragmentCPayInquiry;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentCPayInquiryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cpay_inquiry, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null)
            plaqueText = getArguments().getString(HelperCPay.PLAQUE);

        initToolbar();
        initPlaque();
    }

    private void initPlaque() {

        if (plaqueText == null) return;

        String[] plaqueValue = HelperCPay.getPlaque(plaqueText);

        binding.fciPlaqueView.setPlaque1(plaqueValue[0]);
        binding.fciPlaqueView.setPlaqueAlphabet(HelperCPay.getPlaqueAlphabet(Integer.valueOf(plaqueValue[1])));
        binding.fciPlaqueView.setPlaque2(plaqueValue[2]);
        binding.fciPlaqueView.setPlaqueCity(plaqueValue[3]);


    }

    private void initToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.c_pay_title))
                .setLeftIcon(R.string.icon_back)
                .setListener(this);

        binding.fciToolbar.addView(toolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

}
