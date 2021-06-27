package net.iGap.fragments.mobileBank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.iGap.R;
import net.iGap.databinding.MobileBankLoansFragmentBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.mobileBank.LoanListModel;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.adapter.mobileBank.BankLoanListAdapter;
import net.iGap.viewmodel.mobileBank.MobileBankLoansViewModel;

import java.util.List;

public class MobileBankLoansFragment extends BaseMobileBankFragment<MobileBankLoansViewModel> {

    private MobileBankLoansFragmentBinding binding;

    public static MobileBankLoansFragment newInstance() {
        return new MobileBankLoansFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_loans_fragment, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.progress.getIndeterminateDrawable().setColorFilter(0XFFB6774E, android.graphics.PorterDuff.Mode.MULTIPLY);
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
                .setRoundBackground(false)
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
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), MobileBankServiceLoanDetailFragment.newInstance(num))
                        .setReplace(false)
                        .load();
            }
        });
        binding.rvLoans.setAdapter(adapter);
        adapter.setItems(loanListModels);
        if (loanListModels.size() == 0) viewModel.getNoItemVisibility().set(View.VISIBLE);
    }
}
