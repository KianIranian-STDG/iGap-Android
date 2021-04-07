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
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.repository.CPayRepository;
import net.iGap.databinding.FragmentCpayEditBinding;
import net.iGap.helper.HelperCPay;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentCPayEditViewModel;

public class FragmentCPayEdit extends BaseAPIViewFrag<FragmentCPayEditViewModel> implements ToolbarListener {

    private FragmentCpayEditBinding binding;
    private String plaqueText;

    public FragmentCPayEdit() {

    }

    public static FragmentCPayEdit getInstance(String plaque) {
        FragmentCPayEdit fragmentCPay = new FragmentCPayEdit();
        Bundle bundle = new Bundle();
        bundle.putString(HelperCPay.PLAQUE, plaque);
        fragmentCPay.setArguments(bundle);
        return fragmentCPay;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentCPayEditViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cpay_edit, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) plaqueText = getArguments().getString(HelperCPay.PLAQUE, null);
        initToolbar();
        setupViewIfEditMode();
        setupCallBacks();

    }

    private void setupCallBacks() {

        viewModel.getMessageToUser().observe(getViewLifecycleOwner(), resID -> {
            if (resID == null) return;
            Toast.makeText(getActivity(), getString(resID), Toast.LENGTH_LONG).show();
        });

        viewModel.getMessageToUserText().observe(getViewLifecycleOwner(), s -> {
            if (s == null) return;
            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        });

        viewModel.getLoaderListener().observe(getViewLifecycleOwner(), isVisible -> {
            if (isVisible == null) return;

            closeKeyboard(binding.btnAddCar);

            if (isVisible) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnAddCar.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnAddCar.setEnabled(true);
            }
        });

        viewModel.getAddCarListener().observe(getViewLifecycleOwner(), isOk -> {
            if (isOk == null) return;
            //update list in main
            CPayRepository.getInstance().getPlaquesChangeListener().setValue(true);
            popBackStackFragment();
        });
    }

    private void setupViewIfEditMode() {
        if (plaqueText == null) return;
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
                .setDefaultTitle(getString(R.string.c_pay_title))
                .setLeftIcon(R.string.back_icon)
                .setListener(this);

        binding.fceToolbar.addView(toolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

}
