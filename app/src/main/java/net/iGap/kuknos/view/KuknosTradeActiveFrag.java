package net.iGap.kuknos.view;

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
import net.iGap.databinding.FragmentKuknosTraceHistoryBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.kuknos.view.adapter.WalletOpenOfferAdapter;
import net.iGap.kuknos.viewmodel.KuknosTradeActiveVM;

public class KuknosTradeActiveFrag extends BaseFragment {

    private FragmentKuknosTraceHistoryBinding binding;
    private KuknosTradeActiveVM kuknosTradeHistoryVM;

    public static KuknosTradeActiveFrag newInstance() {
        return new KuknosTradeActiveFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosTradeHistoryVM = ViewModelProviders.of(this).get(KuknosTradeActiveVM.class);
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

        kuknosTradeHistoryVM.getDataFromServer();

        onError();
        onProgressVisibility();
        onDataChanged();
    }

    private void onDataChanged() {
        kuknosTradeHistoryVM.getOfferList().observe(getViewLifecycleOwner(), offerResponsePage -> {
            if (offerResponsePage.getOffers().size() != 0) {
                WalletOpenOfferAdapter mAdapter = new WalletOpenOfferAdapter(offerResponsePage.getOffers());
                binding.kuknosTradeHistoryRecycler.setAdapter(mAdapter);
            } else {
                binding.kuknosTradeHistoryNOitem.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onError() {
        kuknosTradeHistoryVM.getErrorM().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
                defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_wHistory_dialogTitle))
                        .setMessage(getResources().getString(R.string.kuknos_wHistory_error));
                defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), (dialog, id) -> {
                    //close frag
                    popBackStackFragment();
                });
                defaultRoundDialog.show();
            }
        });
    }

    private void onProgressVisibility() {
        kuknosTradeHistoryVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.kuknosTradeHistoryProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.kuknosTradeHistoryProgressV.setVisibility(View.GONE);
            }
        });
    }

}
