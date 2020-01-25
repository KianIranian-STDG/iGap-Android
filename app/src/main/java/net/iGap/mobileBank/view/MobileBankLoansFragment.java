package net.iGap.mobileBank.view;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.MobileBankLoansFragmentBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.repository.model.LoanListModel;
import net.iGap.mobileBank.view.adapter.BankLoanListAdapter;
import net.iGap.mobileBank.viewmodel.MobileBankLoansViewModel;

import java.util.List;

public class MobileBankLoansFragment extends BaseMobileBankFragment<MobileBankLoansViewModel> {

    private MobileBankLoansFragmentBinding binding;

    public static MobileBankLoansFragment newInstance() {
        MobileBankLoansFragment fragment = new MobileBankLoansFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_loans_fragment, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankLoansViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupListeners();
        viewModel.getLoans();
    }


    private void setupToolbar() {
        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(binding.toolbar.getContext())
                .setLogoShown(true)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });
        binding.toolbar.addView(toolbar.getView());
    }

    private void setupListeners() {

        viewModel.getResponseListener().observe(getViewLifecycleOwner(), loanListModels -> {
            if (loanListModels != null) {
                setupLoanList(loanListModels);
            }
        });
    }

    private void setupLoanList(List<LoanListModel> loanListModels) {
        if (getActivity() == null) return;
        binding.rvLoans.setLayoutManager(new LinearLayoutManager(getActivity()));
        BankLoanListAdapter adapter = new BankLoanListAdapter();
        adapter.setListener(num -> {
        });
        binding.rvLoans.setAdapter(adapter);
        adapter.setItems(loanListModels);
        if (loanListModels.size() == 0) viewModel.getNoItemVisibility().set(View.VISIBLE);
    }
}
