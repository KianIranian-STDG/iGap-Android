package net.iGap.fragments.cPay;


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
import net.iGap.adapter.cPay.AdapterCPayHistory;
import net.iGap.databinding.FragmentCpayHistoryBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentCPayHistoryViewModel;

public class FragmentCPayHistory extends BaseFragment implements ToolbarListener {

    private FragmentCPayHistoryViewModel viewModel;
    private FragmentCpayHistoryBinding binding;
    private AdapterCPayHistory mAdapter;

    public FragmentCPayHistory() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(FragmentCPayHistoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cpay_history, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initRecyclerView();
        setupListeners();
    }

    private void setupListeners() {

        viewModel.getOnFiltersButtonStateChangeListener().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            switch (state) {
                case 1:

                    break;

                case 2:

                    break;

                case 3:

                    break;
            }
        });
    }

    private void initRecyclerView() {

        binding.fchRvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AdapterCPayHistory(getContext());
        binding.fchRvHistory.setAdapter(mAdapter);
        //mAdapter.setHistoryItems(null);

    }

    private void initToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.c_pay_title))
                .setLeftIcon(R.string.back_icon)
                .setListener(this);

        binding.fchToolbar.addView(toolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }
}
