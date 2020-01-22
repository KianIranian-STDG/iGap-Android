package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.MobileBankChequesListFragmentBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.repository.model.ChequeModel;
import net.iGap.mobileBank.view.adapter.BankChequesListAdapter;
import net.iGap.mobileBank.viewmoedel.MobileBankChequesListViewModel;

import java.util.List;

public class MobileBankChequesListFragment extends BaseMobileBankFragment<MobileBankChequesListViewModel> {

    private MobileBankChequesListFragmentBinding binding;
    private String depositNumber;

    public static MobileBankChequesListFragment newInstance(String deposit) {
        MobileBankChequesListFragment fragment = new MobileBankChequesListFragment();
        fragment.depositNumber = deposit;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_cheques_list_fragment, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankChequesListViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        viewModel.getCheques(depositNumber);
        setupListeners();
    }

    private void setupToolbar() {
        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(binding.toolbar.getContext())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.cheque))
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

        viewModel.getResponseListener().observe(getViewLifecycleOwner(), chequeModels -> {
            if (chequeModels == null) return;
            if (chequeModels.size() == 0) viewModel.getNoItemVisibility().set(View.VISIBLE);
            setupRecyclerView(chequeModels);
        });
    }

    private void setupRecyclerView(List<ChequeModel> chequeModels) {
        BankChequesListAdapter adapter = new BankChequesListAdapter();
        adapter.setListener(chequeModel -> {
        });
        binding.rvCheque.setAdapter(adapter);
        adapter.setItems(chequeModels);
    }
}
