package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.MobileBankHistoryBinding;
import net.iGap.electricity_bill.view.ElectricityBillMainFrag;
import net.iGap.electricity_bill.view.ElectricityBillPayFrag;
import net.iGap.electricity_bill.viewmodel.ElectricityBillMainVM;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.view.adapter.AddAssetAdvAdapter;
import net.iGap.mobileBank.repository.model.BankDateModel;
import net.iGap.mobileBank.view.adapter.MobileBankDateAdapter;
import net.iGap.mobileBank.viewmoedel.CardHistoryViewModel;

import java.util.List;

public class CardHistoryFragment extends BaseAPIViewFrag<CardHistoryViewModel> {

    private MobileBankHistoryBinding binding;
    private LinearSnapHelper snapHelper;
    private static final String TAG = "CardHistoryFragment";

    public static CardHistoryFragment newInstance() {
        return new CardHistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CardHistoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_history, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        initial();
    }

    private void initial() {
        viewModel.init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.timeRecycler.setHasFixedSize(true);
        binding.timeRecycler.setLayoutManager(layoutManager);
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.timeRecycler);

        onDateChangedListener();
    }

    private void onDateChangedListener() {
        viewModel.getCalender().observe(getViewLifecycleOwner(), bankDateModels -> {
            MobileBankDateAdapter adapter = new MobileBankDateAdapter(viewModel.getCalender().getValue(), position -> {
                Toast.makeText(getContext(), "HI to ME.", Toast.LENGTH_SHORT).show();
                int centerOfScreen = binding.timeRecycler.getWidth() / 2 - snapHelper.findSnapView(binding.timeRecycler.getLayoutManager()).getWidth() / 2;
                ((LinearLayoutManager)binding.timeRecycler.getLayoutManager()).scrollToPositionWithOffset(position, centerOfScreen);
            });
            binding.timeRecycler.setAdapter(adapter);
        });
    }

}
