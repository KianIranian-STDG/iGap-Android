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
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.databinding.MobileBankChequeListFragmentBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.model.mobileBank.BankChequeSingle;
import net.iGap.adapter.mobileBank.MobileBankChequeListAdapter;
import net.iGap.viewmodel.mobileBank.MobileBankChequesListViewModel;

import java.util.List;

public class MobileBankChequesListFragment extends BaseMobileBankFragment<MobileBankChequesListViewModel> {

    private MobileBankChequeListFragmentBinding binding;
    private MobileBankChequeListAdapter adapter;

    public static MobileBankChequesListFragment newInstance(String depositNumber, String bookNumber) {
        MobileBankChequesListFragment fragment = new MobileBankChequesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("depositNumber", depositNumber);
        bundle.putString("bookNumber", bookNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_cheque_list_fragment, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankChequesListViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        setupListeners();
        viewModel.setBookNumber(getArguments().getString("bookNumber"));
        viewModel.setDeposit(getArguments().getString("depositNumber"));
        viewModel.init();
    }

    private void setupView() {
        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(binding.Toolbar.getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setDefaultTitle(getString(R.string.cheque))
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });
        binding.Toolbar.addView(toolbar.getView());

        binding.pullToRefresh.setOnRefreshListener(() -> {
            adapter.removeAll();
            viewModel.init();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rcCheque.setLayoutManager(layoutManager);

        adapter = new MobileBankChequeListAdapter(null, position -> {
            viewModel.blockCheques(adapter.getItem(position).getNumber());
        });
        binding.rcCheque.setAdapter(adapter);
    }

    private void setupListeners() {

        viewModel.getResponseListener().observe(getViewLifecycleOwner(), this::setupRecyclerView);
    }

    private void setupRecyclerView(List<BankChequeSingle> chequeModels) {
        binding.pullToRefresh.setRefreshing(false);
        adapter.addItems(chequeModels);
    }
}
