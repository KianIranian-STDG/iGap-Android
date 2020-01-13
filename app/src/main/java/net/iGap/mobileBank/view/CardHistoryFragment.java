package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.MobileBankHistoryBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.view.adapter.MobileBankDateAdapter;
import net.iGap.mobileBank.view.adapter.MobileBankHistoryAdapter;
import net.iGap.mobileBank.viewmoedel.CardHistoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class CardHistoryFragment extends BaseAPIViewFrag<CardHistoryViewModel> {

    private MobileBankHistoryBinding binding;
    private LinearSnapHelper snapHelper;

    private int currentPage = 0;
    private boolean isLastPage = false;
    private int totalPage = 20;
    private boolean isLoading = false;
    private MobileBankHistoryAdapter adapter;

    private static final String TAG = "Amini";

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

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager2.setAutoMeasureEnabled(true);
        binding.billsRecycler.setLayoutManager(layoutManager2);
        binding.billsRecycler.setNestedScrollingEnabled(false);
        adapter = new MobileBankHistoryAdapter(new ArrayList<>(), position -> {
            // show detail in dialog
        });
        adapter.addLoading();
        binding.billsRecycler.setAdapter(adapter);

        binding.container.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = binding.container.getChildAt(binding.container.getChildCount() - 1);
                int diff = (view.getBottom() - (binding.container.getHeight() + binding.container.getScrollY()));
                if (diff == 0) {
                    isLoading = true;
                    currentPage++;
                    Log.d(TAG, "loadMoreItems: Load more Bitch!!!");
                    viewModel.getAccountDataForMonth(null);
                }
            }
        });

        onDateChangedListener();
    }

    private void initMainRecycler(List<BankHistoryModel> data) {

//        if (currentPage != 0)
        adapter.removeLoading();

        if (data == null || data.size() == 0)
            return;

        Log.d(TAG, "initMainRecycler: in here we are");
        adapter.addItems(data);

        // check weather is last page or not
        if (currentPage < totalPage) {
            adapter.addLoading();
        } else {
            isLastPage = true;
        }
        isLoading = false;
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

        viewModel.getBills().observe(getViewLifecycleOwner(), this::initMainRecycler);
    }

}
