package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.adapter.kuknos.WalletTradeHistoryAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosTraceHistoryBinding;
import net.iGap.kuknos.viewmodel.KuknosTradeHistoryVM;

public class KuknosTradeHistoryFrag extends BaseAPIViewFrag<KuknosTradeHistoryVM> {

    private FragmentKuknosTraceHistoryBinding binding;

    public static KuknosTradeHistoryFrag newInstance() {
        return new KuknosTradeHistoryFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosTradeHistoryVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_trace_history, container, false);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        binding.kuknosTradeHistoryRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.kuknosTradeHistoryRecycler.setLayoutManager(layoutManager);

        viewModel.getDataFromServer();
        binding.pullToRefresh.setRefreshing(true);
        binding.pullToRefresh.setOnRefreshListener(() -> viewModel.getDataFromServer());

        onError();
        onProgressVisibility();
        onDataChanged();
    }

    private void onDataChanged() {
        viewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), offerResponsePage -> {
            if (offerResponsePage.getTrades() != null && offerResponsePage.getTrades().size() != 0) {
                WalletTradeHistoryAdapter mAdapter = new WalletTradeHistoryAdapter(offerResponsePage.getTrades());
                binding.kuknosTradeHistoryRecycler.setAdapter(mAdapter);
            } else {
                binding.kuknosTradeHistoryNOitem.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onError() {
        viewModel.getErrorM().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                new MaterialDialog.Builder(getContext())
                        .title(getResources().getString(R.string.kuknos_wHistory_dialogTitle))
                        .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                        .content(getResources().getString(R.string.kuknos_wHistory_error))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //close frag
                                popBackStackFragment();
                            }
                        })
                        .show();
            }
        });
    }

    private void onProgressVisibility() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            binding.pullToRefresh.setRefreshing(aBoolean);
            /*if (aBoolean) {
                binding.kuknosTradeHistoryProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.kuknosTradeHistoryProgressV.setVisibility(View.GONE);
            }*/
        });
    }

}
