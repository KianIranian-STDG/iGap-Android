package net.iGap.fragments.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.PackagesFilterSpinnerAdapter;
import net.iGap.adapter.payment.internetpackage.AdapterProposalPackage;
import net.iGap.adapter.payment.internetpackage.AdapterRecentlyPackage;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentPaymentInternetPackagesBinding;
import net.iGap.viewmodel.BuyInternetPackageViewModel;

public class FragmentPaymentInternetPackage extends BaseAPIViewFrag<BuyInternetPackageViewModel> {

    private FragmentPaymentInternetPackagesBinding binding;
    private AdapterProposalPackage adapterProposal;
    private AdapterRecentlyPackage adapterRecently;


    public static FragmentPaymentInternetPackage newInstance() {

        Bundle args = new Bundle();

        FragmentPaymentInternetPackage fragment = new FragmentPaymentInternetPackage();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(BuyInternetPackageViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_internet_packages, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getData();

        adapterRecently = new AdapterRecentlyPackage();
        binding.rvRecentlyPackage.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvRecentlyPackage.setAdapter(adapterRecently);

        adapterProposal = new AdapterProposalPackage();
        binding.rvProposalPackage.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvProposalPackage.setAdapter(adapterProposal);


        viewModel.getPackageFiltersList().observe(getViewLifecycleOwner(), filters -> {
            if (filters != null) {
                binding.spinnerTime.setAdapter(new PackagesFilterSpinnerAdapter(filters));
            } else {
                binding.spinnerTime.setSelection(0);
            }
        });


        viewModel.getPackageFiltersList().observe(getViewLifecycleOwner(), filters -> {
            if (filters != null) {
                binding.spinnerSize.setAdapter(new PackagesFilterSpinnerAdapter(filters));
            } else {
                binding.spinnerSize.setSelection(0);
            }
        });

    }
}
