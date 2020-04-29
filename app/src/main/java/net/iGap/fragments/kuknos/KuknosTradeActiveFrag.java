package net.iGap.fragments.kuknos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.adapter.kuknos.WalletOpenOfferAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosTraceHistoryBinding;
import net.iGap.viewmodel.kuknos.KuknosTradeActiveVM;

public class KuknosTradeActiveFrag extends BaseAPIViewFrag<KuknosTradeActiveVM> {

    private FragmentKuknosTraceHistoryBinding binding;

    public static KuknosTradeActiveFrag newInstance() {
        return new KuknosTradeActiveFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosTradeActiveVM.class);
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
        binding.kuknosTradeHistoryDate.setText("");

        viewModel.getDataFromServer();
        binding.pullToRefresh.setRefreshing(true);
        binding.pullToRefresh.setOnRefreshListener(() -> viewModel.getDataFromServer());

        onError();
        onProgressVisibility();
        onDataChanged();
    }

    private void onDataChanged() {
        viewModel.getOfferList().observe(getViewLifecycleOwner(), offerResponsePage -> {
            if (offerResponsePage.getOffers().size() != 0) {
                WalletOpenOfferAdapter mAdapter = new WalletOpenOfferAdapter(offerResponsePage.getOffers(), new WalletOpenOfferAdapter.onClickListener() {
                    @Override
                    public void onDelete(int position) {
                        viewModel.deleteTrade(position);
                    }
                });
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
