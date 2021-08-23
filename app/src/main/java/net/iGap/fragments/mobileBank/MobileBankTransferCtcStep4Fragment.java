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
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.MobileBankTransferCtcStep4FragmentBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.model.mobileBank.TransferMoneyCtcResultModel;
import net.iGap.adapter.mobileBank.TransferMoneyResultAdapter;
import net.iGap.viewmodel.mobileBank.MobileBankTransferCtcStep4ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MobileBankTransferCtcStep4Fragment extends BaseAPIViewFrag<MobileBankTransferCtcStep4ViewModel> {

    private MobileBankTransferCtcStep4FragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_transfer_ctc_step4_fragment, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankTransferCtcStep4ViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupRecycler();
        binding.btnShare.setOnClickListener(v -> {
            //new HelperFragment(getActivity().getSupportFragmentManager(), new MobileBankLoginFragment()).setReplace(false).load();

        });
    }

    private void setupRecycler() {
        if (getActivity() == null) return;
        TransferMoneyResultAdapter adapter = new TransferMoneyResultAdapter();
        binding.rvResult.setAdapter(adapter);
        binding.rvResult.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvResult.setNestedScrollingEnabled(false);
        addFakeDataToAdapter(adapter);
    }

    private void addFakeDataToAdapter(TransferMoneyResultAdapter adapter) {

        List<TransferMoneyCtcResultModel> list = new ArrayList<>();
        TransferMoneyCtcResultModel model;

        model = new TransferMoneyCtcResultModel();
        model.setKey("کد رهگیری");
        model.setValue("123456789");
        list.add(model);

        model = new TransferMoneyCtcResultModel();
        model.setKey("شماره مرجع");
        model.setValue("12345");
        list.add(model);

        model = new TransferMoneyCtcResultModel();
        model.setKey("شماره کارت مبدا");
        model.setValue("**** 1254");
        list.add(model);

        model = new TransferMoneyCtcResultModel();
        model.setKey("نام دارنده حساب");
        model.setValue("علیرضا نظری");
        list.add(model);

        model = new TransferMoneyCtcResultModel();
        model.setKey("بانک مقصد");
        model.setValue("پارسیان");
        list.add(model);

        model = new TransferMoneyCtcResultModel();
        model.setKey("تاریخ");
        model.setValue("1398/10/23");
        list.add(model);

        model = new TransferMoneyCtcResultModel();
        model.setKey("مبلغ");
        model.setValue("20,000,000 ریال");
        list.add(model);

        adapter.setItems(list);
    }

    private void setupToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setLeftIcon(R.string.icon_back)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        binding.toolbar.addView(toolbar.getView());
    }

}
