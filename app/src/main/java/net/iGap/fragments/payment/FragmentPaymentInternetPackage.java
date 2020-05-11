package net.iGap.fragments.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MySpinnerAdapter;
import net.iGap.adapter.payment.internetpackage.AdapterProposalPackage;
import net.iGap.adapter.payment.internetpackage.AdapterRecentlyPackage;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentPaymentInternetPackagesBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.model.MciPurchaseResponse;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.internetPackage.InternetPackage;
import net.iGap.model.internetPackage.MciInternetPackageFilter;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmRecentChargeNumber;
import net.iGap.repository.MciInternetPackageRepository;
import net.iGap.viewmodel.BuyInternetPackageViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

        adapterRecently = new AdapterRecentlyPackage();
        binding.rvRecentlyPackage.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvRecentlyPackage.setAdapter(adapterRecently);

        adapterProposal = new AdapterProposalPackage();
        binding.rvProposalPackage.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rvProposalPackage.setAdapter(adapterProposal);



        viewModel.getTypeList().observe(getViewLifecycleOwner(), typeList -> {
            hideKeyboard();
            if (typeList != null) {
                binding.spinnerSize.setAdapter(new MySpinnerAdapter(typeList));
                binding.spinnerTime.setAdapter(new MySpinnerAdapter(typeList));
            } else {
                binding.spinnerSize.setSelection(0);
                binding.spinnerTime.setSelection(0);
            }
        });

    }
}
